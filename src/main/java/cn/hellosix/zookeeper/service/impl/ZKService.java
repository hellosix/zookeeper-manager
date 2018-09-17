package cn.hellosix.zookeeper.service.impl;

import cn.hellosix.zookeeper.entity.ZKNode;
import cn.hellosix.zookeeper.service.IZKService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.data.Stat;
import cn.hellosix.zookeeper.entity.Constants;
import cn.hellosix.zookeeper.entity.ZKParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: George.Z.Lin, Jay.H.Zou
 * @date: 2018/8/26
 */
@Service
public class ZKService implements IZKService {

    @Override
    public CuratorFramework getClient(String zkAddress) {
        if (StringUtils.isBlank(zkAddress)) {
            return null;
        }
        // TODO: 策略，需要研究一下
        RetryPolicy retryPolicy = new RetryNTimes(10, 5000);
        // TODO: 参数写配置文件
        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString(zkAddress)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();

        client.start();
        return client;
    }

    public JSONObject getAllPath(ZKParam param) {
        String path = param.getZkPath();
        CuratorFramework client = getClient(param.getZkAddress());
        if (client == null) {
            return new JSONObject();
        }
        ZKNode node = new ZKNode();
        node.setId(path);
        node.setText(path);
        node.setType(Constants.PARENT);
        try {
            getPath(client, node, false);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        } finally {
            closeClient(client);
        }
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(node));
        return jsonObject;
    }

    /**
     *
     * @param client
     * @param node
     * @param forceAll
     * @throws Exception
     */
    private void getPath(CuratorFramework client, ZKNode node, boolean forceAll) throws Exception {
        String path = node.getId();
        node.setType(Constants.PARENT);
        List<String> childrenNames = client.getChildren().forPath(path);
        for (int i = 0, length = childrenNames.size(); i < length; i++) {
            String childName = childrenNames.get(i);
            ZKNode childNode = new ZKNode();
            childNode.setText(childName);
            String childPath = "";
            if ("/".equals(path)) {
                // /zookeeper
                childPath = path + childName;
            } else {
                // /zookeeper/temp
                childPath = path + "/" + childName;
            }
            childNode.setId(childPath);
            List<ZKNode> childrenNodeList = node.getChildren();
            if(forceAll) {
                childNode.setText(childName);
                childNode.setType(Constants.FILE);
                childrenNodeList.add(childNode);
                getPath(client, childNode, true);
            } else {
                Stat stat = client.checkExists().forPath(childPath);
                if (stat.getNumChildren() > 0) {
                    childNode.setType(Constants.PARENT);
                    List<ZKNode> grandsonList = new ArrayList<>();
                    grandsonList.add(new ZKNode(childPath + "/...", "...", Constants.ELLIPSIS));
                    childNode.setChildren(grandsonList);
                    childrenNodeList.add(childNode);
                } else {
                    // 验证判断是否是临时节点
                    String type = stat.getEphemeralOwner() != 0 ? Constants.TEMP_LEAF : Constants.LEAF;
                    childNode.setType(type);
                    childrenNodeList.add(childNode);
                }
                if (i > 50) {
                    childrenNodeList.add(new ZKNode(path + "/...", "...", Constants.ELLIPSIS));
                    break;
                }
            }
        }
    }


    @Override
    public String getData(ZKParam param) {
        CuratorFramework client = getClient(param.getZkAddress());
        if (client == null) {
            return null;
        }
        String data = new String();
        try {
            byte[] bytes = client.getData().forPath(param.getZkPath());
            if (bytes != null) {
                data = new String(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeClient(client);
        }
        return data;
    }

    @Override
    public JSONObject getDetail(ZKParam param) {
        JSONObject jsonObject = new JSONObject();
        CuratorFramework client = getClient(param.getZkAddress());
        if (client == null) {
            return jsonObject;
        }
        try {
            String zkPath = param.getZkPath();
            Stat stat = client.checkExists().forPath(zkPath);
            byte[] bytes = client.getData().forPath(zkPath);
            jsonObject.put(Constants.STAT, stat);
            jsonObject.put(Constants.DATA, new String(bytes));
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put(Constants.DATA, e.getMessage());
        } finally {
            closeClient(client);
        }
        return jsonObject;
    }

    @Override
    public JSONObject updateNodeData(ZKParam param) {
        JSONObject jsonObject = new JSONObject();
        String path = param.getZkPath();
        String data = param.getData();
        CuratorFramework client = getClient(param.getZkAddress());
        if (client == null) {
            return jsonObject;
        }

        if(StringUtils.isBlank(data)) {
            data = "";
        }
        try {
            Stat stat = client.checkExists().forPath(path);
            if (stat != null) {
                client.setData().forPath(path, data.getBytes());
            } else {
                client.create().creatingParentsIfNeeded().forPath(path, data.getBytes());
            }
            Stat resultStat = client.checkExists().forPath(path);
            byte[] resultData = client.getData().forPath(path);
            jsonObject.put(Constants.STAT, resultStat);
            jsonObject.put(Constants.DATA, new String(resultData));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeClient(client);
        }
        return jsonObject;
    }

    @Override
    public boolean removeNode(ZKParam param) {
        CuratorFramework client = getClient(param.getZkAddress());
        if (client == null) {
            return false;
        }
        boolean isSuccess = false;
        try {
            client.delete().deletingChildrenIfNeeded().forPath(param.getZkPath());
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeClient(client);
        }
        return isSuccess;
    }

    private void closeClient(CuratorFramework client) {
        if (client != null) {
            client.close();
        }
    }
}


