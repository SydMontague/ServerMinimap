package de.craftlancer.serverminimap.waypoint;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.craftlancer.serverminimap.ServerMinimap;

public class WaypointRemoveCommand extends WaypointSubCommand
{
    
    public WaypointRemoveCommand(String permission, ServerMinimap plugin)
    {
        super(permission, plugin, false);
    }
    
    @Override
    protected void execute(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!checkSender(sender))
            sender.sendMessage("You don't have permission for this command!");
        else if (args.length > 2)
            sender.sendMessage("You need to specify a index or a name!");
        else
        {
            Map<Integer, Waypoint> matchingWaypoints = plugin.getWaypointHandler().getMatchingWaypoints(((Player) sender).getUniqueId(), args[1]);
            
            if (matchingWaypoints.isEmpty())
            {
                sender.sendMessage("No matching waypoint found!");
                return;
            }
            
            int i = 0;
            
            for (Integer index : matchingWaypoints.keySet())
                if (plugin.getWaypointHandler().removeWaypoint(((Player) sender).getUniqueId(), index - 1))
                    i++;
            
            sender.sendMessage(i + " Waypoint(s) removed!");
        }
    }
    
    @Override
    public void help(CommandSender sender)
    {
        sender.sendMessage("/waypoint remove <index> - remove the waypoint of this index, see /waypoint list for the indicies.");
    }
    
}
