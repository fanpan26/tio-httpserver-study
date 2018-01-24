package server.mvc;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import jodd.io.FileUtil;
import com.xiaoleilu.hutool.util.ArrayUtil;
import common.HttpRequest;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ClassAnnotationMatchProcessor;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.MethodAnnotationMatchProcessor;
import org.apache.commons.lang3.StringUtils;
import org.tio.utils.json.Json;
import server.annotation.RequestPath;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:26
 * @Project tio-http-server
 */
public class Routes {
    private static String formateBeanPath(String initPath){
        return initPath;
    }
    private static String formateMethodPath(String initPath){
        return initPath;
    }

    /**
     * 路径和对象映射
     * key:/user
     * value:object
     * */
    public Map<String,Object> pathBeanMap = new TreeMap<>();
    /**
     * 路径和class映射
     * key:/user
     * value:Class
     * */
    public Map<String,Class<?>> pathClassMap = new TreeMap<>();

    /**
     * class 和路径映射
     * key: class
     * value: /user
     */
    public Map<Class<?>,String> classPathMap = new HashMap<>();

    /**
     * 路径 method 映射
     * */
    public Map<String, Method> pathMethodMap = new TreeMap<>();

    //私人定制
    public Map<String,String> pathAllowMethodMap = new TreeMap<>();

    /**
     * Method路径映射
     * 只是用于打印日志
     * key: /user/update
     * value: method string
     */
    public Map<String, String> pathMethodstrMap = new TreeMap<>();
    /**
     * 含有路径变量的请求
     * key: 子路径的个数（pathUnitCount），譬如/user/{userid}就是2
     * value: VariablePathVo
     */
    public Map<Integer,VariablePathVo[]> variablePathMap = new TreeMap<>();
    /**
     * 含有路径变量的请求
     * 只是用于打印日志
     * key: 配置的路径/user/{userid}
     * value: method string
     */
    public Map<String,String> variablePathMethodstrMap = new TreeMap<>();
    /**
     * 方法参数名映射
     * key: method
     * value: ["id", "name", "scanPackages"]
     */
    public Map<Method,String[]> methodParamnameMap = new HashMap<>();
    /**
     * 方法和对象映射
     * key: method
     * value: bean
     */
    public  Map<Method,Object> methodBeanMap = new HashMap<>();

    StringBuilder errorStr = new StringBuilder();

