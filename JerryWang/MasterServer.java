package edu.Brandeis.cs131.Common.JerryWang;

import java.util.HashMap;
import java.util.LinkedList;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Log.Log;
import edu.Brandeis.cs131.Common.Abstract.Server;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MasterServer extends Server {

    //3 maps to track client status: map to basic server, map to conditions queue, map to clients queue
    private final Map<Integer, Server> mapServers = new HashMap<Integer, Server>();
    private final Map<Integer, List<Condition>> mapConditions = new HashMap<Integer, List<Condition>>();

    private final Map<Integer, List<Client>> mapClients = new HashMap<Integer, List<Client>>();
    //lock to ensure synchronized access to shared resource, the basic servers
    final Lock lock = new ReentrantLock();    
    
    public MasterServer(String name, Collection<Server> servers, Log log) {
        super(name, log);
        Iterator<Server> iter = servers.iterator();
        while (iter.hasNext()) {
            this.addServer(iter.next());
        }
    }

    public void addServer(Server server) {
        int location = mapServers.size();
        this.mapServers.put(location, server);
        this.mapConditions.put(location, new LinkedList<Condition>());
        this.mapClients.put(location, new LinkedList<Client>());
       
    }
    
    @Override
    public boolean connectInner(Client client) {
        lock.lock();
    	try{
    		//get the client's basic server, its condition queue, its client queue. 
    		int serverNumber = getKey(client);
	        Server basicServer = this.mapServers.get(serverNumber);
		    List<Condition> conditionQueue = this.mapConditions.get(serverNumber);
		    List<Client> clientQueue = this.mapClients.get(serverNumber);
		    //obtain a new condiiton for every client that calls connect. Add this client-specific condition to its condition queue
		    Condition clientWait = lock.newCondition();    		    
		    conditionQueue.add(clientWait);
		    //condition queue tracks client's condition, so we must add a client for every condition that is added. 
		    clientQueue.add(client);
		    //the most important line of this program. We must make sure the client will wait until it is the first in line and it successfully connects to its basic server. 
		    while(!clientQueue.get(0).equals(client) || !basicServer.connect(client)){		    	
		    	clientWait.await();	    	
		    }
		   	//finished connecting? then release the lock and return true so that it can do work in the server and then disconnect. 	   
    	}
    	catch(InterruptedException e){
    		e.printStackTrace();
    	}
    	finally{   		
    		lock.unlock(); 		
    	}
    	return true;
    }

    @Override
    public void disconnectInner(Client client) {
        lock.lock();
    	try{
	    	//get the client's basic server, its condition queue, its client queue. 
    		int serverNumber = getKey(client);	    	
	        Server basicServer = this.mapServers.get(serverNumber);
		    List<Condition> conditionQueue = this.mapConditions.get(serverNumber);
		    List<Client> clientQueue = this.mapClients.get(serverNumber);
		    //make room for a new client in the basic server
		    basicServer.disconnect(client);
		   //if the queue has only 1 element, then there's no need to signal the next in line. Else, notify next in line. 
		    if(conditionQueue.size()>1){
		    	Condition nextClient = conditionQueue.get(1);
		    	nextClient.signal();
		    }	
		    //remove the client from the condition queue and the client queue. 
		    clientQueue.remove(0);
		    conditionQueue.remove(0);

    	}
    	finally{
    		lock.unlock();
    	}	
    }

	//returns a number from 0- mapServers.size -1
    // MUST be used when calling get() on mapServers or mapQueues
    private int getKey(Client client) {
        return client.getSpeed() % mapServers.size();
    }
}
