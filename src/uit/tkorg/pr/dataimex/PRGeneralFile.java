/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.dataimex;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.model.Paper;

/**
 *
 * @author THNghiep
 */
public class PRGeneralFile {
    
    private PRGeneralFile() {}
    
    public static void writePaperAbstractToTextFile(HashMap<String, Paper> papers, String textDir) throws Exception {
        Set<String> paperIdSet = papers.keySet();
        List<String> paperIdList = new ArrayList<>(paperIdSet);
        
        Collections.sort(paperIdList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return Integer.valueOf((String) o1).compareTo(Integer.valueOf((String) o2));
            }
        });
        
        String subFolder = null;
        int i = 0;
        for (String key : paperIdList) {
            if (i % 1000 == 0) {
                subFolder = "Papers " + String.valueOf(i + 1) + " - " + String.valueOf(i + 1000);
            }
            String fileName = textDir + "\\" + subFolder + "\\" + key + ".txt";
            FileUtils.writeStringToFile(new File(fileName), papers.get(key).getPaperAbstract(), "UTF8", false);
            i++;
        }
    }

    public static void writePaperTitleAbstractToTextFile(HashMap<String, Paper> papers, String textDir) throws Exception {
        Set<String> paperIdSet = papers.keySet();
        List<String> paperIdList = new ArrayList<>(paperIdSet);
        
        Collections.sort(paperIdList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return Integer.valueOf((String) o1).compareTo(Integer.valueOf((String) o2));
            }
        });
        
        String subFolder = null;
        int i = 0;
        for (String key : paperIdList) {
            if (i % 1000 == 0) {
                subFolder = "Papers " + String.valueOf(i + 1) + " - " + String.valueOf(i + 1000);
            }
            String fileName = textDir + "\\" + subFolder + "\\" + key + ".txt";
            FileUtils.writeStringToFile(new File(fileName), papers.get(key).getPaperTitle() + " " + papers.get(key).getPaperAbstract(), "UTF8", false);
            i++;
        }
    }
}
