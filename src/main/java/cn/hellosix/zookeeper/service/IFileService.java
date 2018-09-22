package cn.hellosix.zookeeper.service;

import cn.hellosix.zookeeper.entity.FileDTO;
import cn.hellosix.zookeeper.entity.ZKParam;

/**
 * 下载、上传文件
 *
 * @author Jay.H.Zou, Zenuo
 * @date 2018/8/26
 */
public interface IFileService {

    /**
     * 根据指定的path下载对应的node数据
     *
     * @param param     参数实例
     * @param recursive 是否递归下载子节点
     * @return 文件数据传输对象实例
     * @throws Exception 异常
     */
    FileDTO download(ZKParam param, Boolean recursive) throws Exception;
}
