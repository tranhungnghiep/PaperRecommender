/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.evaluation;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import uit.tkorg.pr.model.Author;

/**
 *
 * @author nghiepth
 */
public class ErrorAnalysis {
    public static void printEachAuthorEvaluationResults(HashMap<String, Author> authors, 
            String fileNameEachAuthorEvaluationResults) throws Exception {
        FileUtils.deleteQuietly(new File(fileNameEachAuthorEvaluationResults));
        StringBuilder content = new StringBuilder();
        content.append("Author ID").append("\t")
            .append("P@10").append("\t")
            .append("P@20").append("\t")
            .append("P@30").append("\t")
            .append("P@40").append("\t")
            .append("P@50").append("\t")
            .append("R@50").append("\t")
            .append("R@100").append("\t")
            .append("F1").append("\t")
            .append("AP@10").append("\t")
            .append("AP@20").append("\t")
            .append("AP@30").append("\t")
            .append("AP@40").append("\t")
            .append("AP@50").append("\t")
            .append("NDCG@5").append("\t")
            .append("NDCG@10").append("\t")
            .append("RR")
            .append("\r\n");
        for (String authorId : authors.keySet()) {
            content.append(authorId).append("\t")
                .append(authors.get(authorId).getPrecision10()).append("\t")
                .append(authors.get(authorId).getPrecision20()).append("\t")
                .append(authors.get(authorId).getPrecision30()).append("\t")
                .append(authors.get(authorId).getPrecision40()).append("\t")
                .append(authors.get(authorId).getPrecision50()).append("\t")
                .append(authors.get(authorId).getRecall50()).append("\t")
                .append(authors.get(authorId).getRecall100()).append("\t")
                .append(authors.get(authorId).getF1()).append("\t")
                .append(authors.get(authorId).getAp10()).append("\t")
                .append(authors.get(authorId).getAp20()).append("\t")
                .append(authors.get(authorId).getAp30()).append("\t")
                .append(authors.get(authorId).getAp40()).append("\t")
                .append(authors.get(authorId).getAp50()).append("\t")
                .append(authors.get(authorId).getNdcg5()).append("\t")
                .append(authors.get(authorId).getNdcg10()).append("\t")
                .append(authors.get(authorId).getRr())
                .append("\r\n");
        }
        FileUtils.writeStringToFile(new File(fileNameEachAuthorEvaluationResults), content.toString(), "UTF8", true);
    }

    public static void printFalseNegativeTopN(HashMap<String, Author> authors, 
            String fileNameFalseNegativeTopN, int method, int topN) throws Exception {
        FileUtils.deleteQuietly(new File(fileNameFalseNegativeTopN));
        StringBuilder content = new StringBuilder();
        content.append("Author ID").append("\t")
            .append("Paper ID False Negative").append("\t")
            .append("Rank").append("\t")
            .append("Method").append("\t")
            .append("Ranking value").append("\t")
            .append("\r\n");
        for (String authorId : authors.keySet()) {
            if (topN > authors.get(authorId).getRecommendationList().size()) {
                topN = authors.get(authorId).getRecommendationList().size();
            }
            for (String paperId : (List<String>) authors.get(authorId).getGroundTruth()) {
                if (!authors.get(authorId).getRecommendationList().subList(0, topN).contains(paperId)) {
                    content.append(authorId).append("\t")
                        .append(paperId).append("\t")
                        .append(authors.get(authorId).getRecommendationList().indexOf(paperId) + 1).append("\t")
                        .append(method).append("\t")
                        .append(authors.get(authorId).getRecommendationValue().get(paperId))
                        .append("\r\n");
                }
            }
        }
        FileUtils.writeStringToFile(new File(fileNameFalseNegativeTopN), content.toString(), "UTF8", true);
    }

    public static void printFalsePositiveTopN(HashMap<String, Author> authors, String fileNameFalsePositiveTopN, int method, int topN) throws Exception {
        FileUtils.deleteQuietly(new File(fileNameFalsePositiveTopN));
        StringBuilder content = new StringBuilder();
        content.append("Author ID").append("\t")
            .append("Paper ID False Positive").append("\t")
            .append("Rank").append("\t")
            .append("Method").append("\t")
            .append("Ranking value").append("\t")
            .append("\r\n");
        for (String authorId : authors.keySet()) {
            if (topN > authors.get(authorId).getRecommendationList().size()) {
                topN = authors.get(authorId).getRecommendationList().size();
            }
            for (String paperId : (List<String>) authors.get(authorId).getRecommendationList().subList(0, topN)) {
                if (!authors.get(authorId).getGroundTruth().contains(paperId)) {
                    content.append(authorId).append("\t")
                        .append(paperId).append("\t")
                        .append(authors.get(authorId).getRecommendationList().indexOf(paperId) + 1).append("\t")
                        .append(method).append("\t")
                        .append(authors.get(authorId).getRecommendationValue().get(paperId))
                        .append("\r\n");
                }
            }
        }
        FileUtils.writeStringToFile(new File(fileNameFalsePositiveTopN), content.toString(), "UTF8", true);
    }
}
