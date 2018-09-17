package cn.hellosix.zookeeper.entity;

/**
 * @author: George.Z.Lin, Jay.H.Zou
 * @date: 2018/8/29
 */
public class ZKParam {
    private String zkAddress;
    private String zkPath;
    private String data;

    public ZKParam() {}

    public ZKParam(String zkAddress, String zkPath, String data) {
        this.zkAddress = zkAddress;
        this.zkPath = zkPath;
        this.data = data;
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public String getZkPath() {
        return zkPath;
    }

    public void setZkPath(String zkPath) {
        this.zkPath = zkPath;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ZKParam{" +
                "zkAddress='" + zkAddress + '\'' +
                ", zkPath='" + zkPath + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
