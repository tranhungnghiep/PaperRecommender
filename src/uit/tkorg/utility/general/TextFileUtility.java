/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.general;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author tin
 */
public class TextFileUtility {

    /**
     * writeTextFile
     *
     * @param textFilePath
     * @param appendText
     */
    public static void writeTextFile(String textFilePath, String textContent) {
        try {
            FileOutputStream fos = new FileOutputStream(textFilePath);
            Writer out = new OutputStreamWriter(fos, "UTF8");
            out.write(textContent);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void writeTextFile(String textFile, HashMap<Integer,Float> hashMap) {
        try {
            FileWriter fstream = new FileWriter(textFile, false);
            BufferedWriter out = new BufferedWriter(fstream);
            StringBuffer buff = new StringBuffer();
            buff.append("Key" + "\t" + "Value" + "\n");
            for (int key : hashMap.keySet()) {
                buff.append(key + "\t" + hashMap.get(key) + "\n");
            }
            out.write(buff.toString());
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void writeTextFileFromHM(String textFilePath, HashMap<Integer, HashMap<Integer, Float>> hashMap) {
        try {
            FileWriter fstream = new FileWriter(textFilePath, false);
            BufferedWriter out = new BufferedWriter(fstream);
            StringBuffer buff = new StringBuffer();
            for (Integer instanceID1 : hashMap.keySet()) {
                buff.append(instanceID1 + "#");
                Set<Integer> otherInstances = (hashMap.get(instanceID1)).keySet();
                for (Integer instanceID2 : otherInstances) {
                    float value = (hashMap.get(instanceID1)).get(instanceID2);
                    buff.append(instanceID2 + ":" + value + ";");
                }
                buff.append("\n");
            }

            out.write(buff.toString());
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * appendTextFile
     *
     * @param textFilePath
     * @param appendText
     */
    public static void appendTextFile(String textFilePath, String appendText) {
        try {
            // Create file
            FileWriter fstream = new FileWriter(textFilePath, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(appendText);
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * read a Text file with UTF8 format,
     *
     * @param filePath
     */
    public static String readTextFile(String filePath) {
        StringBuffer strBuffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            Reader reader = new InputStreamReader(fis, "UTF8");
            BufferedReader bufferReader = new BufferedReader(reader);
            bufferReader.readLine(); // skip the first line
            String line = null;
            while ((line = bufferReader.readLine()) != null) {
                strBuffer.append(line + " ");
            }
            bufferReader.close();
        } catch (Exception e) {
        }

        return strBuffer.toString();
    }

    /**
     * Splitting a text file for many piece with 5.000 rows/file
     *
     * @param filePath
     */
    public static void splitTextFile(String filePath) {
        int count = 1;
        StringBuffer strBufferSpliter = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            Reader reader = new InputStreamReader(fis, "UTF8");
            BufferedReader bufferReader = new BufferedReader(reader);
            bufferReader.readLine(); // skip the first line
            String line = null;
            String path = null;
            String fileNamePiece = null;
            while ((line = bufferReader.readLine()) != null) {
                strBufferSpliter.append(line + "\n");
                if (count % 5000 == 0) {
                    path = (new File(filePath)).getParent();
                    fileNamePiece = path + "\\file_" + (count / 5000) + ".txt";
                    TextFileUtility.writeTextFile(fileNamePiece, strBufferSpliter.toString());
                    strBufferSpliter = new StringBuffer();
                }
                count++;
            }

            path = (new File(filePath)).getParent();
            fileNamePiece = path + "\\file_" + (count / 5000 + 1)  + ".txt";
            TextFileUtility.writeTextFile(fileNamePiece, strBufferSpliter.toString());

            bufferReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     *
     * @param dir: directory containing txt and dat files.
     * @return list of full path of txt and dat files in the directory including subdirectory.
     */
    public static List<String> getPathFile(File dir) throws Exception {
        File[] files = dir.listFiles();
        List<String> listFiles = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                String name = file.getName();
                if (name.endsWith(".txt") || name.endsWith(".dat")) {
                    listFiles.add(file.getAbsolutePath());
                }
            } else {
                listFiles.addAll(getPathFile(file));
            }
        }
        return listFiles;
    }

    public static void checkAndCreateParentDirs(String filePath) throws Exception {
        File f = new File(filePath);
        if (!(f.getParentFile().exists())) {
            f.getParentFile().mkdirs();
        }
    }
    public static void main(String args[]) {
        try {
            splitTextFile("C:\\CRS-Experiment\\MAS\\Content\\LDA\\Stemming\\CRS-InputParallelLDA.txt");
            System.out.printf("DONE");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
