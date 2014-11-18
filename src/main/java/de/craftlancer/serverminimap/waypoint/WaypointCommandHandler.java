package de.craftlancer.serverminimap.waypoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import de.craftlancer.serverminimap.ServerMinimap;

public class WaypointCommandHandler implements TabExecutor
{
    private HashMap<String, WaypointSubCommand> commands = new HashMap<String, WaypointSubCommand>();
    
    public WaypointCommandHandler(ServerMinimap plugin)
    {
        commands.put("help", new WaypointHelpCommand("minimap.command.waypoint.help", plugin));
        commands.put("list", new WaypointListCommand("minimap.command.waypoint.list", plugin));
        commands.put("add", new WaypointAddCommand("minimap.command.waypoint.add", plugin));
        commands.put("remove", new WaypointRemoveCommand("minimap.command.waypoint.remove", plugin));
        commands.put("hide", new WaypointHideCommand("minimap.command.waypoint.hide", plugin));
        commands.put("getid", new WaypointGetIdCommand("minimap.command.waypoint.getid", plugin));
        commands.put("setname", new WaypointSetNameCommand("minimap.command.waypoint.setname", plugin));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (args.length == 0 || !commands.containsKey(args[0]))
            if (commands.containsKey("help"))
                commands.get("help").execute(sender, cmd, label, args);
            else
                return false;
        else
            commands.get(args[0]).execute(sender, cmd, label, args);
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
    {
        switch (args.length)
        {
            case 0:
                return null;
            case 1:
                List<String> l = getMatches(args[0], commands.keySet());
                for (String str : l)
                    if (!sender.hasPermission(commands.get(str).getPermission()))
                        l.remove(l);
                return l;
            default:
                if (!commands.containsKey(args[0]))
                    return null;
                else
                    return commands.get(args[0]).onTabComplete(sender, args);
        }
    }
    
    public void registerSubCommand(String name, WaypointSubCommand command, String... alias)
    {
        Validate.notNull(command, "Command can't be null!");
        Validate.notEmpty(name, "Commandname can't be empty!");
        commands.put(name, command);
        for (String s : alias)
            commands.put(s, command);
    }
    
    protected Map<String, WaypointSubCommand> getCommands()
    {
        return commands;
    }
    
    public static List<String> getMatches(String value, Collection<String> list)
    {
        List<String> result = new LinkedList<String>();
        
        for (String str : list)
            if (str.startsWith(value))
                result.add(str);
        
        return result;
    }
}
