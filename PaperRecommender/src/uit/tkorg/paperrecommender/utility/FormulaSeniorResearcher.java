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
 * Xây dựng vector profile cho senior researchers
 */
public class FormulaSeniorResearcher {

    ArrayList<PaperInformation> paper_senior = new ArrayList<PaperInformation>();
    HashMapVector hmvector_au = new HashMapVector();

    //Xây dựng vector profile cho senior
    public HashMapVector putFormulaJunior(int i) throws Exception {
        paper_senior=GetDatabaseMysql.getDataInformationPaperAuthor(i);
        for(PaperInformation paper:paper_senior)
        {
            hmvector_au.add(builFeatureVectorPaper(paper.getIdPaper()));
        }
        return hmvector_au;
    }

    //Xây dựng vector đặc trưng riêng cho mỗi bài báo
    public HashMapVector builFeatureVectorPaper(int i) throws Exception
    {
        ArrayList<PaperInformation> paper_ref = new ArrayList<PaperInformation>();
        ArrayList<PaperInformation> paper_cit = new ArrayList<PaperInformation>();
        HashMapVector hmvector_paper = new HashMapVector();
        hmvector_paper=GetDatabaseMysql.getKeywordAndWeight(i);
        paper_ref=GetDatabaseMysql.getDataInformationPaperRef(i);
        paper_cit=GetDatabaseMysql.getDataInformationPaperCit(i);
        hmvector_paper.add(sumPaperReference(paper_ref));
        hmvector_paper.add(sumPaperCitation(paper_cit));
        return hmvector_paper;
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
