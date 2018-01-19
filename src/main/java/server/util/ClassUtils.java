package server.util;

import com.xiaoleilu.hutool.util.ClassUtil;
import common.UploadFile;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:34
 * @Project tio-http-server
 */
public class ClassUtils {
    public static boolean isSimpleTypeOrArray(Class<?> clazz) {
        return ClassUtil.isSimpleTypeOrArray(clazz) || clazz.isAssignableFrom(UploadFile.class);
    }
}
