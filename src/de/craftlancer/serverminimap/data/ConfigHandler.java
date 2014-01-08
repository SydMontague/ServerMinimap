package de.craftlancer.serverminimap.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCursor;

import de.craftlancer.serverminimap.ExtraCursor;
import de.craftlancer.serverminimap.ServerMinimap;

public class ConfigHandler implements DataHandler
{
    private File file;
    private FileConfiguration config;
    private ServerMinimap plugin;
    
    public ConfigHandler(ServerMinimap plugin)
    {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "waypoints.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }
    
    @Override
    public Map<String, List<ExtraCursor>> loadWaypoints()
    {
        Map<String, List<ExtraCursor>> map = new HashMap<String, List<ExtraCursor>>();
        
        for (String key : config.getKeys(false))
            for (String value : config.getStringList(key))
            {
                if (!map.containsKey(key))
                    map.put(key, new ArrayList<ExtraCursor>());
                
                String[] arr = value.split(" ");
                
                if (arr.length != 3)
                    continue;
                
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
                    continue;
                }
                
                map.get(key).add(new ExtraCursor(x, z, true, MapCursor.Type.WHITE_CROSS, (byte) 0, arr[2], plugin.showDistantWaypoints()));
            }
        
        return map;
    }
    
    @Override
    public void saveWaypoints(Map<String, List<ExtraCursor>> waypoints)
    {
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
        catch (IOException e)
        {
            plugin.getLogger().severe("Failed to save waypoints.yml!");
            e.printStackTrace();
        }
    }
    
    @Override
    public void addWaypoint(String player, int x, int z, String world)
    {
    }
    
    @Override
    public void removeWaypoint(String player, ExtraCursor c)
    {
    }
    
    @Override
    public void updateVisible(String player, ExtraCursor c, boolean visible)
    {
    }
}
