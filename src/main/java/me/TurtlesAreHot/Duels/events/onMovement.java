package me.TurtlesAreHot.Duels.events;

import me.TurtlesAreHot.Duels.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class onMovement implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(Main.isCantMovePlayer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
