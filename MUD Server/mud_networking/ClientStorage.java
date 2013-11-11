package mud_networking;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * This is a class responsible for storing connected players for the means of a callback
 * RMI communication.
 * 
 * @author Krzysztof Kondrak
 * @version 1.0
 *
 */

public class ClientStorage {
	private Vector ClientList = null;
	private Vector PlayerNames = null;
	
	public ClientStorage() {
		ClientList = new Vector();
		PlayerNames = new Vector();
	}

	/**
	 * This method adds a client to the list of registered clients.
	 * 
	 * @param n identifier of player's connection
	 * @param player_name name of the player
	 * 
	 * @return void
	 */
	
	public void addClient(ClientNotifier n, String player_name) {
		ClientList.addElement(n);
		PlayerNames.addElement(player_name);
	}

	/**
	 * This method removes a client from the list of registered clients.
	 * 
	 * @param player_name name of the player
	 * 
	 * @return void
	 */
	
	public void removeClient(String player_name) {
		int player_index = PlayerNames.indexOf(player_name);
		ClientList.remove(player_index);
		PlayerNames.remove(player_name);
	}

	/**
	 * This method returns a vector of registered player identifiers.
	 * 
	 * @return Vector
	 */
	
	public Vector getClientList() {
		return ClientList;
	}

	/**
	 * This method returns a vector of registered player names.
	 * 
	 * @return Vector
	 */
	
	public Vector getPlayerNames() {
		return PlayerNames;
	}
	
	/**
	 * This method returns the identifier of player's connection with a given name.
	 * 
	 * @param player_name name of the player
	 * 
	 * @return ClientNotifier player's identifier
	 */
	
	public ClientNotifier getClientByName(String player_name) {
		int player_index = PlayerNames.indexOf(player_name);
          if(player_index != -1)
        	  return (ClientNotifier)ClientList.get(player_index);
          else
        	  return null;

	}
	
}
