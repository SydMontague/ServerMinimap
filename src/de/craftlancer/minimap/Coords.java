package de.craftlancer.minimap;

public class Coords
{
    int x;
    int z;
    boolean chuck;
    
    public Coords(int x, int z, boolean chuck)
    {
        this.x = x;
        this.z = z;
        this.chuck = chuck;
    }
    
    @Override
    public boolean equals(Object c)
    {
        return c instanceof Coords ? chuck == ((Coords) c).chuck && x == ((Coords) c).x && z == ((Coords) c).z : false;
    }
    
    public boolean isChuck()
    {
        return chuck;
    }
}
