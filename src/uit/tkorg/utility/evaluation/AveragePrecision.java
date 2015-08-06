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
 * 1. http://en.wikipedia.org/wiki/Information_retrieval#Average_precision
 * 2. http://fastml.com/what-you-wanted-to-know-about-mean-average-precision/
 * Method: - computeAPK
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
     * @param groundTruth
     * @param k
     * @return
     */
    public static double computeAPK(List rankList, List groundTruth, int k) {
        if ((rankList == null) || (groundTruth == null) || (rankList.isEmpty()) || (groundTruth.isEmpty()) || (k <= 0)) {
            return 0.0;
        }
        
        // return value.
        double apk = 0.0;

        // only consider the real ranklist size.
        int nK = k;
        if (nK > rankList.size()) {
            nK = rankList.size();
        }
        
        // sum of precision at k.
        int num_hits = 0;
        for (int i = 0; i < nK; i++) {
            // Only interate to nK = real ranklist size
            // => if list is too small, the result is not changed when k changes.
            // E.g., if nK == 3 and all 3 item is in ground truth, MAP@5, 10, 15... = 1.
            // This is correct, according to the definition of MAP (https://www.kaggle.com/wiki/MeanAveragePrecision)
            if (groundTruth.contains(rankList.get(i))) {
                num_hits += 1;
                apk += (double) num_hits / (i + 1);
            }
        }
        
        // multiply by change in recall at each step, or average by number of relevant document.
        int numRelevantDocument = 0;
        if (nK < groundTruth.size()) {
            numRelevantDocument = nK;
        } else {
            numRelevantDocument = groundTruth.size();
        }
        apk = (double) apk / numRelevantDocument;
        
        return apk;
    }
}
