import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                               G   S P A N                                  */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                           Friday 8 February 2008                           */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/** Implentation of graph mining algorithm using Frequent Sub Tree (FST)
extension to grow candidate graphs:
@author Frans Coenen
@version 9 February 2008          */

//  java GspanApp -FimageBase.gml

public class GraphMiner extends GraphMLreader {

    /*------------------------------------------------------------------------*/
    /*                                                                        */
    /*                                   FIELDS                               */
    /*                                                                        */
    /*------------------------------------------------------------------------*/

    // Input data
    /** Input data recast as DFS codes. */
    protected GraphDFScode[] inputDFSdata = null;

    // Vertex and Edge Binary Trees
    /** Object representing binary tree containing node (vertex) data. Used to
    store label, support and index into node array data. */
    protected VertexTree newVertexTree = null;
    /** Object representing binary tree containing edge data. Used to store
    label, support and index into node array data. */
    protected EdgeTree newEdgeTree = null;

    // Arrays
    /** Array to hold all DFScodes whos startor end vertex, or edge, is
    supported. This is the list of DFS codes referenced by the system. */
    private DFScodeLabel[][][] dfsCodeLabelList = null;

    // Frequent sub-graph structures
    /** Start of tree structure in which to store frequent and candidate sub-
    graps. */
    private FreqSubGraphNode fsgTreeStart = null;

    // Other fields
    /** Support threshold interprted as a percentage of the total
    number of edges/vertoces, value vetweem 0 and 1, default set at 20%. */
//    private double supportThold = 0.3;
    private double supportThold = 0.1;
    /** Support threshold in terms of minimum number of graphs in which
    a sub-graph has to appear for it to be considered frquent (1 by
    default). */
    private int minSupport = 1;
//    private int minSupport = 2;
    /** Number of nodes at level N. */
    protected int nodesAtlevelN = 0;    
    /** Number of pre-pruned nodes at level N (nodes prined because they are 
    duplicates). */
    protected int prePrunedNodesAtlevelN = 0;
    /** Number of pruned nodes at level N. */
    private int prunedNodesAtlevelN = 0;
    /** Number of duplicate branches. */
    protected int duplicateNodes = 0;
    
    /** List of supported graph's id **/
    private ArrayList<Integer> supportList = null;

    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/

    /** Constructor with command line arguments to be process.
    @param args the command line arguments (array of String instances). */

    public GraphMiner(String[] args) {
	// Process command line arguments
        super(args);
	}

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                           T E S T I N G                          */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /** Tests codeSetInGraph method. */

    /* [0,1,red,x,green],[0,2,red,y,blue],
	[2,3,blue,x,red],[2,4,blue,y,green]  red=0, green=1, blue=2
        x =0, y=1.
        */

    public void testing1() {
        DFScode[] newCode = new DFScode[5];
        newCode[0] = new DFScode(0,10,0,0,1);
        newCode[1] = new DFScode(10,12,1,1,2);
        newCode[2] = new DFScode(0,23,0,0,1);
        newCode[3] = new DFScode(23,26,1,1,1);
        newCode[4] = new DFScode(0,30,0,0,2);


        // Loop
        int support=0;
        for (int index=0;index<inputDFSdata.length;index++) {
            System.out.println("=====================\nCheck graph " + index);
            if (inputDFSdata[index].codeSetInGraph(newCode)) {
                System.out.println("Increment SUPPORT");
                support++;
                }
            }

        // End
        System.out.println("support = " + support);
        }

    public void testing2() {
        System.out.println("TESTING 2");
        DFScode[] newCode = new DFScode[5];
        newCode[0] = new DFScode(0,1,0,0,2);
        newCode[1] = new DFScode(0,2,0,0,1);
        newCode[2] = new DFScode(2,3,1,1,1);
        newCode[3] = new DFScode(0,4,0,0,1);
        newCode[4] = new DFScode(4,5,1,1,2);

        if (findInFSGtree(newCode)) System.out.println("FOUND!");
        else System.out.println("NOT FOUND");
        }

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                           S T A R T                              */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /** Starts the Gspan process. To ouput the FSD tree at any time duting 
    processing inclide: outputInputDFSdataLabels().        */

    public void startGraphMiner() {
        // Read given GraphML input file (method in parent class). To output 
        // the input data use: outputGraphData();
        startGraphMLreader();

        // Calculate minimum support, must be at least one record.
        double minSup = (double) graphData.length * supportThold;
        System.out.println("minSupport = " + (supportThold*100) +
                "% (" + minSup + " records)\n");
        minSupport = (int) minSup;
        if (minSupport==0) {
            minSupport = 1;
            System.out.println("Minimunm support set to " + minSupport +
                                                      " lines (by default)");
            }
        else System.out.println("Minimunm support set to " + minSupport +
                                                                   " records");

        // Determine supported vertices and edges un input data. Do this by
        // generate vertex and edge binary trees in  which to store support.
        genVertexAndEdgeArrays();

        // Generate DFS code list (uses grequent node and edge arrays generated
        // previously). This is the  definitive list of DFS codes accortding to
        // the identified frequent vertices and edges.
        generateDFScodeLabelList();

        // Convert input data into DFS codes as DFS code label list created
        // above.
        convertInput2dfsCodes();

        // Generate frequent 1-edge sub-graphs (SGs) tree comprising
        // "FreqSubGraphNode" objects.
        genFreq1edgeSGtree();
        System.out.println("----------------------------\n" +
                                                "Generate FSG tree level 1");
        System.out.println("\tNumber of supported nodes = " + nodesAtlevelN);

        // Prune input data removing unsupported one edge graphs
        pruneInputData();

        // Generate level 2 if there are level 1 one supported edge sub-graphs
        if (fsgTreeStart!=null) genSuppPruneLoop();

        // End
        System.out.println("END");
        }

    /** Generates vertex and edge ordered arrays (ordered according to
    frequency). Starts by creating vetex and edge binary trees dtructures
    (for fast look-up) in which to store and look up vertex and edge data.
    Then recasts this int a ordered arraus of vertoces and edges. */

