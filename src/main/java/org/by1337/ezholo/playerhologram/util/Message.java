package org.by1337.ezholo.playerhologram.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Message {

    private static final ConsoleCommandSender SENDER = Bukkit.getConsoleSender();
    private static final String AUTHOR = "By1337";
    private static final String PREFIX = "[PlayerHologram]";

    public static void SendMsg(Player pl, String msg) {
        pl.sendMessage(MessageBuilder(msg));
    }

    public static void Logger(String msg) {
        SENDER.sendMessage(MessageBuilder(msg));
    }

    public static void Error(String msg) {
        SENDER.sendMessage(ChatColor.RED + MessageBuilder(msg));
    }

    public static void Warning(String msg) {
        SENDER.sendMessage(ChatColor.YELLOW + MessageBuilder(msg));
    }

    private static String MessageBuilder(String msg) {
        return msg.replace("&", "§").replace("PX", PREFIX).replace("AU", AUTHOR);
    }


}
