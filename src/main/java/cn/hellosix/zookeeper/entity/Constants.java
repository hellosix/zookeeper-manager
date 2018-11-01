package cn.hellosix.zookeeper.entity;

/**
 * @author: Jay.H.Zou
 * @date: 2018/8/29
 */
public class Constants {
    /**
     * 暂存文件名称前缀
     */
    public static final String TEMP_FILE_PREFIX = "download_";

    public static final String STAT = "stat";

    public static final String DATA = "data";

    public static final String PARENT = "parent";

    public static final String FILE = "file";

    public static final String ELLIPSIS = "ellipsis";

    public static final String TEMP_LEAF = "tempLeaf";

    public static final String LEAF = "leaf";
    /**
     * 暂存文件名称后缀
     */
    public static final String TEMP_FILE_SUFFIX = ".tmp";
    /**
     * 查询参数path前缀
     */
    public static final String QUERY_PARAM_PATH_PREFIX = "/";
    /**
     * 内容布置的类型
     */
    public static final String CONTENT_DISPOSITION_TYPE = "attachment";
    /**
     * 暂存文件超时分钟数（10分钟）
     */
    public static final long TEMP_FILE_EXPIRE_TIME_IN_MINUTES = 10;
    /**
     * 暂存文件超时毫秒数
     */
    public static final long TEMP_FILE_EXPIRE_TIME_IN_MILLISECONDS = TEMP_FILE_EXPIRE_TIME_IN_MINUTES * 60 * 1000L;

    public static final String ZOOKEEPER = "zookeeper";

    public static final String _FILE = "_file";

    public static final String ROOT_PATH = "data/";

    public static final String ZIP = ".zip";

    private Constants() {
    }
}