    private void genVertexAndEdgeArrays() {
        // Create vertex and edge trees and the determine and store vertex and
        // edge support.
        newVertexTree = new VertexTree();
        newVertexTree.createVertexTree(graphData);
//newVertexTree.outputNodeTree();
        newEdgeTree   = new EdgeTree();
        newEdgeTree.createEdgeTree(graphData);
//newVertexTree.outputNodeTree();
//newEdgeTree.outputEdgeTree();
//System.out.println("---------------------");
        // Create ordered arrays of verices and edges according to support
        newVertexTree.genOrderedVertexArray(minSupport);
        newEdgeTree.genOrderedEdgeArray(minSupport);

        // Add index information to vertex and edge trees. These index numbers
        // are the numbers by which individual vertices and indexes will be
        // knowm
        newVertexTree.addIndexNumsToVertexTree();
        newEdgeTree.addIndexNumsToEdgeTree();
//newVertexTree.outputNodeArray();
//newEdgeTree.outputEdgeArray();
        }

    /** The generate, support, prune loop: (i) generate candidate sets, (ii)
    calculate support, (iii) prune unsupported sets, (iv) repeat. */

    private void genSuppPruneLoop() {
        int level=1;

        // Generate next level (level+1);
        System.out.println("----------------------------\n" +
        "Generate FSG tree level " + (level+1));
        genNextLevel(level);
        System.out.println("\tnumber of duplicate branches IDed = " +
             duplicateNodes + "\n\tnumber of nodes generated         = " +
                                                              nodesAtlevelN);

        //The loop
        while (nodesAtlevelN>0) {
            //Increementlevel counter
            level++;
            // Calculate support
            System.out.println("Calculate support for FSG tree level " +
                                                                       level);
            calculateSupport(level);
            // Prune
            System.out.println("Prune FSG tree level " + level);
            pruneFSGtree(level);
            System.out.println("\tnumber of nodes pruned = " +
                                                         prunedNodesAtlevelN);
            // Test for end
            if (nodesAtlevelN==prunedNodesAtlevelN) break;
            // Generate next level (level+1);
            System.out.println("----------------------------\n" +
                                      "Generate FSG tree level " + (level+1));
            genNextLevel(level);
            System.out.println("\tnumber of duplicate nodes IDed   = " +
                                                              duplicateNodes +
                               "\n\tnumber of nodes generated      = " +
                                                               nodesAtlevelN +
                               "\n\tnumber duplicate nodes deleted = " +
                                                      prePrunedNodesAtlevelN);
            }
        }

    /* --------------------------------------------------------- */
    /*                                                           */
    /*               GENERATE FREQUENT 1 EDGE GRAPHS             */
    /*                                                           */
    /* --------------------------------------------------------- */

    /** Generates top level (1 edge) if frequent sub graph tree comprising a
    sequence of sibling nodes each represented by a "FreqSubGraphNode"
    object.    */

    private void genFreq1edgeSGtree() {
        nodesAtlevelN = 0;

        // Loop through node and edge arrays (backwards so we have nodes in
        // the same order).
        for (int nodeIndex1=dfsCodeLabelList.length-1;nodeIndex1>=0;
                                                               nodeIndex1--) {
            for (int edgeIndex=dfsCodeLabelList[nodeIndex1].length-1;
                                                   edgeIndex>=0;edgeIndex--) {
                for (int nodeIndex2=
                             dfsCodeLabelList[nodeIndex1][edgeIndex].length-1;
                                                 nodeIndex2>=0;nodeIndex2--) {
                    // Create DFS code (first create DFS code label).
                    DFScodeLabel code =
                          dfsCodeLabelList[nodeIndex1][edgeIndex][nodeIndex2];
                    DFScode newCode = new DFScode(code);
                    // Get (calculate) and compare support for sub-graph
                    int support = getSupportFor1edgeGraphs(code);
                    if (support>=minSupport) addNodeToTopFSGtree(newCode,
                                                                     support);
                    }
                }
            }
        }

    /** Creats new FSG tree to top level node abd adds it to the tree.
    @param newCode the DFS code for the new 1 edge frequent sub graph.
    @support the associated support value. */

    private void addNodeToTopFSGtree(DFScode newCode, int support) {
        // Creat node and add to frequent sub-graph tree
        if (fsgTreeStart==null){
//        	fsgTreeStart = new FreqSubGraphNode(newCode,support);
        	fsgTreeStart = new FreqSubGraphNode(newCode,supportList);
        	
        } else {
//            FreqSubGraphNode temp = new FreqSubGraphNode(newCode,support);
            FreqSubGraphNode temp = new FreqSubGraphNode(newCode,supportList);
            temp.siblingLink = fsgTreeStart;
            fsgTreeStart     = temp;
            }

        // Increment counter
        nodesAtlevelN++;
        }

    /** Get the support count for the DFS code label representing a candidate
    frequent one edge sub-graph. Does this by searching through the input
    graph set looking for matches.
    @param code the DFS code for the given graph.
    @return the assocoated support.  */

    private int getSupportFor1edgeGraphs(DFScodeLabel code) {
        int count = 0;
        supportList = new ArrayList<Integer>();

        // Loop
        for (int index=0;index<inputDFSdata.length;index++) {
            if (inputDFSdata[index].codeInGraph(code)>=0) {
            	count++;
            	supportList.add(index);
            	}
            }

        // End
        return(count);
        }

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*         GENERATE LEVEL N IN FREQUENT SUB GRAPH (FSG) TREE        */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /** Commences process of generating new level of candidate nodes in the
    frequent sub-graph tree for the given level.
    @param level the goven level. */

    private void genNextLevel(int level) {
        // New node ID is the level identifier plus 1
        int newNodeID = level+1;
        // Initilise, node at level N, duplicate branches and X-check nodes
        // counters.
        nodesAtlevelN          = 0;
        duplicateNodes         = 0;
        prePrunedNodesAtlevelN = 0;
        // Graph so far
        DFScode[] graphSoFar = null;
        // Process
        genNextLevel(level,fsgTreeStart,newNodeID,graphSoFar);
        }

    /** Continues process of generating new level of candidate nodes in the
    frequent sub-graph tree for the given level by first finding the desired
    level in each of the branches.
    @param level the given level.
    @param link the current location in the frequent sub-graph tree.
    @param graphSoFar list of DFS codes representing the graph so far.
    @param newNodeID the ID number for the new node. */

