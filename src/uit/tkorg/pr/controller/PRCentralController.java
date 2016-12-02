package uit.tkorg.pr.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.constant.PRConstant;
import uit.tkorg.pr.dataimex.MASDataset1;
import uit.tkorg.pr.dataimex.NUSDataset1;
import uit.tkorg.pr.datapreparation.CBFAuthorFVComputation;
import uit.tkorg.pr.datapreparation.CBFPaperFVComputation;
import uit.tkorg.pr.datapreparation.TrustDataModelPreparation;
import uit.tkorg.pr.evaluation.ErrorAnalysis;
import uit.tkorg.pr.evaluation.Evaluator;
import uit.tkorg.pr.method.cbf.FeatureVectorSimilarity;
import static uit.tkorg.pr.method.cbf.FeatureVectorSimilarity.computeCBFSim;
import uit.tkorg.pr.method.cf.CF;
import uit.tkorg.pr.method.hybrid.CBFCF;
import uit.tkorg.pr.method.hybrid.TrustHybrid;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.BinaryFileUtility;

/**
 *
 * @author THNghiep Central controller. Main entry class used for testing. Also
 * control all traffic from gui.
 */
public class PRCentralController {

    public PRCentralController(){
    }
    
    public static void main(String[] args) {
        try {
            recommendationFlowController(3, 1,
                    PRConstant.FOLDER_NUS_DATASET1,
                    PRConstant.FOLDER_NUS_DATASET2,
                    PRConstant.FOLDER_MAS_DATASET,
                    // For CBF
                    PRConstant.FOLDER_MAS_DATASET + "PAPER_BEFORE_T2.csv",
                    PRConstant.FOLDER_MAS_DATASET + "PAPER_CITE_PAPER_BEFORE_T2.csv",
                    // Testing data
                    PRConstant.FOLDER_MAS_DATASET + "JUNIOR100_FILTER_V2_OLD.csv",
                    PRConstant.FOLDER_MAS_DATASET + "GROUND_TRUTH_JUNIOR100_T2_FILTER_V2.csv",
                    // Author Profile
                    PRConstant.FOLDER_MAS_DATASET + "AUTHOR_PAPER_BEFORE_T2.csv",
                    // For CF
                    PRConstant.FOLDER_MAS_DATASET + "AUTHOR_CITE_PAPER_BEFORE_T2.csv", 
                    // Mahout
                    PRConstant.FOLDER_MAS_DATASET + "TF-IDF_TitleAbstract\\Text",
                    PRConstant.FOLDER_MAS_DATASET + "TF-IDF_TitleAbstract\\PreProcessedPaper",
                    PRConstant.FOLDER_MAS_DATASET + "TF-IDF_TitleAbstract\\Sequence",
                    PRConstant.FOLDER_MAS_DATASET + "TF-IDF_TitleAbstract\\Vector",
                    PRConstant.FOLDER_MAS_DATASET + "MahoutCF",
                    // Result
                    "EvaluationResult\\EvaluationResult_PPPRSDataset_250515_FILTER_V2_OLD_TitleAbstract.xls",
                    // Method
                    1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param DatasetToUse : 1: NUS Dataset 1, 2: NUS Dataset 2, 3: MAS Dataset.
     * @param DatasetByResearcherType 0: Both, 1: Junior, 2: Senior.
     * @param NUSDataset1Dir
     * @param NUSDataset2Dir
     * @param MASDatasetDir
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
     * @param recommendationMethod: 
     * 1: CBF, 
     * 2: CF, 
     * 3: CBF-CF Linear, 
     * 4: Trust, 
     * 5: CBF-Trust Linear, 
     * 6: New CBF-Trust Hybrid V2 (get trust list then sort by cbf, or filter cbf by trust), 
     * 7: New CBF-Trust Hybrid V3 (get cbf list then sort by trust), 
     * 8: New CBF-CF Hybrid V2 (get cf list then sort by cbf), 
     * 9: New CBF-CF Hybrid V3 (get cbf list then sort by cf), 
     * 10: New CBF-Trust Hybrid V4 (get trust list then sort by cbf, fill short trust list by cbf), 
     * 100: ML Hybrid Combination.
     * @throws Exception
     */
    public static void recommendationFlowController(int DatasetToUse, int DatasetByResearcherType,
            String NUSDataset1Dir, String NUSDataset2Dir, String MASDatasetDir,
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
        boolean runningFirstTime = false;        
        
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
            fileNameEvaluationResult = NUSDataset1Dir + "\\" + fileNameEvaluationResult;
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
            fileNameEvaluationResult = MASDatasetDir + fileNameEvaluationResult;
            // Step 1: read list 1000 authors for test set.
            System.out.println("Begin reading author test set...");
            startTime = System.nanoTime();
            authorTestSet = MASDataset1.readAuthorListTestSet(fileNameAuthorTestSet, fileNameGroundTruth, fileNameAuthorship);
            estimatedTime = System.nanoTime() - startTime;
            System.out.println("Reading author test set elapsed time: " + estimatedTime / 1000000000 + " seconds");
            System.out.println("End reading author test set.");
            // When method is CF, do not read paper content.
            if (recommendationMethod != 2) { // recommendationMethod == 2 == CF
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
                // Notice: Only run once.
                if (runningFirstTime) {
                    CBFPaperFVComputation.computeTFIDF(papers, dirPapers, dirPreProcessedPaper, sequenceDir, vectorDir, 2);
                }
                CBFPaperFVComputation.readTFIDFFromMahoutFile(papers, vectorDir);
                // Clear no longer in use objects.
                // Always clear abstract.
                CBFPaperFVComputation.clearPaperAbstract(papers);
            }
            // Check the number of TF-IDF feature (size of TF-IDF vector).
//            HashSet<String> x = new HashSet();
//            for (Paper p : papers.values()) {
//                for (String k : p.getTfidfVector().hashMap.keySet()) {
//                    if (!x.contains(k)) {
//                        x.add(k);
//                    }
//                }
//            }
//            long ans = x.size();
            
            // Step 4:
            // Get list of papers to process.
            paperIdsOfAuthorTestSet = CBFAuthorFVComputation.getPaperIdsOfAuthors(authorTestSet);
            paperIdsInTestSet = CBFAuthorFVComputation.getPaperIdsTestSet(authorTestSet);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="PP PRS dataset: Exp 1: compute relevancy.">
/*      
        CBFPaperFVComputation.computeFeatureVectorForAllPapers(papers, paperIdsOfAuthorTestSet, 0, 0, 0);
        CBFAuthorFVComputation.computeFVForAllAuthors(authorTestSet, papers, 0, 0);
        CBFPaperFVComputation.computeFeatureVectorForAllPapers(papers, paperIdsInTestSet, 0, 0, 0);

        String fileNameRelevancy = null;
        if (DatasetToUse == 1) {
            fileNameRelevancy = NUSDataset1Dir + "/rel.csv";
        } else if (DatasetToUse == 3) {
            fileNameRelevancy = MASDatasetDir + "/rel.csv";
        }
        for (Author r : authorTestSet.values()) {
            HashMap<String, Paper> rGroundTruthPapers = CBFPaperFVComputation.extractPapers(papers, 
                    new HashSet<String>(r.getGroundTruth()));
            computeCBFSim(r, rGroundTruthPapers, 0);
            Float rel_r = 0f;
            for (Float sim : r.getCbfSimHM().values()) {
                rel_r += sim;
            }
            rel_r /= r.getCbfSimHM().size();
            FileUtils.writeStringToFile(new File(fileNameRelevancy), r.getAuthorId() + "\t" + rel_r.toString() + "\n", "UTF8", true);
        }
        */
        //</editor-fold>

        // parameters for CBF methods.
        // combiningSchemePaperOfAuthor: 0: itself, 1: itself + ref; 2: itself + citations; 
        // 3: itself + refs + citations.
        int combiningSchemePaperOfAuthor = 0;
        // weightingSchemePaperOfAuthor: 0: linear; 1: cosine; 2: rpy.
        int weightingSchemePaperOfAuthor = 0;
        // timeAwareScheme: 0: unaware; 1: aware.
        int timeAwareScheme = 0;
        // gamma: forgetting factor when timeAwareScheme = 1. gamma = 0 <=> timeAwareScheme = 0.
        double gamma = 0;
        int combiningSchemePaperTestSet = 0;
        int weightingSchemePaperTestSet = 0;
        // Min Threshold to prune citation and reference paper when combining.
        double pruning = 0.0;
        // similarityScheme: 0: cosine
        int similarityScheme = 0;

        // parameters for CF method.
        // 1: KNN, 2: MF SVD
        int cfMethod = 1;
        // 1: Pearson, 2: Cosine, 3: Log likelihood
        int knnSimilarityScheme = 3;
        // KNN: k neighbors.
        int k = 8;
        // SVD: f features, normalize by l, i iterations.
        int f = 8;
        double l = 0.001;
        int i = 20;
        
        // parameters for TRUST-BASED method
        // howToTrustAuthor: 1: combine linear citation author and coauthor, 2: meta trust citation author of coauthor
        // 3: meta trust citation author of citation author.
        int howToTrustAuthor = 1;
        // howToGetTrustedPaper: 1: written by trusted author, 2: written and cited by trusted author
        int howToGetTrustedPaper = 2;
        // howToTrustPaper: 1: average trust value of authors, 2: max trust value of authors.
        int howToTrustPaper = 2;

        // parameters for HYBRID method
        // combinationScheme: 1: combine linear, 2: combine based on confidence, 
        // 3: combine based on confidence and linear, 4: confidence v2, 5: confidence and linear v2.
        int combinationScheme;
        // weighting when combine linear.
        float alpha;
        
        // Recommendation.
        if (recommendationMethod == 1) {
            //<editor-fold defaultstate="collapsed" desc="CONTENT BASED METHOD">
            System.out.println("Begin CBF recommendation...");
            startTime = System.nanoTime();

            algorithmName = CBFController.cbfComputeRecommendingScore(authorTestSet, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor,
                    timeAwareScheme, gamma,
                    combiningSchemePaperTestSet, weightingSchemePaperTestSet, 
                    pruning, similarityScheme);
            
//            // Filter old paper.
//            int cutYear = 2000; // Inclusive, not null year.
//            PaperFilterUtility.filterOldPaper(authorTestSet, papers, cutYear);
            
            FeatureVectorSimilarity.generateRecommendationForAuthorList(authorTestSet, topNRecommend);

            estimatedTime = System.nanoTime() - startTime;
            System.out.println("CBF recommendation elapsed time: " + estimatedTime / 1000000000 + " seconds");
            System.out.println("End CBF recommendation.");
            //</editor-fold>
        } else if (recommendationMethod == 2) {
            //<editor-fold defaultstate="collapsed" desc="CF METHODS">
            System.out.println("Begin CF recommendation...");
            startTime = System.nanoTime();

            algorithmName = CFController.cfComputeRecommendingScore(fileNameAuthorCitePaper, MahoutCFDir, 
                    cfMethod, knnSimilarityScheme, authorTestSet, paperIdsInTestSet,
                    runningFirstTime, k, f, l, i);
            
            CF.cfRecommendToAuthorList(authorTestSet, topNRecommend);

            estimatedTime = System.nanoTime() - startTime;
            System.out.println("CF recommendation elapsed time: " + estimatedTime / 1000000000 + " seconds");
            System.out.println("End CF recommendation.");
            //</editor-fold>
        } else if (recommendationMethod == 3) {
            //<editor-fold defaultstate="collapsed" desc="CBF-CF LINEAR COMBINATION">
            CBFController.cbfComputeRecommendingScore(authorTestSet, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor,
                    timeAwareScheme, gamma,
                    combiningSchemePaperTestSet, weightingSchemePaperTestSet,
                    pruning, similarityScheme);
            CFController.cfComputeRecommendingScore(fileNameAuthorCitePaper, MahoutCFDir,
                    cfMethod, knnSimilarityScheme, authorTestSet, paperIdsInTestSet,
                    runningFirstTime, k, f, l, i);
            
            combinationScheme = 1; // 5 options.
            alpha = 0.8f;
            
            CBFCF.computeCBFCFCombinationAndPutIntoModelForAuthorList(authorTestSet, alpha, combinationScheme);
            
            CBFCF.cbfcfHybridRecommendToAuthorList(authorTestSet, topNRecommend);
            algorithmName = "CBF-CF LINEAR COMBINATION:"
                    + " combinationScheme = " + combinationScheme
                    + " alpha = " + alpha;
            //</editor-fold>
        } else if (recommendationMethod == 4) {
            //<editor-fold defaultstate="collapsed" desc="TRUST BASED">
            TrustDataModelPreparation.computeCoAuthorRSSHM(authorTestSet, fileNameAuthorship, fileNamePapers);
            
            // Note: 
            // Need to go 2 step over citation network in metaTrust, 
            // so maybe need citation information of author outside authorTestSet,
            // so need to store the whole referenceRSSNet.
            // <AuthorID, <AuthorID of citation author, Score>>
            HashMap<String, HashMap<String, Float>> referenceRSSNet = new HashMap<>();
            // Also need to go 2 step over publication list in computeTrustedPaper,
            // so need to store the whole authorPaper list.
            HashMap<String, ArrayList<String>> authorPaperHM = new HashMap<>();
            TrustDataModelPreparation.computeCitationAuthorRSSHM(authorTestSet, 
                    fileNameAuthorship, fileNamePaperCitePaper, referenceRSSNet, authorPaperHM);
            
            combinationScheme = 1; // 5 options.
            alpha = 0.5f;
            
            if (howToTrustAuthor == 1) {
                // When how to trust author = 1, means COMBINE LINEAR COAUTHOR AND CITED AUTHOR
                // we have 5 OPTIONS for COMBINATION SCHEME.
                TrustHybrid.computeTrustedAuthorHMLinearCombinationAndPutIntoModelForAuthorList(authorTestSet, alpha, combinationScheme);
            } else if (howToTrustAuthor == 2) {
                // When how to trust author = 2, 3 
                // only 1 OPTION for combination linear: simple linear combine, DEFAULT COMBINATION SCHEME = 1.
                int metaTrustType = 1;
                TrustHybrid.computeMetaTrustedAuthorHMAndPutIntoModelForAuthorList(authorTestSet, referenceRSSNet, metaTrustType, alpha);
            } else if (howToTrustAuthor == 3) {
                int metaTrustType = 2;
                TrustHybrid.computeMetaTrustedAuthorHMAndPutIntoModelForAuthorList(authorTestSet, referenceRSSNet, metaTrustType, alpha);
            }
            
            TrustHybrid.computeTrustedPaperHMAndPutIntoModelForAuthorList(authorTestSet, authorPaperHM, papers, howToGetTrustedPaper, howToTrustPaper, paperIdsInTestSet);

            TrustHybrid.trustRecommendToAuthorList(authorTestSet, topNRecommend);
            algorithmName = "Trust Based Method:"
                    + " combinationScheme = " + combinationScheme 
                    + " alpha = " + alpha 
                    + " howToTrustAuthor = " + howToTrustAuthor 
                    + " howToTrustPaper = " + howToTrustPaper;
            //</editor-fold>
        } else if (recommendationMethod == 5) {
            //<editor-fold defaultstate="collapsed" desc="CBF-TRUST LINEAR COMBINATION">           
            
            // CBF:
            CBFController.cbfComputeRecommendingScore(authorTestSet, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor,
                    timeAwareScheme, gamma,
                    combiningSchemePaperTestSet, weightingSchemePaperTestSet,
                    pruning, similarityScheme);
            
            // Trust:
            TrustDataModelPreparation.computeCoAuthorRSSHM(authorTestSet, fileNameAuthorship, fileNamePapers);
            HashMap<String, HashMap<String, Float>> referenceRSSNet = new HashMap<>();
            HashMap<String, ArrayList<String>> authorPaperHM = new HashMap<>();
            TrustDataModelPreparation.computeCitationAuthorRSSHM(authorTestSet, fileNameAuthorship, fileNamePaperCitePaper, referenceRSSNet, authorPaperHM);

            combinationScheme = 1; // 5 options.
            alpha = 0.5f;
            
            if (howToTrustAuthor == 1) {
                TrustHybrid.computeTrustedAuthorHMLinearCombinationAndPutIntoModelForAuthorList(authorTestSet, alpha, combinationScheme);
            } else if (howToTrustAuthor == 2) {
                int metaTrustType = 1;
                TrustHybrid.computeMetaTrustedAuthorHMAndPutIntoModelForAuthorList(authorTestSet, referenceRSSNet, metaTrustType, alpha);
            } else if (howToTrustAuthor == 3) {
                int metaTrustType = 2;
                TrustHybrid.computeMetaTrustedAuthorHMAndPutIntoModelForAuthorList(authorTestSet, referenceRSSNet, metaTrustType, alpha);
            }
            
            TrustHybrid.computeTrustedPaperHMAndPutIntoModelForAuthorList(authorTestSet, authorPaperHM, papers, howToGetTrustedPaper, howToTrustPaper, paperIdsInTestSet);

            algorithmName = "CBF-Trust Linear Combination:"
                    + " Trust combinationScheme = " + combinationScheme 
                    + " Trust alpha = " + alpha
                    + " howToTrustAuthor = " + howToTrustAuthor
                    + " howToGetTrustedPaper = " + howToGetTrustedPaper
                    + " howToTrustPaper = " + howToTrustPaper;

            // Combine CBF and Trust:
            combinationScheme = 1; // 5 options.
            alpha = 0.5f;
            
            TrustHybrid.computeCBFTrustLinearCombinationAndPutIntoModelForAuthorList(authorTestSet, alpha, combinationScheme);

            TrustHybrid.trustHybridRecommendToAuthorList(authorTestSet, topNRecommend);
            algorithmName = algorithmName 
                    + " CBF-TB combinationScheme = " + combinationScheme 
                    + " CBF-TB alpha = " + alpha;
            //</editor-fold>
        } else if (recommendationMethod == 6) {
            //<editor-fold defaultstate="collapsed" desc="CBF-TRUST COMBINATION V2">           
            
            // CBF:
            CBFController.cbfComputeRecommendingScore(authorTestSet, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor,
                    timeAwareScheme, gamma,
                    combiningSchemePaperTestSet, weightingSchemePaperTestSet,
                    pruning, similarityScheme);
            
            // Trust:
            TrustDataModelPreparation.computeCoAuthorRSSHM(authorTestSet, fileNameAuthorship, fileNamePapers);
            HashMap<String, HashMap<String, Float>> referenceRSSNet = new HashMap<>();
            HashMap<String, ArrayList<String>> authorPaperHM = new HashMap<>();
            TrustDataModelPreparation.computeCitationAuthorRSSHM(authorTestSet, fileNameAuthorship, fileNamePaperCitePaper, referenceRSSNet, authorPaperHM);

            combinationScheme = 1; // 5 options.
            alpha = 0.5f;
            // Merge coauthor and citedauthor.
            TrustHybrid.computeTrustedAuthorHMLinearCombinationAndPutIntoModelForAuthorList(authorTestSet, alpha, combinationScheme);
            // Get list of social related papers, score is not relevant.
            TrustHybrid.computeTrustedPaperHMAndPutIntoModelForAuthorList(authorTestSet, authorPaperHM, papers, howToGetTrustedPaper, howToTrustPaper, paperIdsInTestSet);

            // Compute CBF and Trust Hybrid value and put into author model:
            TrustHybrid.computeCBFTrustHybridV2AndPutIntoModelForAuthorList(authorTestSet);

            TrustHybrid.trustHybridRecommendToAuthorListV2(authorTestSet, topNRecommend);

            algorithmName = "CBF-Trust Hybrid V2";
            //</editor-fold>
        } else if (recommendationMethod == 7) {
            //<editor-fold defaultstate="collapsed" desc="CBF-TRUST COMBINATION V3">           
            
            // CBF:
            CBFController.cbfComputeRecommendingScore(authorTestSet, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor,
                    timeAwareScheme, gamma,
                    combiningSchemePaperTestSet, weightingSchemePaperTestSet,
                    pruning, similarityScheme);
            FeatureVectorSimilarity.generateRecommendationForAuthorList(authorTestSet, topNRecommend);
            
            // Trust:
            TrustDataModelPreparation.computeCoAuthorRSSHM(authorTestSet, fileNameAuthorship, fileNamePapers);
            HashMap<String, HashMap<String, Float>> referenceRSSNet = new HashMap<>();
            HashMap<String, ArrayList<String>> authorPaperHM = new HashMap<>();
            TrustDataModelPreparation.computeCitationAuthorRSSHM(authorTestSet, fileNameAuthorship, fileNamePaperCitePaper, referenceRSSNet, authorPaperHM);

            combinationScheme = 1; // 5 options.
            alpha = 0.5f;
            // Merge coauthor and citedauthor.
            TrustHybrid.computeTrustedAuthorHMLinearCombinationAndPutIntoModelForAuthorList(authorTestSet, alpha, combinationScheme);
            // Get list of social related papers, score is not relevant.
            TrustHybrid.computeTrustedPaperHMAndPutIntoModelForAuthorList(authorTestSet, authorPaperHM, papers, howToGetTrustedPaper, howToTrustPaper, paperIdsInTestSet);

            // Compute CBF and Trust Hybrid value and put into author model:
            TrustHybrid.computeCBFTrustHybridV3AndPutIntoModelForAuthorList(authorTestSet);

            TrustHybrid.trustHybridRecommendToAuthorListV3(authorTestSet, topNRecommend);

            algorithmName = "CBF-Trust Hybrid V3";
            //</editor-fold>
        } else if (recommendationMethod == 8) {
            //<editor-fold defaultstate="collapsed" desc="CBF-CF HYBRID V2">
            CBFController.cbfComputeRecommendingScore(authorTestSet, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor,
                    timeAwareScheme, gamma,
                    combiningSchemePaperTestSet, weightingSchemePaperTestSet,
                    pruning, similarityScheme);
            CFController.cfComputeRecommendingScore(fileNameAuthorCitePaper, MahoutCFDir,
                    cfMethod, knnSimilarityScheme, authorTestSet, paperIdsInTestSet,
                    runningFirstTime, k, f, l, i);
            
            CBFCF.computeCBFCFHybridV2AndPutIntoModelForAuthorList(authorTestSet);
            
            CBFCF.cbfcfHybridRecommendToAuthorListV2(authorTestSet, topNRecommend);
            
            algorithmName = "CBF-CF HYBRID V2";
            //</editor-fold>
        } else if (recommendationMethod == 9) {
            //<editor-fold defaultstate="collapsed" desc="CBF-CF HYBRID V3">
            CBFController.cbfComputeRecommendingScore(authorTestSet, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor,
                    timeAwareScheme, gamma,
                    combiningSchemePaperTestSet, weightingSchemePaperTestSet,
                    pruning, similarityScheme);
            FeatureVectorSimilarity.generateRecommendationForAuthorList(authorTestSet, topNRecommend);
            CFController.cfComputeRecommendingScore(fileNameAuthorCitePaper, MahoutCFDir,
                    cfMethod, knnSimilarityScheme, authorTestSet, paperIdsInTestSet,
                    runningFirstTime, k, f, l, i);
            
            CBFCF.computeCBFCFHybridV3AndPutIntoModelForAuthorList(authorTestSet);
            
            CBFCF.cbfcfHybridRecommendToAuthorListV3(authorTestSet, topNRecommend);
            
            algorithmName = "CBF-CF HYBRID V3";
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

        String partFileNameWithDataset = PRConstant.FOLDER_MAS_DATASET
                + "ErrorAnalysis\\Dataset" + DatasetToUse;
        String partFileNameWithMethod = " Method" + recommendationMethod
                + " Junior100 251214" + ".xls";

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
        double mp15 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 15);
        double mp20 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 20);
        double mp25 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 25);
        double mp30 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 30);
        double mp40 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 40);
        double mp50 = Evaluator.computeMeanPrecisionTopN(authorTestSet, 50);
        double mr5 = Evaluator.computeMeanRecallTopN(authorTestSet, 5);
        double mr10 = Evaluator.computeMeanRecallTopN(authorTestSet, 10);
        double mr15 = Evaluator.computeMeanRecallTopN(authorTestSet, 15);
        double mr20 = Evaluator.computeMeanRecallTopN(authorTestSet, 20);
        double mr25 = Evaluator.computeMeanRecallTopN(authorTestSet, 25);
        double mr100 = Evaluator.computeMeanRecallTopN(authorTestSet, 100);
        double f1 = Evaluator.computeMeanFMeasure(authorTestSet, 1);
        double map5 = Evaluator.computeMAP(authorTestSet, 5);
        double map10 = Evaluator.computeMAP(authorTestSet, 10);
        double map15 = Evaluator.computeMAP(authorTestSet, 15);
        double map20 = Evaluator.computeMAP(authorTestSet, 20);
        double map25 = Evaluator.computeMAP(authorTestSet, 25);
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
                .append("MP@15").append("\t")
                .append("MP@20").append("\t")
                .append("MP@25").append("\t")
                .append("MP@30").append("\t")
                .append("MP@40").append("\t")
                .append("MP@50").append("\t")
                .append("MR@5").append("\t")
                .append("MR@10").append("\t")
                .append("MR@15").append("\t")
                .append("MR@20").append("\t")
                .append("MR@25").append("\t")
                .append("MR@100").append("\t")
                .append("F1").append("\t")
                .append("MAP@5").append("\t")
                .append("MAP@10").append("\t")
                .append("MAP@15").append("\t")
                .append("MAP@20").append("\t")
                .append("MAP@25").append("\t")
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
                .append(mp15).append("\t")
                .append(mp20).append("\t")
                .append(mp25).append("\t")
                .append(mp30).append("\t")
                .append(mp40).append("\t")
                .append(mp50).append("\t")
                .append(mr5).append("\t")
                .append(mr10).append("\t")
                .append(mr15).append("\t")
                .append(mr20).append("\t")
                .append(mr25).append("\t")
                .append(mr100).append("\t")
                .append(f1).append("\t")
                .append(map5).append("\t")
                .append(map10).append("\t")
                .append(map15).append("\t")
                .append(map20).append("\t")
                .append(map25).append("\t")
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
