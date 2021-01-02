package io.seata.samples.order;


import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.springframework.stereotype.Service;

/**
 * 余额账户 DAO
 */
@Service
public class RedisUtil {
    public static void main(String[] args) {
        // client
        RedisURI redisURI = new RedisURI();
//        redisURI.setHost("10.100.254.144");
        redisURI.setHost("127.0.0.1");
        redisURI.setPort(6379);
//        redisURI.setPassword("hyman");
        RedisClient client = RedisClient.create(redisURI); // "redis://10.100.254.146:6379"
//        client.set

        // connection, 线程安全的长连接，连接丢失时会自动重连，直到调用 close 关闭连接。
        StatefulRedisConnection<String, String> connection = client.connect();

        // sync, 默认超时时间为 60s.
        RedisStringCommands<String, String> sync = connection.sync();
        String k = "host";
        sync.set(k, "note.abeffect.com");
        String value = sync.get(k);
        System.out.println(value);
        System.out.println("value = " + value);

        String getset = sync.getset(k, null);
        System.out.println("getset = " + getset);

        getset = sync.getset(k, null);
        System.out.println("getset = " + getset);

        getset = sync.get(k + ":aaa" );
        System.out.println("aaa = " + getset);

//          getset = sync.getset(k, "1");
//        System.out.println("getset = " + getset);
//
//          getset = sync.getset(k, "2");
//        System.out.println("getset = " + getset);

        sync.set(k, "vvv");

        value = sync.get(k);
        System.out.println(value);

        // close connection
        connection.close();

        // shutdown
        client.shutdown();
    }
}
