package cn.hellosix.zookeeper.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzz on 17/5/8.
 */
public class ZKNode {
    private String id;
    private String text;
    private String type;
    private List<ZKNode> children = new ArrayList<>();

    public ZKNode(){

    }
    public ZKNode(String id, String text, String type) {
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

    public void markAsParent() {
        this.type = "parent";
    }

    public List<ZKNode> getChildren() {
        return children;
    }

    public void setChildren(List<ZKNode> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "ZKNode{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", type='" + type + '\'' +
                ", children=" + children +
                '}';
    }
}
