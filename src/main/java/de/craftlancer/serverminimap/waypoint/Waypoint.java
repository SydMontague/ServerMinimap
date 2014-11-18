package de.craftlancer.serverminimap.waypoint;

import org.bukkit.map.MapCursor;

import de.craftlancer.serverminimap.ExtraCursor;

public class Waypoint extends ExtraCursor
{
    private String name;
    
    public Waypoint(int x, int z, boolean visible, MapCursor.Type type, byte direction, String world, boolean outside, String name)
    {
        super(x, z, visible, type, direction, world, outside);
        this.name = name;
    }
    
    /**
     * Get the name of the waypoint.
     *
     * @return the name of the waypoint, if not set it returns "". (Empty String)
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Set the name of the waypoint.
     * Names are NOT unique.
     *
     * @param name
     *        the new name
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    @Override
    public String toString()
    {
        return super.toString() + " " + name;
    }
    
}
