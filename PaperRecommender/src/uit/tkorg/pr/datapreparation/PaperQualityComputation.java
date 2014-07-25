/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation;

import java.util.HashMap;
import uit.tkorg.pr.model.Paper;

/**
 *
 * @author THNghiep
 */
public class PaperQualityComputation {

    // Prevent instantiation.
    private PaperQualityComputation() {}


    public static void computeQualityValueForAllPapers(HashMap<String, Paper> papers) throws Exception {
        Float minQuality = 0f;
        Float maxQuality = 0f;
        
        for (String paperId : papers.keySet()) {
            papers.get(paperId).setQualityValue(
                    (float) papers.get(paperId).getCitationList().size());
            if (minQuality > papers.get(paperId).getQualityValue()) {
                minQuality = papers.get(paperId).getQualityValue();
            }
            if (maxQuality < papers.get(paperId).getQualityValue()) {
                maxQuality = papers.get(paperId).getQualityValue();
            }
        }
        
        for (String paperId : papers.keySet()) {
            papers.get(paperId).setQualityValue(
                    (papers.get(paperId).getQualityValue() - minQuality)
                    / (maxQuality - minQuality));
        }
    }
}
