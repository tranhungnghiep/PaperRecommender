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
 * Data: paper id, title, content in keywords' tf-idf list (ir's hashmapvector: cần xem kĩ lại class này, dùng như thế nào cho đúng), list of citation (paper id), list of reference (paper id).
 * Method: get tf-idf vector by comparing keyword list and vocabulary, if needed.
 */
public class Paper {
    String paperId;
    String title;
    HashMapVector content;
    List<String> citation;
    List<String> reference;
}
