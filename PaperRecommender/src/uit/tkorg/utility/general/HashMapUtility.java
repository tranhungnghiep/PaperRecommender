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
import uit.tkorg.pr.model.Author;

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
    public static LinkedHashMap getSortedMapAscending(HashMap map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                    .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        LinkedHashMap result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * getSortedMapDescending
     * @param map
     * @return
     */
    public static LinkedHashMap getSortedMapDescending(HashMap map) {
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
            Map.Entry entry = (Map.Entry)it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
    
    public static void linearCombineTwoHashMap(HashMap<String, Float> inputHM1, 
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
            
            Float linearCombinationValue = value1 * alpha + value2 * (1 - alpha);
            outputHM.put(key, linearCombinationValue);
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
}
