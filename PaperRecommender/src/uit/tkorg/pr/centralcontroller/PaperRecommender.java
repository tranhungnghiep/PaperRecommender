/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.centralcontroller;
import ir.vsr.HashMapVector;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.constant.PRConstant;
import uit.tkorg.pr.dataimex.MASDataset1;
import uit.tkorg.pr.dataimex.MahoutFile;
import uit.tkorg.pr.dataimex.NUSDataset1;
import uit.tkorg.pr.dataimex.PRGeneralFile;
import uit.tkorg.pr.datapreparation.cbf.ComputeAuthorFV;
import uit.tkorg.pr.datapreparation.cbf.ComputePaperFV;
import uit.tkorg.pr.evaluation.Evaluator;
import uit.tkorg.pr.method.cbf.CBFRecommender;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.BinaryFileUtility;
import uit.tkorg.utility.textvectorization.TextPreprocessUtility;
import uit.tkorg.utility.textvectorization.TextVectorizationByMahoutTerminalUtility;

/**
 *
 * @author THNghiep Central controller. Main entry class used for testing. Also
 * control all traffic from gui.
 */
public class PaperRecommender {

    // Key of this hash map is paper id.
    // Value of this hash map is the relevant paper object.
    private HashMap<String, Author> authors;

    // Key of this hash map is paper id.
    // Value of this hash map is the relevant paper object.
    private HashMap<String, Paper> papers;

    /**
     * This method is used as a entry point for testing.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) { 
        try {
            recommendationFlowController(PRConstant.FOLDER_MAS_DATASET1 + "[Training] Paper_Before_2006.csv", 
                    PRConstant.FOLDER_MAS_DATASET1 + "[Training] Paper_Cite_Paper_Before_2006.csv", 
                    PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\text", 
                    PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\Removed stopword and stemming text", 
                    PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\sequence", 
                    PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\vector");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void recommendationFlowController(String fileNamePaper, String fileNamePaperCitePaper, String textDir, String textPreprocessedDir, String sequenceDir, String vectorDir) throws Exception {
        HashMap<String, Paper> papers = MASDataset1.readPaperList(fileNamePaper, fileNamePaperCitePaper);
        // write abstract to text file
        PRGeneralFile.writePaperAbstractToTextFile(papers, textDir);
        // preprocess text
        TextPreprocessUtility.parallelProcess(textDir, textPreprocessedDir, true, true);
        // tf-idf
        TextVectorizationByMahoutTerminalUtility.textVectorizeFiles(textPreprocessedDir, sequenceDir, vectorDir);
        // read tf-idf
        HashMap<Integer, String> dictMap = MahoutFile.readMahoutDictionaryFiles(vectorDir);
        HashMap<String, HashMapVector> vectorizedDocuments = MahoutFile.readMahoutVectorFiles(vectorDir);
    }

    /**
     * This method handles all request from gui.
     *
     * @param request
     * @param param
     * @return response: result of request after processing.
     */
    public String[] guiRequestHandler(String request, String param) {
        String[] response = new String[2];

        String Dataset1Folder;
        String SaveDataFolder;

        try {
            switch (request) {

                // Dataset 1: data import.
                case "Read paper":
                    // Read param to get dataset 1 folder.
                    if ((param != null) && !(param.isEmpty())) {
                        Dataset1Folder = param;
                    } else {
                        Dataset1Folder = PRConstant.FOLDER_NUS_DATASET1;
                    }
                    papers = NUSDataset1.buildListOfPapers(Dataset1Folder);
                    response[0] = "Success.";
                    break;
                case "Read author":
                    // Read param to get dataset 1 folder.
                    if ((param != null) && !(param.isEmpty())) {
                        Dataset1Folder = param;
                    } else {
                        Dataset1Folder = PRConstant.FOLDER_NUS_DATASET1;
                    }
                    authors = NUSDataset1.buildListOfAuthors(Dataset1Folder);
                    response[0] = "Success.";
                    break;
                case "Save paper":
                    // Read param to get save data folder.
                    if ((param != null) && !(param.isEmpty())) {
                        SaveDataFolder = param;
                    } else {
                        SaveDataFolder = PRConstant.SAVEDATAFOLDER;
                    }
                    BinaryFileUtility.saveObjectToFile(papers, SaveDataFolder + "\\Papers.dat");
                    response[0] = "Success.";
                    break;
                case "Save author":
                    // Read param to get save data folder.
                    if ((param != null) && !(param.isEmpty())) {
                        SaveDataFolder = param;
                    } else {
                        SaveDataFolder = PRConstant.SAVEDATAFOLDER;
                    }
                    BinaryFileUtility.saveObjectToFile(authors, SaveDataFolder + "\\Authors.dat");
                    response[0] = "Success.";
                    break;
                case "Load paper":
                    // Read param to get save data folder.
                    if ((param != null) && !(param.isEmpty())) {
                        SaveDataFolder = param;
                    } else {
                        SaveDataFolder = PRConstant.SAVEDATAFOLDER;
                    }
                    papers = (HashMap<String, Paper>) BinaryFileUtility.loadObjectFromFile(SaveDataFolder + "\\Papers.dat");
                    response[0] = "Success.";
                    break;
                case "Load author":
                    // Read param to get save data folder.
                    if ((param != null) && !(param.isEmpty())) {
                        SaveDataFolder = param;
                    } else {
                        SaveDataFolder = PRConstant.SAVEDATAFOLDER;
                    }
                    authors = (HashMap<String, Author>) BinaryFileUtility.loadObjectFromFile(SaveDataFolder + "\\Authors.dat");
                    response[0] = "Success.";
                    break;

                // Dataset 1: data preparation.
                case "Paper FV linear":
                    ComputePaperFV.computeAllPapersFV(papers, 3, 0);
                    response[0] = "Success.";
                    break;
                case "Paper FV cosine":
                    ComputePaperFV.computeAllPapersFV(papers, 3, 1);
                    response[0] = "Success.";
                    break;
                case "Paper FV RPY":
                    ComputePaperFV.computeAllPapersFV(papers, 3, 2);
                    response[0] = "Success.";
                    break;
                case "Author FV linear":
                    ComputeAuthorFV.computeAllAuthorsFV(authors, 0);
                    response[0] = "Success.";
                    break;
                case "Author FV cosine":
                    ComputeAuthorFV.computeAllAuthorsFV(authors, 1);
                    response[0] = "Success.";
                    break;
                case "Author FV RPY":
                    ComputeAuthorFV.computeAllAuthorsFV(authors, 2);
                    response[0] = "Success.";
                    break;
                case "Recommend":
                    authors = CBFRecommender.buildAllRecommendationLists(authors, papers);
                    response[0] = "Success.";
                    break;
                case "NDCG5":
                    response[1] = String.valueOf(Evaluator.NDCG(authors, 5));
                    response[0] = "Success.";
                    break;
                case "NDCG10":
                    response[1] = String.valueOf(Evaluator.NDCG(authors, 10));
                    response[0] = "Success.";
                    break;
                case "MRR":
                    response[1] = String.valueOf(Evaluator.MRR(authors));
                    response[0] = "Success.";
                    break;
                default:
                    response[0] = "Unknown.";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace(System.out);
            response[0] = "Fail.";
            return response;
        }

        return response;
    }
}
