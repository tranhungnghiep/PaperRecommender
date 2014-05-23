/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.evaluation;

import java.util.List;

/**
 * This class content methods for computing metric related to ReciprocalRank.
 * Caller needs to get mean of reciprocal rank later.
 * Ref:
 * 1. https://en.wikipedia.org/wiki/Mean_reciprocal_rank
 * 2. http://www.stanford.edu/class/cs276/handouts/EvaluationNew-handout-6-per.pdf
 * @author THNghiep
 */
public class ReciprocalRank {

    // Prevent instantiation.
    private ReciprocalRank() {
    }

    /**
     * This method computes the reciprocal rank of 1 list.
     * @param rankList
     * @param idealList
     * @return reciprocal rank.
     */
    public static double computeRR(List rankList, List idealList) throws Exception {

        for (int i = 0; i < rankList.size(); i++) {
            if (idealList.contains(rankList.get(i))) {
                // Reciprocal of first relevant item position.
                return (double) 1 / (i + 1);
            }
        }

        return 0;
    }
}
