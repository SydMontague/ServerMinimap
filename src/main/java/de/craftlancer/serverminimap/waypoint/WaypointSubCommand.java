package de.craftlancer.serverminimap.waypoint;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.craftlancer.serverminimap.ServerMinimap;

public abstract class WaypointSubCommand
{
    private String permission = "";
    protected ServerMinimap plugin;
    private boolean console;
    
    public WaypointSubCommand(String permission, ServerMinimap plugin, boolean console)
    {
        this.permission = permission;
        this.plugin = plugin;
        this.console = console;
    }
    
    public boolean checkSender(CommandSender sender)
    {
        if (!(sender instanceof Player) && isConsoleCommand())
            return true;
        
        if (sender.hasPermission(getPermission()))
            return true;
        
        return false;
    }
    
    public ServerMinimap getPlugin()
    {
        return plugin;
    }
    
    public boolean isConsoleCommand()
    {
        return console;
    }
    
    public String getPermission()
    {
        return permission;
    }
    
    protected abstract void execute(CommandSender sender, Command cmd, String label, String[] args);
    
    @SuppressWarnings("unused")
    protected List<String> onTabComplete(CommandSender sender, String[] args)
    {
        return null;
    }
    
    public abstract void help(CommandSender sender);
}
