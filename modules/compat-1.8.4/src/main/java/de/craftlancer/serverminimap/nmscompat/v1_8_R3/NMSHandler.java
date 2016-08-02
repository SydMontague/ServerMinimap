package de.craftlancer.serverminimap.nmscompat.v1_8_R3;

import java.util.HashMap;

import net.minecraft.server.v1_8_R3.MaterialMapColor;
import net.minecraft.server.v1_8_R3.BlockPosition;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;

import de.craftlancer.serverminimap.nmscompat.INMSHandler;
import de.craftlancer.serverminimap.nmscompat.MaterialMapColorInterface;

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
        colors.put(MaterialMapColor.p, new MaterialMapColorWrapper(MaterialMapColor.p));
        colors.put(MaterialMapColor.q, new MaterialMapColorWrapper(MaterialMapColor.q));
        colors.put(MaterialMapColor.r, new MaterialMapColorWrapper(MaterialMapColor.r));
        colors.put(MaterialMapColor.s, new MaterialMapColorWrapper(MaterialMapColor.s));
        colors.put(MaterialMapColor.t, new MaterialMapColorWrapper(MaterialMapColor.t));
        colors.put(MaterialMapColor.u, new MaterialMapColorWrapper(MaterialMapColor.u));
        colors.put(MaterialMapColor.v, new MaterialMapColorWrapper(MaterialMapColor.v));
        colors.put(MaterialMapColor.w, new MaterialMapColorWrapper(MaterialMapColor.w));
        colors.put(MaterialMapColor.x, new MaterialMapColorWrapper(MaterialMapColor.x));
        colors.put(MaterialMapColor.y, new MaterialMapColorWrapper(MaterialMapColor.y));
        colors.put(MaterialMapColor.z, new MaterialMapColorWrapper(MaterialMapColor.z));
        colors.put(MaterialMapColor.A, new MaterialMapColorWrapper(MaterialMapColor.A));
        colors.put(MaterialMapColor.B, new MaterialMapColorWrapper(MaterialMapColor.B));
        colors.put(MaterialMapColor.C, new MaterialMapColorWrapper(MaterialMapColor.C));
        colors.put(MaterialMapColor.D, new MaterialMapColorWrapper(MaterialMapColor.D));
        colors.put(MaterialMapColor.E, new MaterialMapColorWrapper(MaterialMapColor.E));
        colors.put(MaterialMapColor.F, new MaterialMapColorWrapper(MaterialMapColor.F));
        colors.put(MaterialMapColor.G, new MaterialMapColorWrapper(MaterialMapColor.G));
        colors.put(MaterialMapColor.H, new MaterialMapColorWrapper(MaterialMapColor.H));
        colors.put(MaterialMapColor.I, new MaterialMapColorWrapper(MaterialMapColor.I));
        colors.put(MaterialMapColor.J, new MaterialMapColorWrapper(MaterialMapColor.J));
        colors.put(MaterialMapColor.K, new MaterialMapColorWrapper(MaterialMapColor.K));
    }
    
    @Override
    public MaterialMapColorInterface getColorNeutral()
    {
        return colors.get(MaterialMapColor.b);
    }
    
    @Override
    public MaterialMapColorInterface getBlockColor(Block block)
    {
        net.minecraft.server.v1_8_R3.Block nmsblock = CraftMagicNumbers.getBlock(block);
        org.bukkit.craftbukkit.v1_8_R3.CraftChunk w = (org.bukkit.craftbukkit.v1_8_R3.CraftChunk) block.getChunk();
        
        MaterialMapColor nms = nmsblock.g(w.getHandle().getBlockData(new BlockPosition(block.getX(), block.getY(), block.getZ())));
        
        if (!colors.containsKey(nms))
            Bukkit.getLogger().severe("[ServerMinimap] unknown color, error in NMSHandler - please report to author!");
        
        return colors.get(nms);
    }

    @Override
    public boolean hasTwoHands() {
        return false;
    }
}
