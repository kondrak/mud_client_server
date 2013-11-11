package mud_networking;

import java.rmi.*;
import java.rmi.server.*;
import java.util.Vector;
import mud_gui.MUDClient;

/**
 * This is the main communication class of the client application. It represents the player
 * and is used to interact with the game server (by performing all player actions, like
 * world interaction, logging into the game, disconnecting etc.).
 * 
 * @author Krzysztof Kondrak
 * @version 1.0
 *
 */

public class MUDPlayer implements ClientNotifier{
	
	private String name;
	private String password;
	private String race;
	private String location="1";
	
	private int loginStage = 1;
	private boolean isConnected = false;
	private boolean isLoggedIn = false;
	private String mode = "l";
	
	private MUDServer server;
	private String hostname = "localhost";
	private int port = 8666;
	private String objectName = "krzko208sv";
	private MUDClient TheWorld;
	
	public MUDPlayer(MUDClient world) {
        try {
            UnicastRemoteObject.exportObject(this);
            TheWorld = world;
        }
        catch(RemoteException re) {
            re.printStackTrace();
        }
	}

	/**
	 * This method connects the player to the game server. 
	 * 
	 * @return boolean - success or failure
	 */
	
	public boolean Connect() {
		
		try{
            System.setSecurityManager(new RMISecurityManager());
            server = (MUDServer)Naming.lookup("//" + hostname + ":" +port+ "/" + objectName);
            if(server != null)
            {
            	isConnected = true;
            	TheWorld.toggleBasicInput(true);
            }
    }
    catch(Exception ex){
            System.out.println(ex);
    }
		
		return isConnected;
	}
	
	/**
	 * This method disconnects the player from the game server.
	 * 
	 * @return void	
	 */
	
	public void Disconnect() {
		
		if(isLoggedIn)
		  announceDisconnect();
		server = null;
		loginStage = 1;
		isConnected = false;
		isLoggedIn = false;
		TheWorld.toggleGameInput(false);
		TheWorld.toggleBasicInput(false);
	}

	/**
	 * This method checks if the player is connected to the game server.
	 * 
	 * @return boolean	
	 */
	
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * This method returns the login stage (0-6) that the player is currently at.
	 * 
	 * @return integer	
	 */
	
	public int getLoginStage() {
		return loginStage;
	}

	/**
	 * This method performs a login stage (choosing an option, character name, password etc).
	 * 
	 * @param input processed user input
	 * 
	 * @return String - reply returned by the server	
	 */
	
	public String performLoginStage(String input) {
		
		switch(loginStage)
		{
		case 1:
			this.mode = input.toLowerCase();
			
			if(this.mode.equals("l"))
			{
				this.loginStage = 5;
				return this.mode + "\n" + getSelectNameText();
			}
			else if (this.mode.equals("c"))
			{
				this.loginStage++;
				return this.mode + "\n" + getSelectNameText();			
			}
			else
				return "Invalid mode. Please try again (C or L)";
		case 2:
			this.name = input;
			if(nameIsValid(this.name))
			{
				this.loginStage++;
				return this.name + "\n" + getSelectPasswordText();
			}
			else
				return "A character with this name already exists.";
		case 3:
			this.password = input;
			this.loginStage++;
			return "*****\n" + getSelectRaceText();
		case 4:
			this.race = input.toLowerCase();
			if(raceIsValid(this.race))
			{
				this.loginStage = 0;
				createCharacter(this.name, this.password, this.race);
				this.location = getPlayerLocation(this.name);
				TheWorld.updateCompass();
				TheWorld.updateLocationInfo();
				TheWorld.toggleGameInput(true);
				this.isLoggedIn = true;
				try
				{
				  server.ClientRegister(this, this.name);
				}
				catch(RemoteException e)
				{
					System.out.println("Could not register the client!");
				}
				return this.race + "\nOK, character is set up.";
			}
			else
				return "Invalid race. Please try again (human, elf, hobbit, dwarf)";
		case 5:
			this.name = input;
			if(!nameIsValid(this.name))
			{
				this.loginStage++;
				return this.name + "\n" + getSelectPasswordText();
			}
			else
				return "\nThere is no character with this name!";
		case 6:
			this.password = input;
			if(passwordIsValid(this.name, this.password))
			{
				this.loginStage = 0;
				this.location = getPlayerLocation(this.name);
				TheWorld.updateCompass();
				TheWorld.updateLocationInfo();
				this.isLoggedIn = true;
				TheWorld.toggleGameInput(true);
				try
				{
				  server.ClientRegister(this, this.name);
				}
				catch(RemoteException e)
				{
					System.out.println("Could not register the client!");
				}
				return "\nOK, " + this.name + " logged in.";
			}
			else
				return "Invalid password. Please try again.";
		}	

		return "N/A";
	}

