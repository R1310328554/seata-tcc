package io.seata.samples.order;


import io.lettuce.core.RedisClient;
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
        RedisClient client = RedisClient.create("redis://localhost:63791");

        // connection, 线程安全的长连接，连接丢失时会自动重连，直到调用 close 关闭连接。
        StatefulRedisConnection<String, String> connection = client.connect();

        // sync, 默认超时时间为 60s.
        RedisStringCommands<String, String> sync = connection.sync();
        sync.set("host", "note.abeffect.com");
        String value = sync.get("host");
        System.out.println(value);
        System.out.println("value = " + value);

        // close connection
        connection.close();

        // shutdown
        client.shutdown();
    }
}
