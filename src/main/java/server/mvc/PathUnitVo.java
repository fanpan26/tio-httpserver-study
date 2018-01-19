package server.mvc;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:26
 * @Project tio-http-server
 */
public class PathUnitVo {
    /**
     *
     */
    public PathUnitVo() {
    }

    public PathUnitVo(boolean isVar, String path) {
        super();
        this.isVar = isVar;
        this.path = path;
    }

    public boolean isVar() {
        return isVar;
    }

    public void setVar(boolean isVar) {
        this.isVar = isVar;
    }
    /**
     * 对于/user/{userid}来说，此值是userid
     */
    public String getPath() {
        return path;
    }
    /**
     * 对于/user/{userid}来说，此值是userid
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 是否是变量，true: 是变量
     */
    private boolean isVar = false;

    /**
     * 对于/user/{userid}来说，此值是userid
     */
    private String path = null;

}
