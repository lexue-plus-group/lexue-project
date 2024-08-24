package com.leguan.content.service.jobhandler;

import com.leguan.base.exception.LexueException;
import com.leguan.content.feignclient.CourseIndex;
import com.leguan.content.feignclient.SearchServiceClient;
import com.leguan.content.mapper.CoursePublishMapper;
import com.leguan.content.model.dto.CoursePreviewDto;
import com.leguan.content.model.po.CoursePublish;
import com.leguan.content.service.CoursePublishService;
import com.leguan.messagesdk.model.po.MqMessage;
import com.leguan.messagesdk.service.MessageProcessAbstract;
import com.leguan.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @description 课程发布的任务类
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    SearchServiceClient searchServiceClient;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        //分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();//执行器的总数

        //调用抽象类的方法执行任务
        process(shardIndex, shardTotal, "course_publish", 30, 60);

    }

    //该方法抛出异常说明任务执行失败
    @Override
    public boolean execute(MqMessage mqMessage) {
        
        //从mqMessage拿到课程id
        Long courseId = Long.parseLong(mqMessage.getBusinessKey1());

        //课程静态化上传到minio
        generateCourseHtml(mqMessage, courseId);

        //向elasticsearch写索引数据
        saveCourseIndex(mqMessage, courseId);

        //向redis写缓存


        return true;
    }

    //课程静态化任务
    private void generateCourseHtml(MqMessage mqMessage, Long courseId) {
        //消息id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();

        //做任务幂等性处理
        //查询数据库取出该阶段执行状态
        int stageOne = mqMessageService.getStageOne(taskId);
        if (stageOne > 0) {
            log.debug("课程静态化任务完成，无需处理...");
            return ;
        }
        //开始进行课程静态化 生成html
         File file = coursePublishService.generateCourseHtml(courseId);
        if (file == null) {
            LexueException.cast("生成的静态文件为空");
        }

        //上传到minio
        coursePublishService.uploadCourseHtml(courseId, file);

        //任务处理完成，任务状态改为1
        mqMessageService.completedStageOne(taskId);

    }

    //保存课程索引信息
    private void saveCourseIndex(MqMessage mqMessage, Long courseId) {
        //消息id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();

        //做任务幂等性处理
        //查询数据库取出该阶段执行状态
        int stageTwo = mqMessageService.getStageTwo(taskId);
        if (stageTwo > 0) {
            log.debug("课程索引信息已写入，无需执行...");
            return ;
        }
        //查询课程信息，调用搜索服务添加索引
        //从课程发布表查询课程信息
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);
        //远程调用
        Boolean add = searchServiceClient.add(courseIndex);
        if (!add) {
            LexueException.cast("远程调用搜索服务添加课程索引失败");
        }

        //任务处理完成，任务状态改为1
        mqMessageService.completedStageTwo(taskId);
    }
}
