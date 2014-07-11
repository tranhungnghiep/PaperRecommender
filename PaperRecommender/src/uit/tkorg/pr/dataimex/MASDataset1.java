/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.dataimex;

import ir.vsr.HashMapVector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import uit.tkorg.pr.constant.PRConstant;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.BinaryFileUtility;
import uit.tkorg.utility.general.NumericUtility;

/**
 *
 * @author THNghiep 
 * This class contents all method to import data from dataset1.
 * Import process needs to filter out noisy data such as keywords longer than 50
 * characters.
 */
public class MASDataset1 {

    // Prevent instantiation.
    private MASDataset1() {

    }

    /**
     * Read from .csv file, for each paper, create a Paper object and put it in the hashmap.
     * HashMap Key: paper id (in file name) 
     * HashMap Value: paper object.
     * @return the hashmap contents all papers.
     * @throws java.io.IOException
     */
    public static HashMap<String, Paper> readPaperList(String fileNamePaper, String fileNamePaperCitePaper) throws Exception {
        HashMap<String, Paper> papers = new HashMap();
        
        try (BufferedReader br = new BufferedReader(new FileReader(fileNamePaper))) {
            String line;
            while ((line = br.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    break;
                }
                String[] str = line.split("\\|\\|\\|");
                String paperId = getAcceptedFieldValue(str[0]);
                String title = getAcceptedFieldValue(str[1]);
                String paperAbstract = getAcceptedFieldValue(str[2]);
                String strYear = getAcceptedFieldValue(str[3]);
                int year = -1;
                if ((strYear != null) && (NumericUtility.isNum(strYear))) {
                    year = Integer.parseInt(strYear);
                }
                
                Paper paper = new Paper();
                paper.setPaperId(paperId);
                paper.setPaperTitle(title);
                paper.setPaperAbstract(paperAbstract);
                paper.setYear(year);
                
                papers.put(paperId, paper);
            }
            
            readCitationRelationship(fileNamePaperCitePaper, papers);
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        
        return papers;
    }
    
    private static void readCitationRelationship(String fileNameCitation, HashMap<String, Paper> papers) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(fileNameCitation))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    break;
                }
                String[] str = line.split("\\|\\|\\|");
                String paperId1 = getAcceptedFieldValue(str[0]);
                String paperId2 = getAcceptedFieldValue(str[1]);
                
                if (papers.containsKey(paperId1)) {
                    papers.get(paperId1).getReferenceList().add(paperId2); // reference is mutable.
                }
                if (papers.containsKey(paperId2)) {
                    papers.get(paperId2).getCitationList().add(paperId1);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    /**
     * Read author write paper file, then for each author, create an Author
     * object and put it in the hashmap.
     *
     * HashMap Key: author id (in file name) 
     * HashMap Value: author object.
     *
     * @return the hashmap contents all authors.
     * @throws java.io.IOException
     */
    public static HashMap<String, Author> readAuthorList(String fileNameAuthorship) throws Exception {

        HashMap<String, Author> authors = new HashMap();
        readAuthorship(fileNameAuthorship, authors, true);

        return authors;
    }
    
    /**
     * Read authorship information for a given author list.
     * 
     * @param fileNameAuthorship
     * @param authors: the author list to add author list.
     * @param autoAddAuthor: auto add author when missing in the authors list.
     * @throws Exception 
     */
    private static void readAuthorship(String fileNameAuthorship, HashMap<String, Author> authors, boolean autoAddAuthor) throws Exception {

        try (BufferedReader br = new BufferedReader(new FileReader(fileNameAuthorship))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    break;
                }
                String[] str = line.split("\\|\\|\\|");
                String authorId = getAcceptedFieldValue(str[0]);
                String paperId = getAcceptedFieldValue(str[1]);
                
                if (authors.containsKey(authorId)) {
                    authors.get(authorId).getPaperList().add(paperId); // mutable.
                } else if (autoAddAuthor) {
                    Author author = new Author();
                    author.setAuthorId(authorId);
                    authors.put(authorId, author);
                    authors.get(authorId).getPaperList().add(paperId); // mutable.
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    /**
     * This method reads list 1000 authors for test set.
     * The return authors does not contain authorship info.
     * 
     * @param fileNameAuthorTestSet
     * @param fileNameGroundTruth
     * @return
     * @throws Exception 
     */
    public static HashMap<String, Author> readAuthorListTestSet(String fileNameAuthorTestSet, String fileNameGroundTruth, String fileNameAuthorship) throws Exception {
        HashMap<String, Author> authors = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(fileNameAuthorTestSet))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    break;
                }
                String[] str = line.split("\\|\\|\\|");
                String authorId = getAcceptedFieldValue(str[0]);
                String authorName = getAcceptedFieldValue(str[1]);

                Author tempAuthor = new Author();
                tempAuthor.setAuthorId(authorId);
                tempAuthor.setAuthorName(authorName);
                
                authors.put(authorId, tempAuthor);
            }
            
            readGroundTruth(fileNameGroundTruth, authors, false);
            readAuthorship(fileNameAuthorship, authors, false);
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        
        return authors;
    }
    
    /**
     * This method find list paper ground truth of Author
     * 
     * @param fileNameGroundTruth
     * @param authors
     * @param autoAddAuthor: auto add author when missing in the authors list.
     * @throws Exception 
     */
    private static void readGroundTruth(String fileNameGroundTruth, HashMap<String, Author> authors, boolean autoAddAuthor) throws Exception {

        try (BufferedReader br = new BufferedReader(new FileReader(fileNameGroundTruth))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    break;
                }
                String[] str = line.split("\\|\\|\\|");
                String authorId = getAcceptedFieldValue(str[0]);
                String paperId = getAcceptedFieldValue(str[1]);
                
                if (authors.containsKey(authorId)) {
                    authors.get(authorId).getGroundTruth().add(paperId); // mutable.
                } else if (autoAddAuthor) {
                    Author author = new Author();
                    author.setAuthorId(authorId);
                    authors.put(authorId, author);
                    authors.get(authorId).getGroundTruth().add(paperId); // mutable.
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    /**
     * Note: this method read the raw rating matrix, i.d., ratings are number of citing, not normalized.
     * 
     * @param fileNameAuthorCitePaperMatrix
     * @return
     * @throws Exception 
     */
    public static HashMap<String, HashMap<String, Double>> readAuthorCitePaperMatrix(String fileNameAuthorCitePaperMatrix) throws Exception {
//        HashMap<String, HashMap<String, HashMap<String, Integer>>> authorCitePaperYear = new HashMap<>();
        HashMap<String, HashMap<String, Double>> authorPaperRating = new HashMap<>(); // HashMap<Author ID, HashMap<Paper Id, Raw Rating>>.
        
        try (BufferedReader br = new BufferedReader(new FileReader(fileNameAuthorCitePaperMatrix))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    break;
                }
                String[] str = line.split("\\|\\|\\|");
                String authorId = getAcceptedFieldValue(str[0]);
                String paperId = getAcceptedFieldValue(str[1]);
                String year = getAcceptedFieldValue(str[2]);
                
                //<editor-fold defaultstate="collapsed" desc="authorCitePaperYear">
                // put into authorCitePaperYear
                // Reserved for using later.
                /*if (authorCitePaperYear.containsKey(authorId)) {
                    if (authorCitePaperYear.get(authorId).containsKey(paperId)) {
                        if (authorCitePaperYear.get(authorId).get(paperId).containsKey(year)) {
                            Integer numOfCite = authorCitePaperYear.get(authorId).get(paperId).get(year) + 1;
                            authorCitePaperYear.get(authorId).get(paperId).put(year, numOfCite);
                        } else {
                            authorCitePaperYear.get(authorId).get(paperId).put(year, Integer.valueOf(1));
                        }
                    } else {
                        HashMap<String, Integer> yearCite = new HashMap();
                        yearCite.put(year, Integer.valueOf(1));
                        authorCitePaperYear.get(authorId).put(paperId, yearCite);
                    }
                } else {
                    HashMap<String, Integer> yearCite = new HashMap();
                    yearCite.put(year, Integer.valueOf(1));
                    HashMap<String, HashMap<String, Integer>> paper = new HashMap<>();
                    paper.put(paperId, yearCite);
                    authorCitePaperYear.put(authorId, paper);
                }*/
                //</editor-fold>

                // put into authorPaperRating
                Double rating = null;
                if (!authorPaperRating.containsKey(authorId)) {
                    HashMap<String, Double> paperRating = new HashMap<>();
                    authorPaperRating.put(authorId, paperRating);
                    rating = Double.valueOf(1);
                } else {
                    if (!authorPaperRating.get(authorId).containsKey(paperId)) {
                        rating = Double.valueOf(1);
                    } else {
                        rating = authorPaperRating.get(authorId).get(paperId) + 1;
                    }
                }
                authorPaperRating.get(authorId).put(paperId, rating);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        
        return authorPaperRating;
    }

    private static String getAcceptedFieldValue(String fieldValue) throws Exception {
        String value = fieldValue.trim();
        if (value.equalsIgnoreCase("\\N")) {
            return "";
        } else {
            return value;
        }
    }
    
    /**
     * Test.
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        HashMap<String, Paper> papers = readPaperList(PRConstant.FOLDER_MAS_DATASET1 
                + "[Training] Paper_Before_2006.csv", PRConstant.FOLDER_MAS_DATASET1 
                + "[Training] Paper_Cite_Paper_Before_2006.csv");
        HashMap<String, Author> authors = readAuthorList(PRConstant.FOLDER_MAS_DATASET1 
                + "[Training] Author_Paper_Before_2006.csv");
        HashMap<String, Author> authorsTestSet = readAuthorListTestSet(PRConstant.FOLDER_MAS_DATASET1 
                + "[Training] 1000Authors.csv", PRConstant.FOLDER_MAS_DATASET1 
                + "[Validation] Ground_Truth_2006_2008.csv", PRConstant.FOLDER_MAS_DATASET1 
                + "[Training] Author_Paper_Before_2006.csv");
        HashMap<String, HashMap<String, Double>> authorPaperRating = readAuthorCitePaperMatrix(PRConstant.FOLDER_MAS_DATASET1 
                + "[Training] Author_Cite_Paper_Before_2006.csv");
    }
}
