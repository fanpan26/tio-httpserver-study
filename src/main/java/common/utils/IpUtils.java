package common.utils;

import common.HttpConfig;
import common.HttpRequest;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:17
 * @Project tio-http-server
 */
public class IpUtils {

    public static String getLocalIp() throws SocketException{
        String localIp = null;
        String netIp = null;

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        boolean finded = false;
        while(networkInterfaces.hasMoreElements()&&!finded){
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> address = networkInterface.getInetAddresses();

            while (address.hasMoreElements()){
                ip = address.nextElement();
                if(!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":")==-1){
                    netIp = ip.getHostAddress();
                    finded = true;
                    break;
                }else if(ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":")==-1){
                    localIp = ip.getHostAddress();
                }
            }
        }
        if(netIp!=null && !"".equals(netIp)){
            return netIp;
        }
        return localIp;
    }

    public static String getRealIp(HttpRequest request){
        HttpConfig httpConfig = request.getHttpConfig();
        if (httpConfig == null) {
            return request.getRemote().getIp();
        }

        if (httpConfig.isProxied()) {
            String ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("proxy-client-ip");
            }
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("wl-proxy-client-ip");
            }
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemote().getIp();
            }

            if (StringUtils.contains(ip, ",")) {
                ip = StringUtils.split(ip, ",")[0].trim();
            }
            return ip;
        } else {
            return request.getRemote().getIp();
        }
    }

    private static boolean isNotIp(String ip){
        return StringUtils.isBlank(ip)||"unknown".equalsIgnoreCase(ip);
    }
}
