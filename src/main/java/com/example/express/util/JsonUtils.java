package com.example.express.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Json工具类
 * @date  创建时间：2018年4月17日 下午3:39:04
 * @dependency jackson-databind
 */
public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 对象-->Json字符串
     * @author jitwxs
     * @version 创建时间：2018年4月17日 下午3:39:35
     */
    public static String objectToJson(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Json字符串-->对象
     * @author jitwxs
     * @version 创建时间：2018年4月17日 下午3:39:45
     */
    public static <T> T jsonToObject(String jsonData, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonData, beanType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Json字符串--> List<对象>
     * @author jitwxs
     * @version 创建时间：2018年4月17日 下午3:40:09
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            return MAPPER.readValue(jsonData, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> Map<String, T> jsonToMap(String jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonData, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String mapToJson(Map map) {
        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> Set<T> jsonToSet(String jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonData, Set.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String setToJson(Set set) {
        try {
            return MAPPER.writeValueAsString(set);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}