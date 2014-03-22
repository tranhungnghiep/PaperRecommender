/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.controller.recommendation;

import ir.vsr.HashMapVector;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import uit.tkorg.paperrecommender.model.Paper;
import uit.tkorg.paperrecommender.utility.FlatFileData.ImportDataset1;
import uit.tkorg.paperrecommender.utility.Weighting;

/**
 *
 * @author THNghiep 
 * This class handles all logics for paper object. 
 * Method: 
 * - Generate list of papers (key: paper id, value: object paper). 
 * - Compute papers' full vector: linear, cosine, rpy.
 */
public class PaperLogic implements Serializable {

    // Key of this hash map is paper id.
    // Value of this hash map is the relevant paper object.
    private HashMap<String, Paper> papers;

    public PaperLogic(HashMap<String, Paper> papers) {
        this.papers = papers;
    }

    /**
     * @return the papers
     */
    public HashMap<String, Paper> getPapers() {
        return papers;
    }

    /**
     * @param papers the papers to set
     */
    public void setPapers(HashMap<String, Paper> papers) {
        this.papers = papers;
    }

    /**
     * This method builds a hashmap of papers.
     *
     * @throws java.io.IOException
     */
    public void buildListOfPapers() throws IOException {
        setPapers(ImportDataset1.buildListOfPapers());
    }

    /**
     * This method computes and set value for all papers' full feature vector
     * (after combining citation and reference papers).
     *
     * @param paperId
     * @param weightingScheme
     */
    public void computeAllPapersFeatureVector(int weightingScheme) {
        for (String key : getPapers().keySet()) {
            papers.get(key).setFeatureVector(computePaperFeatureVector(papers.get(key).getPaperId(), weightingScheme));
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
    public HashMapVector computePaperFeatureVector(String paperId, int weightingScheme) {
        HashMapVector featureVector = null;
        if (weightingScheme == 0) {
            featureVector = computePaperFeatureVectorWithLinear(paperId);
        } else if (weightingScheme == 1) {
            featureVector = computePaperFeatureVectorWithCosine(paperId);
        } else {
            featureVector = computePaperFeatureVectorWithRPY(paperId);
        }
        return featureVector;
    }

    /**
     * This method compute Paper Feature Vector with linear weight
     *
     * @param paperId
     * @return featureVector
     */
    public HashMapVector computePaperFeatureVectorWithLinear(String paperId) {
        HashMapVector featureVector = new HashMapVector();
        Paper paper = getPapers().get(paperId);//get paper has Id is paperId in ListofPapers
        featureVector = paper.getContent();//assign HashMapVector featureVector equal HashMapVector paper
        List<String> citation = paper.getCitation();//get list of citation paper
        List<String> reference = paper.getReference();//get list of reference paper
        featureVector.add(sumFeatureVectorWithLinear(citation));//add featureVector with featureVector of citation papers
        featureVector.add(sumFeatureVectorWithLinear(reference));//add featureVector with featureVector of reference papers
        return featureVector;
    }

    /**
     * This method compute Paper Feature Vector with cosine weight
     *
     * @param paperId
     * @return featureVector
     */
    public HashMapVector computePaperFeatureVectorWithCosine(String paperId) {
        HashMapVector featureVector = new HashMapVector();
        Paper paper = getPapers().get(paperId);//get paper has paperId in ListofPapers
        featureVector = paper.getContent();//assign HashMapVector featureVector equal HashMapVector paper
        List<String> citation = paper.getCitation();//get list of citation paper
        List<String> reference = paper.getReference();//get list of reference paper
        featureVector.add(sumFeatureVectorWithCosine(paper, citation));//add featureVector with featureVector of citation papers
        featureVector.add(sumFeatureVectorWithCosine(paper, reference));//add featureVector with featureVector of reference papers
        return featureVector;
    }

    /**
     * This method compute Paper Feature Vector with RPY weight
     *
     * @param paperId
     * @return featureVector
     */
    public HashMapVector computePaperFeatureVectorWithRPY(String paperId) {
        HashMapVector featureVector = new HashMapVector();
        featureVector = new HashMapVector();//get paper has paperId in ListofPapers
        Paper paper = getPapers().get(paperId);//assign HashMapVector featureVector equal HashMapVector paper
        featureVector = paper.getContent();//get list of citation paper
        List<String> citation = paper.getCitation();//get list of citation paper
        List<String> reference = paper.getReference();//get list of reference paper
        featureVector.add(sumFeatureVectorWithRPY(paper, citation));//add featureVector with featureVector of citation papers
        featureVector.add(sumFeatureVectorWithRPY(paper, reference));//add featureVector with featureVector of reference papers
        return featureVector;
    }

    /**
     * This method compute sum of Papers(Citation or Reference Paper) with
     * linear weight
     *
     * @param paperIds
     * @return featureVector
     */
    public HashMapVector sumFeatureVectorWithLinear(List<String> paperIds) {
        HashMapVector featureVector = new HashMapVector();
        for (String paperId : paperIds) {
            Paper paper = getPapers().get(paperId);
            featureVector.add(paper.getContent());
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
    public HashMapVector sumFeatureVectorWithCosine(Paper cpaper, List<String> paperIds) {
        HashMapVector featureVector = new HashMapVector();
        for (String paperId : paperIds) {
            Paper paper = getPapers().get(paperId);
            double cosine = Weighting.computeCosine(cpaper.getContent(), paper.getContent());
            featureVector.addScaled(paper.getContent(), cosine);
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
    public HashMapVector sumFeatureVectorWithRPY(Paper cpaper, List<String> paperIds) {
        HashMapVector featureVector = new HashMapVector();
        for (String paperId : paperIds) {
            Paper paper = getPapers().get(paperId);
            double rpy = Weighting.computeRPY(cpaper.getYear(), paper.getYear());
            featureVector.addScaled(paper.getContent(), rpy);
        }
        return featureVector;
    }
}
