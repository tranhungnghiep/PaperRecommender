/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.controller.recommendation;

import ir.vsr.HashMapVector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import obsolete.uit.tkorg.paperrecommender.utility.CalculatorFeatureVector;
import obsolete.uit.tkorg.paperrecommender.utility.FormulaCandiatePaper;
import obsolete.uit.tkorg.paperrecommender.utility.FormulaJuniorResearcher;
import obsolete.uit.tkorg.paperrecommender.utility.FormulaSeniorResearcher;
import obsolete.uit.tkorg.paperrecommender.utility.GetDatabaseMysql;
import uit.tkorg.paperrecommender.utility.Weighting;

/**
 * This method create a list similarities of each author with each candidates
 * paper
 *
 * @author THNghiep
 */
public class Recommender {

    //Store all similarities of each author with each candidates
    HashMap<String,HashMap> similarities = new  HashMap<String,HashMap>();
    
    AuthorLogic authors = new AuthorLogic();
    HashMap<String, HashMapVector> authorvectors = authors.allfeaturevectors;
    
    PaperLogic papers = new PaperLogic();
    HashMap<String, HashMapVector> papervectors = papers.allfeaturevectors;

    /**
     * This method store list of similarities of each author with each candidates
     * @throws java.lang.Exception
     */
    public void similarity() throws Exception {
        for(String entry:authorvectors.keySet()){
            similarities.put(entry, computeSimilarityOfAuthor(entry));
        }
    }

    /**
     * This method create a list similarities of an author with each candidates
     *
     * @param authorId
     * @return
     * @throws java.lang.Exception
     */
    public HashMap<String, Double> computeSimilarityOfAuthor(String authorId) throws Exception {
        HashMap<String, Double> cosines = new HashMap<String, Double>();
        for (String entry : papervectors.keySet()) {
            cosines.put(entry, Weighting.computeCosine(authorvectors.get(authorId), papervectors.get(entry)));
        }
        cosines = (HashMap<String, Double>) sortSimilarityOfAuthor(cosines);
        return cosines;
    }

    /**
     * This method sort similarities descending
     *
     * @param hashMap
     * @return sortMap
     */
    public Map sortSimilarityOfAuthor(HashMap<String, Double> hashMap) {
        List list = new LinkedList(hashMap.entrySet());
        //sort list based on comparator
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        //put sorted list into map again
        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
