/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.method.cbf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.HashMapUtility;
import uit.tkorg.utility.general.WeightingUtility;

/**
 * This class handles logic for recommending papers to each author. Data: list
 * of authors used as a universal recommendation list. Method: -
 * generateRecommendationForAllAuthors: + input: list of authors, list of
 * papers. + output: list of authors with recommendation list included, also
 * includes all data of the input list of authors. This output could be used as
 * universal recommendation list, upto the input list of authors.
 *
 * @author THNghiep
 */
public class FeatureVectorSimilarity {

    public static int count = 0;
    // Prevent instantiation.
    private FeatureVectorSimilarity() {
    }

    /**
     * This method runs content based recommendation business for all authors.
     *
     * @param authors: all authors in the test set.
     * @param papers: all papers.
     * @param similarityScheme 0: cosine
     * @param n: top n item to recommend.
     *
     * - For each author: + Compute similarity with all papers. + Sort list of
     * papers, based on similarity. + Take top n papers with highest similarity
     * for the recommendation list. + Save recommendation list into current
     * author.
     */
    public static void generateRecommendationForAllAuthors(final HashMap<String, Author> authors, final HashMap<String, Paper> papers,
            final int similarityScheme, final int n) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        int numOfProcessors = runtime.availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);

        System.out.println("NUM OF AUTHOR:" + authors.size());
        
        for (String authorId : authors.keySet()) {
            final Author authorObj = authors.get(authorId);
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        generateRecommendation(authorObj, papers, similarityScheme, n);
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

    /**
     * This method build list of top n papers to recommend to an author.
     *
     * @param author: current author
     * @param papers: hashmap of all papers to recommend.
     * @param similarityScheme 0: cosine
     * @param n: top n item to recommend.
     *
     * @return recommendationPapers
     */
    private static void generateRecommendation(Author author, HashMap<String, Paper> papers, 
            int similarityScheme, int n) throws Exception {

        List<String> recommendedPapers = new ArrayList<>();
        HashMap<String, Double> paperSimilarityHM = new HashMap(); // <IDPaper, SimValue>

        // Compute similarities between current author and all papers.
        if (similarityScheme == 0) {
            for (String key : papers.keySet()) {
                Double similarity = new Double(WeightingUtility.computeCosine(author.getFeatureVector(), papers.get(key).getFeatureVector()));
                paperSimilarityHM.put(key, similarity);
            }
        }

        // Sort papers descending based on similarity.
        LinkedHashMap<String, Double> sortedPaperSimilarityHM = HashMapUtility.getSortedMapDescending(paperSimilarityHM);

        // Take top n papers.
        int counter = 0;
        for (String paperId : sortedPaperSimilarityHM.keySet()) {
            recommendedPapers.add(paperId);
            counter++;
            if (counter >= n) {
                break;
            }
        }
        
        author.setRecommendationList(recommendedPapers);
        System.out.println(count++ + (new Date(System.currentTimeMillis()).toString()) + "DONE for :" + author.getAuthorId());
    }
}
