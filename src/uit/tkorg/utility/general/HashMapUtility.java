/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.general;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author tin
 */
public class HashMapUtility {
    
    private static Integer countThread = 0;

    /**
     * getSortedMapAscending
     * @param map
     * @return
     */
    public static LinkedHashMap getSortedMapAscending(HashMap map) throws Exception {
        if (map == null) {
            return null;
        }
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                // If there is map entry with null value, value.compareTo() raise Null Pointer Exception.
                // Only raise exception, not modify data 
                // -> have to guarantee that data is not null beforehand.
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                    .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        LinkedHashMap result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * getSortedMapDescending
     * @param map
     * @return
     */
    public static LinkedHashMap getSortedMapDescending(HashMap map) throws Exception {
        if (map == null) {
            return null;
        }
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                    .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        LinkedHashMap result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
    
    /**
     * Linear combine: result = alpha * x + (1 - alpha) * y.
     * @param inputHM1
     * @param inputHM2
     * @param alpha
     * @param outputHM
     * @throws Exception 
     */
    public static void combineLinearTwoHashMap(HashMap<String, Float> inputHM1, 
            HashMap<String, Float> inputHM2, 
            float alpha, 
            HashMap<String, Float> outputHM) throws Exception {

        Set<String> keys = new HashSet<> (inputHM1.keySet());
        keys.addAll(new HashSet<> (inputHM2.keySet()));
        for (String key : keys) {
            // If there is key not in one HashMap or null element in one HashMap, 
            // auto convert it to 0, not raise exception.
            // E.g. 1: CF list contains paper cited before 2006 by everyone, then filtered by paper in testset, 
            // E.g. 2: Trusted paper list contains paper written or cited by related author before 2006, then filtered by paper in testset, 
            // While, CBF list contains all paper cited from 2006 (uncited before 2006) by authors in testset.
            // -> So CBF list has some items not present in CF and trusted paper list.
            // -> auto converting to 0 means the result depends on f(CBFSim value).
            // E.g. 3: Coauthor is not Citationauthor and vice versa.
            Float value1 = inputHM1.get(key);
            if (value1 == null) {
                value1 = Float.valueOf(0);
            }
            Float value2 = inputHM2.get(key);
            if (value2 == null) {
                value2 = Float.valueOf(0);
            }
            
            Float combinedValue = value1 * alpha + value2 * (1 - alpha);
            outputHM.put(key, combinedValue);
        }

        // This method could be call in a parallel runable, so this part will count number of threads.
        synchronized (getCountThread()) {
            System.out.println("Thread No. " + countThread++ + " Done. " + (new Date(System.currentTimeMillis()).toString()));
        }
    }

    public static void combineBasedOnConfidenceTwoHashMap(HashMap<String, Float> inputHM1, 
            HashMap<String, Float> inputHM2, 
            HashMap<String, Float> outputHM) throws Exception {

        Set<String> keys = new HashSet<> (inputHM1.keySet());
        keys.addAll(new HashSet<> (inputHM2.keySet()));
        for (String key : keys) {
            Float value1 = inputHM1.get(key);
            if (value1 == null) {
                value1 = Float.valueOf(0);
            }
            Float value2 = inputHM2.get(key);
            if (value2 == null) {
                value2 = Float.valueOf(0);
            }
            
            Float combinedValue = 
                    (value1 * value1 + value2 * value2) 
                    / (value1 + value2);
            outputHM.put(key, combinedValue);
        }

        synchronized (getCountThread()) {
            System.out.println("Thread No. " + countThread++ + " Done. " + (new Date(System.currentTimeMillis()).toString()));
        }
    }

    public static void combineBasedOnConfidenceAndLinearTwoHashMap(HashMap<String, Float> inputHM1, 
            HashMap<String, Float> inputHM2, 
            float alpha, 
            HashMap<String, Float> outputHM) throws Exception {

        Set<String> keys = new HashSet<> (inputHM1.keySet());
        keys.addAll(new HashSet<> (inputHM2.keySet()));
        for (String key : keys) {
            Float value1 = inputHM1.get(key);
            if (value1 == null) {
                value1 = Float.valueOf(0);
            }
            Float value2 = inputHM2.get(key);
            if (value2 == null) {
                value2 = Float.valueOf(0);
            }
            
            Float combinedValue = 
                    (value1 * value1 * alpha + value2 * value2 * (1 - alpha)) 
                    / (value1 + value2);
            outputHM.put(key, combinedValue);
        }

        synchronized (getCountThread()) {
            System.out.println("Thread No. " + countThread++ + " Done. " + (new Date(System.currentTimeMillis()).toString()));
        }
    }

    /**
     * Result = ((1 - (x-1)^2) + (1 - (y-1)^2)) / (x + y)
     * @param inputHM1
     * @param inputHM2
     * @param outputHM
     * @throws Exception 
     */
    public static void combineBasedOnConfidenceTwoHashMapV2(HashMap<String, Float> inputHM1, 
            HashMap<String, Float> inputHM2, 
            HashMap<String, Float> outputHM) throws Exception {

        Set<String> keys = new HashSet<> (inputHM1.keySet());
        keys.addAll(new HashSet<> (inputHM2.keySet()));
        for (String key : keys) {
            Float value1 = inputHM1.get(key);
            if (value1 == null) {
                value1 = Float.valueOf(0);
            }
            Float value2 = inputHM2.get(key);
            if (value2 == null) {
                value2 = Float.valueOf(0);
            }
            
            Float combinedValue = 
                    ((1 - (value1 - 1) * (value1 - 1)) + (1 - (value2 - 1) * (value2 - 1)))
                    / (value1 + value2);
            outputHM.put(key, combinedValue);
        }

        synchronized (getCountThread()) {
            System.out.println("Thread No. " + countThread++ + " Done. " + (new Date(System.currentTimeMillis()).toString()));
        }
    }

    public static void combineBasedOnConfidenceAndLinearTwoHashMapV2(HashMap<String, Float> inputHM1, 
            HashMap<String, Float> inputHM2, 
            float alpha, 
            HashMap<String, Float> outputHM) throws Exception {

        Set<String> keys = new HashSet<> (inputHM1.keySet());
        keys.addAll(new HashSet<> (inputHM2.keySet()));
        for (String key : keys) {
            Float value1 = inputHM1.get(key);
            if (value1 == null) {
                value1 = Float.valueOf(0);
            }
            Float value2 = inputHM2.get(key);
            if (value2 == null) {
                value2 = Float.valueOf(0);
            }
            
            Float combinedValue = 
                    (alpha * (1 - (value1 - 1) * (value1 - 1)) 
                    + (1 - alpha) * (1 - (value2 - 1) * (value2 - 1)))
                    / (value1 + value2);
            outputHM.put(key, combinedValue);
        }

        synchronized (getCountThread()) {
            System.out.println("Thread No. " + countThread++ + " Done. " + (new Date(System.currentTimeMillis()).toString()));
        }
    }

    /**
     * @return the countThread
     */
    public static Integer getCountThread() {
        return countThread;
    }

    /**
     * @param aCountThread the countThread to set
     */
    public static void setCountThread(Integer aCountThread) {
        countThread = aCountThread;
    }

    public static void minNormalizeHashMap(HashMap<String, Float> hm) throws Exception {
        if ((hm == null) || (hm.isEmpty())) {
            return;
        }
        
        // Avoid mutable.
        // Have to guarantee that data is not null beforehand.
        float min = Collections.min(hm.values());
        float max = Collections.max(hm.values());
        
        if (min == max) {
            for (String id : hm.keySet()) {
                hm.put(id, 0.5f);
            }
        } else {
            for (String id : hm.keySet()) {
                hm.put(id, (hm.get(id) - min) / (max - min));
            }
        }
    }

    // Start range is auto dertermined by [min, max]
    public static void scaleToRangeABHashMap(HashMap<String, Float> hm, float a, float b) throws Exception {
        if ((hm == null) || (hm.isEmpty()) || (a > b)) {
            return;
        }
        
        // Avoid mutable.
        // Have to guarantee that data is not null beforehand.
        float min = Collections.min(hm.values());
        float max = Collections.max(hm.values());
        if ((min == a) && (max == b)) {
            return;
        }
        
        if (min == max) {
            for (String id : hm.keySet()) {
                hm.put(id, (a + b) / 2);
            }
        } else {
            for (String id : hm.keySet()) {
                hm.put(id, (hm.get(id) - min) / (max - min) * (b - a) + a);
            }
        }
    }

    public static void filterHashMap(HashMap<String, Float> inputHM, 
            HashMap<String, Float> filteringHM, 
            HashMap<String, Float> outputHM) throws Exception {

        // e.g., filter trust list by cbf, or re-sort cbf by trust:
        // filteringHM = trustedPaperHM
        // inputHM = cbfSimHM
        for (String paperId : filteringHM.keySet()) {
            Float score = inputHM.get(paperId);
            if (score == null) {
                // Also accept item in filteringHM but not in inputHM.
                // Set score to 0, so that it is behind items in inputHM.
                // e.g., trustedPaper not in testset.
                score = new Float(0);
            } else {
                // Mutable value, create new one.
                score = new Float(score);
            }
            // Only put items in filteringHM, so outputHM may be empty (initialized, not null).
            outputHM.put(paperId, score);
        }

        // This method could be call in a parallel runable, so this part will count number of threads.
        synchronized (getCountThread()) {
            System.out.println("Thread No. " + countThread++ + " Done. " + (new Date(System.currentTimeMillis()).toString()));
        }
    }
}
