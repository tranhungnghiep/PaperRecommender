/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation;

import ir.vsr.HashMapVector;
import java.util.HashMap;
import java.util.List;
import uit.tkorg.pr.dataimport.model.Paper;
import uit.tkorg.pr.utility.general.Weighting;

/**
 *
 * @author THNghiep 
 * This class handles logic to compute feature vector of all papers.
 * Method: 
 * - Compute papers' full vector: linear, cosine, rpy.
 */
public class ComputePaperFV {

    // Prevent instantiation.
    private ComputePaperFV() {
    }

    /**
     * This method computes and set value for all papers' full feature vector
     * (after combining citation and reference papers).
     *
     * @param weightingScheme 0: linear; 1: cosine; 2: rpy
     */
    public static HashMap<String, Paper> computeAllPapersFeatureVector(HashMap<String, Paper> papersInput, int weightingScheme) throws Exception {
        // Reuse papersInput, not constructing new hash map.
        HashMap<String, Paper> papers = papersInput;
        
        for (String key : papersInput.keySet()) {
            papers.get(key).setFeatureVector(computePaperFeatureVector(papersInput, key, weightingScheme));
        }
        
        return papers;
    }

    /**
     * This method compute final feature vector by combining citation and
     * reference.
     *
     * @param paperId
     * @param weightingScheme 0: linear; 1: cosine; 2: rpy
     * @return list represents feature vector.
     */
    public static HashMapVector computePaperFeatureVector(HashMap<String, Paper> papersInput, String paperId, int weightingScheme) throws Exception {
        HashMapVector featureVector;
        
        if (weightingScheme == 0) {
            featureVector = computePaperFeatureVectorWithLinear(papersInput, paperId);
        } else if (weightingScheme == 1) {
            featureVector = computePaperFeatureVectorWithCosine(papersInput, paperId);
        } else {
            featureVector = computePaperFeatureVectorWithRPY(papersInput, paperId);
        }
        
        return featureVector;
    }

    /**
     * This method compute Paper Feature Vector with linear weight
     *
     * @param paperId
     * @return featureVector
     */
    public static HashMapVector computePaperFeatureVectorWithLinear(HashMap<String, Paper> papersInput, String paperId) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        Paper paper = papersInput.get(paperId);//get paper has Id is paperId in ListofPapers
        
        featureVector.add(paper.getContent());//assign HashMapVector featureVector equal HashMapVector paper
        
        List<String> citation = paper.getCitation();//get list of citation paper
        featureVector.add(sumFeatureVectorWithLinear(papersInput, citation));//add featureVector with featureVector of citation papers

        List<String> reference = paper.getReference();//get list of reference paper
        featureVector.add(sumFeatureVectorWithLinear(papersInput, reference));//add featureVector with featureVector of reference papers
        
        return featureVector;
    }

    /**
     * This method compute Paper Feature Vector with cosine weight
     *
     * @param paperId
     * @return featureVector
     */
    public static HashMapVector computePaperFeatureVectorWithCosine(HashMap<String, Paper> papersInput, String paperId) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        Paper paper = papersInput.get(paperId);//get paper has paperId in ListofPapers
        
        featureVector.add(paper.getContent());//assign HashMapVector featureVector equal HashMapVector paper
        
        List<String> citation = paper.getCitation();//get list of citation paper
        featureVector.add(sumFeatureVectorWithCosine(papersInput, paper, citation));//add featureVector with featureVector of citation papers

        List<String> reference = paper.getReference();//get list of reference paper
        featureVector.add(sumFeatureVectorWithCosine(papersInput, paper, reference));//add featureVector with featureVector of reference papers
        
        return featureVector;
    }

    /**
     * This method compute Paper Feature Vector with RPY weight
     *
     * @param paperId
     * @return featureVector
     */
    public static HashMapVector computePaperFeatureVectorWithRPY(HashMap<String, Paper> papersInput, String paperId) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        Paper paper = papersInput.get(paperId);//assign HashMapVector featureVector equal HashMapVector paper
        
        featureVector.add(paper.getContent());//get list of citation paper
        
        List<String> citation = paper.getCitation();//get list of citation paper
        featureVector.add(sumFeatureVectorWithRPY(papersInput, paper, citation));//add featureVector with featureVector of citation papers

        List<String> reference = paper.getReference();//get list of reference paper
        featureVector.add(sumFeatureVectorWithRPY(papersInput, paper, reference));//add featureVector with featureVector of reference papers
        
        return featureVector;
    }

    /**
     * This method compute sum of Papers(Citation or Reference Paper) with
     * linear weight
     *
     * @param paperIds
     * @return featureVector
     */
    public static HashMapVector sumFeatureVectorWithLinear(HashMap<String, Paper> papersInput, List<String> paperIds) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        for (String paperId : paperIds) {
            if (papersInput.containsKey(paperId)) {
                featureVector.add(papersInput.get(paperId).getContent());
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
    public static HashMapVector sumFeatureVectorWithCosine(HashMap<String, Paper> papersInput, Paper cpaper, List<String> paperIds) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        for (String paperId : paperIds) {
            if (papersInput.containsKey(paperId)) {
                double cosine = Weighting.computeCosine(cpaper.getContent(), papersInput.get(paperId).getContent());
                featureVector.addScaled(papersInput.get(paperId).getContent(), cosine);
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
    public static HashMapVector sumFeatureVectorWithRPY(HashMap<String, Paper> papersInput, Paper cpaper, List<String> paperIds) throws Exception {
        HashMapVector featureVector = new HashMapVector();
        
        for (String paperId : paperIds) {
            if (papersInput.containsKey(paperId)) {
                double rpy = Weighting.computeRPY(cpaper.getYear(), papersInput.get(paperId).getYear());
                featureVector.addScaled(papersInput.get(paperId).getContent(), rpy);
            }
        }
        
        return featureVector;
    }
}