    /**
     * 构造方法传入要扫描的包
     * */
    public Routes(String[] scanPackages){
        if(scanPackages != null) {
            final FastClasspathScanner fastClasspathScanner = new FastClasspathScanner(scanPackages);

            //扫描带有RequestPath注解的类。
            fastClasspathScanner.matchClassesWithAnnotation(RequestPath.class, new ClassAnnotationMatchProcessor() {
                //匹配到之后执行的方法
                @Override
                public void processMatch(Class<?> classWithAnnotation) {
                    //这里classWithAnnotation就是 TestController
                    try {
                        //通过反射新创建一个TestController的实例
                        Object bean = classWithAnnotation.newInstance();
                        //获取到注解对象
                        RequestPath mapping = classWithAnnotation.getAnnotation(RequestPath.class);
                        //拿到 value 值 即：/test
                        String beanPath = mapping.value();
                        //暂时忽略，源代码中注释掉了，返回beanPath，即无处理
                        beanPath = formateBeanPath(beanPath);
                        //判断有没有重复定义的路由
                        Object obj = pathBeanMap.get(beanPath);
                        if (obj != null) {
                            errorStr.append("mapping[" + beanPath + "] already exists in class [" + obj.getClass().getName() + "]\r\n\r\n");
                        } else {
                            //将 /test 和 TestController 对象的实例存储到 TreeMap中
                            pathBeanMap.put(beanPath, bean);
                            //存储 /test class  以便后续使用
                            pathClassMap.put(beanPath, classWithAnnotation);
                            //存储 class /test 以便后续使用
                            classPathMap.put(classWithAnnotation, beanPath);
                        }
                    } catch (Throwable e) {

                    }
                }
            });
            //扫描带有RequestPath注解的方法
            fastClasspathScanner.matchClassesWithMethodAnnotation(RequestPath.class, new MethodAnnotationMatchProcessor() {
                @Override
                public void processMatch(Class<?> matchingClass, Executable matchingMethodOrConstructor) {
                    //匹配到方法之后获取注解
                    RequestPath mapping = matchingMethodOrConstructor.getAnnotation(RequestPath.class);
                    //得到方法名
                    String methodName = matchingMethodOrConstructor.getName();
                    //得到路由path
                    String methodPath = mapping.value();
                    String allow = mapping.allow();
                    methodPath = formateMethodPath(methodPath);
                    String beanPath = classPathMap.get(matchingClass);

                    if (StringUtils.isBlank(beanPath)) {
                        errorStr.append("方法有注解，但类没注解, method:" + methodName + ", class:" + matchingClass + "\r\n\r\n");
                        return;
                    }

                    Object bean = pathBeanMap.get(beanPath);
                    String completeMethodPath = methodPath;
                    //组合路径  /test + /hello
                    if (beanPath != null) {
                        completeMethodPath = beanPath + methodPath;
                    }

                    //获取方法的参数类型数组
                    Class<?>[] parameterTypes = matchingMethodOrConstructor.getParameterTypes();
                    Method method;
                    try {
                        //这儿有点小看不懂，应该就是获取方法的参数信息
                        method = matchingClass.getMethod(methodName, parameterTypes);
                        Paranamer paranamer = new BytecodeReadingParanamer();
                        String[] parameterNames = paranamer.lookupParameterNames(method, false);
                        Method checkMethod = pathMethodMap.get(completeMethodPath);
                        if (checkMethod != null) {
                            errorStr.append("mapping[" + completeMethodPath + "] already exists in method [" + checkMethod.getDeclaringClass() + "#" + checkMethod.getName() + "]\r\n\r\n");
                            return;
                        }
                        //存储  /test/hello method
                        pathMethodMap.put(completeMethodPath, method);
                        //存放是否允许的方法
                        pathAllowMethodMap.put(completeMethodPath,allow);
                        //用于打印
                        pathMethodstrMap.put(completeMethodPath, methodToStr(method, parameterNames));
                        //存储  method  参数
                        methodParamnameMap.put(method, parameterNames);
                        //存储 method  对应 TestController实例
                        methodBeanMap.put(method, bean);
                    } catch (Throwable e) {

                    }
                }
            });

            fastClasspathScanner.scan();
            //用于打印
            String pathClassMapStr = Json.toFormatedJson(pathClassMap);
            //用于打印
            String pathMethodstrMapStr = Json.toFormatedJson(pathMethodstrMap);

            processVariablePath();
            String variablePathMethodstrMapStr = Json.toFormatedJson(variablePathMethodstrMap);

            try {
                FileUtil.writeString("/tio_path_class.json", pathClassMapStr, "utf-8");
                FileUtil.writeString("/tio_path_method.json", pathMethodstrMapStr, "utf-8");
                FileUtil.writeString("/tio_variablepath_method.json", variablePathMethodstrMapStr, "utf-8");

                if (errorStr.length() > 0) {
                    FileUtil.writeString("/tio_mvc_error.txt", errorStr.toString(), "utf-8");
                }
            }catch (IOException e){

            }

        }
    }

