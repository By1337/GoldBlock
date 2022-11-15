package org.by1337.ezholo.playerhologram.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.by1337.ezholo.playerhologram.PlayerHologram;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.List;

public class cmdCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;

            if (args.length >= 3) {
                return List.of(
                        "Текст в голограмме!"
                );
            }
            if (args.length == 1) {
                return List.of(
                        "create",
                        "del",
                        "move",
                        "addline",
                        "removeline"
                );

            }
            if (args[0].equals("del")) {
                if (PlayerHologram.playerDatabase.containsKey(String.valueOf(p.getUniqueId()))) {
                    return PlayerHologram.playerDatabase.get(String.valueOf(p.getUniqueId()));
                }
                return List.of(
                        "<name>"
                );

            }
            if (args[0].equals("move") || args[0].equals("addline") || args[0].equals("removeline")) {
                if (PlayerHologram.playerDatabase.containsKey(String.valueOf(p.getUniqueId()))) {
                    return PlayerHologram.playerDatabase.get(String.valueOf(p.getUniqueId()));
                }
                return List.of(
                        "<name>"
                );

            }
            if (args[0].equals("create")) {
                return List.of(
                        "<name>"
                );
            }
        }
        return null;
    }
}
