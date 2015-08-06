/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.model;

import ir.vsr.HashMapVector;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author THNghiep
 * This class represents the author in general.
 * Data: author id, author type, list of ground-truth papers to recommend (List<String> of paperList ids), 
 * list of papers of author (List<Paper>), each paperList in list of papers of author has list of citation and reference papers.
 */
public class Author implements Serializable {
    private String authorId;
    private String authorName;
    private String authorType;
    private List paperList;
    private HashMapVector featureVector;
    private List groundTruth; // List string.
    private List recommendationList; // List string.
    private LinkedHashMap<String, Float> recommendationValue;
    private double precision10;
    private double precision20;
    private double precision30;
    private double precision40;
    private double precision50;
    private double recall50;
    private double recall100;
    private double f1;
    private double ap10;
    private double ap20;
    private double ap30;
    private double ap40;
    private double ap50;
    private double ndcg5;
    private double ndcg10;
    private double rr;
    private HashMap<String, Float> cbfSimHM;
    private HashMap<String, Float> cfRatingHM;
    private HashMap<String, Float> cbfCfHybridHM;
    private HashMap<String, Float> cbfCfHybridV2HM;
    private HashMap<String, Float> cbfCfHybridV3HM;
    private HashMap<String, Float> coAuthorRSSHM;
    private HashMap<String, Float> citationAuthorRSSHM;
    private HashMap<String, Float> trustedAuthorHM;
    private HashMap<String, Float> trustedPaperHM;
    private HashMap<String, Float> cbfTrustHybridHM;
    private HashMap<String, Float> cbfTrustHybridV2HM;
    private HashMap<String, Float> cbfTrustHybridV3HM;
    private HashMap<String, Float> finalRecommendingScoreHM;

