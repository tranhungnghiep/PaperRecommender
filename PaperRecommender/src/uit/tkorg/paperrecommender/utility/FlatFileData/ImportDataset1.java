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
     * @throws java.io.FileNotFoundException
     */
    public static List readAllKeywords() throws FileNotFoundException, IOException {
        List allKeywords = new ArrayList();
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
            } catch (IOException ex) {
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
        for (File file : files) {
            if (file.isDirectory()) {
                getPathFile(file);
            } else if (file.isFile()) {
                name = file.getName().toString();
            }
            if ("*.txt".equals(name)) {
                listfile.add(file.getAbsolutePath());
            }
        }
        return listfile;
    }

    /**
     * This method read dataset folder (from constant class), then for each
     * paper, create a Paper object and put it in the hashmap.
     *
     * HashMap Key: paper id (in file name) HashMap Value: paper object.
     *
     * @return the hashmap contents all papers.
     * @throws java.io.IOException
     */
    public static HashMap<String, Paper> buildListOfPapers() throws IOException {
        HashMap<String, Paper> papers = new HashMap<String, Paper>();
        papers = readAllCandidatePapers(new File(PaperRecommenerConstant.DATASETFOLDER + "\\RecCandidatePapersFV"));
        return papers;
    }

    /**
     * This method browse each file in candidate paper directory
     *
     * @param dir: address of candidate paper directory
     * @return papers: list of candidate papers
     * @throws java.io.IOException
     */
    public static HashMap<String, Paper> readAllCandidatePapers(File dir) throws IOException {
        HashMap<String, Paper> papers = new HashMap<String, Paper>();
        File[] files = dir.listFiles();//Get list of all files in directory
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
        return papers;
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
     * This method read candidate paper file to set content for candidate paper
     *
     * @param file: address of file
     * @return content
     * @throws java.io.FileNotFoundException
     */
    public static HashMapVector readFilePaper(File file) throws FileNotFoundException, IOException {
        HashMapVector content = new HashMapVector();
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
    public static List<String> findGroundTruth(File dir) throws FileNotFoundException, IOException {
        List<String> fileGTruth = new ArrayList();
        File[] files = dir.listFiles();
        String name = null;
        for (File file1 : files) {
            if (file1.isDirectory()) {
                findGroundTruth(file1);
            } else if (file1.isFile()) {
                name = file1.getName().toString();
            }
            if ("-rlv.txt".equals(name)) {
                try {
                    FileReader file = new FileReader(file1.getAbsoluteFile());
                    BufferedReader textReader = new BufferedReader(file);
                    String line;
                    while ((line = textReader.readLine()) != null) {
                        fileGTruth.add(line);
                    }
                }catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return fileGTruth;
    }

    /**
     * Tim cac paper Cit cua tac gia
     *
     * @param dir
     * @return list paper cit of authors
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static List<Paper> findCitOfAuthor(File dir) throws FileNotFoundException, IOException {
        List<Paper> allCitRef = new ArrayList();
        Paper paper = new Paper();
        File[] files = dir.listFiles();
        String name = null;
        String pathVectorFv = null;// ten duong dan den vector dac trung cua paper
        for (File file : files) {
            if (file.isDirectory()) {
                findCitOfAuthor(file);
            } else if (file.isFile()) {
                name = file.getName();
                if (name.equals("*" + "cit" + "*")) {
                    pathVectorFv = file.getAbsolutePath();
                    paper.setPaperId(name.replaceAll("_fv.txt", ""));
                    paper.setContent(readFilePaper(new File(pathVectorFv)));
                    allCitRef.add(paper);
                }
            }
        }
        return allCitRef;
    }

    /**
     * Tim cac paper Ref cua tac gia
     *
     * @param dir
     * @return list cac paper ref cua paper
     * @throws FileNotFoundException
     * @throws IOException
     */

    public static List<Paper> findRefOfAuthor(File dir) throws FileNotFoundException, IOException {
        List<Paper> allCitRef = new ArrayList();
        Paper paper = new Paper();
        File[] files = dir.listFiles();
        String name = null;
        String pathVectorFv = null;// ten duong dan den vector dac trung cua paper
        for (File file : files) {
            if (file.isDirectory()) {
                findRefOfAuthor(file);
            } else if (file.isFile()) {
                name = file.getName();
                if (name.equals("*" + "ref" + "*")) {
                    pathVectorFv = file.getAbsolutePath();
                    paper.setPaperId(name.replaceAll("_fv.txt", ""));
                    paper.setContent(readFilePaper(new File(pathVectorFv)));
                    allCitRef.add(paper);
                }
            }
        }
        return allCitRef;
    }

    /**
     * Tim danh sach các paper cua tac gia
     *
     * @param dir
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static List<Paper> findPaperOfAuthor(File dir) throws FileNotFoundException, IOException {
        List<Paper> papers = new ArrayList();
        Paper paper = new Paper();
        File[] files = dir.listFiles();
        String name = null;
        String pathVectorFv = null;// ten duong dan den vector dac trung cua paper
        for (File file : files) {
            name = file.getName().substring(0, 1); // lay ky tu dau cua ten de xet la Junior hay Senior
            pathVectorFv = file.getAbsolutePath() + "\\".concat(file.getName()).concat("_fv"); //ten duong dan den den file chua vector dac trung cua paper i
            switch (name) {
                case "y":
                    paper.setPaperId(file.getName().concat("-1")); // set id paper i cua Senior
                    paper.setReference(findRefOfAuthor(file)); // set List ref cua paper i
                    paper.setContent(readFilePaper(new File(pathVectorFv)));// set content cho paper i
                    break;
                case "m":
                    paper.setPaperId(file.getName()); // set id paper i cua Senior
                    paper.setCitation(findCitOfAuthor(file)); // set List cit cua  paper i
                    paper.setReference(findRefOfAuthor(file)); // set List ref cua paper i
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
     * Doc danh sach tat ca cac tac gia
     *
     * @param dir
     * @param authors
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void readAllAuthor(File dir, HashMap<String, Author> authors) throws FileNotFoundException, IOException {
        File[] files = dir.listFiles();
        String name = null;
        Author author = new Author();
        for (File file : files) {
            if (file.isDirectory()) {
                name = file.getName();
                String typeAuthor = name.substring(1, 1);
                switch (typeAuthor) {
                    case "y":
                        author.setAuthorId(name);
                        author.setAuthorType("Junior");
                        author.setGroundTruth(findGroundTruth(file));
                        author.setPaper(findPaperOfAuthor(file));
                        break;
                    case "m":
                        author.setAuthorId(name);
                        author.setAuthorType("Senior");
                        author.setGroundTruth(findGroundTruth(file));
                        author.setPaper(findPaperOfAuthor(file));
                        break;
                    default:
                        break;
                }
                authors.put(name, author);
            }
        }
    }

    public static HashMap<String, Author> buildListOfAuthors() throws IOException {
        HashMap<String, Author> authors = new HashMap();
        readAllAuthor(new File(PaperRecommenerConstant.DATASETFOLDER ), authors);
        return authors;
    }

}
