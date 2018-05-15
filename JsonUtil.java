package com.bestpay.insurance.cbs.common.utils;

import java.sql.Timestamp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Mr_Zhang on 2015/12/25.
 */
@Slf4j
public class JsonUtil {
	private JsonUtil() {
		throw new IllegalStateException("JsonUtil class");
	}
    private static SerializeConfig mapping = new SerializeConfig();
    private static String dateFormat;

    static {
        dateFormat = "yyyy-MM-dd HH:mm:ss";
    }

    /**
     * 将json 转换为java对象
     */
    public static <T>T getBean(String jsonString, Class<T> clazz) {
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(jsonString);
        } catch (Exception e) {
            log.error("异常:[{}]", e.toString());
            return null;
        }
        return JSONObject.toJavaObject(jsonObject, clazz);
    }

    /**
     * 将Map等转为json 再转换为java对象
     * 将T转化成clazz
     */
    public static <T> T getBean(Object obj, Class<T>clazz) {
        JSONObject jsonObject = null;
        try {
            String json = JSONObject.toJSONString(obj);
            jsonObject = JSONObject.parseObject(json);
        } catch (Exception e) {
            log.error("异常:[{}]", e.toString());
        }
        return JSONObject.toJavaObject(jsonObject, clazz);
    }

    /**
     * 将对象转换为json
     *
     * @param o
     * @return
     */
    public static String getJSONString(Object o) {
        return JSONObject.toJSONString(o);
    }

    /**
     * 自定义时间格式
     *
     * @param o
     * @return
     */
    public static String toJSON(Object o) {
        mapping.put(Timestamp.class, new SimpleDateFormatSerializer(dateFormat));
        return JSON.toJSONString(o, mapping);
    }

    /**
     * java对象转换成json字符串
     *
     * @param obj Object 对象
     * @return json 字符串
     */
    public static String toJsons(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * json字符串转成java对象
     *
     * @param str  字符串
     * @param type class 对象
     * @return class 对象
     */
    public static <T> T fromJson(String str, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(str, type);
    }
}
