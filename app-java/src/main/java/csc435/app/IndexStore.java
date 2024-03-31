package csc435.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IndexStore {
	// Outer Map keys are words, inner Map keys are file name and values are number of occurrences
    private Map<String, Map<String, Integer>> globalIndex = new HashMap<>();

    public IndexStore() {
        // TO-DO implement constructor
    	this.globalIndex = new HashMap<>();
    }

    public synchronized void insertIndex(String fileName, Map<String, Integer> localIndex) {
        for (Map.Entry<String, Integer> entry : localIndex.entrySet()) {
            String word = entry.getKey();
            Integer count = entry.getValue();
//            System.out.println(word + " " + count);
            // Check if the globalIndex already contains the word
            if (!globalIndex.containsKey(word)) {
                // If not, create a new HashMap to store
                globalIndex.put(word, new HashMap<String, Integer>());
            }
            Map<String, Integer> fileCounts = globalIndex.get(word);

            // Check if fileCounts already contains the filename
            if (!fileCounts.containsKey(fileName)) {
                // If not, directly add the filename and count
                fileCounts.put(fileName, count);
            } else {
                // If it does, update the original count + new count
                int existingCount = fileCounts.get(fileName);
                fileCounts.put(fileName, existingCount + count);
            }
        }
        
        //test
//        for (Map.Entry<String, Map<String, Integer>> entry : globalIndex.entrySet()) {
//            String outerKey = entry.getKey();
//            Map<String, Integer> innerMap = entry.getValue();
//            System.out.println("Word === " + outerKey);
//            for (Map.Entry<String, Integer> innerEntry : innerMap.entrySet()) {
//                String innerKey = innerEntry.getKey();
//                Integer value = innerEntry.getValue();
//                System.out.println(value + " --- " + innerKey);
//            }
//        }
    }
    
    public synchronized Map<String, Integer> queryGlobalIndex(String term) {
        // Check that the globalIndex contains this word
        if (globalIndex.containsKey(term)) {
            return globalIndex.get(term);
        } else {
            return new HashMap<>();
        }
    }

    // Search words
    public synchronized Map<String, Integer> lookupIndex(String term) {
        // TO-DO implement index lookup method
        Map<String, Integer> result = globalIndex.get(term);
        return result;
    }
    
    public synchronized Set<String> getAllWords() {
        return globalIndex.keySet();
    }
}

