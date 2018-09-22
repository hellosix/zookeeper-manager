package cn.hellosix.zookeeper.entity;

import lombok.Builder;
import lombok.Getter;

import java.nio.file.Path;

/**
 * 文件数据传输对象
 *
 * @author Zenuo
 * @date 2018/09/22
 */
@Builder
@Getter
public final class FileDTO {
    private Path path;
    
}
