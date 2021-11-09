package me.TurtlesAreHot.Duels.commands;

import me.TurtlesAreHot.Duels.Arena;
import me.TurtlesAreHot.Duels.Config;
import me.TurtlesAreHot.Duels.Main;
import me.TurtlesAreHot.Duels.utils.ArenaUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {

    public void helpCommand(Player p) {
        p.sendMessage(ChatColor.DARK_AQUA + "[Duels Arena Command]");
        p.sendMessage(ChatColor.AQUA + "/arena help - Performs this command.");
        p.sendMessage(ChatColor.AQUA + "/arena pos1 - Sets the first postion for the arena (where the first player will spawn.");
        p.sendMessage(ChatColor.AQUA + "/arena pos2 - Sets the second position for the arena (where the second player will spawn.");
        p.sendMessage(ChatColor.AQUA + "/arena create <name> - Creates an arena with the given name. You must have arena pos1 and arena pos2 set to perform this command.");
        p.sendMessage(ChatColor.AQUA + "/arena list - Lists all of the arenas by name.");
        p.sendMessage(ChatColor.AQUA + "/arena delete <name> - Deletes the arena with the given name.");
        p.sendMessage(ChatColor.DARK_AQUA + "Created by TurtlesAreHot");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only player can run this command.");
            return false;
        }
        Player p = (Player) sender;
        if(args.length <= 0) {
            if(!p.hasPermission("duels.arena.help")) {
                Main.noPerms(p, "duels.arena.help");
                return false;
            }
            helpCommand(p);
            return false;
        }
        String subCommand = args[0].toLowerCase();
        switch(subCommand) {
            case "create":
                // arena command
                if(!p.hasPermission("duels.arena.create")) {
                    Main.noPerms(p, "duels.arena.create");
                    break;
                }
                if(args.length != 2) {
                    Main.msgPlayer(p, "You did not provide a name for the arena or provided too many arguments.");
                    break;
                }
                Arena created = ArenaUtils.createArena(args[1]);
                if(created == null) {
                    Main.msgPlayer(p, "You did not set the two positions needed to create an arena. " +
                            "Please use /arena pos1 and /arena pos2.");
                    break;
                }
                if(!created.createArena()) {
                    Main.msgPlayer(p,"An error occured while trying to create an arena. Either an arena with " +
                            "this name already exists, or there was some kind of other error.");
                    break;
                }
                Main.addArena(created);
                Main.msgPlayer(p, "Successfully created an arena with the name " + ChatColor.BOLD + args[1] +
                        ChatColor.RESET + "" + ChatColor.AQUA + ". You may edit its config in the arena folder.");
                break;
            case "list":
                // list arenas command
                if(!p.hasPermission("duels.arena.list")) {
                    Main.noPerms(p, "duels.arena.list");
                    break;
                }
                String[] arenas = Config.getArenas();
                if(arenas == null) {
                    Main.msgPlayer(p, "There is currently no arenas.");
                    break;
                }
                Main.msgPlayer(p,"Arenas: ");
                for(String name : arenas) {
                    p.sendMessage(ChatColor.AQUA + name);
                }
                break;
            case "pos1":
                // set first pos command
                if(!p.hasPermission("duels.arena.pos")) {
                    Main.noPerms(p, "duels.arena.pos");
                }
                ArenaUtils.setPos1(p.getLocation());
                Main.msgPlayer(p, "Set the first position to your location.");
                break;
            case "pos2":
                // set second pos command
                if(!p.hasPermission("duels.arena.pos")) {
                    Main.noPerms(p, "duels.arena.pos");
                }
                ArenaUtils.setPos2(p.getLocation());
                Main.msgPlayer(p, "Set the second position to your location.");
                break;
            case "delete":
                // delete arena command
                if(!p.hasPermission("duels.arena.delete")) {
                    Main.noPerms(p, "duels.arena.delete");
                }
                if(args.length != 2) {
                    Main.msgPlayer(p, "You did not provide a name for the arena or provided too many arguments.");
                    break;
                }
                Arena delete = Config.getArena(args[1]);
                if(delete == null) {
                    Main.msgPlayer(p, "The arena name you provided does not exist so we cannot delete it.");
                    break;
                }
                if(!delete.deleteArena()) {
                    Main.msgPlayer(p, "An error occured while trying to delete the arena.");
                    break;
                }
                Main.msgPlayer(p, "Successfully deleted the arena named " + args[1] + ".");
                break;
            default:
                // help command
                if(!p.hasPermission("duels.arena.help")) {
                    Main.noPerms(p, "duels.arena.help");
                    break;
                }
                helpCommand(p);
                break;
        }
        return false;
    }
}
