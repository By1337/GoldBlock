package org.by1337gb.goldblock;

import java.io.File;
import java.util.*;
import java.util.Objects;
import java.util.TreeSet;
import java.util.Set;
import eu.decentsoftware.holograms.api.DHAPI;
import net.milkbowl.vault.economy.Economy;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public final class Goldblock extends JavaPlugin {
    public static Economy econ;
    public final long startInterval = ((long) this.getConfig().getInt("settings.time-start-interval") * 60);
    public long startIntervalChange = startInterval * 20L; // отсчёт до начала в тиках
    public final long duratIonevent = ((long) this.getConfig().getInt("settings.duration-event") * 60);
    public long duratIoneventChange = duratIonevent * 20;
    public long timer = duratIonevent * 20L;
    public final long timeGiveMoney = ((long) this.getConfig().getInt("settings.give-money-timing"));
    public Material Mat = Material.valueOf(Objects.requireNonNull(this.getConfig().getString("settings.material-block")));
    public static List<String> List = new ArrayList<String>();

    public Goldblock() {
    }
    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new SomeExpansion(this).register();
        List = (List<String>) this.getConfig().getStringList("settings.black-List");

        getServer().getPluginManager().registerEvents(new Handler(this), this);

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        Objects.requireNonNull(this.getCommand("gb")).setExecutor(new cmd_gb(this));

        if (rsp != null) {
            econ = (Economy) rsp.getProvider();
            Bukkit.getScheduler().runTaskTimer(this, () -> {
                if (!Objects.equals(this.getConfig().getString("pos.pos"), "")) {
                    Location location = (Location) this.getConfig().get("pos.pos");
                    assert location != null;
                    if (location.getBlock().getType() != Mat) {
                        location.getBlock().setType(Mat);
                    }
                }
            }, 40L, 40L);

            Bukkit.getScheduler().runTaskTimer(this, () -> { //когда ивент начался

                if (Bukkit.getOnlinePlayers().size() >= this.getConfig().getInt("settings.min-online-players")) {
                    if (Objects.equals(this.getConfig().getString("pos.pos"), "")) {
                        startIntervalChange -= 20L;
                        if(startIntervalChange <= 0L){
                            Location loc = null;
                            while (loc == null)
                                loc = RndLoc();
                           // Location locHolo = new Location(world, x + 0.5, y2 + 3.5, z+ 0.5);
                            Location locHolo = new Location(loc.getWorld(), loc.getX() + 0.5, loc.getY() + 2, loc.getZ() + 0.5);
                            List<String> lines = Arrays.asList(Objects.requireNonNull(this.getConfig().getString("messages.holo")), "Line 2");
                            // createHologram("gb" + x, loc, lines, false);
                            DHAPI.createHologram("gb", locHolo, lines);
                            loc.getBlock().setType(Mat);
                            this.getConfig().set("pos.pos", loc.getBlock().getLocation());
                            this.saveConfig();

                            for (Player play : Bukkit.getOnlinePlayers()) {
                                play.sendMessage(Objects.requireNonNull(this.getConfig().getString("settings.prefix")).replace("&", "§") + " " + Objects.requireNonNull(this.getConfig().getString("messages.gd-spawn")).replace("&", "§") + " " + loc.getBlock().getLocation().getX() + ", " + loc.getBlock().getLocation().getY() + ", " + loc.getBlock().getLocation().getZ());
                                play.sendTitle(Objects.requireNonNull(this.getConfig().getString("titles.gd-name")).replace("&", "§"), Objects.requireNonNull(this.getConfig().getString("titles.gd-start")).replace("&", "§"), 120, 60, 120);
                            }
                        }
                        if(startIntervalChange == this.getConfig().getLong("settings.notification-start-event") * 60 * 20 ){ //cfg notification-start-event
                            for (Player play : Bukkit.getOnlinePlayers()) {
                                play.sendMessage(Objects.requireNonNull(this.getConfig().getString("settings.prefix")).replace("&", "§") + " " + Objects.requireNonNull(this.getConfig().getString("messages.notification-msg")).replace("&", "§").replace("<time>", Objects.requireNonNull(this.getConfig().getString("settings.notification-start-event"))));
                            }
                        }
                        Set<Long> time = new TreeSet<Long>();time.add(1200L);time.add(600L);time.add(200L);time.add(100L);time.add(60L);time.add(40L);time.add(20L);//(1200L, 600L, 200L, 100L, 60L, 40L, 20L);

                        //boolean time.contains(startIntervalChange);

                        if(time.contains(startIntervalChange) && this.getConfig().getBoolean("settings.notification-sec")){ // 1 min anyMatch(Predicate<? super T> predicate)
                            for (Player play : Bukkit.getOnlinePlayers()) {
                                play.sendMessage(Objects.requireNonNull(this.getConfig().getString("settings.prefix")).replace("&", "§") + " " + Objects.requireNonNull(this.getConfig().getString("messages.notification-msg-s")).replace("&", "§").replace("<time>", "" + startIntervalChange / 20));
                            }
                        }
                    } else {
                        startIntervalChange = startInterval * 20L;
                    }
                }
            }, 20L, 20L);

            Bukkit.getScheduler().runTaskTimer(this, () -> { // когда ивент закончился
                if (!Objects.equals(this.getConfig().getString("pos.pos"), "")){
                    duratIoneventChange -= 20L;
                    if(duratIoneventChange <= 0L){
                        Location loc = (Location) this.getConfig().get("pos.pos");
                        assert loc != null;
                        loc.getBlock().setType(Material.AIR);
                        DHAPI.removeHologram("gb");
                        this.getConfig().set("pos.pos", "");
                        this.saveConfig();

                        for (Player play : Bukkit.getOnlinePlayers()) {
                            play.sendMessage(Objects.requireNonNull(this.getConfig().getString("settings.prefix")).replace("&", "§") + " " + Objects.requireNonNull(this.getConfig().getString("messages.gd-end")).replace("&", "§"));
                            play.sendTitle(Objects.requireNonNull(this.getConfig().getString("titles.gd-name")).replace("&", "§"), Objects.requireNonNull(this.getConfig().getString("titles.gd-end")).replace("&", "§"), 120, 60, 120);
                        }
                    }

                } else {
                    duratIoneventChange = duratIonevent * 20;
                }
            }, 20L, 20L);

            Bukkit.getScheduler().runTaskTimer(this, () -> {//Выдача денег игрокам по близости
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    if (!Objects.equals(this.getConfig().getString("pos.pos"), "")) {
                        Location location = (Location) this.getConfig().get("pos.pos");
                        Iterator<? extends Player> var3 = Bukkit.getOnlinePlayers().iterator();
                        Player pl;
                        do {
                            if (!var3.hasNext()) {
                                return;
                            }
                            pl = var3.next();
                           // List<Player> plList = new ArrayList<Player>();

                        } while (!(pl.getLocation().distance(Objects.requireNonNull(location)) < this.getConfig().getDouble("goldblock.radius-give-money")));
                        if (econ != null) {

                            econ.depositPlayer(pl, this.getConfig().getDouble("settings.money-give"));

                            pl.sendMessage(" " + Objects.requireNonNull(this.getConfig().getString("messages.give-money")).replace("&", "§").replace("<money>", Objects.requireNonNull(this.getConfig().getString("settings.money-give"))));
                        }
                    }
                }
            }, timeGiveMoney * 20L, timeGiveMoney * 20L);

            Bukkit.getScheduler().runTaskTimer(this, () -> {//таймер
                if (!Objects.equals(this.getConfig().getString("pos.pos"), "")){
                    timer -= 20L;
                    long Sec = timer / 20;
                    DHAPI.setHologramLine(Hologram.getCachedHologram("gb"), 1, "&aДо коца " + String.format(Objects.requireNonNull(this.getConfig().getString("messages.format-time")), Sec / 3600, Sec % 3600 / 60, Sec % 60));
                } else {
                    timer = duratIonevent * 20L;
                }
            }, 20L, 20L);



            File config = new File(this.getDataFolder() + File.separator + "config.yml");
            if (!config.exists()) {
                this.getLogger().info("Creating new config file, please wait");
                this.getConfig().options().copyDefaults(true);
                this.saveDefaultConfig();
            }

            this.getLogger().info("Plugin enabled " + getDescription().getAuthors().get(0));
        }
    }


    public Location RndLoc(){
        double x = ThreadLocalRandom.current().nextLong(this.getConfig().getInt("goldblock.radius-min-spawn-block"), this.getConfig().getInt("goldblock.radius-max-spawn-block"));
        double y = 100.0;
        double z = ThreadLocalRandom.current().nextLong(this.getConfig().getInt("goldblock.radius-min-spawn-block"), this.getConfig().getInt("goldblock.radius-max-spawn-block"));
        World world = Bukkit.getWorld((String) Objects.requireNonNull(this.getConfig().get("settings.world")));
        Location loc1 = new Location(world, x, y, z);
        double y2 = Objects.requireNonNull(loc1.getWorld()).getHighestBlockAt(loc1).getLocation().getY();
        Location loc = new Location(world, x, y2 + 1, z);

        if(List.contains(new Location(world, x, y2, z).getBlock().getType().toString()))//new Location(world, x, y, z).getBlock().getType()
            return null;
        else
            return loc;

    }
    public String Format(long Sec){
        return String.format(Objects.requireNonNull(this.getConfig().getString("messages.format-time")), Sec / 3600, Sec % 3600 / 60, Sec % 60);
    }
    @Override
    public void onDisable() {
        this.getLogger().info("Plugin disabled");
        if (!Objects.equals(this.getConfig().getString("pos.pos"), "")) {
            Location loc = (Location) this.getConfig().get("pos.pos");
            assert loc != null;
            loc.getBlock().setType(Material.AIR);
            this.getConfig().set("pos.pos", "");
            this.saveConfig();
        }
    }
}
