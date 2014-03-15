/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.utility;

import ir.vsr.*;
import java.util.ArrayList;

/**
 *
 * @author Vinh
 * Xây dựng vector profile cho junior researchers
 */
public class FormulaJuniorResearcher {
    
    ArrayList<PaperInformation> paper_junior = new ArrayList<PaperInformation>();
    ArrayList<PaperInformation> paper_ref = new ArrayList<PaperInformation>();
    HashMapVector hmvector_au = new HashMapVector();
    HashMapVector hmvector_ref = new HashMapVector();

    //Xây dựng vector profile cho junior researcher
    public HashMapVector putFormulaJunior(int i) throws Exception {
        paper_junior = GetDatabaseMysql.getDataInformationPaperAuthor(i);
        for (PaperInformation paper : paper_junior) {
            hmvector_au = GetDatabaseMysql.getKeywordAndWeight(paper.getIdPaper());
            paper_ref = GetDatabaseMysql.getDataInformationPaperRef(paper.getIdPaper());
            hmvector_ref=sumPaperReference(paper_ref);
        }
        hmvector_au.add(hmvector_ref);
        return hmvector_au;
    }

    //Tính tổng các vector đặc trưng cho bài báo references
    public HashMapVector sumPaperReference(ArrayList<PaperInformation> paper_ref) throws Exception {
        HashMapVector hmvector_ref_=new HashMapVector();
        for (PaperInformation paper : paper_ref) {
            hmvector_ref_.add(GetDatabaseMysql.getKeywordAndWeight(paper.getIdPaper()));
        }
        return hmvector_ref_;
    }
}
