/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.method;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import uit.tkorg.pr.method.cbf.FeatureVectorSimilarity;
import uit.tkorg.pr.model.Author;
import uit.tkorg.utility.general.HashMapUtility;

/**
 *
 * @author THNghiep
 */
public class GenericRecommender {

    public static Integer count = 0;

    private GenericRecommender() {}
    
    /**
     * 
     * @param authors
     * @param topNRecommend
     * @param method: 0: cbf, 1: cf, 2: cbf and cf linear combination, 3: cbf, cf, and trust hybrid
     * @throws Exception 
     */
    public static void generateRecommendationForAuthorList(final HashMap<String, Author> authors, 
            final int topNRecommend, final int method) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        int numOfProcessors = runtime.availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);

        System.out.println("NUM OF AUTHOR: " + authors.size());

        for (String authorId : authors.keySet()) {
            final Author authorObj = authors.get(authorId);
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        generateRecommendation(authorObj, topNRecommend, method);
                    } catch (Exception ex) {
                        Logger.getLogger(FeatureVectorSimilarity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    private static void generateRecommendation(Author author, int topNRecommend, 
            final int method) throws Exception {

        HashMap<String, Float> recommendingScoreHM = null;
        if (method == 0) {
            recommendingScoreHM = author.getCbfSimHM();
        } else if (method == 1) {
            recommendingScoreHM = author.getCfRatingHM();
        } else if (method == 2) {
            recommendingScoreHM = author.getCbfCfHybridHM();
        } else if (method == 3) {
            recommendingScoreHM = author.getCbfCfTrustHybridHM();
        }

        // Sort papers descending based on recommending score.
        LinkedHashMap<String, Float> sortedRecommendingScoreHM = HashMapUtility.getSortedMapDescending(recommendingScoreHM);

        // Take top n recommended papers and put into model.
        int counter = 0;
        for (String paperId : sortedRecommendingScoreHM.keySet()) {
            author.getRecommendationList().add(paperId);
            author.getRecommendationValue().put(paperId, sortedRecommendingScoreHM.get(paperId));
            counter++;
            if (counter >= topNRecommend) {
                break;
            }
        }

        synchronized (count) {
            System.out.println(count++ + ". " + (new Date(System.currentTimeMillis()).toString()) + " DONE for authorId: " + author.getAuthorId());
        }
    }
}
