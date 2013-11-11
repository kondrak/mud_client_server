import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.Vector;
import java.util.Enumeration;
import mud_networking.*;

/**
 * This is the server implementation. This class implements all public methods from the
 * remote server interface.
 * 
 * @author Krzysztof Kondrak
 * @version 1.0
 *
 */

//implementacja
public class MUDServerImpl implements MUDServer{
       private String description;
       private String url = "jdbc:mysql://www.freesql.org/netkoji";
       private String login = "netkoji";
       private String password = "netko83";
       private String SQLDriver = "com.mysql.jdbc.Driver";
       private Connection SQLConnection = null;
       
       private ClientStorage ConnectedClients = null;

       MUDServerImpl(String desc) throws RemoteException {
             description = desc;

             ConnectedClients = new ClientStorage();
       }

   	/**
   	 * This methods creates a connection to the SQL database.
   	 * 
   	 * @return void	
   	 */
       
       public void SQLConnect() {
    		try {
                	Class.forName(SQLDriver).newInstance();
            	} 
            catch (Exception ex) {
                System.out.println(ex);
            }

            try {
    		SQLConnection = DriverManager.getConnection(url,login, password);
            	}
            catch (SQLException ex) {
        // handle any errors
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }   	   
    	   
       } // SQLConnect()
  
   	/**
   	 * This method executes a non-update SQL query (SELECT)
   	 *
   	 * @param query the SQL query to be performed
   	 * @param field the table field to be obtained
   	 *
   	 * @return String - the query result	
   	 */
       
