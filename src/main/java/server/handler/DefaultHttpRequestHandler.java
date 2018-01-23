package server.handler;

import com.xiaoleilu.hutool.bean.BeanUtil;
import com.xiaoleilu.hutool.convert.Convert;
import com.xiaoleilu.hutool.util.ArrayUtil;
import com.xiaoleilu.hutool.util.ClassUtil;
import common.*;
import common.handler.HttpRequestHandler;
import common.session.HttpSession;
import common.utils.IpUtils;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import jodd.io.FileNameUtil;
import org.apache.commons.lang3.StringUtils;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.utils.SystemTimer;
import org.tio.utils.cache.caffeine.CaffeineCache;
import org.tio.utils.freemarker.FreemarkerUtils;
import server.intf.CurrUseridGetter;
import server.intf.HttpServerInterceptor;
import server.intf.HttpSessionListener;
import server.intf.ThrowableHandler;
import server.mvc.Routes;
import server.session.SessionCookieDecorator;
import server.stat.StatPathFilter;
import server.stat.ip.path.IpAccessStat;
import server.stat.ip.path.IpPathAccessStat;
import server.stat.ip.path.IpPathAccessStatListener;
import server.stat.ip.path.IpPathAccessStats;
import server.stat.token.TokenAccessStat;
import server.stat.token.TokenPathAccessStat;
import server.stat.token.TokenPathAccessStatListener;
import server.stat.token.TokenPathAccessStats;
import server.util.ClassUtils;
import server.util.HttpServerUtils;
import server.util.Resps;
import server.view.freemarker.FreemarkerConfig;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:25
 * @Project tio-http-server
 */
public class DefaultHttpRequestHandler implements HttpRequestHandler {

    /**
     * 静态资源的cacheName
     */
    private static final String STATIC_RES_CONTENT_CACHENAME = "TIO_HTTP_STATIC_RES_CONTENT";
    protected HttpConfig httpConfig;
    protected Routes routes = null;
    private HttpServerInterceptor httpServerInterceptor;
    private HttpSessionListener httpSessionListener;
    private ThrowableHandler throwableHandler;
    private SessionCookieDecorator sessionCookieDecorator;
    private IpPathAccessStats ipPathAccessStats;
    private TokenPathAccessStats tokenPathAccessStats;
    private CaffeineCache staticResCache;

    private String contextPath;
    private int contextPathLength = 0;
    private String suffix;
    private int suffixLength;

    private FreemarkerConfig freemarkerConfig;

    public DefaultHttpRequestHandler(HttpConfig httpConfig, Routes routes) {
        if (httpConfig == null) {
            throw new RuntimeException("httpConfig can't be null");
        }
        this.contextPath = httpConfig.getContextPath();
        this.suffix = httpConfig.getSuffix();

        if (StringUtils.isNotBlank(contextPath)) {
            this.contextPathLength = contextPath.length();
        }

        if (StringUtils.isNotBlank(suffix)) {
            this.suffixLength = suffix.length();
        }
        this.httpConfig = httpConfig;
        //静态资源缓存
        if (httpConfig.getMaxLiveTimeOfStaticRes() > 0) {
            staticResCache = CaffeineCache.register(STATIC_RES_CONTENT_CACHENAME, (long) httpConfig.getMaxLiveTimeOfStaticRes(), null);
        }
        this.routes = routes;
    }

    /**
     * session创建
     */
    private HttpSession createSession(HttpRequest request) {
        String sessionId = httpConfig.getSessionIdGenerator().sessionId(httpConfig, request);
        HttpSession httpSession = new HttpSession(sessionId);
        if (httpSessionListener != null) {
            httpSessionListener.doAfterCreated(request, httpSession, httpConfig);
        }
        return httpSession;
    }

    public HttpConfig getHttpConfig() {
        return httpConfig;
    }

    public HttpServerInterceptor getHttpServerInterceptor() {
        return httpServerInterceptor;
    }

    public static Cookie getSessionCookie(HttpRequest request, HttpConfig httpConfig) {
        Cookie sessionCookie = request.getCookie(httpConfig.getSessionCookieName());
        return sessionCookie;
    }
    public CaffeineCache getStaticResCache() {
        return staticResCache;
    }

    /**
     * 检查域名
     * */
    private boolean checkDomain(HttpRequest request) {
        String[] allowDomains = httpConfig.getAllowDomains();
        if (allowDomains == null || allowDomains.length == 0) {
            return true;
        }
        String host = request.getHost();
        return ArrayUtil.contains(allowDomains, host);
    }

