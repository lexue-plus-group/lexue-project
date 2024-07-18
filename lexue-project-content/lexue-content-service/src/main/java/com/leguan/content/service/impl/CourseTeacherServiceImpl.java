package com.leguan.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leguan.base.exception.LexueException;
import com.leguan.content.mapper.CourseTeacherMapper;
import com.leguan.content.model.po.CourseTeacher;
import com.leguan.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public CourseTeacher getCourseTeacher(CourseTeacher courseTeacher) {
        return courseTeacherMapper.selectById(courseTeacher.getId());
    }

    @Override
    public List<CourseTeacher> getCourseTeacherList(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(wrapper);
        return courseTeachers;
    }

    @Override
    public CourseTeacher saveTeacher(CourseTeacher courseTeacher) {
        Long id = courseTeacher.getId();
        if (id == null) {
            courseTeacher.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(courseTeacher);
            if (insert <= 0) {
                LexueException.cast("添加教师失败");
            }
        } else {
            CourseTeacher updateCourseTeacher = courseTeacherMapper.selectById(id);
            BeanUtils.copyProperties(courseTeacher, updateCourseTeacher);
            int i = courseTeacherMapper.updateById(updateCourseTeacher);
            if (i <= 0) {
                LexueException.cast("更新教师失败");
            }
        }
        return getCourseTeacher(courseTeacher);
    }

    @Override
    public void deleteTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getId, teacherId).eq(CourseTeacher::getCourseId, courseId);
        int flag = courseTeacherMapper.delete(wrapper);
        if (flag < 0) {
            LexueException.cast("删除失败");
        }
    }
}
