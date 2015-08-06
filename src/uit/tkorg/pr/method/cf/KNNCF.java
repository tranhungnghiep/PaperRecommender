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
import java.util.HashSet;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import uit.tkorg.pr.method.GenericRecommender;
import uit.tkorg.pr.model.Author;
import uit.tkorg.pr.model.Paper;

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
        //StringBuffer buff = new StringBuffer();
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

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
                    //buff.append(userId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue() + "\r\n");
                    bw.write(userId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue() + "\r\n");
                }
            }
            count++;
        }
        bw.close();
        //FileUtils.writeStringToFile(new File(outputFile), buff.toString(), "UTF8", true);
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

    /**
     * 
     * @param inputFile
     * @param similarityScheme: 1: CoPearson, 2: Cosine.
     * @param k
     * @param authorTestSet
     * @param paperIdsInTestSet
     * @param outputFile
     * @throws IOException
     * @throws TasteException 
     */
    public static void computeCFRatingAndPutIntoModelForAuthorList(String inputFile, int similarityScheme, 
            int k, HashMap<String, Author> authorTestSet, HashSet<String> paperIdsInTestSet, 
            String outputFile) throws IOException, TasteException {
        DataModel dataModel = new FileDataModel(new File(inputFile));

        UserSimilarity userSimilarity = null;
        if (similarityScheme == 1) {
            userSimilarity = new PearsonCorrelationSimilarity(dataModel);
        } else if (similarityScheme == 2) {
            userSimilarity = new UncenteredCosineSimilarity(dataModel);
        } else if (similarityScheme == 3) {
            // For no rating Binary CF and beyond: http://kickstarthadoop.blogspot.jp/2011/05/generating-recommendations-with-mahout_26.html
            userSimilarity = new LogLikelihoodSimilarity(dataModel);
        }

        // Note: recommendation is drawn from neighbor's rated items.
        // So, if the number of neighbor k is too small, there is not enough information about items
        // --> Thus, some users will have no recommendation.
        // Note 2: Cosine (Uncentered Cosine) has more recommendation than Pearson, 
        // but less accuracy.
        // (explain: With Pearson, many authors have no recommendation, accuracy is not computed for author with no recommendation)
        // Note 3: Pearson is Cosine with data centered to 0 
        // (http://archive.cloudera.com/cdh4/cdh/4/mahout-0.7-cdh4.1.5/mahout-core/org/apache/mahout/cf/taste/impl/similarity/PearsonCorrelationSimilarity.html)
        // Note 4: SVD has most recommendation. All items are computed,
        // but the accuracy is worst.
        // Note 5: LogLikelihoodSimilarity is good for hasRating or noRating, and don't have many issues of others.
        // suggested to used as default by (https://stackoverflow.com/a/2796019/1259561)
        // => Basically, LogLikelihood similarity seems to solve the problem:
        // - Recommendation lists are almost full for all authors.
        // - Accuracy is still good, even better than Pearson.
        // But CBF-CF combination is still worse than CBF: 
        // -> Explain: CBF is too good, CF is just a part of CBF?
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(k, userSimilarity, dataModel);

        // Create a generic user based recommender with the dataModel, the userNeighborhood and the userSimilarity
        Recommender genericRecommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, userSimilarity);
        
        FileUtils.deleteQuietly(new File(outputFile));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            int count = 0;
            System.out.println("Number of users:" + authorTestSet.size());
            for (LongPrimitiveIterator iterator = dataModel.getUserIDs(); iterator.hasNext();) {
                long userId = iterator.nextLong();
                // Generate a list of n recommendations for the user
                if (authorTestSet.containsKey(String.valueOf(userId).trim())) {
                    System.out.println("Computing CF rating value for user no. " + count);
                    // Recommendation list contains all papers in Matrix.
                    List<RecommendedItem> recommendationList = genericRecommender.recommend(userId, dataModel.getNumItems());
                    if (!recommendationList.isEmpty()) {
                        for (RecommendedItem recommendedItem : recommendationList) {
                            String authorId = String.valueOf(userId).trim();
                            String paperId = String.valueOf(recommendedItem.getItemID()).trim();
                            if (paperIdsInTestSet.contains(paperId)) {
                                // Filter out: Only consider paper in testset (ground truth).
                                authorTestSet.get(authorId).getCfRatingHM()
                                        .put(paperId, recommendedItem.getValue());
                                bw.write(userId + "," + recommendedItem.getItemID() + "," + recommendedItem.getValue() + "\r\n");
                            }
                        }
                    }
                    count++;
                }
            }
        }
    }
}
