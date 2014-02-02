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

import org.bukkit.map.MapCursor.Type;

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
    
    private Connection conn;
    
    private PreparedStatement getstatement;
    private PreparedStatement insertstatement;
    private PreparedStatement removestatement;
    private PreparedStatement updatestatement;
    
    public MySQL(ServerMinimap plugin, String host, int port, String user, String pass, String database)
    {
        this.plugin = plugin;
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.database = database;
        
        openConnection();
        try
        {
            getstatement = getConnection().prepareStatement("SELECT * FROM waypoints");
            insertstatement = getConnection().prepareStatement("INSERT INTO waypoints (player, x, z, world, visible) VALUES (?,?,?,?,?)");
            removestatement = getConnection().prepareStatement("DELETE FROM waypoints WHERE player = ? AND x = ? AND  z = ? AND world = ?");
            updatestatement = getConnection().prepareStatement("UPDATE waypoints SET visible = ? WHERE player = ? AND x = ? AND z = ? AND world = ?");
            if (!conn.getMetaData().getTables(null, null, "waypoints", null).next())
            {
                PreparedStatement st = getConnection().prepareStatement("CREATE TABLE waypoints ( player varchar(255), x int, z int, world varchar(255), visible boolean )");
                st.execute();
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
    public Map<String, List<ExtraCursor>> loadWaypoints()
    {
        try
        {
            Map<String, List<ExtraCursor>> waypoints = new HashMap<String, List<ExtraCursor>>();
            ResultSet rs = getstatement.executeQuery();
            while (rs.next())
            {
                String player = rs.getString("player");
                int x = rs.getInt("x");
                int z = rs.getInt("z");
                String world = rs.getString("world");
                boolean visible = rs.getBoolean("visible");
                
                if (!waypoints.containsKey(player))
                    waypoints.put(player, new ArrayList<ExtraCursor>());
                
                waypoints.get(player).add(new ExtraCursor(x, z, visible, Type.WHITE_CROSS, (byte) 0, world, plugin.showDistantWaypoints()));
            }
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
        return new HashMap<String, List<ExtraCursor>>();
    }
    
    @Override
    public void saveWaypoints(Map<String, List<ExtraCursor>> waypoints)
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
    public void addWaypoint(String player, int x, int z, String world)
    {
        try
        {
            insertstatement.setString(1, player);
            insertstatement.setInt(2, x);
            insertstatement.setInt(3, z);
            insertstatement.setString(4, world);
            insertstatement.setBoolean(5, true);
            insertstatement.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void removeWaypoint(String player, ExtraCursor c)
    {
        try
        {
            removestatement.setString(1, player);
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
    
    @Override
    public void updateVisible(String player, ExtraCursor c, boolean visible)
    {
        try
        {
            updatestatement.setBoolean(1, visible);
            updatestatement.setString(2, player);
            updatestatement.setInt(3, c.getX());
            updatestatement.setInt(4, c.getZ());
            updatestatement.setString(5, c.getWorld());
            plugin.getLogger().info(updatestatement.toString());
            updatestatement.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
