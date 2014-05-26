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
     * @param idealList
     * @return rec
     */
    public static double computeRecall(List rankList, List idealList) {
        double rec = 0;

        for (int i = 0; i < rankList.size(); i++) {
            if (idealList.contains(rankList.get(i))) {
                rec++;
            }
        }
        
        return (double) rec / idealList.size();
    }
}
