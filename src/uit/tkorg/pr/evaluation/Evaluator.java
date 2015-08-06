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
 * Method: - computeMeanNDCG: + input: authors' ground truth list and
 * recommendation list, n where computeMeanNDCG computed at. + output:
 * computeMeanNDCG. - computeMRR: + input: authors' ground truth list and
 * recommendation list. + output: computeMRR.
 *
 * @author THNghiep
 */
public class Evaluator {

    // Prevent instantiation.
    private Evaluator() {
    }

    /**
     * This method computes MeanNDCG at position n. If n == 5 or 10 then save
     * MeanNDCG to author list. Note: this method return the MeanNDCG value and
     * change the author hashmap input directly.
     *
     * @param authors
     * @param k
     * @return MeanNDCG
     */
    public static double computeMeanNDCG(HashMap<String, Author> authors, int k) throws Exception {
        double sumNDCG = 0;

        int numRecommendedAuthors = 0;
        double currentNDCG = 0;
        for (String authorId : authors.keySet()) {
            if ((authors.get(authorId).getRecommendationList() != null)
                    && (!authors.get(authorId).getRecommendationList().isEmpty())) {
                numRecommendedAuthors++;
            }
            currentNDCG = NDCG.computeNDCG(authors.get(authorId).getRecommendationList(), authors.get(authorId).getGroundTruth(), k);
            sumNDCG += currentNDCG;
            if (k == 5) {
                authors.get(authorId).setNdcg5(currentNDCG);
            } else if (k == 10) {
                authors.get(authorId).setNdcg10(currentNDCG);
            }
        }
        if (numRecommendedAuthors == 0) {
            return 0;
        }
        // Compute average.
        return sumNDCG / numRecommendedAuthors;
    }

    /**
     * This method computes computeMRR. Note: this method return the mrr value
     * and change the author hashmap input directly.
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
            srr += currentRR;
            authors.get(authorId).setRr(currentRR);
        }
        if (numRecommendedAuthors == 0) {
            return 0;
        }
        return srr / numRecommendedAuthors;
    }

    /**
     * This method computes computeMeanPrecisionTopN. Note: this method return
     * the precision value and change the author hashmap input directly.
     *
     * @param authors
     * @param topN
     * @return
     */
    public static double computeMeanPrecisionTopN(HashMap<String, Author> authors, int topN) {
        double sumPrecision = 0;

        int numRecommendedAuthors = 0;
        double currentPrecision = 0;
        for (String authorId : authors.keySet()) {
            if ((authors.get(authorId).getRecommendationList() != null)
                    && (!authors.get(authorId).getRecommendationList().isEmpty())) {
                numRecommendedAuthors++;
            }
            currentPrecision = Precision.computePrecisionTopN(authors.get(authorId).getRecommendationList(), authors.get(authorId).getGroundTruth(), topN);
            sumPrecision += currentPrecision;
            if (topN == 10) {
                authors.get(authorId).setPrecision10(currentPrecision);
            } else if (topN == 20) {
                authors.get(authorId).setPrecision20(currentPrecision);
            } else if (topN == 30) {
                authors.get(authorId).setPrecision30(currentPrecision);
            } else if (topN == 40) {
                authors.get(authorId).setPrecision40(currentPrecision);
            } else if (topN == 50) {
                authors.get(authorId).setPrecision50(currentPrecision);
            }
        }
        if (numRecommendedAuthors == 0) {
            return 0;
        }
        return sumPrecision / numRecommendedAuthors;
    }

    /**
     * This method computes computeMeanRecallTopN. Note: this method return the
     * recall value and change the author hashmap input directly.
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
            authors.get(authorId).setRecall50(currentRecall);
            sumRecall += currentRecall;
            if (topN == 50) {
                authors.get(authorId).setRecall50(currentRecall);
            } else if (topN == 100) {
                authors.get(authorId).setRecall100(currentRecall);
            }
        }
        if (numRecommendedAuthors == 0) {
            return 0;
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
            authors.get(authorId).setAp10(currentAP);
            sap += currentAP;
            if (k == 10) {
                authors.get(authorId).setAp10(currentAP);
            } else if (k == 20) {
                authors.get(authorId).setAp20(currentAP);
            } else if (k == 30) {
                authors.get(authorId).setAp30(currentAP);
            } else if (k == 40) {
                authors.get(authorId).setAp40(currentAP);
            } else if (k == 50) {
                authors.get(authorId).setAp50(currentAP);
            }
        }
        if (numRecommendedAuthors == 0) {
            return 0;
        }
        return sap / numRecommendedAuthors;
    }

    /**
     * This method computes MeanFMeasure with beta parameter. When beta == 1, we
     * have the ordinary F1 measure.
     *
     * @param authors
     * @param beta
     * @return MeanFMeasure
     */
    public static double computeMeanFMeasure(HashMap<String, Author> authors, double beta) throws Exception {
        double sumFMeasure = 0.0;

        int numRecommendedAuthors = 0;
        double currentFMeasure = 0.0;
        for (String authorId : authors.keySet()) {
            if ((authors.get(authorId).getRecommendationList() != null)
                    && (!authors.get(authorId).getRecommendationList().isEmpty())) {
                numRecommendedAuthors++;
            }
            currentFMeasure = FMeasure.computeFMeasure(authors.get(authorId).getRecommendationList(), authors.get(authorId).getGroundTruth(), beta);
            sumFMeasure += currentFMeasure;
            if (beta == 1) {
                authors.get(authorId).setF1(currentFMeasure);
            }
        }
        if (numRecommendedAuthors == 0) {
            return 0;
        }
        // Compute average.
        return sumFMeasure / numRecommendedAuthors;
    }
}
