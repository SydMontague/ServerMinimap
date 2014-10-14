package de.craftlancer.serverminimap.waypoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.craftlancer.serverminimap.ServerMinimap;

public class WaypointListCommand extends WaypointSubCommand
{
    
    public WaypointListCommand(String permission, ServerMinimap plugin)
    {
        super(permission, plugin, false);
    }
    
    @Override
    protected void execute(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!checkSender(sender))
            sender.sendMessage("You don't have permission for this command!");
        else
        {
            int i = 1;
            for (Waypoint w : plugin.getWaypointHandler().getWaypoints(((Player) sender).getUniqueId()))
                sender.sendMessage(i++ + " Name: " + w.getName() + " X: " + w.getX() + " Z: " + w.getZ() + " Visible: " + w.isVisible());
        }
    }
    
    @Override
    public void help(CommandSender sender)
    {
        sender.sendMessage("List all your waypoints with their index.");
    }
    
}
