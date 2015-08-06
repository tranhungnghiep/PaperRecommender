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
     * @param method: 
     * 1: CBF, 
     * 2: CF, 
     * 3: CBF-CF Linear, 
     * 4: Trust, 
     * 5: CBF-Trust Linear, 
     * 6: New CBF-Trust Hybrid V2 (get trust list then sort by cbf, or filter cbf by trust), 
     * 7: New CBF-Trust Hybrid V3 (get cbf list then sort by trust), 
     * 8: New CBF-CF Hybrid V2 (get cf list then sort by cbf), 
     * 9: New CBF-CF Hybrid V3 (get cbf list then sort by cf), 
     * 10: New CBF-Trust Hybrid V4 (get trust list then sort by cbf, fill short trust list by cbf), 
     * 100: ML Hybrid Combination.
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

        System.err.println("No. Recommended items for each author:");
        for (String authorId : authors.keySet()) {
            System.err.println(authors.get(authorId).getRecommendationList().size());
        }
    }

    private static void generateRecommendation(Author author, int topNRecommend, 
            final int method) throws Exception {

        author.getRecommendationList().clear();
        
        HashMap<String, Float> recommendingScoreHM = null;
        if (method == 1) {
            recommendingScoreHM = author.getCbfSimHM();
        } else if (method == 2) {
            recommendingScoreHM = author.getCfRatingHM();
        } else if (method == 3) {
            recommendingScoreHM = author.getCbfCfHybridHM();
        } else if (method == 4) {
            recommendingScoreHM = author.getTrustedPaperHM();
        } else if (method == 5) {
            recommendingScoreHM = author.getCbfTrustHybridHM();
        } else if (method == 6) {
            recommendingScoreHM = author.getCbfTrustHybridV2HM();
        } else if (method == 7) {
            recommendingScoreHM = author.getCbfTrustHybridV3HM();
        } else if (method == 8) {
            recommendingScoreHM = author.getCbfCfHybridV2HM();
        } else if (method == 9) {
            recommendingScoreHM = author.getCbfCfHybridV3HM();
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
