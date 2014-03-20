/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.controller.recommendation;

import java.io.IOException;
import java.util.HashMap;
import uit.tkorg.paperrecommender.model.Author;
import uit.tkorg.paperrecommender.model.Paper;
import uit.tkorg.paperrecommender.utility.FlatFileData.ImportDataset1;

/**
 * This class handles all logics for author object.
 * Data: List of author: junior and senior.
 * Method: 
 * - Build list authors.
 * - Compute author feature vector.
 * @author THNghiep
 */
public class AuthorLogic {
    // Key of this hash map is paper id.
    // Value of this hash map is the relevant paper object.
    HashMap<String, Author> authors = null;
    
    public void buildListOfAuthors() throws IOException {
        authors = ImportDataset1.buildListOfAuthors();
    }
    
}
