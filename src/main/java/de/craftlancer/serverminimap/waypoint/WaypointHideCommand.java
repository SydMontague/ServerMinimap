package de.craftlancer.serverminimap.waypoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.craftlancer.serverminimap.ExtraCursor;
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
            int index;
            
            try
            {
                index = Integer.parseInt(args[1]) - 1;
            }
            catch (NumberFormatException e)
            {
                sender.sendMessage(args[1] + " is not a number!");
                return;
            }
            
            ExtraCursor w = plugin.getWaypointHandler().getWaypoint(sender.getName(), index);
            
            boolean hide;
            
            if (args.length >= 3 && args[2].equalsIgnoreCase("true"))
                hide = false;
            else if (args.length >= 3 && args[2].equalsIgnoreCase("false"))
                hide = true;
            else
                hide = !w.isVisible();
            
            plugin.getWaypointHandler().updateVisibility(sender.getName(), index, hide);
            w.setVisible(hide);
        }
    }
    
    @Override
    public void help(CommandSender sender)
    {
        sender.sendMessage("/waypoint hide <index> [true/false] - hide/unhide a waypoint with an index");
    }
    
}
