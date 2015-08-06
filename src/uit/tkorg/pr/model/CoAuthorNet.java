/*
 */

package uit.tkorg.pr.model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import uit.tkorg.utility.general.NumericUtility;

/**
 *
 * @author Huynh Ngoc Tin
 */
public class CoAuthorNet {

    /**
     * @param aInstance the _instance to set
     */
    public static void setInstance(CoAuthorNet aInstance) {
        _instance = aInstance;
    }
    private HashMap<String, HashMap<String, Integer>> _coAuthorNet; // <Author, <Coauthor, count coauthored paper>>
    private HashMap<String, HashMap<String, Float>> _rssNet; //weighted, directed graph: <Author, <Coauthor, RSS>>
    private HashMap<String, HashMap<String, Float>> _rtbvsNet; //weighted, directed graph
    private HashMap<String, Integer> _paperID_Year;
    private HashMap<String, ArrayList<String>> _authorID_PaperID;
    private HashMap<String, ArrayList<String>> _paperID_AuthorID;
    
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
            String line;
            String[] tokens;
            String paperId;
            Integer year;
            while ((line = bufferReader.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    continue;
                }
                tokens = line.split("\\|\\|\\|");
                paperId = tokens[0].trim();
                if (tokens.length <= 1) {
                    year = 0;
                } else {
                    if (NumericUtility.isNum(tokens[3])) {
                        year = Integer.parseInt(tokens[3]);
                    } else {
                        year = 0;
                    }
                }
                getPaperID_Year().put(paperId, year);
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
            String line;
            String[] tokens;
            String authorId;
            String paperId;
            while ((line = bufferReader.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    continue;
                }
                tokens = line.split("\\|\\|\\|");
                authorId = tokens[0].trim();
                paperId = tokens[1].trim();

                ArrayList<String> listPaper = getAuthorID_PaperID().get(authorId);
                if (listPaper == null) {
                    listPaper = new ArrayList<>();
                }
                listPaper.add(paperId);
                getAuthorID_PaperID().put(authorId, listPaper);

                ArrayList<String> listAuthor = getPaperID_AuthorID().get(paperId);
                if (listAuthor == null) {
                    listAuthor = new ArrayList<>();
                }
                listAuthor.add(authorId);
                getPaperID_AuthorID().put(paperId, listAuthor);
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
        for (String paperId : getPaperID_AuthorID().keySet()) {
            ArrayList<String> listAuthors = getPaperID_AuthorID().get(paperId);
            if (listAuthors.size() == 1 && !_coAuthorNet.containsKey(listAuthors.get(0))) {
                getCoAuthorNet().put(listAuthors.get(0), new HashMap<String, Integer>());
            } else {
                for (String author1 : listAuthors) {
                    for (String author2 : listAuthors) {
                        if (!author1.equalsIgnoreCase(author2)) {
                            HashMap<String, Integer> collaboration;
                            collaboration = getCoAuthorNet().get(author1);
                            if (collaboration == null) {
                                collaboration = new HashMap<>();
                            }

                            Integer numofPaper = collaboration.get(author2);
                            if (numofPaper == null) {
                                numofPaper = 0;
                            }
                            numofPaper++;
                            collaboration.put(author2, numofPaper);
                            getCoAuthorNet().put(author1, collaboration);
                        }
                    }
                }
            }
        }
    }

    public void BuildingRSSGraph() {
        _rssNet = new HashMap<>();
        for (String authorId1 : getCoAuthorNet().keySet()) {
            if (getCoAuthorNet().get(authorId1).size() == 0) {
                getRssNet().put(authorId1, new HashMap<String, Float>());
            } else {
                int totalPaperOfAuthor1 = 0;
                for (String authorId2 : getCoAuthorNet().get(authorId1).keySet()) {
                    totalPaperOfAuthor1 += getCoAuthorNet().get(authorId1).get(authorId2);
                }

                for (String authorId2 : getCoAuthorNet().get(authorId1).keySet()) {
                    if (!authorId1.equalsIgnoreCase(authorId2)) {
                        float t = 0;
                        float weight = ((float) getCoAuthorNet().get(authorId1).get(authorId2)) / ((float) totalPaperOfAuthor1);
                        HashMap<String, Float> rssWeight = getRssNet().get(authorId1);
                        if (rssWeight == null) {
                            rssWeight = new HashMap<>();
                        }

                        Float _weight = rssWeight.get(authorId2);
                        if (_weight == null) {
                            _weight = weight;
                            rssWeight.put(authorId2, _weight);
                        }
                        getRssNet().put(authorId1, rssWeight);
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

    // Testing Functions of AuthorGraph
    public static void main(String args[]) {
        System.out.println("START LOADING TRAINING DATA");
        CoAuthorNet _graph = CoAuthorNet.getInstance();
        
        _graph.LoadTrainingData("E:\\! Research\\Research Topics\\3. Recommendation Systems\\PRS\\Experiment\\1. Data\\Sample Data\\CSV\\Sample 3\\AUTHOR_PAPER_BEFORE_T2.csv", 
                "E:\\! Research\\Research Topics\\3. Recommendation Systems\\PRS\\Experiment\\1. Data\\Sample Data\\CSV\\Sample 3\\PAPER_BEFORE_T2.csv");

        // Building Graphs
        _graph.BuildCoAuthorGraph();
        _graph.BuildingRSSGraph();
        
        HashMap temp1 = _graph.getCoAuthorNet();
        HashMap temp2 = _graph.getRssNet();
        
        System.out.println("DONE");
    }

    /**
     * @return the _coAuthorNet
     */
    public HashMap<String, HashMap<String, Integer>> getCoAuthorNet() {
        return _coAuthorNet;
    }

    /**
     * @param coAuthorNet the _coAuthorNet to set
     */
    public void setCoAuthorNet(HashMap<String, HashMap<String, Integer>> coAuthorNet) {
        this._coAuthorNet = coAuthorNet;
    }

    /**
     * @return the _rssNet
     */
    public HashMap<String, HashMap<String, Float>> getRssNet() {
        return _rssNet;
    }

    /**
     * @param rssNet the _rssNet to set
     */
    public void setRssNet(HashMap<String, HashMap<String, Float>> rssNet) {
        this._rssNet = rssNet;
    }

    /**
     * @return the _rtbvsNet
     */
    public HashMap<String, HashMap<String, Float>> getRtbvsNet() {
        return _rtbvsNet;
    }

    /**
     * @param rtbvsNet the _rtbvsNet to set
     */
    public void setRtbvsNet(HashMap<String, HashMap<String, Float>> rtbvsNet) {
        this._rtbvsNet = rtbvsNet;
    }

    /**
     * @return the _paperID_Year
     */
    public HashMap<String, Integer> getPaperID_Year() {
        return _paperID_Year;
    }

    /**
     * @param paperID_Year the _paperID_Year to set
     */
    public void setPaperID_Year(HashMap<String, Integer> paperID_Year) {
        this._paperID_Year = paperID_Year;
    }

    /**
     * @return the _authorID_PaperID
     */
    public HashMap<String, ArrayList<String>> getAuthorID_PaperID() {
        return _authorID_PaperID;
    }

    /**
     * @param authorID_PaperID the _authorID_PaperID to set
     */
    public void setAuthorID_PaperID(HashMap<String, ArrayList<String>> authorID_PaperID) {
        this._authorID_PaperID = authorID_PaperID;
    }

    /**
     * @return the _paperID_AuthorID
     */
    public HashMap<String, ArrayList<String>> getPaperID_AuthorID() {
        return _paperID_AuthorID;
    }

    /**
     * @param paperID_AuthorID the _paperID_AuthorID to set
     */
    public void setPaperID_AuthorID(HashMap<String, ArrayList<String>> paperID_AuthorID) {
        this._paperID_AuthorID = paperID_AuthorID;
    }
}
