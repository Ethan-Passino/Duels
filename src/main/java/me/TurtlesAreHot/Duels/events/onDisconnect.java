package me.TurtlesAreHot.Duels.events;

import me.TurtlesAreHot.Duels.Config;
import me.TurtlesAreHot.Duels.Duel;
import me.TurtlesAreHot.Duels.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
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
        Duel d = Main.getDuel(event.getPlayer().getUniqueId());
        if(d != null) {
            // Figure out which user logged out in the duel (player1 or player2)
            Player p1 = Bukkit.getPlayer(d.getPlayer1().getPlayer());
            Player p2 = Bukkit.getPlayer(d.getPlayer2().getPlayer());
            if(p1 != null) {
                p1.sendMessage(ChatColor.AQUA + "The other player logged out so you win!");
                d.setWinner(d.getPlayer1());
            }
            if(p2 != null) {
                p2.sendMessage(ChatColor.AQUA + "The other player logged out so you win!");
                d.setWinner(d.getPlayer2());
            }
            d.endMatch(p1, p2);

        }

    }

}