    private void genNextLevel(int level, FreqSubGraphNode link, int newNodeID,
                                                       DFScode[] graphSoFar) {
        // Right level
        if (level==1) {
            genNextLevel(link,newNodeID,graphSoFar);
            }

        // Wrong level
        else {
            // Process level (siblings) and move down tree
            level = level-1;
            FreqSubGraphNode sibLink = link;
            while (sibLink!=null) {
                if (sibLink.childLink!=null) {
                    // Update graph so far list
                    DFScode[] newGraphSoFar = appendToList(graphSoFar,
                                                             sibLink.dfsCode);
//System.out.print("WRONG LEVEL newGraphSoFar = ");
//outputDFSCodeListLabels(newGraphSoFar);
//System.out.println();
                    // Process
                    genNextLevel(level,sibLink.childLink,newNodeID,newGraphSoFar);
                    }
                sibLink = sibLink.siblingLink;
                }
            }
        }

   /** Extends current node in the t-tree with supported one edge frequent
   sub-graphs identified earlier.
   @param link the current position in the frequent sub-graph tree.
   @param newNodeID the ID number for the new node.
   @param graphSoFar list of DFS codes representing the graph so far.   */

   private void genNextLevel(FreqSubGraphNode link, int newNodeID,
                                                       DFScode[] graphSoFar) {
        // Process sibling nodes in the subgraph tree to be extended.
        while (link!=null) {
            // Update graph so far list
            DFScode[] newGraphSoFar = appendToList(graphSoFar,link.dfsCode);
//System.out.print("* newGraphSoFar = ");
//outputDFSCodeListLabels(newGraphSoFar);
//System.out.println();
            // Process extension list
            for (int index=0;index<link.extensionList.length;index++) {
                 // Link to chain of one edge frequent sub-graphs identified
                 // previously.
                 FreqSubGraphNode oneEdgeFSG = fsgTreeStart;
                 // Continue by stepping through one edge graph lnked list
                 genNextLevel(oneEdgeFSG,link,index,newNodeID,newGraphSoFar);
                 }
            // Destroy previous level extension lists (free up some space!).
            link.extensionList     = null;
            link.extensionCodeList = null;
            // Next
            link = link.siblingLink;
//outputFSGtreeLabels();
            }
        }

    /** Recursively (so that list is processed in reverse) steps through
    linked list of supported one edge graphs adding a node to the FDG tree
    according to the associated extension code.
    @para, oneEdgeFSG the current location in thelinked list of supported one
    edge graphs
    @param node the current position in the frequent sub-graph tree.
    @param the index into the given nodes extension lists.
    @param newNodeID the ID number for the new node.
    @param graphSoFar list of DFS codes representing the graph so far.    */

    private void genNextLevel(FreqSubGraphNode oneEdgeFSG,
                              FreqSubGraphNode link, int index, int newNodeID,
                                                       DFScode[] graphSoFar) {
        // Base case
        if (oneEdgeFSG==null) return;
        // Recursion
        else {
            genNextLevel(oneEdgeFSG.siblingLink,link,index,newNodeID,
                                                                  graphSoFar);
            // Statements pending to be called when recusion un-winds
            DFScodeLabel label = oneEdgeFSG.dfsCode.getDFScodeLabel();
            genNextLevelSwitch(link,label,index,newNodeID,graphSoFar);
            }
        }

    /** Continues process of Adding a node to the FDG tree according to the
    associated extension code.
    @param node the current position in the frequent sub-graph tree.
    @param label the DFScode label for the edge to be added.
    @param the index into the given nodes extension lists.
    @param newNodeID the ID number for the new node.
    @param graphSoFar list of DFS codes representing the graph so far.   */

    protected void genNextLevelSwitch(FreqSubGraphNode node, DFScodeLabel label,
                             int index, int newNodeID, DFScode[] graphSoFar) {
//FreqSubGraphNode oneEdgeFSG = null;

        // Get extension code.
        int extCode    = node.extensionCodeList[index];

        // Switch
        switch (extCode) {
            // Chid and sibling extension
            case 1:
                extendNodeCS(node,label,index,newNodeID,graphSoFar);
                break;
            // sibling leading to sibling, new extension code 2 = S1.
            case 2:
                DFScodeLabel extLabel =
                                 node.extensionList[index].getDFScodeLabel();
                int paritor = label.comparesWith(extLabel);
                if (paritor>=0) extendNodeSiblingS1(node,label,index,
                                                                  newNodeID);
                break;
            // Sibling leading to childsibling, new extension code 3 = S2.
            case 3:
                extendNodeSiblingS2(node,label,index,newNodeID);
                break;
            // Should not get here
            default: System.out.println("ERROR! extCode = " + extCode);
            }
        }

    /** Extends a given node to produce two new nodes, a child and sibling.
    Child node can produce new child and sibling nodes.
    @param node the current node in the frequent sub-graph tree from which the
    exrtensions will hang.
    @param label the DFScopde label for the edge to be added.
    @param the index into the given nodes extension lists.
    @param newNodeID the ID number for the new node.
    @param graphSoFar list of DFS codes representing the graph so far.    */

    private void extendNodeCS(FreqSubGraphNode node, DFScodeLabel label,
                             int index, int newNodeID, DFScode[] graphSoFar) {
        // Start node label for child node must be the same as the parent
        // node end label
        int oldEnd   = node.extensionList[index].getEndNodeLabel();
        int newStart = label.getStartNodeLabel();
        // If same add child node
        if (newStart==oldEnd) extendNodeCSchild(node,label,index,newNodeID,
                                                                  graphSoFar);

        // Add sibling leading to sibling node. New extension code 2 = S1.
        // Potential add-ons are all supported one edge graphs but must not be
        // lexicogrphically before the curret node.
        DFScodeLabel extLabel = node.extensionList[index].getDFScodeLabel();
        int paritor = label.comparesWith(extLabel);
        if (paritor>=0) extendNodeSiblingS1(node,label,index,newNodeID);
        }

    /** Extends a given node to produce a sibling node (which will spawn
    another sibling node etc. but no child nodes).
    @param node the current position in the frequent sub-graph tree.
    @param newLabel the DFScopde label for the edge to be added.
    @param the index into the given nodes extension lists.
    @param newNodeID the ID number for the new node.            */

