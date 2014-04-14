package de.craftlancer.serverminimap.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
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
    public Map<UUID, List<ExtraCursor>> loadWaypoints()
    {
        Map<UUID, List<ExtraCursor>> map = new HashMap<UUID, List<ExtraCursor>>();
        
        boolean requiresUpdaterSave = false;
        
        for (String key : config.getKeys(false))
        {
            UUID uuid;
            boolean updated = false;
            
            try
            {
                uuid = UUID.fromString(key);
            }
            catch (IllegalArgumentException e)
            {
                uuid = Bukkit.getOfflinePlayer(key).getUniqueId();
                if (uuid != null)
                {
                    requiresUpdaterSave = true;
                    updated = true;
                }
                else
                {
                    plugin.getLogger().warning("Could not resolve UUID for " + key + "! The waypoint data of this key might be lost!");
                    continue;
                }
            }
            
            if (!map.containsKey(uuid))
                map.put(uuid, new ArrayList<ExtraCursor>());
            
            for (String value : config.getStringList(key))
            {
                String[] arr = value.split(" ");
                
                if (arr.length < 3)
                    continue;
                
                int x;
                int z;
                
                boolean visible = arr.length >= 4 ? !arr[3].equalsIgnoreCase("false") : true;

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
                
                map.get(uuid).add(new ExtraCursor(x, z, visible, MapCursor.Type.WHITE_CROSS, (byte) 0, arr[2], plugin.showDistantWaypoints()));
            }
            
            if (updated)
                config.set(key, null);
        }
        
        if (requiresUpdaterSave)
            saveWaypoints(map);
        
        return map;
    }
    
    @Override
    public void saveWaypoints(Map<UUID, List<ExtraCursor>> waypoints)
    {
        for (Entry<UUID, List<ExtraCursor>> e : waypoints.entrySet())
        {
            List<String> str = new ArrayList<String>((int) (waypoints.size() * 1.25));
            for (ExtraCursor c : e.getValue())
                str.add(c.toString());
            
            config.set(e.getKey().toString(), str);
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
    public void addWaypoint(UUID player, int x, int z, String world, boolean visible)
    {
    }
    
    @Override
    public void removeWaypoint(UUID player, ExtraCursor c)
    {
    }
    
    @Override
    public void updateVisible(UUID player, ExtraCursor c, boolean visible)
    {
    }
}
