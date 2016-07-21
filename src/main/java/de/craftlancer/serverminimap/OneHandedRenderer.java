package de.craftlancer.serverminimap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

public class OneHandedRenderer extends MinimapRenderer {

    public OneHandedRenderer(int scale, int cpr, ServerMinimap plugin) {
        super(scale, cpr, plugin);
    }
    
    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        if (!(player.getItemInHand().getType() == Material.MAP && player.getItemInHand().getDurability() == ServerMinimap.MAPID))
            return;
        
        super.render(map, canvas, player);
    }
}
