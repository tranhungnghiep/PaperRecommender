/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.utility.evaluation;

import java.util.List;

/**
 * This class content methods for computing metric related to NDCG.
 * Ref:
 * 1. https://en.wikipedia.org/wiki/Discounted_cumulative_gain
 * 2. http://www.stanford.edu/class/cs276/handouts/EvaluationNew-handout-6-per.pdf
 * 3. https://www.kaggle.com/wiki/NormalizedDiscountedCumulativeGain
 * Method:
 * - NDCG
 * - DCG
 * - CG
 * @author THNghiep
 */
public class NDCG {
    
    // Prevent instantiation.
    private NDCG() {
    }
    
    /**
     * This method compute the basic NDCG metric.
     * Relevance score: binary, means relevant item: 1, irrelevant item: 0.
     * @param rankList
     * @param idealList
     * @param k
     * @return ndcg
     */
    public static double computeNDCG(List rankList, List idealList, int k) {
        double ndcg = 0;
        
        ndcg = computeDCG(rankList, idealList, k) / computeIDCG(k);
        
        return ndcg;
    }
    
    /**
     * This method compute the standard basic binary DCG metric.
     * @param rankList
     * @param idealList
     * @param k
     * @return dcg
     */
    public static double computeDCG(List rankList, List idealList, int k) {
        double dcg = 0;
        
        // Items out of ranklist are irrelevant items and gain 0.
        // Reduce k to ranklist size to avoid out of range error and to imply 0 gain.
        if (k > rankList.size()) {
            k = rankList.size();
        }
        
        for (int i = 0; i < k; i++) {
            Object item = rankList.get(i);
            if (idealList.contains(item)) {
                dcg += Math.log(2) / Math.log(i + 2);
            }
        }
        
        return dcg;
    }

    /**
     * This method computes DCG metric of rankList with ideal order.
     * The assumption here is: the result list could content all items, so it could content k relevant items.
     * @param k
     * @return idcg
     */
    public static double computeIDCG(int k) {
        double idcg = 0;

        // With our assumption, the ideal list contents k relevant items.
        // So we sum up all the item.
        for (int i = 0; i < k; i++) {
            idcg += Math.log(2) / Math.log(i + 2);
        }
        
        return idcg;
    }
    
    /**
     * This method computes the cumulated gain, i.e., it does not consider position of ranked items.
     * idealList used to decide relevance.
     * @param rankList
     * @param idealList
     * @param k
     * @return cg
     */
    public static double computeCG(List rankList, List idealList, int k) {
        double cg = 0;
        
        // Items out of ranklist are irrelevant items and gain 0.
        // Reduce k to ranklist size to avoid out of range error and to imply 0 gain.
        if (k > rankList.size()) {
            k = rankList.size();
        }
        
        for (int i = 0; i < k; i++) {
            Object item = rankList.get(i);
            if (idealList.contains(item)) {
                cg += 1;
            }
        }
        
        return cg;
    }
}