    private void extendNodeSiblingS1(FreqSubGraphNode node,
                            DFScodeLabel newLabel, int index, int newNodeID) {
        // Find label reference for cuurent node.
        DFScodeLabel oldLabel = node.extensionList[index].getDFScodeLabel();

        // Start node label must be the same as the parent node start label
        int oldStart = oldLabel.getStartNodeLabel();
        int newStart = newLabel.getStartNodeLabel();
        if (newStart==oldStart) {
            // Add sibling.
            int extCode = 2;
            extendNodeSibling(node,newLabel,index,newNodeID,extCode);
            // Incremnent counter
            nodesAtlevelN++;
            }
        }

    /** Extends a given node to produce a sibling node (which will spawn
    sibling node and child nodes).
    @param node the current position in the frequent sub-graph tree.
    @param newLabel the DFScopde label for the edge to be added.
    @param the index into the given nodes extension lists.
    @param newNodeID the ID number for the new node. */

    private void extendNodeSiblingS2(FreqSubGraphNode node,
                            DFScodeLabel newLabel, int index, int newNodeID) {
        // Find label reference for cuurent node.
        DFScodeLabel oldLabel = node.extensionList[index].getDFScodeLabel();

        // Start node label must be the same as the parent node start label
        int oldStart = oldLabel.getStartNodeLabel();
        int newStart = newLabel.getStartNodeLabel();
        if (newStart==oldStart) {
            // Add sibling.
            int extCode = 1;
            extendNodeSibling(node,newLabel,index,newNodeID,extCode);
            // Incremnent counter
            nodesAtlevelN++;
            }
        }

    /** Extends a given node to produce a new child node. The node from which
    the extension takes place becomes an S1  (sibling tt sibling) identified by
    the integer 2
    @param node the current node in the frequent sub-graph tree from which the
    extensions will hang.
    @param DFScodeLabel the DFScode label for the new edge.
    @param the index into the given nodes extension lists.
    @param newNodeID the ID number for the new node.
    @param graphSoFar list of DFS codes representing the graph so far.  */

    private void extendNodeCSchild(FreqSubGraphNode node, DFScodeLabel label,
                            int index, int newNodeID,  DFScode[] graphSoFar) {
        // Get start node number and new DFS code
        int start = node.extensionList[index].getEndNode();
        DFScode newCode = new DFScode(start,newNodeID,label);

        // Check that candidate graph has not been generated before (first
        // update graph so far list).
        DFScode[] newGraphSoFar = appendToList(graphSoFar,newCode);
        if (findInFSGtree(newGraphSoFar)) {
            duplicateNodes++;
            prePrunedNodesAtlevelN++;
            return;
            }

        // Generate new frequent sub-graph tree node.
        FreqSubGraphNode newNode = new FreqSubGraphNode(newCode);

        // Add to extension list, extension is of type 1 (CS), child and
        // sibling. Change value for current node in extension code list to
        // type 2 (S1 sibling to sibling and child node).
        int newExtensionCode = 1;
        newNode.addToExtensionLists(node.extensionList,node.extensionCodeList,
                                                    newCode,newExtensionCode);
        newNode.extensionCodeList[index]=3;

        // Add to parent FSG tree node
        if (node.childLink == null) node.childLink=newNode;
        else {
            newNode.siblingLink = node.childLink;
            node.childLink      = newNode;
            }

        // Incremnent counter
        nodesAtlevelN++;
        }

    /** Extends a given node to produce a new sibling node.
    @param node the current node in the frequent sub-graph tree from which the
    extensions will hang.
    @param label the DFScode label for the new edge.
    @param index the index into the given nodes extension lists.
    @param newNodeID the next consecutive ID number for the new node.
    @param extCode the new spawn code: 1 for CS, 2 for S1 and 3 for S2 (0 for
    no spawn code).   */

    private void extendNodeSibling(FreqSubGraphNode node, DFScodeLabel label,
                                int index, int newNodeID, int extCode) {
        // Get start node number and genertae new DFS code
        int start = node.extensionList[index].getStartNode();
        DFScode newCode = new DFScode(start,newNodeID,label);

        // Generate newfrequent sub-graph tree node.
        FreqSubGraphNode newNode = new FreqSubGraphNode(newCode);

        // Copy extension list. We want extensions still to be done before
        // current extension node but not after. Then replace current code
        // in list with new code.
        newNode.copyExtensionListsUpToIndex(node.extensionList,
                                               node.extensionCodeList,index);
        newNode.extensionList[index]     = newCode;
        newNode.extensionCodeList[index] = extCode;

        // Add to parent
        if (node.childLink == null) node.childLink=newNode;
        else {
            newNode.siblingLink = node.childLink;
            node.childLink      = newNode;
            }
        }

    /* ------------------------------------------------------------- */
    /*                                                               */
    /*       CALCULATE SUPPORT FOR CANDIDATE GRAPHS IN LEVEL N       */
    /*                                                               */
    /* ------------------------------------------------------------- */

    /** Starts process of claculating support for candidate sun graphs at the
    given lenel.
    @param level the desired level in the tree. */

    private void calculateSupport(int level) {
        // Start DFS code
        DFScode[] startDFScode = null;

        // Ptocess
        calculateSupport(fsgTreeStart,startDFScode,level);
        }

    /** Calculates support for candidate sub-graphs.
    @param link the current location in the sub-graph tree.
    @prama code the code so far for the current candidate sub-graph.
    @param level the desired level in the tree. */

    private void calculateSupport(FreqSubGraphNode link, DFScode[] code,
                                                         int level) {
        // At right level (note this is level in frquent sub graph tree, it
        // does not refer to levels in the sub-graphs themselves).
        if (level==1) {
//System.out.println("RIGHT LEVEL");
            calculateSupport(link,code);
            }

        // At wrong level
        else {
            while (link!=null) {
//System.out.println("WRONG LEVEL");
                DFScode[] newCode = link.dfsCode.appendCode(code);
                if (link.childLink!=null) calculateSupport(link.childLink,
                                                            newCode,level-1);
                link=link.siblingLink;
                }
            }
        }

    /** Calculates support for candidate sub-graphs having arrived at right
    level.
    @param link the current location in the sub-graph tree.
    @prama code the code so far for the current candidate sub-graph.   */

