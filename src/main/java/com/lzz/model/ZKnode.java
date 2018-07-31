package com.lzz.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzz on 17/5/8.
 */
public class ZKnode{
    private String id;
    private String text;
    private String type;
    private List<ZKnode> children = new ArrayList<>();

    public ZKnode(){

    }
    public ZKnode(String id, String text, String type) {
        this.id = id;
        this.text = text;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ZKnode> getChildren() {
        return children;
    }

    public void setChildren(List<ZKnode> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "ZKnode{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", type='" + type + '\'' +
                ", children=" + children +
                '}';
    }
}
