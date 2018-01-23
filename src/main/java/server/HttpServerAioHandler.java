package server;

import common.*;
import common.handler.HttpRequestHandler;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;

import java.nio.ByteBuffer;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:21
 * @Project tio-http-server
 */
public class HttpServerAioHandler implements ServerAioHandler{

    public static final String REQUEST_KEY = "tio_request_key";

    protected HttpConfig httpConfig;

    private HttpRequestHandler requestHandler;

    public HttpConfig getHttpConfig() {
        return httpConfig;
    }
    public void setHttpConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    public HttpServerAioHandler(HttpConfig httpConfig, HttpRequestHandler requestHandler){
        this.httpConfig = httpConfig;
        this.requestHandler = requestHandler;
    }


    @Override
    public Packet decode(ByteBuffer byteBuffer, ChannelContext channelContext) throws AioDecodeException {
        HttpRequest request = HttpRequestDecoder.decode(byteBuffer,channelContext);
        channelContext.setAttribute(REQUEST_KEY,request);
        return request;
    }

    @Override
    public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
        HttpResponse httpResponse = (HttpResponse)packet;
        ByteBuffer byteBuffer = HttpResponseEncoder.encode(httpResponse,groupContext,channelContext,false);
        return byteBuffer;
    }

    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        HttpRequest request = (HttpRequest)packet;
        String ip = request.getClientIp();
        if (channelContext.getGroupContext().ipBlacklist.isInBlacklist(ip)){
            Aio.remove(channelContext,ip + "在黑名单中");
            return;
        }
        HttpResponse httpResponse = requestHandler.handler(request);
        if (httpResponse != null){
            Aio.send(channelContext,httpResponse);
        }else{
            Aio.remove(channelContext,"handler return null");
        }
    }
}
