package org.by1337gb.goldblock;

import java.io.File;
import java.util.*;
import java.util.Objects;
import eu.decentsoftware.holograms.api.DHAPI;
import net.milkbowl.vault.economy.Economy;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.concurrent.ThreadLocalRandom;

public final class Goldblock extends JavaPlugin {
    public static Economy econ;
    private final long startInterval = ((long) this.getConfig().getInt("settings.time-start-interval") * 60);
    public long startIntervalChange = startInterval * 20L; // отсчёт до начала в тиках
    private final long duratIonevent = ((long) this.getConfig().getInt("settings.duration-event") * 60);
    public long duratIoneventChange = duratIonevent * 20;
    public long timer = duratIonevent * 20L;
    public final long timeGiveMoney = ((long) this.getConfig().getInt("settings.give-money-timing"));
    private final Material Mat = Material.valueOf(Objects.requireNonNull(this.getConfig().getString("settings.material-block")));
    private static List<String> BlackList = new ArrayList<String>();
    public List<String> PlayerEvent = new ArrayList<String>();
    public List<Long> time;
    public List<String> FormatTime = this.getConfig().getStringList("messages.format-time");

    public Goldblock() {
    }
    @Override
    public void onEnable() {
        time = this.getConfig().getLongList("settings.notification-time");
        BlackList = (List<String>) this.getConfig().getStringList("settings.black-List");
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new SomeExpansion(this).register();
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
                            Location locHolo = new Location(loc.getWorld(), loc.getX() + 0.5, loc.getY() + 2, loc.getZ() + 0.5);
                            List<String> lines = Arrays.asList(Objects.requireNonNull(this.getConfig().getString("messages.holo")), "Line 2");
                            DHAPI.createHologram("gb", locHolo, lines);
                            loc.getBlock().setType(Mat);
                            this.getConfig().set("pos.pos", loc.getBlock().getLocation());
                            this.saveConfig();

                            for (Player play : Bukkit.getOnlinePlayers()) {
                                play.sendMessage(Objects.requireNonNull(this.getConfig().getString("settings.prefix")).replace("&", "§") + " " + Objects.requireNonNull(this.getConfig().getString("messages.gd-spawn")).replace("&", "§") + " " + loc.getBlock().getLocation().getX() + ", " + loc.getBlock().getLocation().getY() + ", " + loc.getBlock().getLocation().getZ());
                                play.sendTitle(Objects.requireNonNull(this.getConfig().getString("titles.gd-name")).replace("&", "§"), Objects.requireNonNull(this.getConfig().getString("titles.gd-start")).replace("&", "§"), 120, 60, 120);
                            }
                        }
                        if(time.contains(startIntervalChange) && this.getConfig().getBoolean("settings.notification")){
                            for (Player play : Bukkit.getOnlinePlayers()) {
                                play.sendMessage(Objects.requireNonNull(this.getConfig().getString("settings.prefix")).replace("&", "§") + " " + Objects.requireNonNull(this.getConfig().getString("messages.notification-msg")).replace("&", "§").replace("<time>", String.format(Format2(startIntervalChange / 20))));
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
                         //   play.sendMessage("Игроки приневшие участие в ивенте " + PlayerEvent);
                            PlayerEvent.clear();
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
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if(p.getLocation().distance(Objects.requireNonNull(location)) < this.getConfig().getDouble("goldblock.radius-give-money")){
                                if(!PlayerEvent.contains(p.getDisplayName())){
                                    PlayerEvent.add(p.getDisplayName());
                                }
                                if(!this.getConfig().getBoolean("settings.divide-between")){
                                    econ.depositPlayer(p, this.getConfig().getDouble("settings.money-give"));
                                    p.sendMessage(" " + Objects.requireNonNull(this.getConfig().getString("messages.give-money")).replace("&", "§").replace("<money>", Objects.requireNonNull(this.getConfig().getString("settings.money-give"))));
                                } else{
                                    double divider = PlInGb() == 0 ? 1.0 : PlInGb();
                                    String msg = String.valueOf(this.getConfig().getDouble("settings.money-give") / divider);
                                    econ.depositPlayer(p, this.getConfig().getDouble("settings.money-give") / divider);
                                    p.sendMessage(" " + Objects.requireNonNull(this.getConfig().getString("messages.give-money")).replace("&", "§").replace("<money>", msg));
                                }
                               }
                        }
                    }
                }
            }, timeGiveMoney * 20L, timeGiveMoney * 20L);

            Bukkit.getScheduler().runTaskTimer(this, () -> {//таймер
                if (!Objects.equals(this.getConfig().getString("pos.pos"), "")){
                    timer -= 20L;
                    long Sec = timer / 20;
                    DHAPI.setHologramLine(Hologram.getCachedHologram("gb"), 1, this.getConfig().getString("messages.time-left")  + " " + Format(Sec));
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
    public double PlInGb(){//возвращает кол-во игроков в зоне зб
        double pl = 0;
        if (Bukkit.getOnlinePlayers().size() > 0) {
            if (!Objects.equals(this.getConfig().getString("pos.pos"), "")) {
                Location location = (Location) this.getConfig().get("pos.pos");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if(p.getLocation().distance(Objects.requireNonNull(location)) < this.getConfig().getDouble("goldblock.radius-give-money")){
                        pl += 1;
                    }
                }
            }
        }
        return pl;
    }
    public Location RndLoc(){
        double x = ThreadLocalRandom.current().nextLong(this.getConfig().getInt("goldblock.radius-min-spawn-block"), this.getConfig().getInt("goldblock.radius-max-spawn-block"));
        double y = 100.0;
        double z = ThreadLocalRandom.current().nextLong(this.getConfig().getInt("goldblock.radius-min-spawn-block"), this.getConfig().getInt("goldblock.radius-max-spawn-block"));
        World world = Bukkit.getWorld((String) Objects.requireNonNull(this.getConfig().get("settings.world")));
        Location loc1 = new Location(world, x, y, z);
        double y2 = Objects.requireNonNull(loc1.getWorld()).getHighestBlockAt(loc1).getLocation().getY();
        Location loc = new Location(world, x, y2 + 1, z);
        if(BlackList.contains(new Location(world, x, y2, z).getBlock().getType().toString()))
            return null;
        else
            return loc;
    }
    public String Format(long Sec){
        int hour = (int) Sec / 3600;
        int min = (int) Sec % 3600 / 60;
        int sec = (int) Sec % 60;
        String fin = "";
        if(hour != 0) {
            if (hour < 5) {
                if (hour == 1)
                    fin += FormatTime.get(0);
                else
                    fin += hour + " " + FormatTime.get(1);
            } else
                fin += hour + FormatTime.get(2);
        }

        if(min != 0){
            if(min < 5){
                if(min == 1)
                    fin += hour == 0 ?  FormatTime.get(3) : " " + FormatTime.get(3);
                else
                    fin += hour == 0 ? min + " " + FormatTime.get(4) : " " + min + " " + FormatTime.get(4);
            }
            else
                fin += min + FormatTime.get(5);
        }

        if(sec != 0){
            if(sec < 5){
                if(sec == 1)
                    fin += min == 0 ? FormatTime.get(6) : " " + FormatTime.get(6);
                else
                    fin += min == 0 ?  sec + " " + FormatTime.get(7) : " " + sec + " " + FormatTime.get(7);
            }
            else
                fin +=  " " + sec + FormatTime.get(8);
        }
        return fin;

    }
    public String Format2(long Sec){
        int hour = (int) Sec / 3600;
        int min = (int) Sec % 3600 / 60;
        int sec = (int) Sec % 60;
        String fin = "";
        if(hour != 0) {
            if (hour < 5) {
                if (hour == 1)
                    fin += FormatTime.get(0);
                else
                    fin += hour + " " + FormatTime.get(1);
            } else
                fin += hour + " " + FormatTime.get(9);
        }

        if(min != 0){
            if(min < 5){
                if(min == 1)
                    fin += hour == 0 ?  FormatTime.get(10) : " " + FormatTime.get(10);
                else
                    fin +=  min + " " + FormatTime.get(4) ;
            }
            else
                fin += min + " " + FormatTime.get(11);
        }

        if(sec != 0){
            if(sec < 5){
                if(sec == 1)
                    fin += min == 0 ?  FormatTime.get(12) : " " +  FormatTime.get(12);
                else
                    fin +=  sec + " " + FormatTime.get(7);
            }
            else
                fin += sec + " " + FormatTime.get(13);
        }
        return fin;

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
