# multithreading-project
operating systems project: simulate multiple connection requests with a master server via multithreading

Abstract directory contains code for defining abstract clients and servers
JerryWang directory contains classes for different types of clients and servers, and a factory to instantiate them
Test directory contains code for testing the behavior of the classes from JerryWang. 


master server implementation details: 

three hashmaps to track incoming client's status: 
-mapServers: client.getkey() returns an integer. The integer maps to a single basic server
-mapConditions: client.getkey() returns an integer. The integer maps to a single queue of condition variables
-mapClients: client.getkey() returns an integer. The integer maps to a single queue of clients.  

-lock with condition variables to assure proper synchronization of shared resources, the basic servers. 

Master Server's connect inner method:
1. Client obtains lock. given a client, get its server, its condition queue and its client queue using getkey(). 
2. add client to client queue and creat new condition variable. Add the condition variable to condition queue 
3. test for whether the client should be put in condition wait set: is the client the first element in the client queue? and does the client connect successfully with its basic server? If not, then client must wait. 
4. once client connects successfully to basic server, release lock. 

Master Server's disconnect inner method:
1. Client obtains lock. given a client, get its server, its condition queue and its client queue using getkey(). 
2. because disconnect is called by client only after it has already successfully connected to a basic server, now the next client in line may be signaled. 
3. check if condition queue has 2 or more elements. If so, signal next client in line. If not, skip. 
4. remove the client from the client queue and the condition queue's head position. 
5. call disconnect from basic server, which removes references to the client and makes room for new clients to connect. 
6. release lock. 
