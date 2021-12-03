package me.TurtlesAreHot.Duels;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Config {

    public static int getTimer() { return Main.getCustomConfig().getInt("time-limit"); }

    public static String getSpawnWorld() { return Main.getCustomConfig().getString("spawn-world"); }

    public static int getTimeBefore() { return Main.getCustomConfig().getInt("time-before"); }

    public static String[] getArenas() {
        File f = new File(Main.getDataDirectory(), "/arenas/");
        if(!f.exists()) {
            return null;
        }
        return f.list();
    }

    public static Arena getArena(String name) {
        File arenaConfigFile = new File(Main.getDataDirectory(), "/arenas/" + name + ".yml");
        if(!arenaConfigFile.exists()) {
            return null;
        }
        FileConfiguration arenaConfig = new YamlConfiguration();
        try {
            arenaConfig.load(arenaConfigFile);
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return new Arena(arenaConfig.getLocation("player1-location"),
                arenaConfig.getLocation("player2-location"), arenaConfig.getString("name"), arenaConfig.getStringList("commands-after"));
    }



}