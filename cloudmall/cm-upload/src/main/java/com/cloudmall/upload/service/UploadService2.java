package com.cloudmall.upload.service;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cloudmall.common.exception.CmException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
/*
上传至本地的版本
 */
@Service
public class UploadService2 {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    Logger logger= LoggerFactory.getLogger(getClass());
    private static final List<String> ALLOW_TYPES= Arrays.asList("image/jpeg","image/jpg","image/png","image/bmp");
    public String uploadImage(MultipartFile file) {
        try {
            //校验文件类型,请求头里的属性Content-Type:image/jpg
            String contentType = file.getContentType();
            if(!ALLOW_TYPES.contains(contentType)){
                throw  new CmException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //校验文件内容，这个流可以读取一个图片,还可以读取图片宽高等信息校验
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage==null){
                throw  new CmException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //目标路径
            File dest=new File("D:\\images",file.getOriginalFilename());
            //保存文件到本地
            file.transferTo(dest);
            //返回路径
            return "D:\\images\\"+file.getOriginalFilename();

        } catch (IOException e) {
            //上传失败，
            logger.error("上传文件失败!",e);
            throw new CmException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }
}
