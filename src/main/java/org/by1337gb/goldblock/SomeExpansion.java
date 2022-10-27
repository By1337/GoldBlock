package org.by1337gb.goldblock;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SomeExpansion extends PlaceholderExpansion {

    private final Goldblock plugin;

    public SomeExpansion(Goldblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "someauthor";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "gb";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("start")){ //%gb_start%
            return plugin.Format(plugin.startIntervalChange / 20);
        }
        if(params.equalsIgnoreCase("starts")){ //%gb_starts%
            return String.valueOf(plugin.startIntervalChange / 20L);
        }
        if(params.equalsIgnoreCase("player")){ //%gb_player%
            return String.valueOf(plugin.PlInGb());
        }
        if(params.equalsIgnoreCase("poz")){ //%gb_poz%
            if (!Objects.equals(this.plugin.getConfig().getString("pos.pos"), "")) {
                Location loc = (Location) this.plugin.getConfig().get("pos.pos");
                assert loc != null;
                return String.valueOf(loc.getBlock().getLocation().getX() + ", " + loc.getBlock().getLocation().getY() + ", " + loc.getBlock().getLocation().getZ());
            }else{
                return "none";
            }

        }
        return null;
    }
}