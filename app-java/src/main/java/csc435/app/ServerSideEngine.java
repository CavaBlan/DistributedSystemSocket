package csc435.app;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServerSideEngine {
    private IndexStore store;
    private ServerSocket serverSocket;
    private int port;
    
    //private static List<Socket> clientList = new ArrayList<>();
    //Because I need to get the client name, I use HashMap to save it
    //<Key: client name, Value: ip and port>
    private static Map<String, Socket> clientMap = new HashMap<>();
    private static int clientCount = 0;

    public ServerSideEngine(IndexStore store, int port) {
        this.store = store;
        // TO-DO implement constructor
        this.port = port;
    }

    public void initialize() {
        // TO-DO create one dispatcher thread
    	//new Thread(new Dispatcher(this)).start();
        try {
            serverSocket = new ServerSocket(port);
//            System.out.println("Server is listening on port " + port);
            new Thread(new Dispatcher(this, serverSocket)).start();
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port + ": " + e.getMessage());
        }
    }
    


    public void spawnWorker(Socket clientSocket) {
    	// TO-DO create a new worker thread
        clientCount++;
        String clientName = "client" + clientCount; 
        clientMap.put(clientName, clientSocket);
        new Thread(new Worker(clientSocket, store)).start();
    }
 
    public void shutdown() {
        // TO-DO join the dispatcher and worker threads
        // Close the server, stop accepting new connections
        try {
            serverSocket.close();
            for (Socket clientSocket : clientMap.values()) {
                clientSocket.close(); // Close the connection to each client
            }
            System.out.println("Server closed");
        } catch (IOException e) {
            System.err.println("Error occurred while closing server: " + e.getMessage());
        }
    }
    
    public void list() {
        // TO-DO get the connected clients information and return the information
        System.out.println("Connected clients:");
        for (Map.Entry<String, Socket> entry : clientMap.entrySet()) {
            Socket socket = entry.getValue();
            System.out.println(entry.getKey() + ": " + socket.getInetAddress().getHostAddress() + " " + socket.getPort());
        }
    }
}

