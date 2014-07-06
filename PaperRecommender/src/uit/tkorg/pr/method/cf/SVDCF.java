/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.method.cf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.*;
import org.apache.mahout.cf.taste.impl.recommender.svd.*;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import uit.tkorg.pr.model.Author;

/**
 *
 * @author Minh
 */
public class SVDCF {

    /**
     *
     * @param inputFile
     * @param n
     * @param numFeatures
     * @param lamda
     * @param numIterations
     * @param outputFile
     * @throws IOException
     * @throws TasteException
     */
    public static void SVDRecommendation(String inputFile, int n, int numFeatures, double lamda, int numIterations, String outputFile) throws IOException, TasteException {
        DataModel dataModel = new FileDataModel(new File(inputFile));
        Factorizer factorizer = new ALSWRFactorizer(dataModel, numFeatures, lamda, numIterations);
        Recommender svdRecommender = new SVDRecommender(dataModel, factorizer);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        // Recommend n items for each user
        for (LongPrimitiveIterator iterator = dataModel.getUserIDs(); iterator.hasNext();) {
            long userId = iterator.nextLong();

            // Generate a list of n recommendations for the user
            List<RecommendedItem> topItems = svdRecommender.recommend(userId, n);
            if (!topItems.isEmpty()) {
                // Display the list of recommendations
                for (RecommendedItem recommendedItem : topItems) {
                    bw.write(userId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue() + "\r\n");
                }
            }
        }
        bw.close();
    }

    public static void SVDRecommendationToAuthorList(String inputFile, int n, int numFeatures, double lamda, int numIterations, HashMap<String, Author> authorTestSet, String outputFile) throws IOException, TasteException {
        DataModel dataModel = new FileDataModel(new File(inputFile));
        Factorizer factorizer = new ALSWRFactorizer(dataModel, numFeatures, lamda, numIterations);
        Recommender svdRecommender = new SVDRecommender(dataModel, factorizer);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        // Recommend n items for each user
        int count = 0;
        System.out.println("Number of users:" + authorTestSet.size());
        for (LongPrimitiveIterator iterator = dataModel.getUserIDs(); iterator.hasNext();) {
            long userId = iterator.nextLong();
            // Generate a list of n recommendations for the user
            if (authorTestSet.containsKey(String.valueOf(userId))) {
                System.out.println("Generate a list of n recommendations for the user no. " + count);
                List<RecommendedItem> topItems = svdRecommender.recommend(userId, n);
                if (!topItems.isEmpty()) {
                    // Display the list of recommendations
                    for (RecommendedItem recommendedItem : topItems) {
                        authorTestSet.get(String.valueOf(userId)).getRecommendationList().add(String.valueOf(recommendedItem.getItemID()));
                        authorTestSet.get(String.valueOf(userId)).getRecommendationValue().put(String.valueOf(recommendedItem.getItemID()), Double.valueOf(recommendedItem.getValue()));
                        bw.write(userId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue() + "\r\n");
                    }
                }
                count++;
            }
        }
        bw.close();
    }
}
