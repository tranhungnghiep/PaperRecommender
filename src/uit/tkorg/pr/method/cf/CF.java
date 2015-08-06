/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.method.cf;

import java.io.IOException;
import java.util.HashMap;
import org.apache.mahout.cf.taste.common.TasteException;
import uit.tkorg.pr.method.GenericRecommender;
import uit.tkorg.pr.model.Author;

/**
 *
 * @author THNghiep
 */
public class CF {
    public CF() {}
    
    public static void cfRecommendToAuthorList(HashMap<String, Author> authorTestSet, int topNRecommend) throws IOException, TasteException, Exception {
        GenericRecommender.generateRecommendationForAuthorList(authorTestSet, topNRecommend, 2);
    }
}
