package com.cloudmall.upload.service;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cloudmall.common.exception.CmException;
import com.cloudmall.upload.config.UploadProperties;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@EnableConfigurationProperties(UploadProperties.class) //获取自己定义的yml配置文件
public class UploadService {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private UploadProperties prop;

    Logger logger= LoggerFactory.getLogger(getClass());
    private static final List<String> ALLOW_TYPES= Arrays.asList("image/jpeg","image/jpg","image/png","image/bmp");
    public String uploadImage(MultipartFile file) {
        try {
            //校验文件类型,请求头里的属性Content-Type:image/jpg
            String contentType = file.getContentType();
            if(!prop.getAlloTypes().contains(contentType)){
                throw  new CmException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //校验文件内容，这个流可以读取一个图片,还可以读取图片宽高等信息校验
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage==null){
                throw  new CmException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            //这样截取效率很差String extension=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            //改为上传到FastDFS
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);

            //返回路径
            return prop.getBaseUrl()+storePath.getFullPath();
        } catch (IOException e) {
            //上传失败，
            logger.error("[文件上传]上传文件失败!",e);
            throw new CmException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }
}
