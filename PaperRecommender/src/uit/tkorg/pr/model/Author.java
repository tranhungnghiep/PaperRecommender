/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.model;

import ir.vsr.HashMapVector;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author THNghiep
 * This class represents the author in general.
 * Data: author id, author type, list of ground-truth papers to recommend (List<String> of paper ids), 
 * list of papers of author (List<Paper>), each paper in list of papers of author has list of citation and reference papers.
 */
public class Author implements Serializable {
    private String authorId;
    private String authorName;
    private String authorType;
    private List paper;
    private HashMapVector featureVector;
    private List groundTruth; // List string.
    private List RecommendationList; // List string.
    private double precision;
    private double recall;
    private double f1;
    private double map;
    private double rr;
    private double ndcg5;
    private double ndcg10;

    /**
     * Default constructor used for serializable.
     */
    public Author() {
        this.authorId = null;
        this.authorName = null;
        this.authorType = null;
        this.paper = new ArrayList();
        this.featureVector = new HashMapVector();
        this.RecommendationList = new ArrayList();
        this.groundTruth = new ArrayList();
        this.precision = 0;
        this.recall = 0;
        this.f1 = 0;
        this.map = 0;
        this.rr = 0;
        this.ndcg5 = 0;
        this.ndcg10 = 0;
    }

    /**
     * @return the authorId
     */
    public String getAuthorId() {
        return authorId;
    }

    /**
     * @param authorId the authorId to set
     */
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    /**
     * @return the authorType
     */
    public String getAuthorType() {
        return authorType;
    }

    /**
     * @param authorType the authorType to set
     */
    public void setAuthorType(String authorType) {
        this.authorType = authorType;
    }

    /**
     * @return the groundTruth
     */
    public List getGroundTruth() {
        return groundTruth;
    }

    /**
     * @param groundTruth the groundTruth to set
     */
    public void setGroundTruth(List groundTruth) {
        this.groundTruth = groundTruth;
    }

    /**
     * @return the paper
     */
    public List getPaper() {
        return paper;
    }

    /**
     * @param paper the paper to set
     */
    public void setPaper(List paper) {
        this.paper = paper;
    }

    /**
     * @return the featureVector
     */
    public HashMapVector getFeatureVector() {
        if (featureVector == null) {
            return new HashMapVector();
        }
        return featureVector;
    }

    /**
     * @param featureVector the featureVector to set
     */
    public void setFeatureVector(HashMapVector featureVector) {
        this.featureVector = featureVector;
    }

    /**
     * @return the RecommendationList
     */
    public List getRecommendationList() {
        return RecommendationList;
    }

    /**
     * @param RecommendationList the RecommendationList to set
     */
    public void setRecommendationList(List RecommendationList) {
        this.RecommendationList = RecommendationList;
    }

    /**
     * @return the ndcg5
     */
    public double getNdcg5() {
        return ndcg5;
    }

    /**
     * @param ndcg5 the ndcg5 to set
     */
    public void setNdcg5(double ndcg5) {
        this.ndcg5 = ndcg5;
    }

    /**
     * @return the ndcg10
     */
    public double getNdcg10() {
        return ndcg10;
    }

    /**
     * @param ndcg10 the ndcg10 to set
     */
    public void setNdcg10(double ndcg10) {
        this.ndcg10 = ndcg10;
    }

    /**
     * @return the rr
     */
    public double getRr() {
        return rr;
    }

    /**
     * @param rr the rr to set
     */
    public void setRr(double rr) {
        this.rr = rr;
    }

    /**
     * @return the authorName
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * @param authorName the authorName to set
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * @return the precision
     */
    public double getPrecision() {
        return precision;
    }

    /**
     * @param precision the precision to set
     */
    public void setPrecision(double precision) {
        this.precision = precision;
    }

    /**
     * @return the recall
     */
    public double getRecall() {
        return recall;
    }

    /**
     * @param recall the recall to set
     */
    public void setRecall(double recall) {
        this.recall = recall;
    }

    /**
     * @return the map
     */
    public double getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(double map) {
        this.map = map;
    }

    /**
     * @return the f1
     */
    public double getF1() {
        return f1;
    }

    /**
     * @param f1 the f1 to set
     */
    public void setF1(double f1) {
        this.f1 = f1;
    }
}
