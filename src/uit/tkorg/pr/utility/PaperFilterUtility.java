/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;

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
                    authors.get(idAuthor).getCbfCfHybridV2HM().remove(idPaper);
                    authors.get(idAuthor).getCbfCfHybridV3HM().remove(idPaper);
                    authors.get(idAuthor).getTrustedPaperHM().remove(idPaper);
                    authors.get(idAuthor).getCbfTrustHybridHM().remove(idPaper);
                    authors.get(idAuthor).getCbfTrustHybridV2HM().remove(idPaper);
                    authors.get(idAuthor).getCbfTrustHybridV3HM().remove(idPaper);
                    authors.get(idAuthor).getFinalRecommendingScoreHM().remove(idPaper);
                }
            }
        }
    }

    public static void filterPaperRatingListByTestSet(HashMap<String, Author> authors, 
            HashSet<String> paperIdsInTestSet) throws Exception {

        Iterator<String> iter;
        for (Author authorObj : authors.values()) {
            iter = authorObj.getCbfSimHM().keySet().iterator();
            while (iter.hasNext()) {
                if (!paperIdsInTestSet.contains(iter.next())) {
                    iter.remove();
                }
            }
            iter = authorObj.getCfRatingHM().keySet().iterator();
            while (iter.hasNext()) {
                if (!paperIdsInTestSet.contains(iter.next())) {
                    iter.remove();
                }
            }
            iter = authorObj.getCbfCfHybridHM().keySet().iterator();
            while (iter.hasNext()) {
                if (!paperIdsInTestSet.contains(iter.next())) {
                    iter.remove();
                }
            }
            iter = authorObj.getCbfCfHybridV2HM().keySet().iterator();
            while (iter.hasNext()) {
                if (!paperIdsInTestSet.contains(iter.next())) {
                    iter.remove();
                }
            }
            iter = authorObj.getCbfCfHybridV3HM().keySet().iterator();
            while (iter.hasNext()) {
                if (!paperIdsInTestSet.contains(iter.next())) {
                    iter.remove();
                }
            }
            iter = authorObj.getTrustedPaperHM().keySet().iterator();
            while (iter.hasNext()) {
                if (!paperIdsInTestSet.contains(iter.next())) {
                    iter.remove();
                }
            }
            iter = authorObj.getCbfTrustHybridHM().keySet().iterator();
            while (iter.hasNext()) {
                if (!paperIdsInTestSet.contains(iter.next())) {
                    iter.remove();
                }
            }
            iter = authorObj.getCbfTrustHybridV2HM().keySet().iterator();
            while (iter.hasNext()) {
                if (!paperIdsInTestSet.contains(iter.next())) {
                    iter.remove();
                }
            }
            iter = authorObj.getCbfTrustHybridV3HM().keySet().iterator();
            while (iter.hasNext()) {
                if (!paperIdsInTestSet.contains(iter.next())) {
                    iter.remove();
                }
            }
            iter = authorObj.getFinalRecommendingScoreHM().keySet().iterator();
            while (iter.hasNext()) {
                if (!paperIdsInTestSet.contains(iter.next())) {
                    iter.remove();
                }
            }
        }
    }
}