	/**
	 * This is an exclusive public method which gets the welcome text after the player
	 * successfully connects to the game server.
	 *
	 * @return String - the welcome text	
	 */
		
	public String getWelcomeText() {
		String WelcomeText = "";
	      try{
	    	 WelcomeText = server.getWelcomeText();
	      }
      catch(Exception ex){
              System.out.println(ex);
      }
      
      return WelcomeText;
	}
 
	/**
	 * This method gets the name selection text from the server.
	 * 
	 * @return String - the name selection text	
	 */
	
	private String getSelectNameText() {
		String SelectNameText = "";
	      try{
	    	 SelectNameText = server.getSelectNameText();
	      }
    catch(Exception ex){
            System.out.println(ex);
    }
    
    return SelectNameText;		
	}

	/**
	 * This method gets the password selection text from the server.
	 * 
	 * @return String - the password selection text	
	 */
	
	private String getSelectPasswordText() {
		String SelectPasswordText = "";
	      try{
	    	 SelectPasswordText = server.getSelectPasswordText();
	      }
    catch(Exception ex){
            System.out.println(ex);
    }
    
    return SelectPasswordText;		
	}

	/**
	 * This method gets the race selection text from the server.
	 * 
	 * @return String - the race selection text	
	 */
	
	private String getSelectRaceText() {
		String SelectRaceText = "";
	      try{
	    	 SelectRaceText = server.getSelectRaceText();
	      }
    catch(Exception ex){
            System.out.println(ex);
    }
    
    return SelectRaceText;		
	}

	/**
	 * This method checks if the supplied character name isn't 
	 * already stored in the database.
	 * 
	 * @return boolean - the name is (false) or is not (true) in the database	
	 */
	
	private boolean nameIsValid(String name) {
		boolean nameIsValid = false;
	      try{
	    	 nameIsValid = server.checkCharacterName(name);
	      }
    catch(Exception ex){
            System.out.println(ex);
    }
    
    return nameIsValid;		
	}

	/**
	 * This method checks if the supplied character password is valid.
	 * 
	 * @param name the character name
	 * @param password supplied character password
	 * 
	 * @return boolean	
	 */
	
	private boolean passwordIsValid(String name, String password) {
		boolean passwordIsValid = false;
	      try{
	    	 passwordIsValid = server.checkPlayerPassword(name,password);
	      }
  catch(Exception ex){
          System.out.println(ex);
  }
  
  return passwordIsValid;				
	}

	/**
	 * This method checks if the supplied character race is valid. 
	 * 
	 * @param race supplied character race
	 * 
	 * @return boolean	
	 */
	
	private boolean raceIsValid(String race) {
		String selectedRace = race.toLowerCase();
		String races[] = {"elf", "human", "dwarf", "hobbit"};
		
		for(int i=0; i<races.length; i++)
			if(selectedRace.equals(races[i]))
				return true;
			
		return false;
	}

	/**
	 * This method invokes character creation process.
	 * 
	 * @param name character's name
	 * @param password character's password
	 * @param race character's race
	 * 
	 * @return void	
	 */
	
	private void createCharacter(String name, String password, String race) {
	      try{
	    	 server.createCharacter(name, password, race);
	      }
  catch(Exception ex){
          System.out.println(ex);
  }
			
	}

	/**
	 * This method moves player to a given location.
	 * 
	 * @param location_id ID of new player's location
	 * 
	 * @return void	
	 */
	
	public void moveToLocation(String location_id) {
	      try{
	    	 server.movePlayer(this.name, location_id);
	    	 updateLocation(location_id);
	      }
catch(Exception ex){
        System.out.println(ex);
}
			
	}

	/**
	 * This method retrieves information about all adjacent locations with respect to
	 * player's current position
	 * 
	 * @return String[]	- array of adjacent locations
	 */
	
	public String[] getAdjacentLocations() {
	      try{
	    	 String adjacentLocations[] = server.getAdjacentLocations(this.name);
	    	 return adjacentLocations;
	      }
catch(Exception ex){
      System.out.println(ex);
}

 return null;
			
	}

	/**
	 * This method updates player's internal location variable.
	 * 
	 * @param location_id ID of new player's location
	 * 
	 * @return void	
	 */
	
   private void updateLocation(String location_id) {
	   location = location_id;
   }

	/**
	 * This method retrieves the picture name for player's current location.
	 * 
	 * @return String - picture filename	
	 */
   
   public String getLocationPicture() {
	      try{
		    	 String locationPicture = server.getLocationData(this.location, "picture");
		    	 return locationPicture;
		      }
	catch(Exception ex){
	      System.out.println(ex);
	}

	 return null;
				
   }

	/**
	 * This method retrieves player's current location from the database.
	 * 
	 * @param name Player's name
	 * 
	 * @return String - player's location	
	 */
   
