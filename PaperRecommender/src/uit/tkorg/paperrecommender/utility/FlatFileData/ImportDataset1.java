/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.utility.FlatFileData;

import constant.PaperRecommenerConstant;
import ir.vsr.HashMapVector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import uit.tkorg.paperrecommender.model.Author;
import uit.tkorg.paperrecommender.model.Paper;

/**
 *
 * @author THNghiep This class contents all method to import data from dataset1.
 * Import process needs to filter out noisy data such as keywords longer than 50
 * characters.
 */
public class ImportDataset1 {

    /**
     * This method read all keywords in all papers in the dataset 1 and return
     * them in an arraylist.
     *
     * @return allKeywords.
     */
    public static List readAllKeywords() throws FileNotFoundException, IOException {
        List allKeywords = new ArrayList();
        //generate code here
        List<String> ffile = getPathFile(new File(PaperRecommenerConstant.DATASETFOLDER));
        for (int i = 0; i < ffile.size(); i++) {
            try {
                FileReader file = new FileReader(ffile.get(i));
                BufferedReader textReader = new BufferedReader(file);
                String line;
                String[] tokens;
                while ((line = textReader.readLine()) != null) {
                    tokens = line.split(" ");
                    if (tokens.length == 2) {
                        allKeywords.add(tokens[0]);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return allKeywords;
    }

    /**
     * 
     * @param dir
     * @return listfile
     */
    public static List<String> getPathFile(File dir) {
        File[] files = dir.listFiles();
        List listfile = new ArrayList<String>();
        String name = null;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getPathFile(files[i]);
            } else if (files[i].isFile()) {
                name = files[i].getName().toString();
            }
            if (name == "*.txt") {
                listfile.add(files[i].getAbsolutePath());
            }
        }
        return listfile;
    }

    /**
     * This method read dataset folder (from constant class), then for each
     * paper, create a Paper object and put it in the hashmap.
     *
     * HashMap Key: paper id (in file name) HashMap Value: paper object.
     * @return the hashmap contents all papers.
     * @throws java.io.IOException
     */
    public static HashMap<String, Paper> buildListOfPapers() throws IOException {
        HashMap<String, Paper> papers = new HashMap<String, Paper>();
        readAllCandidatePapers(new File(PaperRecommenerConstant.DATASETFOLDER + "\\RecCandidatePapersFV"), papers);
        return papers;
    }

    /**
     * This method browse each file in candidate paper directory
     *
     * @param dir: address of candidate paper directory
     * @param papers: store all candidate papers
     * @throws java.io.IOException
     */
    public static void readAllCandidatePapers(File dir, HashMap<String, Paper> papers) throws IOException {
        //Get list of all files in directory
        File[] files = dir.listFiles();
        //Browse all files in directory
        for (File file : files) {
            String paperid = file.getName().replaceAll("_recfv.txt", "");
            Paper paper = new Paper();
            paper.setPaperId(paperid);//set PaperId for paper
            paper.setYear(paperYear(paperid));//set Year for paper
            paper.setContent(readFilePaper(file.getAbsoluteFile()));//set Content for paper
            paper.setCitation(addCitation(file.getAbsolutePath()));//set Citation for paper
            paper.setReference(addReference(file.getAbsolutePath()));//set Reference for paper
            papers.put(paperid, paper);
        }
    }

    /**
     * This method read each file in candidate paper directory
     *
     * @param file: address of file
     * @return content
     * @throws java.io.FileNotFoundException
     */
    public static HashMapVector readFilePaper(File file) throws FileNotFoundException, IOException {
        HashMapVector content=new HashMapVector();
        String path = file.getAbsolutePath();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] str = line.split(" ");
                content.increment(str[0], Double.valueOf(str[1]));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return content;
    }

    /**
     * This method return year of candidate paper
     *
     * @param paperId
     * @return year of paper
     */
    public static int paperYear(String paperId) {
        paperId = paperId.substring(1, 2);
        return Integer.parseInt("20" + paperId);
    }

    /**
     * This method add Citation for paper
     *
     * @param paperId
     * @return citation
     * @throws java.io.FileNotFoundException
     */
    public static List<String> addCitation(String paperId) throws FileNotFoundException, IOException {
        List<String> citation = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(PaperRecommenerConstant.DATASETFOLDER + "\\InterLink\\acl.20080325.net"))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] str = line.split(" ==> ");
                if (str[1].equals(paperId)) {
                    citation.add(str[0]);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return citation;
    }

