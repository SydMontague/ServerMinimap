package de.craftlancer.serverminimap.nmscompat.v1_5_R3;

import net.minecraft.server.v1_5_R3.MaterialMapColor;
import de.craftlancer.serverminimap.nmscompat.MaterialMapColorInterface;

public class MaterialMapColorWrapper implements MaterialMapColorInterface
{
    private MaterialMapColor color;
    
    public MaterialMapColorWrapper(MaterialMapColor color)
    {
        this.color = color;
    }
    
    @Override
    public int getM()
    {
        return color.q;
    }
    
}
