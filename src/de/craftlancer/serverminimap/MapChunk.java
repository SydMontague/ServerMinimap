package de.craftlancer.serverminimap;

import net.minecraft.server.v1_7_R1.MaterialMapColor;

public class MapChunk
{
    private MaterialMapColor cache[][] = new MaterialMapColor[16][16];
    private short avgY[][] = new short[16][16];
    
    public MapChunk()
    {
        for (int i = 0; i < 16; i++)
            for (int j = 0; j < 16; j++)
            {
                cache[i][j] = MaterialMapColor.b;
                avgY[i][j] = 64;
            }
    }
    
    public void set(int i, int j, RenderResult value)
    {
        cache[i][j] = value.getColor();
        avgY[i][j] = value.getAverageY();
    }
    
    public MaterialMapColor get(int i, int j)
    {
        return cache[i][j];
    }
    
    public short getY(int i, int j)
    {
        return avgY[i][j];
    }
}
