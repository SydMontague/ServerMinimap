package de.craftlancer.serverminimap.waypoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.craftlancer.serverminimap.ServerMinimap;

public class WaypointSetNameCommand extends WaypointSubCommand
{
    
    public WaypointSetNameCommand(String permission, ServerMinimap plugin)
    {
        super(permission, plugin, false);
    }
    
    @Override
    protected void execute(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!checkSender(sender))
            sender.sendMessage("You don't have permission for this command!");
        else if (args.length < 3)
            sender.sendMessage("You need to specify a index and a new name!");
        else
        {
            int index;
            
            try
            {
                index = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e)
            {
                sender.sendMessage("The given value it not a number!");
                return;
            }
            
            Waypoint wp = plugin.getWaypointHandler().getWaypoint(((Player) sender).getUniqueId(), index - 1);
            wp.setName(args[2]);
            plugin.getWaypointHandler().getDataHandler().updateName(((Player) sender).getUniqueId(), wp);
            sender.sendMessage("Name set!");
        }
    }
    
    @Override
    public void help(CommandSender sender)
    {
        sender.sendMessage("/waypoint setname <id> <newName> - set the the name of the waypoint with the given index.");
    }
    
}
