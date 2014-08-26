/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.utility;

import java.util.HashMap;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.WeightingUtility;

/**
 *
 * @author THNghiep
 */
public class PaperFilterUtility {
    
    private PaperFilterUtility() {}
    
    public static void filterOldPaper(HashMap<String, Author> authors, 
            HashMap<String, Paper> papers, 
            int cutYear) throws Exception {

        for (String idPaper : papers.keySet()) {
            if (papers.get(idPaper).getYear() < cutYear) {
                for (String idAuthor : authors.keySet()) {
                    authors.get(idAuthor).getCbfSimHM().remove(idPaper);
                    authors.get(idAuthor).getCfRatingHM().remove(idPaper);
                    authors.get(idAuthor).getCbfCfHybridHM().remove(idPaper);
                    authors.get(idAuthor).getTrustedPaperHM().remove(idPaper);
                    authors.get(idAuthor).getCbfTrustHybridHM().remove(idPaper);
                    authors.get(idAuthor).getFinalRecommendingScoreHM().remove(idPaper);
                }
            }
        }
    }
}
