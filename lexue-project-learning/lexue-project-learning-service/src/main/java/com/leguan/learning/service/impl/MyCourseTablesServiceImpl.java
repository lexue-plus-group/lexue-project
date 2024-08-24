package com.leguan.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leguan.base.exception.LexueException;
import com.leguan.base.model.PageResult;
import com.leguan.content.model.po.CoursePublish;
import com.leguan.learning.feignclient.ContentServiceClient;
import com.leguan.learning.mapper.XcChooseCourseMapper;
import com.leguan.learning.mapper.XcCourseTablesMapper;
import com.leguan.learning.model.dto.MyCourseTableParams;
import com.leguan.learning.model.dto.XcChooseCourseDto;
import com.leguan.learning.model.dto.XcCourseTablesDto;
import com.leguan.learning.model.po.XcChooseCourse;
import com.leguan.learning.model.po.XcCourseTables;
import com.leguan.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class MyCourseTablesServiceImpl implements MyCourseTablesService {

    @Autowired
    XcChooseCourseMapper chooseCourseMapper;

    @Autowired
    XcCourseTablesMapper courseTablesMapper;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Transactional
    @Override
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {

        //选课调用内容管理查询课程的收费规则
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish == null) {
            LexueException.cast("课程不存在");
        }
        //收费规则
        String charge = coursepublish.getCharge();

        XcChooseCourse chooseCourse = null;

        if ("201000".equals(charge)) { //免费课程
            //如果是免费课程，会向选课记录表、我的课程表写数据
            //向选课记录表写
            chooseCourse = addFreeCourse(userId, coursepublish);
            //向我的课程表写
            XcCourseTables xcCourseTables = addCourseTables(chooseCourse);

        } else {
            //如果是收费课程，会向选课记录表写数据
            chooseCourse = addChargeCourse(userId, coursepublish);

        }

        //判断学生的学习资格
        XcCourseTablesDto xcCourseTablesDto = getLearningStatus(userId, courseId);

        XcChooseCourseDto xcChooseCourseDto = new XcChooseCourseDto();
        BeanUtils.copyProperties(chooseCourse, xcChooseCourseDto);
        //设置学习资格状态
        xcChooseCourseDto.setLearnStatus(xcCourseTablesDto.getLearnStatus());

        return xcChooseCourseDto;
    }

    @Override
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId) {

        XcCourseTablesDto xcCourseTablesDto = new XcCourseTablesDto();

        //查询我的课程表，如果查不到说明没选课
        XcCourseTables xcCourseTables = getXcCourseTables(userId, courseId);
        if (xcCourseTables == null) {
            xcCourseTablesDto.setLearnStatus("702002"); //未支付或者没选课
            return xcCourseTablesDto;
        }

        //如果查到了，判断是否过期
        boolean before = xcCourseTables.getValidtimeEnd().isBefore(LocalDateTime.now());
        if (before) {
            BeanUtils.copyProperties(xcCourseTables, xcCourseTablesDto);
            xcCourseTablesDto.setLearnStatus("702003"); //过期
            return xcCourseTablesDto;
        } else {
            BeanUtils.copyProperties(xcCourseTables, xcCourseTablesDto);
            xcCourseTablesDto.setLearnStatus("702001"); //正常学习
            return xcCourseTablesDto;
        }
    }

    //添加免费课程,免费课程加入选课记录表、我的课程表
    public XcChooseCourse addFreeCourse(String userId, CoursePublish coursepublish) {

        Long courseId = coursepublish.getId();

        //判断，如果存在免费的选课记录且选课状态为成功， 直接返回了
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>().eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, courseId)
                .eq(XcChooseCourse::getOrderType, "700001")//免费课程
                .eq(XcChooseCourse::getStatus, "701001");//选课成功
        List<XcChooseCourse> xcChooseCourses = chooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses.size() > 0) {
            return xcChooseCourses.get(0);
        }

        //向选课记录表写数据
        XcChooseCourse chooseCourse = new XcChooseCourse();

        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursepublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        chooseCourse.setOrderType("700001");
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursepublish.getPrice());
        chooseCourse.setValidDays(365);
        chooseCourse.setStatus("701001");
        chooseCourse.setValidtimeStart(LocalDateTime.now());//有效期开始时间
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));//有效期结束时间

        int insert = chooseCourseMapper.insert(chooseCourse);
        if (insert <= 0) {
            LexueException.cast("添加选课记录失败");
        }

        return chooseCourse;
    }


    //添加收费课程
    public XcChooseCourse addChargeCourse(String userId,CoursePublish coursepublish){

        Long courseId = coursepublish.getId();

        //判断，如果存在收费的选课记录且选课状态为待支付， 直接返回了
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>().eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, courseId)
                .eq(XcChooseCourse::getOrderType, "700002")//免费课程
                .eq(XcChooseCourse::getStatus, "701002");//待支付
        List<XcChooseCourse> xcChooseCourses = chooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses.size() > 0) {
            return xcChooseCourses.get(0);
        }

        //向选课记录表写数据
        XcChooseCourse chooseCourse = new XcChooseCourse();

        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursepublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        chooseCourse.setOrderType("700002");
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursepublish.getPrice());
        chooseCourse.setValidDays(365);
        chooseCourse.setStatus("701002");
        chooseCourse.setValidtimeStart(LocalDateTime.now());//有效期开始时间
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));//有效期结束时间

        int insert = chooseCourseMapper.insert(chooseCourse);
        if (insert <= 0) {
            LexueException.cast("添加选课记录失败");
        }

        return chooseCourse;
    }

    @Transactional
    @Override
    public boolean saveChooseCourseSuccess(String chooseCourseId) {

        //根据选课id查询课程表
        XcChooseCourse chooseCourse = chooseCourseMapper.selectById(chooseCourseId);
        if (chooseCourse == null) {
            log.debug("接收购买课程的消息，根据选课id从数据库找不到选课记录，选课id:{}", chooseCourseId);
            return false;
        }
        //选课状态
        String status = chooseCourse.getStatus();
        //只有当待支付时才更新为已支付
        if ("701002".equals(status)) {
            //更新选课记录为支付成功
            chooseCourse.setStatus("701001");
            int i = chooseCourseMapper.updateById(chooseCourse);
            if (i <= 0) {
                log.debug("添加选课记录失败:{}", chooseCourse);
                LexueException.cast("添加选课记录失败");
            }

            //向我的课程表插入记录
            XcCourseTables xcCourseTables = addCourseTables(chooseCourse);
        }
        return true;
    }

    @Override
    public PageResult<XcCourseTables> mycoursetables(MyCourseTableParams params) {
        
        //当前用户
        String userId = params.getUserId();
        //当前页码
        int pageNo = params.getPage();
        //每页记录数
        int size = params.getSize();

        Page<XcCourseTables> courseTablesPage = new Page<>(pageNo, size);
        LambdaQueryWrapper<XcCourseTables> lambdaQueryWrapper = new LambdaQueryWrapper<XcCourseTables>().eq(XcCourseTables::getUserId, userId);

        //查询数据
        Page<XcCourseTables> result = courseTablesMapper.selectPage(courseTablesPage, lambdaQueryWrapper);

        //数据列表
        List<XcCourseTables> records = result.getRecords();
        //总记录数
        long total = result.getTotal();

        PageResult pageResult = new PageResult(records, total, pageNo, size);

        return pageResult;
    }

    //添加到我的课程表
    public XcCourseTables addCourseTables(XcChooseCourse xcChooseCourse){

        //选课成功了才可以向我的课程表添加
        String status = xcChooseCourse.getStatus();
        if (!"701001".equals(status)) {
            LexueException.cast("选课没有成功无法添加到课程表");
        }
        XcCourseTables xcCourseTables = getXcCourseTables(xcChooseCourse.getUserId(), xcChooseCourse.getCourseId());
        if (xcCourseTables != null) {
            return xcCourseTables;
        }

        xcCourseTables = new XcCourseTables();
        BeanUtils.copyProperties(xcChooseCourse, xcCourseTables);
        xcCourseTables.setChooseCourseId(xcChooseCourse.getId()); //记录选课表的主键
        xcCourseTables.setCourseType(xcChooseCourse.getOrderType()); //选课类型
        xcCourseTables.setUpdateDate(LocalDateTime.now());

        int insert = courseTablesMapper.insert(xcCourseTables);
        if (insert <= 0) {
            LexueException.cast("添加我的课程表失败");
        }

        return xcCourseTables;
    }

    /**
     * @description 根据课程和用户查询我的课程表中某一门课程
     * @param userId
     * @param courseId
     * @return com.leguan.learning.model.po.XcCourseTables
     */
    public XcCourseTables getXcCourseTables(String userId,Long courseId){
        XcCourseTables xcCourseTables = courseTablesMapper.selectOne(new LambdaQueryWrapper<XcCourseTables>().eq(XcCourseTables::getUserId, userId).eq(XcCourseTables::getCourseId, courseId));
        return xcCourseTables;

    }
}
