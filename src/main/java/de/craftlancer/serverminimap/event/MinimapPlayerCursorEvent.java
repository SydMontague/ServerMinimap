package de.craftlancer.serverminimap.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.map.MapCursor.Type;

public class MinimapPlayerCursorEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    
    private Player viewer;
    private Player viewed;
    private Type type;
    private boolean display;
    
    public MinimapPlayerCursorEvent(Player viewer, Player viewed, boolean canSee)
    {
        this.viewed = viewed;
        this.viewer = viewer;
        display = canSee;
        setType(Type.WHITE_POINTER);
    }
    
    /**
     * Get the viewing player.
     *
     * @return the player who holds the minimap
     */
    public Player getViewer()
    {
        return viewer;
    }
    
    /**
     * Get the player which shall be shown on the minimap.
     *
     * @return the player which shall be on the minimap
     */
    public Player getViewed()
    {
        return viewed;
    }
    
    /**
     * Get the type of cursor, the viewer sees for the viewed player.
     *
     * @return the cursor type
     */
    public Type getType()
    {
        return type;
    }
    
    /**
     * Set the type of cursor, the viewer sees for the viewed player.
     *
     * @param type the type of cursor
     */
    public void setType(Type type)
    {
        this.type = type;
    }
    
    /**
     * Get if the viewing player get a cursor displayed on his map for the
     * viewed player.
     *
     * @return true if shown, false if not
     */
    public boolean isCursorShown()
    {
        return display;
    }
    
    /**
     * Set whether the viewer gets a cursor for the viewed player on his map.
     *
     * @param display true if he shall see him, false if not
     */
    public void setCursorShown(boolean display)
    {
        this.display = display;
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
