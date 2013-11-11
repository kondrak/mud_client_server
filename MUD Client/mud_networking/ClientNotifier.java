package mud_networking;

import java.rmi.*;

/**
 * This is the client interface used for an RMI callback by the game server.
 * 
 * @author Krzysztof Kondrak
 * @version 1.0
 *
 */

public interface ClientNotifier extends Remote {
    public void notify(String message) throws RemoteException;
}
