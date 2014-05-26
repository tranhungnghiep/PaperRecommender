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
 * This class represents a paper.
 * Data: 
 * paper id, title, year, paper type,
 * content in keywords' tf-idf list (ir's hashmapvector: cần xem kĩ lại class này, dùng như thế nào cho đúng),
 * and the full feature vector of the paper (hashmapvector computed by combining other paper with weighting scheme linear or cosine or rpy).
 * - if the paper is paper to recommend: list<String> of citation, reference (paper id).
 * - if the paper is paper of author: List<Paper> of citation, reference (Paper object, this class).
 * - if the paper is citation or reference paper of author: no list of citation, reference.
 * Note: For a specific paper type, some data are absent.
 */
public class Paper implements Serializable {
    private String paperId;
    private String title;
    private String paperAbstract;
    private int year;
    private String paperType;
    private HashMapVector content;
    private List citation; // cited by those papers.
    private List reference; // citing those papers.
    private HashMapVector featureVector;

    /**
     * Default constructor used for serializable.
     */
    public Paper() {
        this.paperId = null;
        this.title = null;
        this.paperAbstract = null;
        this.year = 0;
        this.paperType = null;
        this.content = null;
        this.citation = new ArrayList();
        this.reference = new ArrayList();
        this.featureVector = null;
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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @return the content
     */
    public HashMapVector getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(HashMapVector content) {
        this.content = content;
    }

    /**
     * @return the citation
     */
    public List getCitation() {
        return citation;
    }

    /**
     * @param citation the citation to set
     */
    public void setCitation(List citation) {
        this.citation = citation;
    }

    /**
     * @return the reference
     */
    public List getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(List reference) {
        this.reference = reference;
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
}
