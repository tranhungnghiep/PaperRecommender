/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.evaluation;

import java.util.HashMap;
import uit.tkorg.pr.model.Author;
import uit.tkorg.utility.evaluation.AveragePrecision;
import uit.tkorg.utility.evaluation.FMeasure;
import uit.tkorg.utility.evaluation.NDCG;
import uit.tkorg.utility.evaluation.Precision;
import uit.tkorg.utility.evaluation.Recall;
import uit.tkorg.utility.evaluation.ReciprocalRank;

/**
 * This class handles all logics for evaluation of recommendation results.
 * Method:
 * - computeMeanNDCG: 
 * + input: authors' ground truth list and recommendation list, n where computeMeanNDCG computed at.
 * + output: computeMeanNDCG.
 * - computeMRR: 
 * + input: authors' ground truth list and recommendation list.
 * + output: computeMRR.
 * @author THNghiep
 */
public class Evaluator {

    // Prevent instantiation.
    private Evaluator() {
    }
    
    /**
     * This method computes MeanNDCG at position n.
     * If n == 5 or 10 then save MeanNDCG to author list.
     * Note: this method return the MeanNDCG value and change the author hashmap input directly.
     * 
     * @param authors
     * @param k
     * @return MeanNDCG
     */
    public static double computeMeanNDCG(HashMap<String, Author> authors, int k) throws Exception {
        double sumNDCG = 0;
        
        int numRecommendedAuthors = 0;
        double currentNDCG = 0;
        if (k == 5) {
            for (String authorId : authors.keySet()) {
                if ((authors.get(authorId).getRecommendationList() != null) 
                        && (!authors.get(authorId).getRecommendationList().isEmpty())) {
                    numRecommendedAuthors++;
                }
                currentNDCG = NDCG.computeNDCG(authors.get(authorId).getRecommendationList(), authors.get(authorId).getGroundTruth(), k);
                authors.get(authorId).setNdcg5(currentNDCG);
                sumNDCG += currentNDCG;
            }
        } else if (k == 10) {
            for (String authorId : authors.keySet()) {
                if ((authors.get(authorId).getRecommendationList() != null) 
                        && (!authors.get(authorId).getRecommendationList().isEmpty())) {
                    numRecommendedAuthors++;
                }
                currentNDCG = NDCG.computeNDCG(authors.get(authorId).getRecommendationList(), authors.get(authorId).getGroundTruth(), k);
                authors.get(authorId).setNdcg10(currentNDCG);
                sumNDCG += currentNDCG;
            }
            
        } else {
            for (String authorId : authors.keySet()) {
                if ((authors.get(authorId).getRecommendationList() != null) 
                        && (!authors.get(authorId).getRecommendationList().isEmpty())) {
                    numRecommendedAuthors++;
                }
                sumNDCG += NDCG.computeNDCG(authors.get(authorId).getRecommendationList(), authors.get(authorId).getGroundTruth(), k);
            }
        }
        // Compute average.
        return sumNDCG / numRecommendedAuthors;
    }
    
    /**
     * This method computes computeMRR.
     * Note: this method return the mrr value and change the author hashmap input directly.
     * 
     * @param authors
     * @return 
     */
    public static double computeMRR(HashMap<String, Author> authors) throws Exception {
        double srr = 0;

        int numRecommendedAuthors = 0;
        double currentRR = 0;
        for (String authorId : authors.keySet()) {
            if ((authors.get(authorId).getRecommendationList() != null) 
                    && (!authors.get(authorId).getRecommendationList().isEmpty())) {
                numRecommendedAuthors++;
            }
            currentRR = ReciprocalRank.computeRR(authors.get(authorId).getRecommendationList(), authors.get(authorId).getGroundTruth());
            authors.get(authorId).setRr(currentRR);
            srr += currentRR;
        }
        return srr / numRecommendedAuthors;
    }

