package de.craftlancer.serverminimap;

import org.bukkit.scheduler.BukkitRunnable;

public class RenderTask extends BukkitRunnable
{
    AlternativeRenderer renderer;
    
    public RenderTask(AlternativeRenderer renderer)
    {
        this.renderer = renderer;
    }
    
    @Override
    public void run()
    {
        int chunks = 0;
        int blocks = 0;
        
        while (chunks < renderer.cpr)
        {
            Coords c = renderer.queue.poll();
            if (c == null)
                break;
            
            if (c.isChunk())
            {
                renderer.loadData(c.x, c.z);
                chunks++;
            }
            else
            {
                renderer.loadBlock(c.x, c.z);
                blocks++;
                if(blocks >= 16 * 16 * renderer.scale * renderer.scale)
                {
                    blocks = 0;
                    chunks++;
                }
            }
            
        }
    }
    
}
