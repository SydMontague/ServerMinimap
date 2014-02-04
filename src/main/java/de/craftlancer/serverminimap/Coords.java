package de.craftlancer.serverminimap;

public class Coords
{
    private int x;
    private int z;
    private boolean chunk;
    private String world;
    
    public Coords(int x, int z, boolean chunk, String world)
    {
        this.x = x;
        this.z = z;
        this.chunk = chunk;
        this.world = world;
    }
    
    public boolean isChunk()
    {
        return chunk;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getZ()
    {
        return z;
    }
    
    public String getWorld()
    {
        return world;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (chunk ? 1231 : 1237);
        result = prime * result + ((world == null) ? 0 : world.hashCode());
        result = prime * result + x;
        result = prime * result + z;
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Coords))
            return false;
        Coords other = (Coords) obj;
        if (chunk != other.chunk)
            return false;
        if (world == null)
        {
            if (other.world != null)
                return false;
        }
        else if (!world.equals(other.world))
            return false;
        if (x != other.x)
            return false;
        if (z != other.z)
            return false;
        return true;
    }
}
