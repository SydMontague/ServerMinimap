package de.craftlancer.serverminimap.nmscompat;

import org.bukkit.block.Block;

public interface INMSHandler
{
    public MaterialMapColorInterface getColorNeutral();
    
    public MaterialMapColorInterface getBlockColor(Block b);
}
