package me.TurtlesAreHot.Duels;

import me.TurtlesAreHot.Duels.commands.ArenaCommand;
import me.TurtlesAreHot.Duels.commands.DuelCommand;
import me.TurtlesAreHot.Duels.events.onBend;
import me.TurtlesAreHot.Duels.events.onDisconnect;
import me.TurtlesAreHot.Duels.events.onKill;
import me.TurtlesAreHot.Duels.events.onMovement;
import me.TurtlesAreHot.Duels.utils.ArenaUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main extends JavaPlugin {

    private File customConfigFile;
    private static FileConfiguration customConfig;
    private static File dataFolder;
    private static List<Duel> duels;
    // target, requester
    private static HashMap<Invite, Long> invites;
    private static HashMap<Arena, Boolean> arenas;
    private static List<UUID> cantMove;
    @Override
    public void onEnable() {
        createCustomConfig();
        dataFolder = getDataFolder();
        duels = new ArrayList<>();
        invites = new HashMap<>();
        arenas = new HashMap<>();
        cantMove = new ArrayList<>();
        this.loadArenas();
        getCommand("arena").setExecutor(new ArenaCommand());
        getCommand("duel").setExecutor(new DuelCommand());
        this.getServer().getPluginManager().registerEvents(new onDisconnect(), this);
        this.getServer().getPluginManager().registerEvents(new onKill(), this);
        this.getServer().getPluginManager().registerEvents(new onMovement(), this);
        this.getServer().getPluginManager().registerEvents(new onBend(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> checkInvites(), 0, 1200);
    }

    @Override
    public void onDisable() {
    }

    public void loadArenas() {
        String[] as = Config.getArenas();
        if(as == null) {
            return;
        }
        for(String arena : as) {
            this.arenas.put(ArenaUtils.getArena(arena.substring(0, arena.indexOf("."))), false);
        }
    }

    public static FileConfiguration getCustomConfig() { return customConfig; }

    public static File getDataDirectory() { return dataFolder; }

    public void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "config.yml");
        if(!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        try {
            PrintWriter pw = new PrintWriter(customConfigFile);
            pw.println("time-limit: 5");
            pw.println("spawn-world: \"spawn\"");
            pw.println("time-before: 5");
            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void msgPlayer(Player p,String message) {
        p.sendMessage(ChatColor.DARK_AQUA + "[Duels] " + ChatColor.AQUA + message);
    }

    public static void noPerms(Player p, String permission) {
        msgPlayer(p, "You require the permission '" + permission + "' in order to use this command.");
    }

    public static List<Duel> getDuels() {
        return duels;
    }

    public static void addDuel(Duel d) {
        duels.add(d);
    }

    public static void removeDuel(Duel d) {
        for(int i = 0; i < duels.size(); i++) {
            if(duels.get(i).getPlayer1().getPlayer().equals(d.getPlayer1().getPlayer())) {
                duels.remove(i);
                break;
            }
        }
    }

    public static Duel getDuel(UUID player) {
        for(Duel d : duels) {
            if (d.getPlayer1().getPlayer().equals(player) || d.getPlayer2().getPlayer().equals(player)) {
                return d;
            }
        }
        return null;
    }
    
    public static Duel getDuelSpectator(UUID spectator) {
        for(Duel d : duels) {
            if(d.getSpectators().contains(spectator)) {
                return d;
            }
        }
        return null;
    }

    public static List<UUID> getDuelPlayers() {
        List<UUID> dps = new ArrayList<>();
        for(Duel d : duels) {
            dps.add(d.getPlayer1().getPlayer());
            dps.add(d.getPlayer2().getPlayer());
            dps.addAll(d.getSpectators());
        }
        return dps;
    }

    public static Invite hasInvite(UUID p) {
        // If this player has an invite it will return the requester
        // If this player doesn't have an invite it will return null.
        for(Map.Entry<Invite, Long> ins : invites.entrySet()) {
            Invite inv = ins.getKey();
            if(inv.getInvited().equals(p)) {
                return inv;
            }
        }
        return null;
    }

    public static void addInvite(Invite inv) {
        // p1 is inviter
        // p2 is invited
        invites.put(inv, System.currentTimeMillis());
    }

    public static void removeInvite(Invite inv) {
        // p2 is the invited player (the player who accepts the 1v1)
        invites.remove(inv);
    }

    public void checkInvites() {
        for(Map.Entry<Invite, Long> ins : invites.entrySet()) {
            if(System.currentTimeMillis() - ins.getValue() >= 120000) {
                // If the time in which the invite was created was 2 minutes or more ago.
                Player invited = Bukkit.getPlayer(ins.getKey().getInvited());
                if(invited != null) {
                    msgPlayer(invited, "The invitation someone sent you expired!");
                }
                invites.remove(ins.getKey());
            }
        }
    }

    public static Arena getRandomArena() {
        List<Arena> availableArenas = new ArrayList<>();
        for(Map.Entry<Arena, Boolean> entry : arenas.entrySet()) {
            if(!entry.getValue()) {
                availableArenas.add(entry.getKey());
            }
        }
        Random rand = new Random();
        return availableArenas.get(rand.nextInt(availableArenas.size()));
    }

    public static boolean isArenaAvailable() {
        for(boolean inUse : arenas.values()) {
            if(!inUse) {
                return true;
            }

        }
        return false;
    }

    public static void addArena(Arena a) {
        arenas.put(a, false);
    }

    public static void addCantMovePlayer(UUID p) {
        cantMove.add(p);
    }

    public static void removeCantMovePlayer(UUID p) {
        cantMove.remove(p);
    }

    public static boolean isCantMovePlayer(Player p) {
        if(cantMove.contains(p.getUniqueId()))
            return true;
        return false;
    }
}
