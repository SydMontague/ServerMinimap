package de.craftlancer.serverminimap;

import de.craftlancer.serverminimap.nmscompat.MaterialMapColorInterface;

public class RenderResult
{
    private MaterialMapColorInterface color;
    private short avgY;
    
    public RenderResult(MaterialMapColorInterface color, short avgY)
    {
        this.color = color;
        this.avgY = avgY;
    }
    
    public MaterialMapColorInterface getColor()
    {
        return color;
    }
    
    public short getAverageY()
    {
        return avgY;
    }
}
