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
            line = br.readLine(); // Skip first line with header content.
            while ((line = br.readLine()) != null) {
                String[] str = line.split("\",\"");
                String paperId = str[0].replace("\"", " ").trim();
                String title = str[1].trim();
                String paperAbstract = str[2].trim();
                int year = Integer.parseInt(str[3].replace("\"", " ").trim());
                
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
            line = br.readLine(); // Skip first line with header content.
            
            while ((line = br.readLine()) != null) {
                String[] str = line.split("\",\"");
                String paperId1 = str[0].replace("\"", " ").trim();
                String paperId2 = str[1].replace("\"", " ").trim();
                
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
            line = br.readLine(); // Skip first line with header content.
            
            while ((line = br.readLine()) != null) {
                String[] str = line.split("\",\"");
                String authorId = str[0].replace("\"", " ").trim();
                String paperId = str[1].replace("\"", " ").trim();
                
                if (authors.containsKey(authorId)) {
                    authors.get(authorId).getPaper().add(paperId); // mutable.
                } else {
                    Author author = new Author();
                    List paper = new ArrayList();
                    paper.add(paperId);
                    author.setAuthorId(authorId);
                    author.setPaper(paper);
                    authors.put(authorId, author);
                }
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
            line = br.readLine(); // Skip first line with header content.
            
            while ((line = br.readLine()) != null) {
                String[] str = line.split("\",\"");
                String authorId = str[0].replace("\"", " ").trim();
                String authorName = str[1].replace("\"", " ").trim();

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
            line = br.readLine(); // Skip first line with header content.
            
            while ((line = br.readLine()) != null) {
                String[] str = line.split("\",\"");
                String authorId = str[0].replace("\"", " ").trim();
                String paperId = str[1].replace("\"", " ").trim();
                
                if (authors.containsKey(authorId)) {
                    authors.get(authorId).getGroundTruth().add(paperId); // mutable.
                } else {
                    Author author = new Author();
                    List groundTruth = new ArrayList();
                    groundTruth.add(paperId);
                    author.setAuthorId(authorId);
                    author.setPaper(groundTruth);
                    authors.put(authorId, author);
                }
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
