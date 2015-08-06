/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.constant.PRConstant;
import uit.tkorg.pr.controller.CBFController;
import uit.tkorg.pr.controller.CFController;
import uit.tkorg.pr.dataimex.MASDataset1;
import uit.tkorg.pr.dataimex.NUSDataset1;
import uit.tkorg.pr.evaluation.ErrorAnalysis;
import uit.tkorg.pr.method.cbf.FeatureVectorSimilarity;
import uit.tkorg.pr.method.cf.CF;
import uit.tkorg.pr.method.hybrid.CBFCF;
import uit.tkorg.pr.method.hybrid.TrustHybrid;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.NumericUtility;

/**
 *
 * @author THNghiep
 */
public class MLDataPreparation {

    private MLDataPreparation() {
    }

    public static void main(String[] args) {
        try {
            // Training Set
            System.out.println("START EXPORTING THE TRAINING SET");
            runExportClassificationMatrix(
                    // For CBF
                    PRConstant.FOLDER_MAS_DATASET + "T0-T1\\[Training] Paper_Before_2006.csv",
                    PRConstant.FOLDER_MAS_DATASET + "T0-T1\\[Training] Paper_Cite_Paper_Before_2006.csv",
                    // Testing data
                    PRConstant.FOLDER_MAS_DATASET + "T0-T1\\[Testing] 1000Authors.csv",
                    //PRConstant.FOLDER_MAS_DATASET + "T0-T1\\[Testing] Ground_Truth_2006_2008.csv",
                    PRConstant.FOLDER_MAS_DATASET + "T0-T1\\[Testing] Ground_Truth_2006_2008_New_Citation.csv",
                    // Author Profile
                    PRConstant.FOLDER_MAS_DATASET + "T0-T1\\[Training] Author_Paper_Before_2006.csv",
                    // For CF
                    PRConstant.FOLDER_MAS_DATASET + "T0-T1\\[Training] Author_Cite_Paper_Before_2006.csv", 
                    // Mahout
                    PRConstant.FOLDER_MAS_DATASET + "T0-T1\\TF-IDF\\Text",
                    PRConstant.FOLDER_MAS_DATASET + "T0-T1\\TF-IDF\\PreProcessedPaper",
                    PRConstant.FOLDER_MAS_DATASET + "T0-T1\\TF-IDF\\Sequence",
                    PRConstant.FOLDER_MAS_DATASET + "T0-T1\\TF-IDF\\Vector",
                    PRConstant.FOLDER_MAS_DATASET + "T0-T1\\MahoutCF",
                    // Result
                    PRConstant.FOLDER_MAS_DATASET + "ML\\MLMatrixTrainingSet.csv",
                    // Filename for Testset with 10 Papers for each author
                    null,
                    1);
            System.out.println("END EXPORTING THE TRAINING SET");
            
            // Test Set
            System.out.println("START EXPORTING THE TESTING SET");
            runExportClassificationMatrix(
                    // For CBF
                    PRConstant.FOLDER_MAS_DATASET + "T1-T2\\[Training] Paper_Before_2009.csv",
                    PRConstant.FOLDER_MAS_DATASET + "T1-T2\\[Training] Paper_Cite_Paper_Before_2009.csv",
                    // Testing data
                    PRConstant.FOLDER_MAS_DATASET + "T1-T2\\[Testing] 1000Authors.csv",
                    //PRConstant.FOLDER_MAS_DATASET + "T1-T2\\[Testing] Ground_Truth_2009_2010.csv",
                    PRConstant.FOLDER_MAS_DATASET + "T1-T2\\[Testing] Ground_Truth_2009_2010_New_Citation.csv",
                    // Author Profile
                    PRConstant.FOLDER_MAS_DATASET + "T1-T2\\[Training] Author_Paper_Before_2009.csv",
                    // For CF
                    PRConstant.FOLDER_MAS_DATASET + "T1-T2\\[Training] Author_Cite_Paper_Before_2009.csv", 
                    // Mahout
                    PRConstant.FOLDER_MAS_DATASET + "T1-T2\\TF-IDF\\Text",
                    PRConstant.FOLDER_MAS_DATASET + "T1-T2\\TF-IDF\\PreProcessedPaper",
                    PRConstant.FOLDER_MAS_DATASET + "T1-T2\\TF-IDF\\Sequence",
                    PRConstant.FOLDER_MAS_DATASET + "T1-T2\\TF-IDF\\Vector",
                    PRConstant.FOLDER_MAS_DATASET + "T1-T2\\MahoutCF",
                    // Result
                    PRConstant.FOLDER_MAS_DATASET + "ML\\MLMatrixTestSet.csv",
                    // Filename for Testset with 10 Papers for each author
                    null,
                    1);
            System.out.println("END EXPORTING THE TESTING SET");
            System.out.println("DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runExportClassificationMatrix(
            String fileNamePapers, String fileNamePaperCitePaper, String fileNameAuthorTestSet,
            String fileNameGroundTruth, String fileNameAuthorship, String fileNameAuthorCitePaper,
            String dirPapers, String dirPreProcessedPaper, String sequenceDir, String vectorDir,
            String MahoutCFDir,
            String fileNameTraingSetMLMatrix, String fileNameTestSetMLMatrix,
            int testingData) throws Exception {

        HashMap<String, Author> authorTestSet = new HashMap<>();
        HashMap<String, Paper> papers = new HashMap<>();
        HashSet<String> paperIdsOfAuthorTestSet = new HashSet<>();
        HashSet<String> paperIdsInTestSet = new HashSet<>();

        //<editor-fold defaultstate="collapsed" desc="Read and Prepare Data (TFIDF)">
        // Step 1: read list 1000 authors for test set.
        authorTestSet = MASDataset1.readAuthorListTestSet(fileNameAuthorTestSet, fileNameGroundTruth, fileNameAuthorship);
        // When method is CF, do not read paper content.
        // Step 2:
        // - Read content of papers from [Training] Paper_Before_2006.csv
        // - Store metadata of all papers into HashMap<String, Paper> papers
        papers = MASDataset1.readPaperList(fileNamePapers, fileNamePaperCitePaper);
        // Step 3: 
        // Compute TF-IDF for MAS papers.
        CBFPaperFVComputation.computeTFIDFFromPaperAbstract(papers, dirPapers, dirPreProcessedPaper, sequenceDir, vectorDir);
        CBFPaperFVComputation.readTFIDFFromMahoutFile(papers, vectorDir);
        // Clear no longer in use objects.
        // Always clear abstract.
        CBFPaperFVComputation.clearPaperAbstract(papers);
        // Step 4:
        // Get list of papers to process.
        paperIdsOfAuthorTestSet = CBFAuthorFVComputation.getPaperIdsOfAuthors(authorTestSet);
        paperIdsInTestSet = CBFAuthorFVComputation.getPaperIdsTestSet(authorTestSet);
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

        // parameters for cf method.
        int cfMethod = 1;
        int knnSimilarityScheme = 3;

        // parameters for hybrid method: 1: combine linear, 2: combine based on confidence, 
        // 3: combine based on confidence and linear
        int combinationScheme;
        float alpha;

        // parameters for trust based method
        int howToGetTrustedPaper;
        int howToTrust;

        //<editor-fold defaultstate="collapsed" desc="ML HYBRID">
        // Compute CBF value.
        CBFController.cbfComputeRecommendingScore(authorTestSet, papers,
                paperIdsOfAuthorTestSet, paperIdsInTestSet,
                combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor,
                timeAwareScheme, gamma,
                combiningSchemePaperTestSet, weightingSchemePaperTestSet,
                pruning, similarityScheme);
        // Compute CF value
        CFController.cfComputeRecommendingScore(fileNameAuthorCitePaper, MahoutCFDir,
                cfMethod, knnSimilarityScheme, authorTestSet, paperIdsInTestSet,
                    true, 8, 8, 0.001, 100);
        // Compute Trust paper value.
        TrustDataModelPreparation.computeCoAuthorRSSHM(authorTestSet,
                fileNameAuthorship, fileNamePapers);
        HashMap<String, HashMap<String, Float>> referenceRSSNet = new HashMap<>();
        HashMap<String, ArrayList<String>> authorPaperHM = new HashMap<>();
        TrustDataModelPreparation.computeCitationAuthorRSSHM(authorTestSet, fileNameAuthorship, fileNamePaperCitePaper, referenceRSSNet, authorPaperHM);
        combinationScheme = 1;
        alpha = (float) 0.5;
        howToGetTrustedPaper = 2;
        howToTrust = 2; // Max of trusted author.
        TrustHybrid.computeTrustedAuthorHMLinearCombinationAndPutIntoModelForAuthorList(authorTestSet, alpha, combinationScheme);
        TrustHybrid.computeTrustedPaperHMAndPutIntoModelForAuthorList(authorTestSet, authorPaperHM, papers, howToGetTrustedPaper, howToTrust, paperIdsInTestSet);
        // Compute paper quality.
        HashMap<String, Paper> paperTestSet = CBFPaperFVComputation.extractPapers(papers, paperIdsInTestSet);
        PaperQualityComputation.computeQualityValueForAllPapers(paperTestSet);
        //</editor-fold>

        // Export Classification matrix.
        if (testingData == 1) {
            MLDataPreparation.exportClassificationMatrix(authorTestSet, paperTestSet, fileNameTraingSetMLMatrix);
        } else if (testingData == 2) {
            MLDataPreparation.exportClassificationMatrixSeparatedTestSet(authorTestSet, paperTestSet, fileNameTraingSetMLMatrix, fileNameTestSetMLMatrix, 10);
        }
    }

    public static void exportClassificationMatrix(HashMap<String, Author> authors,
            HashMap<String, Paper> papers,
            String fileNameMLMatrix) throws Exception {
        FileUtils.deleteQuietly(new File(fileNameMLMatrix));
        FileUtils.write(new File(fileNameMLMatrix), "");

        StringBuilder content = new StringBuilder();
        content.append("AuthorId").append("\t")
                .append("PaperId").append("\t")
                .append("CBFSimValue").append("\t")
                .append("CFRatingValue").append("\t")
                .append("TrustPaperValue").append("\t")
                .append("PaperQualityValue").append("\t")
                .append("TemporalCitationTrendValue").append("\t")
                .append("GroundTruth")
                .append("\r\n");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileNameMLMatrix))) {
            bw.write(content.toString());
            for (String authorId : authors.keySet()) {
                for (String paperId : authors.get(authorId).getCbfSimHM().keySet()) {
                    Float cbfSimValue = authors.get(authorId).getCbfSimHM().get(paperId);
                    if (cbfSimValue == null) {
                        cbfSimValue = 0f;
                    }
                    
                    Float cfRatingValue = authors.get(authorId).getCfRatingHM().get(paperId);
                    if (cfRatingValue == null) {
                        cfRatingValue = 0f;
                    }
                    
                    Float trustedPaperValue = authors.get(authorId).getTrustedPaperHM().get(paperId);
                    if (trustedPaperValue == null) {
                        trustedPaperValue = 0f;
                    }
                    
                    int groundTruth = 0;
                    if (authors.get(authorId).getGroundTruth().contains(paperId)) {
                        groundTruth = 1;
                    }
                    
                    // Reset StringBuilder.
                    content.setLength(0);
                    content.append(authorId).append("\t")
                            .append(paperId).append("\t")
                            .append(String.format("%f", cbfSimValue)).append("\t")
                            .append(String.format("%f", cfRatingValue)).append("\t")
                            .append(String.format("%f", trustedPaperValue)).append("\t")
                            .append(String.format("%f", papers.get(paperId).getQualityValue())).append("\t")
                            .append(String.format("%f", papers.get(paperId).getTemporalCitationTrendValue())).append("\t")
                            .append(groundTruth)
                            .append("\r\n");
                    bw.write(content.toString());
                }
            }
        }
    }

    public static void exportClassificationMatrixSeparatedTestSet(HashMap<String, Author> authors,
            HashMap<String, Paper> papers,
            String fileNameTrainingSetMLMatrix,
            String fileNameTestSetMLMatrix,
            int numberOfItemsInTestSet) throws Exception {
        FileUtils.deleteQuietly(new File(fileNameTrainingSetMLMatrix));
        FileUtils.write(new File(fileNameTrainingSetMLMatrix), "");
        FileUtils.deleteQuietly(new File(fileNameTestSetMLMatrix));
        FileUtils.write(new File(fileNameTestSetMLMatrix), "");

        StringBuilder content = new StringBuilder();
        content.append("AuthorId").append("\t")
                .append("PaperId").append("\t")
                .append("CBFSimValue").append("\t")
                .append("CFRatingValue").append("\t")
                .append("TrustPaperValue").append("\t")
                .append("PaperQualityValue").append("\t")
                .append("TemporalCitationTrendValue").append("\t")
                .append("GroundTruth")
                .append("\r\n");
        try (BufferedWriter bwTrain = new BufferedWriter(new FileWriter(fileNameTrainingSetMLMatrix));
                BufferedWriter bwTest = new BufferedWriter(new FileWriter(fileNameTestSetMLMatrix));) {
            bwTrain.write(content.toString());
            bwTest.write(content.toString());
            for (String authorId : authors.keySet()) {
                int countTestItem = 0;
                for (String paperId : authors.get(authorId).getCbfSimHM().keySet()) {
                    Float cbfSimValue = authors.get(authorId).getCbfSimHM().get(paperId);
                    if (cbfSimValue == null) {
                        cbfSimValue = 0f;
                    }
                    
                    Float cfRatingValue = authors.get(authorId).getCfRatingHM().get(paperId);
                    if (cfRatingValue == null) {
                        cfRatingValue = 0f;
                    }
                    
                    Float trustedPaperValue = authors.get(authorId).getTrustedPaperHM().get(paperId);
                    if (trustedPaperValue == null) {
                        trustedPaperValue = 0f;
                    }
                    
                    int groundTruth = 0;
                    if (authors.get(authorId).getGroundTruth().contains(paperId)) {
                        groundTruth = 1;
                    }
                    
                    content.setLength(0);
                    content.append(authorId).append("\t")
                            .append(paperId).append("\t")
                            .append(String.format("%f", cbfSimValue)).append("\t")
                            .append(String.format("%f", cfRatingValue)).append("\t")
                            .append(String.format("%f", trustedPaperValue)).append("\t")
                            .append(String.format("%f", papers.get(paperId).getQualityValue())).append("\t")
                            .append(String.format("%f", papers.get(paperId).getTemporalCitationTrendValue())).append("\t")
                            .append(groundTruth)
                            .append("\r\n");
                    if ((groundTruth == 1) && (countTestItem < numberOfItemsInTestSet)) {
                        countTestItem++;
                        bwTest.write(content.toString());
                    } else {
                        bwTrain.write(content.toString());
                    }
                }
            }
        }
    }
}
