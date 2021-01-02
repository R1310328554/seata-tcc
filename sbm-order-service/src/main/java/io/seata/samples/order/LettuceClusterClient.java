package io.seata.samples.order;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;

import java.util.ArrayList;

public class LettuceClusterClient {
 
    public static void main(String[] args) {  
        ArrayList<RedisURI> list = new ArrayList<>();
        list.add(RedisURI.create("redis://192.168.37.128:7000"));  
        list.add(RedisURI.create("redis://192.168.37.128:7001"));  
        list.add(RedisURI.create("redis://192.168.37.128:7002"));  
        list.add(RedisURI.create("redis://192.168.37.128:7003"));  
        list.add(RedisURI.create("redis://192.168.37.128:7004"));  
        list.add(RedisURI.create("redis://192.168.37.128:7005"));  
        RedisClusterClient client = RedisClusterClient.create(list);
        //RedisClusterClient client = RedisClusterClient.create("redis://192.168.37.128:7000");  
        StatefulRedisClusterConnection<String, String> connect = client.connect();
          
        /* 同步执行的命令 */  
        RedisAdvancedClusterCommands<String, String> commands = connect.sync();
        String str = commands.get("test2");  
        System.out.println(str);  
          
        /*  异步执行的命令  */  
//      RedisAdvancedClusterAsyncCommands<String, String> commands= connect.async();  
//      RedisFuture<String> future = commands.get("test2");  
//      try {  
//          String str = future.get();  
//          System.out.println(str);  
//      } catch (InterruptedException e) {  
//          e.printStackTrace();  
//      } catch (ExecutionException e) {  
//          e.printStackTrace();  
//      }  
          
        connect.close();  
        client.shutdown();  
    }  
} 
