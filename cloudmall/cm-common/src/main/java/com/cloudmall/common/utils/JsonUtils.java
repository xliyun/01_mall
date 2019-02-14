package com.cloudmall.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-24 17:20
 * 因为springmvc默认jackson，所以没有fastjson
 **/
public class JsonUtils {

    public static final ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    //@Nullable
    public static String serialize(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj.getClass() == String.class) {
            return (String) obj;
        }
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("json序列化出错：" + obj, e);
            return null;
        }
    }

    //@Nullable
    public static <T> T parse(String json, Class<T> tClass) {
        try {
            return mapper.readValue(json, tClass);
        } catch (IOException e) {
            logger.error("json解析出错：" + json, e);
            return null;
        }
    }

    //@Nullable
    public static <E> List<E> parseList(String json, Class<E> eClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, eClass));
        } catch (IOException e) {
            logger.error("json解析出错：" + json, e);
            return null;
        }
    }

    //@Nullable
    public static <K, V> Map<K, V> parseMap(String json, Class<K> kClass, Class<V> vClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructMapType(Map.class, kClass, vClass));
        } catch (IOException e) {
            logger.error("json解析出错：" + json, e);
            return null;
        }
    }

    //@Nullable
    public static <T> T nativeRead(String json, TypeReference<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            logger.error("json解析出错：" + json, e);
            return null;
        }
    }

    public static void main(String[] args) {
        User user = new User("jack","21");
        //json序列化
        String json = serialize(user);
        System.out.println(json);
        //反序列化
        User user1 = parse(json, User.class);
        System.out.println("user1="+user1);
        //toList String Integer等简单的常量
        String json2="[20,11,-19,33]";
        List<Integer> list = parseList(json2,Integer.class);
        System.out.println("list="+list);

        //toMap map里是简单的string int
        String json3="{\"name\":\"zhangsan\",\"age\":33}";
        Map<String, String> map = parseMap(json3, String.class, String.class);
        System.out.println("map="+map);

        //List装的是复杂的对象，比如user(也可以看做是Map<String,String))
        String json4="[{\"name\":\"zhangsan\",\"age\":33},{\"name\":\"lisi\",\"age\":45}]";
        //TypeReference类型引用
        List<Map<String, String>> maps = nativeRead(json4, new TypeReference<List<Map<String, String>>>() {
        });
        for (Map<String, String> map2 : maps) {
            System.out.println("map="+map2);
        }
    }
}
