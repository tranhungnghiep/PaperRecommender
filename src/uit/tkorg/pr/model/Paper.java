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
 This class represents a paper.
 Data: 
 paper id, paperTitle, year, paper type,
 tfidfVector in keywords' tf-idf list (ir's hashmapvector: cần xem kĩ lại class này, dùng như thế nào cho đúng),
 and the full feature vector of the paper (hashmapvector computed by combining other paper with weighting scheme linear or cosine or rpy).
 - if the paper is paper to recommend: list<String> of citationList, referenceList (paper id).
 * - if the paper is paper of author: List<Paper> of citationList, referenceList (Paper object, this class).
 * - if the paper is citationList or referenceList paper of author: no list of citationList, referenceList.
 * Note: For a specific paper type, some data are absent.
 */
public class Paper implements Serializable {
    private String paperId;
    private String paperTitle;
    private String paperAbstract;
    private int year;
    private String paperType;
    private HashMapVector tfidfVector;
    private List citationList; // cited by those papers.
    private List referenceList; // citing those papers.
    private HashMapVector featureVector;
    private Float qualityValue;

    /**
     * Default constructor used for serializable.
     */
    public Paper() {
        this.paperId = null;
        this.paperTitle = null;
        this.paperAbstract = null;
        this.year = 0;
        this.paperType = null;
        this.tfidfVector = new HashMapVector();
        this.citationList = new ArrayList();
        this.referenceList = new ArrayList();
        this.featureVector = new HashMapVector();
        this.qualityValue = 0f;
    }

    public Float getTemporalCitationTrendValue() {
        int present = 2005;
        if (year == 0) {
            return 0f;
        } else {
            int deltaTime = present - year;
            if (deltaTime == 0) {
                return 0.2f;
            } else if (deltaTime == 1) {
                return 0.3f;
            } else if (deltaTime == 2) {
                return 0.5f;
            } else if (deltaTime == 3) {
                return 0.7f;
            } else if (deltaTime == 4) {
                return 0.9f;
            } else if (deltaTime == 5) {
                return 0.9f;
            } else if (deltaTime == 6) {
                return 0.7f;
            } else if (deltaTime == 7) {
                return 0.5f;
            } else if (deltaTime == 8) {
                return 0.3f;
            } else if (deltaTime == 9) {
                return 0.2f;
            } else if (deltaTime > 9) {
                return 0.1f;
            }
        }
        
        return 0f;
    }
    
    /**
     * @return the paperId
     */
    public String getPaperId() {
        return paperId;
    }

    /**
     * @param paperId the paperId to set
     */
    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    /**
     * @return the paperTitle
     */
    public String getPaperTitle() {
        return paperTitle;
    }

    /**
     * @param paperTitle the paperTitle to set
     */
    public void setPaperTitle(String paperTitle) {
        this.paperTitle = paperTitle;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return the tfidfVector
     */
    public HashMapVector getTfidfVector() {
        if (tfidfVector == null) {
            return new HashMapVector();
        }
        return tfidfVector;
    }

    /**
     * @param tfidfVector the tfidfVector to set
     */
    public void setTfidfVector(HashMapVector tfidfVector) {
        this.tfidfVector = tfidfVector;
    }

    /**
     * @return the citationList
     */
    public List getCitationList() {
        return citationList;
    }

    /**
     * @param citationList the citationList to set
     */
    public void setCitationList(List citationList) {
        this.citationList = citationList;
    }

    /**
     * @return the referenceList
     */
    public List getReferenceList() {
        return referenceList;
    }

    /**
     * @param referenceList the referenceList to set
     */
    public void setReferenceList(List referenceList) {
        this.referenceList = referenceList;
    }

    /**
     * @return the paperType
     */
    public String getPaperType() {
        return paperType;
    }

    /**
     * @param paperType the paperType to set
     */
    public void setPaperType(String paperType) {
        this.paperType = paperType;
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
     * @return the paperAbstract
     */
    public String getPaperAbstract() {
        return paperAbstract;
    }

    /**
     * @param paperAbstract the paperAbstract to set
     */
    public void setPaperAbstract(String paperAbstract) {
        this.paperAbstract = paperAbstract;
    }

    /**
     * @return the qualityValue
     */
    public Float getQualityValue() {
        return qualityValue;
    }

    /**
     * @param qualityValue the qualityValue to set
     */
    public void setQualityValue(Float qualityValue) {
        this.qualityValue = qualityValue;
    }
}
