package de.craftlancer.serverminimap.data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.craftlancer.serverminimap.waypoint.Waypoint;

public interface DataHandler
{
    public Map<UUID, List<Waypoint>> loadWaypoints();
    
    public void saveWaypoints(Map<UUID, List<Waypoint>> waypoints);
    
    public void addWaypoint(UUID player, int x, int z, String world, boolean visible, String name);
    
    public void removeWaypoint(UUID player, Waypoint c);
    
    public void updateVisible(UUID player, Waypoint c, boolean visible);
    
    public void updateName(UUID player, Waypoint c);
}
