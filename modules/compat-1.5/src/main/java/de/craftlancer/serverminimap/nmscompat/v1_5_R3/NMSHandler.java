package de.craftlancer.serverminimap.nmscompat.v1_5_R3;

import java.util.HashMap;

import net.minecraft.server.v1_5_R3.MaterialMapColor;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import de.craftlancer.serverminimap.nmscompat.INMSHandler;
import de.craftlancer.serverminimap.nmscompat.MaterialMapColorInterface;
import de.craftlancer.serverminimap.nmscompat.v1_5_R3.MaterialMapColorWrapper;

public class NMSHandler implements INMSHandler
{
    private HashMap<MaterialMapColor, MaterialMapColorWrapper> colors = new HashMap<MaterialMapColor, MaterialMapColorWrapper>();
    {
        colors.put(MaterialMapColor.b, new MaterialMapColorWrapper(MaterialMapColor.b));
        colors.put(MaterialMapColor.c, new MaterialMapColorWrapper(MaterialMapColor.c));
        colors.put(MaterialMapColor.d, new MaterialMapColorWrapper(MaterialMapColor.d));
        colors.put(MaterialMapColor.e, new MaterialMapColorWrapper(MaterialMapColor.e));
        colors.put(MaterialMapColor.f, new MaterialMapColorWrapper(MaterialMapColor.f));
        colors.put(MaterialMapColor.g, new MaterialMapColorWrapper(MaterialMapColor.g));
        colors.put(MaterialMapColor.h, new MaterialMapColorWrapper(MaterialMapColor.h));
        colors.put(MaterialMapColor.i, new MaterialMapColorWrapper(MaterialMapColor.i));
        colors.put(MaterialMapColor.j, new MaterialMapColorWrapper(MaterialMapColor.j));
        colors.put(MaterialMapColor.k, new MaterialMapColorWrapper(MaterialMapColor.k));
        colors.put(MaterialMapColor.l, new MaterialMapColorWrapper(MaterialMapColor.l));
        colors.put(MaterialMapColor.m, new MaterialMapColorWrapper(MaterialMapColor.m));
        colors.put(MaterialMapColor.n, new MaterialMapColorWrapper(MaterialMapColor.n));
        colors.put(MaterialMapColor.o, new MaterialMapColorWrapper(MaterialMapColor.o));
    }
    
    @Override
    public MaterialMapColorInterface getColorNeutral()
    {
        return colors.get(MaterialMapColor.b);
    }
    
    @Override
    public MaterialMapColorInterface getBlockColor(Block b)
    {
        net.minecraft.server.v1_5_R3.Block block = net.minecraft.server.v1_5_R3.Block.byId[b.getTypeId()];
        
        if (block == null || block.material == null)
            return getColorNeutral();
        
        MaterialMapColor nms = block.material.G;
        
        if (!colors.containsKey(nms))
            Bukkit.getLogger().severe("[ServerMinimap] unknown color, error in NMSHandler - please report to author!");
        
        return colors.get(nms);
    }

    @Override
    public boolean hasTwoHands() {
        return false;
    }
}
