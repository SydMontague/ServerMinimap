package de.craftlancer.serverminimap.nmscompat.v1_18_R1;

import java.util.HashMap;

import net.minecraft.world.level.material.MaterialMapColor;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.IBlockData;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftMagicNumbers;

import de.craftlancer.serverminimap.nmscompat.INMSHandler;
import de.craftlancer.serverminimap.nmscompat.MaterialMapColorInterface;

public class NMSHandler implements INMSHandler
{
    private HashMap<MaterialMapColor, MaterialMapColorWrapper> colors = new HashMap<MaterialMapColor, MaterialMapColorWrapper>();
    {
        for(MaterialMapColor color : MaterialMapColor.am)
            if(color != null)
                colors.put(color, new MaterialMapColorWrapper(color));
    }
    
    @Override
    public MaterialMapColorInterface getColorNeutral()
    {
        return colors.get(MaterialMapColor.a);
    }
    
    @Override
    public MaterialMapColorInterface getBlockColor(Block block)
    {
        net.minecraft.world.level.block.Block nmsblock = CraftMagicNumbers.getBlock(block.getType());        
        org.bukkit.craftbukkit.v1_18_R1.CraftChunk w = (org.bukkit.craftbukkit.v1_18_R1.CraftChunk) block.getChunk();
        
        BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
        IBlockData data = w.getHandle().a_(pos);
        IBlockAccess access = w.getHandle().D();
        
        MaterialMapColor nms = nmsblock.s();
        
        if (!colors.containsKey(nms))
            Bukkit.getLogger().severe("[ServerMinimap] unknown color, error in NMSHandler - please report to author!");
        
        return colors.get(nms);
    }

    @Override
    public boolean hasTwoHands() {
        return true;
    }
}
