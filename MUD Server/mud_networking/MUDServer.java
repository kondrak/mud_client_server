package mud_networking;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * This is the game server interface.
 * 
 * @author Krzysztof Kondrak
 * @version 1.0
 *
 */

public interface MUDServer extends Remote{
	
    public void ClientRegister(ClientNotifier n, String player_name) throws RemoteException;
    public void ClientUnregister(String player_name) throws RemoteException;
    public String getWelcomeText() throws RemoteException;
    public String getSelectNameText() throws RemoteException;
    public String getSelectPasswordText() throws RemoteException;
    public String getSelectRaceText() throws RemoteException; 
    public boolean checkCharacterName(String name) throws RemoteException;
    public void createCharacter(String name, String password, String race) throws RemoteException;
    public boolean checkPlayerPassword(String name, String password) throws RemoteException;
    public void movePlayer(String name, String location_id) throws RemoteException;
    public String getPlayerLocation(String name) throws RemoteException;
    public Vector getPlayerPositions() throws RemoteException;
    public String[] getAdjacentLocations(String name) throws RemoteException;
    public String getLocationData(String location_id, String datatype) throws RemoteException;
    public Vector getLocationItems(String location_id) throws RemoteException;
    public Vector getLocationPlayers(String location_id) throws RemoteException;
    public Vector getPlayerInventory(String name) throws RemoteException;
    public String performPlayerAction(String action, String action_subject, String player_name) throws RemoteException;
}
