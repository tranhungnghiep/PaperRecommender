package uit.tkorg.pr.controller;

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
import uit.tkorg.pr.datapreparation.CBFAuthorFVComputation;
import uit.tkorg.pr.datapreparation.CBFPaperFVComputation;
import uit.tkorg.pr.datapreparation.CFRatingMatrixComputation;
import uit.tkorg.pr.datapreparation.MLDataPreparation;
import uit.tkorg.pr.datapreparation.PaperQualityComputation;
import uit.tkorg.pr.datapreparation.TrustDataModelPreparation;
import uit.tkorg.pr.evaluation.ErrorAnalysis;
import uit.tkorg.pr.evaluation.Evaluator;
import uit.tkorg.pr.method.cbf.FeatureVectorSimilarity;
import uit.tkorg.pr.method.cf.CF;
import uit.tkorg.pr.method.cf.KNNCF;
import uit.tkorg.pr.method.cf.SVDCF;
import uit.tkorg.pr.method.hybrid.CBFCF;
import uit.tkorg.pr.method.hybrid.TrustHybrid;
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
public class PRCentralController {

    //<editor-fold defaultstate="collapsed" desc="Parameters for PRCentralController">
    int _DatasetToUse; // 1: NUS Dataset 1, 2: NUS Dataset 2, 3: MAS Dataset.
    int _DatasetByResearcherType; // 0: Both, 1: Junior, 2: Senior.
    String _NUSDataset1Dir;
    String _NUSDataset2Dir;
    String _fileNamePapers;
    String _fileNamePaperCitePaper;
    String _fileNameAuthorTestSet;
    String _fileNameGroundTruth;
    String _fileNameAuthorship;
    String _fileNameAuthorCitePaper;
    String _dirPapers;
    String _dirPreProcessedPaper;
    String _sequenceDir;
    String _vectorDir;
    String _MahoutCFDir;
    String _fileNameEvaluationResult;
    int _recommendationMethod;
    //</editor-fold>

    public PRCentralController(){
    }
    
    public PRCentralController(int DatasetToUse, int DatasetByResearcherType,
            String NUSDataset1Dir, String NUSDataset2Dir,
            String fileNamePapers, String fileNamePaperCitePaper, String fileNameAuthorTestSet,
            String fileNameGroundTruth, String fileNameAuthorship, String fileNameAuthorCitePaper,
            String dirPapers, String dirPreProcessedPaper, String sequenceDir, String vectorDir,
            String MahoutCFDir, String fileNameEvaluationResult,
            int recommendationMethod) {
        
        _DatasetToUse = DatasetToUse;
        _DatasetByResearcherType = DatasetByResearcherType;
        _NUSDataset1Dir = NUSDataset1Dir;
        _NUSDataset2Dir = NUSDataset2Dir;
        _fileNamePapers = fileNamePapers;
        _fileNamePaperCitePaper = fileNamePaperCitePaper;
        _fileNameAuthorTestSet = fileNameAuthorTestSet;
        _fileNameGroundTruth = fileNameGroundTruth;
        _fileNameAuthorship = fileNameAuthorship;
        _fileNameAuthorCitePaper = fileNameAuthorCitePaper;
        _dirPapers = dirPapers;
        _dirPreProcessedPaper = dirPreProcessedPaper;
        _sequenceDir = sequenceDir;
        _vectorDir = vectorDir;
        _MahoutCFDir = MahoutCFDir;
        _fileNameEvaluationResult = fileNameEvaluationResult;
        _recommendationMethod = recommendationMethod;
    }

