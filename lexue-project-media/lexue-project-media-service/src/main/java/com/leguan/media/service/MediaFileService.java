package com.leguan.media.service;

import com.leguan.base.model.PageParams;
import com.leguan.base.model.PageResult;
import com.leguan.base.model.RestResponse;
import com.leguan.media.model.dto.QueryMediaParamsDto;
import com.leguan.media.model.dto.UploadFileParamsDto;
import com.leguan.media.model.dto.UploadFileResultDto;
import com.leguan.media.model.po.MediaFiles;

import java.io.File;

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
     * @param objectName          如果传入objectName，要按objectName的目录去存储，如果不传入，则按年月日存储
     * @return
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath, String objectName);

    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);

    public RestResponse<Boolean> checkFile(String fileMd5);

    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * @param fileMd5            文件md5
     * @param chunk              分块序号
     * @param localChunkFilePath 分块文件本地路径
     * @return com.leguan.base.model.RestResponse
     * @description 上传分块
     * @author leguan
     */
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath);

    /**
     * @param companyId           机构id
     * @param fileMd5             文件md5
     * @param chunkTotal          分块总和
     * @param uploadFileParamsDto 文件信息
     * @return com.leguan.base.model.RestResponse
     * @description 合并分块
     * @author leguan
     */
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    /**
     * 从minio下载文件
     *
     * @param bucket     桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    public File downloadFileFromMinIO(String bucket, String objectName);

    //将文件上传到minio
    public boolean addMediaFilesToMinio(String localFilePath, String mimeType, String bucket, String objectName);

    public MediaFiles getFileById(String mediaId);
}
