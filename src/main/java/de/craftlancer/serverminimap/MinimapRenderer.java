package de.craftlancer.serverminimap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import de.craftlancer.serverminimap.event.MinimapExtraCursorEvent;
import de.craftlancer.serverminimap.event.MinimapPlayerCursorEvent;
import de.craftlancer.serverminimap.nmscompat.MaterialMapColorInterface;

public class MinimapRenderer extends MapRenderer implements Listener
{
    private Map<String, Map<Integer, Map<Integer, MapChunk>>> worldCacheMap = new TreeMap<String, Map<Integer, Map<Integer, MapChunk>>>();
    protected Queue<Coords> queue = new LinkedList<Coords>();
    private RenderTask cacheTask = new RenderTask(this);
    private SendTask sendTask = new SendTask();
    
    private int globalScale = 0;
    private int cpr = 0;
    private int colorlimit;
    private ServerMinimap plugin;
    
    public MinimapRenderer(int scale, int cpr, ServerMinimap plugin)
    {
        super(true);
        
        this.plugin = plugin;
        this.cpr = cpr;
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        globalScale = scale < 1 ? 1 : scale;
        colorlimit = (globalScale * globalScale) / 2;
        
        cacheTask.runTaskTimer(plugin, plugin.getRunPerTicks(), plugin.getRunPerTicks());
        sendTask.runTaskTimer(plugin, plugin.getFastTicks(), plugin.getFastTicks());
    }
    
    public int getDefaultScale()
    {
        return globalScale;
    }
    
    public int getChunksPerRun()
    {
        return cpr;
    }
    
