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
import org.apache.commons.io.FileUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import uit.tkorg.pr.model.Author;

/**
 *
 * @author Vinh
 */
public class KNNCF {

    /**
     * This method write recommend list into output file.
     *
     * @param inputFile
     * @param k
     * @param n
     * @param outputFile
     * @throws IOException
     * @throws TasteException
     */
    public static void CoPearsonRecommend(String inputFile, int k, int n, String outputFile) throws IOException, TasteException {
        File userPreferencesFile = new File(inputFile);
        DataModel dataModel = new FileDataModel(userPreferencesFile);

        UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(dataModel);
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(k, userSimilarity, dataModel);

        // Create a generic user based recommender with the dataModel, the userNeighborhood and the userSimilarity
        Recommender genericRecommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, userSimilarity);
        StringBuffer buff = new StringBuffer();
        //BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        // Recommend n items for each user
        int count = 0;
        System.out.println("Number of users:" + dataModel.getNumUsers());
        for (LongPrimitiveIterator iterator = dataModel.getUserIDs(); iterator.hasNext();) {
            long userId = iterator.nextLong();

            // Generate a list of n recommendations for the user
            if (count % 1000 == 0)
                System.out.println("Generate a list of n recommendations for the user no." + count);
            List<RecommendedItem> itemRecommendations = genericRecommender.recommend(userId, n);
            if (!itemRecommendations.isEmpty()) {
                // Display the list of recommendations
                for (RecommendedItem recommendedItem : itemRecommendations) {
                    buff.append(userId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue() + "\r\n");
                    //bw.write(userId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue() + "\r\n");
                }
            }
            count++;
        }
        //bw.close();
        FileUtils.writeStringToFile(new File(outputFile), buff.toString(), "UTF8", true);
    }

    public static void CosineRecommend(String inputFile, int k, int n, String outputFile) throws IOException, TasteException {
        File userPreferencesFile = new File(inputFile);
        DataModel dataModel = new FileDataModel(userPreferencesFile);

        UserSimilarity userSimilarity = new UncenteredCosineSimilarity(dataModel);
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(k, userSimilarity, dataModel);

        // Create a generic user based recommender with the dataModel, the userNeighborhood and the userSimilarity
        Recommender genericRecommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, userSimilarity);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        // Recommend 5 items for each user
        for (LongPrimitiveIterator iterator = dataModel.getUserIDs(); iterator.hasNext();) {
            long userId = iterator.nextLong();

            // Generate a list of 5 recommendations for the user
            List<RecommendedItem> itemRecommendations = genericRecommender.recommend(userId, n);

            if (!itemRecommendations.isEmpty()) {
                // Display the list of recommendations
                for (RecommendedItem recommendedItem : itemRecommendations) {
                    bw.write(userId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue() + "\r\n");
                }
            }
        }
        bw.close();
    }

    public static void CoPearsonRecommendToAuthorList(String inputFile, int k, int n, HashMap<String, Author> authorTestSet, String outputFile) throws IOException, TasteException {
        File userPreferencesFile = new File(inputFile);
        DataModel dataModel = new FileDataModel(userPreferencesFile);

        UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(dataModel);
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(k, userSimilarity, dataModel);

        // Create a generic user based recommender with the dataModel, the userNeighborhood and the userSimilarity
        Recommender genericRecommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, userSimilarity);
        StringBuilder strBuilder = new StringBuilder();
        //BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        // Recommend n items for each user
        int count = 0;
        System.out.println("Number of users:" + authorTestSet.size());
        for (LongPrimitiveIterator iterator = dataModel.getUserIDs(); iterator.hasNext();) {
            long userId = iterator.nextLong();
            // Generate a list of n recommendations for the user
            if (authorTestSet.containsKey(String.valueOf(userId))) {
                System.out.println("Generate a list of n recommendations for the user no. " + count);
                List<RecommendedItem> itemRecommendations = genericRecommender.recommend(userId, n);
                if (!itemRecommendations.isEmpty()) {
                    // Display the list of recommendations
                    for (RecommendedItem recommendedItem : itemRecommendations) {
                        authorTestSet.get(String.valueOf(userId)).getRecommendationList().add(String.valueOf(recommendedItem.getItemID()));
                        strBuilder.append(userId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue() + "\r\n");
                        //bw.write(userId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue() + "\r\n");
                    }
                }
                count++;
            }
        }
        //bw.close();
        FileUtils.writeStringToFile(new File(outputFile), strBuilder.toString(), "UTF8", true);
    }

    public static void CosineRecommendToAuthorList(String inputFile, int k, int n, HashMap<String, Author> authorTestSet, String outputFile) throws IOException, TasteException {
        File userPreferencesFile = new File(inputFile);
        DataModel dataModel = new FileDataModel(userPreferencesFile);

        UserSimilarity userSimilarity = new UncenteredCosineSimilarity(dataModel);
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(k, userSimilarity, dataModel);

        // Create a generic user based recommender with the dataModel, the userNeighborhood and the userSimilarity
        Recommender genericRecommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, userSimilarity);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        // Recommend n items for each user
        int count = 0;
        System.out.println("Number of users:" + authorTestSet.size());
        for (LongPrimitiveIterator iterator = dataModel.getUserIDs(); iterator.hasNext();) {
            long userId = iterator.nextLong();
            // Generate a list of n recommendations for the user
            if (authorTestSet.containsKey(String.valueOf(userId))) {
                System.out.println("Generate a list of n recommendations for the user no. " + count);
                List<RecommendedItem> itemRecommendations = genericRecommender.recommend(userId, n);
                if (!itemRecommendations.isEmpty()) {
                    // Display the list of recommendations
                    for (RecommendedItem recommendedItem : itemRecommendations) {
                            authorTestSet.get(String.valueOf(userId)).getRecommendationList().add(String.valueOf(recommendedItem.getItemID()));
                            bw.write(userId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue() + "\r\n");
                    }
                }
                count++;
            }
        }
        bw.close();
    }
}
