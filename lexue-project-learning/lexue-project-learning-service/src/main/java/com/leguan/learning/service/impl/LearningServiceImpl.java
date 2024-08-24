package com.leguan.learning.service.impl;

import com.leguan.base.model.RestResponse;
import com.leguan.content.model.po.CoursePublish;
import com.leguan.content.model.po.Teachplan;
import com.leguan.learning.feignclient.ContentServiceClient;
import com.leguan.learning.feignclient.MediaServiceClient;
import com.leguan.learning.model.dto.XcCourseTablesDto;
import com.leguan.learning.service.LearningService;
import com.leguan.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LearningServiceImpl implements LearningService {

    @Autowired
    MyCourseTablesService myCourseTablesService;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {

        //查询课程信息
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish == null) {
            return RestResponse.validfail("课程不存在");
        }

        //根据课程计划id（teachplanId）去查询课程计划信息，如果is_preview的值为1表示支持试学
        //也可以从coursepublish对象中解析出课程计划信息去判断是否支持试学
        Teachplan teachplan = contentServiceClient.getTeachPlan(teachplanId);
        // isPreview字段为1表示支持试学，返回课程url
        if ("1".equals(teachplan.getIsPreview())) {
            return mediaServiceClient.getPlayUrlByMediaId(mediaId);
        }

        //用户已登录
        if (StringUtils.isNotEmpty(userId)) {

            //通过我的课程表,获取学习资格
            XcCourseTablesDto learningStatus = myCourseTablesService.getLearningStatus(userId, courseId);
            //获取学习资格
            String learnStatus = learningStatus.getLearnStatus();
            if ("702002".equals(learnStatus)) {
                return RestResponse.validfail("无法学习，因为没有选课或选课后没有支付");
            } else if ("702003".equals(learnStatus)) {
                return RestResponse.validfail("已过期需要申请续期或重新支付");
            } else {
                //有资格学习，要返回视频的播放地址
                //远程调用媒资获取视频播放地址
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;
            }

        }
        //如果用户没有登录
        //取出课程的收费规则
        String charge = coursepublish.getCharge();
        if ("201000".equals(charge)) {
            //有资格学习，要返回视频的播放地址
            //远程调用媒资获取视频播放地址
            RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
            return playUrlByMediaId;
        }

        return RestResponse.validfail("该课程没有选课，需要购买");
    }
}
