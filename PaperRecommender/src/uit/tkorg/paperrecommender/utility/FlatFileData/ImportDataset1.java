/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.utility.FlatFileData;

import ir.vsr.HashMapVector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
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
    public static List readAllKeywords() {
        List allKeywords = new ArrayList();
        //generate code here
        return allKeywords;
    }

    /**
     * This method read dataset folder (from constant class), then for each
     * paper, create a Paper object and put it in the hashmap.
     *
     * HashMap Key: paper id (in file name)
     * HashMap Value: paper object.
     * @return the hashmap contents all papers.
     */
    public static HashMap<String, Paper> buildListOfPapers() {
        HashMap<String, Paper> papers = new HashMap<String, Paper>();
        readAllCandidatePapers(new File(""), papers);
        return papers;
    }

    /**
     * This method browse each file in candidate paper directory
     *
     * @param dir: address of candidate paper directory
     * @param papers: store all candidate papers
     */
    public static void readAllCandidatePapers(File dir, HashMap<String, Paper> papers) {
        //Get list of all files in directory
        File[] files = dir.listFiles();
        //Browse all files in directory
        for (File file : files) {
            String paperid = file.getName().replaceAll("_recfv.txt", "");
            Paper paper = new Paper();
            /**set data a paper
             * id a paper
             * year a paper
             * paper content(hashmapvector)
             * citation of a paper
             * reference of a paper
             */
            papers.put(paperid, paper);
        }
    }

    /**
     * This method read each file in candidate paper directory
     *
     * @param file: address of file
     * @throws java.io.FileNotFoundException
     */
    public void readFilePaper(File file) throws FileNotFoundException, IOException {
        String path = file.getAbsolutePath();
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] str = line.split(" ");
                /**
                 * content of paper
                 * HashMapVector of CandidatPaper
                 * hashmapvector.increment(paperid, amount);
                 */
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            br.close();
        }
    }
    
    /**
     * This method return year of candidate paper
     *
     * @param paperid
     * @return year of paper
     */
    public int paperyear (String paperid) {
        paperid=paperid.substring(1, 2);
        return Integer.parseInt("20"+paperid);
    }
}
