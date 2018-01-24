package client;

import common.HttpRequest;
import common.HttpResponse;
import server.annotation.RequestPath;
import server.util.Resps;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/22 16:08
 * @Project tio-http-server
 */
@RequestPath(value = "/test")
public class TestController {

    @RequestPath(value = "/hello")
    public HttpResponse test(HttpRequest request){
        return Resps.json(request,"hello,this is tio-http-server test");
    }

    @RequestPath(value = "/post",allow = "POST")
    public HttpResponse testPost(String name,int age){
        return Resps.json(null,"test post");
    }


}
