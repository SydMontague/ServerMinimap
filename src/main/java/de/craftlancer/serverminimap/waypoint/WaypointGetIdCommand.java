package de.craftlancer.serverminimap.waypoint;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.craftlancer.serverminimap.ServerMinimap;

public class WaypointGetIdCommand extends WaypointSubCommand
{
    
    public WaypointGetIdCommand(String permission, ServerMinimap plugin)
    {
        super(permission, plugin, false);
    }
    
    @Override
    protected void execute(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!checkSender(sender))
            sender.sendMessage("You don't have permission for this command!");
        else if (args.length > 2)
            sender.sendMessage("You need to specify a name!");
        else
        {
            Map<Integer, Waypoint> matchingWaypoints = plugin.getWaypointHandler().getMatchingWaypoints(((Player) sender).getUniqueId(), args[1]);
            
            sender.sendMessage(args[1] + " matches to the following IDs: ");
            StringBuilder str = new StringBuilder();
            for (Integer i : matchingWaypoints.keySet())
                str.append(i).append(" ");
            
            sender.sendMessage(str.toString());
        }
    }
    
    @Override
    public void help(CommandSender sender)
    {
        sender.sendMessage("/waypoint getid <name> - get a list of waypoint ids, that have the given name.");
        sender.sendMessage("This is mainly used for the setName command.");
    }
    
}
