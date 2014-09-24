package de.craftlancer.serverminimap.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.map.MapCursor.Type;
import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.serverminimap.ExtraCursor;
import de.craftlancer.serverminimap.ServerMinimap;

public class MySQL implements DataHandler
{
    private ServerMinimap plugin;
    
    private String host;
    private int port;
    private String user;
    private String pass;
    private String database;
    private String prefix;
    
    private Connection conn;
    
    private PreparedStatement getstatement;
    PreparedStatement insertstatement;
    PreparedStatement removestatement;
    PreparedStatement updatestatement;
    
    public MySQL(ServerMinimap plugin, String host, int port, String user, String pass, String database, String prefix)
    {
        this.plugin = plugin;
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.database = database;
        this.prefix = prefix;
        
        String table = prefix + "waypoints";
        
        openConnection();
        try
        {
            getstatement = getConnection().prepareStatement("SELECT * FROM " + table);
            insertstatement = getConnection().prepareStatement("INSERT INTO " + table + " (player, x, z, world, visible) VALUES (?,?,?,?,?)");
            removestatement = getConnection().prepareStatement("DELETE FROM " + table + " WHERE player = ? AND x = ? AND  z = ? AND world = ?");
            updatestatement = getConnection().prepareStatement("UPDATE " + table + " SET visible = ? WHERE player = ? AND x = ? AND z = ? AND world = ?");
            if (!conn.getMetaData().getTables(null, null, table, null).next())
            {
                PreparedStatement st = getConnection().prepareStatement("CREATE TABLE " + table + " ( player varchar(36), x int, z int, world varchar(255), visible boolean )");
                st.execute();
                st.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    private void openConnection()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, pass);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private boolean checkConnection()
    {
        try
        {
            return conn.isValid(1);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    private Connection getConnection()
    {
        if (checkConnection())
            openConnection();
        
        return conn;
    }
    
    @Override
    public Map<UUID, List<ExtraCursor>> loadWaypoints()
    {
        try
        {
            Map<UUID, List<ExtraCursor>> waypoints = new HashMap<UUID, List<ExtraCursor>>();
            ResultSet rs = getstatement.executeQuery();
            
            while (rs.next())
            {
                boolean deleteOld = false;
                String player = rs.getString("player");
                
                UUID uuid = null;
                try
                {
                    uuid = UUID.fromString(player);
                }
                catch (IllegalArgumentException e)
                {
                    uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
                    
                    if (uuid != null)
                        deleteOld = true;
                    else
                    {
                        plugin.getLogger().warning("Could not resolve UUID for " + player + "! The waypoint data of this key might be lost!");
                        continue;
                    }
                }
                
                int x = rs.getInt("x");
                int z = rs.getInt("z");
                String world = rs.getString("world");
                boolean visible = rs.getBoolean("visible");
                
                if (!waypoints.containsKey(uuid))
                    waypoints.put(uuid, new ArrayList<ExtraCursor>());
                
                waypoints.get(uuid).add(new ExtraCursor(x, z, visible, Type.WHITE_CROSS, (byte) 0, world, plugin.showDistantWaypoints()));
                
                if (deleteOld)
                {
                    removeOldWaypoint(player, x, z, world);
                    addWaypoint(uuid, x, z, world, visible);
                }
            }
            
            rs.close();
            return waypoints;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                getstatement.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return new HashMap<UUID, List<ExtraCursor>>();
    }
    
    @Override
    public void saveWaypoints(Map<UUID, List<ExtraCursor>> waypoints)
    {
        try
        {
            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void addWaypoint(final UUID player, final int x, final int z, final String world, final boolean visible)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    insertstatement.setString(1, player.toString());
                    insertstatement.setInt(2, x);
                    insertstatement.setInt(3, z);
                    insertstatement.setString(4, world);
                    insertstatement.setBoolean(5, visible);
                    insertstatement.execute();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
    
    @Override
    public void removeWaypoint(final UUID player, final ExtraCursor c)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    removestatement.setString(1, player.toString());
                    removestatement.setInt(2, c.getX());
                    removestatement.setInt(3, c.getZ());
                    removestatement.setString(4, c.getWorld());
                    removestatement.execute();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
    
    private void removeOldWaypoint(final String player, final int x, final int z, final String world)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    removestatement.setString(1, player);
                    removestatement.setInt(2, x);
                    removestatement.setInt(3, z);
                    removestatement.setString(4, world);
                    removestatement.execute();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
    
    @Override
    public void updateVisible(final UUID player, final ExtraCursor c, final boolean visible)
    {
        
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    updatestatement.setBoolean(1, visible);
                    updatestatement.setString(2, player.toString());
                    updatestatement.setInt(3, c.getX());
                    updatestatement.setInt(4, c.getZ());
                    updatestatement.setString(5, c.getWorld());
                    updatestatement.execute();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
