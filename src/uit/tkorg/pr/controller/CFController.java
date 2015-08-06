/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uit.tkorg.pr.controller;

import java.util.HashMap;
import java.util.HashSet;
import uit.tkorg.pr.dataimex.MASDataset1;
import uit.tkorg.pr.datapreparation.CFRatingMatrixComputation;
import uit.tkorg.pr.method.cf.KNNCF;
import uit.tkorg.pr.method.cf.SVDCF;
import uit.tkorg.pr.model.Author;
import uit.tkorg.utility.general.HashMapUtility;

/**
 *
 * @author Administrator
 */
public class CFController {
    /**
     * 
     * @param fileNameAuthorCitePaper
     * @param MahoutCFDir
     * @param cfMethod: 1: KNN , 2: MF SVD
     * @param knnSimilarityScheme: 1: Pearson, 2: cosine, 3: log likelihood
     * @param authorTestSet
     * @param paperIdsInTestSet
     * @return algorithmName
     * @throws Exception 
     */
    public static String cfComputeRecommendingScore(
            String fileNameAuthorCitePaper, 
            String MahoutCFDir, 
            int cfMethod, int knnSimilarityScheme,
            HashMap<String, Author> authorTestSet, HashSet<String> paperIdsInTestSet,
            boolean runningFirstTime, int k, int f, double l, int i) throws Exception {

        String algorithmName = null;
        
        // Step 1: Prepare CF matrix.
        String MahoutCFFileOriginalFile = null;
        // Just experiment here, check if binary rating is better. 
        // Result with LogLikelihood K=8, binary rating is worse.
        boolean binaryRating = false;
        if (binaryRating) {
            MahoutCFFileOriginalFile = MahoutCFDir + "\\CFRatingMatrixOriginalBinaryRating.txt";
        } else {
            MahoutCFFileOriginalFile = MahoutCFDir + "\\CFRatingMatrixOriginalNumericRating.txt";
        }
        // Notice: Only run once.
        if (runningFirstTime) {
            cfPrepareMatrix(fileNameAuthorCitePaper, MahoutCFFileOriginalFile, binaryRating);
        }
        
        // Step 2: Predict ratings.
        if (cfMethod == 1) {
            // KNN. 
            if (knnSimilarityScheme == 1) {
                // co-pearson.
                algorithmName = "CF KNN Pearson " + "k" + k;
            } else if (knnSimilarityScheme == 2) {
                // cosine.
                algorithmName = "CF KNN Cosine " + "k" + k;
            } else if (knnSimilarityScheme == 3) {
                // likelihood.
                algorithmName = "CF KNN Log Likelihood " + "k" + k;
            }
            System.out.println("Begin calculating CF-KNN Recommending Score");
            cfKNNComputeRecommendingScore(MahoutCFDir, MahoutCFFileOriginalFile, knnSimilarityScheme, authorTestSet, paperIdsInTestSet, k);
            System.out.println("End calculating CF-KNN Recommending Score");
        } else if (cfMethod == 2) {
            // SVD ALSWRFactorizer.
            algorithmName = "CF MF SVD ALSWRFactorizer " + "f" + f + "l" + l + "i" + i;
            // Recommend for authors in author test set.
            System.out.println("Begin calculating CF-MF-SVD Recommending Score");
            cfSVDComputeRecommendingScore(MahoutCFDir, MahoutCFFileOriginalFile, authorTestSet, paperIdsInTestSet, f, l, i);
            System.out.println("End calculating CF-MF-SVD Recommending Score");
        }

        // Normalize
        for (Author author : authorTestSet.values()) {
            HashMapUtility.minNormalizeHashMap(author.getCfRatingHM());
        }

        return algorithmName;
    }
    

    public static void cfPrepareMatrix(String fileNameAuthorCitePaper, String MahoutCFFileOriginalFile, boolean binaryRating) throws Exception {
        System.out.println("Begin preparing CF Matrix...");
        long startTime = System.nanoTime();

        // Read Raw rating matrix
        System.out.println("Begin Reading raw rating matrix");
        HashMap<String, HashMap<String, Float>> authorPaperRating = MASDataset1.readAuthorCitePaperMatrix(fileNameAuthorCitePaper);
        System.out.println("End Reading raw rating matrix");

        // Normalize
        if (!binaryRating) {
            System.out.println("Begin Normalize reating values in Citation Matrix");
            CFRatingMatrixComputation.normalizeAuthorRatingVectorV2(authorPaperRating);
            System.out.println("End Normalize reating values in Citation Matrix");
        }

        // Write to Mahout file
        System.out.println("Begin writeCFRatingToMahoutFormatFile");
        CFRatingMatrixComputation.writeCFRatingToMahoutFormatFile(authorPaperRating, MahoutCFFileOriginalFile, binaryRating);
        System.out.println("End writeCFRatingToMahoutFormatFile");

        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Preparing CF Matrix elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End preparing CF Matrix.");
    }
    
    /**
     * 
     * @param MahoutCFDir
     * @param MahoutCFFileOriginalFile
     * @param knnSimilarityScheme: 1: Pearson, 2: Cosine, 3: log likelihood.
     * @param authorTestSet
     * @param paperIdsInTestSet
     * @param k
     * @throws Exception 
     */
    public static void cfKNNComputeRecommendingScore(String MahoutCFDir, String MahoutCFFileOriginalFile, 
            int knnSimilarityScheme,
            HashMap<String, Author> authorTestSet, HashSet<String> paperIdsInTestSet,
            int k) throws Exception {

        String MahoutCFRatingMatrixPredictionFile = null;
        if (knnSimilarityScheme == 1) {
            MahoutCFRatingMatrixPredictionFile = MahoutCFDir + "\\CFRatingMatrixPredictionByCoPearson" + "k" + k + ".txt";
        } else if (knnSimilarityScheme == 2) {
            MahoutCFRatingMatrixPredictionFile = MahoutCFDir + "\\CFRatingMatrixPredictionByCosine" + "k" + k + ".txt";
        } else if (knnSimilarityScheme == 3) {
            MahoutCFRatingMatrixPredictionFile = MahoutCFDir + "\\CFRatingMatrixPredictionByLogLikelihood" + "k" + k + ".txt";
        }

        // Predict ratings by kNNCF.
        KNNCF.computeCFRatingAndPutIntoModelForAuthorList(MahoutCFFileOriginalFile, knnSimilarityScheme, k, authorTestSet, paperIdsInTestSet, MahoutCFRatingMatrixPredictionFile);
    }
    
    public static void cfSVDComputeRecommendingScore(String MahoutCFDir, String MahoutCFFileOriginalFile, 
            HashMap<String, Author> authorTestSet, HashSet<String> paperIdsInTestSet, 
            int f, double l, int i) throws Exception {

        String MahoutCFRatingMatrixPredictionFile = MahoutCFDir + "\\CFRatingMatrixPredictionBySVD" + "f" + f + "l" + l + "i" + i + ".txt";

        // Predict ratings by SVD.
        SVDCF.computeCFRatingAndPutIntoModelForAuthorList(MahoutCFFileOriginalFile, f, l, i, authorTestSet, paperIdsInTestSet, MahoutCFRatingMatrixPredictionFile);
    }
}
