package de.craftlancer.serverminimap;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MinimapCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3)
    {
        if (sender instanceof Player && !sender.hasPermission("minimap.command.minimap"))
        {
            sender.sendMessage("You are not able to run this command!");
            return true;
        }
        
        if (!((Player) sender).getInventory().addItem(new ItemStack(Material.MAP, 1, ServerMinimap.MAPID)).isEmpty())
            sender.sendMessage("Could not give you the map. Maybe your inventory is full?");
        else
            sender.sendMessage("There is your Map.");
        
        return true;
    }
    
}
