package com.leguan.learning.feignclient;

import com.leguan.content.model.po.CoursePublish;
import com.leguan.content.model.po.Teachplan;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Mr.M
 * @version 1.0
 * @description 内容管理远程接口
 * @date 2022/10/25 9:13
 */
@FeignClient(value = "content-api",fallbackFactory = ContentServiceClientFallbackFactory.class)
public interface ContentServiceClient {

    @ResponseBody
    @GetMapping("/content/r/coursepublish/{courseId}")
    public CoursePublish getCoursepublish(@PathVariable("courseId") Long courseId);

    @PostMapping("/content/teachplan/{teachplanId}")
    Teachplan getTeachPlan(@PathVariable("teachplanId") Long teachplanId);
}
