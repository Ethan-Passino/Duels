package me.TurtlesAreHot.Duels;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;




public class Duel {
    private DuelPlayer player1;
    private DuelPlayer player2;
    private Arena arena;
    private int timeLeft;
    private List<UUID> spectators;
    private DuelPlayer winner;
    private int timerTaskId;
    private int waitTimer;
    private boolean ended = false;

    public Duel(DuelPlayer p1, DuelPlayer p2, Arena a) {
        this.player1 = p1;
        this.player2 = p2;
        this.arena = a;
        this.timeLeft = Config.getTimer() + 1; //get default in the config.
        this.spectators = new ArrayList<>();
        this.winner = null;
    }


    public DuelPlayer getPlayer1() { return this.player1; }

    public DuelPlayer getPlayer2() { return this.player2; }

    public Arena getArena() { return this.arena; }

    public void setArena(Arena a) { this.arena = a; }

    public int getTimeLeft() { return this.timeLeft; }

    public DuelPlayer getWinner() { return this.winner; }

    public void setWinner(DuelPlayer win) { this.winner = win; }

    public boolean hasEnded() { return this.ended; }

    private int startTimer(Player p1, Player p2) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("Duels"), () -> {
            timeLeft--;
            if(timeLeft == 0) {
                endMatch(p1, p2);

            } else {
                p1.sendMessage(ChatColor.AQUA + "There is " + timeLeft + " minutes left in this match.");
                p2.sendMessage(ChatColor.AQUA + "There is " + timeLeft + " minutes left in this match.");
            }
        }, 0, 1200);

    }

    public void tpPlayers(Player p1, Player p2) {
        p1.teleport(this.arena.getPos1());
        p2.teleport(this.arena.getPos2());
    }

    public void startMatch() {
        Player p1 = Bukkit.getPlayer(player1.getPlayer());
        Player p2 = Bukkit.getPlayer(player2.getPlayer());
        tpPlayers(p1, p2);
        Main.msgPlayer(p1, "The match will start in " + Config.getTimeBefore() + " seconds...");
        Main.msgPlayer(p2, "The match will start in " + Config.getTimeBefore() + " seconds...");
        Main.addCantMovePlayer(p1.getUniqueId());
        Main.addCantMovePlayer(p2.getUniqueId());
        waitTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("Duels"), () -> {
            Main.msgPlayer(p1, "Match has started!");
            Main.msgPlayer(p2, "Match has started!");
            Main.removeCantMovePlayer(p1.getUniqueId());
            Main.removeCantMovePlayer(p2.getUniqueId());
            Bukkit.getScheduler().cancelTask(waitTimer);
        }, 20 * Config.getTimeBefore(), 20 * Config.getTimeBefore());
        timerTaskId = startTimer(p1, p2);
    }

    public void tpAllToSpawn(Player p1, Player p2) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "combatlogx untag " + p1.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "combatlogx untag " + p2.getName());
        World spawnWorld = Bukkit.getWorld(Config.getSpawnWorld());
        if(p1 != null) {
            p1.teleport(spawnWorld.getSpawnLocation());
        }
        if(p2 != null) {
            p2.teleport(spawnWorld.getSpawnLocation());
        }
        for(UUID spectator : spectators) {
            Bukkit.getPlayer(spectator).setGameMode(GameMode.SURVIVAL);
            Bukkit.getPlayer(spectator).teleport(spawnWorld.getSpawnLocation());
        }
    }

    public void endMatch(Player p1, Player p2) {
        ended = true;
        // stops timer task
        Bukkit.getScheduler().cancelTask(this.timerTaskId);
        String message = "";
        if(winner.getPlayer() == null) {
            message = ChatColor.AQUA + "There was a tie!";
        } else if(winner.getPlayer().equals(player1.getPlayer())) {
            message = ChatColor.AQUA + "This match was won by " + p1.getDisplayName() + "!";
        } else {
            message = ChatColor.AQUA + "This match was won by " + p2.getDisplayName() + "!";
        }
        if(p1 != null) {
            p1.sendMessage(message);
        }
        if(p2 != null) {
            p2.sendMessage(message);
        }
        tpAllToSpawn(p1, p2);
        List<String> cmdsAfter = this.arena.getCommandsAfter();
        for(String cmd : cmdsAfter) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        Main.removeDuel(this);
    }


    public List<UUID> getSpectators() { return this.spectators; }

    public void addSpectator(Player p) {
        this.spectators.add(p.getUniqueId());
        p.teleport(this.arena.getPos1());
        p.setGameMode(GameMode.SPECTATOR);
    }

    public void removeSpectator(Player p) {
        p.setGameMode(GameMode.SURVIVAL);
        p.teleport(Bukkit.getWorld(Config.getSpawnWorld()).getSpawnLocation());
        this.spectators.remove(p.getUniqueId());

    }







}