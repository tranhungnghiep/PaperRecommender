/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.method.hybrid;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
public class TrustHybrid {
    
    public static Integer count = 0;

    private TrustHybrid() {}
    
    public static void computeTrustedAuthorHMLinearCombinationAndPutIntoModelForAuthorList(HashMap<String, Author> authors, 
            final float alpha) throws Exception {
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
                        HashMapUtility.linearCombineTwoHashMap(authorObj.getCoAuthorRSSHM(), 
                                authorObj.getCitationAuthorRSSHM(), 
                                alpha, 
                                authorObj.getTrustedAuthorHM());
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

    public static void computeTrustedPaperHMAndPutIntoModelForAuthorList(final HashMap<String, Author> authors) throws Exception {
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
                        computeTrustedPaperHM(authors, authorObj);
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

    private static void computeTrustedPaperHM(HashMap<String, Author> authors, Author author) throws Exception {

        HashMap<String, Integer> paperTrustedAuthorCount = new HashMap<>();
        
        for (String authorId : author.getTrustedAuthorHM().keySet()) {
            if (authors.containsKey(authorId)) {
                for (String paperId : (List<String>) authors.get(authorId).getPaperList()) {
                    if (author.getTrustedPaperHM().containsKey(paperId)) {
                        author.getTrustedPaperHM().put(paperId, 
                                author.getTrustedPaperHM().get(paperId) + author.getTrustedAuthorHM().get(authorId));
                        paperTrustedAuthorCount.put(paperId, 
                                paperTrustedAuthorCount.get(paperId) + 1);
                    } else {
                        author.getTrustedPaperHM().put(paperId, author.getTrustedAuthorHM().get(authorId));
                        paperTrustedAuthorCount.put(paperId, 1);
                    }
                }
            }
        }
        
        for (String paperId : author.getTrustedPaperHM().keySet()) {
            author.getTrustedPaperHM().put(paperId, 
                    author.getTrustedPaperHM().get(paperId) / paperTrustedAuthorCount.get(paperId));
        }

        synchronized (count) {
            System.out.println(count++ + ". " + (new Date(System.currentTimeMillis()).toString()) + " DONE for authorId: " + author.getAuthorId());
        }
    }
    
    public static void computeCBFCFTrustLinearCombinationAndPutIntoModelForAuthorList(HashMap<String, Author> authors, 
            final float alpha) throws Exception {
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
                        HashMapUtility.linearCombineTwoHashMap(authorObj.getCbfCfHybridHM(), 
                                authorObj.getTrustedPaperHM(), 
                                alpha, 
                                authorObj.getCbfCfTrustHybridHM());
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
    
    public static void trustHybridRecommendToAuthorList(HashMap<String, Author> authorTestSet, int topNRecommend) throws IOException, TasteException, Exception {
        GenericRecommender.generateRecommendationForAuthorList(authorTestSet, topNRecommend, 3);
    }
    
    public static void trustRecommendToAuthorList(HashMap<String, Author> authorTestSet, int topNRecommend) throws IOException, TasteException, Exception {
        GenericRecommender.generateRecommendationForAuthorList(authorTestSet, topNRecommend, 4);
    }
}
