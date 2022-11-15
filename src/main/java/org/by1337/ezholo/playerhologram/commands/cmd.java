package org.by1337.ezholo.playerhologram.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.by1337.ezholo.playerhologram.PlayerHologram;
import org.by1337.ezholo.playerhologram.util.Holograms;
import org.by1337.ezholo.playerhologram.util.Message;
import org.jetbrains.annotations.NotNull;


import java.util.Objects;

public class cmd implements CommandExecutor {
    private final Holograms Holograms = new Holograms();
    private final PlayerHologram plugin;

    public cmd(PlayerHologram plugin) {
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
       Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
            if (args.length == 0) {
                Message.SendMsg(p, PlayerHologram.GetString("msg.unknown-command"));
                return true;
            }
            if (args[0].equals("create")) {
                if (p.hasPermission("pholo.create")) {
                    if (args.length >= 3) {
                        Holograms.CreateHolograms(p, String.valueOf(args[1]), args);
                        return true;
                    } else
                        Message.SendMsg(p, PlayerHologram.GetString("msg.create-warning"));
                    return true;
                } else
                    Message.SendMsg(p, PlayerHologram.GetString("msg.no-prem"));
                return true;
            }

            if (args[0].equals("del")) {
                if (p.hasPermission("pholo.delete")) {
                    if (args.length == 2) {
                        Holograms.RemoveHolograms(p, String.valueOf(args[1]));
                        return true;
                    } else
                        Message.SendMsg(p, PlayerHologram.GetString("msg.delete-warning"));
                    return true;
                } else
                    Message.SendMsg(p, PlayerHologram.GetString("msg.no-prem"));
                return true;
            }

            if (args[0].equals("move")) {
                if (p.hasPermission("pholo.move")) {
                    if (args.length == 2) {
                        Holograms.MoveHolograms(p, String.valueOf(args[1]));
                        return true;
                    } else
                        Message.SendMsg(p, PlayerHologram.GetString("msg.move-warning"));
                    return true;
                } else
                    Message.SendMsg(p, PlayerHologram.GetString("msg.no-prem"));
                return true;
            }

            if (args[0].equals("addline")) {
                if (p.hasPermission("pholo.addline")) {
                    if (args.length >= 3) {
                        Holograms.AddLine(p, String.valueOf(args[1]), args);
                        return true;
                    } else
                        Message.SendMsg(p, PlayerHologram.GetString("msg.addline-warning"));
                    return true;
                } else
                    Message.SendMsg(p, PlayerHologram.GetString("msg.no-prem"));
                return true;
            }

            if (args[0].equals("removeline")) {
                if (p.hasPermission("pholo.removeLine")) {
                    if (args.length >= 3) {
                        Holograms.RemoveLine(p, String.valueOf(args[1]), Integer.parseInt(args[2]));
                        return true;
                    } else
                        Message.SendMsg(p, PlayerHologram.GetString("msg.removeLine-warning"));
                    return true;
                } else
                    Message.SendMsg(p, PlayerHologram.GetString("msg.no-prem"));
                return true;
            }

            if (args[0].equals("reload")) {
                if (p.hasPermission("pholo.reload")) {
                    plugin.reloadConfig();
                    PlayerHologram.Save();
                    PlayerHologram.Load();
                    Message.SendMsg(p, "&aКонфиг перезагружен!");
                    return true;
                } else
                    Message.SendMsg(p, PlayerHologram.GetString("msg.no-prem"));
                return true;
            }

        } else {
            Message.Warning(PlayerHologram.GetString("msg.only-player"));
        }
        return true;
    }
}
