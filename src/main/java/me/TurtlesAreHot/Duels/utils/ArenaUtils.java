package me.TurtlesAreHot.Duels.utils;

import me.TurtlesAreHot.Duels.Arena;
import me.TurtlesAreHot.Duels.Main;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ArenaUtils {

    private static Location pos1 = null;
    private static Location pos2 = null;

    public static Arena createArena(String name) {
        if(getPos1() == null || getPos2() == null) {

            return null;
        }
        return new Arena(getPos1(), getPos2(), name, null);
    }

    public static void setPos1(Location pos) { pos1 = pos; }

    public static void setPos2(Location pos) { pos2 = pos; }

    private static Location getPos1() { return pos1; }

    private static Location getPos2() { return pos2; }

    public static Arena getArena(String name) {
        File arenaConfigFile = new File(Main.getDataDirectory(), "/arenas/" + name + ".yml");
        FileConfiguration arenaConfig = new YamlConfiguration();
        try {
            arenaConfig.load(arenaConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return new Arena(arenaConfig.getLocation("player1-location"),
                arenaConfig.getLocation("player2-location"), arenaConfig.getString("name"), arenaConfig.getStringList("commands-after"));
    }

}

