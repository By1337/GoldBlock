package org.by1337.ezholo.playerhologram;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.by1337.ezholo.playerhologram.commands.cmd;
import org.by1337.ezholo.playerhologram.commands.cmdCompleter;
import org.by1337.ezholo.playerhologram.util.Message;


import java.io.File;
import java.util.*;

public final class PlayerHologram extends JavaPlugin {

    private static PlayerHologram instance;
    public static Economy econ;
    public static HashMap<String, List<String>> playerDatabase = new HashMap<>();
    public static HashMap<String, Integer> playerGroups = new HashMap<>();

    public static void setInstance(PlayerHologram instance) {
        PlayerHologram.instance = instance;
    }

    @Override
    public void onEnable() {
        setInstance(this);
        Load();
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null)
            econ = (Economy) rsp.getProvider();
        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            Message.Logger("Creating new config file, please wait");
            this.getConfig().options().copyDefaults(true);
            this.saveDefaultConfig();
        }
        Objects.requireNonNull(this.getCommand("phologram")).setExecutor(new cmd(this));
        Objects.requireNonNull(this.getCommand("phologram")).setTabCompleter((new cmdCompleter()));
        if(GetInt("auto-save-timings") != -1 && GetInt("auto-save-timings") > 0)//auto save
            Bukkit.getScheduler().runTaskTimer(this, PlayerHologram::Save, GetInt("auto-save-timings") * 20L, GetInt("auto-save-timings") * 20L);
    }


    public static boolean PlayerIsOwner(String UUID, String Holo) {
        if (playerDatabase.containsKey(UUID)) {
            List<String> list;
            list = playerDatabase.get(UUID);
            return list.contains(Holo);
        }else
            return false;
    }

    public static void AddHolo(String UUID, String Holo) {
        if (playerDatabase.containsKey(UUID)) {
            List<String> list;
            list = playerDatabase.get(UUID);
            list.add(Holo);
            playerDatabase.remove(UUID);
            playerDatabase.put(UUID, list);
        }else{
            List<String> list = new ArrayList<>();
            list.add(Holo);
            playerDatabase.put(UUID, list);
        }

    }

    public static void RemovePlayerHolo(String UUID, String Holo) {
        if (playerDatabase.containsKey(UUID)) {
            List<String> list;
            list = playerDatabase.get(UUID);
            list.remove(Holo);
            playerDatabase.remove(UUID);
            playerDatabase.put(UUID, list);
        }else{
            Message.Error("The player is not in the database!");
        }
    }

    public static void Save(){
        instance.getConfig().set("data", null);
        for(String str : playerDatabase.keySet()){
            instance.getConfig().set("data." + str, playerDatabase.get(str));
        }
        instance.saveConfig();
        Message.Logger("PX playerDatabase save &asuccessful!");
    }
    public static void Load(){
        playerDatabase.clear();
        for(String str : instance.getConfig().getConfigurationSection("data").getKeys(false)){
            playerDatabase.put(str, instance.getConfig().getStringList("data." + str));
        }
        playerGroups.clear();
        for(String str : instance.getConfig().getConfigurationSection("groups").getKeys(false)){
            playerGroups.put(str, instance.getConfig().getInt("groups." + str));
        }
    }
    public static String GetString(String path) {
        return instance.getConfig().getString(path);
    }

    public static List<String> GetList(String path) {
        return instance.getConfig().getStringList(path);
    }

    public static int GetInt(String path) {
        return instance.getConfig().getInt(path);
    }

    public static boolean GetBoolean(String path) {
        return instance.getConfig().getBoolean(path);
    }

    @Override
    public void onDisable() {
        Save();
    }


}
