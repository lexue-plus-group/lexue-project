package com.leguan.learning.service;

import com.leguan.base.model.RestResponse;

public interface LearningService {

    /**
     * @description 获取教学视频
     * @param courseId 课程id
     * @param teachplanId 课程计划id
     * @param mediaId 视频文件id
     * @return com.leguan.base.model.RestResponse<java.lang.String>
     */
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);
}
