/*
    CitationAuthorNet will be built from 2 files: 
        AuthorID_PaperID.txt ("idAuthor", "idPaper"); 
        PaperID_Year_ReferenceID.txt ("idPaper", "Year", "idPaperRef")

    private HashMap<Integer, HashMap<Integer, Integer>> _referenceNumberNet;
    private HashMap<Integer, HashMap<Integer, Float>> _referenceRSSNet;
 */
package uit.tkorg.pr.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Huynh Ngoc Tin
 */
public class CitationAuthorNet {

    /**
     * @param aInstance the _instance to set
     */
    public static void setInstance(CitationAuthorNet aInstance) {
        _instance = aInstance;
    }
     // <AuthorID, <AuthorID_RefTo, NumberOfRef>>
    private HashMap<String, HashMap<String, Integer>> _referenceNumberNet;
    private HashMap<String, HashMap<String, Float>> _referenceRSSNet;
    private HashMap<String, ArrayList<String>> _paperID_RefID_List;
    private HashMap<String, ArrayList<String>> _paperID_CitedID_List;
    private HashMap<String, ArrayList<String>> _paperID_AuthorID_List;
    private HashMap<String, ArrayList<String>> _authorID_PaperID_List;

    private static CitationAuthorNet _instance;
    public static CitationAuthorNet getInstance() {
        if (_instance == null) {
            _instance = new CitationAuthorNet();
        }
        return _instance;
    }

    /**
     * @return the _referenceNumberNet
     */
    public HashMap<String, HashMap<String, Integer>> getReferenceNumberNet() {
        return _referenceNumberNet;
    }

    /**
     * @param referenceNumberNet the _referenceNumberNet to set
     */
    public void setReferenceNumberNet(HashMap<String, HashMap<String, Integer>> referenceNumberNet) {
        this._referenceNumberNet = referenceNumberNet;
    }

    /**
     * @return the _referenceRSSNet
     */
    public HashMap<String, HashMap<String, Float>> getReferenceRSSNet() {
        return _referenceRSSNet;
    }

    /**
     * @param referenceRSSNet the _referenceRSSNet to set
     */
    public void setReferenceRSSNet(HashMap<String, HashMap<String, Float>> referenceRSSNet) {
        this._referenceRSSNet = referenceRSSNet;
    }

    /**
     * @return the _paperID_RefID_List
     */
    public HashMap<String, ArrayList<String>> getPaperID_RefID_List() {
        return _paperID_RefID_List;
    }

    /**
     * @param paperID_RefID_List the _paperID_RefID_List to set
     */
    public void setPaperID_RefID_List(HashMap<String, ArrayList<String>> paperID_RefID_List) {
        this._paperID_RefID_List = paperID_RefID_List;
    }

    /**
     * @return the _paperID_CitedID_List
     */
    public HashMap<String, ArrayList<String>> getPaperID_CitedID_List() {
        return _paperID_CitedID_List;
    }

    /**
     * @param paperID_CitedID_List the _paperID_CitedID_List to set
     */
    public void setPaperID_CitedID_List(HashMap<String, ArrayList<String>> paperID_CitedID_List) {
        this._paperID_CitedID_List = paperID_CitedID_List;
    }

    /**
     * @return the _paperID_AuthorID_List
     */
    public HashMap<String, ArrayList<String>> getPaperID_AuthorID_List() {
        return _paperID_AuthorID_List;
    }

    /**
     * @param paperID_AuthorID_List the _paperID_AuthorID_List to set
     */
    public void setPaperID_AuthorID_List(HashMap<String, ArrayList<String>> paperID_AuthorID_List) {
        this._paperID_AuthorID_List = paperID_AuthorID_List;
    }

    /**
     * @return the _authorID_PaperID_List
     */
    public HashMap<String, ArrayList<String>> getAuthorID_PaperID_List() {
        return _authorID_PaperID_List;
    }

    /**
     * @param authorID_PaperID_List the _authorID_PaperID_List to set
     */
    public void setAuthorID_PaperID_List(HashMap<String, ArrayList<String>> authorID_PaperID_List) {
        this._authorID_PaperID_List = authorID_PaperID_List;
    }
    
