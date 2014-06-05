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
     * Read Paper.csv, for each paper, create a Paper object and put it in the hashmap.
     *
     * HashMap Key: paper id (in file name) 
     * HashMap Value: paper object.
     *
     * @return the hashmap contents all papers.
     * @throws java.io.IOException
     */
    public static HashMap<String, Paper> readPaperList(String fileNamePaper, String fileNamePaperCitePaper) throws Exception {
//        System.out.println("Begin reading paper list...");
//        long startTime = System.nanoTime();

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
                paper.setTitle(title);
                paper.setPaperAbstract(paperAbstract);
                paper.setYear(year);
                
                papers.put(paperId, paper);
            }
            
            readCitationRelationship(fileNamePaperCitePaper, papers);
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        
//        long estimatedTime = System.nanoTime() - startTime;
//        System.out.println("Reading paper list elapsed time: " + estimatedTime / 1000000000 + " seconds");
//        System.out.println("End reading paper list.");
        
        return papers;
    }
    
    private static void readCitationRelationship(String fileNameCitation, HashMap<String, Paper> papers) throws Exception {
//        System.out.println("Begin reading citation relationship...");
//        long startTime = System.nanoTime();

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
                    papers.get(paperId1).getReference().add(paperId2); // reference is mutable.
                }
                if (papers.containsKey(paperId2)) {
                    papers.get(paperId2).getCitation().add(paperId1);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        
//        long estimatedTime = System.nanoTime() - startTime;
//        System.out.println("Reading citation relationship elapsed time: " + estimatedTime / 1000000000 + " seconds");
//        System.out.println("End reading citation relationship.");
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
//        System.out.println("Begin reading author list...");
//        long startTime = System.nanoTime();

        HashMap<String, Author> authors = new HashMap();
        readAuthorship(fileNameAuthorship, authors, true);
        
//        long estimatedTime = System.nanoTime() - startTime;
//        System.out.println("Reading author list elapsed time: " + estimatedTime / 1000000000 + " seconds");
//        System.out.println("End reading author list.");

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
//        System.out.println("Begin reading authorship...");
//        long startTime = System.nanoTime();

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
                    authors.get(authorId).getPaper().add(paperId); // mutable.
                } else if (autoAddAuthor) {
                    Author author = new Author();
                    author.setAuthorId(authorId);
                    authors.put(authorId, author);
                    authors.get(authorId).getPaper().add(paperId); // mutable.
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        
//        long estimatedTime = System.nanoTime() - startTime;
//        System.out.println("Reading authorship elapsed time: " + estimatedTime / 1000000000 + " seconds");
//        System.out.println("End reading author list.");
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
//        System.out.println("Begin reading recommending author list...");
//        long startTime = System.nanoTime();

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
        
//        long estimatedTime = System.nanoTime() - startTime;
//        System.out.println("Reading recommending author list elapsed time: " + estimatedTime / 1000000000 + " seconds");
//        System.out.println("End reading recommending author list.");
        
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
//        System.out.println("Begin reading ground truth...");
//        long startTime = System.nanoTime();

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
        
//        long estimatedTime = System.nanoTime() - startTime;
//        System.out.println("Reading ground truth elapsed time: " + estimatedTime / 1000000000 + " seconds");
//        System.out.println("End reading ground truth.");
    }

    /**
     * Note: this method read the raw rating matrix, i.d., ratings are number of citing, not normalized.
     * 
     * @param fileNameAuthorCitePaperMatrix
     * @return
     * @throws Exception 
     */
    public static HashMap<String, HashMap<String, Double>> readAuthorCitePaperMatrix(String fileNameAuthorCitePaperMatrix) throws Exception {
        System.out.println("Begin reading author cite paper matrix...");
        long startTime = System.nanoTime();

        HashMap<String, HashMap<String, HashMap<String, Integer>>> authorCitePaperYear = new HashMap<>();
        HashMap<String, HashMap<String, Double>> authorPaperRating = new HashMap<>();
        
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

                // put into authorPaperRating
                if (authorPaperRating.containsKey(authorId)) {
                    if (authorPaperRating.get(authorId).containsKey(paperId)) {
                        Double rating = authorPaperRating.get(authorId).get(paperId) + 1;
                        authorPaperRating.get(authorId).put(paperId, rating);
                    } else {
                        authorPaperRating.get(authorId).put(paperId, Double.valueOf(1));
                    }
                } else {
                    HashMap<String, Double> paperRating = new HashMap<>();
                    paperRating.put(paperId, Double.valueOf(1));
                    authorPaperRating.put(authorId, paperRating);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Reading author cite paper matrix elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End reading author cite paper matrix.");
        
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
