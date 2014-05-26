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
 * - computePrecisionK
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
     * @param idealList
     * @return
     */
    public static double computePrecision(List rankList, List idealList) {
        double prec = 0;

        for (int i = 0; i < rankList.size(); i++) {
            if (idealList.contains(rankList.get(i))) {
                prec++;
            }
        }

        return (double) prec / rankList.size();
    }

    /**
     * This method computes precision with threshold k based on relevant
     * documents retrieved and k retrieved documents
     *
     * @param rankList
     * @param idealList
     * @param k
     * @return
     */
    public static double computePrecisionK(List rankList, List idealList, int k) {
        double preck = 0;

        if (k > rankList.size()) {
            k = rankList.size();
        }

        for (int i = 0; i < k; i++) {
            if (idealList.contains(rankList.get(i))) {
                preck++;
            }
        }

        return (double) preck / k;
    }
}
