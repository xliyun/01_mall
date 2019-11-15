package com.cmcloud.page.web;

import com.cmcloud.page.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-02 15:12
 */
@Controller
public class HelloController {

    @GetMapping("hello")
    public String toHello(Model model){
        User user=new User();
        user.setAge(21);
        user.setName("zhangsan");
        user.setFriend(new User("lisi",33,null));

        User user1=new User("wangwu",44,null);
        model.addAttribute("user",user);
        model.addAttribute("users", Arrays.asList(user,user1));
        model.addAttribute("msg","hello, thymeleaf!");
        return "hello";//普通字符串被当成视图名称，结合前缀和后缀 以前是通过InternalResourceViewResulver.java jstl视图解析器，我们没有引入Jsp的依赖用的是thymeleaf的依赖
    }
    //ThymeleafViewResolver 来解析thymeleaf的视图

}
