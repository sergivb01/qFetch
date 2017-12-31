package me.sergivb01.serversync;

import lombok.Getter;
import me.sergivb01.serversync.commands.ServerDataCommand;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

public class ServerSync extends JavaPlugin{
    @Getter private static ServerSync instance;
    @Getter private JedisPool pool;
    @Getter private String serverName;

    public void onEnable(){
        instance = this;

        final File configFile = new File(this.getDataFolder() + "/config.yml");
        if (!configFile.exists()) {
            this.saveDefaultConfig();
        }
        this.getConfig().options().copyDefaults(true);

        if(getConfig().getBoolean("redis.auth.enabled")) {
            pool = new JedisPool(new JedisPoolConfig(), getConfig().getString("redis.host"), getConfig().getInt("redis.port"), getConfig().getInt("redis.timeout"), getConfig().getString("redis.auth.password"));
        }else {
            pool = new JedisPool(new JedisPoolConfig(), getConfig().getString("redis.host"), getConfig().getInt("redis.port"), getConfig().getInt("redis.timeout"));
        }

        getCommand("qfetch").setExecutor(new ServerDataCommand());
        serverName = getConfig().getString("server-name");
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, ()-> updateServerData(true), 10L, getConfig().getInt("delay") * 20L);

    }

    public void onDisable(){
        updateServerData(false); //Server is closed now

        pool.destroy();

        instance = null;
    }

    private void updateServerData(boolean up){
        String serverUptime = DurationFormatUtils.formatDurationWords(ManagementFactory.getRuntimeMXBean().getUptime(), true, true);

        Map<String, String> data = new HashMap<>();
        data.put("tps0", String.valueOf(Bukkit.spigot().getTPS()[0]));
        data.put("tps1", String.valueOf(Bukkit.spigot().getTPS()[1]));
        data.put("tps2", String.valueOf(Bukkit.spigot().getTPS()[2]));
        data.put("status", String.valueOf(up));
        data.put("uptime", serverUptime);
        data.put("whitelisted", String.valueOf(Bukkit.hasWhitelist()));
        data.put("online", String.valueOf(Bukkit.getOnlinePlayers().size()));
        data.put("max", String.valueOf(Bukkit.getMaxPlayers()));

        Jedis jedis = null;
        try {
            jedis = instance.getPool().getResource();
            jedis.hmset("serversync:status:" + serverName, data);
            instance.getPool().returnResource(jedis);
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


}
