package me.TurtlesAreHot.Duels.events;

import com.projectkorra.projectkorra.event.AbilityStartEvent;
import me.TurtlesAreHot.Duels.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class onBend implements Listener {

    @EventHandler
    public void onBending(AbilityStartEvent e) {
        if(Main.isCantMovePlayer(e.getAbility().getPlayer())) {
            e.setCancelled(true);
        }
    }
}
