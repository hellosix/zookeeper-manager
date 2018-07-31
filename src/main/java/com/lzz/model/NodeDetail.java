package com.lzz.model;

import org.apache.zookeeper.data.Stat;

/**
 * Created by lzz on 2018/3/13.
 */
public class NodeDetail {
    private Stat stat;
    private String data;

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
