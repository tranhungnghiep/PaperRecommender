/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.pr.method.cf.memorybased;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.*;
import org.apache.mahout.cf.taste.impl.recommender.svd.*;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

/**
 *
 * @author Minh
 */
public class SVD {

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
            if (topItems.isEmpty()) {
                bw.write(userId + "\t" + "No recommendations for this user." + "\n");
            } else {
                // Display the list of recommendations
                for (RecommendedItem recommendedItem : topItems) {
                    bw.write(userId + "\t" + recommendedItem.getItemID() + "\t" + recommendedItem.getValue() + "\n");
                }
            }
        }
        bw.close();
    }
}
