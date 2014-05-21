/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.utility.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author THNghiep
 */
public class IOUtility {

    // Prevent instantiation.
    private IOUtility() {}

    /**
     * Serialize.
     */
    public static void saveObjectToFile(Object o, String fileName) throws Exception {
        try (
                FileOutputStream fileOut = new FileOutputStream(fileName); 
                ObjectOutputStream out = new ObjectOutputStream(fileOut)
                ) {
            out.writeObject(o);
        }
    }
    
    /**
     * Deserialize.
     */
    public static Object loadObjectFromFile(String fileName) throws Exception {
        Object o = null;
        try (
                FileInputStream fileIn = new FileInputStream(fileName); 
                ObjectInputStream in = new ObjectInputStream(fileIn)
                ) {
            o = in.readObject();
        }
        return o;
    }
    
    /**
     *
     * @param dir
     * @return list file
     */
    public static List<String> getPathFile(File dir) throws Exception {
        File[] files = dir.listFiles();
        List listfile = new ArrayList<String>();
        String name = null;
        for (File file : files) {
            if (file.isDirectory()) {
                getPathFile(file);
            } else if (file.isFile()) {
                name = file.getName().toString();
            }
            if ("*.txt".equals(name)) {
                listfile.add(file.getAbsolutePath());
            }
        }
        return listfile;
    }
}
