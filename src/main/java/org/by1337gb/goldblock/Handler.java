package org.by1337gb.goldblock;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public class Handler implements Listener {
     Goldblock plugin;

    public Handler(Goldblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        plugin.getConfig();
        if (!Objects.equals(this.plugin.getConfig().getString("pos.pos"), "")) {
            Location location = (Location)this.plugin.getConfig().get("pos.pos");
            assert location != null;
            if (e.getBlock().getLocation().distance(location) < this.plugin.getConfig().getDouble("goldblock.radius-protect-block")) {
                e.getPlayer().sendMessage(Objects.requireNonNull(this.plugin.getConfig().getString("settings.prefix")).replace("&", "§") + " " + Objects.requireNonNull(this.plugin.getConfig().getString("messages.protect-block")).replace("&", "§"));
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        plugin.getConfig();
        if (!Objects.equals(this.plugin.getConfig().getString("pos.pos"), "")) {
            Location location = (Location)this.plugin.getConfig().get("pos.pos");
            assert location != null;
            if (e.getBlock().getLocation().distance(location) < this.plugin.getConfig().getDouble("goldblock.radius-protect-block")) {
                e.getPlayer().sendMessage(Objects.requireNonNull(this.plugin.getConfig().getString("settings.prefix")).replace("&", "§") + " " + Objects.requireNonNull(this.plugin.getConfig().getString("messages.protect-block")).replace("&", "§"));
                e.setCancelled(true);
            }

        }
    }
    @EventHandler
    public void explode(EntityExplodeEvent e){
        plugin.getConfig();
        if (!Objects.equals(this.plugin.getConfig().getString("pos.pos"), "")) {
            Location location = (Location)this.plugin.getConfig().get("pos.pos");
            assert location != null;
            if (e.getLocation().distance(location) < this.plugin.getConfig().getDouble("goldblock.radius-protect-block")) {
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onChange(EntityChangeBlockEvent e) {
        plugin.getConfig();
        if (!Objects.equals(this.plugin.getConfig().getString("pos.pos"), "")) {
            Location location = (Location)this.plugin.getConfig().get("pos.pos");
            assert location != null;
            if (e.getBlock().getLocation().distance(location) < this.plugin.getConfig().getDouble("goldblock.radius-protect-block")) {
                e.setCancelled(true);
            }

        }
    }
}
