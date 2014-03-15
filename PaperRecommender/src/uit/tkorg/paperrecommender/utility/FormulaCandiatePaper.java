/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.utility;

import ir.vsr.HashMapVector;
import java.util.ArrayList;

/**
 *
 * @author Vinh
 * Xây dựng feature đặc trưng cho từng candidate paper
 */
public class FormulaCandiatePaper {

    ArrayList<PaperInformation> paper_candidate = new ArrayList<PaperInformation>();
    ArrayList<HashMapVector> array_vector = new ArrayList<HashMapVector>();

    //Xây dựng danh sách vector đặc trưng cho các bài báo ứng viên
    public ArrayList<HashMapVector> papercandidate() throws Exception {
        ArrayList<PaperInformation> paper_ref = new ArrayList<PaperInformation>();
        ArrayList<PaperInformation> paper_cit = new ArrayList<PaperInformation>();
        paper_candidate = GetDatabaseMysql.getDataInformationPaperRec();
        for (PaperInformation paper : paper_candidate) {
            HashMapVector hmvector_rec = new HashMapVector();
            hmvector_rec.add(GetDatabaseMysql.getKeywordAndWeight(paper.getIdPaper()));
            paper_ref=GetDatabaseMysql.getDataInformationPaperRef(paper.getIdPaper());
            paper_cit=GetDatabaseMysql.getDataInformationPaperCit(paper.getIdPaper());
            hmvector_rec.add(sumPaperReference(paper_ref));
            hmvector_rec.add(sumPaperCitation(paper_cit));
            array_vector.add(hmvector_rec);
        }
        return array_vector;
    }

    //Tính tổng các vector đặc trưng cho bài báo references
    public HashMapVector sumPaperReference(ArrayList<PaperInformation> paper_ref) throws Exception {
        HashMapVector hmvector_ref_ = new HashMapVector();
        for (PaperInformation paper : paper_ref) {
            hmvector_ref_.add(GetDatabaseMysql.getKeywordAndWeight(paper.getIdPaper()));
        }
        return hmvector_ref_;
    }

    //Tính tổng các vector đặc trưng cho bài báo citations
    public HashMapVector sumPaperCitation(ArrayList<PaperInformation> paper_cit) throws Exception {
        HashMapVector hmvector_cit_ = new HashMapVector();
        for (PaperInformation paper : paper_cit) {
            hmvector_cit_.add(GetDatabaseMysql.getKeywordAndWeight(paper.getIdPaper()));
        }
        return hmvector_cit_;
    }
}
