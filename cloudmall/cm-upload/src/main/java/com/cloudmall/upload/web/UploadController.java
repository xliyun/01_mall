package com.cloudmall.upload.web;

import com.cloudmall.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;
    /**
     * 上传图片
     * @param file
     * @return
     */
    @PostMapping("image")
    //springMVC 会自动把上传的文件封装到MultipartFile对象里面去利用对象接收上传的对象
    public ResponseEntity<String> uploadImage(@RequestParam("file")MultipartFile file){
/*        String url=uploadService.uploadImage(file);
        return ResponseEntity.ok(url);*/
        return ResponseEntity.ok(uploadService.uploadImage(file));
    }
}
