package com.cloudmall.common.advice;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cloudmall.common.exception.CmException;
import com.cloudmall.common.vo.ExceptionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice//默认自动拦截所有带有@controller的类 ControllerAdvice(annotations =  RestController.class)
public class CommonExceptionHandler {

    @ExceptionHandler(CmException.class)//拦截RuntimeException异常，在controller里面报错时抛的
    public ResponseEntity<ExceptionResult> handleExcetpion(CmException e){
        ExceptionEnum em=e.getExceptionEnum();
        return ResponseEntity.status(em.getCode()).body(new ExceptionResult(e.getExceptionEnum()));
    }
}
