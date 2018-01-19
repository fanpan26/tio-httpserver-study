package server.util;

import com.xiaoleilu.hutool.util.ZipUtil;
import common.HttpConst;
import common.HttpRequest;
import common.HttpResponse;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:34
 * @Project tio-http-server
 */
public class HttpServerUtils {
    public HttpServerUtils(){}

    /**
     * gzip 压缩
     * */
    public static void gzip(HttpRequest request, HttpResponse response){
        if (response == null){
            return;
        }
        if(response.isHasGzipped()){
            return;
        }
        if(request.getIsSupportGzip()){
            byte[] bs = response.getBody();
            if(bs != null && bs.length >=600){
                byte[] bs2 = ZipUtil.gzip(bs);
                if(bs2.length < bs.length){
                    response.setBody(bs2);
                    response.setHasGzipped(true);
                    response.addHeader(HttpConst.ResponseHeaderKey.Content_Encoding,"gzip");
                }
            }
        }
    }
}
