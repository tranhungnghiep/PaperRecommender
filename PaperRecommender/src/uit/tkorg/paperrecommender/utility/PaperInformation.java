/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.utility;

/**
 *
 * @author Vinh
 * Lớp mô phỏng bảng Paper trong database
 */
public class PaperInformation {

    private String id_paper;
    private int year_paper;
    private int type_paper;

    public void setIdPaper(String id, int year, int type) {
        id_paper = id;
        year_paper = year;
        type_paper = type;
    }

    public String getIdPaper() {
        return id_paper;
    }

    public int getYearPaper() {
        return year_paper;
    }

    public int getTypePaper() {
        return type_paper;
    }
}
