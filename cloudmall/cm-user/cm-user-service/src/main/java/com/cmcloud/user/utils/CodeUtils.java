package com.cmcloud.user.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.util.UUID;


/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-13 20:35
 */
public class CodeUtils {
    public static String md5Hex(String data,String salt){
        if(StringUtils.isBlank(salt)){
                salt=salt.hashCode()+"";
        }
        return DigestUtils.md5Hex(salt+DigestUtils.md5Hex(data));
    }

    public static String shaHex(String data,String salt){
        if(StringUtils.isBlank(salt)){
            salt=salt.hashCode()+"";
        }
        return DigestUtils.sha512Hex(salt+DigestUtils.sha512Hex(data));
    }

    public static String generateSalt(){
        return StringUtils.replace(UUID.randomUUID().toString(),"-","");
    }
}
