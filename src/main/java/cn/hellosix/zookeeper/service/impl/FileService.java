package cn.hellosix.zookeeper.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author Jay.H.Zou
 * @date 2018/10/15
 */
public class FileService {

    private static final ExecutorService executorService = new ThreadPoolExecutor(
            5,
            5,
            60L,
            TimeUnit.SECONDS, new SynchronousQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("Write File pool-thread-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

}
