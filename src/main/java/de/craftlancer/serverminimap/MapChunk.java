package de.craftlancer.serverminimap;

import de.craftlancer.serverminimap.nmscompat.MaterialMapColorInterface;

public class MapChunk
{
    private MaterialMapColorInterface cache[][] = new MaterialMapColorInterface[16][16];
    private short avgY[][] = new short[16][16];
    
    public MapChunk(ServerMinimap plugin)
    {
        for (int i = 0; i < 16; i++)
            for (int j = 0; j < 16; j++)
            {
                cache[i][j] = plugin.getNMSHandler().getColorNeutral();
                avgY[i][j] = 64;
            }
    }
    
    public void set(int i, int j, RenderResult value)
    {
        cache[i][j] = value.getColor();
        avgY[i][j] = value.getAverageY();
    }
    
    public MaterialMapColorInterface get(int i, int j)
    {
        return cache[i][j];
    }
    
    public short getY(int i, int j)
    {
        return avgY[i][j];
    }
}
