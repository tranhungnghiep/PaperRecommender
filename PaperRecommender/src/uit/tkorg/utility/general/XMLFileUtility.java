/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.utility.general;

import java.io.File;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author tin
 */
public class XMLFileUtility {
     /**
     * readXMLFile(File xmlFile)
     * @param xmlFile
     * @return
     */
    public static Document readXMLFile(File xmlFile)  {
        Document doc = null;
        try {
            SAXReader reader = new SAXReader();
            doc = reader.read(xmlFile);
            return doc;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return doc;
    }

    /**
     * readXMLFile
     * @param filePath
     * @return
     */
    public static Document readXMLFile(String filePath)  {
        Document doc = null;
        try {
            File xml = new File(filePath);
            SAXReader reader = new SAXReader();
            doc = reader.read(xml);
            return doc;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return doc;
    }

    /**
     * writeXMLFile
     * @param filePath
     * @param doc
     */
    public static void writeXMLFile(String filePath, Document doc) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(fos, format);
            writer.write(doc);
            writer.flush();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
