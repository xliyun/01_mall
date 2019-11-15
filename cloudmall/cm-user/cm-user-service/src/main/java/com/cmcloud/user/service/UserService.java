package com.cmcloud.user.service;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cloudmall.common.exception.CmException;
import com.cmcloud.user.mapper.UserMapper;
import com.cmcloud.user.pojo.User;
import com.cmcloud.user.utils.CodeUtils;
import main.java.com.cloudmall.common.utils.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-12 10:20
 */
@Service
public class UserService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX="user:vertify:phone:";


    /*
     * @Author xiaoliyu
     * @Description //校验数据
     * @Date 14:58 2019/4/12
     **/
    public Boolean checkData(String data, Integer type) {
        User record=new User();

        switch (type){
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                 break;
             default:
                 throw new CmException(ExceptionEnum.USER_DATA_TYPE_EXISTS);

        }
        return userMapper.selectCount(record)==0;
    }


    public void sendCode(String phone) {
        //生成redis需要的key
        String key=KEY_PREFIX+phone;

        //生成验证码
        String code = NumberUtils.generateCode(6);

        Map<String,String> msg=new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);

                                    //交换机 routingkey
        amqpTemplate.convertAndSend("cm.sms.exchange","sms.vertify.code",msg);

        //
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);//redis中存入key-code 设置5分钟失效
    }

    public void register(User user, String code) {
        //从redis中取出校验码
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        //校验验证码
/*        if (!StringUtils.equals(code,cacheCode)) {
            throw new CmException(ExceptionEnum.INVALID_VERIFY_CODE);
        }*/
        //生成盐,随机生成
        String slat=CodeUtils.generateSalt();
        user.setSalt(slat);
        //对密码加密
        user.setPassword(CodeUtils.md5Hex(user.getPassword(),slat));
        user.setCreated(new Date());
        //写入数据库
        userMapper.insert(user);
    }

    public User queryUserByUserNameAndPasswd(String username, String password) {
        User record=new User();
        record.setUsername(username);//数据库里给用户名加了索引
        User user = userMapper.selectOne(record);
        if(user==null){
            throw new CmException(ExceptionEnum.USER_NOT_EXISTS);
        }

        //校验密码
        if (!StringUtils.equals(user.getPassword(), CodeUtils.md5Hex(password,user.getSalt()))) {
           // throw new CmException(ExceptionEnum.INVALID_PASSWORD);
            throw new RuntimeException("密码错误");
        }
        return user;
    }
}
