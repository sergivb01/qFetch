package me.sergivb01.serversync.api;

import me.sergivb01.serversync.ServerSync;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class ServerSyncAPI {

    /**
     * Returns whether the requested server exists in database.
     * @param server Server name
     * @return Does the server exist
     */
    public static boolean serverDataExists(String server){
        boolean toReturn;

        Jedis jedis = null;
        try {
            jedis = ServerSync.getInstance().getPool().getResource();
            toReturn = jedis.exists("serversync:status:" + server);
            ServerSync.getInstance().getPool().returnResource(jedis);
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return toReturn;
    }

    /**
     * Returns data from requested server
     * @param server Server name
     * @return Data from requested server
     */
    public static Map<String, String> getServerData(String server){
        Map<String, String> toReturn;

        Jedis jedis = null;
        try {
            jedis = ServerSync.getInstance().getPool().getResource();
            toReturn = jedis.hgetAll("serversync:status:" + server);
            ServerSync.getInstance().getPool().returnResource(jedis);
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return toReturn;
    }


}
