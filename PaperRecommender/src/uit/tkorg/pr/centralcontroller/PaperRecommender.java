package uit.tkorg.pr.centralcontroller;

import ir.vsr.HashMapVector;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.constant.PRConstant;
import uit.tkorg.pr.dataimex.MASDataset1;
import uit.tkorg.pr.dataimex.MahoutFile;
import uit.tkorg.pr.dataimex.NUSDataset1;
import uit.tkorg.pr.dataimex.PRGeneralFile;
import uit.tkorg.pr.datapreparation.cbf.AuthorFVComputation;
import uit.tkorg.pr.datapreparation.cbf.PaperFVComputation;
import uit.tkorg.pr.datapreparation.cf.CFRatingMatrixComputation;
import uit.tkorg.pr.evaluation.Evaluator;
import uit.tkorg.pr.method.cbf.FeatureVectorSimilarity;
import uit.tkorg.pr.method.cf.KNNCF;
import uit.tkorg.pr.method.cf.SVDCF;
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

    public static void main(String[] args) {
        try {
            recommendationFlowController(3, 0,
                    PRConstant.FOLDER_NUS_DATASET1,
                    PRConstant.FOLDER_NUS_DATASET2,
                    PRConstant.FOLDER_MAS_DATASET1 + "[Training] Paper_Before_2006.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "[Training] Paper_Cite_Paper_Before_2006.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "[Testing] 1000Authors.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "[Testing] Ground_Truth_2006_2008.csv",
//                    PRConstant.FOLDER_MAS_DATASET1 + "[Testing] Ground_Truth_2006_2008_New_Citation.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "[Training] Author_Paper_Before_2006.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "[Training] Author_Cite_Paper_Before_2006.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "Text",
                    PRConstant.FOLDER_MAS_DATASET1 + "PreProcessedPaper",
                    PRConstant.FOLDER_MAS_DATASET1 + "Sequence",
                    PRConstant.FOLDER_MAS_DATASET1 + "Vector",
                    PRConstant.FOLDER_MAS_DATASET1 + "MahoutCF",
                    "EvaluationResult\\EvaluationResult_Maintain_OldCitation.xls",
                    2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param DatasetToUse : 1: NUS Dataset 1, 2: NUS Dataset 2, 3: MAS Dataset.
     * @param DatasetByResearcherType 0: Both, 1: Junior, 2: Senior.
     * @param fileNamePapers
     * @param fileNamePaperCitePaper
     * @param fileNameAuthorTestSet
     * @param fileNameGroundTruth
     * @param fileNameAuthorship
     * @param fileNameAuthorCitePaper
     * @param dirPapers
     * @param dirPreProcessedPaper
     * @param sequenceDir
     * @param vectorDir
     * @param MahoutCFDir
     * @param fileNameEvaluationResult
     * @param recommendationMethod: 1: CBF, 2: CF, 3: Hybrid.
     * @throws Exception 
     */
    public static void recommendationFlowController(int DatasetToUse, int DatasetByResearcherType,
            String NUSDataset1Dir, String NUSDataset2Dir,
            String fileNamePapers, String fileNamePaperCitePaper, String fileNameAuthorTestSet,
            String fileNameGroundTruth, String fileNameAuthorship, String fileNameAuthorCitePaper,
            String dirPapers, String dirPreProcessedPaper, String sequenceDir, String vectorDir,
            String MahoutCFDir, String fileNameEvaluationResult,
            int recommendationMethod) throws Exception {

        System.out.println("Begin recommendation flow for Dataset " + DatasetToUse + " with recommendation method " + recommendationMethod + " ...");
        long startRecommendationFlowTime = System.nanoTime();
        long startTime;
        long estimatedTime;
        
        int topNRecommend = 100;
        String datasetName = null;
        String algorithmName = null;
        
        HashMap<String, Author> authorTestSet = new HashMap<>();
        HashMap<String, Paper> papers = new HashMap<>();
        HashSet<String> paperIdsOfAuthorTestSet = new HashSet<>();
        HashSet<String> paperIdsTestSet = new HashSet<>();
        
        //<editor-fold defaultstate="collapsed" desc="Read and Prepare Data (TFIDF)">
        if (DatasetToUse == 1) {
            datasetName = "NUS Dataset 1";
            if (DatasetByResearcherType == 1) {
                datasetName += " Junior";
            } else if (DatasetByResearcherType == 2) {
                datasetName += " Senior";
            }
            fileNameEvaluationResult = PRConstant.FOLDER_NUS_DATASET1 + "\\" + fileNameEvaluationResult;
            // Read authors and their papers from data folder.
            authorTestSet = NUSDataset1.buildListOfAuthors(NUSDataset1Dir, DatasetByResearcherType);
            // Read papers (test set) from data folder.
            papers = NUSDataset1.buildListOfPapers(NUSDataset1Dir);
            // paper id of Test set (597 papers)
            paperIdsTestSet.addAll(papers.keySet());
            // extract papers from authors and put into the common paper map.
            papers.putAll(AuthorFVComputation.getPapersFromAuthors(authorTestSet));
            // paper id of authors.
            paperIdsOfAuthorTestSet = AuthorFVComputation.getPaperIdsOfAuthors(authorTestSet);
        } else if (DatasetToUse == 2) {
            datasetName = "NUS Dataset 2";
            // Not yet implement.
        } else if (DatasetToUse == 3) {
            datasetName = "MAS Dataset";
            fileNameEvaluationResult = PRConstant.FOLDER_MAS_DATASET1 + fileNameEvaluationResult;
            // Step 1: read list 1000 authors for test set.
            System.out.println("Begin reading author test set...");
            startTime = System.nanoTime();
            authorTestSet = MASDataset1.readAuthorListTestSet(fileNameAuthorTestSet, fileNameGroundTruth, fileNameAuthorship);
            estimatedTime = System.nanoTime() - startTime;
            System.out.println("Reading author test set elapsed time: " + estimatedTime / 1000000000 + " seconds");
            System.out.println("End reading author test set.");
            // When method is CF, do not read paper content.
            if (recommendationMethod != 2) {
                // Step 2:
                // - Read content of papers from [Training] Paper_Before_2006.csv
                // - Store metadata of all papers into HashMap<String, Paper> papers
                System.out.println("Begin reading paper list...");
                startTime = System.nanoTime();
                papers = MASDataset1.readPaperList(fileNamePapers, fileNamePaperCitePaper);
                estimatedTime = System.nanoTime() - startTime;
                System.out.println("Reading paper list elapsed time: " + estimatedTime / 1000000000 + " seconds");
                System.out.println("End reading paper list.");
                // Step 3: 
                // Compute TF-IDF for MAS papers.
    //            PaperFVComputation.computeTFIDFFromPaperAbstract(papers, dirPapers, dirPreProcessedPaper, sequenceDir, vectorDir);
                PaperFVComputation.readTFIDFFromMahoutFile(papers, vectorDir);
                // Clear no longer in use objects.
                // Always clear abstract.
                PaperFVComputation.clearPaperAbstract(papers);
                // Step 4:
                // Get list of papers to process.
                paperIdsOfAuthorTestSet = AuthorFVComputation.getPaperIdsOfAuthors(authorTestSet);
                paperIdsTestSet = AuthorFVComputation.getPaperIdsTestSet(authorTestSet);
            }
        }
        //</editor-fold>

        // Recommendation.
        if (recommendationMethod == 1) {
            //<editor-fold defaultstate="collapsed" desc="CONTENT BASED METHOD">
            // parameters for CBF methods.
            int combiningSchemePaperOfAuthor = 3;
            int weightingSchemePaperOfAuthor = 1;
            int timeAwareScheme = 0;
            double gamma = 0.2;
            int combiningSchemePaperTestSet = 3;
            int weightingSchemePaperTestSet = 1;
            int similarityScheme = 0;
            double pruning = 0.2;
                    
            System.out.println("Begin CBF recommendation...");
            startTime = System.nanoTime();
            
            algorithmName = cbfRecommendation(authorTestSet, papers, paperIdsOfAuthorTestSet, paperIdsTestSet, 
                    topNRecommend, 
                    combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor, 
                    timeAwareScheme, gamma, 
                    combiningSchemePaperTestSet, weightingSchemePaperTestSet, similarityScheme,
                    pruning);

            estimatedTime = System.nanoTime() - startTime;
            System.out.println("CBF recommendation elapsed time: " + estimatedTime / 1000000000 + " seconds");
            System.out.println("End CBF recommendation.");
            //</editor-fold>
        } else if (recommendationMethod == 2) {
            //<editor-fold defaultstate="collapsed" desc="CF METHODS">
            // cf method: 1: KNN Pearson, 2: KNN Cosine, 3: KNN SVD
            int cfMethod = 3;
            System.out.println("Begin CF recommendation...");
            startTime = System.nanoTime();
            
            algorithmName = cfRecommendation(fileNameAuthorCitePaper, MahoutCFDir, cfMethod, 
                    authorTestSet, topNRecommend);
            
            estimatedTime = System.nanoTime() - startTime;
            System.out.println("CF recommendation elapsed time: " + estimatedTime / 1000000000 + " seconds");
            System.out.println("End CF recommendation.");
            //</editor-fold>
        }

        //<editor-fold defaultstate="collapsed" desc="EVALUATION">
        System.out.println("Begin evaluating...");
        startTime = System.nanoTime();

        evaluation(datasetName, algorithmName, startRecommendationFlowTime, authorTestSet, fileNameEvaluationResult);

        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Evaluating elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End evaluating.");
        //</editor-fold>
        
        long estimatedRecommendationFlowTime = System.nanoTime() - startRecommendationFlowTime;
        System.out.println("Recommendation elapsed time: " + estimatedRecommendationFlowTime / 1000000000 + " seconds");
        System.out.println("End recommendation flow.");
    }
    
    public static String cbfRecommendation(HashMap<String, Author> authorTestSet, 
            HashMap<String, Paper> papers,
            HashSet<String> paperIdsOfAuthorTestSet,
            HashSet<String> paperIdsTestSet,
            int topNRecommend,
            int combiningSchemePaperOfAuthor, int weightingSchemePaperOfAuthor,
            int timeAwareScheme, double gamma,
            int combiningSchemePaperTestSet, int weightingSchemePaperTestSet,
            int similarityScheme,
            double pruning) throws Exception {
        
        String algorithmName = "CBF with FV_PaperOfAuthor(" 
                + combiningSchemePaperOfAuthor + "," + weightingSchemePaperOfAuthor + ")"
                + ", FV_Authors("
                + timeAwareScheme + "," + gamma + ")"
                + ", FV_PaperTestSet("
                + combiningSchemePaperTestSet + "," + weightingSchemePaperTestSet + ")"
                + "pruned by " + pruning;

        long startTime;
        long estimatedTime;
        
        // Step 1: compute feature vector for those all 1000 authors.
        System.out.println("Begin computing authors FV...");
        startTime = System.nanoTime();
        PaperFVComputation.computeFeatureVectorForAllPapers(papers, paperIdsOfAuthorTestSet, 
                combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor, pruning);
        AuthorFVComputation.computeFVForAllAuthors(authorTestSet, papers, timeAwareScheme, gamma);
        // Clear no longer in use objects.
        PaperFVComputation.clearFV(papers);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Computing authors FV elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End computing authors FV.");

        // Step 2: Aggregating feature vectors for all papers and
        // put the result into HashMap<String, Paper> papers (model)
        // (papers, 0, 0): baseline
        System.out.println("Begin computing FV for all papers...");
        startTime = System.nanoTime();
        PaperFVComputation.computeFeatureVectorForAllPapers(papers, paperIdsTestSet, 
                combiningSchemePaperTestSet, weightingSchemePaperTestSet, pruning);
        HashMap<String, Paper> paperTestSet = PaperFVComputation.extractPapers(papers, paperIdsTestSet);
        // Clear no longer in use objects.
        papers = null;
        PaperFVComputation.clearTFIDF(paperTestSet);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Computing FV for all papers elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End computing FV for all papers.");

        // Step 3: generate recommended papers list.
        System.out.println("Begin CBF Recommending...");
        startTime = System.nanoTime();
        FeatureVectorSimilarity.generateRecommendationForAllAuthors(authorTestSet, paperTestSet, 
                similarityScheme, topNRecommend);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("CBF Recommending elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End CBF Recommending.");
        
        return algorithmName;
    }
    
    /**
     * 
     * @param fileNameAuthorCitePaper
     * @param MahoutCFDir
     * @param cfMethod: 1: KNN Pearson, 2: KNN Cosine, 3: KNN SVD
     * @param authorTestSet
     * @param topNRecommend
     * @throws Exception 
     */
    public static String cfRecommendation(String fileNameAuthorCitePaper, String MahoutCFDir, 
            int cfMethod,
            HashMap<String, Author> authorTestSet, int topNRecommend) throws Exception {

        String algorithmName = null;
        
        // Prepare CF matrix.
        String MahoutCFFileOriginalFile = MahoutCFDir + "\\CFRatingMatrixOriginal.txt";
        cfPrepareMatrix(fileNameAuthorCitePaper, MahoutCFFileOriginalFile);
        
        // Predict ratings.
        if ((cfMethod == 1) || (cfMethod == 2)) {
            // KNN.
            // k neighbors.
            int k = 8;
            if (cfMethod == 1) {
                // kNNCF co-pearson.
                algorithmName = "CF KNN Pearson " + "k" + k;
            } else if (cfMethod == 2) {
                // kNNCF cosine.
                algorithmName = "CF KNN Cosine " + "k" + k;
            }
            System.out.println("Begin KNN");
            cfKNN(MahoutCFDir, MahoutCFFileOriginalFile, cfMethod, authorTestSet, topNRecommend, k);
            System.out.println("End KNN");
        } else if (cfMethod == 3) {
            // SVD ALSWRFactorizer.
            // f features, normalize by l, i iterations.
            int f = 5;
            double l = 0.01;
            int i = 1;
            algorithmName = "CF SVD ALSWRFactorizer " + "n" + topNRecommend + "f" + f + "l" + l + "i" + i;
            // Recommend for authors in author test set.
            System.out.println("Begin SVD Recommend");
            cfSVD(MahoutCFDir, MahoutCFFileOriginalFile, authorTestSet, topNRecommend, f, l, i);
            System.out.println("End SVD Recommend");
        }
        
        return algorithmName;
    }
    

    public static void cfPrepareMatrix(String fileNameAuthorCitePaper, String MahoutCFFileOriginalFile) throws Exception {

        // Read Raw rating matrix
        System.out.println("Begin Reading raw rating matrix");
        HashMap<String, HashMap<String, Double>> authorPaperRating = MASDataset1.readAuthorCitePaperMatrix(fileNameAuthorCitePaper);
        System.out.println("End Reading raw rating matrix");

        // Normalize
        System.out.println("Begin Normalize reating values in Citation Matrix");
        CFRatingMatrixComputation.normalizeAuthorRatingVector(authorPaperRating);
        System.out.println("End Normalize reating values in Citation Matrix");

        // Write to Mahout file
        System.out.println("Begin writeCFRatingToMahoutFormatFile");
        CFRatingMatrixComputation.writeCFRatingToMahoutFormatFile(authorPaperRating, MahoutCFFileOriginalFile);
        System.out.println("End writeCFRatingToMahoutFormatFile");
    }
    
    /**
     * 
     * @param MahoutCFDir
     * @param MahoutCFFileOriginalFile
     * @param similarityMethod: 1: Pearson, 2: Cosine.
     * @param authorTestSet
     * @param topNRecommend
     * @param k
     * @throws Exception 
     */
    public static void cfKNN(String MahoutCFDir, String MahoutCFFileOriginalFile, int similarityMethod,
            HashMap<String, Author> authorTestSet, int topNRecommend, int k) throws Exception {

        // Predict ratings by kNNCF.
        String MahoutCFRatingMatrixPredictionFile = MahoutCFDir + "\\CFRatingMatrixPredictionByCoPearson" + "k" + k + "n" + topNRecommend + ".txt";

        // Recommend for all author in matrix.
//        KNNCF.CoPearsonRecommend(MahoutCFFileOriginalFile, k, n, MahoutCFRatingMatrixPredictionFile);

        // Recommend for authors in author test set.
        if (similarityMethod == 1) {
            KNNCF.CoPearsonRecommendToAuthorList(MahoutCFFileOriginalFile, k, topNRecommend, authorTestSet, MahoutCFRatingMatrixPredictionFile);
        } else if (similarityMethod == 2) {
            KNNCF.CosineRecommendToAuthorList(MahoutCFFileOriginalFile, k, topNRecommend, authorTestSet, MahoutCFRatingMatrixPredictionFile);
        }
        
        // Read Recommendation for 1000 authors, put it into authorTestSetList.
        MahoutFile.readMahoutCFRating(MahoutCFRatingMatrixPredictionFile, authorTestSet);
    }
    
    public static void cfSVD(String MahoutCFDir, String MahoutCFFileOriginalFile, 
            HashMap<String, Author> authorTestSet, int topNRecommend, int f, double l, int i) throws Exception {

        // Predict ratings by SVD.
        String MahoutCFRatingMatrixPredictionFile = MahoutCFDir + "\\CFRatingMatrixPredictionBySVD" + "n" + topNRecommend + "f" + f + "l" + l + "i" + i + ".txt";

        // Recommend for authors in author test set.
        SVDCF.SVDRecommendationToAuthorList(MahoutCFFileOriginalFile, topNRecommend, f, l, i, authorTestSet, MahoutCFRatingMatrixPredictionFile);

        // Read Recommendation for 1000 authors, put it into authorTestSetList.
        MahoutFile.readMahoutCFRating(MahoutCFRatingMatrixPredictionFile, authorTestSet);
    }
    
    public static void evaluation(String datasetName, String algorithmName, long startRecommendationFlowTime,
            HashMap<String, Author> authorTestSet, String fileNameEvaluationResult) throws Exception {

        // Compute evaluation index.
        double precision10 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 10);
        double precision20 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 20);
        double precision30 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 30);
        double precision40 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 40);
        double precision50 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 50);
        double recall50 = Evaluator.computeMeanRecallTopN(authorTestSet, 50);
        double recall100 = Evaluator.computeMeanRecallTopN(authorTestSet, 100);
        double f1 = Evaluator.computeMeanFMeasure(authorTestSet, 1);
        double map10 = Evaluator.computeMAP(authorTestSet, 10);
        double map20 = Evaluator.computeMAP(authorTestSet, 20);
        double map30 = Evaluator.computeMAP(authorTestSet, 30);
        double map40 = Evaluator.computeMAP(authorTestSet, 40);
        double map50 = Evaluator.computeMAP(authorTestSet, 50);
        double ndcg5 = Evaluator.computeMeanNDCG(authorTestSet, 5);
        double ndcg10 = Evaluator.computeMeanNDCG(authorTestSet, 10);
        double mrr = Evaluator.computeMRR(authorTestSet);

        long estimatedRecommendationFlowTime = System.nanoTime() - startRecommendationFlowTime;

        StringBuilder evaluationResult = new StringBuilder();
        evaluationResult.append("Time Stamp").append("\t")
                .append("Dataset").append("\t")
                .append("Algorithm").append("\t")
                .append("Running time in second").append("\t")
                .append("P@10").append("\t")
                .append("P@20").append("\t")
                .append("P@30").append("\t")
                .append("P@40").append("\t")
                .append("P@50").append("\t")
                .append("R@50").append("\t")
                .append("R@100").append("\t")
                .append("F1").append("\t")
                .append("MAP@10").append("\t")
                .append("MAP@20").append("\t")
                .append("MAP@30").append("\t")
                .append("MAP@40").append("\t")
                .append("MAP@50").append("\t")
                .append("NDCG@5").append("\t")
                .append("NDCG@10").append("\t")
                .append("MRR")
                .append("\r\n")
                .append(new Date(System.currentTimeMillis()).toString()).append("\t")
                .append(datasetName).append("\t")
                .append(algorithmName).append("\t")
                .append(estimatedRecommendationFlowTime / 1000000000).append("\t")
                .append(precision10).append("\t")
                .append(precision20).append("\t")
                .append(precision30).append("\t")
                .append(precision40).append("\t")
                .append(precision50).append("\t")
                .append(recall50).append("\t")
                .append(recall100).append("\t")
                .append(f1).append("\t")
                .append(map10).append("\t")
                .append(map20).append("\t")
                .append(map30).append("\t")
                .append(map40).append("\t")
                .append(map50).append("\t")
                .append(ndcg5).append("\t")
                .append(ndcg10).append("\t")
                .append(mrr)
                .append("\r\n");
        FileUtils.writeStringToFile(new File(fileNameEvaluationResult), evaluationResult.toString(), "UTF8", true);
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
        HashMap<String, Paper> papersOfAuthors = new HashMap<>();

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
                    authors = NUSDataset1.buildListOfAuthors(Dataset1Folder, 0);
                    papersOfAuthors = AuthorFVComputation.getPapersFromAuthors(authors);
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
                    papersOfAuthors = AuthorFVComputation.getPapersFromAuthors(authors);
                    response[0] = "Success.";
                    break;

                // Dataset 1: data preparation.
                case "Paper FV linear":
                    PaperFVComputation.computeFeatureVectorForAllPapers(papers, null, 3, 0, 0.2);
                    response[0] = "Success.";
                    break;
                case "Paper FV cosine":
                    PaperFVComputation.computeFeatureVectorForAllPapers(papers, null, 3, 1, 0.2);
                    response[0] = "Success.";
                    break;
                case "Paper FV RPY":
                    PaperFVComputation.computeFeatureVectorForAllPapers(papers, null, 3, 2, 0.2);
                    response[0] = "Success.";
                    break;
                case "Author FV linear":
                    PaperFVComputation.computeFeatureVectorForAllPapers(papersOfAuthors, null, 3, 0, 0.2);
                    AuthorFVComputation.computeFVForAllAuthors(authors, papersOfAuthors, 0, 0);
                    response[0] = "Success.";
                    break;
                case "Author FV cosine":
                    PaperFVComputation.computeFeatureVectorForAllPapers(papersOfAuthors, null, 3, 1, 0.2);
                    AuthorFVComputation.computeFVForAllAuthors(authors, papersOfAuthors, 0, 0);
                    response[0] = "Success.";
                    break;
                case "Author FV RPY":
                    PaperFVComputation.computeFeatureVectorForAllPapers(papersOfAuthors, null, 3, 2, 0.2);
                    AuthorFVComputation.computeFVForAllAuthors(authors, papersOfAuthors, 0, 0);
                    response[0] = "Success.";
                    break;
                case "Recommend":
                    FeatureVectorSimilarity.generateRecommendationForAllAuthors(authors, papers, 0, 10);
                    response[0] = "Success.";
                    break;
                case "NDCG5":
                    response[1] = String.valueOf(Evaluator.computeMeanNDCG(authors, 5));
                    response[0] = "Success.";
                    break;
                case "NDCG10":
                    response[1] = String.valueOf(Evaluator.computeMeanNDCG(authors, 10));
                    response[0] = "Success.";
                    break;
                case "MRR":
                    response[1] = String.valueOf(Evaluator.computeMRR(authors));
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
