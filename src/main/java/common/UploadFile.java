package common;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/17 13:19
 * @Project tio-http-server
 */
public class UploadFile {
    private String name = null;
    private int size = -1;

    private byte[] data = null;
    /**
     *
     */
    public UploadFile() {

    }

    public byte[] getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
