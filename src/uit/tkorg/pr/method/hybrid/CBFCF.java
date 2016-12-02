/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.method.hybrid;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
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
    
    private CBFCF() {}
    
    /**
     * 
     * @param authors
     * @param alpha
     * @param combinationScheme 1: linear, 2: basedOnConfidence, 3: basedOnConfidence and linear.
     * @throws Exception 
     */
    public static void computeCBFCFCombinationAndPutIntoModelForAuthorList(HashMap<String, Author> authors, 
            final float alpha, final int combinationScheme) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        int numOfProcessors = runtime.availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);

        System.out.println("NUM OF AUTHOR: " + authors.size());

        HashMapUtility.setCountThread(0);
        for (String authorId : authors.keySet()) {
            final Author authorObj = authors.get(authorId);
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (combinationScheme == 1) {
                            HashMapUtility.combineLinearTwoHashMap(authorObj.getCbfSimHM(), 
                                    authorObj.getCfRatingHM(), 
                                    alpha, 
                                    authorObj.getCbfCfHybridHM());
                        } else if (combinationScheme == 2) {
                            HashMapUtility.combineBasedOnConfidenceTwoHashMap(authorObj.getCbfSimHM(), 
                                    authorObj.getCfRatingHM(), 
                                    authorObj.getCbfCfHybridHM());
                        } else if (combinationScheme == 3) {
                            HashMapUtility.combineBasedOnConfidenceAndLinearTwoHashMap(authorObj.getCbfSimHM(), 
                                    authorObj.getCfRatingHM(), 
                                    alpha, 
                                    authorObj.getCbfCfHybridHM());
                        } else if (combinationScheme == 4) {
                            HashMapUtility.combineBasedOnConfidenceTwoHashMapV2(authorObj.getCbfSimHM(), 
                                    authorObj.getCfRatingHM(), 
                                    authorObj.getCbfCfHybridHM());
                        } else if (combinationScheme == 5) {
                            HashMapUtility.combineBasedOnConfidenceAndLinearTwoHashMapV2(authorObj.getCbfSimHM(), 
                                    authorObj.getCfRatingHM(), 
                                    alpha, 
                                    authorObj.getCbfCfHybridHM());
                        }
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

    public static void cbfcfHybridRecommendToAuthorList(HashMap<String, Author> authorTestSet, int topNRecommend) throws IOException, TasteException, Exception {
        GenericRecommender.generateRecommendationForAuthorList(authorTestSet, topNRecommend, 2);
    }
}
