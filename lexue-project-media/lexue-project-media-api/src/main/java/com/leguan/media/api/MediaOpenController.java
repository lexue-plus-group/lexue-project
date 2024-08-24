package com.leguan.media.api;

import com.leguan.base.exception.LexueException;
import com.leguan.base.model.RestResponse;
import com.leguan.media.model.po.MediaFiles;
import com.leguan.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
@RestController
@RequestMapping("/open")
public class MediaOpenController {
    @Autowired
    MediaFileService mediaFileService;

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){
        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        if (mediaFiles == null) {
            return RestResponse.validfail("找不到视频");
        }
        if(StringUtils.isEmpty(mediaFiles.getUrl())){
            return RestResponse.validfail("视频还没有转码处理");
        }
        return RestResponse.success(mediaFiles.getUrl());
    }
}
