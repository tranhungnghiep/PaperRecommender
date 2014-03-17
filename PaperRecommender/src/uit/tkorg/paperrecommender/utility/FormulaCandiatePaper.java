/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.utility;

import ir.vsr.HashMapVector;
import static java.lang.Math.abs;
import java.util.ArrayList;

/**
 *
 * @author Vinh Xây dựng feature đặc trưng cho từng candidate paper
 */
public class FormulaCandiatePaper {

    ArrayList<PaperInformation> paper_candidate = new ArrayList<PaperInformation>();
    ArrayList<HashMapVector> array_vector = new ArrayList<HashMapVector>();
    GetDatabaseMysql getdatabase=new GetDatabaseMysql();

    //Xây dựng danh sách vector đặc trưng cho các bài báo ứng viên
    public ArrayList<HashMapVector> papercandidate() throws Exception {
        ArrayList<PaperInformation> paper_ref = new ArrayList<PaperInformation>();
        ArrayList<PaperInformation> paper_cit = new ArrayList<PaperInformation>();
        paper_candidate = getdatabase.getDataInformationPaperRec();
        for (PaperInformation paper : paper_candidate) {
            HashMapVector hmvector_rec = new HashMapVector();
            hmvector_rec.add(getdatabase.getKeywordAndWeight(paper.getIdPaper()));
            getdatabase.getKeywordAndWeight(paper.getIdPaper()).print();
            paper_ref = getdatabase.getDataInformationPaperRef(paper.getIdPaper());
            paper_cit = getdatabase.getDataInformationPaperCit(paper.getIdPaper());
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
    //======================================================================================
    //Xây dựng danh sách vector đặc trưng cho các bài báo ứng viên
    public ArrayList<HashMapVector> papercandidatesim() throws Exception {
        ArrayList<PaperInformation> paper_ref = new ArrayList<PaperInformation>();
        ArrayList<PaperInformation> paper_cit = new ArrayList<PaperInformation>();
        paper_candidate = getdatabase.getDataInformationPaperRec();
        for (PaperInformation paper : paper_candidate) {
            HashMapVector hmvector_rec = new HashMapVector();
            hmvector_rec.add(getdatabase.getKeywordAndWeight(paper.getIdPaper()));
            paper_ref = getdatabase.getDataInformationPaperRef(paper.getIdPaper());
            paper_cit = getdatabase.getDataInformationPaperCit(paper.getIdPaper());
            hmvector_rec.add(sumPaperReferenceSim(paper_ref, hmvector_rec));
            hmvector_rec.add(sumPaperCitationSim(paper_cit, hmvector_rec));
            array_vector.add(hmvector_rec);
        }
        return array_vector;
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
    //=============================================================================================
    //Xây dựng danh sách vector đặc trưng cho các bài báo ứng viên
    public ArrayList<HashMapVector> papercandidaterpy() throws Exception {
        ArrayList<PaperInformation> paper_ref = new ArrayList<PaperInformation>();
        ArrayList<PaperInformation> paper_cit = new ArrayList<PaperInformation>();
        paper_candidate = getdatabase.getDataInformationPaperRec();
        for (PaperInformation paper : paper_candidate) {
            HashMapVector hmvector_rec = new HashMapVector();
            hmvector_rec.add(getdatabase.getKeywordAndWeight(paper.getIdPaper()));
            paper_ref = getdatabase.getDataInformationPaperRef(paper.getIdPaper());
            paper_cit = getdatabase.getDataInformationPaperCit(paper.getIdPaper());
            hmvector_rec.add(sumPaperReferencerpy(paper_ref, paper));
            hmvector_rec.add(sumPaperCitationrpy(paper_cit, paper));
            array_vector.add(hmvector_rec);
        }
        return array_vector;
    }
    //Tính tổng các vector đặc trưng cho bài báo references
    public HashMapVector sumPaperReferencerpy(ArrayList<PaperInformation> paper_ref, PaperInformation paper_in) throws Exception {
        HashMapVector hmvector_ref_ = new HashMapVector();
        for (PaperInformation paper : paper_ref) {
            HashMapVector hmvector_tempp = new HashMapVector();
            hmvector_tempp = getdatabase.getKeywordAndWeight(paper.getIdPaper());
            double weight_rpy=0;
            if(paper.getYearPaper()!=paper_in.getYearPaper())
                weight_rpy=1/(abs(paper.getYearPaper()-paper_in.getYearPaper()));
            else weight_rpy=10/9;
            hmvector_ref_.addScaled(hmvector_tempp, weight_rpy);
        }
        return hmvector_ref_;
    }

    //Tính tổng các vector đặc trưng cho bài báo citations
    public HashMapVector sumPaperCitationrpy(ArrayList<PaperInformation> paper_cit, PaperInformation paper_in) throws Exception {
        HashMapVector hmvector_cit_ = new HashMapVector();
        for (PaperInformation paper : paper_cit) {
            HashMapVector hmvector_tempp = new HashMapVector();
            hmvector_tempp = getdatabase.getKeywordAndWeight(paper.getIdPaper());
            double weight_rpy=0;
            if(paper.getYearPaper()!=paper_in.getYearPaper())
                weight_rpy=1/(abs(paper.getYearPaper()-paper_in.getYearPaper()));
            else weight_rpy=10/9;
            hmvector_cit_.addScaled(hmvector_tempp, weight_rpy);
        }
        return hmvector_cit_;
    }
}
