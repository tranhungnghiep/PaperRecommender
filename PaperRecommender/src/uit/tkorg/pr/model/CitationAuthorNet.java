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
import uit.tkorg.utility.algorithm.PageRank;
import uit.tkorg.utility.general.TextFileUtility;

/**
 *
 * @author Huynh Ngoc Tin
 */
public class CitationAuthorNet {
     // <AuthorID, <AuthorID_RefTo, NumberOfRef>>
    private HashMap<Integer, HashMap<Integer, Integer>> _referenceNumberNet;
    private HashMap<Integer, HashMap<Integer, Float>> _referenceRSSNet;
    private HashMap<Integer, ArrayList<Integer>> _paperID_RefID_List;
    private HashMap<Integer, ArrayList<Integer>> _paperID_CitedID_List;
    private HashMap<Integer, ArrayList<Integer>> _paperID_AuthorID_List;
    private HashMap<Integer, ArrayList<Integer>> _authorID_PaperID_List;
    private String _file_All_AuthorID_PaperID;
    private String _file_PaperID_RefID;

    public CitationAuthorNet(String file_All_AuthorID_PaperID, String file_PaperID_RefID){
        _file_All_AuthorID_PaperID = file_All_AuthorID_PaperID;
        _file_PaperID_RefID = file_PaperID_RefID;
    }

    private void load_AuthorID_PaperID() throws Exception {
        try {
            _authorID_PaperID_List = new HashMap<>();
            _paperID_AuthorID_List = new HashMap<>();
            FileInputStream fis = new FileInputStream(_file_All_AuthorID_PaperID);
            Reader reader = new InputStreamReader(fis, "UTF8");
            BufferedReader bufferReader = new BufferedReader(reader);
            bufferReader.readLine();
            String line = null;
            String[] tokens;
            int authorId;
            int paperId;
            while ((line = bufferReader.readLine()) != null) {
                tokens = line.split(",");
                if (tokens.length == 2) {
                    authorId = Integer.parseInt(tokens[0].trim());
                    paperId = Integer.parseInt(tokens[1].trim());

                    ArrayList<Integer> listPaper = _authorID_PaperID_List.get(authorId);
                    if (listPaper == null) {
                        listPaper = new ArrayList<>();
                    }
                    listPaper.add(paperId);
                    _authorID_PaperID_List.put(authorId, listPaper);

                    ArrayList<Integer> listAuthor = _paperID_AuthorID_List.get(paperId);
                    if (listAuthor == null) {
                        listAuthor = new ArrayList<>();
                    }
                    listAuthor.add(authorId);
                    _paperID_AuthorID_List.put(paperId, listAuthor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load_PaperID_RefID() throws Exception {
        try {
            _paperID_RefID_List = new HashMap<>();
            _paperID_CitedID_List = new HashMap<>();

            FileInputStream fis = new FileInputStream(_file_PaperID_RefID);
            Reader reader = new InputStreamReader(fis, "UTF8");
            BufferedReader bufferReader = new BufferedReader(reader);
            bufferReader.readLine();
            String line = null;
            String[] tokens;
            int paperId;
            int year;
            int refId;
            while ((line = bufferReader.readLine()) != null) {
                tokens = line.split(",");
                if (tokens.length == 3) {
                    paperId = Integer.parseInt(tokens[0].trim());
                    year = Integer.parseInt(tokens[1].trim());
                    refId = Integer.parseInt(tokens[2].trim());

                    ArrayList<Integer> refIDList = _paperID_RefID_List.get(paperId);
                    if (refIDList == null) {
                        refIDList = new ArrayList<>();
                    }
                    refIDList.add(refId);
                    _paperID_RefID_List.put(paperId, refIDList);

                    ArrayList<Integer> citedIDList = _paperID_CitedID_List.get(refId);
                    if (citedIDList == null) {
                        citedIDList = new ArrayList<>();
                    }
                    citedIDList.add(paperId);
                    _paperID_CitedID_List.put(refId, citedIDList);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buildRefGraph() throws Exception {
        try {
            _referenceNumberNet = new HashMap<>();
            for (int paperID : _paperID_RefID_List.keySet()) {
                if (_paperID_AuthorID_List.containsKey(paperID)) {
                    ArrayList<Integer> refIDList = _paperID_RefID_List.get(paperID);
                    ArrayList<Integer> authorIDList = _paperID_AuthorID_List.get(paperID);
                    if (refIDList != null && refIDList.size() > 0) {
                        for (int paperIDRef : refIDList) {
                            if (_paperID_AuthorID_List.containsKey(paperIDRef)) {
                                ArrayList<Integer> refAuthorIDList = _paperID_AuthorID_List.get(paperIDRef);
                                for (int authorID : authorIDList) {
                                    HashMap<Integer, Integer> refHM = _referenceNumberNet.get(authorID);
                                    if (refHM == null) {
                                        refHM = new HashMap<>();
                                    }

                                    for (int refAuthorID : refAuthorIDList) {
                                        int numberOfRef = 0;
                                        if (refHM.containsKey(refAuthorID)) {
                                            numberOfRef = refHM.get(refAuthorID);
                                        }

                                        numberOfRef++;
                                        refHM.put(refAuthorID, numberOfRef);
                                    }

                                    _referenceNumberNet.put(authorID, refHM);
                                }
                            }
                        }
                    }
                }
            }
            
            for (int authorID : _authorID_PaperID_List.keySet()) {
                if (!_referenceNumberNet.containsKey(authorID)) {
                    _referenceNumberNet.put(authorID, new HashMap<Integer, Integer>());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private HashMap<Integer, HashMap<Integer, Float>> buildRefRSSGraph() throws Exception {
        _referenceRSSNet = new HashMap<>();
        for (int authorID : _referenceNumberNet.keySet()) {
            HashMap<Integer, Integer> refIDHM = _referenceNumberNet.get(authorID);
            int TotalNumberOfRef = 0;
            for (int refID : refIDHM.keySet()) {
                TotalNumberOfRef += refIDHM.get(refID);
            }

            float refRSSValue = 0f;
            HashMap<Integer, Float> rssRefIDHM = new HashMap<>();
            for (int refID : refIDHM.keySet()) {
                refRSSValue = (float) refIDHM.get(refID) / (float) TotalNumberOfRef;
                rssRefIDHM.put(refID, refRSSValue);
            }
            _referenceRSSNet.put(authorID, rssRefIDHM);
        }

        return _referenceRSSNet;
    }

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
    }
    
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
