/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation;

import java.util.ArrayList;
import java.util.HashMap;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.CitationAuthorNet;
import uit.tkorg.pr.model.CoAuthorNet;
import uit.tkorg.utility.general.HashMapUtility;

/**
 *
 * @author Administrator
 */
public class TrustDataModelPreparation {

    // Prevent instantiation.
    private TrustDataModelPreparation() {
    }

    public static void computeCoAuthorRSSHM(HashMap<String, Author> authors,
            String file_AuthorID_PaperID, String file_PaperID_Year) throws Exception {
        
        CoAuthorNet.getInstance().LoadTrainingData(file_AuthorID_PaperID, file_PaperID_Year);
        CoAuthorNet.getInstance().BuildCoAuthorGraph();
        CoAuthorNet.getInstance().BuildingRSSGraph();

        for (String authorId : CoAuthorNet.getInstance().getRssNet().keySet()) {
            if (authors.containsKey(authorId)) {
                authors.get(authorId).setCoAuthorRSSHM(
                        CoAuthorNet.getInstance().getRssNet().get(authorId));
            }
        }
        
        // Normalize
        for (Author author : authors.values()) {
            HashMapUtility.minNormalizeHashMap(author.getCoAuthorRSSHM());
    }
    }

    public static void computeCitationAuthorRSSHM(HashMap<String, Author> authors,
            String file_All_AuthorID_PaperID, String file_PaperID_RefID,
            HashMap<String, HashMap<String, Float>> referenceRSSNet, 
            HashMap<String, ArrayList<String>> authorPaperHM) throws Exception {
        
        CitationAuthorNet.getInstance().load_AuthorID_PaperID(file_All_AuthorID_PaperID);
        CitationAuthorNet.getInstance().load_PaperID_RefID(file_PaperID_RefID);
        CitationAuthorNet.getInstance().buildRefGraph();
        CitationAuthorNet.getInstance().buildRefRSSGraph();

        referenceRSSNet.putAll(CitationAuthorNet.getInstance().getReferenceRSSNet());
        
        // Normalize
        for (HashMap<String, Float> hm : referenceRSSNet.values()) {
            HashMapUtility.minNormalizeHashMap(hm);
        }

        // Put into author model.
        for (String authorId : referenceRSSNet.keySet()) {
            if (authors.containsKey(authorId)) {
                authors.get(authorId).setCitationAuthorRSSHM(referenceRSSNet.get(authorId));
            }
        }
        
        // Get authorPaper.
        authorPaperHM.putAll(CitationAuthorNet.getInstance().getAuthorID_PaperID_List());
    }
}
