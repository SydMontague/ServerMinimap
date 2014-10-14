package de.craftlancer.serverminimap.waypoint;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.craftlancer.serverminimap.ServerMinimap;

public class WaypointHideCommand extends WaypointSubCommand
{
    
    public WaypointHideCommand(String permission, ServerMinimap plugin)
    {
        super(permission, plugin, false);
    }
    
    @Override
    protected void execute(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!checkSender(sender))
            sender.sendMessage("You don't have permission for this command!");
        else if (args.length < 2)
            sender.sendMessage("You need to specify a index!");
        else
        {
            Map<Integer, Waypoint> w = plugin.getWaypointHandler().getMatchingWaypoints(((Player) sender).getUniqueId(), args[1]);
            
            if (w.isEmpty())
            {
                sender.sendMessage("There is no waypoint with this index/name!");
                return;
            }
            
            for (Entry<Integer, Waypoint> entry : w.entrySet())
            {
                Waypoint ww = entry.getValue();
                int index = entry.getKey();
                boolean hide;
                
                if (args.length >= 3 && args[2].equalsIgnoreCase("true"))
                    hide = false;
                else if (args.length >= 3 && args[2].equalsIgnoreCase("false"))
                    hide = true;
                else
                    hide = !ww.isVisible();
                
                plugin.getWaypointHandler().updateVisibility(((Player) sender).getUniqueId(), index, hide);
            }
        }
    }
    
    @Override
    public void help(CommandSender sender)
    {
        sender.sendMessage("/waypoint hide <index> [true/false] - hide/unhide a waypoint with an index");
    }
    
}
