/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.method.cbf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.HashMapUtility;
import uit.tkorg.utility.general.WeightingUtility;

/**
 * This class handles logic for recommending papers to each author.
 * Data: list of authors used as a universal recommendation list.
 * Method: 
 * - generateRecommendationForAllAuthors: 
 * + input: list of authors, list of papers.
 * + output: list of authors with recommendation list included, also includes all data of the input list of authors.
 * This output could be used as universal recommendation list, upto the input list of authors.
 * @author THNghiep
 */
public class CBFRecommender {
    
    // Prevent instantiation.
    private CBFRecommender() {
    }

    /**
     * This method runs content based recommendation business for all authors.
     * 
     * @param authors: all authors.
     * @param papers: all papers.
     * @param similarityScheme 0: cosine
     * @param n: top n item to recommend.
     * 
     * - For each author:
     * + Compute similarity with all papers.
     * + Sort list of papers, based on similarity.
     * + Take top n papers with highest similarity for the recommendation list.
     * + Save recommendation list into current author.
     */
    public static void generateRecommendationForAllAuthors(HashMap<String, Author> authors, HashMap<String, Paper> papers, int similarityScheme, int n) throws Exception {
        for (String authorId : authors.keySet()) {
            authors.get(authorId).setRecommendation(generateRecommdation(authors.get(authorId), papers, similarityScheme, n));
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
    private static List<String> generateRecommdation(Author author, HashMap<String, Paper> papers, int similarityScheme, int n) throws Exception {
        List<String> recommendedPapers = new ArrayList<>();
        HashMap<String, Double> paperSimilarity = new HashMap();
        
        // Compute similarities between current author and all papers.
        if (similarityScheme == 0) {
            for (String key : papers.keySet()) {
                Double similarity = new Double(WeightingUtility.computeCosine(author.getFeatureVector(), papers.get(key).getFeatureVector()));
                paperSimilarity.put(key, similarity);
            }
        }
        
        // Sort papers descending based on similarity.
        LinkedHashMap<String, Double> sortedPaperSimilarity = HashMapUtility.getSortedMapDescending(paperSimilarity);
        
        // Take top n papers.
        int counter = 0;
        for (String paperId : sortedPaperSimilarity.keySet()) {
            recommendedPapers.add(paperId);
            counter++;
            if (counter >= n) {
                break;
            }
        }
        
        return recommendedPapers;
    }
}
