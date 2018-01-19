package server.handler;

import common.*;
import common.handler.HttpRequestHandler;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;
import server.mvc.Routes;

import java.nio.ByteBuffer;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:24
 * @Project tio-http-server
 */
public abstract class AbstractHttpServerAioHandler implements ServerAioHandler,HttpRequestHandler {

    protected HttpConfig httpConfig;
    protected Routes routes = null;

    public AbstractHttpServerAioHandler(){}

    public AbstractHttpServerAioHandler(HttpConfig httpConfig){
        this.httpConfig = httpConfig;
    }

    public AbstractHttpServerAioHandler(HttpConfig httpConfig,Routes routes){
       this(httpConfig);
       this.routes = routes;
    }

    public HttpConfig getHttpConfig() {
        return httpConfig;
    }
    public void setHttpConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    @Override
    public HttpRequest decode(ByteBuffer buffer,ChannelContext channelContext) throws AioDecodeException{
        return HttpRequestDecoder.decode(buffer,channelContext);
    }

    @Override
    public ByteBuffer encode(Packet packet,GroupContext groupContext,ChannelContext channelContext){
        HttpResponse response = (HttpResponse)packet;
        ByteBuffer byteBuffer = HttpResponseEncoder.encode(response,groupContext,channelContext,false);
        return byteBuffer;
    }

    @Override
    public void handler(Packet packet,ChannelContext channelContext) throws Exception{
        HttpRequest request = (HttpRequest)packet;
        HttpResponse response = this.handler(request);
        Aio.send(channelContext,response);
    }

}
