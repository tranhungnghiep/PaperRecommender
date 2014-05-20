/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.method.cbf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import uit.tkorg.pr.dataimport.model.Author;
import uit.tkorg.pr.dataimport.model.Paper;
import uit.tkorg.pr.utility.general.GeneralUtility;
import uit.tkorg.pr.utility.general.Weighting;

/**
 * This class handles logic for recommending papers to each author.
 * Data: list of authors used as a universal recommendation list.
 * Method: 
 * - buildAllRecommendationLists: 
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
     * This method runs recommendation business.
     * @param authorsInput
     * @param papers
     * - For each author:
     * + Compute similarity with all papers.
     * + Sort list of papers, based on similarity.
     * + Take top ten papers with highest similarity for the recommendation list.
     * + Save recommendation list into current author.
     * - Finish all authors, finish the hashmap of authors with all input data plus recommendation list.
     */
    public static HashMap<String, Author> buildAllRecommendationLists(HashMap<String, Author> authorsInput, HashMap<String, Paper> papers) throws Exception {
        HashMap<String, Author> authors = authorsInput;
        List<String> recommendationPapers;
        for (String key : authorsInput.keySet()) {
            recommendationPapers = buildRecommdationList(authors.get(key), papers);
            authors.get(key).setRecommendation(recommendationPapers);
        }
        return authors;
    }

    /**
     * This method build list of 10 papers to recommend to an author.
     * @param author: current author
     * @param papers: hashmap of all papers to recommend.
     * @return recommendationPapers
     */
    private static List<String> buildRecommdationList(Author author, HashMap<String, Paper> papers) throws Exception {
        List<String> recommendationPapers = new ArrayList();
        LinkedHashMap<String, Double> paperSimilarity = new LinkedHashMap();
        
        // Compute similarities between current author and all papers.
        for (String key : papers.keySet()) {
            Double similarity = new Double(Weighting.computeCosine(author.getFeatureVector(), papers.get(key).getFeatureVector()));
            paperSimilarity.put(key, similarity);
        }
        
        // Sort papers descending based on similarity.
        paperSimilarity = GeneralUtility.sortHashMap(paperSimilarity);
        
        // Take top ten papers.
        int counter = 0;
        for (String paperId : paperSimilarity.keySet()) {
            recommendationPapers.add(paperId);
            counter++;
            if (counter >= 10) {
                break;
            }
        }
        
        return recommendationPapers;
    }
}
