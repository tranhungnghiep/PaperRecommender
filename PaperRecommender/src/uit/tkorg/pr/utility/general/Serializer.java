/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.utility.general;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author THNghiep
 * Support class for serializing and deserializing.
 */
public class Serializer {

    // Prevent instantiation.
    private Serializer() {}

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
}
