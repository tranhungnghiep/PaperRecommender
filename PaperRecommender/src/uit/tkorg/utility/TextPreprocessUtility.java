/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
     * @param pathDir
     * @param fileName
     */
    private void process(String pathDir, String fileName, String pathStemmingResult, boolean isStem) {
        System.out.println(fileName);

        String fileContent = null;
        ArrayList<String> processedWordList = null;
        StringBuffer strBuffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(pathDir + "\\" + fileName);
            Reader reader = new InputStreamReader(fis, "UTF8");
            BufferedReader bufferReader = new BufferedReader(reader);
            bufferReader.readLine(); // skip the header file
            String line = null;
            int lineCount = 0;
            if (isStem == true) {
                while ((line = bufferReader.readLine()) != null) {
                    processedWordList = removeStopWordAndStemming(line);
                    for (String word : processedWordList) {
                        strBuffer.append(word + " ");
                    }
                    strBuffer.append("\n");
                    lineCount++;
                }
            }
            else {
                while ((line = bufferReader.readLine()) != null) {
                    processedWordList = removeStopWord(line);
                    for (String word : processedWordList) {
                        strBuffer.append(word + " ");
                    }
                    strBuffer.append("\n");
                    lineCount++;
                }
            }


            strBuffer = strBuffer.insert(0, lineCount + "\n");
            TextFileUtility.writeTextFile(pathStemmingResult + "\\OutStem_"
                    + fileName + ".dat", strBuffer.toString());

            bufferReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parallelProcess(String rootPath, final String pathDir, String subFolderName, final boolean isStem) {
        Runtime runtime = Runtime.getRuntime();
        int numOfProcessors = runtime.availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numOfProcessors - 1);

        (new File(rootPath + "\\" + "OutStem")).mkdir();
        (new File(rootPath + "\\" + "OutStem" + "\\" + subFolderName)).mkdir();
        final String pathStemmingResult = rootPath + "\\" + "OutStem" + "\\" + subFolderName;

        File mainFolder = new File(pathDir);
        System.out.println(mainFolder.getAbsolutePath());
        File[] fList = mainFolder.listFiles();
        for (int i = 0; i < fList.length; i++) {
            if (fList[i].isFile()) {
                final String fileName = fList[i].getName();
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        process(pathDir, fileName, pathStemmingResult, isStem);
                    }
                });
            }
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    public ArrayList<String> removeStopWord(String str) throws Exception {
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

    public ArrayList<String> removeStopWordAndStemming(String str) {
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
}
