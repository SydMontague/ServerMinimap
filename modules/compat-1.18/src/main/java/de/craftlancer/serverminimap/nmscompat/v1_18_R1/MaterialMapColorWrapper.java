package de.craftlancer.serverminimap.nmscompat.v1_18_R1;

import net.minecraft.world.level.material.MaterialMapColor;
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
        return color.al;
    }
}
