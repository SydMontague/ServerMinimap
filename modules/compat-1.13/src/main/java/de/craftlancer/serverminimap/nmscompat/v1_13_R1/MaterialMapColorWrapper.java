package de.craftlancer.serverminimap.nmscompat.v1_13_R1;

import net.minecraft.server.v1_13_R1.MaterialMapColor;
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
        return color.ac;
    }
}
