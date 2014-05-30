/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.evaluation;

import java.util.HashMap;
import uit.tkorg.pr.model.Author;
import uit.tkorg.utility.evaluation.NDCG;
import uit.tkorg.utility.evaluation.ReciprocalRank;

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

    // Prevent instantiation.
    private Evaluator() {
    }
    
    /**
     * This method computes NDCG at position n.
     * If n == 5 or 10 then save ndcg to author list.
     * Note: this method return the ndcg value and change the author hashmap input directly.
     * 
     * @param authors
     * @param n
     * @return ndcg
     */
    public static double NDCG(HashMap<String, Author> authors, int n) throws Exception {
        double ndcg = 0;
        
        double currentNDCG = 0;
        if (n == 5) {
            for (String authorId : authors.keySet()) {
                currentNDCG = NDCG.computeNDCG(authors.get(authorId).getRecommendation(), authors.get(authorId).getGroundTruth(), n);
                authors.get(authorId).setNdcg5(currentNDCG);
                ndcg += currentNDCG;
            }
        } else if (n == 10) {
            for (String authorId : authors.keySet()) {
                currentNDCG = NDCG.computeNDCG(authors.get(authorId).getRecommendation(), authors.get(authorId).getGroundTruth(), n);
                authors.get(authorId).setNdcg10(currentNDCG);
                ndcg += currentNDCG;
            }
            
        } else {
            for (String authorId : authors.keySet()) {
                ndcg += NDCG.computeNDCG(authors.get(authorId).getRecommendation(), authors.get(authorId).getGroundTruth(), n);
            }
        }
        // Compute average.
        ndcg = ndcg / authors.size();
        
        return ndcg;
    }
    
    /**
     * This method computes MRR.
     * Note: this method return the mrr value and change the author hashmap input directly.
     * 
     * @param authors
     * @return 
     */
    public static double MRR(HashMap<String, Author> authors) throws Exception {
        double mrr = 0;

        double currentRR = 0;
        for (String authorId : authors.keySet()) {
            currentRR = ReciprocalRank.computeRR(authors.get(authorId).getRecommendation(), authors.get(authorId).getGroundTruth());
            authors.get(authorId).setRr(currentRR);
            mrr += currentRR;
        }
        mrr = mrr / authors.size();

        return mrr;
    }
}