    /**
     * This method add Reference for paper
     *
     * @param paperId
     * @return reference
     * @throws java.io.FileNotFoundException
     */
    public static List<String> addReference(String paperId) throws FileNotFoundException, IOException {
        List<String> reference = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(PaperRecommenerConstant.DATASETFOLDER + "\\InterLink\\acl.20080325.net"))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] str = line.split(" ==> ");
                if (str[0].equals(paperId)) {
                    reference.add(str[1]);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return reference;
    }
    /**
     * 
     * @param dir 
     * @return groundTruth of author
     * @throws FileNotFoundException
     * @throws IOException 
     */
    // Phuong thuc tim groundTruth cua Auhtor
    public static List<String> findGroundTruth (File dir) throws FileNotFoundException, IOException
    {
        List<String> fileGTruth = new ArrayList();
        File[] files = dir.listFiles();
        String name = null;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                findGroundTruth(files[i]);
            } else if (files[i].isFile()) {
                name = files[i].getName().toString();
            }
            if (name == "-rlv.txt") 
            {
             try {
                FileReader file = new FileReader(files[i].getAbsoluteFile());
                BufferedReader textReader = new BufferedReader(file);
                String line;
                while ((line = textReader.readLine()) != null)
                    {
                        fileGTruth.add(line);
                    }
                } catch (Exception ex)
                    {
                        System.out.println(ex.getMessage());
                    }
            }  
        }  
        return fileGTruth;
    }
    /**
     * Tim cac paper Cit cua tac gia
     * @param dir
     * @return list paper cit of authors
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static List<Paper> findCitOfAuthor(File dir)throws FileNotFoundException, IOException
    {
        List <Paper> allCitRef = new ArrayList();
        Paper paper =new Paper();
        File [] files= dir.listFiles();
        String name = null;
        String pathVectorFv =null;// ten duong dan den vector dac trung cua paper
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                findCitOfAuthor(files[i]);
            } else if (files[i].isFile()) 
                {
                    name =files[i].getName();
                    if(name== "*" +"cit"+ "*")
                    {
                        pathVectorFv= files[i].getAbsolutePath();
                        paper.setPaperId(name.replaceAll("_fv.txt",""));
                        paper.setContent(readFilePaper(new File(pathVectorFv)));
                        allCitRef.add(paper);
                    }          
                }
        }
        return allCitRef;
    }
    /**
     * Tim cac paper Ref cua tac gia
     * @param dir
     * @return list cac paper ref cua paper
     * @throws FileNotFoundException
     * @throws IOException 
     */
  
      public static List<Paper> findRefOfAuthor(File dir)throws FileNotFoundException, IOException
    {
        List <Paper> allCitRef = new ArrayList();
        Paper paper =new Paper();
        File [] files= dir.listFiles();
        String name = null;
        String pathVectorFv =null;// ten duong dan den vector dac trung cua paper
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                findRefOfAuthor(files[i]);
            } else if (files[i].isFile()) 
                {
                    name =files[i].getName();
                    if(name== "*" +"ref"+ "*")
                    {
                        pathVectorFv= files[i].getAbsolutePath();
                        paper.setPaperId(name.replaceAll("_fv.txt",""));
                        paper.setContent(readFilePaper(new File(pathVectorFv)));
                        allCitRef.add(paper);
                    }          
                }
        }
        return allCitRef;
    }
      /**
       * Tim danh sach các paper cua tac gia
       * @param dir
       * @return
       * @throws FileNotFoundException
       * @throws IOException 
       */
    public static List<Paper> findPaperOfAuthor (File dir)throws FileNotFoundException, IOException
    {
        List<Paper> papers = new ArrayList();
        Paper paper = new Paper();
        File[] files = dir.listFiles();
        String name= null;
        String pathVectorFv =null;// ten duong dan den vector dac trung cua paper
        for (int i=0; i<files.length;i++)
        { 
            name= files[i].getName().substring(1, 1);// lay ky tu dau cua ten de xet la Junior hay Senior
            pathVectorFv = files[i].getAbsolutePath()+
            "\\".concat(files[i].getName()).concat("_fv");//ten duong dan den den file chua vector dac trung cua paper i
            switch(name)
            {
                case "y":
                  paper.setPaperId(files[i].getName().concat("-1"));// set id paper i cua Senior
                  paper.setReference(findRefOfAuthor(files[i]));// set List ref cua paper i
                  paper.setContent(readFilePaper(new File(pathVectorFv)));// set content cho paper i
                    break;
                case "m":
                    paper.setPaperId(files[i].getName());// set id paper i cua Senior
                    paper.setCitation(findCitOfAuthor(files[i])); // set List cit cua  paper i
                    paper.setReference(findRefOfAuthor(files[i]));// set List ref cua paper i
                    paper.setContent(readFilePaper(new File(pathVectorFv)));// set content cho paper i
                    break;
                default:
                    break;      
            }
             papers.add(paper);
        }
        return papers;
    }
    /**
     *  Doc danh sach tat ca cac tac gia
     * @param dir 
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void  readAllAuthor(File dir,HashMap<String,Author> authors) throws FileNotFoundException, IOException
    {
        File [] files = dir.listFiles();
        String name= null;
        Author author = new Author();
        List<String> listName = new ArrayList();
        for(int i=0; i< files.length;i++)
        {
            if(files[i].isDirectory())
            {
                name=files[i].getName();
                String typeAuthor = name.substring(1, 1);
               /* if(name.substring(1, 1)== "y|m")
                {
                    author.setAuthorId(files[i].getName());
                }*/
                switch (typeAuthor)
                {
                    case "y":
                        author.setAuthorId(name);
                        author.setAuthorType("Junior");
                        author.setFeatureVector(null);// Tih vector dac trung cho user
                        author.setGroundTruth(findGroundTruth(files[i]));
                        author.setPaper(findPaperOfAuthor(files[i]));
                        break;
                    case "m":
                        author.setAuthorId(name);
                        author.setAuthorType("Senior");
                        author.setFeatureVector(null);// Tih vector dac trung cho user
                        author.setGroundTruth(findGroundTruth(files[i]));
                        author.setPaper(findPaperOfAuthor(files[i]));
                        break;
                        
                    default:
                        break;
                }
                   authors.put(name, author);
            }      
           
        }
    }
    public static HashMap<String, Author> buildListOfAuthors() throws IOException {
        HashMap<String, Author> authors = new HashMap<String, Author>();
        // code here.
      readAllAuthor(new File(PaperRecommenerConstant.DATASETFOLDER + "\\JuniorR|SeniorR"),authors);
        return authors;
    }

   
}
