/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.controller.recommendation;

import ir.vsr.HashMapVector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import uit.tkorg.paperrecommender.model.Author;
import uit.tkorg.paperrecommender.model.Paper;
import uit.tkorg.paperrecommender.utility.FlatFileData.ImportDataset1;
import uit.tkorg.paperrecommender.utility.Weighting;

/**
 * This class handles all logics for author object. Data: List of author: junior
 * and senior. Method: - Build list authors. - Compute author feature vector.
 *
 * @author THNghiep
 */
public class AuthorLogic {

    // Key of this hash map is paper id.
    // Value of this hash map is the relevant paper object.
    HashMap<String, Author> authors = null;

    //List feature vectors of all authors
    HashMap<String, HashMapVector> allfeaturevectors = new HashMap<String, HashMapVector>();

    public void buildListOfAuthors() throws IOException {
        authors = ImportDataset1.buildListOfAuthors();
    }

    /**
     * This method computes and set value for all authors' full feature vector
     * (after combining citation and reference papers).
     *
     * @param authorId
     * @param weightScheme 0: linear; 1: cosine; 2: rpy
     */
    public void computeAllPapersFeatureVector(String authorId, int weightScheme) {
        for (String entry : authors.keySet()) {
            allfeaturevectors.put(entry, computeAuthorFeatureVector(authors.get(entry).getAuthorId(), weightScheme));
        }
    }

    /**
     * This method compute final feature vector by combining citation and
     * reference.
     *
     * @param authorId
     * @param weightingScheme 0: linear; 1: cosine; 2: rpy
     * @return list represents feature vector.
     */
    public HashMapVector computeAuthorFeatureVector(String authorId, int weightingScheme) {
        HashMapVector featureVector = null;
        if (weightingScheme == 0) {
            if (authorId.contains("y")) {
                computeJuniorFeatureVectorWithLinear(authorId);
            } else {
                computeSeniorFeatureVectorWithLinear(authorId);
            }
        } else if (weightingScheme == 1) {
            if (authorId.contains("y")) {
                computeJuniorFeatureVectorWithCosine(authorId);
            } else {
                computeSeniorFeatureVectorWithCosine(authorId);
            }
        } else {
            if (authorId.contains("y")) {
                computeJuniorFeatureVectorWithRPY(authorId);
            } else {
                computeSeniorFeatureVectorWithRPY(authorId);
            }
        }
        return featureVector;
    }
    //==================================================================================================================================================
    /**
     * This method compute Junior Feature Vector with linear weight
     *
     * @param authorId
     * @return featureVector
     */
    public HashMapVector computeJuniorFeatureVectorWithLinear(String authorId) {
        HashMapVector featureVector = new HashMapVector();
        Paper authorpaper = new Paper();
        Author author = authors.get(authorId);//get author has Id equally authorId in ListofPapers
        authorpaper = (Paper) author.getPaper().get(0);//get paper of junior researchers 
        featureVector = authorpaper.getContent();
        List reference = authorpaper.getReference();//get list of reference papers of author's paper 
        featureVector.add(sumFeatureVectorWithLinear(reference));//add featureVector with featureVector of reference papers of author's paper 
        return featureVector;
    }

    /**
     * This method compute Junior Feature Vector with cosine weight
     *
     * @param authorId
     * @return featureVector
     */
    public HashMapVector computeJuniorFeatureVectorWithCosine(String authorId) {
        HashMapVector featureVector = new HashMapVector();
        Paper authorpaper = new Paper();
        Author author = authors.get(authorId);//get author has Id equally authorId in ListofPapers
        authorpaper = (Paper) author.getPaper().get(0);//get paper of junior researchers 
        featureVector = authorpaper.getContent();
        List reference = authorpaper.getReference();//get list of reference papers of author's paper 
        featureVector.add(sumFeatureVectorWithCosine(authorpaper, reference));//add featureVector with featureVector of reference papers of author's paper 
        return featureVector;
    }

    /**
     * This method compute Junior Feature Vector with RPY weight
     *
     * @param authorId
     * @return featureVector
     */
    public HashMapVector computeJuniorFeatureVectorWithRPY(String authorId) {
        HashMapVector featureVector = new HashMapVector();
        Paper authorpaper = new Paper();
        Author author = authors.get(authorId);//get author has Id equally authorId in ListofPapers
        authorpaper = (Paper) author.getPaper().get(0);//get paper of junior researchers 
        featureVector = authorpaper.getContent();
        List reference = authorpaper.getReference();//get list of reference papers of author's paper 
        featureVector.add(sumFeatureVectorWithRPY(authorpaper, reference));//add featureVector with featureVector of reference papers of author's paper 
        return featureVector;
    }
    //======================================================================================================================================================

