/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation.cbf;

import ir.vsr.HashMapVector;
import java.util.HashMap;
import java.util.List;
import uit.tkorg.pr.dataimport.model.Paper;
import uit.tkorg.pr.utility.general.TextUtility;
import uit.tkorg.pr.utility.general.Weighting;

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
            papers.get(key).setContent(TextUtility.computeTFIDF());
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
            papers.get(key).setFeatureVector(computePaperFV(papers, key, combiningScheme, weightingScheme));
        }
    }

    /**
     * This method compute final feature vector by combining citation and
     * reference.
     *
     * @param paperId
     * @param weightingScheme 0: linear; 1: cosine; 2: rpy
     * @return list represents feature vector.
     */
    public static HashMapVector computePaperFV(HashMap<String, Paper> papers, String paperId, int combiningScheme, int weightingScheme) throws Exception {
        HashMapVector featureVector = null;
        
        if (combiningScheme == 0) {
            featureVector = papers.get(paperId).getContent();
        } else if (weightingScheme == 0) {
            featureVector = computePaperFVLinear(papers, paperId);
        } else if (weightingScheme == 1) {
            featureVector = computePaperFVCosine(papers, paperId);
        } else if (weightingScheme == 2) {
            featureVector = computePaperFVRPY(papers, paperId);
        }
        
        return featureVector;
    }

    /**
     * This method compute Paper Feature Vector with linear weight
     *
     * @param paperId
     * @return featureVector
     */
    public static HashMapVector computePaperFVLinear(HashMap<String, Paper> papers, String paperId) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        Paper paper = papers.get(paperId);//get paper has Id is paperId in ListofPapers
        
        featureVector.add(paper.getContent());//assign HashMapVector featureVector equal HashMapVector paper
        
        List<String> citation = paper.getCitation();//get list of citation paper
        featureVector.add(sumFVLinear(papers, citation));//add featureVector with featureVector of citation papers

        List<String> reference = paper.getReference();//get list of reference paper
        featureVector.add(sumFVLinear(papers, reference));//add featureVector with featureVector of reference papers
        
        return featureVector;
    }

    /**
     * This method compute Paper Feature Vector with cosine weight
     *
     * @param paperId
     * @return featureVector
     */
    public static HashMapVector computePaperFVCosine(HashMap<String, Paper> papers, String paperId) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        Paper paper = papers.get(paperId);//get paper has paperId in ListofPapers
        
        featureVector.add(paper.getContent());//assign HashMapVector featureVector equal HashMapVector paper
        
        List<String> citation = paper.getCitation();//get list of citation paper
        featureVector.add(sumFVCosine(papers, paper, citation));//add featureVector with featureVector of citation papers

        List<String> reference = paper.getReference();//get list of reference paper
        featureVector.add(sumFVCosine(papers, paper, reference));//add featureVector with featureVector of reference papers
        
        return featureVector;
    }

    /**
     * This method compute Paper Feature Vector with RPY weight
     *
     * @param paperId
     * @return featureVector
     */
    public static HashMapVector computePaperFVRPY(HashMap<String, Paper> papers, String paperId) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        Paper paper = papers.get(paperId);//assign HashMapVector featureVector equal HashMapVector paper
        
        featureVector.add(paper.getContent());//get list of citation paper
        
        List<String> citation = paper.getCitation();//get list of citation paper
        featureVector.add(sumFVRPY(papers, paper, citation));//add featureVector with featureVector of citation papers

        List<String> reference = paper.getReference();//get list of reference paper
        featureVector.add(sumFVRPY(papers, paper, reference));//add featureVector with featureVector of reference papers
        
        return featureVector;
    }

    /**
     * This method compute sum of Papers(Citation or Reference Paper) with
     * linear weight
     *
     * @param paperIds
     * @return featureVector
     */
    public static HashMapVector sumFVLinear(HashMap<String, Paper> papers, List<String> paperIds) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        for (String paperId : paperIds) {
            if (papers.containsKey(paperId)) {
                featureVector.add(papers.get(paperId).getContent());
            }
        }
        
        return featureVector;
    }

    /**
     * This method compute sum of Papers(Citation or Reference Paper) with
     * cosine weight
     *
     * @param cpaper
     * @param paperIds
     * @return featureVector
     */
    public static HashMapVector sumFVCosine(HashMap<String, Paper> papers, Paper cpaper, List<String> paperIds) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        for (String paperId : paperIds) {
            if (papers.containsKey(paperId)) {
                double cosine = Weighting.computeCosine(cpaper.getContent(), papers.get(paperId).getContent());
                featureVector.addScaled(papers.get(paperId).getContent(), cosine);
            }
        }
        
        return featureVector;
    }

    /**
     * This method compute sum of Papers(Citation or Reference Paper) with rpy
     * weight
     *
     * @param cpaper
     * @param paperIds
     * @return featureVector
     */
    public static HashMapVector sumFVRPY(HashMap<String, Paper> papers, Paper cpaper, List<String> paperIds) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        for (String paperId : paperIds) {
            if (papers.containsKey(paperId)) {
                double rpy = Weighting.computeRPY(cpaper.getYear(), papers.get(paperId).getYear());
                featureVector.addScaled(papers.get(paperId).getContent(), rpy);
            }
        }
        
        return featureVector;
    }
}
