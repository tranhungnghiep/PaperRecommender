/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.method.hybrid;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import uit.tkorg.pr.method.GenericRecommender;
import uit.tkorg.pr.method.cbf.FeatureVectorSimilarity;
import uit.tkorg.pr.model.Author;
import uit.tkorg.utility.general.HashMapUtility;

/**
 *
 * @author Administrator
 */
public class CBFCF {
    
    public static Integer count = 0;

    private CBFCF() {}
    
    public static void computeCBFCFLinearCombinationAndPutIntoModelForAuthorList(HashMap<String, Author> authors, 
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
                        computeCBFCFLinearCombination(authorObj, alpha);
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

    private static void computeCBFCFLinearCombination(Author author, float alpha) throws Exception {

        for (String paperId : author.getCbfSimHM().keySet()) {
            Float cbfScore = author.getCbfSimHM().get(paperId);
            if (cbfScore == null) {
                cbfScore = Float.valueOf(0);
            }
            Float cfScore = author.getCfRatingHM().get(paperId);
            if (cfScore == null) {
                cfScore = Float.valueOf(0);
            }
            
            Float cbfcfCombinationScore = cbfScore * alpha + cfScore * (1 - alpha);
            author.getCbfcfHybridHM().put(paperId, cbfcfCombinationScore);
        }

        synchronized (count) {
            System.out.println(count++ + ". " + (new Date(System.currentTimeMillis()).toString()) + " DONE for authorId: " + author.getAuthorId());
        }
    }
    
    public static void cbfcfHybridRecommendToAuthorList(HashMap<String, Author> authorTestSet, int topNRecommend) throws IOException, TasteException, Exception {
        GenericRecommender.generateRecommendationForAuthorList(authorTestSet, topNRecommend, 2);
    }
}
