package com.leguan.media.service;

import com.leguan.base.model.PageParams;
import com.leguan.base.model.PageResult;
import com.leguan.media.model.dto.QueryMediaParamsDto;
import com.leguan.media.model.dto.UploadFileParamsDto;
import com.leguan.media.model.dto.UploadFileResultDto;
import com.leguan.media.model.po.MediaFiles;

/**
 * @author leguan
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.leguan.base.model.PageResult<com.leguan.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author leguan
     * @date 2022/9/10 8:57
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     *
     * @param companyId           机构id
     * @param uploadFileParamsDto 文件信息
     * @param localFilePath       本地文件路径
     * @return
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);

    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);

}
