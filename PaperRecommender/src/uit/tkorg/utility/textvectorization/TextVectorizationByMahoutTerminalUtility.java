/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.textvectorization;

import ir.vsr.HashMapVector;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.text.SequenceFilesFromDirectory;
import org.apache.mahout.vectorizer.SparseVectorsFromSequenceFiles;
import uit.tkorg.pr.constant.PRConstant;

/**
 *
 * @author THNghiep
 */
public class TextVectorizationByMahoutTerminalUtility {

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
        String[] params = {"-i", textDir, "-o", sequenceDir, "-ow", "-xm", "sequential", "-chunk", "12000"};
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
        String[] params = {"-i", sequenceDir, "-o", vectorDir, "-ow", "-wt", "tfidf", "-s", "1", "-md", "1", "-x", "100", "-xs", "-1", "-ng", "2", "-ml", "50", "-chunk", "12000", "-n", "0"};
        int status = new SparseVectorsFromSequenceFiles().run(params);
        return status;
    }

    private static HashMap<Integer, String> readMahoutDictionaryFiles(String vectorDir) throws Exception {
        HashMap<Integer, String> dictMap = new HashMap();

        Configuration conf = new Configuration();
        SequenceFile.Reader reader = new SequenceFile.Reader(FileSystem.get(conf), new Path(vectorDir + "\\dictionary.file-0"), conf);
        Text term = new Text();
        IntWritable dictKey = new IntWritable();

        // Note: sequence file mapping from term to its key code.
        // our map will map from key code to term.
        while (reader.next(term, dictKey)) {
            dictMap.put(Integer.valueOf(dictKey.toString()), term.toString());
        }
        reader.close();

        return dictMap;
    }

    private static HashMap<String, HashMapVector> readMahoutVectorFiles(String vectorDir) throws Exception {
        HashMap<String, HashMapVector> vectorizedDocuments = new HashMap<>();
        
        Configuration conf = new Configuration();
        SequenceFile.Reader reader = new SequenceFile.Reader(FileSystem.get(conf), new Path(vectorDir + "\\tfidf-vectors\\part-r-00000"), conf);
        Text key = new Text(); // document id.
        VectorWritable value = new VectorWritable(); // document content.
        while (reader.next(key, value)) {
            Vector vector = value.get();
            String documentId = key.toString();
            HashMapVector vectorContent = new HashMapVector();
            Iterator<Vector.Element> iter = vector.nonZeroes().iterator();
            while (iter.hasNext()) {
                Vector.Element element = iter.next();
                vectorContent.increment(String.valueOf(element.index()), element.get());
            }
            vectorizedDocuments.put(documentId, vectorContent);
        }
        reader.close();
        
        return vectorizedDocuments;
    }

    public static void main(String[] args) throws Exception {
        // Prepare input documents in text folder, each document in a .txt file, file name is document id.
        textVectorizeFiles(PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\Removed stopword and stemming text", PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\sequence", PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\vector");
        HashMap<Integer, String> dictionary = readMahoutDictionaryFiles(PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\vector");
        HashMap<String, HashMapVector> vectorizedDocuments = readMahoutVectorFiles(PRConstant.FOLDER_MAS_DATASET1 + "Test Compute TFIDF\\vector");
    }
}