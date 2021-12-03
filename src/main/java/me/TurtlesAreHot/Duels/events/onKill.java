package me.TurtlesAreHot.Duels.events;

import me.TurtlesAreHot.Duels.Duel;
import me.TurtlesAreHot.Duels.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.UUID;

public class onKill implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Duel d = Main.getDuel(event.getEntity().getUniqueId());
        if(d != null) {
            // The player who died is in a duel.
            Player p1 = Bukkit.getPlayer(d.getPlayer1().getPlayer());
            Player p2 = Bukkit.getPlayer(d.getPlayer2().getPlayer());
            if(p1 != null && event.getEntity().getUniqueId().equals(p1.getUniqueId())) {
                // Player1 died, player2 wins
                d.setWinner(d.getPlayer2());
                d.endMatch(p1, p2);
            } else if(p2 != null && event.getEntity().getUniqueId().equals(p2.getUniqueId())) {
                // Player2 died, player1 wins
                d.setWinner(d.getPlayer1());
                d.endMatch(p1, p2);
            }
        }
    }
}