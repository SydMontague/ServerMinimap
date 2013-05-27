package de.craftlancer.minimap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SendTask extends BukkitRunnable
{
    @Override
    public void run()
    {
        for (Player p : Bukkit.getOnlinePlayers())
            if (p.hasPermission("minimap.fastupdate"))
                if (p.getItemInHand().getType() == Material.MAP && p.getItemInHand().getDurability() == Minimap.MAPID)
                    p.sendMap(Bukkit.getMap((short) 0));
    }
    
}
