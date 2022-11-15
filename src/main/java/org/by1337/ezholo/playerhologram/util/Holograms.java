package org.by1337.ezholo.playerhologram.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.by1337.ezholo.playerhologram.PlayerHologram;

import java.util.Collections;
import java.util.List;


public class Holograms {

    public void CreateHolograms(Player pl, String pholoName, String[] content) {
        String[] blackSymbol = {" ", "|", "\\", "/", "-", "_", "+", "=", "~", "^", ">", "<", "`"};
        for (String s : blackSymbol) pholoName = pholoName.replace(s, "");
        Hologram hologram;
        try {
             hologram = DHAPI.getHologram(pholoName);
        }catch (Exception e){
            Message.Error("Ошибка с API DecentHolograms\n" + e);
            return;
        }
        Location loc = pl.getLocation();
        loc.setY(loc.getY() + 1);
        String msg = "";
        int maxHolo = 0;
        for (String str : PlayerHologram.playerGroups.keySet()) {
            if (pl.hasPermission("pholo.groups." + str)) {
                maxHolo = maxHolo < PlayerHologram.playerGroups.get(str) ? PlayerHologram.playerGroups.get(str) : maxHolo;
            }
        }
        if (PlayerHologram.playerDatabase.get(String.valueOf(pl.getUniqueId())) != null)
            if (PlayerHologram.playerDatabase.get(String.valueOf(pl.getUniqueId())).size() > maxHolo) {
                Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-max-holo"));
                return;
            }

        for (int x = 2; x < content.length; x++) {
            msg += msg.equals("") ? "" : " ";
            msg += content[x];
        }
        if (IsBadWord(msg)) {
            Message.SendMsg(pl, PlayerHologram.GetString("msg.bad-word"));
            return;
        }
        if (hologram != null) {
            Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-name-error"));
            return;
        }

        if (PlayerHologram.GetInt("max-string-length") <= msg.length()) {
            Message.SendMsg(pl, PlayerHologram.GetString("msg.max-string-length-error"));
            return;
        }

        if (PlayerHologram.GetBoolean("using-worldguard") && IsRegion(pl)) {
            Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-worldguard"));
            return;
        }

        double price = msg.length() * PlayerHologram.GetInt("price-per-symbol");
        if (PlayerHologram.econ != null) {
            if (PlayerHologram.econ.has(pl, price)) {
                PlayerHologram.econ.withdrawPlayer(pl, price);
                Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-paid").replace("{paid}", "" + price));
            } else {
                Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-paid-error").replace("{price}", "" + price));
                return;
            }
        }

        DHAPI.createHologram(pholoName, loc, true, Collections.singletonList(msg));
        PlayerHologram.AddHolo(String.valueOf(pl.getUniqueId()), pholoName);
    }

    public void RemoveHolograms(Player pl, String name) {
        if (PlayerHologram.PlayerIsOwner(String.valueOf(pl.getUniqueId()), name)) {
            Hologram hologram = DHAPI.getHologram(name);
            if (hologram != null) {
             //   hologram.destroy();
                DHAPI.removeHologram(name);
                PlayerHologram.RemovePlayerHolo(String.valueOf(pl.getUniqueId()), name);
                Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-remove"));
            } else {
                Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-not-found"));
            }
        } else
            Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-player-not-owner"));

    }

    public void MoveHolograms(Player pl, String name) {
        if (PlayerHologram.PlayerIsOwner(String.valueOf(pl.getUniqueId()), name)) {

            if (PlayerHologram.GetBoolean("using-worldguard") && IsRegion(pl)) {
                Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-worldguard"));
                return;
            }

            Hologram hologram = DHAPI.getHologram(name);
            if (hologram != null) {
                Location loc = pl.getLocation();
                loc.setY(loc.getY() + 1);
                DHAPI.moveHologram(hologram, loc);
                Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-move"));
            } else {
                Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-not-found"));
            }
        } else
            Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-player-not-owner"));
    }

