package de.craftlancer.minimap;

import java.util.LinkedList;
import java.util.Map;
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

public class AlternativeRenderer extends MapRenderer implements Listener
{
    private Map<Integer, Map<Integer, MapChunk>> cacheMap = new TreeMap<Integer, Map<Integer, MapChunk>>();
    protected Queue<Coords> queue = new LinkedList<Coords>();
    private RenderTask cacheTask = new RenderTask(this);
    private SendTask sendTask = new SendTask();
    
    protected int scale = 0;
    protected int cpr = 0;
    private int colorlimit;
    protected Minimap plugin;
    private World world;
    
    public AlternativeRenderer(int scale, int cpr, World world, Minimap plugin)
    {
        super(true);
        
        this.plugin = plugin;
        this.cpr = cpr;
        this.world = world;
                
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        this.scale = (scale < 1 || scale > 4) ? 1 : (int) Math.pow(2, scale);
        this.colorlimit = (this.scale * this.scale) / 2;        

        this.cacheTask.runTaskTimer(plugin, plugin.runPerTicks, plugin.runPerTicks);
        this.sendTask.runTaskTimer(plugin, plugin.fastTicks, plugin.fastTicks);
    }
    
    @Override
    public void render(MapView map, MapCanvas canvas, Player player)
    {
        if (!player.getWorld().equals(world))
            return;
        
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
                
                try
                {
                    canvas.setPixel(i, j, (byte) (cacheMap.get(x).get(z).get(Math.abs((locX + i + 16 * Math.abs(x))) % 16, Math.abs((locZ + j + 16 * Math.abs(z))) % 16) % 55));
                }
                catch (NullPointerException e)
                {
                    canvas.setPixel(i, j, (byte) 0);
                    if (queue.size() < 500)
                        addToQueue(x, z, true);
                }
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
            
            byte color = getPlayerColor(player, p);
            
            cursors.addCursor(x, z, direction, color);
        }
    }
    
    @SuppressWarnings("static-method")
    private byte getPlayerColor(Player player, Player p)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public void addToQueue(int x, int y, boolean chuck)
    {
        Coords c = new Coords(x, y, chuck);
        if (!queue.contains(c))
            queue.offer(c);
    }
    
    public void loadData(int x, int z)
    {
        if (!cacheMap.containsKey(x))
            cacheMap.put(x, new TreeMap<Integer, MapChunk>());
        
        if (!cacheMap.get(x).containsKey(z))
            cacheMap.get(x).put(z, new MapChunk());
        
        MapChunk map = cacheMap.get(x).get(z);
        
        int initX = x * scale * 16;
        int initZ = z * scale * 16;
        
        for (int i = 0; i < 16; i++)
            for (int j = 0; j < 16; j++)
                map.set(i, j, renderBlock(initX + i * scale, initZ + j * scale));
    }
    
    public void loadBlock(int initX, int initZ)
    {
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
            cacheMap.put(x, new TreeMap<Integer, MapChunk>());
        
        if (!cacheMap.get(x).containsKey(z))
            cacheMap.get(x).put(z, new MapChunk());
        
        MapChunk map = cacheMap.get(x).get(z);
        map.set(sx, sz, renderBlock((x * 16 + sx) * scale, (z * 16 + sz) * scale));
    }
    
    public byte renderBlock(int baseX, int baseZ)
    {
        byte arr[] = new byte[56];
        byte maxpos = 0;
        byte max = 0;
        
        for (int k = 0; k < scale; k++)
            for (int l = 0; l < scale; l++)
            {
                Block b = world.getHighestBlockAt(baseX + k, baseZ + l);
                if(b.getChunk().isLoaded())
                    b.getChunk().load();
                
                byte color = plugin.getColor(b.getRelative(0, -1, 0).getTypeId());
                arr[color]++;
                if (arr[color] >= colorlimit)
                    return color;
            }
        
        if (maxpos == 0)
            for (byte h = 0; h < arr.length; h++)
                if (arr[h] > max)
                {
                    max = arr[h];
                    maxpos = h;
                }
        
        return maxpos;
    }
    
    private boolean canSee(Player viewer, Player p)
    {
        return viewer.getName().equals(p.getName()) || plugin.canSeeOthers;
    }
    
    private void handleBlockEvent(Block e)
    {
        Location loc = e.getLocation();
        if (loc.getBlockY() >= loc.getWorld().getHighestBlockYAt(loc) - 1)
            addToQueue(loc.getBlockX(), loc.getBlockZ(), false);
    }
    
    // TOTEST every Event handled?
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
            case STATIONARY_LAVA:
            case STATIONARY_WATER:
                handleBlockEvent(e.getBlock()); break;
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