    public Queue<Coords> getQueue()
    {
        return queue;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void render(MapView map, MapCanvas canvas, Player player)
    {        
        if (player.getWorld().hasMetadata("minimap.disabled"))
            return;
        
        if (!worldCacheMap.containsKey(player.getWorld().getName()))
            worldCacheMap.put(player.getWorld().getName(), new TreeMap<Integer, Map<Integer, MapChunk>>());
        
        Map<Integer, Map<Integer, MapChunk>> cacheMap = worldCacheMap.get(player.getWorld().getName());
        
        int scale = player.getWorld().hasMetadata("minimap.scale") && !player.getWorld().getMetadata("minimap.scale").isEmpty() ? player.getWorld().getMetadata("minimap.scale").get(0).asInt() : getDefaultScale();
        
        int locX = player.getLocation().getBlockX() / scale - 64;
        int locZ = player.getLocation().getBlockZ() / scale - 64;
        
        for (int i = 0; i < 128; i++)
            for (int j = 0; j < 128; j++)
            {
                int x = (int) ((locX + i) / 16D);
                if (locX + i < 0 && (locX + i) % 16 != 0)
                    x--;
                int z = (int) ((locZ + j) / 16D);
                if (locZ + j < 0 && (locZ + j) % 16 != 0)
                    z--;
                
                if (cacheMap.containsKey(x) && cacheMap.get(x).containsKey(z))
                {
                    MaterialMapColorInterface color = cacheMap.get(x).get(z).get(Math.abs((locX + i + 16 * Math.abs(x))) % 16, Math.abs((locZ + j + 16 * Math.abs(z))) % 16);
                    short avgY = cacheMap.get(x).get(z).getY(Math.abs((locX + i + 16 * Math.abs(x))) % 16, Math.abs((locZ + j + 16 * Math.abs(z))) % 16);
                    short prevY = getPrevY(x, z, Math.abs((locX + i + 16 * Math.abs(x))) % 16, Math.abs((locZ + j + 16 * Math.abs(z))) % 16, player.getWorld().getName());
                    
                    double d2 = (avgY - prevY) * 4.0D / (scale + 4) + ((i + j & 1) - 0.5D) * 0.4D;
                    byte b0 = 1;
                    
                    if (d2 > 0.6D)
                        b0 = 2;
                    if (d2 < -0.6D)
                        b0 = 0;
                    
                    canvas.setPixel(i, j, (byte) (color.getM() * 4 + b0));
                }
                else
                {
                    canvas.setPixel(i, j, (byte) 0);
                    if (queue.size() < 200)
                        addToQueue(x, z, true, player.getWorld().getName());
                    else
                        break;
                }
            }
        
        MapCursorCollection cursors = canvas.getCursors();
        while (cursors.size() > 0)
            cursors.removeCursor(cursors.getCursor(0));
        
        MinimapExtraCursorEvent e = new MinimapExtraCursorEvent(player);
        plugin.getServer().getPluginManager().callEvent(e);
        
        for (ExtraCursor c : e.getCursors())
        {
            if (!c.getWorld().equalsIgnoreCase(player.getWorld().getName()))
                continue;
            
            int x = ((c.getX() - player.getLocation().getBlockX()) / scale) * 2;
            int z = ((c.getZ() - player.getLocation().getBlockZ()) / scale) * 2;
            
            if (Math.abs(x) > 127)
                if (c.isShownOutside())
                    x = c.getX() > player.getLocation().getBlockX() ? 127 : -128;
                else
                    continue;
            
            if (Math.abs(z) > 127)
                if (c.isShownOutside())
                    z = c.getZ() > player.getLocation().getBlockZ() ? 127 : -128;
                else
                    continue;
            
            cursors.addCursor(x, z, c.getDirection(), c.getType().getValue(), c.isVisible());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCursor(MinimapExtraCursorEvent e)
    {
        Player player = e.getPlayer();
        
        for (Player p : plugin.getServer().getOnlinePlayers())
        {
            if (!p.getWorld().equals(player.getWorld()))
                continue;
            
            float yaw = p.getLocation().getYaw();
            if (yaw < 0)
                yaw += 360;
            
            byte direction = (byte) ((Math.abs(yaw) + 11.25) / 22.5);
            if (direction > 15)
                direction = 0;
            
            int x = p.getLocation().getBlockX();
            int z = p.getLocation().getBlockZ();
            
            MinimapPlayerCursorEvent event = new MinimapPlayerCursorEvent(player, p, plugin.canSeeOthers());
            plugin.getServer().getPluginManager().callEvent(event);
            
            e.getCursors().add(new ExtraCursor(x, z, player == p || event.isCursorShown(), event.getType(), direction, p.getWorld().getName(), false));
        }
    }
    
    public void addToQueue(int x, int y, boolean chunk, String world)
    {
        Coords c = new Coords(x, y, chunk, world);
        if (!queue.contains(c))
            queue.offer(c);
    }
    
    public void loadData(int x, int z, String world)
    {
        if (!worldCacheMap.containsKey(world))
            worldCacheMap.put(world, new TreeMap<Integer, Map<Integer, MapChunk>>());
        
        World w = plugin.getServer().getWorld(world);
        int scale = w.hasMetadata("minimap.scale") ? w.getMetadata("minimap.scale").get(0).asInt() : getDefaultScale();
        
        Map<Integer, Map<Integer, MapChunk>> cacheMap = worldCacheMap.get(world);
        
        if (!cacheMap.containsKey(x))
            cacheMap.put(x, new TreeMap<Integer, MapChunk>());
        
        if (!cacheMap.get(x).containsKey(z))
            cacheMap.get(x).put(z, new MapChunk(plugin));
        
        MapChunk map = cacheMap.get(x).get(z);
        
        int initX = x * scale * 16;
        int initZ = z * scale * 16;
        
        for (int i = 0; i < 16; i++)
            for (int j = 0; j < 16; j++)
                map.set(i, j, renderBlock(initX + i * scale, initZ + j * scale, world));
    }
    
    private short getPrevY(int x, int z, int i, int j, String world)
    {
        
        Map<Integer, Map<Integer, MapChunk>> cacheMap = worldCacheMap.get(world);
        
        j--;
        
        if (j < 0)
        {
            z--;
            j = 15;
        }
        
        if (cacheMap.containsKey(x) && cacheMap.get(x).containsKey(z))
            return cacheMap.get(x).get(z).getY(i, j);
        
        return 0;
    }
    
    public void loadBlock(int initX, int initZ, String world)
    {
        if (!worldCacheMap.containsKey(world))
            worldCacheMap.put(world, new TreeMap<Integer, Map<Integer, MapChunk>>());
        
        Map<Integer, Map<Integer, MapChunk>> cacheMap = worldCacheMap.get(world);
        
        World w = plugin.getServer().getWorld(world);
        int scale = w.hasMetadata("minimap.scale") && !w.getMetadata("minimap.scale").isEmpty() ? w.getMetadata("minimap.scale").get(0).asInt() : getDefaultScale();
        
        int locX = initX / scale;
        int locZ = initZ / scale;
        
        int x = (int) (locX / 16D);
        if (locX < 0 && locX % 16 != 0)
            x--;
        int z = (int) (locZ / 16D);
        if (locZ < 0 && locZ % 16 != 0)
            z--;
        
        int sx = Math.abs((locX + 16 * Math.abs(x))) % 16;
        int sz = Math.abs((locZ + 16 * Math.abs(z))) % 16;
        
        if (!cacheMap.containsKey(x))
            return;
        
        if (!cacheMap.get(x).containsKey(z))
            return;
        
        MapChunk map = cacheMap.get(x).get(z);
        map.set(sx, sz, renderBlock((x * 16 + sx) * scale, (z * 16 + sz) * scale, world));
    }
    
    public RenderResult renderBlock(int baseX, int baseZ, String strworld)
    {
        Map<MaterialMapColorInterface, Integer> colors = new HashMap<MaterialMapColorInterface, Integer>();
        short avgY = 0;
        MaterialMapColorInterface mainColor = null;
        World world = plugin.getServer().getWorld(strworld);
        int scale = world.hasMetadata("minimap.scale") && !world.getMetadata("minimap.scale").isEmpty() ? world.getMetadata("minimap.scale").get(0).asInt() : getDefaultScale();
        
        boolean changedHeight = world.hasMetadata("minimap.drawheight") && !world.getMetadata("minimap.drawheight").isEmpty();
        int y = changedHeight ? world.getMetadata("minimap.drawheight").get(0).asInt() : 0;
        
        for (int k = 0; k < scale; k++)
            for (int l = 0; l < scale; l++)
            {
                if (!changedHeight)
                    y = world.getHighestBlockYAt(baseX + k, baseZ + l) + 1;
                
                Block b = world.getBlockAt(baseX + k, y, baseZ + l);
                
                if (!b.getChunk().isLoaded())
                    b.getChunk().load();
                
                while (b.getY() > 0 && plugin.getNMSHandler().getBlockColor(b) == plugin.getNMSHandler().getColorNeutral())
                    b = world.getBlockAt(b.getX(), b.getY() - 1, b.getZ());
                
                avgY += b.getY();
                
                if (mainColor == null)
                {
                    MaterialMapColorInterface color = plugin.getNMSHandler().getBlockColor(b);
                    int value = colors.containsKey(color) ? colors.get(color) + 1 : 1;
                    colors.put(color, value);
                    
                    if (colors.get(color) >= colorlimit)
                        mainColor = color;
                }
            }
        
        avgY /= scale;
        
        if (mainColor == null)
        {
            int max = 0;
            for (Entry<MaterialMapColorInterface, Integer> c : colors.entrySet())
                if (c.getValue() > max)
                {
                    max = c.getValue();
                    mainColor = c.getKey();
                }
        }
        
        // d0 - previous d1
        // d1 - average height
        // i - 1 << scale == (2^scale)
        // k1 - x iterator (map x)
        // j2 - z iterator (map z)
        // i4 - height of liquid divided by i*i
        // double d2 = (d1 - d0) * 4.0D / (double) (i + 4) + ((double) (k1 + j2
        // & 1) - 0.5D) * 0.4D;
        // d2 = (double) i4 * 0.1D + (double) (k1 + j2 & 1) * 0.2D;
        return new RenderResult(mainColor, avgY);
    }
    
    private void handleBlockEvent(Block e)
    {
        Location loc = e.getLocation();
        if (loc.getBlockY() >= loc.getWorld().getHighestBlockYAt(loc) - 1)
            addToQueue(loc.getBlockX(), loc.getBlockZ(), false, e.getWorld().getName());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockEvent(BlockPlaceEvent e)
    {
        handleBlockEvent(e.getBlock());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockEvent(BlockFromToEvent e)
    {
        handleBlockEvent(e.getBlock());
        handleBlockEvent(e.getToBlock());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockEvent(BlockPhysicsEvent e)
    {
        switch (e.getChangedType())
        {
            case LAVA:
            case WATER:
                handleBlockEvent(e.getBlock());
                break;
            default:
                break;
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockEvent(BlockBreakEvent e)
    {
        handleBlockEvent(e.getBlock());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockEvent(BlockBurnEvent e)
    {
        handleBlockEvent(e.getBlock());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockEvent(BlockFadeEvent e)
    {
        handleBlockEvent(e.getBlock());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockEvent(BlockFormEvent e)
    {
        handleBlockEvent(e.getBlock());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockEvent(BlockGrowEvent e)
    {
        handleBlockEvent(e.getBlock());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockEvent(BlockSpreadEvent e)
    {
        handleBlockEvent(e.getBlock());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockEvent(EntityBlockFormEvent e)
    {
        handleBlockEvent(e.getBlock());
    }
}
