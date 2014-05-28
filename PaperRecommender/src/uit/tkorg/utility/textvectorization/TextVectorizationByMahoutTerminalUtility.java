/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.textvectorization;

import ir.vsr.HashMapVector;
import java.util.HashMap;
import org.apache.mahout.text.SequenceFilesFromDirectory;
import org.apache.mahout.vectorizer.SparseVectorsFromSequenceFiles;
import uit.tkorg.pr.constant.PRConstant;
import uit.tkorg.pr.dataimex.MahoutFile;

/**
 *
 * @author THNghiep
 */
public class TextVectorizationByMahoutTerminalUtility {
    
    private TextVectorizationByMahoutTerminalUtility() {}

    public static void textVectorizeFiles(String textDir, String sequenceDir, String vectorDir) throws Exception {
        textToSequenceFiles(textDir, sequenceDir);
        sequenceToVectorFiles(sequenceDir, vectorDir);
    }

    /**
     *
     * @param textDir
     * @param sequenceDir
     * @return
     * @throws Exception 
     * Using mahout terminal driver.
     */
    public static int textToSequenceFiles(String textDir, String sequenceDir) throws Exception {
        String[] params = {"-i", textDir, "-o", sequenceDir, "-ow", "-xm", "sequential", "-chunk", "8000"};
        int status = new SequenceFilesFromDirectory().run(params);
        return status;
    }

    /**
     * @param sequenceDir
     * @param vectorDir
     * @return
     * @throws Exception
     */
    public static int sequenceToVectorFiles(String sequenceDir, String vectorDir) throws Exception {
        String[] params = {"-i", sequenceDir, "-o", vectorDir, "-ow", "-wt", "tfidf", "-s", "1", "-md", "1", "-x", "100", "-xs", "3.0", "-ng", "2", "-ml", "50", "-chunk", "8000", "-n", "2"};
        int status = new SparseVectorsFromSequenceFiles().run(params);
        return status;
    }

    public static void main(String[] args) throws Exception {
        // Prepare input documents in text folder, each document in a .txt file, file name is document id.
        textVectorizeFiles(PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\Removed stopword and stemming text", PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\sequence", PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\vector");
        HashMap<Integer, String> dictionary = MahoutFile.readMahoutDictionaryFiles(PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\vector");
        HashMap<String, HashMapVector> vectorizedDocuments = MahoutFile.readMahoutVectorFiles(PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\vector");
    }
}