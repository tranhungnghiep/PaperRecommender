/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.utility.general;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class contents all general utilities.
 * @author THNghiep
 */
public class GeneralUtility {

    // Prevent instantiation.
    private GeneralUtility() {}

    /**
     * This method sort similarities descending
     *
     * @param hashMap
     * @return sortMap
     */
    public static LinkedHashMap sortHashMap(HashMap<String, Double> hashMap) throws Exception {
        List list = new LinkedList(hashMap.entrySet());
        
        //sort list based on comparator
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        
        // Reverse order to be descending.
        Collections.reverse(list);
        
        //put sorted list into map again
        LinkedHashMap sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        
        return sortedMap;
    }
    
    /**
     * This method standardizes the similarity value of cosine.
     * Value range would be between 0 and 1.
     * 
     * @param d
     * @return standardized value of d.
     */
    public static double standardizeSimilarityValue(double d) {
        
        if (d < 0) {
            d = 0.0;
        } else if (d > 1) {
            d = 1.0;
        } else if (Double.isNaN(d)) {
            d = 0.0;
        }
        
        return d;
    }

    /**
     * Check String is Numeric or String
     *
     * @param strNum
     * @return
     */
    public static boolean isNum(String strNum) {
        boolean ret = true;
        try {

            Double.parseDouble(strNum);

        } catch (NumberFormatException e) {
            ret = false;
        }
        return ret;
    }
    
    /**
     *
     * @param dir
     * @return list file
     */
    public static List<String> getPathFile(File dir) throws Exception {
        File[] files = dir.listFiles();
        List listfile = new ArrayList<String>();
        String name = null;
        for (File file : files) {
            if (file.isDirectory()) {
                getPathFile(file);
            } else if (file.isFile()) {
                name = file.getName().toString();
            }
            if ("*.txt".equals(name)) {
                listfile.add(file.getAbsolutePath());
            }
        }
        return listfile;
    }
}
