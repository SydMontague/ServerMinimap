package de.craftlancer.serverminimap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

public class TwoHandedRenderer extends MinimapRenderer {

    public TwoHandedRenderer(int scale, int cpr, ServerMinimap plugin) {
        super(scale, cpr, plugin);
    }
    
    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        
        if (!(main.getType() == Material.MAP && main.getDurability() == ServerMinimap.MAPID) &&
             !(off.getType() == Material.MAP && off.getDurability() == ServerMinimap.MAPID))
            return;
        
        super.render(map, canvas, player);
    }
}
