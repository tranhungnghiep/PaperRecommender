/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.evaluation;

import java.util.List;

/**
 * This class content methods for computing metric related to Recall. 
 * Ref:
 * 1. http://en.wikipedia.org/wiki/Precision_and_recall
 * 2. http://www.stanford.edu/class/cs276/handouts/EvaluationNew-handout-6-per.pdf
 * Method: 
 * - computeRecall
 * @author Vinh-PC
 */
public class Recall {

    // Prevent instantiation.
    private Recall() {
    }

    /**
     * This method computes recall based on relevant documents retrieved and
     * total relevant documents
     *
     * @param rankList
     * @param groundTruth
     * @return rec
     */
    public static double computeRecall(List rankList, List groundTruth) {
        if ((rankList == null) || (groundTruth == null) || (rankList.isEmpty()) || (groundTruth.isEmpty())) {
            return 0.0;
        }
        
        double recall = 0.0;

        for (int i = 0; i < groundTruth.size(); i++) {
            if (rankList.contains(groundTruth.get(i))) {
                recall++;
            }
        }
        
        return (double) recall / groundTruth.size();
    }

    /**
     * 
     * @param rankList
     * @param groundTruth
     * @param topN
     * @return 
     */
    public static double computeRecallTopN(List rankList, List groundTruth, int topN) {
        if ((rankList == null) || (groundTruth == null) || (rankList.isEmpty()) || (groundTruth.isEmpty()) || (topN <= 0)) {
            return 0.0;
        }
        
        double recallN = 0.0;

        // count to rank list size but divide by original top n.
        int nN = topN;
        if (nN > rankList.size()) {
            nN = rankList.size();
        }
        
        List topNRankList = rankList.subList(0, nN);
        for (int i = 0; i < groundTruth.size(); i++) {
            if (topNRankList.contains(groundTruth.get(i))) {
                recallN++;
            }
        }
        
        return (double) recallN / groundTruth.size();
    }
}
