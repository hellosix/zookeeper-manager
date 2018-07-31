package com.lzz.util;

import com.lzz.model.ZKnode;
import net.sf.json.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Curator framework's client test.
 * Output:
 *  $ create /zktest2 hello
 *  create /zktest2/node1 hello1
 *  create /zktest2/node1/nn2 hello1
 *  create /zktest2/node1/nn2/user3 '{"username":"linzhouzhi", "age":1}'
 *  $ ls /
 *  [zktest, zookeeper]
 *  $ get /zktest
 *  hello
 *  $ set /zktest world
 *  $ get /zktest
 *  world
 *  $ delete /zktest
 *  $ ls /
 *  [zookeeper]
 */
public class CuratorClientTest {

    /** Zookeeper info */
    private static final String ZK_ADDRESS = "localhost:2181";
    private static final String ZK_PATH = "/";

    public static void main(String[] args) throws Exception {
        // 1.Connect to zk
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                ZK_ADDRESS,
                new RetryNTimes(10, 5000)
        );
        client.start();
        System.out.println("zk client start successfully!");

        // 2.Client API test
        // 2.1 Create node
        /*
        String data1 = "hello";
        print("create", ZK_PATH, data1);
        client.create().
                creatingParentsIfNeeded().
                forPath(ZK_PATH, data1.getBytes());
        */

        // 2.2 Get node and data
        /*
        print("ls", "/");
        ZKnode zKnode = new ZKnode();
        String path = "/";
        zKnode.setId( path );
        zKnode.setText( path );
        zKnode.setType( "root" );
        getPath( client, zKnode);
        JSONObject jsonObject = JSONObject.fromObject(zKnode);
        System.out.println( jsonObject );
        */


        // 2.3 Modify data
        //String data2 = "world";
        //print("set", ZK_PATH, data2);
        //client.setData().forPath(ZK_PATH, data2.getBytes());
        print("get", ZK_PATH);
        print(client.getData().forPath(ZK_PATH));
        Stat stat = client.checkExists().forPath("/zktest/node1/nn2");
        String d = String.valueOf(client.getData().forPath("/zktest/node1/nn2"));
        byte[] datas = client.getData().forPath("/zktest/node1/nn2");
        System.out.println(new String(datas));
        JSONObject jsonObject = JSONObject.fromObject(stat);
        //System.out.println( jsonObject );
        /*
        // 2.4 Remove node
        print("delete", ZK_PATH);
        client.delete().forPath(ZK_PATH);
        print("ls", "/");
        print(client.getChildren().forPath("/"));
        */
    }

    private static void getPath(CuratorFramework client, ZKnode zKnode) throws Exception {
        String path = zKnode.getId();
        List<String> paths =  client.getChildren().forPath( path );
        for( int i = 0; i < paths.size(); i++ ){
            List<ZKnode> zKnodeChildren = zKnode.getChildren();
            ZKnode temZknode = new ZKnode();
            String childrenPath;
            if( "/".equals( path ) ){
                childrenPath = path + paths.get(i);
            }else{
                childrenPath = path + "/" + paths.get(i);
            }
            temZknode.setId( childrenPath );
            temZknode.setText( childrenPath );
            temZknode.setType("file");
            zKnodeChildren.add( temZknode );
            zKnode.setChildren( zKnodeChildren );
            getPath( client, temZknode);
        }
    }

    private static void print(String... cmds) {
        StringBuilder text = new StringBuilder("$ ");
        for (String cmd : cmds) {
            text.append(cmd).append(" ");
        }
        System.out.println(text.toString());
    }

    private static void print(Object result) {
        System.out.println(
                result instanceof byte[]
                        ? new String((byte[]) result)
                        : result);
    }

}