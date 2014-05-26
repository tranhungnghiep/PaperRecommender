/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.general;

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
 *
 * @author THNghiep
 */
public class NumericUtility {
    
    // Prevent instantiation.
    private NumericUtility() {}

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
}