    /**
     * 处理有变量的路径
     * */
    private void processVariablePath(){
        Set<Map.Entry<String,Method>> set = pathMethodMap.entrySet();
        for (Map.Entry<String,Method> entry : set){
            String path = entry.getKey();
            Method method = entry.getValue();
            if(StringUtils.contains(path,"{") && StringUtils.contains(path,"}")){
                String[] pathUnits = StringUtils.split(path, "/");
                PathUnitVo[] pathUnitVos = new PathUnitVo[pathUnits.length];
                //是否是带变量的路径
                boolean isVarPath = false;
                for(int i=0;i<pathUnits.length;i++){
                    PathUnitVo pathUnitVo = new PathUnitVo();
                    String pathUnit = pathUnits[i];
                    if (StringUtils.contains(pathUnit, "{") || StringUtils.contains(pathUnit, "}")) {
                        if (StringUtils.startsWith(pathUnit, "{") && StringUtils.endsWith(pathUnit, "}")) {
                            String[] xx = methodParamnameMap.get(method);
                            String varName = StringUtils.substringBetween(pathUnit, "{", "}");
                            if (ArrayUtil.contains(xx, varName)) {
                                isVarPath = true;
                                pathUnitVo.setVar(true);
                                pathUnitVo.setPath(varName);
                            } else {
                                errorStr.append("path:{" + path + "}, 对应的方法中并没有包含参数名为" + varName + "的参数\r\n\r\n");
                            }
                        } else {
                            pathUnitVo.setVar(false);
                            pathUnitVo.setPath(pathUnit);
                        }
                    } else {
                        pathUnitVo.setVar(false);
                        pathUnitVo.setPath(pathUnit);
                    }
                    pathUnitVos[i] = pathUnitVo;
                }

                if (isVarPath) {
                    VariablePathVo variablePathVo = new VariablePathVo(path, method, pathUnitVos);
                    addVariablePathVo(pathUnits.length, variablePathVo);
                }
            }
        }
    }

    private  void addVariablePathVo(Integer pathUnitCount,VariablePathVo variablePathVo){
        VariablePathVo[] existValue = variablePathMap.get(pathUnitCount);
        if (existValue == null) {
            existValue = new VariablePathVo[] { variablePathVo };
            variablePathMap.put(pathUnitCount, existValue);
        } else {
            VariablePathVo[] newExistValue = new VariablePathVo[existValue.length + 1];
            System.arraycopy(existValue, 0, newExistValue, 0, existValue.length);
            newExistValue[newExistValue.length - 1] = variablePathVo;
            variablePathMap.put(pathUnitCount, newExistValue);
        }
        variablePathMethodstrMap.put(variablePathVo.getPath(), methodToStr(variablePathVo.getMethod(), methodParamnameMap.get(variablePathVo.getMethod())));
    }

    @SuppressWarnings("unused")
    private VariablePathVo[] getVariablePathVos(Integer pathUnitCount, boolean forceCreate) {
        VariablePathVo[] ret = variablePathMap.get(pathUnitCount);
        if (forceCreate && ret == null) {
            ret = new VariablePathVo[0];
            variablePathMap.put(pathUnitCount, ret);
        }
        return ret;
    }

    private String methodToStr(Method method, String[] parameterNames) {
        return method.getDeclaringClass().getName() + "." + method.getName() + "(" + ArrayUtil.join(parameterNames, ",") + ")";
    }

    /**
     * 检查请求方法是否允许
     * */
    public  boolean checkAllowMethod(String path,HttpRequest request) {
        String allows = pathAllowMethodMap.get(path);
        common.Method requestMethod = request.getRequestLine().getMethod();
        String[] allowMethods = StringUtils.split(allows, ",");
        return ArrayUtil.contains(allowMethods,requestMethod.name());
    }

    @SuppressWarnings("unused")
    public Method getMethodByPath(String path, HttpRequest request){
        Method method = pathMethodMap.get(path);

        if(method == null){
            String[] pathUnitsOfRequest = StringUtils.split(path,"/");
            VariablePathVo[] variablePathVos = variablePathMap.get(pathUnitsOfRequest.length);
            if(variablePathVos != null){
                tag1:for(VariablePathVo variablePathVo : variablePathVos){
                    PathUnitVo[] pathUnitVos = variablePathVo.getPathUnits();
                    tag2:for(int i=0;i<pathUnitVos.length;i++){
                        PathUnitVo pathUnitVo = pathUnitVos[i];
                        String pathUnitOfRequest = pathUnitsOfRequest[i];
                        if(pathUnitVo.isVar()){
                            request.addParam(pathUnitVo.getPath(),pathUnitOfRequest);
                        }else{
                            if(!StringUtils.equals(pathUnitVo.getPath(),pathUnitOfRequest)){
                                continue tag1;
                            }
                        }
                    }
                    method = variablePathVo.getMethod();
                    return method;
                }
            }
            return null;
        }else{
            return method;
        }
    }
}



































