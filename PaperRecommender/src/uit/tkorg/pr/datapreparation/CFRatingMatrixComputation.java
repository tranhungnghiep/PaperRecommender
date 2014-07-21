/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.datapreparation;

import java.io.File;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.model.Paper;

/**
 *
 * @author THNghiep
 */
public class CFRatingMatrixComputation {
    // Prevent instantiation.

    private CFRatingMatrixComputation() {
    }

    public static void normalizeAuthorRatingVector(HashMap<String, HashMap<String, Double>> authorPaperRating) throws Exception {
        HashMap<String, Double> numAllAuthorCite = computeNumAllAuthorCite(authorPaperRating);

        for (String authorId : authorPaperRating.keySet()) {
            for (String paperId : authorPaperRating.get(authorId).keySet()) {
                Double normalizedRating = authorPaperRating.get(authorId).get(paperId) / numAllAuthorCite.get(authorId);
                authorPaperRating.get(authorId).put(paperId, normalizedRating);
            }
        }
    }

    private static HashMap<String, Double> computeNumAllAuthorCite(HashMap<String, HashMap<String, Double>> authorPaperRating) throws Exception {
        HashMap<String, Double> numAllAuthorCite = new HashMap<>();

        for (String authorId : authorPaperRating.keySet()) {
            double numAuthorCite = 0.0;
            for (String paperId : authorPaperRating.get(authorId).keySet()) {
                numAuthorCite += authorPaperRating.get(authorId).get(paperId);
            }
            numAllAuthorCite.put(authorId, new Double(numAuthorCite));
        }

        return numAllAuthorCite;
    }

    public static void writeCFRatingToMahoutFormatFile(HashMap<String, HashMap<String, Double>> authorPaperRating, String fileNameCFRatingMahoutFormatFile) throws Exception {
        FileUtils.deleteQuietly(new File(fileNameCFRatingMahoutFormatFile));
        StringBuilder content = new StringBuilder();
        for (String authorId : authorPaperRating.keySet()) {
            for (String paperId : authorPaperRating.get(authorId).keySet()) {
                content.append(authorId).append(",")
                        .append(paperId).append(",")
                        .append(authorPaperRating.get(authorId).get(paperId).toString())
                        .append("\r\n");
            }
        }
        FileUtils.writeStringToFile(new File(fileNameCFRatingMahoutFormatFile), content.toString(), "UTF8", true);
    }
}
