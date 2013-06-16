package de.craftlancer.minimap;

public class MapChunk
{
    byte cache[][] = new byte[16][16];
    
    public MapChunk()
    {
        for (int i = 0; i < 16; i++)
            for (int j = 0; j < 16; j++)
                cache[i][j] = -1;
    }
    
    public void set(int i, int j, byte value)
    {
        cache[i][j] = value;
    }
    
    public byte get(int i, int j)
    {
        return cache[i][j];
    }
}
