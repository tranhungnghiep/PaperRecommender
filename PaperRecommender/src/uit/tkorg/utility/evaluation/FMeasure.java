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
     * @param idealList
     * @return F1
     */
    public static double computeF1(List rankList, List idealList) {
        return computeFMeasure(rankList, idealList, 1);
    }

    /**
     * This method computes F1 measure.
     *
     * @param rankList
     * @param idealList
     * @param beta
     * @return F measure
     */
    public static double computeFMeasure(List rankList, List idealList, double beta) {
        if ((rankList == null) || (idealList == null) || (beta < 0)) {
            return 0.0;
        }
        
        double precision = Precision.computePrecision(rankList, idealList);
        double recall = Recall.computeRecall(rankList, idealList);

        return ((1 + beta) * precision * recall) / ((beta * beta * precision) + recall);
    }
    
}
