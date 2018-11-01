package cn.hellosix.zookeeper.service;

import cn.hellosix.zookeeper.entity.ZKParam;

/**
 * @author Jay.H.Zou
 * @date 2018/10/15
 */
public interface IFileService {

    /**
     *  download zip
     * @param nodePath zk node path
     * @return file name, eg: conf.zip
     */
    String download(ZKParam param);
}
