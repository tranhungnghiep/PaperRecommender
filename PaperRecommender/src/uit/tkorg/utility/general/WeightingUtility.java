/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.general;

import ir.vsr.HashMapVector;

/**
 *
 * @author THNghiep
 */
public class WeightingUtility {

    // Prevent instantiation.
    private WeightingUtility() {}

    /**
     * Compute cosine angle of two vector. Use HashMapVector from ir package as
     * vector datatype: need to examine how to use correctly.
     *
     * @param v1
     * @param v2
     * @return cosine.
     */
    public static double computeCosine(HashMapVector v1, HashMapVector v2) throws Exception {
        double cosine = 0;
        
        cosine = v1.cosineTo(v2);
        
        cosine = NumericUtility.standardizeSimilarityValue(cosine);

        return cosine;
    }

    /**
     * Compute RPY of two paper. RPY = 1/(|y1-y2| + c).
     *
     * @param y1 publication year
     * @param y2 publication year
     * @param c a constant, e.g., 0.9
     * @return RPY
     */
    public static double computeRPY(int y1, int y2, double c) throws Exception {
        double rpy;
        if ((y1 == -1) || (y2 == -1)) {
            rpy = 0.5;
        } else {
            rpy = 1 / (Math.abs(y1 - y2) + c);
        }
        return rpy;
    }

    /**
     * This method compute the forgetting factor. 
     * When y1 or y2 is invalid, return 0.5
     * When y1 = y2, return 1
     * 
     * @param y1 latest publication time
     * @param y2 considering publication time
     * @param gamma forgetting coefficient.
     * @return forgetting factor as exp(- delta time * gamma).
     * @throws Exception 
     */
    public static double computeForgettingFactor(int y1, int y2, double gamma) throws Exception {
        double ff;
        if ((y1 == -1) || (y2 == -1)) {
            ff = 0.5;
        } else {
            ff = 1 / Math.exp(gamma * Math.abs(y1 - y2));
        }
        return ff;
    }
}
