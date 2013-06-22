package de.craftlancer.serverminimap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The plugin is based on a request/idea by toxictroop and by the team at:
 * http://www.crossfiregaming.net/
 * http://forums.bukkit.org/threads/dynamic-maps.146024/
 * 
 * @author Syd
 */
public class ServerMinimap extends JavaPlugin
{
    public static short MAPID = 0;
    private FileConfiguration config;
    private Map<Integer, Byte> colorMap = new HashMap<Integer, Byte>();
    private int SCALE = 0;
    private int CPR = 8;
    public int runPerTicks = 1;
    public int fastTicks = 20;
    public boolean canSeeOthers;
    
    @Override
    public void onEnable()
    {
        loadConfig();
        loadMap();
    }
    
    private void loadConfig()
    {
        if (!new File(getDataFolder().getPath() + File.separatorChar + "config.yml").exists())
            saveDefaultConfig();
        
        config = getConfig();
        
        SCALE = config.getInt("scale", 0);
        CPR = config.getInt("chunksPerRun", 4);
        runPerTicks = config.getInt("runPerTicks", 1);
        fastTicks = config.getInt("fastTicks", 20);
        canSeeOthers = config.getBoolean("canSeeOthers", true);
        
        for (String key : config.getConfigurationSection("colors").getKeys(false))
            if (isMaterial(key))
                if (isValidColor(config.getInt("colors." + key, 0)))
                    colorMap.put(getMaterial(key).getId(), (byte) config.getInt("colors." + key, 0));
    }
    
    private static boolean isValidColor(int color)
    {
        return (color < 0 || color > 56) ? false : true;
    }
    
    private static boolean isMaterial(String s)
    {
        return getMaterial(s) != null;
    }
    
    private static Material getMaterial(String s)
    {
        try
        {
            return Material.getMaterial(Integer.parseInt(s));
        }
        catch (NumberFormatException e)
        {
            return Material.getMaterial(s);
        }
    }
    
    private void loadMap()
    {
        MapView map = getServer().getMap((short) 0);
        if (map == null)
            map = getServer().createMap(getServer().getWorlds().get(0));
        
        if(map.getId() != 0)
            getLogger().severe("Created Map has not Id 0 while map 0 is non existent! PLEASE REPORT TO ME!!!");
        
        if (!(map.getRenderers().get(0) instanceof AlternativeRenderer))
        {
            for (MapRenderer r : map.getRenderers())
                map.removeRenderer(r);
            
            map.addRenderer(new AlternativeRenderer(SCALE, CPR, getServer().getWorlds().get(0), this));
        }
    }
    
    public byte getColor(Material mat)
    {
        return getColor(mat.getId());
    }
    
    public byte getColor(int id)
    {
        return colorMap.containsKey(id) ? colorMap.get(id) : 0;
    }
}