   public String getPlayerLocation(String name) {
	      try{
		    	 String PlayerLocation = server.getPlayerLocation(name);
		    	 return PlayerLocation;
		      }
	catch(Exception ex){
	      System.out.println(ex);
	}

	 return null;
				
   }
 
	/**
	 * This method retrieves the name of player's current location.
	 * 
	 * @return String - name of the location	
	 */
   
   public String getLocationName() {
	      try{
		    	 String locationName = server.getLocationData(this.location, "name");
		    	 return locationName;
		      }
	catch(Exception ex){
	      System.out.println(ex);
	}

	 return null;
				
}
 
	/**
	 * This method retrieves the description of player's current location.
	 * 
	 * @return String - location description
	 */
   
   public String getLocationDescription() {
	      try{
		    	 String locationDescription = server.getLocationData(this.location, "description");
		    	 return locationDescription;
		      }
	catch(Exception ex){
	      System.out.println(ex);
	}

	 return null;
				
}

	/**
	 * This method retrieves the set of items placed in player's current location.
	 * 
	 * @return Vector - set of items in player's location	
	 */
   
   public Vector getLocationItems() {
	      try{
		    	 Vector locationItems = server.getLocationItems(this.location);
		    	 return locationItems;
		      }
	catch(Exception ex){
	      System.out.println(ex);
	}

	 return null;
				
}

	/**
	 * This method retrieves the set of players located in the player's current location.
	 * 
	 * @return Vector - set of players in player's location	
	 */
   
   public Vector getLocationPlayers() {
	      try{
		    	 Vector locationPlayers = server.getLocationPlayers(this.location);
		    	 return locationPlayers;
		      }
	catch(Exception ex){
	      System.out.println(ex);
	}

	 return null;
				
}
   
	/**
	 * This method retrieves player's inventory.
	 * 
	 * @return void	
	 */
   
   public void getInventory() {
	      try{
		    	 Vector Inventory = server.getPlayerInventory(this.name);
	    	  	 String InventoryList = null;
		    	 
		    	 for(int i=0; i< Inventory.size(); i++)
		    	 {
		    		 if(i == 0)
		    			 InventoryList = Inventory.get(0).toString();
		    		 else
		    			 InventoryList += ", " + Inventory.get(i);
		    	 }
		    	
		    	 if(InventoryList == null)
		    		 InventoryList = "nothing!";
		    	 
		    	 TheWorld.updateWorldPanel("You are carrying: " + InventoryList);
		    	 
		      }
	catch(Exception ex){
	      System.out.println(ex);
	}
				
}
 
	/**
	 * This method retrieves player's inventory in order to update the item list.
	 * 
	 * @return Vector - set of items in player inventory	
	 */
   
   public Vector getInventoryItemsList() {
	   
	   Vector Inventory = new Vector();
	      try{
		    	 Inventory = server.getPlayerInventory(this.name);
		      }
	catch(Exception ex){
	      System.out.println(ex);
	}
	
	return Inventory;
}
  
	/**
	 * This method retrieves current positions of all players for the purpose of updating
	 * the map.
	 * 
	 * @return Vector - set of all player positions	
	 */
   
   public Vector getPlayerPositions() {
	   
	   Vector Tracker = new Vector();
	      try{
		    	 Tracker = server.getPlayerPositions();
		      }
	catch(Exception ex){
	      System.out.println(ex);
	}
	
	return Tracker;
}
   
	/**
	 * This method performs the command specified by user.
	 * 
	 * @param action specific command
	 * @param action_subject the subject of user's action
	 * 
	 * @return void
	 */
   
   public void performCommand(String action, String action_subject) {
	      try{
	    	   System.out.println("performing: *" + action + "*" + action_subject + "*");
		       String response = server.performPlayerAction(action, action_subject, this.name);
		       System.out.println("performed");
		       TheWorld.updateWorldPanel(response);  
	      	}
	catch(Exception ex){
	      System.out.println(ex);
	}
				
}
   
	/**
	 * This method manifests its disconnect to the server.
	 * 
	 * @return void	
	 */
   
   public void announceDisconnect() {
	      try{
		    	server.ClientUnregister(this.name);
		      }
	catch(Exception ex){
	      System.out.println(ex);
	}
				
}
   
	/**
	 * This is the callback RMI method invoked by the server whenever it wants to update
	 * the client on the world status
	 * 
	 * @param message server message
	 * 
	 * @return void
	 */
	
	public void notify(String message) throws RemoteException {
		
		if(message.equals("updateItemList"))
			TheWorld.updateItemList();
		else if(message.equals("updatePlayersList"))
			TheWorld.updatePlayersList();
		else if(message.equals("updateTrackerList"))
			TheWorld.updateTrackerList();
		else
			 TheWorld.updateWorldPanel(message);
	}
	
}