    public static void main(String[] args) {
        try {
            recommendationFlowController(3, 0,
                    PRConstant.FOLDER_NUS_DATASET1,
                    PRConstant.FOLDER_NUS_DATASET2,
                    // For CBF
                    PRConstant.FOLDER_MAS_DATASET1 + "T0-T1\\[Training] Paper_Before_2006.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "T0-T1\\[Training] Paper_Cite_Paper_Before_2006.csv",
                    // Testing data
                    PRConstant.FOLDER_MAS_DATASET1 + "T0-T1\\[Testing] 1000Authors.csv",
                    //PRConstant.FOLDER_MAS_DATASET1 + "T0-T1\\[Testing] Ground_Truth_2006_2008.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "T0-T1\\[Testing] Ground_Truth_2006_2008_New_Citation.csv",
                    // Author Profile
                    PRConstant.FOLDER_MAS_DATASET1 + "T0-T1\\[Training] Author_Paper_Before_2006.csv",
                    // For CF
                    PRConstant.FOLDER_MAS_DATASET1 + "T0-T1\\[Training] Author_Cite_Paper_Before_2006.csv", 
                    // Mahout
                    PRConstant.FOLDER_MAS_DATASET1 + "T0-T1\\TF-IDF\\Text",
                    PRConstant.FOLDER_MAS_DATASET1 + "T0-T1\\TF-IDF\\PreProcessedPaper",
                    PRConstant.FOLDER_MAS_DATASET1 + "T0-T1\\TF-IDF\\Sequence",
                    PRConstant.FOLDER_MAS_DATASET1 + "T0-T1\\TF-IDF\\Vector",
                    PRConstant.FOLDER_MAS_DATASET1 + "T0-T1\\MahoutCF",
                    // Result
                    "EvaluationResult\\EvaluationResult_NewCitation.xls",
                    1);
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
     * @param recommendationMethod: 1: CBF, 2: CF, 3: CBF CF Hybrid Linear, 
     * 4: Trust, 5: Trust Hybrid, 10: ML Hybrid
     * Combination.
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

        int topNRecommend = 1000;
        String datasetName = null;
        String algorithmName = null;

        HashMap<String, Author> authorTestSet = new HashMap<>();
        HashMap<String, Paper> papers = new HashMap<>();
        HashSet<String> paperIdsOfAuthorTestSet = new HashSet<>();
        HashSet<String> paperIdsInTestSet = new HashSet<>();

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
            paperIdsInTestSet.addAll(papers.keySet());
            // extract papers from authors and put into the common paper map.
            papers.putAll(CBFAuthorFVComputation.getPapersFromAuthors(authorTestSet));
            // paper id of authors.
            paperIdsOfAuthorTestSet = CBFAuthorFVComputation.getPaperIdsOfAuthors(authorTestSet);
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
                CBFPaperFVComputation.computeTFIDFFromPaperAbstract(papers, dirPapers, dirPreProcessedPaper, sequenceDir, vectorDir);
                CBFPaperFVComputation.readTFIDFFromMahoutFile(papers, vectorDir);
                // Clear no longer in use objects.
                // Always clear abstract.
                CBFPaperFVComputation.clearPaperAbstract(papers);
            }
            // Step 4:
            // Get list of papers to process.
            paperIdsOfAuthorTestSet = CBFAuthorFVComputation.getPaperIdsOfAuthors(authorTestSet);
            paperIdsInTestSet = CBFAuthorFVComputation.getPaperIdsTestSet(authorTestSet);
        }
        //</editor-fold>

        // parameters for CBF methods.
        int combiningSchemePaperOfAuthor = 0;
        int weightingSchemePaperOfAuthor = 0;
        int timeAwareScheme = 1;
        double gamma = 0.3;
        int combiningSchemePaperTestSet = 0;
        int weightingSchemePaperTestSet = 0;
        int similarityScheme = 0;
        double pruning = 0.0;

        // parameters for cf method: 1: KNN Pearson, 2: KNN Cosine, 3: SVD
        int cfMethod = 1;
        
        // parameters for hybrid method
        // combinationScheme: 1: combine linear, 2: combine based on confidence, 
        // 3: combine based on confidence and linear
        int combinationScheme;
        // weighting when combine linear.
        float alpha;
        
        // parameters for trust based method
        // howToTrustAuthor: 1: combine linear citation author and coauthor, 2: meta trust citation author of coauthor
        // 3: meta trust citation author of citation author.
        int howToTrustAuthor;
        // howToTrustPaper: 1: average trust value of authors, 2: max trust value of authors.
        int howToTrustPaper;

