package uit.tkorg.pr.centralcontroller;

import ir.vsr.HashMapVector;
import java.util.HashMap;
import uit.tkorg.pr.constant.PRConstant;
import uit.tkorg.pr.dataimex.MASDataset1;
import uit.tkorg.pr.dataimex.MahoutFile;
import uit.tkorg.pr.dataimex.NUSDataset1;
import uit.tkorg.pr.dataimex.PRGeneralFile;
import uit.tkorg.pr.datapreparation.cbf.AuthorFVComputation;
import uit.tkorg.pr.datapreparation.cbf.PaperFVComputation;
import uit.tkorg.pr.evaluation.Evaluator;
import uit.tkorg.pr.method.cbf.CBFRecommender;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.BinaryFileUtility;
import uit.tkorg.utility.textvectorization.TextVectorizationByMahoutTerminalUtility;

/**
 *
 * @author THNghiep 
 * Central controller. Main entry class used for testing. Also
 * control all traffic from gui.
 */
public class PaperRecommender {

    public static void main(String[] args) { 
        try {
            recommendationFlowController(PRConstant.FOLDER_MAS_DATASET1 + "[Training] Paper_Before_2006.csv", 
                    PRConstant.FOLDER_MAS_DATASET1 + "[Training] Paper_Cite_Paper_Before_2006.csv", 
                    PRConstant.FOLDER_MAS_DATASET1 + "[Training] 1000Authors.csv", 
                    PRConstant.FOLDER_MAS_DATASET1 + "[Validation] Ground_Truth_2006_2008.csv", 
                    PRConstant.FOLDER_MAS_DATASET1 + "[Training] Author_Paper_Before_2006.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "Text", 
                    PRConstant.FOLDER_MAS_DATASET1 + "PreProcessedPaper", 
                    PRConstant.FOLDER_MAS_DATASET1 + "Sequence", 
                    PRConstant.FOLDER_MAS_DATASET1 + "Vector");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void recommendationFlowController(String fileNamePapers, 
            String fileNamePaperCitePaper, String fileNameAuthorTestSet, 
            String fileNameGroundTruth, String fileNameAuthorship, String dirPapers, 
            String dirPreProcessedPaper, String sequenceDir, String vectorDir) throws Exception {
        // Step 1: 
        // - Read content of papers from [Training] Paper_Before_2006.csv
        // - Store metadata of all papers into HashMap<String, Paper> papers
        System.out.println("Begin reading paper list...");
        long startTime = System.nanoTime();
        HashMap<String, Paper> papers = MASDataset1.readPaperList(fileNamePapers, fileNamePaperCitePaper);
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Reading paper list elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End reading paper list.");
        
        // Step 2: 
        // - Writting abstract of all papers to text files. One file for each paper in 'dirPapers' directory.
        // - Clear abstract of all papers.
        System.out.println("Begin writing abstract to file...");
        startTime = System.nanoTime();
//        PRGeneralFile.writePaperAbstractToTextFile(papers, dirPapers);
        PaperFVComputation.clearPaperAbstract(papers);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Writing abstract to file elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End writing abstract to file.");
        
        // Step 3: Preprocessing content of all papers. Remove stop words and stemming
        System.out.println("Begin removing stopword and stemming...");
        startTime = System.nanoTime();
//        TextPreprocessUtility.parallelProcess(dirPapers, dirPreProcessedPaper, true, true);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Removing stopword and stemming elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End removing stopword and stemming.");
        
        // Step 4: tf-idf. Output of this process is vectors of papers stored in a Mahout's binary file 
        System.out.println("Begin vectorizing...");
        startTime = System.nanoTime();
//        TextVectorizationByMahoutTerminalUtility.textVectorizeFiles(dirPreProcessedPaper, sequenceDir, vectorDir);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Vectorizing elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End vectorizing.");
        
        // Step 5: Read vectors of all papers store in 
        // - HashMap<Integer, String> dictMap: Dictionary of the whole collection.
        // - HashMap<String, HashMapVector> vectorizedDocuments: <PaperID, Vector TF*IDF of PaperID>
        System.out.println("Begin reading vector...");
        startTime = System.nanoTime();
//        HashMap<Integer, String> dictMap = MahoutFile.readMahoutDictionaryFiles(vectorDir);
        HashMap<String, HashMapVector> vectorizedDocuments = MahoutFile.readMahoutVectorFiles(vectorDir);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Reading vector elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End reading vector.");
        
        // Step 6: put TFIDF vectors of all paper (vectorizedDocuments) 
        // into HashMap<String, Paper> papers (model)
        PaperFVComputation.setTFIDFVectorForAllPapers(papers, vectorizedDocuments);
        
        // Step 7: calculating feature vector for all papers and 
        // put the result into HashMap<String, Paper> papers (model)
        // (papers, 0, 0): baseline
        PaperFVComputation.computeFeatureVectorForAllPapers(papers, 0, 0);
        
        // Step 8: read list 1000 authors.
        HashMap<String, Author> authorTestSet = MASDataset1.readAuthorListTestSet(fileNameAuthorTestSet, fileNameGroundTruth, fileNameAuthorship);

        // Step 9: compute feature vector for those all 1000 authors.
        AuthorFVComputation.computeFVForAllAuthorsWithSeparatedPaperList(authorTestSet, papers, 0, 0, false, 0, 0);

        // Step 10: generate recommended papers list.
        CBFRecommender.generateRecommendationForAllAuthors(authorTestSet, papers, 0, 10);
        
        // Step 11: compute evaluation index: ndcg, mrr.
        double ndcg5 = Evaluator.NDCG(authorTestSet, 5);
        double ndcg10 = Evaluator.NDCG(authorTestSet, 10);
        double mrr = Evaluator.MRR(authorTestSet);
    }

    /**
     * This method handles all request from gui.
     *
     * @param request
     * @param param
     * @return response: result of request after processing.
     */
    public String[] guiRequestHandler(String request, String param) {

        HashMap<String, Paper> papers = new HashMap<>();
        HashMap<String, Author> authors = new HashMap<>();

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
                    PaperFVComputation.computeFeatureVectorForAllPapers(papers, 3, 0);
                    response[0] = "Success.";
                    break;
                case "Paper FV cosine":
                    PaperFVComputation.computeFeatureVectorForAllPapers(papers, 3, 1);
                    response[0] = "Success.";
                    break;
                case "Paper FV RPY":
                    PaperFVComputation.computeFeatureVectorForAllPapers(papers, 3, 2);
                    response[0] = "Success.";
                    break;
                case "Author FV linear":
                    AuthorFVComputation.computeFVForAllAuthorsWithAttachedPaperList(authors, 3, 0, 0, 0);
                    response[0] = "Success.";
                    break;
                case "Author FV cosine":
                    AuthorFVComputation.computeFVForAllAuthorsWithAttachedPaperList(authors, 3, 1, 0, 0);
                    response[0] = "Success.";
                    break;
                case "Author FV RPY":
                    AuthorFVComputation.computeFVForAllAuthorsWithAttachedPaperList(authors, 3, 2, 0, 0);
                    response[0] = "Success.";
                    break;
                case "Recommend":
                    CBFRecommender.generateRecommendationForAllAuthors(authors, papers, 0, 10);
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
