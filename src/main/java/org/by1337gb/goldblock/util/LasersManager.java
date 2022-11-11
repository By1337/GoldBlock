package org.by1337gb.goldblock.util;


import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.by1337gb.goldblock.Goldblock;



public class LasersManager implements Listener {
    public static List<Laser> laserList = new ArrayList<>();
    private static Goldblock plugin;

    public LasersManager(Goldblock plugin) {
        LasersManager.plugin = plugin;
    }

    public static void createLaser(Location loc, int time) {
        String mode = plugin.getConfig().getString("settings.laser");
        Location toprandomloc = new Location(loc.getWorld(), loc.getX(), (double) Objects.requireNonNull(loc.getWorld()).getMaxHeight(), loc.getZ());
        Location fixedrandomloc = new Location(loc.getWorld(), loc.getX(), loc.getY() - 2.0, loc.getZ());
        String airdropLaser = plugin.getConfig().getString("settings.airdrop-laser");
        int laserViewDistance = plugin.getConfig().getInt("settings.laser-view-distance");
        switch (Objects.requireNonNull(mode)) {
            case "CRYSTAL_LASER":
                try {
                    Laser laser = new Laser.CrystalLaser(toprandomloc, fixedrandomloc, time, laserViewDistance);
                    laser.start(plugin);
                    laserList.add(laser);
                } catch (ReflectiveOperationException var12) {
                    var12.printStackTrace();
                    plugin.getLogger().info(" Error spawning laser, skipping");
                }
                break;
            case "GUARDIAN_LASER":
                try {
                    Laser laser = new Laser.GuardianLaser(toprandomloc, fixedrandomloc, time, laserViewDistance);
                    laser.start(plugin);
                    laserList.add(laser);
                } catch (ReflectiveOperationException var11) {
                    var11.printStackTrace();
                    plugin.getLogger().info( " Error spawning laser, skipping");
                }
            case "NONE":
                break;
            default:
                plugin.getLogger().info( " The Airdrop falling mode " + airdropLaser + " does not exist, change it in the config.");
        }

    }

    public static void removeLaser(Location loc) {

        for (Laser laser : laserList) {
            if (laser.getEnd().equals(new Location(loc.getWorld(), loc.getX(), loc.getY() - 2.0, loc.getZ()))) {
                laser.stop();
                laserList.remove(laser);
                break;
            }
        }

    }
}