    public void AddLine(Player pl, String name, String[] content) {
        String msg = "";
        for (int x = 2; x < content.length; x++) {
            msg += msg.equals("") ? "" : " ";
            msg += content[x];
        }
        if (PlayerHologram.GetInt("max-string-length") <= msg.length()) {
            Message.SendMsg(pl, PlayerHologram.GetString("msg.max-string-length-error"));
            return;
        }
        if (IsBadWord(msg)) {
            Message.SendMsg(pl, PlayerHologram.GetString("bad-word"));
            return;
        }

        if (PlayerHologram.PlayerIsOwner(String.valueOf(pl.getUniqueId()), name)) {
            Hologram hologram = DHAPI.getHologram(name);
            if (hologram != null) {

                HologramLine line = DHAPI.getHologramLine(hologram.getPage(0), PlayerHologram.GetInt("max-lines") - 1);
                if (line != null) {
                    Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-addline-error"));
                    return;
                }
                DHAPI.addHologramLine(hologram, msg);

                Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-addline"));
            } else {
                Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-not-found"));
            }
        } else
            Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-player-not-owner"));
    }

    public void RemoveLine(Player pl, String name, int index) {
        if (PlayerHologram.PlayerIsOwner(String.valueOf(pl.getUniqueId()), name)) {
            Hologram hologram = DHAPI.getHologram(name);
            if (hologram != null) {

                HologramLine line = DHAPI.getHologramLine(hologram.getPage(0), index - 1);
                if (line == null) {
                    Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-line-not-found"));
                    return;
                }
                DHAPI.removeHologramLine(hologram, index - 1);

                Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-line-remove"));
            } else {
                Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-not-found"));
            }
        } else
            Message.SendMsg(pl, PlayerHologram.GetString("msg.pholo-player-not-owner"));
    }

    public boolean IsBadWord(String msg) {
        List<String> blackList = PlayerHologram.GetList("black-list");
        List<String> whiteList = PlayerHologram.GetList("white-list");
        msg = msg.toLowerCase();
        for (String s : whiteList) msg = msg.replace(s, "");
        String msg1 = msg;
        String[] translit = {"й", "ц", "у", "к", "е", "н", "г", "ш", "щ", "з", "ф", "ы", "в", "а", "п", "р", "о", "л", "д", "я", "ч", "с", "м", "и", "т", "б", "ю", "х", "з", "э"};
        String[] translit2 = {"y", "ts", "u", "k", "ye", "n", "g", "sh", "shch", "z", "f", "y", "v", "a", "p", "r", "o", "l", "d", "ya", "ch", "s", "m", "i", "t", "b", "yu", "kh", "z", "e"};
        String[] duplicates = {"p", "b", "a", "o", "c", "x", "y", "k", "m", "e", "t", "g", "ё"};
        String[] duplicates2 = {"р", "в", "а", "о", "с", "х", "у", "к", "м", "е", "т", "г", "е"};
        String[] duplicates3 = {" ", "|", "\\", "/", "-", "_", "+", "=", "~", "^", ">", "<", "`", "&q", "&w", "&e", "&r", "&t", "&y", "&u", "&i", "&o", "&p", "&a", "&s", "&d", "&f", "&g", "&h", "&j", "&k", "&l", "&z", "&x", "&c", "&v", "&b", "&n", "&m", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&0"};

        for (String s : duplicates3) msg1 = msg1.replace(s, "");

        for (int i = 0; i < translit.length; i++)
            msg1 = msg1.replace(translit2[i], translit[i]);

        for (int x = 0; x < duplicates.length; x++)
            msg = msg.replace(duplicates[x], duplicates2[x]);

        for (String str : blackList) {
            if (msg.contains(str))
                return true;
            if (msg1.contains(str))
                return true;

        }
        return false;
    }

    public boolean IsRegion(Player player) {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            Message.Error("Not found WorldGuard! WorldGuard check is off!");
            return false;
        }
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(player.getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);
        if (set.getRegions().isEmpty()) {
            return false;
        }
        for (ProtectedRegion region : set) {
            DefaultDomain members = region.getMembers();
            members.addAll(region.getOwners());
            if (members.contains(player.getUniqueId())) {
                return false;
            }
        }
        return true;
    }

}
