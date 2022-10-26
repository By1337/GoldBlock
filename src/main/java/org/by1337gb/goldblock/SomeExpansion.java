package org.by1337gb.goldblock;

//import at.helpch.placeholderapi.example.SomePlugin;
//import at.helpch.placeholderapi.example.Goldblock;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

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
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("start")){ //%gb_start%
            //long x = plugin.startIntervalChange / 20L;
            //plugin.Format(plugin.startIntervalChange * 50)
            return plugin.Format(plugin.startIntervalChange / 20);
           // return plugin.getConfig().getString("placeholders.placeholder1", "default1");
        }

        return null; // Placeholder is unknown by the Expansion
    }
}