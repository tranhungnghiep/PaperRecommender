/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author THNghiep
 */
public class Serializer {
    /**
     * Serialize.
     */
    public void saveObjectToFile(Object o, String fileName) {
      try {
         FileOutputStream fileOut = new FileOutputStream(fileName);
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(o);
         out.close();
         fileOut.close();
      } catch(IOException i) {
          i.printStackTrace();
      }
    }
    
    /**
     * Deserialize.
     */
    public Object loadObjectFromFile(String fileName) {
      Object o = null;
      try {
         FileInputStream fileIn = new FileInputStream(fileName);
         ObjectInputStream in = new ObjectInputStream(fileIn);
         o = in.readObject();
         in.close();
         fileIn.close();
         return o;
      } catch(IOException i) {
         i.printStackTrace();
         return null;
      } catch(ClassNotFoundException c) {
         c.printStackTrace();
         return null;
      }
    }
}