        // Recommendation.
        if (recommendationMethod == 1) {
            //<editor-fold defaultstate="collapsed" desc="CONTENT BASED METHOD">
            System.out.println("Begin CBF recommendation...");
            startTime = System.nanoTime();

            algorithmName = CBFController.cbfComputeRecommendingScore(authorTestSet, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor,
                    timeAwareScheme, gamma,
                    combiningSchemePaperTestSet, weightingSchemePaperTestSet, similarityScheme,
                    pruning);
            FeatureVectorSimilarity.generateRecommendationForAuthorList(authorTestSet, topNRecommend);

            estimatedTime = System.nanoTime() - startTime;
            System.out.println("CBF recommendation elapsed time: " + estimatedTime / 1000000000 + " seconds");
            System.out.println("End CBF recommendation.");
            //</editor-fold>
        } else if (recommendationMethod == 2) {
            //<editor-fold defaultstate="collapsed" desc="CF METHODS">
            System.out.println("Begin CF recommendation...");
            startTime = System.nanoTime();

            algorithmName = CFController.cfComputeRecommendingScore(fileNameAuthorCitePaper, MahoutCFDir, cfMethod,
                    authorTestSet, paperIdsInTestSet);
            CF.cfRecommendToAuthorList(authorTestSet, topNRecommend);

            estimatedTime = System.nanoTime() - startTime;
            System.out.println("CF recommendation elapsed time: " + estimatedTime / 1000000000 + " seconds");
            System.out.println("End CF recommendation.");
            //</editor-fold>
        } else if (recommendationMethod == 3) {
            //<editor-fold defaultstate="collapsed" desc="LINEAR COMBINATION">
            CBFController.cbfComputeRecommendingScore(authorTestSet, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor,
                    timeAwareScheme, gamma,
                    combiningSchemePaperTestSet, weightingSchemePaperTestSet, similarityScheme,
                    pruning);
            CFController.cfComputeRecommendingScore(fileNameAuthorCitePaper, MahoutCFDir,
                    cfMethod, authorTestSet, paperIdsInTestSet);
            combinationScheme = 1;
            alpha = (float) 0.9;
            CBFCF.computeCBFCFCombinationAndPutIntoModelForAuthorList(authorTestSet, alpha, combinationScheme);
            CBFCF.cbfcfHybridRecommendToAuthorList(authorTestSet, topNRecommend);
            algorithmName = "CBF-CF LINEAR COMBINATION:"
                    + " combinationScheme = " + combinationScheme
                    + " alpha = " + alpha;
            //</editor-fold>
        } else if (recommendationMethod == 4) {
            //<editor-fold defaultstate="collapsed" desc="TRUST BASED">
            TrustDataModelPreparation.computeCoAuthorRSSHM(authorTestSet, fileNameAuthorship, fileNamePapers);
            HashMap<String, HashMap<String, Float>> referenceRSSNet = new HashMap<>();
            TrustDataModelPreparation.computeCitationAuthorRSSHM(authorTestSet, fileNameAuthorship, fileNamePaperCitePaper, referenceRSSNet);
            
            combinationScheme = 1;
            alpha = 0f;
            howToTrustAuthor = 1;
            howToTrustPaper = 2;
            
            if (howToTrustAuthor == 1) {
                TrustHybrid.computeTrustedAuthorHMLinearCombinationAndPutIntoModelForAuthorList(authorTestSet, alpha, combinationScheme);
            } else if (howToTrustAuthor == 2) {
                int metaTrustType = 1;
                TrustHybrid.computeMetaTrustedAuthorHMAndPutIntoModelForAuthorList(authorTestSet, referenceRSSNet, metaTrustType, alpha);
            } else if (howToTrustAuthor == 3) {
                int metaTrustType = 2;
                TrustHybrid.computeMetaTrustedAuthorHMAndPutIntoModelForAuthorList(authorTestSet, referenceRSSNet, metaTrustType, alpha);
            }
            
            TrustHybrid.computeTrustedPaperHMAndPutIntoModelForAuthorList(authorTestSet, howToTrustPaper);

            TrustHybrid.trustRecommendToAuthorList(authorTestSet, topNRecommend);
            algorithmName = "Trust Based Method:"
                    + " combinationScheme = " + combinationScheme 
                    + " alpha = " + alpha 
                    + " howToTrustAuthor = " + howToTrustAuthor 
                    + " howToTrustPaper = " + howToTrustPaper;
            //</editor-fold>
        } else if (recommendationMethod == 5) {
            //<editor-fold defaultstate="collapsed" desc="TRUST BASED LINEAR COMBINATION">           
            CBFController.cbfComputeRecommendingScore(authorTestSet, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor,
                    timeAwareScheme, gamma,
                    combiningSchemePaperTestSet, weightingSchemePaperTestSet, similarityScheme,
                    pruning);
            TrustDataModelPreparation.computeCoAuthorRSSHM(authorTestSet, fileNameAuthorship, fileNamePapers);
            HashMap<String, HashMap<String, Float>> referenceRSSNet = new HashMap<>();
            TrustDataModelPreparation.computeCitationAuthorRSSHM(authorTestSet, fileNameAuthorship, fileNamePaperCitePaper, referenceRSSNet);

            combinationScheme = 1;
            alpha = 0f;
            howToTrustAuthor = 1;
            howToTrustPaper = 2;
            
            if (howToTrustAuthor == 1) {
                TrustHybrid.computeTrustedAuthorHMLinearCombinationAndPutIntoModelForAuthorList(authorTestSet, alpha, combinationScheme);
            } else if (howToTrustAuthor == 2) {
                int metaTrustType = 1;
                TrustHybrid.computeMetaTrustedAuthorHMAndPutIntoModelForAuthorList(authorTestSet, referenceRSSNet, metaTrustType, alpha);
            } else if (howToTrustAuthor == 3) {
                int metaTrustType = 2;
                TrustHybrid.computeMetaTrustedAuthorHMAndPutIntoModelForAuthorList(authorTestSet, referenceRSSNet, metaTrustType, alpha);
            }
            
            TrustHybrid.computeTrustedPaperHMAndPutIntoModelForAuthorList(authorTestSet, howToTrustPaper);

            combinationScheme = 1;
            alpha = (float) 0.3;
            TrustHybrid.computeCBFTrustLinearCombinationAndPutIntoModelForAuthorList(authorTestSet, alpha, combinationScheme);

            TrustHybrid.trustHybridRecommendToAuthorList(authorTestSet, topNRecommend);
            algorithmName = "Trust Based combined with CBF:"
                    + " howToTrustAuthor = " + howToTrustAuthor
                    + " howToTrustPaper = " + howToTrustPaper
                    + " combinationScheme = " + combinationScheme 
                    + " alpha = " + alpha;
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

        //<editor-fold defaultstate="collapsed" desc="ERROR ANALYSIS">
        System.out.println("Begin error analysis...");
        startTime = System.nanoTime();

        String partFileNameWithDataset = PRConstant.FOLDER_MAS_DATASET1
                + "ErrorAnalysis\\Dataset" + DatasetToUse;
        String partFileNameWithMethod = " Method" + recommendationMethod
                + " Customed file name ending" + ".xls";

        // EachAuthorEvaluationResults
        String fileNameErrorAnalysis = partFileNameWithDataset
                + " EachAuthorEvaluationResults " 
                + partFileNameWithMethod;
        ErrorAnalysis.printEachAuthorEvaluationResults(authorTestSet, fileNameErrorAnalysis);

        int topNErrorAnalysis = 50;
        // FalseNegativeTopN
        fileNameErrorAnalysis = partFileNameWithDataset
                + " FalseNegative Top" + topNErrorAnalysis
                + partFileNameWithMethod;
        ErrorAnalysis.printFalseNegativeTopN(authorTestSet, fileNameErrorAnalysis, recommendationMethod, topNErrorAnalysis);

        // FalsePositveTopN
        fileNameErrorAnalysis = partFileNameWithDataset
                + " FalsePositive Top" + topNErrorAnalysis
                + partFileNameWithMethod;
        ErrorAnalysis.printFalsePositiveTopN(authorTestSet, fileNameErrorAnalysis, recommendationMethod, topNErrorAnalysis);

        // TruePositveTopN
        fileNameErrorAnalysis = partFileNameWithDataset
                + " TruePositive Top" + topNErrorAnalysis
                + partFileNameWithMethod;
        ErrorAnalysis.printTruePositiveTopN(authorTestSet, fileNameErrorAnalysis, recommendationMethod, topNErrorAnalysis);
        
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Error analysis elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End error analysis.");
        //</editor-fold>

        long estimatedRecommendationFlowTime = System.nanoTime() - startRecommendationFlowTime;
        System.out.println("Recommendation elapsed time: " + estimatedRecommendationFlowTime / 1000000000 + " seconds");
        System.out.println("End recommendation flow.");
    }

