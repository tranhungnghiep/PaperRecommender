/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.controller.evaluation;

import java.util.HashMap;

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
    /**
     * This method computes NDCG at position n.
     * @param authors
     * @param n
     * @return ndcg
     */
    public double NDCG(HashMap authors, int n) {
        double ndcg = 0;
        // coding here.
        return ndcg;
    }
    
    /**
     * This method computes MRR.
     * @param authors
     * @return 
     */
    public double MRR(HashMap authors) {
        double mrr = 0;
        // coding here.
        return mrr;
    }
}
