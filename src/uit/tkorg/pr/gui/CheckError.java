/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.constant.ImportFiles;
import uit.tkorg.utility.general.NumericUtility;

/**
 *
 * @author Minh
 */
public class CheckError {

    /**
     *
     * @param importFile
     * @param path
     * @return
     * @throws java.io.IOException
     */
    public static void CheckImportData(ImportFiles importFile, final String path) throws IOException {
        switch (importFile) {
            case FILE_AUTHORS:
                fileAuthors(path);
                break;
            case FILE_AUTHOR_PAPER:
                fileAuthorPaper(path);
                break;
            case FILE_AUTHOR_CITE_PAPER:
                fileAuthorCitePaper(path);
                break;
            case FILE_PAPERS:
                filePapers(path);
                break;
            case FILE_PAPER_CITE_PAPER:
                filePaperCitePaper(path);
                break;
            case FILE_GROUNDTRUTH:
                fileGroundTruth(path);
                break;
            default:
                break;
        }
    }

//File author.csv has format idAuthor|||nameAuthor
    public static void fileAuthors(final String path) throws FileNotFoundException, IOException {
        final File fileLog = new File("Temp\\log.txt");

        Runtime runtime = Runtime.getRuntime();
        int numOfProcessors = runtime.availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);
        FileReader file = new FileReader(new File(path));
        BufferedReader textReader = new BufferedReader(file);
        String line = null;
        int numline = 0;
        while ((line = textReader.readLine()) != null) {
            final int numlineFinal = numline;
            final String[] tokens = line.split("\\|\\|\\|");
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    if (tokens.length != 2 || !NumericUtility.isNum(tokens[0])) {
                        String error = "\nFile '" + path + "' not correct format\nReason:\nError at line " + String.valueOf(numlineFinal + 1);
                        try {
                            FileUtils.writeStringToFile(fileLog, error, "UTF8", false);
                        } catch (IOException ex) {
                            Logger.getLogger(CheckError.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            numline++;
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

//File author_paper.csv has format idAuthor|||idPaper
    public static void fileAuthorPaper(final String path) throws FileNotFoundException, IOException {
        final File fileLog = new File("Temp\\log.txt");

        Runtime runtime = Runtime.getRuntime();
        int numOfProcessors = runtime.availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);
        FileReader file = new FileReader(new File(path));
        BufferedReader textReader = new BufferedReader(file);
        String line = null;
        int numline = 0;
        while ((line = textReader.readLine()) != null) {
            final int numlineFinal = numline;
            final String[] tokens = line.split("\\|\\|\\|");
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    if (tokens.length != 2 || !NumericUtility.isNum(tokens[0]) || !NumericUtility.isNum(tokens[1])) {
                        String error = "\nFile '" + path + "' not correct format\nReason:\nError at line " + numlineFinal;
                        try {
                            FileUtils.writeStringToFile(fileLog, error, "UTF8", true);
                        } catch (IOException ex) {
                            Logger.getLogger(CheckError.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            numline++;
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

//File author_cite_paper.csv has format idAuthor|||IdPaper|||Year
    public static void fileAuthorCitePaper(final String path) throws FileNotFoundException, IOException {
        final File fileLog = new File("Temp\\log.txt");

        Runtime runtime = Runtime.getRuntime();
        int numOfProcessors = runtime.availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);
        FileReader file = new FileReader(new File(path));
        BufferedReader textReader = new BufferedReader(file);
        String line = null;
        int numline = 0;
        while ((line = textReader.readLine()) != null) {
            final int numlineFinal = numline;
            final String[] tokens = line.split("\\|\\|\\|");
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    if (tokens.length != 3 || !NumericUtility.isNum(tokens[0]) || !NumericUtility.isNum(tokens[1])) {
                        String error = "\nFile '" + path + "' not correct format\nReason:\nError at line " + numlineFinal;
                        try {
                            FileUtils.writeStringToFile(fileLog, error, "UTF8", true);
                        } catch (IOException ex) {
                            Logger.getLogger(CheckError.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

//File paper.csv has format idPaper|||title|||content|||year
    public static void filePapers(final String path) throws FileNotFoundException, IOException {
        final File fileLog = new File("Temp\\log.txt");

        Runtime runtime = Runtime.getRuntime();
        int numOfProcessors = runtime.availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);
        FileReader file = new FileReader(new File(path));
        BufferedReader textReader = new BufferedReader(file);
        String line = null;
        int numline = 0;
        while ((line = textReader.readLine()) != null) {
            final int numlineFinal = numline;
            final String[] tokens = line.split("\\|\\|\\|");
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    if (tokens.length != 4 || !NumericUtility.isNum(tokens[0])) {
                        String error = "\nFile '" + path + "' not correct format\nReason:\nError at line " + numlineFinal;
                        try {
                            FileUtils.writeStringToFile(fileLog, error, "UTF8", true);
                        } catch (IOException ex) {
                            Logger.getLogger(CheckError.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            numline++;
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

//File paper_cite_paper.csv has format idPaper|||idPaper
    public static void filePaperCitePaper(final String path) throws FileNotFoundException, IOException {
        final File fileLog = new File("Temp\\log.txt");

        Runtime runtime = Runtime.getRuntime();
        int numOfProcessors = runtime.availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);
        FileReader file = new FileReader(new File(path));
        BufferedReader textReader = new BufferedReader(file);
        String line = null;
        int numline = 0;
        while ((line = textReader.readLine()) != null) {
            final int numlineFinal = numline;
            final String[] tokens = line.split("\\|\\|\\|");
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    if (tokens.length != 2 || !NumericUtility.isNum(tokens[0]) || !NumericUtility.isNum(tokens[1])
                            || tokens[0].equals(tokens[1])) {
                        String error = "\nFile '" + path + "' not correct format\nReason:\nError at line " + numlineFinal;
                        try {
                            FileUtils.writeStringToFile(fileLog, error, "UTF8", true);
                        } catch (IOException ex) {
                            Logger.getLogger(CheckError.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            numline++;
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

//File groundtruth.csv has format idAuthor|||idPaper
    public static void fileGroundTruth(final String path) throws FileNotFoundException, IOException {
        final File fileLog = new File("Temp\\log.txt");

        Runtime runtime = Runtime.getRuntime();
        int numOfProcessors = runtime.availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);
        FileReader file = new FileReader(new File(path));
        BufferedReader textReader = new BufferedReader(file);
        String line = null;
        int numline = 0;
        while ((line = textReader.readLine()) != null) {
            final int numlineFinal = numline;
            final String[] tokens = line.split("\\|\\|\\|");
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    if (tokens.length != 2 || !NumericUtility.isNum(tokens[0]) || !NumericUtility.isNum(tokens[1])) {
                        String error = "\nFile '" + path + "' not correct format\nReason:\nError at line " + numlineFinal;
                        try {
                            FileUtils.writeStringToFile(fileLog, error, "UTF8", true);
                        } catch (IOException ex) {
                            Logger.getLogger(CheckError.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            numline++;
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }
}
