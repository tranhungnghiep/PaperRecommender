/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation.hybrid;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import uit.tkorg.pr.method.cbf.FeatureVectorSimilarity;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.CitationAuthorNet;
import uit.tkorg.pr.model.CoAuthorNet;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.HashMapUtility;
import uit.tkorg.utility.general.WeightingUtility;

/**
 *
 * @author Administrator
 */
public class TrustHybridDataModelPreparation {

    // Prevent instantiation.
    private TrustHybridDataModelPreparation() {
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
    }
    
    public static void computeCitationAuthorRSSHM(HashMap<String, Author> authors, 
            String file_All_AuthorID_PaperID, String file_PaperID_RefID,
            HashMap<String, HashMap<String, Float>> referenceRSSNet) throws Exception {
        CitationAuthorNet.getInstance().load_AuthorID_PaperID(file_All_AuthorID_PaperID);
        CitationAuthorNet.getInstance().load_PaperID_RefID(file_PaperID_RefID);
        CitationAuthorNet.getInstance().buildRefGraph();
        CitationAuthorNet.getInstance().buildRefRSSGraph();

        referenceRSSNet = CitationAuthorNet.getInstance().getReferenceRSSNet();
        
        for (String authorId : CitationAuthorNet.getInstance().getReferenceRSSNet().keySet()) {
            if (authors.containsKey(authorId)) {
                authors.get(authorId).setCitationAuthorRSSHM(
                        CitationAuthorNet.getInstance().getReferenceRSSNet().get(authorId));
            }
        }
    }
}
