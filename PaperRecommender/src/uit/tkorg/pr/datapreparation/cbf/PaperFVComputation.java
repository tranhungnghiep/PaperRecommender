/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation.cbf;

import ir.vsr.HashMapVector;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.WeightingUtility;

/**
 *
 * @author THNghiep 
 * This class handles logic to compute feature vector of all papers.
 * Method: 
 * - Compute papers' full vector: its content itself or combining its refs and cits by linear, cosine, rpy.
 */
public class PaperFVComputation {

    // Prevent instantiation.
    private PaperFVComputation() {}

    public static void clearPaperAbstract(HashMap<String, Paper> papers) throws Exception {
        for (String paperId : papers.keySet()) {
            papers.get(paperId).setPaperAbstract(null);
        }
    }

    public static void setTFIDFVectorForAllPapers(HashMap<String, Paper> papers, HashMap<String, HashMapVector> vectorizedDocuments) throws Exception {
        for (String paperId : papers.keySet()) {
            papers.get(paperId).setTfidfVector(vectorizedDocuments.get(paperId));
        }
    }

    /**
     * This method computes and set value for all papers' full feature vector
     * (after combining citation and reference papers).
     *
     * @param combiningScheme   0: itself content, 1: itself content + content of references; 
     *                          2: itself content + content of citations; 3: itself content + content of references + content of citations.
     * @param weightingScheme   0: linear; 1: cosine; 2: rpy
     */
    public static void computeFeatureVectorForAllPapers(HashMap<String, Paper> papers, int combiningScheme, int weightingScheme) throws Exception {
        for (String paperId : papers.keySet()) {
            List<String> combiningPapers = getCombiningPapers(papers, paperId, combiningScheme);
            HashMapVector featureVector = computePaperFV(papers, paperId, combiningPapers, weightingScheme);
            papers.get(paperId).setFeatureVector(featureVector);
        }
    }

    /**
     * 
     * @param papers
     * @param paperId
     * @param combiningScheme
     * @return List of papers which are used to combine with current paper.
     * @throws Exception 
     */
    private static List<String> getCombiningPapers(HashMap<String, Paper> papers, String paperId, int combiningScheme) throws Exception {
        List<String> combiningPapers = null;
        
        // combining scheme
        if (combiningScheme == 0) {
            combiningPapers = new ArrayList<>();
        } else if (combiningScheme == 1) {
            combiningPapers = papers.get(paperId).getReference();
        } else if (combiningScheme == 2) {
            combiningPapers = papers.get(paperId).getCitation();
        } else if (combiningScheme == 3) {
            combiningPapers = papers.get(paperId).getReference();
            combiningPapers.addAll(papers.get(paperId).getCitation());
        }
        
        return combiningPapers;
    }
    
    /**
     * This method compute final feature vector by combining citation and
     * reference.
     *
     * @param paperId
     * @return list represents feature vector.
     */
    public static HashMapVector computePaperFV(HashMap<String, Paper> papers, String paperId, List<String> combiningPapers, int weightingScheme) throws Exception {
        HashMapVector featureVector = new HashMapVector();

        Paper paper = papers.get(paperId);//get paper has Id is paperId in ListofPapers
        featureVector.add(paper.getTfidfVector());//assign HashMapVector featureVector equal HashMapVector paper
        
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
                featureVector.add(papers.get(combiningPaperId).getTfidfVector());
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
                double cosine = WeightingUtility.computeCosine(paper.getTfidfVector(), papers.get(combiningPaperId).getTfidfVector());
                featureVector.addScaled(papers.get(combiningPaperId).getTfidfVector(), cosine);
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
                double rpy = WeightingUtility.computeRPY(paper.getYear(), papers.get(combiningPaperId).getYear(), 0.9);
                featureVector.addScaled(papers.get(combiningPaperId).getTfidfVector(), rpy);
            }
        }
        
        return featureVector;
    }
}
