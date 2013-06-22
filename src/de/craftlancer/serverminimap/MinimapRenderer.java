package de.craftlancer.serverminimap;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

@Deprecated
class MinimapRenderer extends MapRenderer implements Listener
{    
    Map<String, Integer> useMap = new HashMap<String, Integer>();
    
    int scale = 0;
    int lpr = 1;
    ServerMinimap plugin;
    
    public MinimapRenderer(int scale, ServerMinimap plugin, int linesperrun)
    {
        super(true);
        
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        if (linesperrun < 1)
            lpr = 1;
        else if (linesperrun > 128)
            lpr = 128;
        else
            lpr = linesperrun;
        
        this.scale = (scale < 1 || scale > 4) ? 1 : (int) Math.pow(2, scale);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        useMap.put(e.getPlayer().getName(), 0);
    }
    
    @EventHandler
    public void onPlayerLeft(PlayerQuitEvent e)
    {
        useMap.remove(e.getPlayer().getName());
    }
    
    @Override
    public void render(MapView map, MapCanvas canvas, Player player)
    {
        String name = player.getName();
        
        if (!useMap.containsKey(name))
            return;
        
        int run = useMap.get(name);
        
        int initX = player.getLocation().getBlockX() - (64 * scale);
        int initZ = player.getLocation().getBlockZ() - (64 * scale);
        int colorlimit = (scale * scale) / 2;
        
        for (int i = lpr * (run % (128 / lpr)); i < lpr * (run % (128 / lpr)) + lpr; i++)
            for (int j = 0; j < 128; j++)
            {
                if (i > 128)
                    break;
                
                byte arr[] = new byte[56];
                byte maxpos = 0;
                byte max = 0;
                
                int baseX = initX + i * scale;
                int baseZ = initZ + j * scale;
                
                for (int k = 0; k < scale; k++)
                    for (int l = 0; l < scale; l++)
                    {
                        byte color = plugin.getColor(player.getWorld().getHighestBlockAt(baseX + k, baseZ + l).getRelative(0, -1, 0).getTypeId());
                        arr[color]++;
                        if (arr[color] >= colorlimit)
                        {
                            maxpos = color;
                            break;
                        }
                    }
                
                if (maxpos == 0)
                    for (byte h = 0; h < arr.length; h++)
                        if (arr[h] > max)
                        {
                            max = arr[h];
                            maxpos = h;
                        }
                
                canvas.setPixel(i, j, maxpos);
            }
        
        MapCursorCollection cursors = canvas.getCursors();
        while (cursors.size() > 0)
            cursors.removeCursor(cursors.getCursor(0));
        
        for (Player p : plugin.getServer().getOnlinePlayers())
        {
            if (!canSee(player, p))
                continue;
            
            float yaw = p.getLocation().getYaw();
            if (yaw < 0)
                yaw += 360;
            
            byte direction = (byte) ((Math.abs(yaw) + 11.25) / 22.5);
            if (direction > 15)
                direction = 0;
            
            int x = ((p.getLocation().getBlockX() - player.getLocation().getBlockX()) / scale) * 2;
            int z = ((p.getLocation().getBlockZ() - player.getLocation().getBlockZ()) / scale) * 2;
            
            if (Math.abs(x) > 128 || Math.abs(z) > 128)
                continue;
            
            byte color = 0; // getColor(Player viewer, player);
            
            cursors.addCursor(x, z, direction, color);
        }
                
        if (useMap.containsKey(name))
            useMap.put(name, useMap.get(name) + 1);
    }
    
    private boolean canSee(Player viewer, Player p)
    {
        return viewer.getName().equals(p.getName()) || plugin.canSeeOthers;
    }
}
