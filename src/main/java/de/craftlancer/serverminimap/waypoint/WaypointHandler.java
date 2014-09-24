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

import de.craftlancer.serverminimap.ExtraCursor;
import de.craftlancer.serverminimap.ServerMinimap;
import de.craftlancer.serverminimap.data.ConfigHandler;
import de.craftlancer.serverminimap.data.DataHandler;
import de.craftlancer.serverminimap.data.MySQL;
import de.craftlancer.serverminimap.event.MinimapExtraCursorEvent;

public class WaypointHandler implements Listener
{
    private ServerMinimap plugin;
    private Map<UUID, List<ExtraCursor>> waypoints = new HashMap<UUID, List<ExtraCursor>>();
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
    
    public boolean addWaypoint(UUID player, int x, int z, String world)
    {
        if (player == null || world == null)
            return false;
        
        if (!waypoints.containsKey(player))
            waypoints.put(player, new ArrayList<ExtraCursor>());
        
        handler.addWaypoint(player, x, z, world, true);
        return waypoints.get(player).add(new ExtraCursor(x, z, true, MapCursor.Type.WHITE_CROSS, (byte) 0, world, plugin.showDistantWaypoints()));
    }
    
    public boolean removeWaypoint(UUID name, int index)
    {
        List<ExtraCursor> wp = getWaypoints(name);
        if (index < 0 || wp == null || wp.size() == 0 || wp.size() < index)
            return false;
        
        handler.removeWaypoint(name, getWaypoints(name).get(index));
        
        return getWaypoints(name).remove(index) != null;
    }
    
    public ExtraCursor getWaypoint(UUID name, int index)
    {
        List<ExtraCursor> wp = getWaypoints(name);
        if (index < 0 || wp == null || wp.size() == 0 || wp.size() < index)
            return null;
        
        return getWaypoints(name).get(index);
    }
    
    public List<ExtraCursor> getWaypoints(UUID player)
    {
        if (!waypoints.containsKey(player))
            waypoints.put(player, new ArrayList<ExtraCursor>());
        
        return waypoints.get(player);
    }
    
    public void updateVisibility(UUID name, int index, boolean hide)
    {
        List<ExtraCursor> wp = getWaypoints(name);
        if (index < 0 || wp == null || wp.size() == 0 || wp.size() < index)
            return;
        
        ExtraCursor c = getWaypoint(name, index);
        
        if (c == null)
            return;
        
        handler.updateVisible(name, c, hide);
        c.setVisible(hide);
    }
}
