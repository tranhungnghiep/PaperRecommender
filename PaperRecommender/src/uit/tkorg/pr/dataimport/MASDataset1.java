/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.dataimport;

import ir.vsr.HashMapVector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import uit.tkorg.pr.constant.PRConstant;
import uit.tkorg.pr.dataimport.model.Author;
import uit.tkorg.pr.dataimport.model.Paper;
import uit.tkorg.pr.utility.IOUtility;
import uit.tkorg.pr.utility.NumericUtility;

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
        System.out.println("Begin reading paper list...");
        long startTime = System.nanoTime();

        HashMap<String, Paper> papers = new HashMap();
        
        try (BufferedReader br = new BufferedReader(new FileReader(fileNamePaper))) {
            String line;
            while ((line = br.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    break;
                }
                String[] str = line.split("|||");
                String paperId = IOUtility.getAcceptedFieldValue(str[0]);
                String title = IOUtility.getAcceptedFieldValue(str[1]);
                String paperAbstract = IOUtility.getAcceptedFieldValue(str[2]);
                String strYear = IOUtility.getAcceptedFieldValue(str[3]);
                int year = 0;
                if (NumericUtility.isNum(strYear)) {
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
        
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Reading paper list elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End reading paper list.");
        
        return papers;
    }
    
    private static void readCitationRelationship(String fileNameCitation, HashMap<String, Paper> papers) throws Exception {
        System.out.println("Begin reading citation relationship...");
        long startTime = System.nanoTime();

        try (BufferedReader br = new BufferedReader(new FileReader(fileNameCitation))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    break;
                }
                String[] str = line.split("|||");
                String paperId1 = IOUtility.getAcceptedFieldValue(str[0]);
                String paperId2 = IOUtility.getAcceptedFieldValue(str[1]);
                
                papers.get(paperId1).getReference().add(paperId2); // reference is mutable.
                papers.get(paperId2).getCitation().add(paperId1);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Reading citation relationship elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End reading citation relationship.");
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
        System.out.println("Begin reading author list...");
        long startTime = System.nanoTime();

        HashMap<String, Author> authors = new HashMap();
        readAuthorship(fileNameAuthorship, authors);
        
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Reading author list elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End reading author list.");

        return authors;
    }
    
    private static void readAuthorship(String fileNameAuthorship, HashMap<String, Author> authors) throws Exception {
        System.out.println("Begin reading authorship...");
        long startTime = System.nanoTime();

        try (BufferedReader br = new BufferedReader(new FileReader(fileNameAuthorship))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    break;
                }
                String[] str = line.split("|||");
                String authorId = IOUtility.getAcceptedFieldValue(str[0]);
                String paperId = IOUtility.getAcceptedFieldValue(str[1]);
                
                if (!authors.containsKey(authorId)) {
                    Author author = new Author();
                    author.setAuthorId(authorId);
                    authors.put(authorId, author);
                }
                authors.get(authorId).getPaper().add(paperId); // mutable.
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Reading authorship elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End reading author list.");
    }

    public static HashMap<String, Author> readRecommendingAuthorList(String fileNameRecommendingAuthors, String fileNameGroundTruth) throws Exception {
        System.out.println("Begin reading recommending author list...");
        long startTime = System.nanoTime();

        HashMap<String, Author> authors = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(fileNameRecommendingAuthors))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    break;
                }
                String[] str = line.split("|||");
                String authorId = IOUtility.getAcceptedFieldValue(str[0]);
                String authorName = IOUtility.getAcceptedFieldValue(str[1]);

                Author tempAuthor = new Author();
                tempAuthor.setAuthorId(authorId);
                tempAuthor.setAuthorName(authorName);
                authors.put(authorId, tempAuthor);
            }
            
            readGroundTruth(fileNameGroundTruth, authors);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Reading recommending author list elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End reading recommending author list.");
        
        return authors;
    }
    
    /**
     * This method find list paper ground truth of Author
     *
     * @param dir
     * @return a string is groundTruth of author
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void readGroundTruth(String fileNameGroundTruth, HashMap<String, Author> authors) throws Exception {
        System.out.println("Begin reading ground truth...");
        long startTime = System.nanoTime();

        try (BufferedReader br = new BufferedReader(new FileReader(fileNameGroundTruth))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    break;
                }
                String[] str = line.split("|||");
                String authorId = IOUtility.getAcceptedFieldValue(str[0]);
                String paperId = IOUtility.getAcceptedFieldValue(str[1]);
                
                if (!authors.containsKey(authorId)) {
                    Author author = new Author();
                    author.setAuthorId(authorId);
                    authors.put(authorId, author);
                }
                authors.get(authorId).getGroundTruth().add(paperId); // mutable.
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Reading ground truth elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End reading ground truth.");
    }
    
    /**
     * Test.
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        HashMap<String, Paper> papers = readPaperList(PRConstant.FOLDER_MAS_DATASET1 
                + "Paper_Before_2006.csv", PRConstant.FOLDER_MAS_DATASET1 
                + "Paper_Cite_Paper_Before_2006.csv");
        HashMap<String, Author> authors = readAuthorList(PRConstant.FOLDER_MAS_DATASET1 
                + "Author_Write_Paper_Before_2006.csv");
        HashMap<String, Author> groundTruth = readRecommendingAuthorList(PRConstant.FOLDER_MAS_DATASET1 
                + "1000Authors.csv", PRConstant.FOLDER_MAS_DATASET1 
                + "Ground_Truth_2006_2008.csv");
    }
}