    /**
     * This method compute Junior Feature Vector with linear weight
     *
     * @param authorId
     * @return featureVector
     */
    public HashMapVector computeSeniorFeatureVectorWithLinear(String authorId) {
        HashMapVector featureVector = new HashMapVector();
        List<Paper> authorpapers = new ArrayList();
        Author author = authors.get(authorId);//get author has Id equally authorId in ListofPapers
        authorpapers = author.getPaper();//get list of papers of senior researchers 
        for (Paper paper : authorpapers) {
            featureVector.add(paper.getContent());
            List citation = paper.getReference();//get list of citation papers of author's paper 
            featureVector.add(sumFeatureVectorWithLinear(citation));//add featureVector with featureVector of citation papers of author's paper 
            List reference = paper.getReference();//get list of reference papers of author's paper 
            featureVector.add(sumFeatureVectorWithLinear(reference));//add featureVector with featureVector of reference papers of author's paper 
        }
        return featureVector;
    }

    /**
     * This method compute Junior Feature Vector with cosine weight
     *
     * @param authorId
     * @return featureVector
     */
    public HashMapVector computeSeniorFeatureVectorWithCosine(String authorId) {
        HashMapVector featureVector = new HashMapVector();
        List<Paper> authorpapers = new ArrayList();
        Author author = authors.get(authorId);//get author has Id equally authorId in ListofPapers
        authorpapers = author.getPaper();//get list of papers of senior researchers 
        for (Paper paper : authorpapers) {
            featureVector.add(paper.getContent());
            List citation = paper.getReference();//get list of citation papers of author's paper 
            featureVector.add(sumFeatureVectorWithCosine(paper, citation));//add featureVector with featureVector of citation papers of author's paper 
            List reference = paper.getReference();//get list of reference papers of author's paper 
            featureVector.add(sumFeatureVectorWithCosine(paper, reference));//add featureVector with featureVector of reference papers of author's paper 
        }
        return featureVector;
    }

    /**
     * This method compute Junior Feature Vector with RPY weight
     *
     * @param authorId
     * @return featureVector
     */
    public HashMapVector computeSeniorFeatureVectorWithRPY(String authorId) {
        HashMapVector featureVector = new HashMapVector();
        List<Paper> authorpapers = new ArrayList();
        Author author = authors.get(authorId);//get author has Id equally authorId in ListofPapers
        authorpapers = author.getPaper();//get list of papers of senior researchers 
        for (Paper paper : authorpapers) {
            featureVector.add(paper.getContent());
            List citation = paper.getReference();//get list of citation papers of author's paper 
            featureVector.add(sumFeatureVectorWithRPY(paper, citation));//add featureVector with featureVector of citation papers of author's paper 
            List reference = paper.getReference();//get list of reference papers of author's paper 
            featureVector.add(sumFeatureVectorWithRPY(paper, reference));//add featureVector with featureVector of reference papers of author's paper 
        }
        return featureVector;
    }
    //==================================================================================================================================================
    /**
     * This method compute sum of Papers(Citation or Reference Paper) with
     * linear weight
     *
     * @param papers
     * @return featureVector
     */
    public HashMapVector sumFeatureVectorWithLinear(List<Paper> papers) {
        HashMapVector featureVector = new HashMapVector();
        for (Paper paper : papers) {
            featureVector.add(paper.getContent());
        }
        return featureVector;
    }

    /**
     * This method compute sum of Papers(Citation or Reference Paper) with
     * cosine weight
     *
     * @param cpaper
     * @param papers
     * @return featureVector
     */
    public HashMapVector sumFeatureVectorWithCosine(Paper cpaper, List<Paper> papers) {
        HashMapVector featureVector = new HashMapVector();
        for (Paper paper : papers) {
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
     * @param papers
     * @return featureVector
     */
    public HashMapVector sumFeatureVectorWithRPY(Paper cpaper, List<Paper> papers) {
        HashMapVector featureVector = new HashMapVector();
        for (Paper paper : papers) {
            double rpy = Weighting.computeRPY(cpaper.getYear(), paper.getYear());
            featureVector.addScaled(paper.getContent(), rpy);
        }
        return featureVector;
    }
}
