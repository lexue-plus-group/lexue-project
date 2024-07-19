package com.leguan.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @description 测试minio的sdk
 */
public class MinioTest {

    MinioClient minioClient = MinioClient.builder().endpoint("http://192.168.101.65:9000").credentials("minioadmin", "minioadmin").build();

    @Test
    public void test_upload() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        //通过扩展名得到媒体资源类型  mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".txt");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }

        //上传文件的参数信息
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("testbucket") //确认桶
                .filename("D:\\gdpu\\mywork\\Java\\yhmx.txt") //指定本地文件路径
                //.object("1.txt") //上传到了主目录后的文件名
                .object("test/01/1.txt") //上传到了子目录下的文件名
                .contentType(mimeType) //设置媒体文件类型
                .build();

        //上传文件
        minioClient.uploadObject(uploadObjectArgs);

    }
    @Test
    public void test_delete() throws Exception {

        //删除文件的参数信息
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket("testbucket").object("1.txt").build();

        //删除文件
        minioClient.removeObject(removeObjectArgs);

    }

    //查询文件 从minio中下载
    @Test
    public void test_getFile() throws Exception {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("test/01/1.txt").build();
        GetObjectResponse getObjectResponse = minioClient.getObject(getObjectArgs);
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\gdpu\\mywork\\Java\\1.txt"));
        IOUtils.copy(getObjectResponse, outputStream);

        //校验文件的完整性对文件的内容进行md5
        String source_md5 = DigestUtils.md5Hex(getObjectResponse);
        FileInputStream fileInputStream = new FileInputStream(new File("D:\\gdpu\\mywork\\Java\\1.txt"));
        String local_md5 = DigestUtils.md5Hex(fileInputStream);
        if (source_md5.equals(local_md5)) {
            System.out.println("下载成功");
        }
    }
}
