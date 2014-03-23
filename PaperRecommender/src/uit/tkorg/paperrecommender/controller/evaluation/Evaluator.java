/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.controller.evaluation;

import java.util.HashMap;
import uit.tkorg.paperrecommender.model.Author;
import uit.tkorg.paperrecommender.utility.evaluation.NDCG;

/**
 * This class handles all logics for evaluation of recommendation results.
 * Method:
 * - NDCG: 
 * + input: authors' ground truth list and recommendation list, n where NDCG computed at.
 * + output: NDCG.
 * - MRR: 
 * + input: authors' ground truth list and recommendation list.
 * + output: MRR.
 * @author THNghiep
 */
public class Evaluator {

    HashMap<String, Author> authors;
    
    /**
     * This method computes NDCG at position n.
     * If n == 5 or 10 then save ndcg to author list.
     * @param authors
     * @param n
     * @return ndcg
     */
    public double NDCG(HashMap<String, Author> authorsInput, int n) {
        double ndcg = 0;
        double currentNDCG = 0;
        
        this.authors = authorsInput;
        
        if (n == 5) {
            for (String key : authorsInput.keySet()) {
                currentNDCG = NDCG.computeNDCG(authorsInput.get(key).getRecommendation(), authorsInput.get(key).getGroundTruth(), n);
                authors.get(key).setNdcg5(currentNDCG);
                ndcg += currentNDCG;
            }
        } else if (n == 10) {
            for (String key : authorsInput.keySet()) {
                currentNDCG = NDCG.computeNDCG(authorsInput.get(key).getRecommendation(), authorsInput.get(key).getGroundTruth(), n);
                authors.get(key).setNdcg10(currentNDCG);
                ndcg += currentNDCG;
            }
            
        } else {
            for (String key : authorsInput.keySet()) {
                ndcg += NDCG.computeNDCG(authorsInput.get(key).getRecommendation(), authorsInput.get(key).getGroundTruth(), n);
            }
        }
        // Compute average.
        ndcg = ndcg / authorsInput.size();
        
        return ndcg;
    }
    
    /**
     * This method computes MRR.
     * @param authors
     * @return 
     */
    public double MRR(HashMap authorsInput) {
        double mrr = 0;
        // coding here.
        return mrr;
    }
}
