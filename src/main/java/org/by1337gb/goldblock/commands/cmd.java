package org.by1337gb.goldblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.by1337gb.goldblock.Goldblock;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class cmd implements CommandExecutor {
    private final JavaPlugin plugin;

    public cmd(Goldblock plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player p = (Player) sender;
        FileConfiguration config = this.plugin.getConfig();
        String prefix = Objects.requireNonNull(config.getString("settings.prefix")).replace("&", "§");
        if (args.length == 0) {
            p.sendMessage(prefix + " " + Objects.requireNonNull(config.getString("messages.no-message")).replace("&", "§"));
            return true;
        } else if (args[0].equals("reload")) {
            if (!p.hasPermission("gb.reload")) {
                p.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("messages.no-perms")).replace("&", "§"));
                return true;
            } else {
                Bukkit.getPluginManager().disablePlugin(this.plugin);
                this.plugin.reloadConfig();
                Bukkit.getPluginManager().enablePlugin(this.plugin);
                p.sendMessage(prefix + " " + Objects.requireNonNull(config.getString("messages.reload")).replace("&", "§"));
                return true;
            }
        } else if (args[0].equals("tp")) {
            if (!p.hasPermission("gb.tp")) {
                p.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("messages.no-perms")).replace("&", "§"));
                return true;
            } else if (Objects.equals(this.plugin.getConfig().getString("pos.pos"), "")) {
                p.sendMessage(prefix + " " + Objects.requireNonNull(config.getString("messages.no-event")).replace("&", "§"));
                return true;
            } else {
                p.teleport((Location) Objects.requireNonNull(this.plugin.getConfig().get("pos.pos")));
                p.sendMessage(prefix + " " + Objects.requireNonNull(config.getString("messages.tp")).replace("&", "§"));
                return true;
            }
        } else {
            p.sendMessage(prefix + " " + Objects.requireNonNull(config.getString("messages.no-message")).replace("&", "§"));
            return true;
        }
    }
}
