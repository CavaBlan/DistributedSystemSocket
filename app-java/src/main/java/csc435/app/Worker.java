package csc435.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Worker implements Runnable {
    private IndexStore store;
    private Socket clientSocket;
    

    public Worker(Socket clientSocket, IndexStore store) {
        this.store = store;
        // TO-DO implement constructor
        this.clientSocket = clientSocket;
    }
    
    @Override
    public void run() {
        // TO-DO receive index and search commands from the client until the client disconnects
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            String inputLine;
            boolean isIndexing = false;
            String fileName = null;
            Map<String, Integer> wordCounts = new HashMap<String, Integer>();

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("INDEX")) {
                    isIndexing = true;
                    fileName = inputLine.substring(6); // Extract file name
                    wordCounts.clear(); // Clean wordCounts before each load to globleIndex
                // Make sure it is currently not indexing
                } else if (!isIndexing && inputLine.startsWith("SEARCH")) {
                    List<String> wordsList = new ArrayList<>();
                    while (!(inputLine = in.readLine()).equals("END_OF_SEARCH")) {
                        wordsList.add(inputLine);
                    }
//                    for (String word : wordsList) {
//                        System.out.println(word);
//                    }
                    if (!wordsList.isEmpty()) { 
                        sendSearchResults(wordsList, out); // Process send search logic
                    } else {
                        continue;
                    }
                } else if (inputLine.equals("END_OF_INDEX")) {
                    if (isIndexing && fileName != null) {
                        store.insertIndex(fileName, wordCounts);
                    }
                    isIndexing = false;
                    fileName = null;
                    wordCounts.clear();
                    out.println("INDEXING_COMPLETE");
                } else {
                    // During indexing, words: count
                    String[] parts = inputLine.split(":");
                    if (parts.length == 2) {
                        wordCounts.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }        
        } catch (IOException e) {
            System.err.println("An exception occurred while processing a client request: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.err.println("Error while shutting down the resource");
            }
            
        }
    }
    
    private void sendSearchResults(List<String> wordsList, PrintWriter out) {
        for (String word : wordsList) {
            Map<String, Integer> occurrences = store.lookupIndex(word);
            for (Map.Entry<String, Integer> entry : occurrences.entrySet()) {
                // Sends the address and occurrence of the word to the client
                out.println(entry.getKey() + ":" + entry.getValue());
//            	System.out.println(entry.getKey() + " " + entry.getValue());
            }
        }
        out.println("END_OF_RESULTS"); // Mark the end of search results
    }
}