    /**
     * Default constructor used for serializable.
     */
    public Author() {
        this.authorId = null;
        this.authorName = null;
        this.authorType = null;
        this.paperList = new ArrayList();
        this.featureVector = new HashMapVector();
        this.groundTruth = new ArrayList();
        this.recommendationList = new ArrayList();
        this.recommendationValue = new LinkedHashMap<>();
        this.precision10 = 0;
        this.precision20 = 0;
        this.precision30 = 0;
        this.precision40 = 0;
        this.precision50 = 0;
        this.recall50 = 0;
        this.recall100 = 0;
        this.f1 = 0;
        this.ap10 = 0;
        this.ap20 = 0;
        this.ap30 = 0;
        this.ap40 = 0;
        this.ap50 = 0;
        this.ndcg5 = 0;
        this.ndcg10 = 0;
        this.rr = 0;
        this.cbfSimHM = new HashMap<>();
        this.cfRatingHM = new HashMap<>();
        this.cbfCfHybridHM = new HashMap<>();
        this.cbfCfHybridV2HM = new HashMap<>();
        this.cbfCfHybridV3HM = new HashMap<>();
        this.coAuthorRSSHM = new HashMap<>();
        this.citationAuthorRSSHM = new HashMap<>();
        this.trustedAuthorHM = new HashMap<>();
        this.trustedPaperHM = new HashMap<>();
        this.cbfTrustHybridHM = new HashMap<>();
        this.cbfTrustHybridV2HM = new HashMap<>();
        this.cbfTrustHybridV3HM = new HashMap<>();
        this.finalRecommendingScoreHM = new HashMap<>();
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
     * @return the paperList
     */
    public List getPaperList() {
        return paperList;
    }

    /**
     * @param paperList the paperList to set
     */
    public void setPaperList(List paperList) {
        this.paperList = paperList;
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
     * @return the recommendationList
     */
    public List getRecommendationList() {
        return recommendationList;
    }

    /**
     * @param recommendationList the recommendationList to set
     */
    public void setRecommendationList(List RecommendationList) {
        this.recommendationList = RecommendationList;
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
     * @return the precision10
     */
    public double getPrecision10() {
        return precision10;
    }

    /**
     * @param precision10 the precision10 to set
     */
    public void setPrecision10(double precision10) {
        this.precision10 = precision10;
    }

    /**
     * @return the recall50
     */
    public double getRecall50() {
        return recall50;
    }

    /**
     * @param recall50 the recall50 to set
     */
    public void setRecall50(double recall50) {
        this.recall50 = recall50;
    }

    /**
     * @return the ap10
     */
    public double getAp10() {
        return ap10;
    }

    /**
     * @param ap10 the ap10 to set
     */
    public void setAp10(double ap10) {
        this.ap10 = ap10;
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

    /**
     * @return the recommendationValue
     */
    public LinkedHashMap<String, Float> getRecommendationValue() {
        return recommendationValue;
    }

    /**
     * @param recommendationValue the recommendationValue to set
     */
    public void setRecommendationValue(LinkedHashMap<String, Float> recommendationValue) {
        this.recommendationValue = recommendationValue;
    }

    /**
     * @return the precision20
     */
    public double getPrecision20() {
        return precision20;
    }

    /**
     * @param precision20 the precision20 to set
     */
    public void setPrecision20(double precision20) {
        this.precision20 = precision20;
    }

    /**
     * @return the precision30
     */
    public double getPrecision30() {
        return precision30;
    }

    /**
     * @param precision30 the precision30 to set
     */
    public void setPrecision30(double precision30) {
        this.precision30 = precision30;
    }

    /**
     * @return the precision40
     */
    public double getPrecision40() {
        return precision40;
    }

    /**
     * @param precision40 the precision40 to set
     */
    public void setPrecision40(double precision40) {
        this.precision40 = precision40;
    }

    /**
     * @return the precision50
     */
    public double getPrecision50() {
        return precision50;
    }

    /**
     * @param precision50 the precision50 to set
     */
    public void setPrecision50(double precision50) {
        this.precision50 = precision50;
    }

    /**
     * @return the recall100
     */
    public double getRecall100() {
        return recall100;
    }

    /**
     * @param recall100 the recall100 to set
     */
    public void setRecall100(double recall100) {
        this.recall100 = recall100;
    }

    /**
     * @return the ap20
     */
    public double getAp20() {
        return ap20;
    }

    /**
     * @param ap20 the ap20 to set
     */
    public void setAp20(double ap20) {
        this.ap20 = ap20;
    }

    /**
     * @return the ap30
     */
    public double getAp30() {
        return ap30;
    }

    /**
     * @param ap30 the ap30 to set
     */
    public void setAp30(double ap30) {
        this.ap30 = ap30;
    }

    /**
     * @return the ap40
     */
    public double getAp40() {
        return ap40;
    }

    /**
     * @param ap40 the ap40 to set
     */
    public void setAp40(double ap40) {
        this.ap40 = ap40;
    }

    /**
     * @return the ap50
     */
    public double getAp50() {
        return ap50;
    }

    /**
     * @param ap50 the ap50 to set
     */
    public void setAp50(double ap50) {
        this.ap50 = ap50;
    }

    /**
     * @return the cbfSimHM
     */
    public HashMap<String, Float> getCbfSimHM() {
        return cbfSimHM;
    }

    /**
     * @param cbfSimHM the cbfSimHM to set
     */
    public void setCbfSimHM(HashMap<String, Float> cbfSimHM) {
        this.cbfSimHM = cbfSimHM;
    }

    /**
     * @return the cfRatingHM
     */
    public HashMap<String, Float> getCfRatingHM() {
        return cfRatingHM;
    }

    /**
     * @param cfRatingHM the cfRatingHM to set
     */
    public void setCfRatingHM(HashMap<String, Float> cfRatingHM) {
        this.cfRatingHM = cfRatingHM;
    }

    /**
     * @return the cbfCfHybridHM
     */
    public HashMap<String, Float> getCbfCfHybridHM() {
        return cbfCfHybridHM;
    }

    /**
     * @param cbfCfHybridHM the cbfCfHybridHM to set
     */
    public void setCbfCfHybridHM(HashMap<String, Float> cbfCfHybridHM) {
        this.cbfCfHybridHM = cbfCfHybridHM;
    }

    /**
     * @return the coAuthorRSSHM
     */
    public HashMap<String, Float> getCoAuthorRSSHM() {
        return coAuthorRSSHM;
    }

    /**
     * @param coAuthorRSSHM the coAuthorRSSHM to set
     */
    public void setCoAuthorRSSHM(HashMap<String, Float> coAuthorRSSHM) {
        this.coAuthorRSSHM = coAuthorRSSHM;
    }

    /**
     * @return the citationAuthorRSSHM
     */
    public HashMap<String, Float> getCitationAuthorRSSHM() {
        return citationAuthorRSSHM;
    }

    /**
     * @param citationAuthorRSSHM the citationAuthorRSSHM to set
     */
    public void setCitationAuthorRSSHM(HashMap<String, Float> citationAuthorRSSHM) {
        this.citationAuthorRSSHM = citationAuthorRSSHM;
    }

    /**
     * @return the trustedAuthorHM
     */
    public HashMap<String, Float> getTrustedAuthorHM() {
        return trustedAuthorHM;
    }

    /**
     * @param trustedAuthorHM the trustedAuthorHM to set
     */
    public void setTrustedAuthorHM(HashMap<String, Float> trustedAuthorHM) {
        this.trustedAuthorHM = trustedAuthorHM;
    }

    /**
     * @return the trustedPaperHM
     */
    public HashMap<String, Float> getTrustedPaperHM() {
        return trustedPaperHM;
    }

    /**
     * @param trustedPaperHM the trustedPaperHM to set
     */
    public void setTrustedPaperHM(HashMap<String, Float> trustedPaperHM) {
        this.trustedPaperHM = trustedPaperHM;
    }

    /**
     * @return the finalRecommendingScoreHM
     */
    public HashMap<String, Float> getFinalRecommendingScoreHM() {
        return finalRecommendingScoreHM;
    }

    /**
     * @param finalRecommendingScoreHM the finalRecommendingScoreHM to set
     */
    public void setFinalRecommendingScoreHM(HashMap<String, Float> finalRecommendingScoreHM) {
        this.finalRecommendingScoreHM = finalRecommendingScoreHM;
    }

    /**
     * @return the cbfTrustHybridHM
     */
    public HashMap<String, Float> getCbfTrustHybridHM() {
        return cbfTrustHybridHM;
    }

    /**
     * @param cbfTrustHybridHM the cbfTrustHybridHM to set
     */
    public void setCbfTrustHybridHM(HashMap<String, Float> cbfTrustHybridHM) {
        this.cbfTrustHybridHM = cbfTrustHybridHM;
    }

    /**
     * @return the cbfTrustHybridV2HM
     */
    public HashMap<String, Float> getCbfTrustHybridV2HM() {
        return cbfTrustHybridV2HM;
    }

    /**
     * @param cbfTrustHybridV2HM the cbfTrustHybridV2HM to set
     */
    public void setCbfTrustHybridV2HM(HashMap<String, Float> cbfTrustHybridV2HM) {
        this.cbfTrustHybridV2HM = cbfTrustHybridV2HM;
    }

    /**
     * @return the cbfTrustHybridV3HM
     */
    public HashMap<String, Float> getCbfTrustHybridV3HM() {
        return cbfTrustHybridV3HM;
    }

    /**
     * @param cbfTrustHybridV3HM the cbfTrustHybridV3HM to set
     */
    public void setCbfTrustHybridV3HM(HashMap<String, Float> cbfTrustHybridV3HM) {
        this.cbfTrustHybridV3HM = cbfTrustHybridV3HM;
    }

    /**
     * @return the cbfCfHybridV2HM
     */
    public HashMap<String, Float> getCbfCfHybridV2HM() {
        return cbfCfHybridV2HM;
    }

    /**
     * @param cbfCfHybridV2HM the cbfCfHybridV2HM to set
     */
    public void setCbfCfHybridV2HM(HashMap<String, Float> cbfCfHybridV2HM) {
        this.cbfCfHybridV2HM = cbfCfHybridV2HM;
    }

    /**
     * @return the cbfCfHybridV3HM
     */
    public HashMap<String, Float> getCbfCfHybridV3HM() {
        return cbfCfHybridV3HM;
    }

    /**
     * @param cbfCfHybridV3HM the cbfCfHybridV3HM to set
     */
    public void setCbfCfHybridV3HM(HashMap<String, Float> cbfCfHybridV3HM) {
        this.cbfCfHybridV3HM = cbfCfHybridV3HM;
    }
}
