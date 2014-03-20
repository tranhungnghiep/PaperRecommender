/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.utility.FlatFileData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import uit.tkorg.paperrecommender.model.Paper;

/**
 *
 * @author THNghiep
 * This class contents all method to import data from dataset1.
 * Import process needs to filter out noisy data such as keywords longer than 50 characters.
 */
public class ImportDataset1 {
    /**
     * This method read all keywords in all papers in the dataset 1 and return them in an arraylist.
     * @return allKeywords.
     */
    public static List readAllKeywords() {
        List allKeywords = new ArrayList();
        // generate list here.
        return allKeywords;
    }

    /**
     * This method read dataset folder (from constant class), then for each paper, 
     * create a Paper object and put it in the hashmap.
     * HashMap Key: paper id (in file name)
     * HashMap Value: paper object.
     * @return the hashmap contents all papers.
     */
    public static HashMap<String, Paper> buildListOfPapers() {
        HashMap<String, Paper> papers = new HashMap<String, Paper>();
        // generate map here.
        return papers;
    }
}
