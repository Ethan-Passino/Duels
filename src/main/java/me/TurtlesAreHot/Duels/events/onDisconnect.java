package me.TurtlesAreHot.Duels.events;

import me.TurtlesAreHot.Duels.Config;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class onDisconnect implements Listener {

    @EventHandler
    public void disconnect(PlayerQuitEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
            event.getPlayer().teleport(Bukkit.getWorld(Config.getSpawnWorld()).getSpawnLocation());
        }

    }

}
