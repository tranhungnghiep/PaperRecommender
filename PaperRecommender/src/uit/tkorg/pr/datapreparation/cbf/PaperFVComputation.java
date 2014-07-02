/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation.cbf;

import ir.vsr.HashMapVector;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.dataimex.MASDataset1;
import uit.tkorg.pr.dataimex.MahoutFile;
import uit.tkorg.pr.dataimex.PRGeneralFile;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.WeightingUtility;
import uit.tkorg.utility.textvectorization.TextPreprocessUtility;
import uit.tkorg.utility.textvectorization.TextVectorizationByMahoutTerminalUtility;

/**
 *
 * @author THNghiep 
 * This class handles logic to compute feature vector of all papers.
 * Method: 
 * - Compute papers' full vector: its content itself or combining its refs and cits by linear, cosine, rpy.
 */
public class PaperFVComputation {

    // Prevent instantiation.
    private PaperFVComputation() {}

    public static void computeTFIDFFromPaperAbstract(HashMap<String, Paper> papers, 
            String dirPapers, String dirPreProcessedPaper, String sequenceDir, String vectorDir) throws Exception {
        // Step 1:
        // - Writing abstract of all papers to text files. One file for each paper in 'dirPapers' directory.
        // - Clear abstract of all papers.
        System.out.println("Begin writing abstract to file...");
        long startTime = System.nanoTime();
        PRGeneralFile.writePaperAbstractToTextFile(papers, dirPapers);
        // Clear no longer in use objects.
        // Always clear abstract.
        PaperFVComputation.clearPaperAbstract(papers);
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Writing abstract to file elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End writing abstract to file.");

        // Step 2: Preprocessing content of all papers. Remove stop words and stemming
        System.out.println("Begin removing stopword and stemming...");
        startTime = System.nanoTime();
        TextPreprocessUtility.parallelProcess(dirPapers, dirPreProcessedPaper, true, true);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Removing stopword and stemming elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End removing stopword and stemming.");

        // Step 3: tf-idf. Output of this process is vectors of papers stored in a Mahout's binary file
        System.out.println("Begin vectorizing...");
        startTime = System.nanoTime();
        TextVectorizationByMahoutTerminalUtility.textVectorizeFiles(dirPreProcessedPaper, sequenceDir, vectorDir);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Vectorizing elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End vectorizing.");

        // Step 4: Read vectors of all papers store in
        // - HashMap<Integer, String> dictMap: Dictionary of the whole collection.
        // - HashMap<String, HashMapVector> vectorizedDocuments: <PaperID, Vector TF*IDF of PaperID>
        System.out.println("Begin reading vector...");
        startTime = System.nanoTime();
//        HashMap<Integer, String> dictMap = MahoutFile.readMahoutDictionaryFiles(vectorDir);
        HashMap<String, HashMapVector> vectorizedPapers = MahoutFile.readMahoutVectorFiles(vectorDir);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Reading vector elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End reading vector.");

        // Step 5: put TFIDF vectors of all paper (vectorizedDocuments)
        // into HashMap<String, Paper> papers (model)
        System.out.println("Begin setting tf-idf to papers...");
        startTime = System.nanoTime();
        PaperFVComputation.setTFIDFVectorForAllPapers(papers, vectorizedPapers);
        // Clear no longer in use objects to free memory (although just procedure, underlying data are still in use).
//        dictMap = null;
        vectorizedPapers = null;
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Setting tf-idf to papers elapsed time: " + estimatedTime / 1000000000 + " seconds");
        System.out.println("End setting tf-idf to papers.");
    }

    public static void clearPaperAbstract(HashMap<String, Paper> papers) throws Exception {
        for (String paperId : papers.keySet()) {
            papers.get(paperId).setPaperAbstract(null);
        }
    }

    public static void clearTFIDF(HashMap<String, Paper> papers) throws Exception {
        for (String paperId : papers.keySet()) {
            papers.get(paperId).setTfidfVector(new HashMapVector());
        }
    }

    public static void clearFV(HashMap<String, Paper> papers) throws Exception {
        for (String paperId : papers.keySet()) {
            papers.get(paperId).setFeatureVector(new HashMapVector());
        }
    }

    public static void setTFIDFVectorForAllPapers(HashMap<String, Paper> papers, HashMap<String, HashMapVector> vectorizedDocuments) throws Exception {
        for (String paperId : papers.keySet()) {
            papers.get(paperId).setTfidfVector(vectorizedDocuments.get(paperId));
        }
    }

