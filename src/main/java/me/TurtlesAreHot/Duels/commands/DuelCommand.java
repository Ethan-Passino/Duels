package me.TurtlesAreHot.Duels.commands;

import me.TurtlesAreHot.Duels.Duel;
import me.TurtlesAreHot.Duels.DuelPlayer;
import me.TurtlesAreHot.Duels.Invite;
import me.TurtlesAreHot.Duels.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DuelCommand implements CommandExecutor {
    public void helpCommand(Player p) {
        p.sendMessage(ChatColor.DARK_AQUA + "[Duels Command]");
        p.sendMessage(ChatColor.AQUA + "/duel help - Performs this command.");
        p.sendMessage(ChatColor.AQUA + "/duel invite <player> - Invites a player to a duel. Randomly chooses an arena.");
        p.sendMessage(ChatColor.AQUA + "/duel list - Lists all of the current duels in session.");
        p.sendMessage(ChatColor.AQUA + "/duel spectate <player> - If the given player is in a match it will allow you to spectate the player.");
        p.sendMessage(ChatColor.DARK_AQUA + "Created by TurtlesAreHot");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only player can run this command.");
            return false;
        }
        Player p = (Player) sender;
        if(args.length <= 0) {
            if(!p.hasPermission("duels.help")) {
                Main.noPerms(p, "duels.help");
                return false;
            }
            helpCommand(p);
            return false;
        }
        String subCommand = args[0].toLowerCase();
        switch(subCommand) {
            case "invite":
                // invite command
                if(!p.hasPermission("duels.invite")) {
                    Main.noPerms(p, "duels.invite");
                    break;
                }
                if(args.length != 2) {
                    Main.msgPlayer(p, "You did not provide a name for the invitation or provided too many arguments.");
                    break;
                }
                Player invited = Bukkit.getPlayer(args[1]);
                if(invited == null) {
                    Main.msgPlayer(p, "The player you tried to invite is either offline or not a real player.");
                    break;
                }
                if(invited.getName().equalsIgnoreCase(p.getName())) {
                    Main.msgPlayer(p, "You cannot duel yourself!");
                    break;
                }
                if(Main.hasInvite(invited.getUniqueId()) != null) {
                    Main.msgPlayer(p, "This player already has an invitation from someone else. Please try again later.");
                    break;
                }
                Main.addInvite(new Invite(p.getUniqueId(), invited.getUniqueId()));
                Main.msgPlayer(invited, p.getName()
                        + " has invited you to a duel! Type /duel accept or click on this message to accept.");
                break;
            case "spectate":
                // Spectate command
                if(!p.hasPermission("duels.spectate")) {
                    Main.noPerms(p, "duels.spectate");
                    break;
                }
                if(args.length != 2) {
                    Main.msgPlayer(p, "You did not provide a name to spectate or provided too many arguments.");
                    break;
                }
                Player sp = Bukkit.getPlayer(args[1]);
                if(sp == null) {
                    Main.msgPlayer(p, "This player is not a valid player.");
                    break;
                }
                Duel d = Main.getDuel(sp.getUniqueId());
                if(d == null) {
                    Main.msgPlayer(p, "This player is currently not in a duel.");
                    break;
                }
                d.addSpectator(p);
                break;
            case "list":
                // List command to see all the duels currently being done.
                if(!p.hasPermission("duels.list")) {
                    Main.noPerms(p, "duels.list");
                    break;
                }
                List<Duel> duels = Main.getDuels();
                if(duels.size() == 0) {
                    Main.msgPlayer(p, "There is currently no duels going on.");
                    break;
                }
                p.sendMessage(ChatColor.DARK_AQUA + "[Current Duels]");
                for(Duel duel : duels) {
                    p.sendMessage(ChatColor.AQUA + Bukkit.getPlayer(duel.getPlayer1().getPlayer()).getDisplayName() +
                            " vs " + Bukkit.getPlayer(duel.getPlayer2().getPlayer()).getName());
                }
                break;
            case "accept":
                // Case when accepting an invitation
                if(!(p.hasPermission("duels.invite"))) {
                    Main.noPerms(p, "duels.invite");
                    break;
                }
                Invite inv = Main.hasInvite(p.getUniqueId());
                if(inv == null) {
                    Main.msgPlayer(p, "You do not currently have an invitation to accept.");
                    break;
                }
                if(!Main.isArenaAvailable()) {
                    Main.msgPlayer(p, "Currently there is no arenas available to 1v1 in. Please try again later.");
                    Main.removeInvite(inv);
                    break;
                }
                Player inviter = Bukkit.getPlayer(inv.getInviter());
                if(inviter == null) {
                    Main.msgPlayer(p, "The player who invited you has either left the game or doesn't exist somehow...");
                    Main.removeInvite(inv);
                    break;
                }
                Main.msgPlayer(inviter, p.getName() + " has accepted your invite. Starting match.");
                Duel theDuel = new Duel(new DuelPlayer(inv.getInviter()), new DuelPlayer(inv.getInvited()), Main.getRandomArena());
                Main.addDuel(theDuel);
                theDuel.startMatch();
                Main.removeInvite(inv);
                break;
            default:
                // help command
                if(!p.hasPermission("duels.help")) {
                    Main.noPerms(p, "duels.help");
                    break;
                }
                helpCommand(p);
                break;
        }
        return false;
    }
}
