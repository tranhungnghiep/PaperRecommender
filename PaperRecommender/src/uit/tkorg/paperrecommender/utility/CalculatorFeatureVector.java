/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uit.tkorg.paperrecommender.utility;

import ir.vsr.*;

/**
 *
 * @author Vinh
 * Tính tổng, cosine của vector dùng ir.vsr lib
 */
public class CalculatorFeatureVector {

    //Tính tổng 2 vector đặc trung HashMapVector
    public static HashMapVector sum2vector(HashMapVector hmvector1, HashMapVector hmvector2) {
        hmvector1.add(hmvector2);
        return hmvector1;
    }

    //Tính cosine(similarity) của 2 vector đặc trưng HashMapVector
    public static double cosine2vector(HashMapVector hmvector1, HashMapVector hmvector2) {
        return hmvector1.cosineTo(hmvector2);
    }

}
