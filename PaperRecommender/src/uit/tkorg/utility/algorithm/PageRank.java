package uit.tkorg.utility.algorithm;

import java.util.HashMap;

/**
 *
 * @author TinHuynh
 */
public class PageRank {
    // _PageRankResult<NodeID, RankOfNode>
    HashMap<Integer, Float> _PageRankResult = new HashMap<>();
    // Direct AuthorGraph <BeginID, <EndID, Weight>>
    HashMap<Integer, HashMap<Integer, Float>> _graph = null;
    // Direct AuthorGraph <CurrentID, <FromID, Weight>>
    HashMap<Integer, HashMap<Integer, Float>> _inLinkHM = new HashMap<>();
    // Direct AuthorGraph <CurrentID, <OutID, Weight>>
    HashMap<Integer, HashMap<Integer, Float>> _outLinkHM = new HashMap<>(); 
    int N; // Number of Page/Node
    public float d; // damping factor
    public int iterationNumber;

    public PageRank(HashMap<Integer, HashMap<Integer, Float>> graph, int iterationNum, float df) {
        _graph = graph;
        N = _graph.size();
        d = df;
        iterationNumber = iterationNum;
    }

    public HashMap<Integer, Float> calculatePR() {
        HashMap<Integer, Float> npg = new HashMap<>();
        initInLinkHMFromGraph(_graph);
        initOutLinkHMFromGraph(_graph);
        
        float initValuePR = (float) 1 / (float) N;
        for (int id : _graph.keySet()) {
            _PageRankResult.put(id, initValuePR);
        }

        float valPR = 0f;
        while (iterationNumber > 0) {
            float dp = 0;
            
            for (int id : _graph.keySet()) {
                // if id has no out link                
                if ((_graph.get(id) == null) || (_graph.get(id).size() == 0) ) {
                    dp = dp + d*(_PageRankResult.get(id))/N;
                }
            }
            
            for (int id : _graph.keySet()) {
                // get PageRank from random jump
                valPR = dp + (float)(1 - d)/(float)N;
                if (_inLinkHM.get(id) != null) {
                    for (int inLinkID : _inLinkHM.get(id).keySet()) {
                        // get PageRank from inlinks
                        valPR = valPR + ((float)d*_PageRankResult.get(inLinkID))/_outLinkHM.get(inLinkID).size();
                    }
                }
                npg.put(id, valPR);
            }

            // update PageRank
            for (int id : _graph.keySet()) {
                _PageRankResult.put(id, npg.get(id));
            }
            
            iterationNumber = iterationNumber - 1;
            System.out.println("PageRank IterationNumber:" + iterationNumber);
        }

        return _PageRankResult;
    }
    
    /**
     * initInLinkHMFromGraph
     * @param graph: is exactly rssGraph
     */
    public HashMap<Integer, HashMap<Integer, Float>> initInLinkHMFromGraph(HashMap<Integer, HashMap<Integer, Float>> graph) {
        HashMap<Integer, Float> inLinkEachNode = null;
        for (int id1 : graph.keySet()) {
            for (int id2 : graph.get(id1).keySet()) {
                // Inlink to id2 from id1
                inLinkEachNode = _inLinkHM.get(id2);
                if (inLinkEachNode == null) {
                    inLinkEachNode = new HashMap<>();
                }
                inLinkEachNode.put(id1, graph.get(id1).get(id2));

                _inLinkHM.put(id2, inLinkEachNode);
            }
        }

        return _inLinkHM;
    }

    public void initOutLinkHMFromGraph(HashMap<Integer, HashMap<Integer, Float>> graph) {
        _outLinkHM = graph;
    }

    // Testing Functions of AuthorGraph
    public static void main(String args[]) {
        System.out.println("START LOADING TRAINING DATA");
//        HashMap<Integer, HashMap<Integer, Float>> graph = new HashMap<>();
//        
//        HashMap<Integer, Float> link_Node1 = new HashMap<>();
//        link_Node1.put(2, 1f);
//        link_Node1.put(3, 1f);
//        link_Node1.put(6, 1f);
//        graph.put(1, link_Node1);
//        
//        HashMap<Integer, Float> link_Node2 = new HashMap<>();
//        link_Node2.put(3, 1f);
//        link_Node2.put(4, 1f);
//        link_Node2.put(5, 1f);
//        link_Node2.put(6, 1f);
//        graph.put(2, link_Node2);
//        
//        HashMap<Integer, Float> link_Node3 = new HashMap<>();
//        link_Node3.put(4, 1f);
//        link_Node3.put(5, 1f);
//        graph.put(3, link_Node3);
//        
//        HashMap<Integer, Float> link_Node4 = new HashMap<>();
//        link_Node4.put(1, 1f);
//        link_Node4.put(3, 1f);
//        link_Node4.put(5, 1f);
//        link_Node4.put(6, 1f);
//        graph.put(4, link_Node4);
//        
//        HashMap<Integer, Float> link_Node5 = new HashMap<>();
//        link_Node5.put(1, 1f);
//        graph.put(5, link_Node5);
//        
//        HashMap<Integer, Float> link_Node6 = new HashMap<>();
//        link_Node6.put(1, 1f);
//        link_Node6.put(2, 1f);
//        link_Node6.put(5, 1f);
//        graph.put(6, link_Node6);
        
//        System.out.println("START LOADING TRAINING DATA");
//        AuthorGraph _graph = AuthorGraph.getInstance();
//        _graph.LoadTrainingData("C:\\CRS-Experiment\\Sampledata\\Input\\Link-Net\\[Training]AuthorId_PaperID.txt", 
//                "C:\\CRS-Experiment\\Sampledata\\Input\\Link-Net\\[Training]PaperID_Year.txt");
//
//        // Building Graphs
//        _graph.BuildCoAuthorGraph();
//        _graph.BuildingRSSGraph();
//        
//        HashMap temp1 = _graph.coAuthorGraph;
//        HashMap temp2 = _graph.rssGraph;
//        
//        PageRank pr = new PageRank(_graph.rssGraph, 1000, 0.85f);
//        HashMap<Integer, Float> resultPR = pr.calculatePR();
//
//        System.out.println("PAGE RANK RESULT ...");
//        for (int id : resultPR.keySet()) {
//            System.out.println("ID:" + id + "\tRank:" + resultPR.get(id));
//        }
//        System.out.println("DONE");
    }
}