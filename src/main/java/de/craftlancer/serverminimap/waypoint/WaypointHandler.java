package de.craftlancer.serverminimap.waypoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.map.MapCursor;

import de.craftlancer.serverminimap.ExtraCursor;
import de.craftlancer.serverminimap.ServerMinimap;
import de.craftlancer.serverminimap.data.ConfigHandler;
import de.craftlancer.serverminimap.data.DataHandler;
import de.craftlancer.serverminimap.data.MySQL;
import de.craftlancer.serverminimap.event.MinimapExtraCursorEvent;

public class WaypointHandler implements Listener
{
    private ServerMinimap plugin;
    private Map<String, List<ExtraCursor>> waypoints = new HashMap<String, List<ExtraCursor>>();
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
            
            handler = new MySQL(plugin, host, port, user, pass, database);
        }
        else
            handler = new ConfigHandler(plugin);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMinimapExtraCursor(MinimapExtraCursorEvent e)
    {
        e.getCursors().addAll(getWaypoints(e.getPlayer().getName()));
    }
    
    public void load()
    {
        waypoints = handler.loadWaypoints();
    }
    
    public void save()
    {
        handler.saveWaypoints(waypoints);
    }
    
    public boolean addWaypoint(String player, int x, int z, String world)
    {
        if (player == null || world == null)
            return false;
        
        if (!waypoints.containsKey(player))
            waypoints.put(player, new ArrayList<ExtraCursor>());
        
        handler.addWaypoint(player, x, z, world);
        return waypoints.get(player).add(new ExtraCursor(x, z, true, MapCursor.Type.WHITE_CROSS, (byte) 0, world, plugin.showDistantWaypoints()));
    }
    
    public boolean removeWaypoint(String name, int index)
    {
        if (index < 0 || getWaypoints(name) == null || getWaypoints(name).size() == 0)
            return false;
        
        handler.removeWaypoint(name, getWaypoints(name).get(index));
        
        return getWaypoints(name).remove(index) != null;
    }
    
    public ExtraCursor getWaypoint(String name, int index)
    {
        if (index < 0 || getWaypoints(name) == null || getWaypoints(name).size() == 0)
            return null;
        
        return getWaypoints(name).get(index);
    }
    
    public List<ExtraCursor> getWaypoints(String player)
    {
        if (!waypoints.containsKey(player))
            waypoints.put(player, new ArrayList<ExtraCursor>());
        
        return waypoints.get(player);
    }
    
    public void updateVisibility(String name, int index, boolean hide)
    {
        if (index < 0 || getWaypoints(name) == null || getWaypoints(name).size() == 0)
            return;
        
        ExtraCursor c = getWaypoint(name, index);
        
        if (c == null)
            return;
        
        handler.updateVisible(name, c, hide);
        c.setVisible(hide);
    }
}
