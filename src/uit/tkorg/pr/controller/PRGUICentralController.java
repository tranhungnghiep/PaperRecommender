/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.constant.Options;
import uit.tkorg.pr.dataimex.MASDataset1;
import uit.tkorg.pr.datapreparation.CBFAuthorFVComputation;
import uit.tkorg.pr.datapreparation.CBFPaperFVComputation;
import uit.tkorg.pr.datapreparation.TrustDataModelPreparation;
import uit.tkorg.pr.evaluation.Evaluator;
import uit.tkorg.pr.method.cbf.FeatureVectorSimilarity;
import uit.tkorg.pr.method.cf.CF;
import uit.tkorg.pr.method.hybrid.CBFCF;
import uit.tkorg.pr.method.hybrid.TrustHybrid;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;

/**
 *
 * @author Zoe
 */
public class PRGUICentralController {

    //<editor-fold defaultstate="collapsed" desc="Parameters of PRGUICentralController">
    //paths of dataset files
    public String fileNameAuthors;// File 1
    public String fileNameAuthorPaper;// File 2
    public String fileNameAuthorCitePaper;// File 3
    public String fileNamePapers; //File 4
    public String fileNamePaperCitePaper;// File 5
    public String fileNameGroundTruth;// File 6

    //temporary folders
    public String dirPapers = "Temp\\Text";
    public String dirPreProcessedPaper = "Temp\\Preprocess";
    public String sequenceDir = "Temp\\Sequence";
    public String vectorDir = "Temp\\VectorDir";
    public String MahoutCFDir = "Temp\\MahoutDir";
    public String _fileName_EvaluationResult = "Temp\\ResultEvaluation.txt";
    public String _fileName_RecommendationList = "Temp\\RecommendationList.txt";

    //papers and authors hashmap
    public HashMap<String, Paper> papers = new HashMap<>();
    public HashMap<String, Author> authors = new HashMap<>();
    public HashSet<String> paperIdsOfAuthorTestSet = new HashSet<>();
    public HashSet<String> paperIdsInTestSet = new HashSet<>();

    //authors hashmap for each algorithm
    public HashMap<String, Author> authorsCB = new HashMap<>();
    public HashMap<String, Author> authorsHybrid = new HashMap<>();
    public HashMap<String, Author> authorsCFP = new HashMap<>();
    public HashMap<String, Author> authorsCFC = new HashMap<>();
    public HashMap<String, Author> authorsCFSVD = new HashMap<>();
    public HashMap<String, Author> authorsTrustbased=new HashMap<String, Author>();
    public HashMap<String, Author> authorsHybridTrustbased=new HashMap<String, Author>();

    //recommendation algorithms
    public int algorithm_Recommendation; //1: CBF, 2: CF, 3: CBFCFHybrid, 4:Trust - based, 5:Hybrid Trust - based

    //<editor-fold defaultstate="collapsed" desc="Parameters of each algorithms">
    //<editor-fold defaultstate="collapsed" desc="Parameters of content - based algorithm">
    public int combinePaperOfAuthor_CB;
    public int weightingPaperOfAuthor_CB;
    public int timeAware_CB;
    public float gamma_CB;
    public int combineCandiatePaper_CB;
    public int weightingCandidatePaper_CB;
    public float pruning_CB;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Parameters of collaborative filtering algorithm">
    public int cfMethod_CF;
    public int knnSimilarityScheme_CF;
    public int kNeighbourhood_CF;
    public int f_CF;
    public float l_CF;
    public int i_CF;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Parameters of hybrid algorithm">
    public int combinePaperOfAuthor_HB;
    public int weightingPaperOfAuthor_HB;
    public int timeAware_HB;
    public float gamma_HB;
    public int combineCandiatePaper_HB;
    public int weightingCandidatePaper_HB;
    public float pruning_HB;
    
    public int cfMethod_HB;
    public int knnSimilarityScheme_HB;
    public int kNeighbourhood_HB;
    public int f_HB;
    public float l_HB;
    public int i_HB;
    
    public float alpha_HB;
    public int combineHybrid_HB;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Parameters of trust - based algorithm">
    public int combinationScheme_TB = 1;
    public float alpha_TB=0f;
    public int howToTrustAuthor_TB = 1;
    public int howToGetTrustedPaper_TB = 2;
    public int howToTrustPaper_TB = 2;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Parameters of Hybrid trust - based algorithm">
    public int combinePaperOfAuthor_HTB;
    public int weightingPaperOfAuthor_HTB;
    public int timeAware_HTB;
    public float gamma_HTB;
    public int combineCandiatePaper_HTB;
    public int weightingCandidatePaper_HTB;
    public float pruning_HTB;
    
