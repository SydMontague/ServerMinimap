package de.craftlancer.serverminimap;

import org.bukkit.scheduler.BukkitRunnable;

public class RenderTask extends BukkitRunnable
{
    private MinimapRenderer renderer;
    
    public RenderTask(MinimapRenderer renderer)
    {
        this.renderer = renderer;
    }
    
    @Override
    public void run()
    {
        int chunks = 0;
        int blocks = 0;
        
        while (chunks < renderer.getChunksPerRun())
        {
            Coords c = renderer.getQueue().poll();
            
            if (c == null)
                break;
            
            if (c.isChunk())
            {
                renderer.loadData(c.getX(), c.getZ(), c.getWorld());
                chunks++;
            }
            else
            {
                renderer.loadBlock(c.getX(), c.getZ(), c.getWorld());
                blocks++;
                if (blocks >= 16 * 16 * renderer.getDefaultScale() * renderer.getDefaultScale())
                {
                    blocks = 0;
                    chunks++;
                }
            }
        }
    }
    
}
