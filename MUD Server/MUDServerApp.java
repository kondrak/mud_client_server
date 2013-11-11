import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.Date;
import java.net.*;
import java.io.*;
import java.security.*;

import mud_networking.MUDServer;

/** 
 * This is the main server application class. It is used to invoke the server and start
 * the listening process.
 * 
 * @author Krzysztof Kondrak
 * @version 1.0
 *
 */

public class MUDServerApp{
    public static void main(String[] s){
         boolean err = false;
         int registryPort = 8666;
         try{
              LocateRegistry.createRegistry(registryPort);
              MUDServer server = new MUDServerImpl("Simple MUD Game Server: v. 1.0");
              UnicastRemoteObject.exportObject(server);
              Naming.rebind("//localhost:"+registryPort+"/krzko208sv", server);
         }
         catch(java.rmi.UnknownHostException uhe){
              System.out.println("[error] invalid hostname \n"+uhe+"\n");
              err = true;
         }
         catch(AccessControlException ace){
              System.out.println("[error] no permissions to bind the server \n"+ace+"\n");
              err = true;
         }
         catch(RemoteException re){
              System.out.println("[error] cannot register remote server object \n"+re+"\n");
              err = true;
         }
         catch(MalformedURLException mURLe){
              System.out.println("[error] internal error \n" + mURLe+"\n");
              err = true;
         }
         catch(Exception ee){
              System.out.println("[error] cccc"+ee.getMessage()+"\n");
              err = true;
         }
         if(!err)
             System.out.println("\n[OK] Simple MUD Game Server running...\n");
                   
    }
}

