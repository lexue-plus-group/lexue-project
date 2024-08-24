package com.leguan.media.api;

import com.leguan.base.model.PageParams;
import com.leguan.base.model.PageResult;
import com.leguan.media.model.dto.QueryMediaParamsDto;
import com.leguan.media.model.dto.UploadFileParamsDto;
import com.leguan.media.model.dto.UploadFileResultDto;
import com.leguan.media.model.po.MediaFiles;
import com.leguan.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author leguan
 * @version 1.0
 * @description 媒资文件管理接口
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {


    @Autowired
    MediaFileService mediaFileService;


    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);

    }

    @ApiOperation("上传图片")
    @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto upload(@RequestPart("filedata")MultipartFile filedata,
                                      @RequestParam(value = "objectName", required = false) String objectName) throws IOException {

        Long companyId = 1232141425L;
        File tempFile = File.createTempFile("minio", ".temp");
        filedata.transferTo(tempFile);
        String localFilePath = tempFile.getAbsolutePath();
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());
        uploadFileParamsDto.setFileSize(filedata.getSize());
        uploadFileParamsDto.setFileType("001001");

        //调用service上传图片
        UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, localFilePath, objectName);

        return uploadFileResultDto;

    }


}
