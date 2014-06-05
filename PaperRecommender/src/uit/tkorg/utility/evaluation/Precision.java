/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.evaluation;

import java.util.List;

/**
 * This class content methods for computing metric related to Precision. 
 * Ref: 
 * 1. http://en.wikipedia.org/wiki/Precision_and_recall 
 * 2. http://www.stanford.edu/class/cs276/handouts/EvaluationNew-handout-6-per.pdf
 * Method: 
 * - computePrecision 
 * - computePrecisionTopN
 *
 * @author Vinh-PC
 */
public class Precision {

    // Prevent instantiation.
    private Precision() {
    }

    /**
     * This method computes precision based on relevant documents retrieved and
     * total retrieved documents
     *
     * @param rankList
     * @param groundTruth
     * @return
     */
    public static double computePrecision(List rankList, List groundTruth) {
        if ((rankList == null) || (groundTruth == null) || (rankList.isEmpty()) || (groundTruth.isEmpty())) {
            return 0.0;
        }
        
        // true positive
        double tp = 0.0;

        for (int i = 0; i < rankList.size(); i++) {
            if (groundTruth.contains(rankList.get(i))) {
                tp++;
            }
        }

        // ranklist size = true positive + false positive.
        return (double) tp / rankList.size();
    }

    /**
     * This method computes precision with threshold k based on relevant
     * documents retrieved and k retrieved documents
     *
     * @param rankList
     * @param groundTruth
     * @param topN
     * @return
     */
    public static double computePrecisionTopN(List rankList, List groundTruth, int topN) {
        if ((rankList == null) || (groundTruth == null) || (rankList.isEmpty()) || (groundTruth.isEmpty()) || (topN <= 0)) {
            return 0.0;
        }
        
        // true positive
        double tp = 0.0;

        // count to rank list size but divide by original top n.
        int nN = topN;
        if (nN > rankList.size()) {
            nN = rankList.size();
        }

        for (int i = 0; i < nN; i++) {
            if (groundTruth.contains(rankList.get(i))) {
                tp++;
            }
        }

        // topN = true positive + false positive.
        return (double) tp / topN;
    }
}
