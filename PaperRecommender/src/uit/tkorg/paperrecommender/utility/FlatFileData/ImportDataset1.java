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
     *
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
     * This method build list of authors
     *
     * @return authors
     */
    public static HashMap<String, Author> buildListOfAuthors() {
        HashMap<String, Author> authors = new HashMap<String, Author>();
        //junior authors
        //senior authors
        return authors;
    }

    /**
     * This method get author list
     *
     * @param authors
     * @param dir
     * @return authors
     */
    public static HashMap<String, Author> readAllAuthors(HashMap<String, Author> authors, File dir) {
        List authordirs = new ArrayList();
        File[] files = dir.listFiles();//Get list of all files and folders in directory
        for (File file : files) {
            Author author = new Author();//initial an author
            author.setAuthorId(file.getName());//set AuthorId
            //set AuthoType
            if (file.getName().contains("y")) {
                author.setAuthorType("Junior");
            } else {
                author.setAuthorType("Senior");
            }
            authordirs = getAllPaperFilesOfAuthor(file.getAbsoluteFile());
            //set paper of a author
            //set ground truth
            authors.put(file.getName(), author);
        }
        return authors;
    }

    /**
     * This method get all paper files which relevant with an author
     *
     * @param dir
     * @return paperfiles
     */
    public static List getAllPaperFilesOfAuthor(File dir) {
        List paperfiles = new ArrayList();
        File[] files = dir.listFiles();//Get list of all folders in a directory
        for (File file : files) {
            if (file.isDirectory()) {
                getAllPaperFilesOfAuthor(file);
            } else {
                paperfiles.add(file.getAbsolutePath());
            }
        }
        return paperfiles;
    }

    /**
     * This method set groundTruth List of Author
     *
     * @param file
     * @return groundTruth
     */
    public static List groundTruthList(File file) {
        List groundTruth = new ArrayList();
        String path = file.getAbsolutePath();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                groundTruth.add(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return groundTruth;
    }

    /**
     * This method add papers list for author
     *
     * @param author
     * @param authordirs
     * @return paperlist
     */
    public static List addPaperList(Author author, List authordirs) {
        List paperlist = new ArrayList();
        for (Iterator it = authordirs.iterator(); it.hasNext();) {
            String link = (String) it.next();
            //duyet ơ day
            Paper paper = new Paper();
            paper = setDataOfPaper(author, link);
            paperlist.add(paper);
        }
        return paperlist;
    }

    /**
     * This method set data of a paper
     *
     * @param author
     * @param link
     * @return paper
     */
    public static Paper setDataOfPaper(Author author, String link) {
        Paper paper = new Paper();
        List citation=new ArrayList();
        List reference=new ArrayList();
        if (!link.contains("rlv")) {
            paper.setPaperId(link.replaceAll("_fv.txt", ""));
            if (link.contains("ref")) {
                paper.setPaperType("reference");
                reference.add(link.replaceAll("_fv.txt", ""));
            } else if (link.contains("cit")) {
                paper.setPaperType("citation");
                citation.add(link.replaceAll("_fv.txt", ""));
            } else {
                paper.setPaperType("paper of author");
            }
        } else {
            author.setGroundTruth(groundTruthList(new File(link)));
        }
        return paper;
    }
}