       public String executeQuery(String query, String field) {
    	   Statement stmt = null;
    	   ResultSet rs = null;
           try {

       		try {
       			stmt = SQLConnection.createStatement();
       			rs = stmt.executeQuery(query);
       		 if(field.length() > 0)
       			while(rs.next())
       			{
       				String strng = rs.getString(field);
       				System.out.println(strng);
       				return strng;
       			}
       			
       		} finally {

       			if (rs != null) {
       				try {
       					rs.close();
       				} catch (SQLException sqlEx) { // ignore 
       				}
               rs = null;
           }

           if (stmt != null) {
               try {
                   stmt.close();
               } catch (SQLException sqlEx) { // ignore 
               }

               stmt = null;
           }
       }
       		
       } catch (SQLException ex) {
           // handle any errors
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());
       }   	     	 
        return "";
       } // executeQuery()
 
      	/**
      	 * This method executes a non-update SQL query (SELECT) which may produce
      	 * more than one result.
      	 *
      	 * @param query the SQL query to be performed
      	 * @param field the table field to be obtained
      	 *
      	 * @return Vector - the query result	
      	 */
       
       public Vector executeMultiResultQuery(String query, String field) {
    	   Statement stmt = null;
    	   ResultSet rs = null;
    	   Vector results = new Vector();
           try {

       		try {
       			stmt = SQLConnection.createStatement();
       			rs = stmt.executeQuery(query);
       		 if(field.length() > 0)
       			while(rs.next())
       			{
       				String strng = rs.getString(field);
       				System.out.println(strng);
       				results.addElement(strng);
       			}
       			
       		} finally {

       			if (rs != null) {
       				try {
       					rs.close();
       				} catch (SQLException sqlEx) { // ignore 
       				}
               rs = null;
           }

           if (stmt != null) {
               try {
                   stmt.close();
               } catch (SQLException sqlEx) { // ignore 
               }

               stmt = null;
           }
       }
       		
       } catch (SQLException ex) {
           // handle any errors
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());
       }   	     	 
        return results;
       } // executeMultiResultQuery()
       
      	/**
      	 * This method executes an update SQL query (INSERT, UPDATE, DELETE)
      	 *
      	 * @param query the SQL query to be performed
      	 *
      	 * @return int - the code of the response	
      	 */      
       
       public int executeUpdate(String query) {
    	   Statement stmt = null;
    	   int code = 0;
           try {

       		try {
       			stmt = SQLConnection.createStatement();
       			code = stmt.executeUpdate(query);
       			
       		} finally {

           if (stmt != null) {
               try {
                   stmt.close();
               } catch (SQLException sqlEx) { // ignore 
               }

               stmt = null;
           }
       }
       		
       } catch (SQLException ex) {
           // handle any errors
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());
       }   	     	 
        return code;
       } // executUpdate()       
       

      	/**
      	 * This method registers a connected client on the server and stores its identifier
      	 * for further communication purposes.
      	 *
      	 * @param n the remote client object that called registration method
      	 * @throws RemoteException
      	 * @return void	
      	 */      
       
       public void ClientRegister(ClientNotifier n, String player_name) throws RemoteException {
  
    	   notifyAllClients("* " + player_name + " entered the game.");
    	   ConnectedClients.addClient(n, player_name);
 
     	   String player_location = executeQuery("select location from players where name='" + player_name + "'","location");
    	   String Location = executeQuery("select name from locations where id=" + player_location,"name");
    	   executeUpdate("update `stats` set hits=hits+1 where Name='" + player_name + "' and statID='2';");
    	   notifyClientsInLocation(player_name, player_name + " has entered " + Location + ".");
		   notifyAllClients("updatePlayersList");
		   notifyAllClients("updateTrackerList");
    	   System.out.println("Registered a client.\n");
       }

     	/**
     	 * This method unregisters a connected client from the server.
     	 *
     	 * @param player_name the player to be unegistered
     	 * @throws RemoteException
     	 * @return void	
     	 */ 
       
       public void ClientUnregister(String player_name) throws RemoteException {
           ConnectedClients.removeClient(player_name);
           notifyAllClients("* " + player_name + " left the game.");

     	   String player_location = executeQuery("select location from players where name='" + player_name + "'","location");
    	   String Location = executeQuery("select name from locations where id=" + player_location,"name");
    	   notifyClientsInLocation(player_name, player_name + " has left " + Location + ".");
		   notifyAllClients("updatePlayersList");
		   notifyAllClients("updateTrackerList");
           System.out.println("Removed a client.\n");
       }
       
      	/**
      	 * This method supplies the client with a welcome text upon client connection.
      	 * 
      	 * @throws RemoteException
      	 * @return String - the welcome text	
      	 */
       
       public String getWelcomeText() throws RemoteException{
    	   return "Welcome to Middle Earth, please choose your option:\n (C)reate a new character\n(L)oad an existing character";
       }

     	/**
     	 * This method supplies the client with a name selection text.
     	 * 
     	 * @throws RemoteException
     	 * @return String - the name selection text	
     	 */
       
       public String getSelectNameText() throws RemoteException{
             return "Please choose your character's name:";
       }

    	/**
    	 * This method sends a broadcast message to all registered clients.
    	 * 
    	 * @param message message to be sent
    	 * 
    	 * @return void	
    	 */
       
       private void notifyAllClients(String message) {
    	   for(Enumeration clients = ConnectedClients.getClientList().elements();
           clients.hasMoreElements();) {
               ClientNotifier thingToNotify = (ClientNotifier) clients.nextElement();
               try {
            	   thingToNotify.notify(message);
               }
               catch(RemoteException e) { // tu usuniecie z wektora klienta ktorego nie ma
            	   System.out.println("notifyAllClients(): error");
               }
           }
       }
 
   	/**
   	 * This method sends a message to a specific registered client.
   	 * 
   	 * @param name the name of the player to be notified
   	 * @param message message to be sent
   	 * 
   	 * @return void	
   	 */
       
       private void notifySpecificClient(String name, String message) {
    	   
               ClientNotifier thingToNotify = ConnectedClients.getClientByName(name);
           
             if(thingToNotify != null)
               try {
            	   thingToNotify.notify(message);
               }
               catch(RemoteException e) {
            	   System.out.println("notifySpecificClient(): error");
               }

       }
 
   	/**
   	 * This method sends a broadcast message to all clients in a certain location.
   	 * 
   	 * @param name the name of the location
   	 * @param message message to be sent
   	 * 
   	 * @return void	
   	 */
       
       private void notifyClientsInLocation(String name, String message) {
          	String player_location = executeQuery("select location from players where name='" + name + "'","location");
    		Vector all_players_in_location = executeMultiResultQuery("select name from players where name not like '" + name + "' and location=" + player_location, "name");

    			for(Enumeration clients = all_players_in_location.elements();
    	           clients.hasMoreElements();)
    			   { 
    				  notifySpecificClient((String)clients.nextElement(), message);
    			   }
       }
       
    	/**
    	 * This method supplies the client with a password selection text.
    	 * 
    	 * @throws RemoteException
    	 * @return String - the password selection text	
    	 */
       
       public String getSelectPasswordText() throws RemoteException{
             return "Please choose your character's password:";
       }

    	/**
    	 * This method supplies the client with a race selection text.
    	 * 
    	 * @throws RemoteException
    	 * @return String - the race selection text	
    	 */
       
       public String getSelectRaceText() throws RemoteException{
             return "Please choose your race (human, elf, hobbit, dwarf):";
       }

    	/**
    	 * This method checks if the supplied character name isn't stored in the database.
    	 * 
    	 * @throws RemoteException
    	 * @return boolean - the name is (false) or is not (true) in the database.
    	 */
       
       public boolean checkCharacterName(String name) throws RemoteException {
    	   String storedName;
    	   if(SQLConnection == null)
    		   SQLConnect();
    	   storedName = executeQuery("select name from players where name='" + name + "'","name"); 
    	   if(storedName.matches(name))
    		   return false;
    	   else
    		   return true;
       }
 
    	/**
    	 * This method creates a new character.
    	 * 
    	 * @throws RemoteException
    	 * @return void	
    	 */
       
       public void createCharacter(String name, String password, String race) throws RemoteException {
    	   if(SQLConnection == null)
    		   SQLConnect();
    	   executeUpdate("INSERT INTO `players` ( `name` , `pass` , `race`, `location` ) VALUES ('" + name + "', '" + password + "', '" + race + "', '1');"); 
    	   executeUpdate("INSERT INTO `stats` ( `statID` , `Name` , `hits`) VALUES ('2', '" + name + "', '1');");
    	   executeUpdate("update `stats` set hits=hits+1 where Name='" + race + "' and statID='3';");
       }
 
    	/**
    	 * This method checks if the supplied character password is valid.
    	 * 
    	 * @throws RemoteException
    	 * @return boolean	
    	 */
       
       public boolean checkPlayerPassword(String name, String password) throws RemoteException {
    	  String StoredPassword;
    	   if(SQLConnection == null)
    		   SQLConnect();
    	   StoredPassword = executeQuery("select pass from players where name='" + name + "'","pass"); 
    	   
    	   if(StoredPassword.matches(password))
    		   return true;
    	   else
    		   return false;
       }
 
   	/**
   	 * This method moves the player to a new location.
   	 * 
   	 * @param name name of the player
   	 * @param location_id ID of new player location
   	 * @throws RemoteException
   	 * @return void	
   	 */
       
       public void movePlayer(String name, String location_id) throws RemoteException {
    	   if(SQLConnection == null)
    		   SQLConnect();
    	   String player_location = executeQuery("select location from players where name='" + name + "'","location");
    	   String oldLocation = executeQuery("select name from locations where id=" + player_location,"name");
    	   String newLocation = executeQuery("select name from locations where id=" + location_id,"name");
    	   notifyClientsInLocation(name, name + " has left " + oldLocation + ".");
    	   executeUpdate("update players set location=" + location_id + " where name='" + name + "'");  
     	   executeUpdate("update stats set hits=hits+1 where Name='" + newLocation + "' and statID=1");  
    	   notifyClientsInLocation(name, name + " has entered " + newLocation + ".");
		   notifyAllClients("updatePlayersList");
		   notifyAllClients("updateTrackerList");
       }

      	/**
      	 * This method returns a list of adjacent locations with respect to the location 
      	 * given as parameter.
      	 * 
      	 * @param name location name
      	 * @throws RemoteException
      	 * @return String[] - List of adjacent locations
      	 */
       
       public String[] getAdjacentLocations(String name) throws RemoteException {
    	   String PlayerLocation = null;
    	   String locationNorth, locationSouth, locationEast, locationWest = null;
    	   
    	   if(SQLConnection == null)
    		   SQLConnect();
    	   PlayerLocation = executeQuery("select location from players where name='" + name + "'","location");  
  
       	   locationNorth = executeQuery("select nid from locations where id=" + PlayerLocation,"nid");  
       	   locationSouth = executeQuery("select sid from locations where id=" + PlayerLocation,"sid"); 
       	   locationEast = executeQuery("select eid from locations where id=" + PlayerLocation,"eid"); 
       	   locationWest = executeQuery("select wid from locations where id=" + PlayerLocation,"wid"); 
    	   
           String adjacentLocations[] = { locationNorth, locationSouth, locationEast, locationWest };
           
           return adjacentLocations;
       }
 
      	/**
      	 * This method returns certain information about the location, depending on its
      	 * parameters.
      	 * 
      	 * @param location_id ID of considered location
      	 * @param datatype type of information to be retrieved
      	 * @throws RemoteException
      	 * @return void	
      	 */
       
       public String getLocationData(String location_id, String datatype) throws RemoteException {
    	   
    	   String locationData;
    	   
    	   if(SQLConnection == null)
    		   SQLConnect();

       	   locationData = executeQuery("select * from locations where id=" + location_id, datatype); 

           return locationData;
       }
 
      	/**
      	 * This method retrieves player's location.
      	 * 
      	 * @param name name of the player
      	 * 
      	 * @throws RemoteException
      	 * @return void	
      	 */
       
       public String getPlayerLocation(String name) throws RemoteException {
    	   
    	   String PlayerLocation;
    	   
    	   if(SQLConnection == null)
    		   SQLConnect();

       	   PlayerLocation = executeQuery("select location from players where name='" + name + "'", "location"); 

           return PlayerLocation;
       }
 
     	/**
     	 * This method retrieves items in a location.
     	 * 
     	 * @param location_id ID of the location
     	 * 
     	 * @throws RemoteException
     	 * @return Vector - set of items in the location
     	 */
       
       public Vector getLocationItems(String location_id) throws RemoteException {
    	   
    	  Vector LocationItems = new Vector();
    	   
    	   if(SQLConnection == null)
    		   SQLConnect();

       	   LocationItems = executeMultiResultQuery("select name from location_items where location=" + location_id, "name"); 

           return LocationItems;
       }

     	/**
     	 * This method retrieves players present in a location.
     	 * 
     	 * @param location_id ID of the location
     	 * 
     	 * @throws RemoteException
     	 * @return Vector - set of players in the location
     	 */
       
       public Vector getLocationPlayers(String location_id) throws RemoteException {
    	   
     	  Vector LocationPlayers = new Vector();
     	  Vector AllLocationPlayers = new Vector();
     	  ClientNotifier loggedInPlayers = null;
     	  
     	   if(SQLConnection == null)
     		   SQLConnect();

        	   AllLocationPlayers = executeMultiResultQuery("select name from players where location=" + location_id, "name"); 

        	   for(int i=0; i<AllLocationPlayers.size(); i++)
        	   {
        		   loggedInPlayers = ConnectedClients.getClientByName(AllLocationPlayers.get(i).toString());
        		   if(loggedInPlayers != null)
        			   LocationPlayers.addElement(AllLocationPlayers.get(i).toString());
        	   }
        	   
            return LocationPlayers;
        }
  
    	/**
    	 * This method retrieves positions of all connected players.
    	 * 
    	 * @throws RemoteException
    	 * @return Vector - set of player locations
    	 */
       
       public Vector getPlayerPositions() throws RemoteException {
    	   
      	  Vector Players = ConnectedClients.getPlayerNames();
      	  Vector PlayerPositions = new Vector();
      	  String player_location;
      	  String location_name;
      	  
      	   if(SQLConnection == null)
      		   SQLConnect();

      	   	 for(int i=0; i<Players.size(); i++)
      	   	 {
         	   player_location = executeQuery("select location from players where name='" + Players.get(i).toString() + "'", "location"); 
         	   location_name = executeQuery("select name from locations where id=" + player_location, "name");
         	   PlayerPositions.addElement(Players.get(i).toString() + " - " + location_name);
      	   	 }
         	   
             return PlayerPositions;
         }
 
    	/**
    	 * This method retrieves player inventory.
    	 * 
    	 * @param name name of the player
    	 * 
    	 * @throws RemoteException
    	 * @return Vector - set of items in player's inventory
    	 */
       
       public Vector getPlayerInventory(String name) throws RemoteException {
    	   
     	  Vector PlayerInventory = new Vector();
     	   
     	   if(SQLConnection == null)
     		   SQLConnect();

        	   PlayerInventory = executeMultiResultQuery("select item_name from player_inventory where player_name='" + name + "'", "item_name"); 

            return PlayerInventory;
        }
   
    	/**
    	 * This method performs user's command.
    	 * 
    	 * @param action user's command
    	 * @param action_subject the subject of user's command
    	 * @param player_name name of the player performing the command
    	 * 
    	 * @throws RemoteException
    	 * @return String - server response to the command
    	 */
       
       public String performPlayerAction(String action, String action_subject, String player_name) throws RemoteException {
    	   String response = "N/A";
    	   
    	   if(SQLConnection == null)
     		   SQLConnect();
//drop
        	if(action.equals("drop"))
        	{
        		String item_name = executeQuery("select item_name from player_inventory where player_name='" + player_name + "' and item_name='" + action_subject + "'", "item_name");
        		
        		if(item_name != null && item_name.length() > 0)
        		  {
        			String item_desc = executeQuery("select description from player_inventory where player_name='" + player_name + "' and item_name='" + action_subject + "'", "description");
                	String player_location = executeQuery("select location from players where name='" + player_name + "'","location");
        			
        			 executeUpdate("insert into location_items values (NULL,'" + item_name + "',"+ player_location +",'"+ item_desc +"',1)");
        			 executeUpdate("delete from player_inventory where player_name='" + player_name + "' and item_name='" + action_subject + "'"); 
        			 response = "OK, you drop " + action_subject;
        			 notifyClientsInLocation(player_name, player_name + " dropped " + action_subject);
        			 notifyAllClients("updateItemList");
        		  }	 
        		else
        			 response = "You cannot drop " + action_subject + "!";
        	}	
//give
        	if(action.equals("give"))
        	{
 
            	String player_location = executeQuery("select location from players where name='" + player_name + "'","location");

        		String what_to_give = action_subject.substring(action_subject.indexOf(" "));
        		what_to_give = what_to_give.trim();
        	
        		String second_player = action_subject.substring(0, action_subject.indexOf(" "));
        		second_player = second_player.trim();
        		
        		String item_name = executeQuery("select item_name from player_inventory where player_name='" + player_name + "' and item_name='" + what_to_give + "'", "item_name");
      		    String second_player_name = executeQuery("select name from players where name='" + second_player + "' and location=" + player_location, "name");
      		 
      		   if(second_player_name != null && second_player_name.length() > 0)
      		   {
        		
        		if(item_name != null && item_name.length() > 0)
        		  {
        			 executeUpdate("update player_inventory set player_name='" + second_player + "' where player_name='" + player_name + "' and item_name='" + what_to_give + "'"); 
        			 response = "OK, you give " + what_to_give + " to " + second_player;
        			 notifySpecificClient(second_player , player_name + " gives you " + what_to_give);
        			 notifyAllClients("updateItemList");
        		  }	 
        		else
        			 response = "You don't have " + what_to_give + " in your inventory!";
      		   }
      		   else
      			   response = " I can't see " + second_player + " here!";
      		  }	

//take
        	if(action.equals("take"))
        	{
        		String item_type = executeQuery("select pickable from location_items where name='" + action_subject + "'", "pickable");
        		
        		if(item_type != null && item_type.equals("1"))
        		  {
        			String item_desc = executeQuery("select description from location_items where name='" + action_subject + "'", "description");
        			
        			 executeUpdate("insert into player_inventory values ('" + player_name + "','"+ action_subject +"','"+ item_desc +"')");
        			 executeUpdate("delete from location_items where name='" + action_subject + "'"); 
        			 response = "OK, you take " + action_subject;
        			 notifyClientsInLocation(player_name, player_name + " takes " + action_subject);
        			 notifyAllClients("updateItemList");
        		  }	 
        		else if(item_type != null && item_type.equals("0"))
        			 response = "You cannot take " + action_subject + ".";
        		else
        			 response = "I can't see " + action_subject + " here!";
        	}
//look 
        	if(action.equals("look"))
        	{
        		if(action_subject.matches("around"))
        		{
                   	String player_location = executeQuery("select location from players where name='" + player_name + "'","location");
        			String location_desc = executeQuery("select description from locations where id=" + player_location + "", "description");
            		response = location_desc;
        		}
        		else
        		{
            		String item_desc = executeQuery("select description from location_items where name='" + action_subject + "'", "description");
            		
            		if(item_desc != null && item_desc.length() > 0)
            			response = item_desc;
            		else
            			item_desc = executeQuery("select description from player_inventory where item_name='" + action_subject + "' and player_name='" + player_name + "'", "description");
     
            		if(item_desc != null && item_desc.length() > 0)
            			response = item_desc;       			
            		else
            			 response = "I can't see " + action_subject + " here!";	
        		}        		

        	}
//say 
        	if(action.equals("say"))
        	{
  
            	String player_location = executeQuery("select location from players where name='" + player_name + "'","location");

        		String what_to_say = action_subject.substring(action_subject.indexOf(" "));
        		what_to_say = what_to_say.trim();
        	
        		String second_player = action_subject.substring(0, action_subject.indexOf(" "));
        		second_player = second_player.trim();
        		

        		if(second_player.matches("everyone"))
        		{
        			notifyClientsInLocation(player_name, player_name + " says: " + what_to_say);
        			response = "";
        		}
        		else
        		{
         		   String second_player_name = executeQuery("select name from players where name='" + second_player + "' and location=" + player_location, "name");
     
            		if(second_player_name != null && second_player_name.length() > 0)
            		{
            			notifySpecificClient(second_player_name, player_name + " says: " + what_to_say);
            			response = "";
            		}
            		else
            			 response = "I can't see " + second_player + " here!";	
        		}        		

        	}
        	
    	   return response;
       }
       
}