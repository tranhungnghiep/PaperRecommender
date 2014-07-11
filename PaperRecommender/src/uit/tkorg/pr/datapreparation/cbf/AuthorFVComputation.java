/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation.cbf;

import ir.vsr.HashMapVector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.WeightingUtility;

/**
 * This class handles all logics for author object. 
 * Data: List of author: junior and senior. 
 * Method: 
 * - Build list authors. 
 * - Compute author feature vector.
 *
 * @author THNghiep
 */
public class AuthorFVComputation {

    // Prevent instantiation.
    private AuthorFVComputation() {
    }

    public static HashSet<String> getPaperIdsOfAuthors(HashMap<String, Author> authors) throws Exception {
        HashSet<String> paperIds = new HashSet<>();
        
        for (String authorId : authors.keySet()) {
            paperIds.addAll(authors.get(authorId).getPaperList());
        }
        
        return paperIds;
        
    }

    public static HashSet<String> getPaperIdsTestSet(HashMap<String, Author> authors) throws Exception {
        HashSet<String> paperIds = new HashSet<>();
        
        for (String authorId : authors.keySet()) {
            paperIds.addAll(authors.get(authorId).getGroundTruth());
        }
        
        return paperIds;
    }

    /**
     * This method get all papers attached in hashmap authors recursively.
     * 
     * @param authors
     * @return Hashmap of all papers attached in hashmap authors.
     * @throws Exception 
     */
    public static HashMap<String, Paper> getPapersFromAuthors(HashMap<String, Author> authors) throws Exception {
        HashMap<String, Paper> allPapers = new HashMap<>();
        
        for (String authorId : authors.keySet()) {
            authors.get(authorId).setPaperList(convertPaperListToPaperIdList(allPapers, authors.get(authorId).getPaperList()));
        }
        
        return allPapers;
    }

    /**
     * This method is called recursively to convert a list of papers to list of 
     * paper ids and save the paper object to the hashmap allPapers.
     * 
     * @param allPapers
     * @param paperList
     * @return List of string containing all paper ids corresponding to paperList.
     * @throws Exception 
     */
    private static List<String> convertPaperListToPaperIdList(HashMap<String, Paper> allPapers, List<Paper> paperList) throws Exception {
        List<String> paperIdList = new ArrayList<>();

        for (Paper paper : paperList) {
            paperIdList.add(paper.getPaperId());
            if(!allPapers.containsKey(paper.getPaperId())) {
                allPapers.put(paper.getPaperId(), paper); // Note: because of exploiting ref type parameter in recursion, need to put paper into allPapers right after checking for existence, before call recursion, to avoid re-put duplicating Paper.
                if ((paper.getReferenceList() != null) && (paper.getReferenceList().size() > 0)) {
                    paper.setReferenceList(convertPaperListToPaperIdList(allPapers, paper.getReferenceList()));
                }
                if ((paper.getCitationList() != null) && (paper.getCitationList().size() > 0)) {
                    paper.setCitationList(convertPaperListToPaperIdList(allPapers, paper.getCitationList())); // Exploiting mutable object paper.
                }
            }
        }
        
        return paperIdList;
    }

    /**
     * This method computes and set value of full feature vector for all authors. 
     * Need to put in a hashmap containing all papers with FV computed.
     * 
     * @param timeAwareScheme 
     *      0: unaware of author's publication time
     *      1: weighting author's publications by exp(- delta time * gamma).
     * @param gamma: forgetting coefficient 0 <= gamma <= 1.
     */
    public static void computeFVForAllAuthors(HashMap<String, Author> authors, HashMap<String, Paper> papers, 
            int timeAwareScheme, double gamma) throws Exception {

        for (String authorId : authors.keySet()) {
            authors.get(authorId).setFeatureVector(computeAuthorFV(authors, authorId, papers, timeAwareScheme, gamma));
        }
    }

    /**
     * @return author feature vector.
     */
    public static HashMapVector computeAuthorFV(HashMap<String, Author> authors, String authorId, HashMap<String, Paper> papers, 
            int timeAwareScheme, double gamma) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        Author author = authors.get(authorId);
        
        List<String> paperIds = author.getPaperList();
        
        if (timeAwareScheme == 0) {
            for (String paperId : paperIds) {
                featureVector.add(papers.get(paperId).getFeatureVector());
            }
        } else if (timeAwareScheme == 1) {
            int latestPublicationYear = getLatestPublicationYear(papers, paperIds);
            for (String paperId : paperIds) {
                double ff = WeightingUtility.computeForgettingFactor(latestPublicationYear, papers.get(paperId).getYear(), gamma);
                featureVector.addScaled(papers.get(paperId).getFeatureVector(), ff);
            }
        }
        
        return featureVector;
    }

    /**
     * 
     * @param papers
     * @param paperIds
     * @return the latest publication year.
     * @throws Exception 
     */
    private static int getLatestPublicationYear(HashMap<String, Paper> papers, List<String> paperIds) throws Exception {
        int latestPublicationYear = -1;
        
        for (String paperId : paperIds) {
            if (papers.get(paperId).getYear() > latestPublicationYear) {
                latestPublicationYear = papers.get(paperId).getYear();
            }
        }
        
        return latestPublicationYear;
    }
}
