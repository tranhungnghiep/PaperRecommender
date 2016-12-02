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

/**
 *
 * @author Administrator
 */
public class CFController {
        /**
     * 
     * @param fileNameAuthorCitePaper
     * @param MahoutCFDir
     * @param cfMethod: 1: KNN Pearson, 2: KNN Cosine, 3: SVD
     * @param authorTestSet
     * @param topNRecommend
     * @throws Exception 
     */
    public static String cfComputeRecommendingScore(
            String fileNameAuthorCitePaper, 
            String MahoutCFDir, 
            int cfMethod,
            HashMap<String, Author> authorTestSet, HashSet<String> paperIdsInTestSet) throws Exception {
        String algorithmName = null;
        
        // Step 1: Prepare CF matrix.
        String MahoutCFFileOriginalFile = MahoutCFDir + "\\CFRatingMatrixOriginal.txt";
        cfPrepareMatrix(fileNameAuthorCitePaper, MahoutCFFileOriginalFile);
        
        // Step 2: Predict ratings.
        if ((cfMethod == 1) || (cfMethod == 2)) {
            // KNN. k neighbors.
            int k = 8;
            if (cfMethod == 1) {
                // kNNCF co-pearson.
                algorithmName = "CF KNN Pearson " + "k" + k;
            } else if (cfMethod == 2) {
                // kNNCF cosine.
                algorithmName = "CF KNN Cosine " + "k" + k;
            }
            System.out.println("Begin calculating CF-KNN Recommending Score");
            cfKNNComputeRecommendingScore(MahoutCFDir, MahoutCFFileOriginalFile, cfMethod, authorTestSet, paperIdsInTestSet, k);
            System.out.println("End calculating CF-KNN Recommending Score");
        } else if (cfMethod == 3) {
            // SVD ALSWRFactorizer.
            // f features, normalize by l, i iterations.
            int f = 5;
            double l = 0.001;
            int i = 100;
            algorithmName = "CF SVD ALSWRFactorizer " + "f" + f + "l" + l + "i" + i;
            // Recommend for authors in author test set.
            System.out.println("Begin calculating CF-SVD Recommending Score");
            cfSVDComputeRecommendingScore(MahoutCFDir, MahoutCFFileOriginalFile, authorTestSet, paperIdsInTestSet, f, l, i);
            System.out.println("End calculating CF-SVD Recommending Score");
        }
        
        return algorithmName;
    }
    

    public static void cfPrepareMatrix(String fileNameAuthorCitePaper, String MahoutCFFileOriginalFile) throws Exception {

        // Read Raw rating matrix
        System.out.println("Begin Reading raw rating matrix");
        HashMap<String, HashMap<String, Double>> authorPaperRating = MASDataset1.readAuthorCitePaperMatrix(fileNameAuthorCitePaper);
        System.out.println("End Reading raw rating matrix");

        // Normalize
        System.out.println("Begin Normalize reating values in Citation Matrix");
        CFRatingMatrixComputation.normalizeAuthorRatingVector(authorPaperRating);
        System.out.println("End Normalize reating values in Citation Matrix");

        // Write to Mahout file
        System.out.println("Begin writeCFRatingToMahoutFormatFile");
        CFRatingMatrixComputation.writeCFRatingToMahoutFormatFile(authorPaperRating, MahoutCFFileOriginalFile);
        System.out.println("End writeCFRatingToMahoutFormatFile");
    }
    
    /**
     * 
     * @param MahoutCFDir
     * @param MahoutCFFileOriginalFile
     * @param similarityMethod: 1: Pearson, 2: Cosine.
     * @param authorTestSet
     * @param topNRecommend
     * @param k
     * @throws Exception 
     */
    public static void cfKNNComputeRecommendingScore(String MahoutCFDir, String MahoutCFFileOriginalFile, int similarityMethod,
            HashMap<String, Author> authorTestSet, HashSet<String> paperIdsInTestSet,
            int k) throws Exception {

        String MahoutCFRatingMatrixPredictionFile = MahoutCFDir + "\\CFRatingMatrixPredictionByCoPearson" + "k" + k + ".txt";

        // Predict ratings by kNNCF.
        KNNCF.computeCFRatingAndPutIntoModelForAuthorList(MahoutCFFileOriginalFile, similarityMethod, k, authorTestSet, paperIdsInTestSet, MahoutCFRatingMatrixPredictionFile);
    }
    
    public static void cfSVDComputeRecommendingScore(String MahoutCFDir, String MahoutCFFileOriginalFile, 
            HashMap<String, Author> authorTestSet, HashSet<String> paperIdsInTestSet, 
            int f, double l, int i) throws Exception {

        String MahoutCFRatingMatrixPredictionFile = MahoutCFDir + "\\CFRatingMatrixPredictionBySVD" + "f" + f + "l" + l + "i" + i + ".txt";

        // Predict ratings by SVD.
        SVDCF.computeCFRatingAndPutIntoModelForAuthorList(MahoutCFFileOriginalFile, f, l, i, authorTestSet, paperIdsInTestSet, MahoutCFRatingMatrixPredictionFile);
    }
}
