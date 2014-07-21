/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation;

import java.io.File;
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

/**
 *
 * @author THNghiep
 */
public class MLDataPreparation {

    private MLDataPreparation() {
    }

    public static void main(String[] args) {
        try {
            runExportClassificationMatrix(
                    PRConstant.FOLDER_MAS_DATASET1 + "[Training] Paper_Before_2006.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "[Training] Paper_Cite_Paper_Before_2006.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "[Testing] 1000Authors.csv",
                    //PRConstant.FOLDER_MAS_DATASET1 + "[Testing] Ground_Truth_2006_2008.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "[Testing] Ground_Truth_2006_2008_New_Citation.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "[Training] Author_Paper_Before_2006.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "[Training] Author_Cite_Paper_Before_2006.csv",
                    PRConstant.FOLDER_MAS_DATASET1 + "Text",
                    PRConstant.FOLDER_MAS_DATASET1 + "PreProcessedPaper",
                    PRConstant.FOLDER_MAS_DATASET1 + "Sequence",
                    PRConstant.FOLDER_MAS_DATASET1 + "Vector",
                    PRConstant.FOLDER_MAS_DATASET1 + "MahoutCF",
                    PRConstant.FOLDER_MAS_DATASET1 + "ML\\MLMatrix.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runExportClassificationMatrix(
            String fileNamePapers, String fileNamePaperCitePaper, String fileNameAuthorTestSet,
            String fileNameGroundTruth, String fileNameAuthorship, String fileNameAuthorCitePaper,
            String dirPapers, String dirPreProcessedPaper, String sequenceDir, String vectorDir,
            String MahoutCFDir,
            String fileNameMLMatrix) throws Exception {

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
        //PaperFVComputation.computeTFIDFFromPaperAbstract(papers, dirPapers, dirPreProcessedPaper, sequenceDir, vectorDir);
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

        // parameters for cf method: 1: KNN Pearson, 2: KNN Cosine, 3: SVD
        int cfMethod = 1;

        // parameters for hybrid method: 1: combine linear, 2: combine based on confidence, 
        // 3: combine based on confidence and linear
        int combinationScheme;
        float alpha;

        // parameters for trust based method
        int howToTrust;

        //<editor-fold defaultstate="collapsed" desc="ML HYBRID">
        // Compute CBF value.
        CBFController.cbfComputeRecommendingScore(authorTestSet, papers,
                paperIdsOfAuthorTestSet, paperIdsInTestSet,
                combiningSchemePaperOfAuthor, weightingSchemePaperOfAuthor,
                timeAwareScheme, gamma,
                combiningSchemePaperTestSet, weightingSchemePaperTestSet, similarityScheme,
                pruning);
        // Compute CF value
        CFController.cfComputeRecommendingScore(fileNameAuthorCitePaper, MahoutCFDir,
                cfMethod,
                authorTestSet, paperIdsInTestSet);
        // Compute Trust paper value.
        TrustDataModelPreparation.computeCoAuthorRSSHM(authorTestSet,
                fileNameAuthorship, fileNamePapers);
        HashMap<String, HashMap<String, Float>> referenceRSSNet = new HashMap<>();
        TrustDataModelPreparation.computeCitationAuthorRSSHM(authorTestSet,
                fileNameAuthorship, fileNamePaperCitePaper, referenceRSSNet);
        combinationScheme = 1;
        alpha = (float) 0.5;
        howToTrust = 2; // Max of trusted author.
        TrustHybrid.computeTrustedAuthorHMLinearCombinationAndPutIntoModelForAuthorList(authorTestSet, alpha, combinationScheme);
        TrustHybrid.computeTrustedPaperHMAndPutIntoModelForAuthorList(authorTestSet, howToTrust);
        // Compute paper quality.
        HashMap<String, Paper> paperTestSet = CBFPaperFVComputation.extractPapers(papers, paperIdsInTestSet);
        PaperQualityComputation.computeQualityValueForAllPapers(paperTestSet);

        // Export Classification matrix.
        MLDataPreparation.exportClassificationMatrix(authorTestSet, paperTestSet, fileNameMLMatrix);
        //</editor-fold>
    }

    public static void exportClassificationMatrix(HashMap<String, Author> authors,
            HashMap<String, Paper> papers,
            String fileNameMLMatrix) throws Exception {
        FileUtils.deleteQuietly(new File(fileNameMLMatrix));

        StringBuilder content = new StringBuilder();
        content.append("AuthorId").append("\t")
                .append("PaperId").append("\t")
                .append("CBFSimValue").append("\t")
                .append("CFRatingValue").append("\t")
                .append("TrustPaperValue").append("\t")
                .append("PaperQualityValue").append("\t")
                .append("TemporalCitationTrendValue").append("\t")
                .append("GroundTruth").append("\t")
                .append("\r\n");

        for (String authorId : authors.keySet()) {
            for (String paperId : authors.get(authorId).getCbfSimHM().keySet()) {
                int groundTruth = 0;
                if (authors.get(authorId).getGroundTruth().contains(paperId)) {
                    groundTruth = 1;
                }
                content.append(authorId).append("\t")
                        .append(paperId).append("\t")
                        .append(authors.get(authorId).getCbfSimHM().get(paperId)).append("\t")
                        .append(authors.get(authorId).getCfRatingHM().get(paperId)).append("\t")
                        .append(authors.get(authorId).getTrustedPaperHM().get(paperId)).append("\t")
                        .append(papers.get(paperId).getQualityValue()).append("\t")
                        .append(papers.get(paperId).getTemporalCitationTrendValue()).append("\t")
                        .append(groundTruth).append("\t")
                        .append("\r\n");
            }
        }
        FileUtils.writeStringToFile(new File(fileNameMLMatrix), content.toString(), "UTF8", true);
    }
}