    /**
     * 处理请求
     * */
    @Override
    public HttpResponse handler(HttpRequest request) throws Exception {
        if (!checkDomain(request)) {
            //域名检查未通过
            Aio.remove(request.getChannelContext(), "wrong domain:" + request.getDomain());
            return null;
        }

        HttpResponse response = null;
        RequestLine requestLine = request.getRequestLine();
        String path = requestLine.getPath();
        if (StringUtils.isNotBlank(contextPath)) {
            if (StringUtils.startsWith(path, contextPath)) {
                path = StringUtils.substring(path, contextPathLength);
            }
        }
        if (StringUtils.isNotBlank(suffix)) {
            if (StringUtils.endsWith(path, suffix)) {
                path = StringUtils.substring(path, 0, path.length() - suffixLength);
            }
        }
        requestLine.setPath(path);

        FileCache fileCache = null;
        File file = null;

        try {
            processCookieBeforeHandler(request, requestLine);
            HttpSession httpSession = request.getHttpSession();
            //如果拦截器返回了响应，以拦截器为准
            if (httpServerInterceptor != null) {
                response = httpServerInterceptor.doBeforeHandler(request, requestLine, response);
                if (response != null) {
                    return response;
                }
            }

            requestLine = request.getRequestLine();
            path = requestLine.getPath();

            Method method = null;
            if (routes != null) {
                method = routes.getMethodByPath(path, request);
            }

            if (method != null) {
                String[] paramnames = routes.methodParamnameMap.get(method);
                Class<?>[] parameterTypes = method.getParameterTypes();

                Object bean = routes.methodBeanMap.get(method);
                Object obj = null;
                Map<String, Object[]> params = request.getParams();
                if (parameterTypes == null || parameterTypes.length == 0) {
                    obj = method.invoke(bean);
                } else {
                    Object[] paramValues = new Object[parameterTypes.length];
                    int i = 0;
                    for (Class<?> paramType : parameterTypes) {
                        try {

                            if (paramType.isAssignableFrom(HttpRequest.class)) {
                                paramValues[i] = request;
                            } else if (paramType == HttpSession.class) {
                                paramValues[i] = httpSession;
                            } else if (paramType.isAssignableFrom(HttpConfig.class)) {
                                paramValues[i] = httpConfig;
                            } else if (paramType.isAssignableFrom(ChannelContext.class)) {
                                paramValues[i] = request.getChannelContext();
                            } else {
                                if (params != null) {
                                    if (server.util.ClassUtils.isSimpleTypeOrArray(paramType)) {
                                        Object[] value = params.get(paramnames[i]);
                                        if (value != null && value.length > 0) {
                                            if (paramType.isArray()) {
                                                paramValues[i] = Convert.convert(paramType, value);
                                            } else {
                                                paramValues[i] = Convert.convert(paramType, value[0]);
                                            }
                                        }
                                    } else {
                                        paramValues[i] = paramType.newInstance();
                                        Set<Map.Entry<String, Object[]>> set = params.entrySet();
                                        label2:
                                        for (Map.Entry<String, Object[]> entry : set) {
                                            String fieldName = entry.getKey();
                                            Object[] fieldValue = entry.getValue();

                                            PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(paramType, fieldName, true);
                                            if (propertyDescriptor == null) {
                                                continue label2;
                                            } else {
                                                Method writeMethod = propertyDescriptor.getWriteMethod();
                                                if (writeMethod == null) {
                                                    continue label2;
                                                }
                                                writeMethod = ClassUtil.setAccessible(writeMethod);
                                                Class<?>[] classes = writeMethod.getParameterTypes();
                                                if (classes == null || classes.length != 1) {
                                                    //方法的参数长度不为1
                                                    continue label2;
                                                }
                                                Class<?> clazz = classes[0];
                                                if (ClassUtils.isSimpleTypeOrArray(clazz)) {
                                                    if (fieldValue != null && fieldValue.length > 0) {
                                                        if (clazz.isArray()) {
                                                            Object theValue = Convert.convert(clazz, fieldValue);
                                                            writeMethod.invoke(paramValues[i], theValue);
                                                        } else {
                                                            Object theValue = Convert.convert(clazz, fieldValue[0]);
                                                            writeMethod.invoke(paramValues[i], theValue);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Throwable e) {

                        } finally {
                            i++;
                        }
                    }
                    obj = method.invoke(bean, paramValues);
                }

                if (obj instanceof HttpResponse) {
                    response = (HttpResponse) obj;
                    return response;
                } else {
                    if (obj == null) {
                        if (method.getReturnType() == HttpResponse.class) {
                            return null;
                        } else {
                            Resps.json(request, obj);
                        }
                    } else {
                        response = Resps.json(request, obj);
                    }
                    return response;
                }

            } else {
                if (staticResCache != null) {
                    fileCache = (FileCache) staticResCache.get(path);
                }
                if (fileCache != null) {
                    long lastModified = fileCache.getLastModified();
                    response = Resps.try304(request, lastModified);
                    if (response != null) {
                        response.addHeader(HttpConst.ResponseHeaderKey.tio_from_cache, "true");
                        return response;
                    }
                    response = fileCache.getResponse();
                    response = HttpResponse.cloneResponse(request, response);
                    return response;
                } else {
                    File pageRoot = httpConfig.getPageRoot();
                    if (pageRoot != null) {
                        file = new File(pageRoot + path);
                        if (!file.exists() || file.isDirectory()) {
                            path = path + "page/index.html";
                        } else {
                            path = path + "/page/index.html";
                        }
                        file = new File(pageRoot, path);
                        if (file.exists()) {
                            if (freemarkerConfig != null) {
                                String extension = FileNameUtil.getExtension(file.getName());
                                if (ArrayUtil.contains(freemarkerConfig.getSuffixes(), extension)) {
                                    Configuration configuration = freemarkerConfig.getConfiguration();
                                    Object model = freemarkerConfig.getModelMaker().generate(request);
                                    if (configuration != null) {
                                        TemplateLoader templateLoader = configuration.getTemplateLoader();
                                        if (templateLoader instanceof FileTemplateLoader) {
                                            try {
                                                String filePath = file.getCanonicalPath();
                                                String pageRootPath = httpConfig.getPageRoot().getCanonicalPath();
                                                String template = StringUtils.substring(filePath, pageRootPath.length());
                                                String retStr = FreemarkerUtils.generateStringByFile(template, configuration, model);
                                                response = Resps.bytes(request, retStr.getBytes(configuration.getDefaultEncoding()), extension);
                                                return response;
                                            } catch (java.lang.Throwable e) {

                                            }
                                        }
                                    }
                                }
                            }

                            response = Resps.file(request, file);
                            response.setStaticRes(true);
                            //静态资源放入缓存
                            if (response.isStaticRes() && staticResCache != null) {
                                if (response.getBody() != null && response.getStatus() != HttpResponseStatus.C200) {
                                    String contentType = response.getHeader(HttpConst.ResponseHeaderKey.Content_Type);
                                    String contentEncoding = response.getHeader(HttpConst.ResponseHeaderKey.Content_Encoding);
                                    String lastModified = response.getHeader(HttpConst.ResponseHeaderKey.Last_Modified);

                                    Map<String, String> headers = new HashMap<>();
                                    if (StringUtils.isNotBlank(contentType)) {
                                        headers.put(HttpConst.ResponseHeaderKey.Content_Type, contentType);
                                    }
                                    if (StringUtils.isNotBlank(contentEncoding)) {
                                        headers.put(HttpConst.ResponseHeaderKey.Content_Encoding, contentEncoding);
                                    }
                                    if (StringUtils.isNotBlank(lastModified)) {
                                        headers.put(HttpConst.ResponseHeaderKey.Last_Modified, lastModified);
                                    }

                                    HttpResponse responseInCache = new HttpResponse(request);
                                    responseInCache.addHeaders(headers);
                                    responseInCache.setBody(response.getBody());
                                    responseInCache.setHasGzipped(response.isHasGzipped());

                                    fileCache = new FileCache(responseInCache, file.lastModified());
                                    staticResCache.put(path, fileCache);
                                }
                            }
                            return response;
                        }
                    }
                }
            }
            return resp404(request, requestLine);

        } catch (Throwable e) {
            response = resp500(request, requestLine, e);
            return response;
        } finally {
            if (response != null) {
                try {
                    processCookieAfterHandler(request, requestLine, response);
                    if (httpServerInterceptor != null) {
                        httpServerInterceptor.doAfterHandler(request, requestLine, response);
                    }
                } catch (Throwable e) {

                } finally {
                    HttpServerUtils.gzip(request, response);
                    long time = SystemTimer.currentTimeMillis();
                    long iv = time - request.getCreateTime();

                    boolean f = statIpPath(request, response, path, iv);
                    if (!f) {
                        return null;
                    }
                    f = statTokenPath(request, response, path, iv);
                    if (!f) {
                        return null;
                    }
                }
            }
        }
    }

    private void processCookieBeforeHandler(HttpRequest request,RequestLine requestLine) throws ExecutionException{
        if (!httpConfig.isUseSession()){
            return;
        }
        Cookie cookie = getSessionCookie(request,httpConfig);
        HttpSession httpSession = null;
        if (cookie == null){
            httpSession = createSession(request);
        }else{
            String sessionId = cookie.getValue();
            httpSession = (HttpSession)httpConfig.getSessionStore().get(sessionId);
            if (httpSession == null){
                httpSession = createSession(request);
            }
        }
        request.setHttpSession(httpSession);
    }
    private void processCookieAfterHandler(HttpRequest request, RequestLine requestLine, HttpResponse httpResponse) throws ExecutionException {
        if (!httpConfig.isUseSession()) {
            return;
        }

        HttpSession httpSession = request.getHttpSession();//(HttpSession) channelContext.getAttribute();//.getHttpSession();//not null
        Cookie cookie = getSessionCookie(request, httpConfig);
        String sessionId = null;

        if (cookie == null) {
            createSessionCookie(request, httpSession, httpResponse);
        } else {
            sessionId = cookie.getValue();
            HttpSession httpSession1 = (HttpSession) httpConfig.getSessionStore().get(sessionId);

            if (httpSession1 == null) {//有cookie但是超时了
                createSessionCookie(request, httpSession, httpResponse);
            }
        }
    }
    private Cookie createSessionCookie(HttpRequest request, HttpSession httpSession, HttpResponse httpResponse) {
        String sessionId = httpSession.getId();
        //		String host = request.getHost();
        String domain = request.getDomain();

        String name = httpConfig.getSessionCookieName();
        long maxAge = httpConfig.getSessionTimeout() * 30;

        Cookie sessionCookie = new Cookie(domain, name, sessionId, maxAge);

        if (sessionCookieDecorator != null) {
            sessionCookieDecorator.decorate(sessionCookie);
        }
        httpResponse.addCookie(sessionCookie);

        httpConfig.getSessionStore().put(sessionId, httpSession);

        return sessionCookie;
    }
    private boolean statIpPath(HttpRequest request, HttpResponse response, String path, long iv) {
        if (response.isSkipIpStat() || request.isClosed()) {
            return true;
        }

        //统计一下IP访问数据
        if (ipPathAccessStats != null) {
            String ip = IpUtils.getRealIp(request);
            List<Long> list = ipPathAccessStats.durationList;

            Cookie cookie = getSessionCookie(request, httpConfig);

            StatPathFilter statPathFilter = ipPathAccessStats.getStatPathFilter();

            //添加统计
            for (Long duration : list) {
                IpAccessStat ipAccessStat = ipPathAccessStats.get(duration, ip);//.get(duration, ip, path);//.get(v, channelContext.getClientNode().getIp());

                ipAccessStat.count.incrementAndGet();
                ipAccessStat.timeCost.addAndGet(iv);
                ipAccessStat.setLastAccessTime(SystemTimer.currentTimeMillis());
                if (cookie == null) {
                    ipAccessStat.noSessionCount.incrementAndGet();
                } else {
                    ipAccessStat.sessionIds.add(cookie.getValue());
                }

                if (statPathFilter.filter(path, request, response)) {
                    IpPathAccessStat ipPathAccessStat = ipAccessStat.get(path);
                    ipPathAccessStat.count.incrementAndGet();
                    ipPathAccessStat.timeCost.addAndGet(iv);
                    ipPathAccessStat.setLastAccessTime(SystemTimer.currentTimeMillis());

                    if (cookie == null) {
                        ipPathAccessStat.noSessionCount.incrementAndGet();
                    } else {
                        ipAccessStat.sessionIds.add(cookie.getValue());
                    }

                    IpPathAccessStatListener ipPathAccessStatListener = ipPathAccessStats.getListener(duration);
                    if (ipPathAccessStatListener != null) {
                        boolean isContinue = ipPathAccessStatListener.onChanged(request, ip, path, ipAccessStat, ipPathAccessStat);
                        if (!isContinue) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * tokenPathAccessStat
     * @param request
     * @param response
     * @param path
     * @param iv
     * @return
     */
    private boolean statTokenPath(HttpRequest request, HttpResponse response, String path, long iv) {
        if (response.isSkipTokenStat() || request.isClosed()) {
            return true;
        }
        //统计一下Token访问数据
        if (tokenPathAccessStats != null) {
            String token = tokenPathAccessStats.getTokenGetter().getToken(request);
            if (StringUtils.isNotBlank(token)) {
                List<Long> list = tokenPathAccessStats.durationList;

                CurrUseridGetter currUseridGetter = tokenPathAccessStats.getCurrUseridGetter();
                String uid = null;
                if (currUseridGetter != null) {
                    uid = currUseridGetter.getUserid(request);
                }

                StatPathFilter statPathFilter = tokenPathAccessStats.getStatPathFilter();

                //添加统计
                for (Long duration : list) {
                    TokenAccessStat tokenAccessStat = tokenPathAccessStats.get(duration, token, request.getClientIp(), uid);//.get(duration, ip, path);//.get(v, channelContext.getClientNode().getIp());

                    tokenAccessStat.count.incrementAndGet();
                    tokenAccessStat.timeCost.addAndGet(iv);
                    tokenAccessStat.setLastAccessTime(SystemTimer.currentTimeMillis());

                    if (statPathFilter.filter(path, request, response)) {
                        TokenPathAccessStat tokenPathAccessStat = tokenAccessStat.get(path);
                        tokenPathAccessStat.count.incrementAndGet();
                        tokenPathAccessStat.timeCost.addAndGet(iv);
                        tokenPathAccessStat.setLastAccessTime(SystemTimer.currentTimeMillis());

                        TokenPathAccessStatListener tokenPathAccessStatListener = tokenPathAccessStats.getListener(duration);
                        if (tokenPathAccessStatListener != null) {
                            boolean isContinue = tokenPathAccessStatListener.onChanged(request, token, path, tokenAccessStat, tokenPathAccessStat);
                            if (!isContinue) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }


    @Override
    public HttpResponse resp404(HttpRequest request, RequestLine requestLine) {
        return Resps.resp404(request, requestLine, httpConfig);
    }

    @Override
    public HttpResponse resp500(HttpRequest request, RequestLine requestLine, Throwable throwable) {
        if (throwableHandler != null) {
            return throwableHandler.handler(request, requestLine, throwable);
        }
        return Resps.resp500(request, requestLine, httpConfig, throwable);
    }

    public void setHttpConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    public void setHttpServerInterceptor(HttpServerInterceptor httpServerInterceptor) {
        this.httpServerInterceptor = httpServerInterceptor;
    }

    /**
     * @param staticResCache the staticResCache to set
     */
    public void setStaticResCache(CaffeineCache staticResCache) {
        this.staticResCache = staticResCache;
    }

    @Override
    public void clearStaticResCache(HttpRequest request) {
        if (staticResCache != null) {
            staticResCache.clear();
        }
    }

    public HttpSessionListener getHttpSessionListener() {
        return httpSessionListener;
    }

    public void setHttpSessionListener(HttpSessionListener httpSessionListener) {
        this.httpSessionListener = httpSessionListener;
    }

    public SessionCookieDecorator getSessionCookieDecorator() {
        return sessionCookieDecorator;
    }

    public void setSessionCookieDecorator(SessionCookieDecorator sessionCookieDecorator) {
        this.sessionCookieDecorator = sessionCookieDecorator;
    }

    public IpPathAccessStats getIpPathAccessStats() {
        return ipPathAccessStats;
    }

    public void setIpPathAccessStats(IpPathAccessStats ipPathAccessStats) {
        this.ipPathAccessStats = ipPathAccessStats;
    }

    public FreemarkerConfig getFreemarkerConfig() {
        return freemarkerConfig;
    }

    public void setFreemarkerConfig(FreemarkerConfig freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public ThrowableHandler getThrowableHandler() {
        return throwableHandler;
    }

    public void setThrowableHandler(ThrowableHandler throwableHandler) {
        this.throwableHandler = throwableHandler;
    }

    public TokenPathAccessStats getTokenPathAccessStats() {
        return tokenPathAccessStats;
    }

    public void setTokenPathAccessStats(TokenPathAccessStats tokenPathAccessStats) {
        this.tokenPathAccessStats = tokenPathAccessStats;
    }
}
