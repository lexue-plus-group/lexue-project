package com.leguan.content.service;

import com.leguan.content.model.dto.CoursePreviewDto;
import com.leguan.content.model.po.CoursePublish;

import java.io.File;

/**
 * @description 课程预览、发布接口
 * @author leguan
 */
public interface CoursePublishService {

    /**
     * @description 获取课程预览信息
     * @param courseId 课程id
     * @return com.leguan.content.model.dto.CoursePreviewDto
     * @author leguan
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * @description 提交审核
     * @param courseId  课程id
     * @return void
     * @author leguan
     */
    public void commitAudit(Long companyId,Long courseId);

    public void publish(Long companyId, Long courseId);

    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     */
    public File generateCourseHtml(Long courseId);
    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     */
    public void  uploadCourseHtml(Long courseId,File file);

    public CoursePublish getCoursePublish(Long courseId);

    public CoursePublish getCoursePublishCache(Long courseId);
}
