/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.model;

import ir.vsr.HashMapVector;
import java.util.List;

/**
 *
 * @author THNghiep
 * This class represents a paper.
 * Data: paper id, title, year, content in keywords' tf-idf list (ir's hashmapvector: cần xem kĩ lại class này, dùng như thế nào cho đúng), list of citation (paper id), list of reference (paper id).
 */
public class Paper {
    private String paperId;
    private String title;
    private int year;
    private HashMapVector content;
    private List<String> citation;
    private List<String> reference;

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
    public List<String> getCitation() {
        return citation;
    }

    /**
     * @param citation the citation to set
     */
    public void setCitation(List<String> citation) {
        this.citation = citation;
    }

    /**
     * @return the reference
     */
    public List<String> getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(List<String> reference) {
        this.reference = reference;
    }
}
