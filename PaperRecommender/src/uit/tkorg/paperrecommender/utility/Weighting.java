/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.utility;

import ir.vsr.HashMapVector;

/**
 *
 * @author THNghiep
 */
public class Weighting {

    // Prevent instantiation.
    private Weighting() {}

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
        
        cosine = GeneralUtility.standardizeSimilarityValue(cosine);

        return cosine;
    }

    /**
     * Compute RPY of two paper. RPY = 1/|y1-y2|.
     *
     * @param y1
     * @param y2
     * @return RPY
     */
    public static double computeRPY(int y1, int y2) throws Exception {
        double rpy;
        if ((y1 == 0) || (y2 == 0)) {
            rpy = 1;
        } else if (y1 == y2) {
            rpy = 10 / 9;
        } else {
            rpy = 1 / Math.abs(y1 - y2);
        }
        return rpy;
    }
}
