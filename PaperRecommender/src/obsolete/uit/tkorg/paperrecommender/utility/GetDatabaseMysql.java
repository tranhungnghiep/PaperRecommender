/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obsolete.uit.tkorg.paperrecommender.utility;

import ir.vsr.HashMapVector;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import obsolete.uit.tkorg.paperrecommender.model.nativejava.dbconnection.ConnectionService;

/**
 *
 * @author Vinh
 * Các hàm lấy dữ liệu từ database
 */
public class GetDatabaseMysql {

    //Trả về idAuthorType của tác giả
    //select idAuthorType from Author where Author.idAuthor='12345'
    public int getDataIdTypeAuthor(String str) throws Exception {
        String query = "select idAuthorType from Author where Author.idAuthor='" + str + "'";
        int id_type = -1;
        Statement stmt = ConnectionService.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            id_type = rs.getInt(1);
        }
        rs.close();
        stmt.close();
        return id_type;
    }

    //Trả về danh sách các bài báo của 1 tác giả
    //select * from Paper where Author_Paper.idAuthor='12345' AND Author_Paper.idPaper=Paper.idPaper
    public ArrayList<PaperInformation> getDataInformationPaperAuthor(String str) throws Exception {
        String query = "select * from Paper,Author_Paper where Author_Paper.idAuthor='" + str + "' AND Author_Paper.idPaper=Paper.idPaper";
        ArrayList<PaperInformation> arraypaper = new ArrayList<PaperInformation>();
        Statement stmt = ConnectionService.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            PaperInformation paper = new PaperInformation();
            paper.setIdPaper(rs.getString(1), rs.getInt(2), rs.getInt(3));
            arraypaper.add(paper);
        }
        rs.close();
        stmt.close();
        return arraypaper;
    }

    //Trả về danh sách các bài báo tham khảo của 1 bài báo
    //select * from Paper where Paper_Paper.idPaper="12345" AND Paper_Paper.idPaperRef=Paper.idPaper
    public ArrayList<PaperInformation> getDataInformationPaperRef(String str) throws Exception {
        String query = "select * from Paper,Paper_Paper where Paper_Paper.idPaper='" + str + "' AND Paper_Paper.idPaperRef=Paper.idPaper";
        ArrayList<PaperInformation> arraypaper = new ArrayList<PaperInformation>();
        Statement stmt = ConnectionService.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            PaperInformation paper = new PaperInformation();
            paper.setIdPaper(rs.getString(1), rs.getInt(2), rs.getInt(3));
            arraypaper.add(paper);
        }
        rs.close();
        stmt.close();
        return arraypaper;
    }

    //Trả về danh sách các bài báo trích dẫn của 1 bài báo
    //select * from Paper where Paper_Paper.idPaperRef="12345" AND Paper_Paper.idPaper=Paper.idPaper
    public ArrayList<PaperInformation> getDataInformationPaperCit(String str) throws Exception {
        String query = "select * from Paper,Paper_Paper where Paper_Paper.idPaperRef='" + str + "' AND Paper_Paper.idPaper=Paper.idPaper";
        ArrayList<PaperInformation> arraypaper = new ArrayList<PaperInformation>();
        Statement stmt = ConnectionService.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            PaperInformation paper = new PaperInformation();
            paper.setIdPaper(rs.getString(1), rs.getInt(2), rs.getInt(3));
            arraypaper.add(paper);
        }
        rs.close();
        stmt.close();
        return arraypaper;
    }

    //Trả về keyword và tf-idf của 1 bài báo
    //Select Vocabulary.keyword, Paper_Keyword.TF-IDF from Vocabulary, Paper_Keyword where Paper_Keyword.idPaper="12345" AND Paper_Keyword.idKeyword=Vocabulary.idKeyword
    public HashMapVector getKeywordAndWeight(String str) throws Exception {
        String query = "Select Vocabulary.keyword, Paper_Keyword.TFIDF from Vocabulary, Paper_Keyword where Paper_Keyword.idPaper='" + str + "' AND Paper_Keyword.idKeyword=Vocabulary.idKeyword";
        HashMapVector hmvector = new HashMapVector();
        Statement stmt = ConnectionService.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            hmvector.increment(rs.getString(1), rs.getDouble(2));
        }
        rs.close();
        stmt.close();
        return hmvector;
    }

    //Trả về danh sách tất cả các bài báo khuyến nghị
    //select * from Paper where Paper.idPaperType='1'
    public ArrayList<PaperInformation> getDataInformationPaperRec() throws Exception {
        String query = "select * from Paper where Paper.idPaperType='1'";
        ArrayList<PaperInformation> arraypaper = new ArrayList<PaperInformation>();
        Statement stmt = ConnectionService.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            PaperInformation paper = new PaperInformation();
            paper.setIdPaper(rs.getString(1), rs.getInt(2), rs.getInt(3));
            arraypaper.add(paper);
        }
        rs.close();
        stmt.close();
        return arraypaper;
    }
}
