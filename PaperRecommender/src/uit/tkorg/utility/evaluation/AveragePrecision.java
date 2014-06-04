/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.evaluation;

import java.util.List;

/**
 * This class content methods for computing metric related to AveragePrecision.
 * Ref:
 * 1.http://www.stanford.edu/class/cs276/handouts/EvaluationNew-handout-6-per.pdf
 * 2.http://essay.utwente.nl/59711/1/MA_thesis_J_de_Wit.pdf 
 * 
 * Method: - computeAP
 *
 * @author Vinh-PC
 */
public class AveragePrecision {

    // Prevent instantiation.
    private AveragePrecision() {
    }

    /**
     * This method computes average precision with threshold k.
     *
     * @param rankList
     * @param idealList
     * @param k
     * @return
     */
    public static double computeAP(List rankList, List idealList, int k) {
        if ((rankList == null) || (idealList == null) || (k <= 0)) {
            return 0.0;
        }
        
        double ap = 0.0;
        
        
        

        int nK = k;
        if (nK > rankList.size()) {
            nK = rankList.size();
        }

        for (int i = 0; i < k; i++) {
            ap += Precision.computePrecisionTopN(rankList, idealList, i + 1);
        }

        return (double) ap / nK;
    }
}