    protected void calculateSupport(FreqSubGraphNode link, DFScode[] code) {
//System.out.println("calculateSupport/2");
        while (link!=null) {
            // Complete DFS code lists for candidate graph
            DFScode[] newCode = link.dfsCode.appendCode(code);
            // Loop through data set looking for graphs in which candidate
            // graph appears.
            int support = 0;
            ArrayList<Integer> supportList = new ArrayList<Integer>();
            for (int index=0;index<inputDFSdata.length;index++) {
                if (inputDFSdata[index].codeSetInGraph(newCode)) {
                	support++;
                	supportList.add(index);
                	}
                }
            link.setSupport(support);
            if(!supportList.isEmpty()) link.setSupportList(supportList);
            link=link.siblingLink;
            }
        }

    /* -------------------------- */
    /*                            */
    /*        PRUNE LEVEL N       */
    /*                            */
    /* -------------------------- */

    /** Starts process of claculating support for candidate sun graphs at the
    given lenel.
    @param level the desired level in the tree. */

    private void pruneFSGtree(int level) {
        // Initialise parent node
        FreqSubGraphNode parentLink = null;
        // Initilise counter
        prunedNodesAtlevelN = 0;
        // Prune
        pruneFSGtree(parentLink,fsgTreeStart,level);
        }

    /** Prunes unsupported nodes from a fiven level in the FSG tree.
    @param link the current location in the sub-graph tree.
    @parentLink the reference to the parent node.
    @param level the desired level in the tree. */

    private void pruneFSGtree(FreqSubGraphNode parentLink,
                                        FreqSubGraphNode link, int level) {
        // At right level (note this is level in frquent sub graph tree, it
        // does not refer to levels in the sub-graphs themselves).
        if (level==1) {
            FreqSubGraphNode marker = null;
            boolean start = true;
            while (link!=null) {
                // Unsupported
                if (link.support<minSupport) {
                    prunedNodesAtlevelN++;
                    // If start
                    if (start) {
                        parentLink.childLink = link.siblingLink;
                        }
                    else marker.siblingLink = link.siblingLink;
                    FreqSubGraphNode temp = link.siblingLink;
                    link = null;
                    link = temp;
                    }
                else {
                    // If start
                    if (start) {
                        start  = false;
                        marker = link;
                        }
                    else marker = link;
                    link=link.siblingLink;
                    }
                }
            }

        // At wrong level
        else {
            while (link!=null) {
                if (link.childLink!=null) pruneFSGtree(link,link.childLink,
                                                                level-1);
                link=link.siblingLink;
                }
            }
        }

    /* ------------------------------------------------------ */
    /*                                                        */
    /*                     SEARCH METHODS                     */
    /*                                                        */
    /* ------------------------------------------------------ */

    /** Top level method that check whether a given DFS code is in the frequent
    sub-graph (FSG) tree as constructed sofar. Note that we need to check all
    permutations of the given DFS code.
    @param code given set of DFS codes.
    @return true if found, false otherwise. */

    protected boolean findInFSGtree(DFScode[] code) {
        // Define permutaion DFS code array.
        DFScode[] permCode = new DFScode[code.length];
        // Define permutation DFS code array index.
        int pIndex=0;
        // Proceed
        return(findInFSGtree(code,pIndex,permCode));
        }

    /** Continues process of checking whether a given DFS code is in the given
    fequent sub-graph (FSG) tree. More specifically method recursively
    calculate permutations of the input code and then passes these on for
    checking.
    @param code given set of DFS codes.
    @param pIndex the permutation DFS code array index.
    @param permCode the permutation DFS code array.
    @return true if found, false otherwise. */

    private boolean findInFSGtree(DFScode[] code, int pIndex,
                                                         DFScode[] permCode) {
        // Base case, check
        if (code==null) return(findInFSGtreeStart(permCode));

        // Loop
        for (int index=0;index<code.length;index++) {
            // Assign
            permCode[pIndex] = code[index];
            // Repeat
            if (findInFSGtree(deleteElement(code,index),pIndex+1,
                                                      permCode)) return(true);
            }

        // Return
        return(false);
        }

    /** Continues process of checking whether a given DFS is in the given
    fequent sub-graph (FSG) tree.
    @param code given permutation of the input set of DFS codes.
    @return true if found, false otherwise. */

    private boolean findInFSGtreeStart(DFScode[] code) {
//System.out.print("PERMUTATION code = ");
//outputDFSCodeListLabels(code);
//System.out.println();
       int codeIndex = 0;
       // The list of structure codes for the serach code list and the chain
       // of FSG tree nodes identified so far.
       int[] listSrch_Cd = null;
       int[] listFSGtree = null;
String s = "(";
       return(findInFSGtree(code,codeIndex,fsgTreeStart,listSrch_Cd,
                                                                 listFSGtree,s));
       }

    /** Method to continue process of checking whether given DFS is in the
    given fequent sub-graph (FSG) tree or some sub-tree of it. Levels in tree
    are ordered so no need to search entire level.
    @param codeList given list of DFS codes (the serach codes).
    @param codeIndex the current inex into the given DFS code list.
    @param fsgTreeRef the reference to the current locatioon in the FSG tree
    (which holds the comparitor code).
    @param listSrch_Cd the list of structure codes for the serach code list.
    @param listFSGtree thelist of structure codes for the chain of FSG tree
    nodes identified so far.
    @return true if found, false otherwise. */

