package de.craftlancer.serverminimap.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.craftlancer.serverminimap.ExtraCursor;

/**
 * This event allows the add own markers to the minimap and is called every
 * tick.
 */
public class MinimapExtraCursorEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private List<ExtraCursor> cursor = new ArrayList<ExtraCursor>();
    
    public MinimapExtraCursorEvent(Player player)
    {
        this.player = player;
    }
    
    /**
     * Get all already added cursors.
     *
     * @return A mutable list of all added cursors.
     */
    public List<ExtraCursor> getCursors()
    {
        return cursor;
    }
    
    /**
     * Get the player for whom this event is called.
     *
     * @return the Player
     */
    public Player getPlayer()
    {
        return player;
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
}
