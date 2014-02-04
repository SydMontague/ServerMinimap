package de.craftlancer.serverminimap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

public class SendTask extends BukkitRunnable
{
    @SuppressWarnings("deprecation")
    @Override
    public void run()
    {
        for (Player p : Bukkit.getOnlinePlayers())
            if (p.hasPermission("minimap.fastupdate"))
                if (p.getItemInHand().getType() == Material.MAP && p.getItemInHand().getDurability() == ServerMinimap.MAPID)
                {
                    MapView map = Bukkit.getMap(ServerMinimap.MAPID);
                    if (map != null)
                        p.sendMap(map);
                }
    }
    
}