    protected boolean findInFSGtree(DFScode[] codeList, int codeIndex,
                                FreqSubGraphNode fsgTreeRef, int[] listSrch_Cd,
                                                 int[] listFSGtree, String s) {
int counter = 1;
        FreqSubGraphNode link = fsgTreeRef;

        while(link!=null) {
//System.out.println("Node = " + s + counter + ")");
            // -1 = before, 0 = same, 1 = after
            int paritor = codeList[codeIndex].comparesWith(link.dfsCode);
//System.out.println("COMPARE WITH codeList[" + codeIndex + "] = " +
//codeList[codeIndex].toStringLabels(newVertexTree,newEdgeTree) +
//", link.dfsCode = " +
//link.dfsCode.toStringLabels(newVertexTree,newEdgeTree));
//System.out.println("paritor = " + paritor);
            // Test
            switch (paritor) {
                // Before, move on to next sibling
                case -1:
                    break;
                // Codes the same:
                case 0:
                    // Update structure list
                    int startNode = codeList[codeIndex].getStartNode();
                    int endNode   = codeList[codeIndex].getEndNode();
                    int[] newListSrch_Cd = appendToList(listSrch_Cd,
                                                           startNode,endNode);
                    startNode = link.dfsCode.getStartNode();
                    endNode   = link.dfsCode.getEndNode();
                    int[] newListFSGtree = appendToList(listFSGtree,
                                                           startNode,endNode);
                    // If no more codes in code list check structure.
                    if (codeIndex+1==codeList.length) {
//System.out.println("FOUND CODES");
                        // If same return true
                        if (checkStructure(newListSrch_Cd,newListFSGtree))
                                                                 return(true);
                        }
                    // Otherwise continue processing (provided there is a child
                    // link)
                    else {
                        if (link.childLink!=null) {
//System.out.println("PROCEED DOWN TREE");
                            if (findInFSGtree(codeList,codeIndex+1,
                                                 link.childLink,newListSrch_Cd,
                                   newListFSGtree,s+counter+".")) return(true);
                            }
//System.out.println("NO CHILD");
                        }
                    break;
                default:
                    // Past, return false
                    return(false);
                }
            // Next node
            link=link.siblingLink;
            counter++;
            }

        // End
        return(false);
        }

    /** Checks the structure of the search code and FSG tree path structure
    lists. If both lists must have the same format, the locations for the
    number N in one list must be the same as for the number M in the other
    list. WARNING: Identical piece of code graphDFScode class (bad practice I
    know!).
    @param list1 the given forst list.
    @param list2 the gicen second list.
    @return true if the two lists have the same structure and false
    otherwise. */

    protected boolean checkStructure(int[] list1, int[] list2) {
//System.out.print("CHECK STRUCTURE: list1 = ");
//outputArray(list1);
//System.out.print("; list2 = ");
//outputArray(list2);
//System.out.println();
        // Get highest number in list
        int max = 1 + getMaxNumInList(list1);

        // process numbers
        for (int num1=0;num1<max;num1++) {
            boolean firstOccurance = true;
            int num2 = -1;
            // Find locations in list
            for (int index=0;index<list1.length;index++) {
                if (num1==list1[index]) {
                    // If first occurance get number in list2
                    if (firstOccurance) {
                        num2           = list2[index];
                        firstOccurance = false;
                        }
                    else {
                        if (num2!=list2[index]) return(false);
                        }
                    }
                }
            }

        // End
        return(true);
        }

    /* ----------------------------------------------------------- */
    /*                                                             */
    /*                     DFS CODE LABEL LIST                     */
    /*                                                             */
    /* ----------------------------------------------------------- */

    /** The graph set comprises a finite set of {start_node,edge,end_node}
    value tuples. The expectation is that these are repeated frequently
    within the graph set and thus space savings can be made by storing a
    single reference to groups of labels rather than the labels
    themselves. This method generates a tree structure which allows fast
    look up of these structures and returns a reference to the
    DFScodeLabel stored at the leaf.
    @param edgeArray the array of edges ordered according to frequency.
    @param nodeArray the array of nodes ordered according to frequency.  */

    private void generateDFScodeLabelList() {
        // Get number of supported nodes and edges, and dimension list
        int numNodes     = newVertexTree.getNumFrequentNodes();
        int numEdges     = newEdgeTree.getNumFrequentEdges();
        dfsCodeLabelList = new DFScodeLabel[numNodes][numEdges][numNodes];

        // Populate
        for (int startNodeID=0;startNodeID<numNodes;startNodeID++) {
            for (int edgeID=0;edgeID<numEdges;edgeID++) {
                for (int endNodeID=0;endNodeID<numNodes;endNodeID++) {
                    dfsCodeLabelList[startNodeID][edgeID][endNodeID] =
                               new DFScodeLabel(startNodeID,edgeID,endNodeID);
                    }
                }
            }
        }

    /** Get the DFS code label for a given ID number triple.
    @param startIndex the ID number for the start node
    @param edgeIndex the ID bumveer for the esge.
    @param endIndex the ID number for the end node.
    @retirn the associated DFS code label. */

    public DFScodeLabel getDFScodeLabel(int startIndex, int edgeIndex,
                                                               int endIndex) {
        return(dfsCodeLabelList[startIndex][edgeIndex][endIndex]);
        }

    /* -------------------------------------------------- */
    /*                                                    */
    /*                  INPUT DATA METHODS                */
    /*                                                    */
    /* -------------------------------------------------- */

    /** Process input graphML set so that graphs are encoded in DFS (Depth
    First Search) format. Note that only supported codes are included. */

    private void convertInput2dfsCodes() {
        int total1 = 0;
        int total2 = 0;

        // Dimension
        inputDFSdata = new GraphDFScode[graphData.length];

        // Loop through graph set
        for (int index=0;index<graphData.length;index++) {
            // Create GrpahDFScode object
            inputDFSdata[index] = new GraphDFScode(minSupport,this,
                                                  newVertexTree,newEdgeTree);
            // Populate GrpahDFScode object
            int numDFScodes =
                   inputDFSdata[index].convertInput2dfsCodes(graphData[index]);
            total1 = total1 + graphData[index].getNumEdges();
            total2 = total2 + numDFScodes;
            }

        // Output
        if (total1==total2) System.out.println("Pruning 1: No reduction\n");
        else {
            double average = 100.0-(((double) total2/(double) total1)*100.0);
            System.out.println("Pruning 1: Number of DFS codes in input " +
                                 "reduced from " + total1 + " to " + total2 +
                                      " (" + (twoDecPlaces(average)) + "%)");
            }
        }

    /** Prunes the input dara removing unsuporred one edge graphs. */

    private void pruneInputData() {
        int total1 = 0;
        int total2 = 0;
        // Loop
        for (int index=0;index<inputDFSdata.length;index++) {
            total1 = total1+inputDFSdata[index].getNumDFScodes();
            total2 = total2+inputDFSdata[index].pruneInputData();
            }

        // Output
        if (total1==total2) {
            System.out.println("Pruning 2: No further reduction");
            return;
            }
        double average = 100.0 - (((double) total2 / (double) total1) * 100.0);
        System.out.println("Pruning 2: Number of DFS codes in input reduced " +
        " from " + total1 + " to " + total2 + " (" + (twoDecPlaces(average)) +
        "%)");
        }

