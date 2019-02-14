package com.cloudmall.common.exception;

import com.cloudmall.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
public class CmException extends RuntimeException {
    private ExceptionEnum exceptionEnum;

    //有参无参构造函数


    public CmException() {
    }

    public CmException(ExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }

    //Getter方法
    public ExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }
}
