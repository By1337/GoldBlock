package org.by1337gb.goldblock.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Message {

    private static final ConsoleCommandSender SENDER = Bukkit.getConsoleSender();
    private static final String AUTHOR = "By1337";

    public static void SendMsg(Player pl, String msg){
    pl.sendMessage(msg.replace("&", "§"));
    UUID uutd = pl.getUniqueId();

    }
    public static void Logger(String msg){
        SENDER.sendMessage(msg.replace("{author}", AUTHOR));
    }
    public static void Error(String msg){
        SENDER.sendMessage(ChatColor.RED + msg);
    }
    public static void Warning(String msg){
        SENDER.sendMessage(ChatColor.YELLOW + msg);
    }
}