    /* ---------------------------------------------- */
    /*                                                */
    /*                  TREE METHODS                */
    /*                                                */
    /* ---------------------------------------------- */

    /** Search through Frequent Sub-graph Tree for given code.
    @param code the given code.
    @return the trur if found, false otherwise.    */

    public boolean findCodeInTree(DFScodeLabel code) {
        // Find block of DFS codes in graph that contain start node for the
        // search code
        int cStart  = code.getStartNodeLabel();
        int cEdge   = code.getStartNodeLabel();
        int cEnd    = code.getStartNodeLabel();

        FreqSubGraphNode link = fsgTreeStart;
        while (link!=null) {
            if (cStart==link.dfsCode.getStartNodeLabel() &&
                        cEdge==link.dfsCode.getStartNodeLabel() &&
                           cEnd==link.dfsCode.getStartNodeLabel())
                                                                 return(true);
            // Otherwise try next
            link = link.siblingLink;
            }

        // End
        return(false);
        }

    /* ---------------------------------------------- */
    /*                                                */
    /*                  OUTPUT METHODS                */
    /*                                                */
    /* ---------------------------------------------- */

    /* This is mostly for diagnostic purposes. */

    /** Outputs number of frequent sub-graph tree nodes. */

    public void outputNumFSGtreeNodes() {
        // Initialise counter
        int counter = 0;

        // Get number of node
        counter = getNumFSGtreeNodes(fsgTreeStart,counter);

        // End
        System.out.println("Number of FSG tree nodes = " + counter);
        }

    /** Gets number of frequent sub-graph tree nodes (recursive method).
    @param link the current node in the node tree.
    @param counter the count so far. */

    private int getNumFSGtreeNodes(FreqSubGraphNode link, int counter) {
        // Loop
        while (link!=null) {
            if (link.childLink!=null) counter =
                               getNumFSGtreeNodes(link.childLink,counter);
            // Increment counter
            counter=counter+1;
            // Next
            link =link.siblingLink;
            }

        // End
        return(counter);
        }

    /** Output DFS code label list. This is the list of all supported 1 edge
    labels that are used through out the process (they are the identified
    edges that can be used to "grow" a graph). */

    private void outputDFScodeLabelList() {
        System.out.println("DFS CODE LABEL LIST\n-------------------");

        // Loop
        for (int index=0;index<dfsCodeLabelList.length;index++) {
            System.out.println("(" + index + ") " + dfsCodeLabelList[index]);
            }

        // End
        System.out.println();
        }

    /** Outputs input graphs as DFScodes. */

    private void outputInputDFSdata() {
        System.out.println("INPUT GRAPHS AS DFS CODES");
        for (int index=0;index<inputDFSdata.length;index++) {
            System.out.print("(" + index + ") ");
            inputDFSdata[index].outputDFSCodeGraph();
            }

        // End
        System.out.println();
        }

    /** Outputs input graphs as DFScodes translated to labels. */

    private void outputInputDFSdataLabels() {
        System.out.println("INPUT GRAPHS AS DFS CODES");
        for (int index=0;index<inputDFSdata.length;index++) {
            System.out.print("(" + index + ") ");
            inputDFSdata[index].outputDFSCodeGraphLabels();
            }

        // End
        System.out.println();
        }

    /** Outputs given array of DFS codes (used in relation to various types of
    output).
    @param list the goven DFS code array*/

    private void outputDFSCodeList(DFScode[] list) {
        if (list==null) System.out.print("null");
        else {
            // Loop
            for (int index=0;index<list.length;index++) {
                if (index>0) System.out.print("," + list[index]);
                else System.out.print(list[index]);
                }
            }
        }

    /** Outputs list of DFS codes as string labels. */

    protected void outputDFSCodeListLabels(DFScode[] list) {
        if (list==null) System.out.print("null");
        else {
            // Loop
            for (int index=0;index<list.length;index++) {
                if (index>0) System.out.print("," +
                        list[index].toStringLabels(newVertexTree,newEdgeTree));
                else System.out.print(list[index].
                                    toStringLabels(newVertexTree,newEdgeTree));
                }
            }
        }

    /** Outputs frequent sub-graph tree usimg numeric codes. */

    public void outputFSGtree() {
        FreqSubGraphNode link = fsgTreeStart;

        System.out.println("FREQUENT SUB-GRAPH TREE\n-----------------------");

        int counter = 1;
        while (link!=null) {
            System.out.println("(" + counter + ") " + link);
            // Child branch
            if (link.childLink!=null) {
                String s = "(" + counter + ".";
                outputFSGtree(link.childLink,s);
                }
            // Next
            counter++;
            link =link.siblingLink;
            }

        // End
        System.out.println();
        }

    /** Outputs frequent sub-grapg tree node.
    @param link the current node in the node tree.
    @param s1 the output string so far. */

    private void outputFSGtree(FreqSubGraphNode link, String s1) {
        int counter = 1;
        while (link!=null) {
            System.out.println(s1 + counter + ") " + link);
            if (link.childLink!=null)  {
                String s2 = s1 + counter + ".";
                outputFSGtree(link.childLink,s2);
                }
            // Next
            counter++;
            link =link.siblingLink;
            }

        // End
        System.out.println();
        }

    /** Outputs frequent sub-graph tree with string labels. */

    public void outputFSGtreeLabels() {
        FreqSubGraphNode link = fsgTreeStart;

        System.out.println("FREQUENT SUB-GRAPH TREE\n-----------------------");

        int counter = 1;
        while (link!=null) {
            System.out.println("(" + counter + ") " +
                              link.toStringLabels(newVertexTree,newEdgeTree));
            // Child branch
            if (link.childLink!=null) {
                String s = "(" + counter + ".";
                outputFSGtreeLabels(link.childLink,s);
                }
            // Next
            counter++;
            link =link.siblingLink;
            }

        // End
        System.out.println();
        }

    /** Outputs frequent sub-graph tree node with string labels.
    @param link the current node in the node tree.
    @param s1 the output string so far. */

