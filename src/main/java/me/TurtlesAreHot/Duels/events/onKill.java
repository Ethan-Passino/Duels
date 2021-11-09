package me.TurtlesAreHot.Duels.events;

import me.TurtlesAreHot.Duels.Duel;
import me.TurtlesAreHot.Duels.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.UUID;

public class onKill implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if(event.getEntity().getKiller() != null) {
            List<UUID> duelPlayers = Main.getDuelPlayers();
            for(UUID p : duelPlayers) {
                if(event.getEntity().getUniqueId().equals(p)) {
                    // In this case it is a duel and the duel needs to end
                    Duel d = Main.getDuel(p);
                    if(d.getPlayer1().getPlayer().equals(p)) {
                        d.setWinner(d.getPlayer1());
                    } else {
                        d.setWinner(d.getPlayer2());
                    }
                    d.endMatch(Bukkit.getPlayer(d.getPlayer1().getPlayer()),
                            Bukkit.getPlayer(d.getPlayer2().getPlayer()));
                    Main.removeDuel(d);

                }
            }
        }
    }
}
