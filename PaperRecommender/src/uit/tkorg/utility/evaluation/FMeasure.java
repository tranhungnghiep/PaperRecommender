/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.evaluation;

import java.util.List;

/**
 *
 * @author THNghiep
 */
public class FMeasure {

    // Prevent instantiation.
    private FMeasure() {
    }

    /**
     * This method computes F1 measure.
     *
     * @param rankList
     * @param groundTruth
     * @return F1
     */
    public static double computeF1(List rankList, List groundTruth) {
        return computeFMeasure(rankList, groundTruth, 1);
    }

    /**
     * This method computes F1 measure.
     *
     * @param rankList
     * @param groundTruth
     * @param beta
     * @return F measure
     */
    public static double computeFMeasure(List rankList, List groundTruth, double beta) {
        if ((rankList == null) || (groundTruth == null) || (rankList.isEmpty()) || (groundTruth.isEmpty()) || (beta < 0)) {
            return 0.0;
        }
        
        double precision = Precision.computePrecision(rankList, groundTruth);
        double recall = Recall.computeRecall(rankList, groundTruth);
        double f = ((1 + beta) * precision * recall) / ((beta * beta * precision) + recall);
        if (Double.isNaN(f) || (Double.isInfinite(f))) {
            f = 0.0;
        }

        return f;
    }
    
}
