package me.TurtlesAreHot.Duels;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Arena {
    private Location pos1;
    private Location pos2;
    private String name;
    private List<String> commandsAfter;

    public Arena(Location pos1, Location pos2, String name, List<String> commandsAfter) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.name = name;
        this.commandsAfter = commandsAfter;
    }

    public Location getPos1() { return this.pos1; }

    public Location getPos2() { return this.pos2; }

    public List<String> getCommandsAfter() { return this.commandsAfter; }

    public boolean exists() {
        File arenaConfigFile = new File(Main.getDataDirectory(), "/arenas/" + name + ".yml");
        return arenaConfigFile.exists();
    }

    public boolean createArena() {
        File arenaConfigFile = new File(Main.getDataDirectory(), "/arenas/" + name + ".yml");
        File arenaFolder = new File(Main.getDataDirectory(), "/arenas/");
        if(!arenaFolder.exists()) {
            arenaFolder.mkdirs();
        }
        if(arenaConfigFile.exists()) {
            // An arena with this name already exists, so we return false.
            return false;
        }
        try {
            arenaConfigFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileConfiguration arenaConfig = new YamlConfiguration();
        try {
            arenaConfig.load(arenaConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        List<String> def = new ArrayList<>();
        arenaConfig.addDefault("player1-location", getPos1());
        arenaConfig.addDefault("player2-location", getPos2());
        arenaConfig.addDefault("name", name);
        arenaConfig.addDefault("commands-after", def);
        arenaConfig.options().copyDefaults(true);
        try {
            arenaConfig.save(arenaConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean deleteArena() {
        File arenaConfigFile = new File(Main.getDataDirectory(), "/arenas/" + name + ".yml");
        return arenaConfigFile.delete();

    }

}
