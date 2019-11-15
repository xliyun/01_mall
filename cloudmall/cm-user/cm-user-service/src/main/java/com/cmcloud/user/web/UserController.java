package com.cmcloud.user.web;

import com.cmcloud.user.pojo.User;
import com.cmcloud.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-12 10:21
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /*
     * @Author xiaoliyu
     * @Description //校验数据
     * @Date 14:58 2019/4/12
     **/
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data")String data,@PathVariable("type") Integer type){

        //因为是GET请求，返回OK
        return ResponseEntity.ok(userService.checkData(data,type));
    }

    /*
     * @Author xiaoliyu
     * @Description //发送验证码
     * @Date 14:58 2019/4/12
     * @Param [phone]
     * @return org.springframework.http.ResponseEntity<java.lang.Void>
     **/
    @PostMapping("code")
    ResponseEntity<Void> sendCode(@RequestParam("phone") String phone){
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();//204无返回值
    }
                                        //valid注解是让在User类里hibernate校验注解生效
                                        //hibernate校验的数据报错后是springmvc自己处理，想要自定义处理，就在后面加上BindingResult
    @PostMapping("register")            //注册的时候传递过来的code，不属于User类，所以我们应该再用@RequestParam来接收它
    public ResponseEntity<Void> register(@Valid User user, BindingResult result, @RequestParam("code") String code){
/*       if(result.hasFieldErrors()){
           //获取字段错误 getDefaultMessage就是我们在校验字段上加的Message
           throw new RuntimeException(result.getFieldErrors().stream()
                   .map(e->e.getDefaultMessage()).collect(Collectors.joining("|")));
       }*/
        userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();//返回201
    }

    @GetMapping("/query")
    public ResponseEntity<User> queryUserByUserNameAndPasswd(
            @RequestParam("username")String username,
            @RequestParam("password")String password){
        return ResponseEntity.ok(userService.queryUserByUserNameAndPasswd(username,password));
    }
}
