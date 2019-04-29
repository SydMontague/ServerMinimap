package de.craftlancer.serverminimap.nmscompat.v1_14_R1;

import java.util.HashMap;

import net.minecraft.server.v1_14_R1.MaterialMapColor;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.IBlockAccess;
import net.minecraft.server.v1_14_R1.IBlockData;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftMagicNumbers;

import de.craftlancer.serverminimap.nmscompat.INMSHandler;
import de.craftlancer.serverminimap.nmscompat.MaterialMapColorInterface;

public class NMSHandler implements INMSHandler
{
    private HashMap<MaterialMapColor, MaterialMapColorWrapper> colors = new HashMap<MaterialMapColor, MaterialMapColorWrapper>();
    {
        for(MaterialMapColor color : MaterialMapColor.a)
            if(color != null)
                colors.put(color, new MaterialMapColorWrapper(color));
    }
    
    @Override
    public MaterialMapColorInterface getColorNeutral()
    {
        return colors.get(MaterialMapColor.b);
    }
    
    @Override
    public MaterialMapColorInterface getBlockColor(Block block)
    {
        net.minecraft.server.v1_14_R1.Block nmsblock = CraftMagicNumbers.getBlock(block.getType());        
        org.bukkit.craftbukkit.v1_14_R1.CraftChunk w = (org.bukkit.craftbukkit.v1_14_R1.CraftChunk) block.getChunk();
        
        BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
        IBlockData data = w.getHandle().getType(pos);
        IBlockAccess access = w.getHandle().getWorld();
        
        @SuppressWarnings("deprecation")
        MaterialMapColor nms = nmsblock.e(data, access, pos);
        
        if (!colors.containsKey(nms))
            Bukkit.getLogger().severe("[ServerMinimap] unknown color, error in NMSHandler - please report to author!");
        
        return colors.get(nms);
    }

    @Override
    public boolean hasTwoHands() {
        return true;
    }
}
