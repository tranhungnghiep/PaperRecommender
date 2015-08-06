/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.textvectorization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import weka.core.Stopwords;
import weka.core.stemmers.IteratedLovinsStemmer;
import weka.core.tokenizers.WordTokenizer;

/**
 *
 * @author tin
 */
public class PreProcessMAS {

    /**
     * Remove stop-words and do stemming, and then out to TXT file
     *
     * @param fileInput
     * @param fileName
     */
    private static void process(String fileInput, String fileOutput, boolean isStem) {
        ArrayList<String> processedWordList = null;
        StringBuilder strBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                new FileInputStream(fileInput), "UTF-8"));
            BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(
                                new FileOutputStream(fileOutput, true), "UTF-8"));
            writer.write("1182744\n");
            
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                if (isStem == true) {
                    processedWordList = removeStopWordAndStemming(line);
                }
                else {
                    processedWordList = removeStopWord(line);
                }
                for (String word : processedWordList) {
                    strBuilder.append(word + " ");
                }
                strBuilder.append("\n");
            }

            writer.write(strBuilder.toString());

            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        new PreProcessMAS().process("C:\\Users\\nghiepth\\Desktop\\MAS\\MAS_doc.txt", 
                "C:\\Users\\nghiepth\\Desktop\\MAS\\MAS_doc_removedSW.txt", false);
        new PreProcessMAS().process("C:\\Users\\nghiepth\\Desktop\\MAS\\MAS_doc.txt", 
                "C:\\Users\\nghiepth\\Desktop\\MAS\\MAS_doc_removedSWandStem.txt", true);
    }
}
