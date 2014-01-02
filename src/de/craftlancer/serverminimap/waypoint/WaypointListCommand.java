package de.craftlancer.serverminimap.waypoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.craftlancer.serverminimap.ExtraCursor;
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
        if(!checkSender(sender))
            sender.sendMessage("You don't have permission for this command!");
        else
        {
            int i = 1;
            for(ExtraCursor w : plugin.getWaypointHandler().getWaypoints(sender.getName()))
                sender.sendMessage(i + " X: " + w.getX() + " Z: " + w.getZ() + " Visible: " + w.isVisible());
        }        
    }
    
    @Override
    public void help(CommandSender sender)
    {
        // TODO Auto-generated method stub
        
    }
    
}
