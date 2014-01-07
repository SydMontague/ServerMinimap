package de.craftlancer.serverminimap.waypoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.map.MapCursor;

import de.craftlancer.serverminimap.ExtraCursor;
import de.craftlancer.serverminimap.ServerMinimap;
import de.craftlancer.serverminimap.event.MinimapExtraCursorEvent;

public class WaypointHandler implements Listener
{
    private ServerMinimap plugin;
    private Map<String, List<ExtraCursor>> waypoints = new HashMap<String, List<ExtraCursor>>();
    
    public WaypointHandler(ServerMinimap plugin)
    {
        this.plugin = plugin;
    }
    
    public boolean addWaypoint(String player, int x, int z, String world)
    {
        if (player == null || world == null)
            return false;
        
        if (!waypoints.containsKey(player))
            waypoints.put(player, new ArrayList<ExtraCursor>());
        
        return waypoints.get(player).add(new ExtraCursor(x, z, true, MapCursor.Type.WHITE_CROSS, (byte) 0, world, plugin.showDistantWaypoints()));
    }
    
    public List<ExtraCursor> getWaypoints(String player)
    {
        if (!waypoints.containsKey(player))
            waypoints.put(player, new ArrayList<ExtraCursor>());
        
        return waypoints.get(player);
    }
    
    public boolean addWaypoint(Player p)
    {
        if (p == null)
            return false;
        
        return addWaypoint(p.getName(), p.getLocation());
    }
    
    public boolean addWaypoint(String player, Location loc)
    {
        if (loc == null)
            return false;
        
        return addWaypoint(player, loc.getBlockX(), loc.getBlockZ(), loc.getWorld().getName());
    }
    
    public boolean addWaypoint(String player, String loc)
    {
        String[] arr = loc.split(" ");
        
        if (arr.length != 3)
            return false;
        
        int x;
        int z;
        
        try
        {
            x = Integer.parseInt(arr[0]);
            z = Integer.parseInt(arr[1]);
        }
        catch (NumberFormatException e)
        {
            plugin.getLogger().severe(arr[0] + " and/or " + arr[1] + " is not a number!");
            return false;
        }
        
        return addWaypoint(player, x, z, arr[2]);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMinimapExtraCursor(MinimapExtraCursorEvent e)
    {
        List<ExtraCursor> cursors = getWaypoints(e.getPlayer().getName());
        
        e.getCursors().addAll(getWaypoints(e.getPlayer().getName()));
    }
    
    public void load()
    {
        if (plugin.getConfig().getBoolean("useMySQL", false))
            loadMySQL();
        else
            loadFile();
    }
    
    public void save()
    {
        if (plugin.getConfig().getBoolean("useMySQL", false))
            saveMySQL();
        else
            saveFile();
    }
    
    private void saveMySQL()
    {
        // TODO Auto-generated method stub
        
    }
    
    private void saveFile()
    {
        File file = new File(plugin.getDataFolder(), "waypoints.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        for (Entry<String, List<ExtraCursor>> e : waypoints.entrySet())
        {
            List<String> str = new ArrayList<String>((int) (waypoints.size() * 1.25));
            for (ExtraCursor c : e.getValue())
                str.add(c.toString());
            
            config.set(e.getKey(), str);
        }
        
        try
        {
            config.save(file);
        }
        catch (IOException e1)
        {
            plugin.getLogger().severe("Failed to save waypoints.yml!");
            e1.printStackTrace();
        }
    }
    
    private void loadFile()
    {
        File file = new File(plugin.getDataFolder(), "waypoints.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        for (String key : config.getKeys(false))
            for (String value : config.getStringList(key))
                if (!addWaypoint(key, value))
                    plugin.getLogger().warning("Invalid location string: " + value);
    }
    
    private void loadMySQL()
    {
        // TODO Auto-generated method stub
        
    }
    
    public boolean removeWaypoint(String name, int index)
    {
        if (index < 0 || getWaypoints(name) == null || getWaypoints(name).size() == 0)
            return false;
        
        return getWaypoints(name).remove(index) != null;
    }
    
    public ExtraCursor getWaypoint(String name, int index)
    {
        if (index < 0 || getWaypoints(name) == null || getWaypoints(name).size() == 0)
            return null;
        
        return getWaypoints(name).get(index);
    }
}