    public static void evaluation(String datasetName, String algorithmName, long startRecommendationFlowTime,
            HashMap<String, Author> authorTestSet, String fileNameEvaluationResult) throws Exception {

        // Compute evaluation index.
        double mp5 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 5);
        double mp10 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 10);
        double mp20 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 20);
        double mp30 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 30);
        double mp40 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 40);
        double mp50 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 50);
        double mr50 = Evaluator.computeMeanRecallTopN(authorTestSet, 50);
        double mr100 = Evaluator.computeMeanRecallTopN(authorTestSet, 100);
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
                .append("MP@5").append("\t")
                .append("MP@10").append("\t")
                .append("MP@20").append("\t")
                .append("MP@30").append("\t")
                .append("MP@40").append("\t")
                .append("MP@50").append("\t")
                .append("MR@50").append("\t")
                .append("MR@100").append("\t")
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
                .append(mp5).append("\t")
                .append(mp10).append("\t")
                .append(mp20).append("\t")
                .append(mp30).append("\t")
                .append(mp40).append("\t")
                .append(mp50).append("\t")
                .append(mr50).append("\t")
                .append(mr100).append("\t")
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
                    papersOfAuthors = CBFAuthorFVComputation.getPapersFromAuthors(authors);
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
                    papersOfAuthors = CBFAuthorFVComputation.getPapersFromAuthors(authors);
                    response[0] = "Success.";
                    break;

                // Dataset 1: data preparation.
                case "Paper FV linear":
                    CBFPaperFVComputation.computeFeatureVectorForAllPapers(papers, null, 3, 0, 0.2);
                    response[0] = "Success.";
                    break;
                case "Paper FV cosine":
                    CBFPaperFVComputation.computeFeatureVectorForAllPapers(papers, null, 3, 1, 0.2);
                    response[0] = "Success.";
                    break;
                case "Paper FV RPY":
                    CBFPaperFVComputation.computeFeatureVectorForAllPapers(papers, null, 3, 2, 0.2);
                    response[0] = "Success.";
                    break;
                case "Author FV linear":
                    CBFPaperFVComputation.computeFeatureVectorForAllPapers(papersOfAuthors, null, 3, 0, 0.2);
                    CBFAuthorFVComputation.computeFVForAllAuthors(authors, papersOfAuthors, 0, 0);
                    response[0] = "Success.";
                    break;
                case "Author FV cosine":
                    CBFPaperFVComputation.computeFeatureVectorForAllPapers(papersOfAuthors, null, 3, 1, 0.2);
                    CBFAuthorFVComputation.computeFVForAllAuthors(authors, papersOfAuthors, 0, 0);
                    response[0] = "Success.";
                    break;
                case "Author FV RPY":
                    CBFPaperFVComputation.computeFeatureVectorForAllPapers(papersOfAuthors, null, 3, 2, 0.2);
                    CBFAuthorFVComputation.computeFVForAllAuthors(authors, papersOfAuthors, 0, 0);
                    response[0] = "Success.";
                    break;
                case "Recommend":
                    FeatureVectorSimilarity.computeCBFSimAndPutIntoModelForAuthorList(authors, papers, 0);
                    FeatureVectorSimilarity.generateRecommendationForAuthorList(authors, 10);
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
