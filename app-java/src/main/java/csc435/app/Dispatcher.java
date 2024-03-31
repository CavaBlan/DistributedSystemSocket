package csc435.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Dispatcher implements Runnable {
    private ServerSideEngine engine;
//    private int port; // listener port
    private ServerSocket serverSocket; 

    // ServerSideEngine instance and the port number
    public Dispatcher(ServerSideEngine engine, ServerSocket serverSocket) { 
        this.engine = engine;
        // TO-DO implement constructor
        this.serverSocket = serverSocket;
    }
    
   
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted() && !serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept(); 
//                System.out.println("New client connected");
                engine.spawnWorker(clientSocket);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

