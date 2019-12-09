package com.jyhuang.java.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 统一的Json的处理工具类
 */
public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 把对象序列化为 String
     */
    public static String toString(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }

    /**
     * 把 Json 反序列化为简单的Bean
     */
    public static <T> T toBean(String json, Class<T> clazz) throws IOException {
        return MAPPER.readValue(json, clazz);
    }

    /**
     * 把 Json 反序列化为简单的List
     */
    public static <T> List<T> toList(String json, Class<T> clazz) throws IOException {
        return MAPPER.readValue(json,
                MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    /**
     * 把 Json 反序列化为简单的Map
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> k, Class<V> v) throws IOException {
        return MAPPER.readValue(json, MAPPER.getTypeFactory().constructMapType(Map.class, k, v));
    }

    /**
     * 把 Json 序列化为任意对象
     */
    public static <T> T toObject(String json, TypeReference<T> type) throws IOException {
        return MAPPER.readValue(json, type);
    }
}
