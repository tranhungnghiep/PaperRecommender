/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uit.tkorg.pr.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Huynh Ngoc Tin
 */
public class CoAuthorNet {
     private static CoAuthorNet _instance;

    public static CoAuthorNet getInstance() {
        if (_instance == null) {
            _instance = new CoAuthorNet();
        }
        return _instance;
    }
    
    public HashMap<Integer, HashMap<Integer, Integer>> coAuthorNet;
    public HashMap<Integer, HashMap<Integer, Float>> rssNet; //weighted, directed graph
    public HashMap<Integer, HashMap<Integer, Float>> rtbvsNet; //weighted, directed graph

    private CoAuthorNet() {
        coAuthorNet = new HashMap<>();
    }
    public HashMap<Integer, Integer> paperId_year;
    public HashMap<Integer, ArrayList<Integer>> authorPaper;
    public HashMap<Integer, ArrayList<Integer>> paperAuthor;

    /**
     * Load Training data from 2 text files are AuthorID_PaperID.txt and
     * PaperID_Year.txt and put into HashMaps are paperId_year, authorPaper,
     * paperAuthor
     *
     * @param fileAuthorIdPubId
     * @param filePubIdYear
     */
    public void LoadTrainingData(String fileAuthorIdPubId, String filePubIdYear) {
        try {
            paperId_year = new HashMap<>();
            FileInputStream fis = new FileInputStream(filePubIdYear);
            Reader reader = new InputStreamReader(fis, "UTF8");
            BufferedReader bufferReader = new BufferedReader(reader);
            bufferReader.readLine();
            String line = null;
            String[] tokens;
            int paperId;
            Integer year;
            while ((line = bufferReader.readLine()) != null) {
                tokens = line.split("\t");
                paperId = Integer.parseInt(tokens[0]);
                if (tokens.length <= 1) {
                    year = 0;
                } else {
                    year = Integer.parseInt(tokens[1]);
                }
                paperId_year.put(paperId, year);
            }
            bufferReader.close();
        } catch (Exception e) {
        }

        try {
            authorPaper = new HashMap<>();
            paperAuthor = new HashMap<>();
            FileInputStream fis = new FileInputStream(fileAuthorIdPubId);
            Reader reader = new InputStreamReader(fis, "UTF8");
            BufferedReader bufferReader = new BufferedReader(reader);
            bufferReader.readLine();
            String line = null;
            String[] tokens;
            int authorId;
            int paperId;
            while ((line = bufferReader.readLine()) != null) {
                tokens = line.split("\t");
                authorId = Integer.parseInt(tokens[0]);
                paperId = Integer.parseInt(tokens[1]);

                ArrayList<Integer> listPaper = authorPaper.get(authorId);
                if (listPaper == null) {
                    listPaper = new ArrayList<>();
                }
                listPaper.add(paperId);
                authorPaper.put(authorId, listPaper);

                ArrayList<Integer> listAuthor = paperAuthor.get(paperId);
                if (listAuthor == null) {
                    listAuthor = new ArrayList<>();
                }
                listAuthor.add(authorId);
                paperAuthor.put(paperId, listAuthor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Building all graphs
     *
     * @param k
     * @param year
     */
    public void BuildAllGraph(float k, int year) {
        BuildCoAuthorGraph();
        BuildingRSSGraph();
    }

    /**
     * Build graphs coAuthorGraph (weight is number of collations), rssGraph
     */
    public void BuildCoAuthorGraph() {
        for (int pubId : paperAuthor.keySet()) {
            ArrayList<Integer> listAuthors = paperAuthor.get(pubId);
            if (listAuthors.size() == 1 && !coAuthorNet.containsKey(listAuthors.get(0))) {
                coAuthorNet.put(listAuthors.get(0), new HashMap<Integer, Integer>());
            } else {
                for (int author1 : listAuthors) {
                    for (int author2 : listAuthors) {
                        if (author1 != author2) {
                            HashMap<Integer, Integer> collaboration;
                            collaboration = coAuthorNet.get(author1);
                            if (collaboration == null) {
                                collaboration = new HashMap<>();
                            }

                            Integer numofPaper = collaboration.get(author2);
                            if (numofPaper == null) {
                                numofPaper = 0;
                            }
                            numofPaper++;
                            collaboration.put(author2, numofPaper);
                            coAuthorNet.put(author1, collaboration);
                        }
                    }
                }
            }
        }
    }

    public void BuildingRSSGraph() {
        rssNet = new HashMap<>();
        for (int authorId1 : coAuthorNet.keySet()) {
            if (coAuthorNet.get(authorId1).size() == 0) {
                rssNet.put(authorId1, new HashMap<Integer, Float>());
            } else {
                int totalPaperOfAuthor1 = 0;
                for (int authorId2 : coAuthorNet.get(authorId1).keySet()) {
                    totalPaperOfAuthor1 += coAuthorNet.get(authorId1).get(authorId2);
                }

                for (int authorId2 : coAuthorNet.get(authorId1).keySet()) {
                    if (authorId1 != authorId2) {
                        float t = 0;
                        float weight = ((float) coAuthorNet.get(authorId1).get(authorId2)) / ((float) totalPaperOfAuthor1);
                        HashMap<Integer, Float> rssWeight = rssNet.get(authorId1);
                        if (rssWeight == null) {
                            rssWeight = new HashMap<>();
                        }

                        Float _weight = rssWeight.get(authorId2);
                        if (_weight == null) {
                            _weight = weight;
                            rssWeight.put(authorId2, _weight);
                        }
                        rssNet.put(authorId1, rssWeight);
                    }
                }
            }
        }
    }

    public boolean isLinkExistInRSSGraph(HashMap<Integer, HashMap<Integer, Float>> rssGraph, int authorID1, int authorID2) {
        boolean found = false;
        if (rssGraph.containsKey(authorID1)) {
            if (rssGraph.get(authorID1).containsKey(authorID2)) {
                found = true;
            }
        }
        if (rssGraph.containsKey(authorID2)) {
            if (rssGraph.get(authorID2).containsKey(authorID1)) {
                found = true;
            }
        }
        
        return found;
    }
    
    public boolean isLinkExistInFutureNet(HashMap<Integer, ArrayList<Integer>> futureGraph, int authorID1, int authorID2) {
        boolean found = false;
        if (futureGraph.containsKey(authorID1)) {
            ArrayList<Integer> listCoAuthor = futureGraph.get(authorID1);
            for (int i=0; i<listCoAuthor.size(); i++){
                int coAuthorID = listCoAuthor.get(i);
                if (coAuthorID == authorID2) {
                    found = true;
                }
            }
        }
        if (futureGraph.containsKey(authorID2)) {
            ArrayList<Integer> listCoAuthor = futureGraph.get(authorID2);
            for (int i=0; i<listCoAuthor.size(); i++){
                int coAuthorID = listCoAuthor.get(i);
                if (coAuthorID == authorID1) {
                    found = true;
                }
            }
        }
        
        return found;
    }

//    // Testing Functions of AuthorGraph
//    public static void main(String args[]) {
//        System.out.println("START LOADING TRAINING DATA");
//        AuthorGraph _graph = AuthorGraph.getInstance();
//        
//        _graph.LoadTrainingData("C:\\CRS-Experiment\\Sampledata\\[Training]AuthorId_PaperID.txt", 
//                "C:\\CRS-Experiment\\Sampledata\\[Training]PaperID_Year.txt");
//
//        // Building Graphs
//        _graph.BuidCoAuthorGraph();
//        _graph.BuildingRSSGraph();
//        
//        HashMap temp1 = _graph.coAuthorGraph;
//        HashMap temp2 = _graph.rssGraph;
//        
//        PageRank pr = new PageRank();
//        HashMap<Integer, HashMap<Integer, Float>> inLinkHM = pr.initInLinkHMFromGraph(temp2);
//        
//        System.out.println("DONE");
//    }
}
