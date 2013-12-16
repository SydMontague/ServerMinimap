package de.craftlancer.serverminimap.nmscompat.v1_6_R3;

import de.craftlancer.serverminimap.nmscompat.MaterialMapColorInterface;
import net.minecraft.server.v1_6_R3.MaterialMapColor;

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