    private void outputFSGtreeLabels(FreqSubGraphNode link, String s1) {
        int counter = 1;
        while (link!=null) {
            System.out.println(s1 + counter + ") " +
                              link.toStringLabels(newVertexTree,newEdgeTree));
            if (link.childLink!=null)  {
                String s2 = s1 + counter + ".";
                outputFSGtreeLabels(link.childLink,s2);
                }
            // Next
            counter++;
            link =link.siblingLink;
            }
        }

    /** Outputs frequent sub-graph tree to CSV file
    @param fileName Output file name. */

    public void outputFSGtreeCSV(String fileName) {
        FreqSubGraphNode link = fsgTreeStart;

        System.out.println("FREQUENT SUB-GRAPH TREE CSV\n-----------------------------");

        int counter = 1;
        ArrayList<String> headerList = new ArrayList<String>();
        ArrayList<ArrayList<Integer>> supportLists = new ArrayList<ArrayList<Integer>>();
        while (link!=null) {
        	headerList.add(String.valueOf(counter));
        	supportLists.add(link.supportList);
            System.out.println("(" + counter + ") " +
                              link.toStringLabels(newVertexTree,newEdgeTree));
            // Child branch
            if (link.childLink!=null) {
                String s = "(" + counter + ".";
                outputFSGtreeCSV(link.childLink,s, headerList, supportLists);
                }
            // Next
            counter++;
            link =link.siblingLink;
            }

        // prepare data to output
        String contain = "";
        ArrayList<String> containList = new ArrayList<String>();
        if(!headerList.isEmpty()){
        	contain = "No," + headerList.get(0);
            for(int i=1; i < headerList.size(); i++){
            	contain += "," + headerList.get(i);
            }
            containList.add(contain);
            for(int i=0; i < graphData.length; i++){
            	contain = String.valueOf(i+1);
            	for(int j=0; j < supportLists.size(); j++){
            		contain += "," + (supportLists.get(j).contains(i)?"1":"0");
            	}
            	containList.add(contain);
            }
        }

        //output to file
        if(fileName != null){
        	try {
				FileWriter writer = new FileWriter(fileName);
		        for(int i=0; i < containList.size(); i ++){
		        	System.out.println("Line " + i + " (" + containList.get(i).split(",").length + "): " + containList.get(i));
		        	writer.write(containList.get(i) + System.getProperty( "line.separator" ));		        	
		        }
		        writer.close();
			} catch (IOException e) {
				System.out.println("\n outputFSGtreeCSV Exception: " + e.getMessage());
			}
        }
        
        
        // End
        System.out.println();
        }

    /** Outputs frequent sub-graph tree node with string labels.
    @param link the current node in the node tree.
    @param s1 the output string so far. 
    @param headerList array for header string. */

    private void outputFSGtreeCSV(FreqSubGraphNode link, String s1, ArrayList<String> headerList, ArrayList<ArrayList<Integer>> supportLists) {
        int counter = 1;
        while (link!=null) {
        	headerList.add(s1.substring(1) + counter);
        	supportLists.add(link.supportList);
            System.out.println(s1 + counter + ") " +
                              link.toStringLabels(newVertexTree,newEdgeTree));
            if (link.childLink!=null)  {
                String s2 = s1 + counter + ".";
                outputFSGtreeCSV(link.childLink,s2,headerList,supportLists);
                }
            // Next
            counter++;
            link =link.siblingLink;
            }
        }

    /* ---------------------------------------------------- */
    /*                                                      */
    /*                        UTILITIES                     */
    /*                                                      */
    /* ---------------------------------------------------- */

    /** Get the highest number in the list.
    @param list the given list.
    @return the maximum (numerically highest) number. */

    private int getMaxNumInList(int list[]) {
        // Get highest number
        int max = list[0];
        for (int index=1;index<list.length;index++) {
            if (list[index]>max) max=list[index];
            }

        // End
        return(max);
        }

    /** Outputs an integer array.
    @param array the given array. */

    private void outputArray(int[] array) {
        if (array==null) System.out.print("null");
        else {System.out.print("[");
            for (int i=0;i<array.length;i++) {
                if (i>0) System.out.print("," + array[i]);
                else System.out.print(array[i]);
                }
            System.out.print("]");
            }
        }

    /** Appends given numbers to given array.
    @param list the given array
    @param num1 the first given number
    @param num2 the second given mumber
    @return the array with the given numbers appended to it. */

    protected int[] appendToList(int[] list, int num1, int num2) {
        // Dimension mew list
        int size;
        if (list==null) size = 2;
        else size = list.length+2;
        int[] newList = new int[size];

        // Loop
        int i   = 0;
        int end = size-2;
        for (;i<end;i++) newList[i]=list[i];

        // End
        newList[i]   = num1;
        newList[i+1] = num2;
        return(newList);
        }

    /** Append given DFS code to given DFS code list.
    @param code the new DFS code.
    @param list the DFS code list to be extended.
    @return the new list. */

    protected DFScode[] appendToList(DFScode[] list, DFScode code) {
        // Dimension mew list
        int size;
        if (list==null) size = 1;
        else size = list.length+1;
        DFScode[] newList = new DFScode[size];

        // Loop
        int i   = 0;
        int end = size-1;
        for (;i<end;i++) newList[i]=list[i];

        // End
        newList[i] = code;
        return(newList);
        }

    /** Removes the indicated elemnet from the DFS vode list and returns the
    list with the element removed.
    @param code the given DFS code list.
    @param index the index of the element to be removed from the goven DFS code
    list.
    @return the DFS code list with the element removed.      */

    private DFScode[] deleteElement(DFScode[] code, int index) {
        if (code.length==1) return(null);

        // Define new code
        DFScode[] newCode = new DFScode[code.length-1];

        // Loop
        int counter = 0;
        for (int cIndex=0;cIndex<code.length;cIndex++) {
            if (cIndex!=index) {
                newCode[counter]=code[cIndex];
                counter++;
                }
            }

        // End
        return(newCode);
        }

    /** Outputs difference between two given times.
    @param time1 the first time.
    @param time2 the second time.
    @return duration. */

    public double outputDuration(double time1, double time2) {
        double duration = (time2-time1)/1000;
	System.out.println("Generation time = " + twoDecPlaces(duration) +
			" seconds (" + twoDecPlaces(duration/60) + " mins)");

	// Return
	return(duration);
	}
	}
