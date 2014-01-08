package de.craftlancer.serverminimap.data;

import java.util.List;
import java.util.Map;

import de.craftlancer.serverminimap.ExtraCursor;

public interface DataHandler
{
    public Map<String, List<ExtraCursor>> loadWaypoints();
    
    public void saveWaypoints(Map<String, List<ExtraCursor>> waypoints);
    
    public void addWaypoint(String player, int x, int z, String world);
    
    public void removeWaypoint(String player, ExtraCursor c);
    
    public void updateVisible(String player, ExtraCursor c, boolean visible);
}
