package de.craftlancer.serverminimap.waypoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.craftlancer.serverminimap.ServerMinimap;

public class WaypointHelpCommand extends WaypointSubCommand
{
    
    public WaypointHelpCommand(String permission, ServerMinimap plugin)
    {
        super(permission, plugin, true);
    }
    
    @Override
    protected void execute(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!checkSender(sender))
            sender.sendMessage("You don't have permission for this command!");
        else
        {
            sender.sendMessage("/waypoint help [command] - this command");
            sender.sendMessage("/waypoint add [x] [z] - adds a new waypoint to your position or to x,z");
            sender.sendMessage("/waypoint hide <id/name> - toggle visibility of a waypoint.");
            sender.sendMessage("/waypoint setname <id> <newName> - rename a waypoint.");
            sender.sendMessage("/waypoint getid <name> - Get the waypoint ids, that match the given name.");
            sender.sendMessage("/waypoint list - list all your waypoints");
            sender.sendMessage("/waypoint remove <id/name> - remove the waypoint with the given index.");
        }
    }
    
    @Override
    public void help(CommandSender sender)
    {
        sender.sendMessage("Shows help for waypoint commands.");
    }
    
}
