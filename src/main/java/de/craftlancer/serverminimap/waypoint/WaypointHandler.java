package de.craftlancer.serverminimap.waypoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.map.MapCursor;

import de.craftlancer.serverminimap.ServerMinimap;
import de.craftlancer.serverminimap.data.ConfigHandler;
import de.craftlancer.serverminimap.data.DataHandler;
import de.craftlancer.serverminimap.data.MySQL;
import de.craftlancer.serverminimap.event.MinimapExtraCursorEvent;

public class WaypointHandler implements Listener
{
    private ServerMinimap plugin;
    private Map<UUID, List<Waypoint>> waypoints = new HashMap<UUID, List<Waypoint>>();
    private DataHandler handler;
    
    public WaypointHandler(ServerMinimap plugin)
    {
        this.plugin = plugin;
        if (plugin.getConfig().getBoolean("mysql.enabled", false))
        {
            String host = plugin.getConfig().getString("mysql.host");
            int port = plugin.getConfig().getInt("mysql.port");
            String user = plugin.getConfig().getString("mysql.user");
            String pass = plugin.getConfig().getString("mysql.pass");
            String database = plugin.getConfig().getString("mysql.database");
            String prefix = plugin.getConfig().getString("mysql.table_prefix");
            
            handler = new MySQL(plugin, host, port, user, pass, database, prefix);
        }
        else
            handler = new ConfigHandler(plugin);
    }
    
    public DataHandler getDataHandler()
    {
        return handler;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMinimapExtraCursor(MinimapExtraCursorEvent e)
    {
        e.getCursors().addAll(getWaypoints(e.getPlayer().getUniqueId()));
    }
    
    public void load()
    {
        waypoints = handler.loadWaypoints();
    }
    
    public void save()
    {
        handler.saveWaypoints(waypoints);
    }
    
    public boolean addWaypoint(UUID player, int x, int z, String world, String name)
    {
        if (player == null || world == null)
            return false;
        
        if (!waypoints.containsKey(player))
            waypoints.put(player, new ArrayList<Waypoint>());
        
        handler.addWaypoint(player, x, z, world, true, "");
        return waypoints.get(player).add(new Waypoint(x, z, true, MapCursor.Type.WHITE_CROSS, (byte) 0, world, plugin.showDistantWaypoints(), name));
    }
    
    public boolean removeWaypoint(UUID name, int index)
    {
        List<Waypoint> wp = getWaypoints(name);
        if (index < 0 || wp == null || wp.size() == 0 || wp.size() < index || getWaypoints(name).contains(index))
            return false;
        
        handler.removeWaypoint(name, getWaypoints(name).get(index));
        
        return getWaypoints(name).remove(index) != null;
    }
    
    public Waypoint getWaypoint(UUID name, int index)
    {
        List<Waypoint> wp = getWaypoints(name);
        if (index < 0 || wp == null || wp.size() == 0 || wp.size() < index)
            return null;
        
        return getWaypoints(name).get(index);
    }
    
    public List<Waypoint> getWaypoints(UUID player)
    {
        if (!waypoints.containsKey(player))
            waypoints.put(player, new ArrayList<Waypoint>());
        
        return waypoints.get(player);
    }
    
    public Map<Integer, Waypoint> getMatchingWaypoints(UUID name, String string)
    {
        List<Waypoint> localWaypoints = getWaypoints(name);
        Map<Integer, Waypoint> match = new HashMap<Integer, Waypoint>();
        
        for (int i = 0; localWaypoints.size() > i; i++)
            if (localWaypoints.get(i).getName().equalsIgnoreCase(string) || String.valueOf(i + 1).equals(string))
                match.put(i + 1, localWaypoints.get(i));
        
        return match;
    }
}
