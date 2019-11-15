package com.cmcloud.page.pojo;

import lombok.Data;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-02 15:55
 */
@Data
public class User {
    String  name;
    int age;
    User friend;//对象类型属性

    public User() {
    }

    public User(String name, int age, User friend) {
        this.name = name;
        this.age = age;
        this.friend = friend;
    }
}