    public static HashMap<String, Paper> extractPapers(HashMap<String, Paper> papers, HashSet<String> paperIds) throws Exception {
        HashMap<String, Paper> returnPapers = new HashMap<>();
        
        for (String paperId : paperIds) {
            if (papers.containsKey(paperId)) {
                returnPapers.put(paperId, papers.get(paperId));
            }
        }
        
        return returnPapers;
    }

    
    /**
     * This method computes and set value for all papers' full feature vector
     * (after combining citation and reference papers).
     * 
     * @param paperIds: restrict paper ids to compute FV. Null means no restriction.
     * @param combiningScheme   0: itself content, 1: itself content + content of references; 
     *                          2: itself content + content of citations; 3: itself content + content of references + content of citations.
     * @param weightingScheme   0: linear; 1: cosine; 2: rpy
     */
    public static void computeFeatureVectorForAllPapers(HashMap<String, Paper> papers, HashSet<String> paperIds, int combiningScheme, int weightingScheme) throws Exception {
        
        if (paperIds == null) {
            paperIds = (HashSet) papers.keySet();
        }
        
        // Current paper.
        int count = 0;
        System.out.println("Number of papers to compute FV: " + paperIds.size());

        for (String paperId : paperIds) {
            // Print current paper number.
            if (count % 1000 == 0) {
                System.out.println("Compting FV for paper No. " + (count + 1));
            }
            count++;

            if (papers.containsKey(paperId)) {
                computePaperFV(papers, paperId, combiningScheme, weightingScheme);
            }
        }
    }

    /**
     * This method compute final feature vector by combining citation and
     * reference.
     *
     * @param paperId
     * @return list represents feature vector.
     */
    public static void computePaperFV(HashMap<String, Paper> papers, String paperId, int combiningScheme, int weightingScheme) throws Exception {

        papers.get(paperId).setFeatureVector(new HashMapVector()); // Re-initiate feature vector
        papers.get(paperId).getFeatureVector().add(papers.get(paperId).getTfidfVector());// add tfidf to zero vector, not assign
        
        // weighting scheme
        if (weightingScheme == 0) {
            if (combiningScheme == 1) {
                sumFVLinear(papers, paperId, papers.get(paperId).getReference());
            } else if (combiningScheme == 2) {
                sumFVLinear(papers, paperId, papers.get(paperId).getCitation());
            } else if (combiningScheme == 3) {
                sumFVLinear(papers, paperId, papers.get(paperId).getReference());
                sumFVLinear(papers, paperId, papers.get(paperId).getCitation());
            }
        } else if (weightingScheme == 1) {
            if (combiningScheme == 1) {
                sumFVCosine(papers, paperId, papers.get(paperId).getReference());
            } else if (combiningScheme == 2) {
                sumFVCosine(papers, paperId, papers.get(paperId).getCitation());
            } else if (combiningScheme == 3) {
                sumFVCosine(papers, paperId, papers.get(paperId).getReference());
                sumFVCosine(papers, paperId, papers.get(paperId).getCitation());
            }
        } else if (weightingScheme == 2) {
            if (combiningScheme == 1) {
                sumFVRPY(papers, paperId, papers.get(paperId).getReference());
            } else if (combiningScheme == 2) {
                sumFVRPY(papers, paperId, papers.get(paperId).getCitation());
            } else if (combiningScheme == 3) {
                sumFVRPY(papers, paperId, papers.get(paperId).getReference());
                sumFVRPY(papers, paperId, papers.get(paperId).getCitation());
            }
        }
    }

    /**
     * This method compute sum of Papers(Citation or Reference Paper) with
     * linear weight
     *
     * @param combiningPaperIds
     * @return featureVector
     */
    private static void sumFVLinear(HashMap<String, Paper> papers, String paperId, List<String> combiningPaperIds) throws Exception {
        for (String combiningPaperId : combiningPaperIds) {
            if (papers.containsKey(combiningPaperId)) {
                papers.get(paperId).getFeatureVector().add(papers.get(combiningPaperId).getTfidfVector());
            }
        }
    }

    /**
     * This method compute sum of Papers(Citation or Reference Paper) with
     * cosine weight
     *
     * @param paper
     * @param combiningPaperIds
     * @return featureVector
     */
    private static void sumFVCosine(HashMap<String, Paper> papers, String paperId, List<String> combiningPaperIds) throws Exception {
        for (String combiningPaperId : combiningPaperIds) {
            if (papers.containsKey(combiningPaperId)) {
                double cosine = WeightingUtility.computeCosine(papers.get(paperId).getTfidfVector(), papers.get(combiningPaperId).getTfidfVector());
                papers.get(paperId).getFeatureVector().addScaled(papers.get(combiningPaperId).getTfidfVector(), cosine);
            }
        }
    }

    /**
     * This method compute sum of Papers(Citation or Reference Paper) with rpy
     * weight
     *
     * @param paper
     * @param combiningPaperIds
     * @return featureVector
     */
    private static void sumFVRPY(HashMap<String, Paper> papers, String paperId, List<String> combiningPaperIds) throws Exception {
        for (String combiningPaperId : combiningPaperIds) {
            if (papers.containsKey(combiningPaperId)) {
                double rpy = WeightingUtility.computeRPY(papers.get(paperId).getYear(), papers.get(combiningPaperId).getYear(), 0.9);
                papers.get(paperId).getFeatureVector().addScaled(papers.get(combiningPaperId).getTfidfVector(), rpy);
            }
        }
    }
}
