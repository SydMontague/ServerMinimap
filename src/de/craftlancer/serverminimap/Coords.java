package de.craftlancer.serverminimap;

public class Coords
{
    int x;
    int z;
    boolean chunk;
    
    public Coords(int x, int z, boolean chunk)
    {
        this.x = x;
        this.z = z;
        this.chunk = chunk;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (chunk ? 1231 : 1237);
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
        if (x != other.x)
            return false;
        if (z != other.z)
            return false;
        
        return true;
    }
    
    public boolean isChuck()
    {
        return chunk;
    }
}
