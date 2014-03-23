/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.controller.central;

import uit.tkorg.paperrecommender.controller.recommendation.AuthorLogic;
import uit.tkorg.paperrecommender.controller.recommendation.PaperLogic;


/**
 *
 * @author THNghiep
 * Central controller.
 * Main entry class used for testing.
 * Also control all traffic from gui.
 */
public class PaperRecommender {
    private AuthorLogic authorLogic;
    private PaperLogic paperLogic;
    
    /**
     * @param args the command line arguments
     * This method is used as a entry point for testing.
     */
    public static void main(String[] args) {        
    }
    
    /**
     * This method handles all request from gui.
     * @param request
     * @return response: result of request after processing.
     */
    public String centralController(String request) {
        String response = null;
        
        authorLogic = new AuthorLogic();
        paperLogic = new PaperLogic();
        
        try {
            switch (request) {
                case "Read paper":
                    paperLogic.buildListOfPapers();
                    break;
                case "Read author":
                    authorLogic.buildListOfAuthors();
                    break;
                default: 
                    response = null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return response;
    }
}