    public void load_AuthorID_PaperID(String file_All_AuthorID_PaperID) throws Exception {
        try {
            _authorID_PaperID_List = new HashMap<>();
            _paperID_AuthorID_List = new HashMap<>();
            FileInputStream fis = new FileInputStream(file_All_AuthorID_PaperID);
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
                if (tokens.length >= 2) {
                    authorId = tokens[0].trim();
                    paperId = tokens[1].trim();

                    ArrayList<String> listPaper = getAuthorID_PaperID_List().get(authorId);
                    if (listPaper == null) {
                        listPaper = new ArrayList<>();
                    }
                    listPaper.add(paperId);
                    getAuthorID_PaperID_List().put(authorId, listPaper);

                    ArrayList<String> listAuthor = getPaperID_AuthorID_List().get(paperId);
                    if (listAuthor == null) {
                        listAuthor = new ArrayList<>();
                    }
                    listAuthor.add(authorId);
                    getPaperID_AuthorID_List().put(paperId, listAuthor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load_PaperID_RefID(String file_PaperID_RefID) throws Exception {
        try {
            _paperID_RefID_List = new HashMap<>();
            _paperID_CitedID_List = new HashMap<>();

            FileInputStream fis = new FileInputStream(file_PaperID_RefID);
            Reader reader = new InputStreamReader(fis, "UTF8");
            BufferedReader bufferReader = new BufferedReader(reader);
            String line;
            String[] tokens;
            String paperId;
//            int year;
            String refId;
            while ((line = bufferReader.readLine()) != null) {
                if ((line == null) || (line.equals(""))) {
                    continue;
                }
                tokens = line.split("\\|\\|\\|");
                if (tokens.length >= 2) {
                    paperId = tokens[0].trim();
//                    year = Integer.parseInt(tokens[1].trim());
                    refId = tokens[1].trim();

                    ArrayList<String> refIDList = getPaperID_RefID_List().get(paperId);
                    if (refIDList == null) {
                        refIDList = new ArrayList<>();
                    }
                    refIDList.add(refId);
                    getPaperID_RefID_List().put(paperId, refIDList);

                    ArrayList<String> citedIDList = getPaperID_CitedID_List().get(refId);
                    if (citedIDList == null) {
                        citedIDList = new ArrayList<>();
                    }
                    citedIDList.add(paperId);
                    getPaperID_CitedID_List().put(refId, citedIDList);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void buildRefGraph() throws Exception {
        try {
            _referenceNumberNet = new HashMap<>();
            for (String paperID : getPaperID_RefID_List().keySet()) {
                if (getPaperID_AuthorID_List().containsKey(paperID)) {
                    ArrayList<String> refIDList = getPaperID_RefID_List().get(paperID);
                    ArrayList<String> authorIDList = getPaperID_AuthorID_List().get(paperID);
                    if (refIDList != null && refIDList.size() > 0) {
                        for (String paperIDRef : refIDList) {
                            if (getPaperID_AuthorID_List().containsKey(paperIDRef)) {
                                ArrayList<String> refAuthorIDList = getPaperID_AuthorID_List().get(paperIDRef);
                                for (String authorID : authorIDList) {
                                    HashMap<String, Integer> refHM = getReferenceNumberNet().get(authorID);
                                    if (refHM == null) {
                                        refHM = new HashMap<>();
                                    }

                                    for (String refAuthorID : refAuthorIDList) {
                                        int numberOfRef = 0;
                                        if (refHM.containsKey(refAuthorID)) {
                                            numberOfRef = refHM.get(refAuthorID);
                                        }

                                        numberOfRef++;
                                        refHM.put(refAuthorID, numberOfRef);
                                    }

                                    getReferenceNumberNet().put(authorID, refHM);
                                }
                            }
                        }
                    }
                }
            }
            
            for (String authorID : getAuthorID_PaperID_List().keySet()) {
                if (!_referenceNumberNet.containsKey(authorID)) {
                    getReferenceNumberNet().put(authorID, new HashMap<String, Integer>());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public HashMap<String, HashMap<String, Float>> buildRefRSSGraph() throws Exception {
        _referenceRSSNet = new HashMap<>();
        for (String authorID : getReferenceNumberNet().keySet()) {
            HashMap<String, Integer> refIDHM = getReferenceNumberNet().get(authorID);
            int TotalNumberOfRef = 0;
            for (String refID : refIDHM.keySet()) {
                TotalNumberOfRef += refIDHM.get(refID);
            }

            float refRSSValue = 0f;
            HashMap<String, Float> rssRefIDHM = new HashMap<>();
            for (String refID : refIDHM.keySet()) {
                refRSSValue = (float) refIDHM.get(refID) / (float) TotalNumberOfRef;
                rssRefIDHM.put(refID, refRSSValue);
            }
            getReferenceRSSNet().put(authorID, rssRefIDHM);
        }

        return getReferenceRSSNet();
    }

    /*
    public void calculateImportantRate(String outputFile) {
        try {
            load_AuthorID_PaperID();
            load_PaperID_RefID();
            buildRefGraph();
            HashMap<Integer, HashMap<Integer, Float>> refRSSGraph = buildRefRSSGraph();
            System.out.println("START PAGE RANK... ");
            PageRank pr = new PageRank(refRSSGraph, 2000, 0.85f);
            HashMap<Integer, Float> authorID_PageRank_HM = pr.calculatePR();
            System.out.println("END PAGERANK");

            StringBuffer strBuff = new StringBuffer();
            strBuff.append("AuthorID" + "\t" + "ImportantRate(PageRank)" + "\n");
            for (int authorID : authorID_PageRank_HM.keySet()) {
                strBuff.append(authorID + "\t" + authorID_PageRank_HM.get(authorID) + "\n");
            }

            TextFileUtility.writeTextFile(outputFile, strBuff.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }    
    }*/
    
    public static void main(String args[]) {
//        try {
//            System.out.println("START...");
//            CitationAuthorNet citedGraph = new CitationAuthorNet(
//                    "C:\\2.CRS-Experiment\\Sample Data\\[TrainingData]AuthorID_PaperID.txt",
//                    "C:\\2.CRS-Experiment\\Sample Data\\[TrainingData]PaperID_Year_ReferenceID.txt"
//                    );
//            citedGraph.calculateImportantRate("C:\\2.CRS-Experiment\\pagerank.txt");
//            System.out.println("END...");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }
}
