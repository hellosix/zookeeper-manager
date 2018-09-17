package org.hellosix.zookeeper.utils;

import org.hellosix.zookeeper.entity.ZKParam;
import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.data.Stat;
import org.hellosix.zookeeper.entity.ZKNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzz on 17/5/8.
 */
@Deprecated
public class ZKNodeUtil {
    private CuratorFramework client;

    public static CuratorFramework client(String zk) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                zk,
                new RetryNTimes(10, 5000)
        );
        client.start();
        return client;
    }

    public static JSONObject getDetailPath(String zk, String path) {
        CuratorFramework client = client(zk);
        Stat stat = null;
        JSONObject nodeDetail = new JSONObject();
        try {
            stat = client.checkExists().forPath(path);
            byte[] datas = client.getData().forPath(path);
            nodeDetail.put("stat", stat);
            nodeDetail.put("data", new String(datas));
        } catch (Exception e) {
            nodeDetail.put("data", e.getMessage());
        } finally {
            client.close();
        }
        return nodeDetail;
    }


    public static JSONObject updatePathData(ZKParam zKParam) throws Exception {
        JSONObject nodeDetail = new JSONObject();
        CuratorFramework client = client(zKParam.getZkAddress());
        try {
            Stat stat = client.checkExists().forPath(zKParam.getZkPath());
            if (null == stat) {
                client.create().creatingParentsIfNeeded().forPath(zKParam.getZkPath(), zKParam.getData().getBytes());
            } else {
                client.setData().forPath(zKParam.getZkPath(), zKParam.getData().getBytes());
            }
            Stat resStat = client.checkExists().forPath(zKParam.getZkPath());
            byte[] datas = client.getData().forPath(zKParam.getZkPath());
            nodeDetail.put("stat", resStat);
            nodeDetail.put("data", new String(datas));
        } catch (Exception e) {

        } finally {
            client.close();
        }
        return nodeDetail;
    }


    public static boolean deletePath(ZKParam zkParam) {
        CuratorFramework client = client(zkParam.getZkAddress());
        boolean res = true;
        try {
            client.delete().deletingChildrenIfNeeded().forPath(zkParam.getZkPath());
        } catch (Exception e) {
            res = false;
        } finally {
            client.close();
        }
        return res;
    }

    public static JSONObject getAllPath(String zk, String path) {
        CuratorFramework client = client(zk);
        ZKNode zKnode = new ZKNode();
        zKnode.setId(path);
        zKnode.setText(path);
        zKnode.setType("parent");
        try {
            getPath(client, zKnode, false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(zKnode));
        return jsonObject;
    }

    private static void getPath(CuratorFramework client, ZKNode zKnode, Boolean forceAll) throws Exception {
        String path = zKnode.getId();
        zKnode.setType("parent");
        List<String> childrenNames = client.getChildren().forPath(path);
        for (int i = 0; i < childrenNames.size(); i++) {
            String childName = childrenNames.get(i);
            ZKNode childNode = new ZKNode();
            childNode.setText(childName);
            String childPath = "";
            if ("/".equals(path)) {
                childPath = path + childName;
            } else {
                childPath = path + "/" + childName;
            }
            childNode.setId(childPath);
            List<ZKNode> zkChildren = zKnode.getChildren();
            if (forceAll) {
                childNode.setText(childName);
                childNode.setType("file");
                zkChildren.add(childNode);
                getPath(client, childNode, true);
            } else {
                Stat stat = client.checkExists().forPath(childPath);
                if (stat.getNumChildren() > 0) {
                    childNode.setType("parent");
                    List<ZKNode> grandson = new ArrayList<>();
                    grandson.add(new ZKNode(childPath + "/...", "...", "ellipsis"));
                    childNode.setChildren(grandson);
                    zkChildren.add(childNode);
                } else {
                    if (stat.getEphemeralOwner() != 0) {
                        childNode.setType("tmp-leaf");
                    } else {
                        childNode.setType("leaf");
                    }
                    zkChildren.add(childNode);
                }
                if (i > 50) {
                    zkChildren.add(new ZKNode(path + "/...", "...", "ellipsis"));
                    break;
                }
            }
        }
    }


}
