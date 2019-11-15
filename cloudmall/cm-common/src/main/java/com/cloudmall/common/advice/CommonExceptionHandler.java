package com.cloudmall.common.advice;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cloudmall.common.exception.CmException;
import com.cloudmall.common.vo.ExceptionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
//我们的启动类是放在com.cmcloud包 里面，我们的通用异常处理类也在com.cmcloud包下，所以能够扫描到，如果启动类放到com.cmcloud.item里面的时候，就扫描不到了
@ControllerAdvice//默认自动拦截所有带有@controller注解的类 ControllerAdvice(annotations =  RestController.class)
public class CommonExceptionHandler {

    @ExceptionHandler(CmException.class)//拦截RuntimeException异常，在controller里面报错时抛的
    public ResponseEntity<ExceptionResult> handleExcetpion(CmException e){
        ExceptionEnum em=e.getExceptionEnum();
        return ResponseEntity.status(em.getCode()).body(new ExceptionResult(e.getExceptionEnum()));
    }
    //可以根据异常类别的不同，写多个函数拦截
}
