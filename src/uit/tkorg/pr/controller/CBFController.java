/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.controller;

import java.util.HashMap;
import java.util.HashSet;
import uit.tkorg.pr.datapreparation.CBFAuthorFVComputation;
import uit.tkorg.pr.datapreparation.CBFPaperFVComputation;
import uit.tkorg.pr.method.cbf.FeatureVectorSimilarity;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;

/**
 *
 * @author Administrator
 */
public class CBFController {

    public static String cbfComputeRecommendingScore(HashMap<String, Author> authorTestSet,
            HashMap<String, Paper> papers,
            HashSet<String> paperIdsOfAuthorTestSet,
            HashSet<String> paperIdsInTestSet,
            int combiningSchemePaperOfAuthor, int weightingSchemePaperOfAuthor,
            int timeAwareScheme, double gamma,
            int combiningSchemePaperTestSet, int weightingSchemePaperTestSet,
            double pruning,
            int similarityScheme) throws Exception {

        String algorithmName = "CBF with FV_PaperOfAuthor("
                + combiningSchemePaperOfAuthor + "," + weightingSchemePaperOfAuthor + ")"
                + ", FV_Authors("
                + timeAwareScheme + "," + gamma + ")"
                + ", FV_PaperTestSet("
                + combiningSchemePaperTestSet + "," + weightingSchemePaperTestSet + ")"
                + "pruned by " + pruning;

        long startTime;
        long estimatedTime;

        // Step 1: compute feature vector for those all 1000 authors.
        System.out.println("Begin computing authors FV...");
        startTime = System.nanoTime();
        CBFPaperFVComputation.computeFeatureVectorForAllPapers(papers, paperIdsOfAuthorTestSet,
                combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor, pruning);
        CBFAuthorFVComputation.computeFVForAllAuthors(authorTestSet, papers, timeAwareScheme, gamma);
        // Clear no longer in use objects.
        CBFPaperFVComputation.clearFV(papers);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Computing authors FV elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End computing authors FV.");

        // Step 2: Aggregating feature vectors for all papers and
        // put the result into HashMap<String, Paper> papers (model)
        // (papers, 0, 0): baseline
        System.out.println("Begin computing FV for all papers...");
        startTime = System.nanoTime();
        CBFPaperFVComputation.computeFeatureVectorForAllPapers(papers, paperIdsInTestSet,
                combiningSchemePaperTestSet, weightingSchemePaperTestSet, pruning);
        // Separate paper test set.
        // Note: still share the same Paper Objects.
        HashMap<String, Paper> paperTestSet = CBFPaperFVComputation.extractPapers(papers, paperIdsInTestSet);
        // Clear no longer in use objects.
        // Keep papers HM for citation list.
        // Keep FV for paperTestSet. Note: only paper in TestSet is computed FV, so no need to clear FV in general.
        CBFPaperFVComputation.clearTFIDF(papers);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Computing FV for all papers elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End computing FV for all papers.");

        // Step 3: compute CBF score.
        System.out.println("Computing CBF Score and put into DataModel ...");
        startTime = System.nanoTime();
        FeatureVectorSimilarity.computeCBFSimAndPutIntoModelForAuthorList(authorTestSet, paperTestSet, similarityScheme);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Computing CBF Score elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End Computing CBF Score and put into DataModel ...");

        return algorithmName;
    }
}
