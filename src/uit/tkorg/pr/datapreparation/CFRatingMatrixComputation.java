/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation;

import java.io.File;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.model.Paper;
import uit.tkorg.utility.general.HashMapUtility;

/**
 *
 * @author THNghiep
 */
public class CFRatingMatrixComputation {
    // Prevent instantiation.

    private CFRatingMatrixComputation() {
    }

    /**
     * Keynotes:
     * - Data have the form of general rec data, such as movie rec data, rating 1, 2, ... 5
     * - 0 is not rating, can not normalized to 0.
     * - preference can be empty: only yes/no. -> implement more option params when saving matrix and no need to normalize.
     * 
     * Here, normalize V1 by divide the raw rating citation count by the total number of citation by each author.
     * 
     * @param authorPaperRating
     * @throws Exception 
     */
    public static void normalizeAuthorRatingVector(HashMap<String, HashMap<String, Float>> authorPaperRating) throws Exception {
        
        HashMap<String, Float> numAllAuthorCite = computeNumAllAuthorCite(authorPaperRating);

        for (String authorId : authorPaperRating.keySet()) {
            for (String paperId : authorPaperRating.get(authorId).keySet()) {
                float normalizedRating = authorPaperRating.get(authorId).get(paperId) / numAllAuthorCite.get(authorId);
                authorPaperRating.get(authorId).put(paperId, normalizedRating);
            }
        }
    }

    private static HashMap<String, Float> computeNumAllAuthorCite(HashMap<String, HashMap<String, Float>> authorPaperRating) throws Exception {
        
        HashMap<String, Float> numAllAuthorCite = new HashMap<>();

        for (String authorId : authorPaperRating.keySet()) {
            float numAuthorCite = 0f;
            for (String paperId : authorPaperRating.get(authorId).keySet()) {
                numAuthorCite += authorPaperRating.get(authorId).get(paperId);
            }
            numAllAuthorCite.put(authorId, numAuthorCite);
        }

        return numAllAuthorCite;
    }

    public static void normalizeAuthorRatingVectorV2(HashMap<String, HashMap<String, Float>> authorPaperRating) throws Exception {
        
        for (String authorId : authorPaperRating.keySet()) {
            HashMapUtility.minNormalizeHashMap(authorPaperRating.get(authorId));
            HashMapUtility.scaleToRangeABHashMap(authorPaperRating.get(authorId), 1, 5);
        }
    }

    public static void writeCFRatingToMahoutFormatFile(HashMap<String, HashMap<String, Float>> authorPaperRating, 
            String fileNameCFRatingMahoutFormatFile, boolean noRating) throws Exception {
        FileUtils.deleteQuietly(new File(fileNameCFRatingMahoutFormatFile));
        StringBuilder content = new StringBuilder();
        if (noRating) {
            for (String authorId : authorPaperRating.keySet()) {
                for (String paperId : authorPaperRating.get(authorId).keySet()) {
                    content.append(authorId).append(",").append(paperId).append("\r\n");
                }
            }
        } else {
            for (String authorId : authorPaperRating.keySet()) {
                for (String paperId : authorPaperRating.get(authorId).keySet()) {
                    content.append(authorId).append(",").append(paperId).append(",")
                            .append(authorPaperRating.get(authorId).get(paperId).toString())
                            .append("\r\n");
                }
            }
        }
        FileUtils.writeStringToFile(new File(fileNameCFRatingMahoutFormatFile), content.toString(), "UTF8", true);
    }
}
