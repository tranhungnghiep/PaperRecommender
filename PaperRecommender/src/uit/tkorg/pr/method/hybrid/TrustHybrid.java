/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.method.hybrid;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import uit.tkorg.pr.method.GenericRecommender;
import uit.tkorg.pr.method.cbf.FeatureVectorSimilarity;
import uit.tkorg.pr.model.Author;

/**
 *
 * @author Administrator
 */
public class TrustHybrid {
    
    public static Integer count = 0;

    private TrustHybrid() {}
    
    public static void computeTrustedAuthorHMLinearCombinationAndPutIntoModelForAuthorList(HashMap<String, Author> authors, 
            final float alpha) throws Exception {
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
                        computeTrustedAuthorHMLinearCombination(authorObj, alpha);
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

    private static void computeTrustedAuthorHMLinearCombination(Author author, float alpha) throws Exception {

        Set<String> authorIds = new HashSet<> (author.getCoAuthorRSSHM().keySet());
        authorIds.addAll(new HashSet<> (author.getCitationAuthorRSSHM().keySet()));
        for (String authorId : authorIds) {
            Float citationAuthorScore = author.getCoAuthorRSSHM().get(authorId);
            if (citationAuthorScore == null) {
                citationAuthorScore = Float.valueOf(0);
            }
            Float coAuthorScore = author.getCitationAuthorRSSHM().get(authorId);
            if (coAuthorScore == null) {
                coAuthorScore = Float.valueOf(0);
            }
            
            Float trustedAuthorScore = coAuthorScore * alpha + citationAuthorScore * (1 - alpha);
            author.getTrustedAuthorHM().put(authorId, trustedAuthorScore);
        }

        synchronized (count) {
            System.out.println(count++ + ". " + (new Date(System.currentTimeMillis()).toString()) + " DONE for authorId: " + author.getAuthorId());
        }
    }
    
    public static void trustHybridRecommendToAuthorList(HashMap<String, Author> authorTestSet, int topNRecommend) throws IOException, TasteException, Exception {
        GenericRecommender.generateRecommendationForAuthorList(authorTestSet, topNRecommend, 3);
    }
}
