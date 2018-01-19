package common;

import java.util.Objects;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:18
 * @Project tio-http-server
 */
public enum Method {
    GET("GET"), POST("POST"), HEAD("HEAD"), PUT("PUT"), TRACE("TRACE"), OPTIONS("OPTIONS"), PATCH("PATCH");
    public static Method from(String method) {
        Method[] values = Method.values();
        for (Method v : values) {
            if (Objects.equals(v.value, method)) {
                return v;
            }
        }
        return GET;
    }

    String value;

    private Method(String value) {
        this.value = value;
    }
}
