/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obsolete.uit.tkorg.paperrecommender.utility;

import ir.utilities.Weight;
import ir.vsr.HashMapVector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Vinh 
 * In ra danh sách các bài báo khuyến nghị cho researcher Input:
 * Vector Profile của researcher, Arraylist Feature Vector của Candidate Papers
 * Output: Top 10 Papers khuyến nghị
 */
public class RecommendationPaperList {

    HashMapVector hmvector_user = new HashMapVector();
    ArrayList<HashMapVector> array_candidatepaper = new ArrayList<HashMapVector>();
    HashMap<String, Double> hashMap = new HashMap<String, Double>();
    GetDatabaseMysql getdatabase=new GetDatabaseMysql();

    //Trả về danh sách bài báo ứng viên cùng độ tương tự của nó với P_user
    public HashMap<String, Double> recommendationlist(String str) throws Exception {
        if (getdatabase.getDataIdTypeAuthor(str) == 0) {
            FormulaJuniorResearcher junior_researcher = new FormulaJuniorResearcher();
            hmvector_user = junior_researcher.putFormulaJunior(str);
        } else {
            FormulaSeniorResearcher senior_researcher = new FormulaSeniorResearcher();
            hmvector_user = senior_researcher.putFormulaSenior(str);
        }
        FormulaCandiatePaper candidate_paper = new FormulaCandiatePaper();
        array_candidatepaper = candidate_paper.papercandidate();
        for (int j = 0; j < array_candidatepaper.size(); j++) {
            hashMap.put(candidate_paper.paper_candidate.get(j).getIdPaper(), CalculatorFeatureVector.cosine2vector(hmvector_user, array_candidatepaper.get(j)));
        }
        return hashMap;
    }

    //Sắp xếp danh sách bài báo
    public Map sortRecommendationPaperList(HashMap<String, Double> hashMap) {
        List list = new LinkedList(hashMap.entrySet());
        //sort list based on comparator
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        //put sorted list into map again
        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
