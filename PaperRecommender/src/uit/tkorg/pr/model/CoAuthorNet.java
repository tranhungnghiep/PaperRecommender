/*
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
    public HashMap<Integer, HashMap<Integer, Integer>> _coAuthorNet;
    public HashMap<Integer, HashMap<Integer, Float>> _rssNet; //weighted, directed graph
    public HashMap<Integer, HashMap<Integer, Float>> _rtbvsNet; //weighted, directed graph
    public HashMap<Integer, Integer> _paperID_Year;
    public HashMap<Integer, ArrayList<Integer>> _authorID_PaperID;
    public HashMap<Integer, ArrayList<Integer>> _paperID_AuthorID;
    
    private static CoAuthorNet _instance;
    public static CoAuthorNet getInstance() {
        if (_instance == null) {
            _instance = new CoAuthorNet();
        }
        return _instance;
    }
    
    private CoAuthorNet() {
        _coAuthorNet = new HashMap<>();
    }

    /**
     * Load Training data from 2 text files are AuthorID_PaperID.txt and
        PaperID_Year.txt and put into HashMaps are _paperID_Year, _authorID_PaperID,
        _paperID_AuthorID
     * @param file_AuthorID_PaperID
     * @param file_PaperID_Year 
     */
    public void LoadTrainingData(String file_AuthorID_PaperID, String file_PaperID_Year) {
        try {
            _paperID_Year = new HashMap<>();
            FileInputStream fis = new FileInputStream(file_PaperID_Year);
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
                _paperID_Year.put(paperId, year);
            }
            bufferReader.close();
        } catch (Exception e) {
        }

        try {
            _authorID_PaperID = new HashMap<>();
            _paperID_AuthorID = new HashMap<>();
            FileInputStream fis = new FileInputStream(file_AuthorID_PaperID);
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

                ArrayList<Integer> listPaper = _authorID_PaperID.get(authorId);
                if (listPaper == null) {
                    listPaper = new ArrayList<>();
                }
                listPaper.add(paperId);
                _authorID_PaperID.put(authorId, listPaper);

                ArrayList<Integer> listAuthor = _paperID_AuthorID.get(paperId);
                if (listAuthor == null) {
                    listAuthor = new ArrayList<>();
                }
                listAuthor.add(authorId);
                _paperID_AuthorID.put(paperId, listAuthor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Building all graphs
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
        for (int pubId : _paperID_AuthorID.keySet()) {
            ArrayList<Integer> listAuthors = _paperID_AuthorID.get(pubId);
            if (listAuthors.size() == 1 && !_coAuthorNet.containsKey(listAuthors.get(0))) {
                _coAuthorNet.put(listAuthors.get(0), new HashMap<Integer, Integer>());
            } else {
                for (int author1 : listAuthors) {
                    for (int author2 : listAuthors) {
                        if (author1 != author2) {
                            HashMap<Integer, Integer> collaboration;
                            collaboration = _coAuthorNet.get(author1);
                            if (collaboration == null) {
                                collaboration = new HashMap<>();
                            }

                            Integer numofPaper = collaboration.get(author2);
                            if (numofPaper == null) {
                                numofPaper = 0;
                            }
                            numofPaper++;
                            collaboration.put(author2, numofPaper);
                            _coAuthorNet.put(author1, collaboration);
                        }
                    }
                }
            }
        }
    }

    public void BuildingRSSGraph() {
        _rssNet = new HashMap<>();
        for (int authorId1 : _coAuthorNet.keySet()) {
            if (_coAuthorNet.get(authorId1).size() == 0) {
                _rssNet.put(authorId1, new HashMap<Integer, Float>());
            } else {
                int totalPaperOfAuthor1 = 0;
                for (int authorId2 : _coAuthorNet.get(authorId1).keySet()) {
                    totalPaperOfAuthor1 += _coAuthorNet.get(authorId1).get(authorId2);
                }

                for (int authorId2 : _coAuthorNet.get(authorId1).keySet()) {
                    if (authorId1 != authorId2) {
                        float t = 0;
                        float weight = ((float) _coAuthorNet.get(authorId1).get(authorId2)) / ((float) totalPaperOfAuthor1);
                        HashMap<Integer, Float> rssWeight = _rssNet.get(authorId1);
                        if (rssWeight == null) {
                            rssWeight = new HashMap<>();
                        }

                        Float _weight = rssWeight.get(authorId2);
                        if (_weight == null) {
                            _weight = weight;
                            rssWeight.put(authorId2, _weight);
                        }
                        _rssNet.put(authorId1, rssWeight);
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
