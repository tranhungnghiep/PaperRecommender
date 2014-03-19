/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.controller.recommendation;

import java.util.HashMap;
import uit.tkorg.paperrecommender.model.Paper;

/**
 *
 * @author THNghiep
 * This class handles all logic for paper object.
 * Method: 
 * - Generate list of papers (key: paper id, value: object paper).
 * - Compute weight: cosine, rpy.
 * - Compute papers' full vector: linear, cosine, rpy.
 */
public class PaperLogic {
    HashMap<String, Paper> papers = null;
    
    public void buildListOfPapers() {
        
    }
}