    /**
     * This method computes computeMeanPrecisionTopN. Note: this method return the precision
     * value and change the author hashmap input directly.
     *
     * @param authors
     * @param n
     * @return
     */
    public static double computeMeanPrecisionTopN(HashMap<String, Author> authors, int n) {
        double sumPrecision = 0;

        int numRecommendedAuthors = 0;
        double currentPrecision = 0;
        for (String authorId : authors.keySet()) {
            if ((authors.get(authorId).getRecommendationList() != null) 
                    && (!authors.get(authorId).getRecommendationList().isEmpty())) {
                numRecommendedAuthors++;
            }
            currentPrecision = Precision.computePrecisionTopN(authors.get(authorId).getRecommendationList(), authors.get(authorId).getGroundTruth(), n);
            authors.get(authorId).setPrecision(currentPrecision);
            sumPrecision += currentPrecision;
        }

        return sumPrecision / numRecommendedAuthors;
    }

    /**
     * This method computes computeMeanRecallTopN. Note: this method return the recall value
     * and change the author hashmap input directly.
     *
     * @param authors
     * @param topN
     * @return
     */
    public static double computeMeanRecallTopN(HashMap<String, Author> authors, int topN) {
        double sumRecall = 0;

        int numRecommendedAuthors = 0;
        double currentRecall = 0;
        for (String authorId : authors.keySet()) {
            if ((authors.get(authorId).getRecommendationList() != null) 
                    && (!authors.get(authorId).getRecommendationList().isEmpty())) {
                numRecommendedAuthors++;
            }
            currentRecall = Recall.computeRecallTopN(authors.get(authorId).getRecommendationList(), authors.get(authorId).getGroundTruth(), topN);
            authors.get(authorId).setRecall(currentRecall);
            sumRecall += currentRecall;
        }

        return sumRecall / numRecommendedAuthors;
    }

    /**
     * This method computes Map. Note: this method return the map value and
     * change the author hashmap input directly.
     *
     * @param authors
     * @param k
     * @return
     */
    public static double computeMAP(HashMap<String, Author> authors, int k) {
        double sap = 0;

        int numRecommendedAuthors = 0;
        double currentAP = 0;
        for (String authorId : authors.keySet()) {
            if ((authors.get(authorId).getRecommendationList() != null) 
                    && (!authors.get(authorId).getRecommendationList().isEmpty())) {
                numRecommendedAuthors++;
            }
            currentAP = AveragePrecision.computeAPK(authors.get(authorId).getRecommendationList(), authors.get(authorId).getGroundTruth(), k);
            authors.get(authorId).setAp(currentAP);
            sap += currentAP;
        }

        return sap / numRecommendedAuthors;
    }

    /**
     * This method computes MeanFMeasure with beta parameter. 
     * When beta == 1, we have the ordinary F1 measure.
     * 
     * @param authors
     * @param beta
     * @return MeanFMeasure
     */
    public static double computeMeanFMeasure(HashMap<String, Author> authors, double beta) throws Exception {
        double sumFMeasure = 0.0;
        
        int numRecommendedAuthors = 0;
        double currentFMeasure = 0.0;
        if (beta == 1) {
            for (String authorId : authors.keySet()) {
                if ((authors.get(authorId).getRecommendationList() != null) 
                        && (!authors.get(authorId).getRecommendationList().isEmpty())) {
                    numRecommendedAuthors++;
                }
                currentFMeasure = FMeasure.computeF1(authors.get(authorId).getRecommendationList(), authors.get(authorId).getGroundTruth());
                authors.get(authorId).setF1(currentFMeasure);
                sumFMeasure += currentFMeasure;
            }
        } else {
            for (String authorId : authors.keySet()) {
                if ((authors.get(authorId).getRecommendationList() != null) 
                        && (!authors.get(authorId).getRecommendationList().isEmpty())) {
                    numRecommendedAuthors++;
                }
                sumFMeasure += FMeasure.computeFMeasure(authors.get(authorId).getRecommendationList(), authors.get(authorId).getGroundTruth(), beta);
            }
        }
        // Compute average.
        return sumFMeasure / numRecommendedAuthors;
    }
}
