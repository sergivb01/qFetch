package me.sergivb01.serversync.commands;

import me.sergivb01.serversync.api.ServerSyncAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class ServerDataCommand implements CommandExecutor{

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.hasPermission("qfetch.command")){
            sender.sendMessage(ChatColor.RED + "No perms.");
            return false;
        }

        if(args.length <= 0){
            sender.sendMessage(ChatColor.RED + "/serverdata <server>");
            return false;
        }

        String server = args[0];
        if(!ServerSyncAPI.serverDataExists(server)){
            sender.sendMessage(ChatColor.RED + "No data found for '" + server + "'");
            return false;
        }

        Map<String, String> data = ServerSyncAPI.getServerData(server);

        if(!data.get("status").equals("true")){
            sender.sendMessage(ChatColor.RED + "I'm sorry but it looks like '" + server + "' is currently closed. Please check back later.");
            return false;
        }

        sender.sendMessage(t("  &7* &6&lOnline: &e" + data.get("online") + "/" + data.get("max")));
        sender.sendMessage(t("  &7* &6&lWhitelist: &e" + (data.get("whitelisted").equals("true") ? "&aEnabled" : "&cDisabled")));
        sender.sendMessage(t("  &7* &6&lUptime: &e" + data.get("uptime")));
        sender.sendMessage(t("  &7* &6&lTPS 0: &e" + data.get("tps0")));
        sender.sendMessage(t("  &7* &6&lTPS 1: &e" + data.get("tps0")));
        sender.sendMessage(t("  &7* &6&lTPS 2: &e" + data.get("tps0")));

        return true;
    }

    private String t(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }


}
