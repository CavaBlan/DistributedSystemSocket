package csc435.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ClientSideEngine {
    // TO-DO keep track of the connection
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    public ClientSideEngine() {
        
        // TO-DO implement constructor
    }

    public void indexFiles(String datasetPath) {
        File datasetDir = new File(datasetPath);
        if (!datasetDir.exists() || !datasetDir.isDirectory()) {
            System.out.println("The specified path is not a valid directory.");
            return;
        }

        // Iterate the directory and process each txt file
        indexDirectory(datasetDir);
    }

    private void indexDirectory(File directory) {
    	long startTime = System.currentTimeMillis();
    	
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                indexDirectory(file); // Process subdirectories recursively
            } else if (file.isFile() && file.getName().endsWith(".txt")) {
//            	System.out.println("Processing file: " + file.getPath());
                // Create an index for the txt file
                HashMap<String, Integer> wordCount = new HashMap<>();
                try (Scanner scanner = new Scanner(file)) {
                    scanner.useDelimiter("[^a-zA-Z0-9]+"); // separator
                    while (scanner.hasNext()) {
                    	String word = scanner.next();
                        if (wordCount.containsKey(word)) {                        
                            wordCount.put(word, wordCount.get(word) + 1);
                        } else {
                            wordCount.put(word, 1);
                        } 
                    } 
                    // Gets the parent directory name and file name of the file
                    String parentDirName = file.getParentFile().getName();
                    String filePath = parentDirName + "/" + file.getName();
                    // Construct and send index messages to the server
                    sendIndexDataToServer(filePath, wordCount);
//                    sendIndexDataToServer(file.getPath(), wordCount);
                } catch (FileNotFoundException e) {
                    System.err.println("Error reading file");
                    e.printStackTrace();
                }
            }
            
        }
        long endTime = System.currentTimeMillis(); 
        long totalTime = endTime - startTime; 
        double totalTimeInSeconds = (double) totalTime / 1000.0;
        System.out.println("Completed indexing in " + totalTimeInSeconds + " seconds");
    }
    
    private void sendIndexDataToServer(String fileName, HashMap<String, Integer> wordCount) {
        // Send file name to server
    	// I originally wanted to use fileName as one of the values, 
    	// but this would have increased the amount of data by a third
        out.println("INDEX " + fileName);
        // Send occurrence of words data in the format of "word1:count1 word2:count2 ..."
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            out.println(entry.getKey() + ":" + entry.getValue());
        }
        // Add a specific end tag so that the server knows that a send has ended
        out.println("END_OF_INDEX");
    }
    
    public void searchFiles(List<String> wordsList) {
        // TO-DO implement search files method
        // for each term contact the server to retrieve the list of documents that contain the word
        // combine the results of a multi-term query
        // return top 10 results
        out.println("SEARCH");
        long startTime = System.nanoTime(); 
        for (String word : wordsList) {
            out.println(word);
        }
        out.println("END_OF_SEARCH"); // The search term is sent

        try {
            // Store the file and the corresponding total number of occurrences
            Map<String, Integer> fileScores = new HashMap<>();
            Map<String, Integer> fileMatchCount = new HashMap<>();

            String line;
            while (!(line = in.readLine()).equals("END_OF_RESULTS")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    // The server sends back "word:fileName:frequency"
                    String fileName = parts[0];
                    Integer count = Integer.parseInt(parts[1]);

                    // Update total occurrences
                    if (!fileScores.containsKey(fileName)) {
                        fileScores.put(fileName, count);
                    } else {
                        fileScores.put(fileName, fileScores.get(fileName) + count);
                    }

                    // Update match count
                    if (!fileMatchCount.containsKey(fileName)) {
                        fileMatchCount.put(fileName, 1);
                    } else {
                        fileMatchCount.put(fileName, fileMatchCount.get(fileName) + 1);
                    }
                }
            }

            // Filter files that match all words
            List<Map.Entry<String, Integer>> allMatchFiles = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : fileScores.entrySet()) {
                String fileName = entry.getKey();
                if (fileMatchCount.get(fileName) == wordsList.size()) {
                    allMatchFiles.add(entry);
                }
            }

            // Sort by comparator
            Collections.sort(allMatchFiles, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue()); // Descending order
                }
            });

            // Print top 10
            printTop(allMatchFiles);
//            System.out.println("Search results (top 10):");
//            int printCount = Math.min(allMatchFiles.size(), 10);
//            for (int i = 0; i < printCount; i++) {
//                Map.Entry<String, Integer> file = allMatchFiles.get(i);
//                System.out.println("* " + file.getKey() + " " + file.getValue());
//            }
            
            long endTime = System.nanoTime(); 
            long duration = endTime - startTime; 
            System.out.println("Search completed in " + (duration / 1000000.0) + " milliseconds");
        } catch (IOException e) {
            System.err.println("Error reading search results from server.");
            e.printStackTrace();
        }
    }
    
    // Printing method
    public void printTop(List<Map.Entry<String, Integer>> allMatchFiles) {
    	System.out.println("Search results (top 10):");
        int printCount = Math.min(allMatchFiles.size(), 10);
        for (int i = 0; i < printCount; i++) {
            Map.Entry<String, Integer> file = allMatchFiles.get(i);
            System.out.println("* " + file.getKey() + " " + file.getValue());
        }
    }

    public void openConnection(String address, int port) {
        // TO-DO implement connect to server
        // create a new TCP/IP socket and connect to the server
        try {
            this.socket = new Socket(address, port);
            System.out.println("Connection successful!");
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    public void closeConnection() {
        // TO-DO implement disconnect from server
        // close the TCP/IP socket
        try {
            if (socket != null) {
                socket.close();
                System.out.println("Connection closed.");
            }
        } catch (IOException e) {
            System.err.println("Error closing the connection: " + e.getMessage());
        }
    }
}