    public int combinationScheme_HTB = 1;
    public float alpha_HTB=0f;
    public int howToTrustAuthor_HTB = 1;
    public int howToGetTrustedPaper_HTB = 2;
    public int howToTrustPaper_HTB = 2;
    
    public float alpha_HTB1;
    public int combineHybrid_HTB;
    //</editor-fold>
    
    //</editor-fold>
    
    public int topRecommend;
    public int measure_Evaluation;
    public int topRank;
    
    //</editor-fold>
    
    public PRGUICentralController() {
        algorithm_Recommendation = 1; //1: CBF, 2: CF, 3: Hybrid, 

        //<editor-fold defaultstate="collapsed" desc="init CB">
        combinePaperOfAuthor_CB = 0;// combine paper of author
        weightingPaperOfAuthor_CB = 0;// weighting combine paper of author
        timeAware_CB = 0;// combine paper of author
        gamma_CB = 0;// gamma for timeAware
        combineCandiatePaper_CB = 0; // combine candiate paper
        weightingCandidatePaper_CB = 0;// weighting combine candiate paper
        pruning_CB = 0f;// pruning citation or preference paper for all paper
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="init CF">
        cfMethod_CF = 1;
        knnSimilarityScheme_CF = 3;
        kNeighbourhood_CF = 8;// number of neighbhood
        f_CF = 5;//SVD
        l_CF = 0.001f;//SVD
        i_CF = 100;//SVD
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="init Hybrid">
        combinePaperOfAuthor_HB = 0;// combine paper of author
        weightingPaperOfAuthor_HB = 0;// weighting combine paper of author
        timeAware_HB = 0;// combine paper of author
        gamma_HB = 0;// gamma for timeAware
        combineCandiatePaper_HB = 0; // combine candiate paper
        weightingCandidatePaper_HB = 0;// weighting combine candiate paper
        pruning_HB = 0f;// pruning citation or preference paper for all paper
        
        cfMethod_HB = 1;
        knnSimilarityScheme_HB = 3;
        kNeighbourhood_HB = 8;// number of neighbhood
        f_HB = 5;//SVD
        l_HB = 0.001f;//SVD
        i_HB = 100;//SVD
        
        alpha_HB = (float) 0.9;//Hybrid
        combineHybrid_HB = 1;//Hybrid
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="init trust - based">
        combinationScheme_TB = 1;
        alpha_TB=0f;
        howToTrustAuthor_TB = 1;
        howToGetTrustedPaper_TB = 2;
        howToTrustPaper_TB = 2;
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="init hybrid trust - based">
        combinePaperOfAuthor_HTB = 0;// combine paper of author
        weightingPaperOfAuthor_HTB = 0;// weighting combine paper of author
        timeAware_HTB = 0;// combine paper of author
        gamma_HTB = 0;// gamma for timeAware
        combineCandiatePaper_HTB = 0; // combine candiate paper
        weightingCandidatePaper_HTB = 0;// weighting combine candiate paper
        pruning_HTB = 0f;// pruning citation or preference paper for all paper
        
        combinationScheme_HTB = 1;
        alpha_HTB=0.5f;
        howToTrustAuthor_HTB = 1;
        howToGetTrustedPaper_HTB = 2;
        howToTrustPaper_HTB = 2;
        
        alpha_HTB1=0.3f;
        combineHybrid_HTB=1;
        //</editor-fold>

        topRecommend = 100;// top Recommmed
        measure_Evaluation = 0;// evaluation method
        topRank = 0;// top Rank

        fileNamePapers = "C:\\3.PRS-Experiment\\Dataset1 - MAS\\PRS Experimental data\\T0-T1\\[Training] Paper_Before_2006.csv"; //File 1
        fileNamePaperCitePaper = "C:\\3.PRS-Experiment\\Dataset1 - MAS\\PRS Experimental data\\T0-T1\\[Training] Paper_Cite_Paper_Before_2006.csv";// File 2
        fileNameAuthors = "C:\\3.PRS-Experiment\\Dataset1 - MAS\\PRS Experimental data\\T0-T1\\[Testing] 1000Authors.csv";// File 3
        fileNameAuthorPaper = "C:\\3.PRS-Experiment\\Dataset1 - MAS\\PRS Experimental data\\T0-T1\\[Training] Author_Paper_Before_2006.csv";// File 4
        fileNameAuthorCitePaper = "C:\\3.PRS-Experiment\\Dataset1 - MAS\\PRS Experimental data\\T0-T1\\[Training] Author_Cite_Paper_Before_2006.csv";// File 5
        fileNameGroundTruth = "C:\\3.PRS-Experiment\\Dataset1 - MAS\\PRS Experimental data\\T0-T1\\[Testing] Ground_Truth_2006_2008_New_Citation.csv";// File 6

        papers = new HashMap<>();
        authors = new HashMap<>();
        paperIdsOfAuthorTestSet = new HashSet<>();
        paperIdsInTestSet = new HashSet<>();

        authorsCFP = new HashMap<>();
        authorsCFC = new HashMap<>();
        authorsCFSVD = new HashMap<>();
        authorsTrustbased=new HashMap<>();
        authorsHybridTrustbased=new HashMap<>();
    }

