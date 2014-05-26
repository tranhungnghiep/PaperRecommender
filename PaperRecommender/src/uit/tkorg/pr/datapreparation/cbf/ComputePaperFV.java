/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation.cbf;

import ir.vsr.HashMapVector;
import java.util.HashMap;
import java.util.List;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.textvectorization.TextVectorizationByMahoutTerminalUtility;
import uit.tkorg.utility.general.WeightingUtility;

/**
 *
 * @author THNghiep 
 * This class handles logic to compute feature vector of all papers.
 * Method: 
 * - Compute papers' full vector: its content itself or combining its refs and cits by linear, cosine, rpy.
 */
public class ComputePaperFV {

    // Prevent instantiation.
    private ComputePaperFV() {
    }

    public static void computeAllPapersContent(HashMap<String, Paper> papers) throws Exception {
        for (String key : papers.keySet()) {
//            papers.get(key).setContent(TextVectorizationByMahoutTerminalUtility.computeTFIDF());
        }
    }

    /**
     * This method computes and set value for all papers' full feature vector
     * (after combining citation and reference papers).
     *
     * @param combiningScheme 0: itself, 1: ref only; 2: cite only; 3: ref and cite.
     * @param weightingScheme 0: linear; 1: cosine; 2: rpy
     */
    public static void computeAllPapersFV(HashMap<String, Paper> papers, int combiningScheme, int weightingScheme) throws Exception {
        for (String key : papers.keySet()) {
            List<String> combiningPapers = getCombiningPapers(papers, key, combiningScheme);
            HashMapVector featureVector = computePaperFV(papers, key, combiningPapers, weightingScheme);
            papers.get(key).setFeatureVector(featureVector);
        }
    }

    /**
     * 
     * @param papers
     * @param key
     * @param combiningScheme
     * @return List of papers which are used to combine with current paper.
     * @throws Exception 
     */
    private static List<String> getCombiningPapers(HashMap<String, Paper> papers, String key, int combiningScheme) throws Exception {
        List<String> combiningPapers = null;
        
        // combining scheme
        if (combiningScheme == 0) {
            return null;
        } else if (combiningScheme == 1) {
            combiningPapers = papers.get(key).getReference();
        } else if (combiningScheme == 2) {
            combiningPapers = papers.get(key).getCitation();
        } else if (combiningScheme == 3) {
            combiningPapers = papers.get(key).getReference();
            combiningPapers.addAll(papers.get(key).getCitation());
        }
        
        return combiningPapers;
    }
    
    /**
     * This method compute final feature vector by combining citation and
     * reference.
     *
     * @param key
     * @return list represents feature vector.
     */
    public static HashMapVector computePaperFV(HashMap<String, Paper> papers, String key, List<String> combiningPapers, int weightingScheme) throws Exception {
        HashMapVector featureVector = new HashMapVector();

        Paper paper = papers.get(key);//get paper has Id is paperId in ListofPapers
        featureVector.add(paper.getContent());//assign HashMapVector featureVector equal HashMapVector paper
        
        // weighting scheme
        if (weightingScheme == 0) {
            featureVector.add(sumFVLinear(papers, combiningPapers)); // add featureVector of combining papers
        } else if (weightingScheme == 1) {
            featureVector.add(sumFVCosine(papers, paper, combiningPapers));
        } else if (weightingScheme == 2) {
            featureVector.add(sumFVRPY(papers, paper, combiningPapers));
        }
        
        return featureVector;
    }

    /**
     * This method compute sum of Papers(Citation or Reference Paper) with
     * linear weight
     *
     * @param combiningPaperIds
     * @return featureVector
     */
    private static HashMapVector sumFVLinear(HashMap<String, Paper> papers, List<String> combiningPaperIds) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        for (String combiningPaperId : combiningPaperIds) {
            if (papers.containsKey(combiningPaperId)) {
                featureVector.add(papers.get(combiningPaperId).getContent());
            }
        }
        
        return featureVector;
    }

    /**
     * This method compute sum of Papers(Citation or Reference Paper) with
     * cosine weight
     *
     * @param paper
     * @param combiningPaperIds
     * @return featureVector
     */
    private static HashMapVector sumFVCosine(HashMap<String, Paper> papers, Paper paper, List<String> combiningPaperIds) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        for (String combiningPaperId : combiningPaperIds) {
            if (papers.containsKey(combiningPaperId)) {
                double cosine = WeightingUtility.computeCosine(paper.getContent(), papers.get(combiningPaperId).getContent());
                featureVector.addScaled(papers.get(combiningPaperId).getContent(), cosine);
            }
        }
        
        return featureVector;
    }

    /**
     * This method compute sum of Papers(Citation or Reference Paper) with rpy
     * weight
     *
     * @param paper
     * @param combiningPaperIds
     * @return featureVector
     */
    private static HashMapVector sumFVRPY(HashMap<String, Paper> papers, Paper paper, List<String> combiningPaperIds) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        for (String combiningPaperId : combiningPaperIds) {
            if (papers.containsKey(combiningPaperId)) {
                double rpy = WeightingUtility.computeRPY(paper.getYear(), papers.get(combiningPaperId).getYear());
                featureVector.addScaled(papers.get(combiningPaperId).getContent(), rpy);
            }
        }
        
        return featureVector;
    }
}
