/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.textvectorization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.constant.PRConstant;
import uit.tkorg.utility.general.TextFileUtility;
import weka.core.Stopwords;
import weka.core.stemmers.IteratedLovinsStemmer;
import weka.core.tokenizers.WordTokenizer;

/**
 *
 * @author tin
 */
public class TextPreprocessUtility {

    /**
     * Remove stop-words and do stemming, and then out to TXT file
     *
     * @param fileInput
     * @param fileName
     */
    private static void process(String fileInput, String fileOutput, boolean isStem) {
//        System.out.println(fileInput);

        ArrayList<String> processedWordList = null;
        StringBuffer strBuffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(fileInput);
            Reader reader = new InputStreamReader(fis, "UTF8");
            BufferedReader bufferReader = new BufferedReader(reader);
            String line = null;
            if (isStem == true) {
                while ((line = bufferReader.readLine()) != null) {
                    processedWordList = removeStopWordAndStemming(line);
                    for (String word : processedWordList) {
                        strBuffer.append(word + " ");
                    }
                    strBuffer.append("\n");
                }
            }
            else {
                while ((line = bufferReader.readLine()) != null) {
                    processedWordList = removeStopWord(line);
                    for (String word : processedWordList) {
                        strBuffer.append(word + " ");
                    }
                    strBuffer.append("\n");
                }
            }

            FileUtils.writeStringToFile(new File(fileOutput), strBuffer.toString(), "UTF8", false);
//            TextFileUtility.writeTextFile(fileOutput, strBuffer.toString());

            bufferReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param rootPathInput
     * @param rootPathOutput
     * @param overwrite
     * @param isStem
     * @throws Exception 
     */
    public static void parallelProcess(String rootPathInput, String rootPathOutput, boolean overwrite, final boolean isStem) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        int numOfProcessors = runtime.availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);


        File output = new File(rootPathOutput);
        if (!output.exists()) {
            output.mkdirs();
        } else if (overwrite) {
            FileUtils.deleteQuietly(output);
            output.mkdirs();
        } else {
            throw new Exception("The output folder is already existing.");
        }
        
        List<String> listFiles = TextFileUtility.getPathFile(new File(rootPathInput));
        
        for (int i = 0; i < listFiles.size(); i++) {
            final String fileInput = listFiles.get(i);
            final String fileOutput = fileInput.replace(rootPathInput, rootPathOutput);
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    process(fileInput, fileOutput, isStem);
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {}
    }

    public static ArrayList<String> removeStopWord(String str) throws Exception {
        ArrayList<String> result = new ArrayList<String>();
        WordTokenizer wordTokenizer = new WordTokenizer();
        String delimiters = " \r\t\n.,;:\'\"()?!-><#$\\%&*+/@^_=[]{}|`~0123456789·‘’“”\\«ª©¯¬£¢§™•ϵϕ­ ´";
        wordTokenizer.setDelimiters(delimiters);
        wordTokenizer.tokenize(str);
        try {
            String token;
            while (wordTokenizer.hasMoreElements()) {
                token = wordTokenizer.nextElement().toString();
                if (!Stopwords.isStopword(token.toLowerCase()) && token.length() > 1
                        && token.length() < 15) {
                    result.add(token);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> removeStopWordAndStemming(String str) {
        ArrayList<String> result = new ArrayList<String>();
        WordTokenizer wordTokenizer = new WordTokenizer();
        IteratedLovinsStemmer stemmerLovin = new IteratedLovinsStemmer();
        String delimiters = " \r\t\n.,;:\'\"()?!-><#$\\%&*+/@^_=[]{}|`~0123456789·‘’“”\\«ª©¯¬£¢§™•ϵϕ­ ´";
        wordTokenizer.setDelimiters(delimiters);
        wordTokenizer.tokenize(str);
        try {
            String token;
            while (wordTokenizer.hasMoreElements()) {
                token = wordTokenizer.nextElement().toString();
                if (!Stopwords.isStopword(token.toLowerCase()) && token.length() > 1
                        && token.length() < 15) {
                    token = stemmerLovin.stem(token);
                    result.add(token);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    public static void main(String[] args) throws Exception {
        new TextPreprocessUtility().parallelProcess(PRConstant.FOLDER_MAS_DATASET1 
                + "Test Compute TFIDF\\text", 
                PRConstant.FOLDER_MAS_DATASET1 
                + "Test Compute TFIDF\\Removed stopword and stemming text", true, true);
    }
}
