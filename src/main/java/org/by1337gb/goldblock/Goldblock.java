package org.by1337gb.goldblock;

import java.io.File;
import java.util.*;
import java.util.Objects;
import eu.decentsoftware.holograms.api.DHAPI;
import net.milkbowl.vault.economy.Economy;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.by1337gb.goldblock.Listeners.Handler;
import org.by1337gb.goldblock.commands.cmd;
import org.by1337gb.goldblock.commands.cmdCompleter;
import org.by1337gb.goldblock.util.LasersManager;
import org.by1337gb.goldblock.util.Message;
import org.by1337gb.goldblock.util.PlaceholderExpansion;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class Goldblock extends JavaPlugin {
    public static Economy econ;
    private final long startDelay = ((long) this.getConfig().getInt("settings.time-start-interval") * 60);
    public long startIntervalChange = startDelay * 20L; // отсчёт до начала в тиках
    private final long eventDuration = ((long) this.getConfig().getInt("settings.duration-event") * 60);
    public long eventDurationChange = eventDuration * 20;
    public long countdownHolo = eventDuration * 20L;
    public final long countdownReceiveMoney = ((long) this.getConfig().getInt("settings.give-money-timing"));
    private final Material blockMaterial = Material.valueOf(Objects.requireNonNull(GetString("settings.material-block")));
    private static List<String> blackList = new ArrayList<String>();
    public String[][] localPlayerTop = new String[999][3];

    public Location location = null;
    public List<Long> alertTime;
    public List<String> formatTime = this.getConfig().getStringList("messages.format-time");

    public Goldblock() {
    }

    @Override
    public void onEnable() {

        alertTime = this.getConfig().getLongList("settings.notification-time");
        blackList = this.getConfig().getStringList("settings.black-List");

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PlaceholderExpansion(this).register();
        else
            Message.Error("PlaceholderAPI is off!");

        getServer().getPluginManager().registerEvents(new Handler(this), this);
        getServer().getPluginManager().registerEvents(new LasersManager(this), this);
        Objects.requireNonNull(this.getCommand("goldblock")).setExecutor(new cmd(this));
        Objects.requireNonNull(this.getCommand("goldblock")).setTabCompleter((new cmdCompleter()));
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Load();
        if (rsp != null) {
            econ = (Economy) rsp.getProvider();
            Bukkit.getScheduler().runTaskTimer(this, () -> {
                if (!Objects.equals(GetString("pos.pos"), "")) {
                    Location location = (Location) this.getConfig().get("pos.pos");
                    assert location != null;
                    if (location.getBlock().getType() != blockMaterial) {
                        location.getBlock().setType(blockMaterial);
                    }
                }
            }, 40L, 40L);

            Bukkit.getScheduler().runTaskTimer(this, () -> {
                //начало ивента
                if (Bukkit.getOnlinePlayers().size() >= this.getConfig().getInt("settings.min-online-players")) {
                    if (Objects.equals(GetString("pos.pos"), "")) {
                        startIntervalChange -= 20L;
                        if(startIntervalChange <= 0L){
                            if(location == null) {
                                Message.Warning("Заранее сгенерированная локация имеет значение null! Не критичная ошибка.");
                                while (location == null)
                                    location = RndLoc();
                            }
                            Location loc = location;

                            Location locHolo = new Location(loc.getWorld(), loc.getX() + 0.5, loc.getY() + 2, loc.getZ() + 0.5);
                            List<String> lines = Arrays.asList(Objects.requireNonNull(GetString("messages.holo")), "Line 2");
                            DHAPI.createHologram("gb", locHolo, lines);
                            if(this.getConfig().getBoolean("settings.defenders.spawn"))
                                spawnGuards(loc);
                            loc.getBlock().setType(blockMaterial);
                            LasersManager.createLaser(loc, this.getConfig().getInt("settings.laser-time") * 20);
                            this.getConfig().set("pos.pos", loc.getBlock().getLocation());
                            this.saveConfig();

                            for (Player play : Bukkit.getOnlinePlayers()) {
                                Message.SendMsg(play,GetString("settings.prefix") + " " +  GetString("messages.gd-spawn") + " " + loc.getBlock().getLocation().getX() + ", " + loc.getBlock().getLocation().getY() + ", " + loc.getBlock().getLocation().getZ());
                                play.sendTitle(Objects.requireNonNull(GetString("titles.gd-name")), Objects.requireNonNull(GetString("titles.gd-start")), 120, 60, 120);
                            }
                        }
                        if(alertTime.contains(startIntervalChange / 20) && this.getConfig().getBoolean("settings.notification")){
                            for (Player play : Bukkit.getOnlinePlayers()) {
                                Message.SendMsg(play, GetString("settings.prefix") + " " + Objects.requireNonNull(GetString("messages.notification-msg")).replace("<time>", String.format(Format2(startIntervalChange / 20))));
                            }
                        }
                    } else {
                        startIntervalChange = startDelay * 20L;
                    }
                }//начало ивента


                //окончание ивента
                if (!Objects.equals(GetString("pos.pos"), "")){
                    eventDurationChange -= 20L;
                    if(eventDurationChange <= 0L){
                        Location loc = (Location) this.getConfig().get("pos.pos");
                        assert loc != null;
                        loc.getBlock().setType(Material.AIR);
                        location = null;
                        LasersManager.removeLaser(loc);
                        DHAPI.removeHologram("gb");
                        Save();
                        this.getConfig().set("pos.pos", "");
                        this.saveConfig();
                        for (Player play : Bukkit.getOnlinePlayers()) {
                            Message.SendMsg(play, GetString("settings.prefix") + " " + GetString("messages.gd-end"));
                            Message.SendMsg(play, GetString("messages.msg-top"));
                            Message.SendMsg(play, TopCompliter());
                            play.sendTitle(Objects.requireNonNull(GetString("titles.gd-name")), Objects.requireNonNull(GetString("titles.gd-end")), 120, 60, 120);
                        }
                    }
                } else {
                    eventDurationChange = eventDuration * 20;
                }//окончание ивента


                if(location == null)
                    location = RndLoc();
                if (!Objects.equals(GetString("pos.pos"), "")){
                    countdownHolo -= 20L;
                    long Sec = countdownHolo / 20;
                    DHAPI.setHologramLine(Hologram.getCachedHologram("gb"), 1, GetString("messages.time-left")  + " " + Format(Sec));
                } else {
                    countdownHolo = eventDuration * 20L;
                }
            }, 20L, 20L);


            Bukkit.getScheduler().runTaskTimer(this, () -> {//Выдача денег игрокам по близости
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    if (!Objects.equals(GetString("pos.pos"), "")) {
                        Location location = (Location) this.getConfig().get("pos.pos");
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if(p.getWorld() == Bukkit.getWorld((String) Objects.requireNonNull(this.getConfig().get("settings.world")))){
                                if(p.getLocation().distance(Objects.requireNonNull(location)) < this.getConfig().getDouble("goldblock.radius-give-money") && !p.isDead()){
                                    TopAddPlayer(p);
                                    if(!this.getConfig().getBoolean("settings.divide-between")){
                                        econ.depositPlayer(p, this.getConfig().getDouble("settings.money-give"));
                                        Message.SendMsg(p, Objects.requireNonNull(GetString("messages.give-money")).replace("<money>", Objects.requireNonNull(GetString("settings.money-give"))));
                                        TopAddScore(p, this.getConfig().getDouble("settings.money-give"));
                                    } else{
                                        double divider = PlInGb() == 0 ? 1.0 : PlInGb();
                                        double money = this.getConfig().getDouble("settings.money-give") / divider;
                                        money = Math.round(money);
                                        String msg = String.valueOf(money);
                                        econ.depositPlayer(p, money);
                                        TopAddScore(p, money);
                                        Message.SendMsg(p, Objects.requireNonNull(GetString("messages.give-money")).replace("<money>", msg));
                                    }
                                }
                            }
                        }
                    }
                }
            }, countdownReceiveMoney * 20L, countdownReceiveMoney * 20L);

            Bukkit.getScheduler().runTaskTimer(this, () -> {//таймер
                if (!Objects.equals(GetString("pos.pos"), "")){
                    if(this.getConfig().getBoolean("settings.defenders.permanent-spawn")){
                        if(PlInGb() >= (long) this.getConfig().getInt("settings.defenders.spawn-players-near-block"))
                            spawnGuards((Location) Objects.requireNonNull(this.getConfig().get("pos.pos")));
                    }
                }
            }, ((long) this.getConfig().getInt("settings.defenders.time-spawn") * 20), ((long) this.getConfig().getInt("settings.defenders.time-spawn") * 20));
            File config = new File(this.getDataFolder() + File.separator + "config.yml");
            if (!config.exists()) {
                Message.Logger("Creating new config file, please wait");
                this.getConfig().options().copyDefaults(true);
                this.saveDefaultConfig();
            }
            Message.Logger("Plugin enabled author {author}");
        }
    }
    public double PlInGb(){//возвращает кол-во игроков в зоне зб
        double pl = 0;
        if (Bukkit.getOnlinePlayers().size() > 0) {
            if (!Objects.equals(GetString("pos.pos"), "")) {
                Location location = (Location) this.getConfig().get("pos.pos");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if(p.getWorld() == Bukkit.getWorld((String) Objects.requireNonNull(this.getConfig().get("settings.world")))){
                        if (p.getLocation().distance(Objects.requireNonNull(location)) < this.getConfig().getDouble("goldblock.radius-give-money") && !p.isDead()) {
                            pl += 1;
                        }
                    }
                }
            }
        }
        return pl;
    }

    public void spawnGuards(Location loc) {
        String name = GetString("settings.defenders.name");
        EntityType gur = EntityType.valueOf(GetString("settings.defenders.type"));
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        assert world != null;
        world.strikeLightning(loc);
        Location south = new Location(world, (x + ThreadLocalRandom.current().nextInt(0, 3)), (world.getHighestBlockYAt(x + ThreadLocalRandom.current().nextInt(0, 3), z) + ThreadLocalRandom.current().nextInt(0, 3)), z);
        Chunk southChunk = south.getChunk();
        world.loadChunk(southChunk);
        world.spawnEntity(south, gur).setCustomName(name);
    }
    public Location RndLoc(){
        double x = ThreadLocalRandom.current().nextLong(this.getConfig().getInt("goldblock.radius-min-spawn-block"), this.getConfig().getInt("goldblock.radius-max-spawn-block"));
        double y = 100.0;
        double z = ThreadLocalRandom.current().nextLong(this.getConfig().getInt("goldblock.radius-min-spawn-block"), this.getConfig().getInt("goldblock.radius-max-spawn-block"));
        World world = Bukkit.getWorld((String) Objects.requireNonNull(this.getConfig().get("settings.world")));
        Location loc1 = new Location(world, x, y, z);
        double y2 = Objects.requireNonNull(loc1.getWorld()).getHighestBlockAt(loc1).getLocation().getY();
        Location loc = new Location(world, x, y2 + 1, z);
        if(blackList.contains(new Location(world, x, y2, z).getBlock().getType().toString()))
            return null;
        else
            return loc;
    }

    public void SortTop(){
        boolean isSorted = false;
        String[] buff;
        while (!isSorted){
            isSorted = true;
            for(int i = 0; i < localPlayerTop.length-1; i++){
                if(localPlayerTop[i][1] != null && localPlayerTop[i+1][1] != null){
                    int c1 = Integer.parseInt(localPlayerTop[i][1].replace(".0", ""));
                    int c2 = Integer.parseInt(localPlayerTop[i+1][1].replace(".0", ""));
                    if(c1 < c2){
                        isSorted = false;
                        buff = localPlayerTop[i];
                        localPlayerTop[i] = localPlayerTop[i+1];
                        localPlayerTop[i+1] = buff;
                    }
                }
            }
        }
    }
    public void Save(){
        List<String> list = new ArrayList<>();
        for (String[] strings : localPlayerTop) {
            if(strings[0] != null){
                list.add(strings[0]);
                list.add(strings[1]);
                list.add(strings[2]);
            }
        }
        this.getConfig().set("date.top", list);
        this.saveConfig();
    }
    public void Load(){
        List<String> list = getConfig().getStringList("date.top");
        int poz = 0;
        for(int x = 0; x < list.size();x += 3){
            localPlayerTop[poz][0] = list.get(x);
            localPlayerTop[poz][1] = list.get(x + 1);
            localPlayerTop[poz][2] = list.get(x + 2);
            poz++;
        }
    }
    public String TopCompliter(){
        SortTop();
        String top = "";
        for(int x = 0; x < this.getConfig().getInt("settings.top-length"); x++) {
            if (localPlayerTop[x][0] != null) {
                top += Objects.requireNonNull(GetString("messages.player-top")).replace("<position>", "" + (x + 1)).replace("<name>", localPlayerTop[x][0]).replace("<score>", localPlayerTop[x][1]).replace("&", "§") + "\n";
            }
        }
        return top.equals("") ? GetString("messages.empty-top") : top;
    }

    public void TopAddScore(Player pl, double score){
        if(!PlayerInList(pl)){
            for(int x = 0; x < localPlayerTop.length; x++){
                if(Objects.equals(localPlayerTop[x][2], String.valueOf(pl.getUniqueId()))){
                    double old = localPlayerTop[x][1] != null ? Double.parseDouble(localPlayerTop[x][1]) : 0;
                    double scoreNew = old += score;
                    localPlayerTop[x][0] = pl.getDisplayName();
                    localPlayerTop[x][1] = String.valueOf(scoreNew);
                }

            }
        }

    }
    public void TopAddPlayer(Player pl){
        if(PlayerInList(pl)){
            for(int x = 0; x < localPlayerTop.length; x++){
                if(localPlayerTop[x][2] == null) {
                    localPlayerTop[x][2] = String.valueOf(pl.getUniqueId());
                    break;
                }
            }
        }
    }
    public boolean PlayerInList(Player pl) {
        List<String> list = new ArrayList<String>();
        for (String[] strings : localPlayerTop) {
            list.add(strings[2]);
        }
        return !list.contains(String.valueOf(pl.getUniqueId()));
    }
    public String GetString(String path){
        return Objects.requireNonNull(this.getConfig().getString(path)).replace("&", "§");
    }
    public String Format(long Sec){
        int hour = (int) Sec / 3600;
        int min = (int) Sec % 3600 / 60;
        int sec = (int) Sec % 60;
        String fin = "";
        if(hour != 0) {
            if (hour < 5) {
                if (hour == 1)
                    fin += formatTime.get(0);
                else
                    fin += hour + " " + formatTime.get(1);
            } else
                fin += hour + formatTime.get(2);
        }

        if(min != 0){
            if(min < 5){
                if(min == 1)
                    fin += hour == 0 ?  formatTime.get(3) : " " + formatTime.get(3);
                else
                    fin += hour == 0 ? min + " " + formatTime.get(4) : " " + min + " " + formatTime.get(4);
            }
            else
                fin += min + formatTime.get(5);
        }

        if(sec != 0){
            if(sec < 5){
                if(sec == 1)
                    fin += min == 0 ? formatTime.get(6) : " " + formatTime.get(6);
                else
                    fin += min == 0 ?  sec + " " + formatTime.get(7) : " " + sec + " " + formatTime.get(7);
            }
            else
                fin += min == 0 ? sec + formatTime.get(8) : " " + sec + formatTime.get(8);
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
                    fin += formatTime.get(0);
                else
                    fin += hour + " " + formatTime.get(1);
            } else
                fin += hour + " " + formatTime.get(9);
        }

        if(min != 0){
            if(min < 5){
                if(min == 1)
                    fin += hour == 0 ?  formatTime.get(10) : " " + formatTime.get(10);
                else
                    fin +=  min + " " + formatTime.get(4) ;
            }
            else
                fin += min + " " + formatTime.get(11);
        }

        if(sec != 0){
            if(sec < 5){
                if(sec == 1)
                    fin += min == 0 ?  formatTime.get(12) : " " +  formatTime.get(12);
                else
                    fin +=  sec + " " + formatTime.get(7);
            }
            else
                fin += sec + " " + formatTime.get(13);
        }
        return fin;

    }
    @Override
    public void onDisable() {
        Save();
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PlaceholderExpansion(this).unregister();
        if (!Objects.equals(GetString("pos.pos"), "")) {
            Location loc = (Location) this.getConfig().get("pos.pos");
            assert loc != null;
            loc.getBlock().setType(Material.AIR);
            this.getConfig().set("pos.pos", "");
        }
        this.saveConfig();
        Message.Logger("Plugin disabled");
    }
}
