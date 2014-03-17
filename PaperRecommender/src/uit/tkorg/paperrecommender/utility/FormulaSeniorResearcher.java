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
 * @author Vinh Xây dựng vector profile cho senior researchers
 */
public class FormulaSeniorResearcher {

    ArrayList<PaperInformation> paper_senior = new ArrayList<PaperInformation>();
    HashMapVector hmvector_au = new HashMapVector();
    GetDatabaseMysql getdatabase=new GetDatabaseMysql();

    //Xây dựng vector profile cho senior
    public HashMapVector putFormulaSenior(String str) throws Exception {
        paper_senior = getdatabase.getDataInformationPaperAuthor(str);
        for (PaperInformation paper : paper_senior) {
            hmvector_au.add(builFeatureVectorPaper(paper.getIdPaper()));
        }
        return hmvector_au;
    }
//Xây dựng vector đặc trưng riêng cho mỗi bài báo

    public HashMapVector builFeatureVectorPaper(String str) throws Exception {
        ArrayList<PaperInformation> paper_ref = new ArrayList<PaperInformation>();
        ArrayList<PaperInformation> paper_cit = new ArrayList<PaperInformation>();
        HashMapVector hmvector_paper = new HashMapVector();
        hmvector_paper = getdatabase.getKeywordAndWeight(str);
        paper_ref = getdatabase.getDataInformationPaperRef(str);
        paper_cit = getdatabase.getDataInformationPaperCit(str);
        hmvector_paper.add(sumPaperReference(paper_ref));
        hmvector_paper.add(sumPaperCitation(paper_cit));
        return hmvector_paper;
    }

    //===========================================================================
    //Tính tổng các vector đặc trưng cho bài báo references
    public HashMapVector sumPaperReference(ArrayList<PaperInformation> paper_ref) throws Exception {
        HashMapVector hmvector_ref_ = new HashMapVector();
        for (PaperInformation paper : paper_ref) {
            hmvector_ref_.add(getdatabase.getKeywordAndWeight(paper.getIdPaper()));
        }
        return hmvector_ref_;
    }

    //Tính tổng các vector đặc trưng cho bài báo citations
    public HashMapVector sumPaperCitation(ArrayList<PaperInformation> paper_cit) throws Exception {
        HashMapVector hmvector_cit_ = new HashMapVector();
        for (PaperInformation paper : paper_cit) {
            hmvector_cit_.add(getdatabase.getKeywordAndWeight(paper.getIdPaper()));
        }
        return hmvector_cit_;
    }

    //================================================================================
    //Xây dựng vector đặc trưng riêng cho mỗi bài báo
    public HashMapVector builFeatureVectorPaperSim(String str) throws Exception {
        ArrayList<PaperInformation> paper_ref = new ArrayList<PaperInformation>();
        ArrayList<PaperInformation> paper_cit = new ArrayList<PaperInformation>();
        HashMapVector hmvector_paper = new HashMapVector();
        hmvector_paper = getdatabase.getKeywordAndWeight(str);
        paper_ref = getdatabase.getDataInformationPaperRef(str);
        paper_cit = getdatabase.getDataInformationPaperCit(str);
        hmvector_paper.add(sumPaperReferenceSim(paper_ref, hmvector_paper));
        hmvector_paper.add(sumPaperCitationSim(paper_cit, hmvector_paper));
        return hmvector_paper;
    }
    
    //Tính tổng các vector đặc trưng cho bài báo references
    public HashMapVector sumPaperReferenceSim(ArrayList<PaperInformation> paper_ref, HashMapVector hmvector) throws Exception {
        HashMapVector hmvector_ref_ = new HashMapVector();
        for (PaperInformation paper : paper_ref) {
            HashMapVector hmvector_tempp = new HashMapVector();
            hmvector_tempp = getdatabase.getKeywordAndWeight(paper.getIdPaper());
            hmvector_ref_.addScaled(hmvector_tempp, CalculatorFeatureVector.cosine2vector(hmvector, hmvector_tempp));
        }
        return hmvector_ref_;
    }

    //Tính tổng các vector đặc trưng cho bài báo citations
    public HashMapVector sumPaperCitationSim(ArrayList<PaperInformation> paper_cit, HashMapVector hmvector) throws Exception {
        HashMapVector hmvector_cit_ = new HashMapVector();
        for (PaperInformation paper : paper_cit) {
            HashMapVector hmvector_tempp = new HashMapVector();
            hmvector_tempp = getdatabase.getKeywordAndWeight(paper.getIdPaper());
            hmvector_cit_.addScaled(hmvector_tempp, CalculatorFeatureVector.cosine2vector(hmvector, hmvector_tempp));
        }
        return hmvector_cit_;
    }
}
