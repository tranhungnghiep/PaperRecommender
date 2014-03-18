/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obsolete.uit.tkorg.paperrecommender.utility;

import ir.vsr.*;
import java.util.ArrayList;

/**
 *
 * @author Vinh Xây dựng vector profile cho junior researchers
 */
public class FormulaJuniorResearcher {

    ArrayList<PaperInformation> paper_junior = new ArrayList<PaperInformation>();
    ArrayList<PaperInformation> paper_ref = new ArrayList<PaperInformation>();
    HashMapVector hmvector_au = new HashMapVector();
    HashMapVector hmvector_ref = new HashMapVector();
    GetDatabaseMysql getdatabase=new GetDatabaseMysql();

    //Xây dựng vector profile cho junior researcher
    public HashMapVector putFormulaJunior(String str) throws Exception {
        paper_junior = getdatabase.getDataInformationPaperAuthor(str);
        for (PaperInformation paper : paper_junior) {
            hmvector_au = getdatabase.getKeywordAndWeight(paper.getIdPaper());
            paper_ref = getdatabase.getDataInformationPaperRef(paper.getIdPaper());
            hmvector_ref = sumPaperReference(paper_ref);
        }
        hmvector_au.add(hmvector_ref);
        return hmvector_au;
    }

    //Xây dựng vector profile cho junior researcher
    public HashMapVector putFormulaJuniorSim(String str) throws Exception {
        paper_junior = getdatabase.getDataInformationPaperAuthor(str);
        for (PaperInformation paper : paper_junior) {
            hmvector_au = getdatabase.getKeywordAndWeight(paper.getIdPaper());
            paper_ref = getdatabase.getDataInformationPaperRef(paper.getIdPaper());
            hmvector_ref = sumPaperReferenceSim(paper_ref,hmvector_au);
        }
        hmvector_au.add(hmvector_ref);
        return hmvector_au;
    }

    //Tính tổng các vector đặc trưng cho bài báo references
    public HashMapVector sumPaperReference(ArrayList<PaperInformation> paper_ref) throws Exception {
        HashMapVector hmvector_ref_ = new HashMapVector();
        for (PaperInformation paper : paper_ref) {
            hmvector_ref_.add(getdatabase.getKeywordAndWeight(paper.getIdPaper()));
        }
        return hmvector_ref_;
    }

    //Tính tổng các vector đặc trưng cho bài báo references
    public HashMapVector sumPaperReferenceSim(ArrayList<PaperInformation> paper_ref, HashMapVector hmvectorauthor) throws Exception {
        HashMapVector hmvector_ref_ = new HashMapVector();
        for (PaperInformation paper : paper_ref) {
            HashMapVector hmvector_tempp=new HashMapVector();
            hmvector_tempp=getdatabase.getKeywordAndWeight(paper.getIdPaper());
            hmvector_ref_.addScaled(hmvector_tempp,CalculatorFeatureVector.cosine2vector(hmvectorauthor, hmvector_tempp));
        }
        return hmvector_ref_;
    }
}
