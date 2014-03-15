/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.utility;

import ir.vsr.HashMapVector;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import uit.tkorg.paperrecommender.model.nativejava.dbconnection.ConnectionService;

/**
 *
 * @author Vinh
 * Các hàm lấy dữ liệu từ database
 */
public class GetDatabaseMysql {

    //Trả về idAuthorType của tác giả
    //select idAuthorType from Author where Author.idAuthor='12345'
    public static int getDataIdTypeAuthor(int i) throws Exception {
        String query = "select idAuthorType from Author where Author.idAuthor='" + i + "'";
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
    public static ArrayList<PaperInformation> getDataInformationPaperAuthor(int i) throws Exception {
        String query = "select * from Paper where Author_Paper.idAuthor='" + i + "' AND Author_Paper.idPaper=Paper.idPaper";
        ArrayList<PaperInformation> arraypaper = new ArrayList<PaperInformation>();
        Statement stmt = ConnectionService.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            PaperInformation paper = new PaperInformation();
            paper.setIdPaper(rs.getInt(1), rs.getInt(2), rs.getInt(3));
            arraypaper.add(paper);
        }
        rs.close();
        stmt.close();
        return arraypaper;
    }

    //Trả về danh sách các bài báo tham khảo của 1 bài báo
    //select * from Paper where Paper_Paper.idPaper="12345" AND Paper_Paper.idPaperRef=Paper.idPaper
    public static ArrayList<PaperInformation> getDataInformationPaperRef(int i) throws Exception {
        String query = "select * from Paper where Paper_Paper.idPaper='" + i + "' AND Paper_Paper.idPaperRef=Paper.idPaper";
        ArrayList<PaperInformation> arraypaper = new ArrayList<PaperInformation>();
        Statement stmt = ConnectionService.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            PaperInformation paper = new PaperInformation();
            paper.setIdPaper(rs.getInt(1), rs.getInt(2), rs.getInt(3));
            arraypaper.add(paper);
        }
        rs.close();
        stmt.close();
        return arraypaper;
    }

    //Trả về danh sách các bài báo trích dẫn của 1 bài báo
    //select * from Paper where Paper_Paper.idPaperRef="12345" AND Paper_Paper.idPaper=Paper.idPaper
    public static ArrayList<PaperInformation> getDataInformationPaperCit(int i) throws Exception {
        String query = "select * from Paper where Paper_Paper.idPaperRef='" + i + "' AND Paper_Paper.idPaper=Paper.idPaper";
        ArrayList<PaperInformation> arraypaper = new ArrayList<PaperInformation>();
        Statement stmt = ConnectionService.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            PaperInformation paper = new PaperInformation();
            paper.setIdPaper(rs.getInt(1), rs.getInt(2), rs.getInt(3));
            arraypaper.add(paper);
        }
        rs.close();
        stmt.close();
        return arraypaper;
    }

    //Trả về keyword và tf-idf của 1 bài báo
    //Select Vocabulary.keyword, Paper_Keyword.TF-IDF from Vocabulary, Paper_Keyword where Paper_Keyword.idPaper="12345" AND Paper_Keyword.idKeyword=Vocabulary.idKeyword
    public static HashMapVector getKeywordAndWeight(int i) throws Exception {
        String query = "Select Vocabulary.keyword, Paper_Keyword.TF-IDF from Vocabulary, Paper_Keyword where Paper_Keyword.idPaper='" + i + "' AND Paper_Keyword.idKeyword=Vocabulary.idKeyword";
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
    public static ArrayList<PaperInformation> getDataInformationPaperRec() throws Exception {
        String query = "select * from Paper where Paper.idPaperType='1'";
        ArrayList<PaperInformation> arraypaper = new ArrayList<PaperInformation>();
        Statement stmt = ConnectionService.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            PaperInformation paper = new PaperInformation();
            paper.setIdPaper(rs.getInt(1), rs.getInt(2), rs.getInt(3));
            arraypaper.add(paper);
        }
        rs.close();
        stmt.close();
        return arraypaper;
    }
}
