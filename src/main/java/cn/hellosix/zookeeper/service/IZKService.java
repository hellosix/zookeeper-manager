package cn.hellosix.zookeeper.service;

import cn.hellosix.zookeeper.entity.ZKParam;
import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;

/**
 * @author: Jay.H.Zou
 * @date: 2018/8/26
 */
public interface IZKService {

    /**
     * Get zookeeper client
     * @param zkAddress
     * @return
     */
    CuratorFramework getClient(String zkAddress);

    /**
     * Get all path
     * @param param
     * @return
     */
    JSONObject getAllPath(ZKParam param);

    /**
     * Get data for path
     * @param param
     * @return
     */
    String getData(ZKParam param);

    /**
     * Get detail info for path
     * @param param
     * @return
     */
    JSONObject getDetail(ZKParam param);

    /**
     * Update node data
     * @param param
     * @return
     */
    JSONObject updateNodeData(ZKParam param);

    /**
     * Remove a node
     * @param param
     * @return
     */
    boolean removeNode(ZKParam param);

}
