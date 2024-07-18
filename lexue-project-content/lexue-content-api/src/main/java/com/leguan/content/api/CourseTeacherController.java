package com.leguan.content.api;
import com.leguan.content.model.po.CourseTeacher;
import com.leguan.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description 课程教师信息管理相关的接口
 */
@Api(value = "教师信息管理接口", tags = "教师信息管理接口")
@RestController
public class CourseTeacherController {

    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation("根据课程id查询教师信息")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getCourseTeacherList(@PathVariable Long courseId) {
        List<CourseTeacher> courseTeachers = courseTeacherService.getCourseTeacherList(courseId);
        return courseTeachers;
    }

    @ApiOperation("添加教师")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveTeacher(@RequestBody CourseTeacher courseTeacher) {
        CourseTeacher newCourseTeacher = courseTeacherService.saveTeacher(courseTeacher);
        return newCourseTeacher;
    }

    @ApiOperation("删除教师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        courseTeacherService.deleteTeacher(courseId, teacherId);
    }
}
