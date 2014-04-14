package de.craftlancer.serverminimap.data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.craftlancer.serverminimap.ExtraCursor;

public interface DataHandler
{
    public Map<UUID, List<ExtraCursor>> loadWaypoints();
    
    public void saveWaypoints(Map<UUID, List<ExtraCursor>> waypoints);
    
    public void addWaypoint(UUID player, int x, int z, String world, boolean visible);
    
    public void removeWaypoint(UUID player, ExtraCursor c);
    
    public void updateVisible(UUID player, ExtraCursor c, boolean visible);
}
