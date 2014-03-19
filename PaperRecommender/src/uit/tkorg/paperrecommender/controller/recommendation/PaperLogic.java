/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.controller.recommendation;

import java.util.HashMap;
import java.util.List;
import uit.tkorg.paperrecommender.model.Paper;
import uit.tkorg.paperrecommender.utility.FlatFileData.ImportDataset1;

/**
 *
 * @author THNghiep
 * This class handles all logic for paper object.
 * Method: 
 * - Generate list of papers (key: paper id, value: object paper).
 * - Compute papers' full vector: linear, cosine, rpy.
 */
public class PaperLogic {
    HashMap<String, Paper> papers = null;
    
    /**
     * This method builds a hashmap of papers.
     */
    public void buildListOfPapers() {
        papers = ImportDataset1.buildListOfPapers();
    }
    
    /**
     * This method compute final feature vector by combining citation and reference.
     * @param paperId
     * @param weightingScheme
     * - 0: linear
     * - 1: cosine
     * - 2: rpy
     * @return list represents feature vector.
     */
    public List computePaperFeatureVector(String paperId, int weightingScheme) {
        List fearureVector = null;
        // compute here.
        return fearureVector;
    }
}
