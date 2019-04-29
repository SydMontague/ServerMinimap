package de.craftlancer.serverminimap;

import java.io.File;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import de.craftlancer.serverminimap.nmscompat.INMSHandler;
import de.craftlancer.serverminimap.waypoint.WaypointCommandHandler;
import de.craftlancer.serverminimap.waypoint.WaypointHandler;

/**
 * The plugin is based on a request/idea by toxictroop and by the team at:
 * http://www.crossfiregaming.net/
 * http://forums.bukkit.org/threads/dynamic-maps.146024/
 *
 * @author Syd
 */
public class ServerMinimap extends JavaPlugin
{
    public static int MAPID = 0;
    private FileConfiguration config;
    private int SCALE = 0;
    private int CPR = 8;
    private int runPerTicks = 1;
    private int fastTicks = 20;
    private boolean canSeeOthers;
    private boolean distantWaypoints;
    
    private WaypointHandler waypoint;
    
    private INMSHandler nms;
    
    @Override
    public void onEnable()
    {
        setupNMSHandler();
        
        loadConfig();
        loadMap();
        
        waypoint = new WaypointHandler(this);
        waypoint.load();
        getServer().getPluginManager().registerEvents(waypoint, this);
        
        getCommand("waypoint").setExecutor(new WaypointCommandHandler(this));
        getCommand("minimap").setExecutor(new MinimapCommand());
    }
    
    private void setupNMSHandler()
    {
        String cbPackage = getServer().getClass().getPackage().getName();
        String version = cbPackage.substring(cbPackage.lastIndexOf('.') + 1);
        
        try
        {
            Class<?> c = Class.forName("de.craftlancer.serverminimap.nmscompat." + version + ".NMSHandler");
            if (INMSHandler.class.isAssignableFrom(c))
                nms = (INMSHandler) c.getConstructor().newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            getLogger().severe("Error while loading NMS Compat Layer - please notify author!");
        }
    }
    
    public INMSHandler getNMSHandler()
    {
        return nms;
    }
    
    @Override
    public void onDisable()
    {
        waypoint.save();
        config.set("mapID", MAPID);
        saveConfig();
        getServer().getScheduler().cancelTasks(this);
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
        MAPID = (short) config.getInt("mapID", 0);
        distantWaypoints = config.getBoolean("showDistantWaypoints", false);
        
        for (String s : config.getConfigurationSection("worlds").getKeys(false))
        {
            World w = getServer().getWorld(s);
            
            if (w == null)
                continue;
            
            if (!config.getBoolean("worlds." + s + ".enabled", true))
                w.setMetadata("minimap.disabled", new FixedMetadataValue(this, null));
            
            if (config.isInt("worlds." + s + ".drawHeight"))
                w.setMetadata("minimap.drawheight", new FixedMetadataValue(this, config.getInt("worlds." + s + ".drawHeight")));
            
            if (config.isInt("worlds." + s + ".scale"))
            {
                int value = config.getInt("worlds." + s + ".scale");
                if (value < 1)
                    value = 1;
                w.setMetadata("minimap.scale", new FixedMetadataValue(this, value));
            }
        }
    }
    
    public int getRunPerTicks()
    {
        return runPerTicks;
    }
    
    public int getFastTicks()
    {
        return fastTicks;
    }
    
    public boolean canSeeOthers()
    {
        return canSeeOthers;
    }
    
    public int getScale()
    {
        return SCALE;
    }
    
    @SuppressWarnings("deprecation")
    private void loadMap()
    {
        MapView map = getServer().getMap(MAPID);
        if (map == null)
            map = getServer().createMap(getServer().getWorlds().get(0));
        
        MAPID = map.getId();
        
        if (!(map.getRenderers().get(0) instanceof MinimapRenderer))
        {
            for (MapRenderer r : map.getRenderers())
                map.removeRenderer(r);
            
            MinimapRenderer renderer = getNMSHandler().hasTwoHands() ? new TwoHandedRenderer(SCALE, CPR, this) : new OneHandedRenderer(SCALE, CPR, this);
            
            map.addRenderer(renderer);
        }
        
        getLogger().info("Created Minimap with ID " + MAPID + ". Use /give <name> MAP 1 " + MAPID + " to get the map as item. (Vanilla command)");
        getLogger().info("Alternative command: /minimap (Plugin command)");
    }
    
    public WaypointHandler getWaypointHandler()
    {
        return waypoint;
    }
    
    public boolean showDistantWaypoints()
    {
        return distantWaypoints;
    }
    
}
