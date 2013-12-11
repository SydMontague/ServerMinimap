package de.craftlancer.serverminimap;

import net.minecraft.server.v1_7_R1.MaterialMapColor;

public class RenderResult
{
    private MaterialMapColor color;
    private short avgY;
    
    public RenderResult(MaterialMapColor color, short avgY)
    {
        this.color = color;
        this.avgY = avgY;
    }
    
    public MaterialMapColor getColor()
    {
        return color;
    }
    
    public short getAverageY()
    {
        return avgY;
    }
}
