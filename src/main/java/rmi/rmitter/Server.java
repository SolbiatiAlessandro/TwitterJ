package rmi.rmitter;

import rmi.rmitter.control.Controller;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by affo on 19/03/18.
 */
public class Server {
    public static void main(String[] args) throws RemoteException {
        System.setProperty("java.security.policy", "stupid.policy");
        System.setSecurityManager(new SecurityManager());
        Controller controller = new Controller();
        System.out.println(">>> Controller exported");
        //System.setProperty("java.rmi.server.hostname", "192.168.1.13");
        LocateRegistry.createRegistry(1099);
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("controller", controller);
    }
}
