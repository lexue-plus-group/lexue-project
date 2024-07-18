package com.leguan.content.service;

import com.leguan.content.model.po.CourseTeacher;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @description 课程教师信息管理接口
 */
public interface CourseTeacherService {

    public CourseTeacher getCourseTeacher(CourseTeacher courseTeacher);

    public List<CourseTeacher> getCourseTeacherList(Long courseId);

    public CourseTeacher saveTeacher(CourseTeacher courseTeacher);

    public void deleteTeacher(Long courseId, Long teacherId);
}