    public String[] guiHandlerRequest(Options request) {
        String[] response = new String[2];
        try {
            switch (request) {
                case IMPORT_DATA:
                    long startTime;
                    long estimatedTime;
                    //Step 1: read list 1000 authors for test set.
                    System.out.println("Begin reading author test set...");
                    startTime = System.nanoTime();
                    authors = MASDataset1.readAuthorListTestSet(fileNameAuthors, fileNameGroundTruth, fileNameAuthorPaper);
                    estimatedTime = System.nanoTime() - startTime;
                    System.out.println("Reading author test set elapsed time: " + estimatedTime / 1000000000 + " seconds");
                    System.out.println("End reading author test set.");
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
                    //CBFPaperFVComputation.computeTFIDFFromPaperAbstract(papers, dirPapers, dirPreProcessedPaper, sequenceDir, vectorDir);
                    CBFPaperFVComputation.readTFIDFFromMahoutFile(papers, vectorDir);
                    // Clear no longer in use objects.
                    // Always clear abstract.
                    CBFPaperFVComputation.clearPaperAbstract(papers);
                    // Step 4:
                    // Get list of papers to process.
                    paperIdsOfAuthorTestSet = CBFAuthorFVComputation.getPaperIdsOfAuthors(authors);
                    paperIdsInTestSet = CBFAuthorFVComputation.getPaperIdsTestSet(authors);
                    response[0] = "Importing dataset is successed. Please choose tab Recommendation to recommend and generate recommendation list.";
                    break;
                case RECOMMEND:
                    recommend();
                    response[0] = "Generating recommendation list is successed. Please choose tab Evaluation to evaluate algorithms.";
                    break;
                case EVALUATE:
                    response[1] = evaluate(authors, measure_Evaluation, topRank).toString();
                    response[0] = "Evaluation is successed.";
                    break;
                case ANALYSE_ERROR:
                    break;
                case HELP:
                    break;
                case SAVE_RECOMMENDATION_LIST:
                    StringBuilder recommendList = new StringBuilder();
                    for (String authorId : authors.keySet()) {
                        recommendList.append(authorId + ":\n").append(authors.get(authorId).getRecommendationList().toString() + "\r\n");
                    }
                    FileUtils.writeStringToFile(new File(_fileName_RecommendationList), recommendList.toString(), "UTF8", true);
                    response[0] = "Saving is successed.";
                    break;
                case SAVE_EVALUATION_RESULT:
                    FileUtils.writeStringToFile(new File(_fileName_EvaluationResult), evaluate(authors, measure_Evaluation, topRank).toString(), "UTF8", true);
                    response[0] = "Saving is successed.";
                    break;
                default:
                    response[0] = "Unknown.";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace(System.out);
            response[0] = "Fail.";
        }
        return response;
    }

    public void recommend() throws Exception {
        if (algorithm_Recommendation == 1) {
            //<editor-fold defaultstate="collapsed" desc="Content - based">
            long startTime;
            long estimatedTime;
            System.out.println("Begin CBF recommendation...");
            startTime = System.nanoTime();

            for (String authorId : authors.keySet()) {
                authors.get(authorId).getRecommendationList().clear();
                authors.get(authorId).getCbfSimHM();
            }
            
            CBFController.cbfComputeRecommendingScore(authors, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combinePaperOfAuthor_CB, weightingPaperOfAuthor_CB,
                    timeAware_CB, gamma_CB,
                    combineCandiatePaper_CB, weightingCandidatePaper_CB, 
                    pruning_CB, 0);
            FeatureVectorSimilarity.generateRecommendationForAuthorList(authors, topRecommend);
            
            estimatedTime = System.nanoTime() - startTime;
            System.out.println("CBF recommendation elapsed time: " + estimatedTime / 1000000000 + " seconds");
            System.out.println("End CBF recommendation.");
            //</editor-fold>
        } else if (algorithm_Recommendation == 2) {
            //<editor-fold defaultstate="collapsed" desc="CF Method">
            long startTime;
            long estimatedTime;
            System.out.println("Begin CF recommendation...");
            startTime = System.nanoTime();

            for (String authorId : authors.keySet()) {
                authors.get(authorId).getRecommendationList().clear();
                authors.get(authorId).getCfRatingHM().clear();
            }

            CFController.cfComputeRecommendingScore(fileNameAuthorCitePaper, MahoutCFDir, 
                    cfMethod_CF, knnSimilarityScheme_CF, authors, paperIdsInTestSet,
                    true, 8, 8, 0.001, 100);
            CF.cfRecommendToAuthorList(authors, topRecommend);

            estimatedTime = System.nanoTime() - startTime;
            System.out.println("CF recommendation elapsed time: " + estimatedTime / 1000000000 + " seconds");
            System.out.println("End CF recommendation.");
            //</editor-fold>
        } else if (algorithm_Recommendation == 3) {
            //<editor-fold defaultstate="collapsed" desc="Hybrid Method">
            float alpha_temp = (float) alpha_HB;
            long startTime;
            long estimatedTime;
            System.out.println("Begin Hybrid recommendation...");
            startTime = System.nanoTime();

            for (String authorId : authors.keySet()) {
                authors.get(authorId).getRecommendationList().clear();
                authors.get(authorId).getCbfSimHM().clear();
                authors.get(authorId).getCfRatingHM().clear();
                authors.get(authorId).getCbfCfHybridHM().clear();
            }

            CBFController.cbfComputeRecommendingScore(authors, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combinePaperOfAuthor_HB, weightingPaperOfAuthor_HB,
                    timeAware_HB, gamma_HB,
                    combineCandiatePaper_HB, weightingCandidatePaper_HB, 
                    pruning_HB, 0);
            
            CFController.cfComputeRecommendingScore(fileNameAuthorCitePaper, MahoutCFDir,
                    cfMethod_HB, knnSimilarityScheme_HB, authors, paperIdsInTestSet,
                    true, 8, 8, 0.001, 100);
            
            CBFCF.computeCBFCFCombinationAndPutIntoModelForAuthorList(authors, alpha_temp, combineHybrid_HB);
            CBFCF.cbfcfHybridRecommendToAuthorList(authors, topRecommend);

            estimatedTime = System.nanoTime() - startTime;
            System.out.println("Hybrid recommendation elapsed time: " + estimatedTime / 1000000000 + " seconds");
            System.out.println("End Hybrid recommendation.");
            //</editor-fold>
        }else if (algorithm_Recommendation == 4) {
            //<editor-fold defaultstate="collapsed" desc="TRUST BASED">
            TrustDataModelPreparation.computeCoAuthorRSSHM(authors, fileNameAuthorPaper, fileNamePapers);
            HashMap<String, HashMap<String, Float>> referenceRSSNet = new HashMap<>();
            HashMap<String, ArrayList<String>> authorPaperHM = new HashMap<>();
            TrustDataModelPreparation.computeCitationAuthorRSSHM(authors, fileNameAuthorPaper, fileNamePaperCitePaper, referenceRSSNet, authorPaperHM);
            
            //int combinationScheme = 1;
            
            float alpha_temp = (float) alpha_TB;
            
            //int howToTrustAuthor = 1;
            //int howToTrustPaper = 2;
            
            if (howToTrustAuthor_TB == 1) {
                TrustHybrid.computeTrustedAuthorHMLinearCombinationAndPutIntoModelForAuthorList(authors, alpha_temp, combinationScheme_TB);
            } else if (howToTrustAuthor_TB == 2) {
                int metaTrustType = 1;
                TrustHybrid.computeMetaTrustedAuthorHMAndPutIntoModelForAuthorList(authors, referenceRSSNet, metaTrustType, alpha_temp);
            } else if (howToTrustAuthor_TB == 3) {
                int metaTrustType = 2;
                TrustHybrid.computeMetaTrustedAuthorHMAndPutIntoModelForAuthorList(authors, referenceRSSNet, metaTrustType, alpha_temp);
            }
            
            TrustHybrid.computeTrustedPaperHMAndPutIntoModelForAuthorList(authors, authorPaperHM, papers, howToGetTrustedPaper_TB, howToTrustPaper_TB, paperIdsInTestSet);

            TrustHybrid.trustRecommendToAuthorList(authors, topRecommend);
            //System.out.println();
            //</editor-fold>
        } else if (algorithm_Recommendation == 5) {
            //<editor-fold defaultstate="collapsed" desc="TRUST BASED LINEAR COMBINATION">           
            CBFController.cbfComputeRecommendingScore(authors, papers,
                    paperIdsOfAuthorTestSet, paperIdsInTestSet,
                    combinePaperOfAuthor_HTB, weightingPaperOfAuthor_HTB,
                    timeAware_HTB, gamma_HTB,
                    combineCandiatePaper_HTB, weightingCandidatePaper_HTB, 
                    pruning_HTB, 0);
            TrustDataModelPreparation.computeCoAuthorRSSHM(authors, fileNameAuthors, fileNamePapers);
            HashMap<String, HashMap<String, Float>> referenceRSSNet = new HashMap<>();
            HashMap<String, ArrayList<String>> authorPaperHM = new HashMap<>();
            TrustDataModelPreparation.computeCitationAuthorRSSHM(authors, fileNameAuthorPaper, fileNamePaperCitePaper, referenceRSSNet, authorPaperHM);

            //int combinationScheme = 1;
            float alpha_temp = (float) alpha_HTB;
           
            //int howToTrustAuthor = 1;
            //int howToTrustPaper = 2;
            
            
            if (howToTrustAuthor_HTB == 1) {
                TrustHybrid.computeTrustedAuthorHMLinearCombinationAndPutIntoModelForAuthorList(authors, alpha_temp, combinationScheme_HTB);
            } else if (howToTrustAuthor_HTB == 2) {
                int metaTrustType = 1;
                TrustHybrid.computeMetaTrustedAuthorHMAndPutIntoModelForAuthorList(authors, referenceRSSNet, metaTrustType, alpha_temp);
            } else if (howToTrustAuthor_HTB == 3) {
                int metaTrustType = 2;
                TrustHybrid.computeMetaTrustedAuthorHMAndPutIntoModelForAuthorList(authors, referenceRSSNet, metaTrustType, alpha_temp);
            }
            
            TrustHybrid.computeTrustedPaperHMAndPutIntoModelForAuthorList(authors, authorPaperHM, papers, howToGetTrustedPaper_HTB, howToTrustPaper_HTB, paperIdsInTestSet);

            
            TrustHybrid.computeCBFTrustLinearCombinationAndPutIntoModelForAuthorList(authors, alpha_HTB1, combineHybrid_HTB);

            TrustHybrid.trustHybridRecommendToAuthorList(authors, topRecommend);
            
            //</editor-fold>
        }
    }
    
    //evaluation accuracy of recommendation algorithm
    public StringBuilder evaluate(HashMap<String, Author> authors, int measure_Evaluation, int topRank) throws Exception {
        StringBuilder evaluationResult = new StringBuilder();
        if (measure_Evaluation == 1) {
            double evaluation = Evaluator.computeMeanPrecisionTopN(authors, topRank);
            evaluation = Math.round(evaluation * 1000) / 1000.0d;

            evaluationResult.append("Precision\t").append(topRank).append("\t")
                    .append(evaluation).append("\r\n");
        } else if (measure_Evaluation == 2) {
            double evaluation = Evaluator.computeMeanRecallTopN(authors, topRank);
            evaluation = Math.round(evaluation * 1000) / 1000.0d;

            evaluationResult.append("Recall\t").append(topRank).append("\t")
                    .append(evaluation).append("\r\n");
        } else if (measure_Evaluation == 3) {
            double evaluation = Evaluator.computeMeanFMeasure(authors, 1);
            evaluation = Math.round(evaluation * 1000) / 1000.0d;

            evaluationResult.append("F1\t").append(topRank).append("\t")
                    .append(evaluation).append("\r\n");
        } else if (measure_Evaluation == 4) {
            double evaluation = Evaluator.computeMAP(authors, topRank);
            evaluation = Math.round(evaluation * 1000) / 1000.0d;

            evaluationResult.append("MAP\t").append(topRank).append("\t")
                    .append(evaluation).append("\r\n");
        } else if (measure_Evaluation == 5) {
            double evaluation = Evaluator.computeMeanNDCG(authors, topRank);
            evaluation = Math.round(evaluation * 1000) / 1000.0d;

            evaluationResult.append("NDCG\t").append(topRank).append("\t")
                    .append(evaluation).append("\r\n");
        } else if (measure_Evaluation == 6) {
            double evaluation = Evaluator.computeMRR(authors);
            evaluation = Math.round(evaluation * 1000) / 1000.0d;

            evaluationResult.append("MRR\t").append(topRank).append("\t")
                    .append(evaluation).append("\r\n");
        }
        return evaluationResult;
    }
}
