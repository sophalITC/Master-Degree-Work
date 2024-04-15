/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                            G   S P A N  (R M E)                            */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                           Sunday 14 December 2008                          */ 
/*                       Modified: 26 January 2009                            */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/** LUCS-KDD implentation of Gspan Algorithm using Right Most Extension (RME)
approach to growing candidate item sets:
@author Frans Coenen
@version 14 December 2008          */

public class GspanRME extends GraphMiner {

    /*------------------------------------------------------------------------*/
    /*                                                                        */
    /*                                   FIELDS                               */
    /*                                                                        */
    /*------------------------------------------------------------------------*/

    /** A temprary reference into the Frequenrt Sub-Graph (FSG) tree
    structure. */
    FreqSubGraphNode tempLinkRef = null;

    // Also inherited fields.

    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/

    /** Constructor with command line arguments to be process.
    @param args the command line arguments (array of String instances). */

    public GspanRME(String[] args) {
	// Process command line arguments
        super(args);
	}

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*         GENERATE LEVEL N IN FREQUENT SUB GRAPH (FSG) TREE        */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /** Continues process of Adding a node to the FDG tree according to the
    associated extension code.
    @param node the current position in the frequent sub-graph tree.
    @param label the DFScode label for the edge to be added.
    @param the index into the given nodes extension lists.
    @param newNodeID the ID number for the new node.
    @param graphSoFar list of DFS codes representing the graph so far.   */

    protected void genNextLevelSwitch(FreqSubGraphNode node, DFScodeLabel label,
                             int index, int newNodeID, DFScode[] graphSoFar) {
//System.out.print("SWITCH graphSoFar = ");
//outputDFSCodeListLabels(graphSoFar);
//System.out.println(", index = "+ index);

        // Get extension code.
        int extCode    = node.extensionCodeList[index];

        // Switch
        switch (extCode) {
            // Chid and sibling extension
            case 1:
                extendNodeCS(node,label,index,newNodeID,graphSoFar);
                break;
            // Child leading to Child, new extension code 4 = C1
            case 4:
                extendNodeChild(node,label,index,newNodeID,graphSoFar);
                break;
            // Should not get here    
            default: System.out.println("ERROR! extCode = " + extCode);
            }
        }

    /** Extends a given node to produce two new nodes, a child and sibling.
    Child node can produce new child and siblig nodes.
    @param node the current node in the frequent sub-graph tree from which the
    exrtensions will hang.
    @param label the DFScopde label for the edge to be added.
    @param the index into the given nodes extension lists.
    @param newNodeID the ID number for the new node.
    @param graphSoFar list of DFS codes representing the graph so far.    */

    private void extendNodeCS(FreqSubGraphNode node, DFScodeLabel label,
                             int index, int newNodeID, DFScode[] graphSoFar) {
//System.out.println("CS: label = " + label.toStringLabel(newVertexTree,newEdgeTree));
        // Add child leading to child node. New extension code 4 = C1.
        extendNodeChild(node,label,index,newNodeID,graphSoFar);

        // Add sibling leading to sibling node. New extension code 1 = CS.
        extendNodeSiblingS1(node,label,index,newNodeID,graphSoFar);
        }

    /** Extends a given node to produce a sibling node (which will spawn
    another sibling node etc. but no child nodes).
    @param node the current position in the frequent sub-graph tree.
    @param newLabel the DFScopde label for the edge to be added.
    @param the index into the given nodes extension lists.
    @param newNodeID the ID number for the new node.
    @param graphSoFar list of DFS codes representing the graph so far.       */

    private void extendNodeSiblingS1(FreqSubGraphNode node,
                                            DFScodeLabel newLabel, int index,
                                       int newNodeID, DFScode[] graphSoFar) {
//System.out.println("S1: newLabel = " + newLabel.toStringLabel(newVertexTree,newEdgeTree));
        // Find label reference for cuurent node.
        DFScodeLabel oldLabel = node.extensionList[index].getDFScodeLabel();

        // Start node label must be the same as the parent node start label
        int oldStart = oldLabel.getStartNodeLabel();
        int newStart = newLabel.getStartNodeLabel();
        if (newStart==oldStart) {
            // Add sibling.
            extendNodeSibling(node,newLabel,index,newNodeID,graphSoFar);
            }
//else System.out.println("Fail: satart nodes differrent");
        }

    /** Extends a given node to produce a new child node. The node from which
    the extension takes place becomes an S1  (sibling tt sibling) identified by
    the integer 2
    @param node the current node in the frequent sub-graph tree from which the
    extensions will hang.
    @param DFScodeLabel the DFScode label for the new edge.
    @param the index into the given nodes extension lists.
    @param newNodeID the ID number for the new node.
    @param graphSoFar list of DFS codes representing the graph so far.   */

    private void extendNodeChild(FreqSubGraphNode node, DFScodeLabel label,
                            int index, int newNodeID, DFScode[] graphSoFar) {
        // Start node label for child node must be the same as the parent
        // node end label
        int oldEnd   = node.extensionList[index].getEndNodeLabel();
        int newStart = label.getStartNodeLabel();
        // If not same return
        if (newStart!=oldEnd) {
//System.out.println("Fail: start and end nodes differrent");
            return;
            }

        // Get start node number and new DFS code
        int start = node.extensionList[index].getEndNode();
        DFScode newCode = new DFScode(start,newNodeID,label);

        // Check that candidate graph has not been generated before (first
        // update graph so far list).
        DFScode[] newGraphSoFar = appendToList(graphSoFar,newCode);
//System.out.print("===============\nnewGraphSoFar = ");
//outputDFSCodeListLabels(newGraphSoFar);
//System.out.println();
//outputFSGtreeLabels();
       if (findInFSGtree(newGraphSoFar)) {
            duplicateNodes++;
            prePrunedNodesAtlevelN++;
//System.out.println("FAIL EXISTS");
            // return
            return;
            }
//else System.out.println("SUCCEED");
//System.out.print("===============");

        // Generate new frequent sub-graph tree node and incremnert counter
        FreqSubGraphNode newNode = new FreqSubGraphNode(newCode);
        nodesAtlevelN++;
//System.out.println("newCode = " + newCode.toStringLabels(newVertexTree,newEdgeTree));
//System.out.println("Add Child node");

        // Copy extension list. We want extensions still to be done before
        // current extension node but not after. Then replace current code
        // in list with new code C4 (childs to child).
        int extCode = 4;
        newNode.copyAndAppendExtensionListsUpToIndex(node.extensionList,
                                node.extensionCodeList,index,newCode,extCode);

        // Add to parent FSG tree node
        if (node.childLink == null) node.childLink=newNode;
        else {
            newNode.siblingLink = node.childLink;
            node.childLink      = newNode;
            }
        }

    /** Extends a given node to produce a new sibling node. Checks whether
    sibling has been generated previously.
    @param node the current node in the frequent sub-graph tree from which the
    extensions will hang.
    @param label the DFScode label for the new edge.
    @param index the index into the given nodes extension lists.
    @param newNodeID the next consecutive ID number for the new node.
    @param graphSoFar list of DFS codes representing the graph so far.     */

    private void extendNodeSibling(FreqSubGraphNode node, DFScodeLabel label,
                 int index, int newNodeID, DFScode[] graphSoFar) {
        // Get start node number and genertae new DFS code
        int start = node.extensionList[index].getStartNode();
        DFScode newCode = new DFScode(start,newNodeID,label);

        // Check that candidate graph has not been generated before (first
        // update graph so far list).
        DFScode[] newGraphSoFar = appendToList(graphSoFar,newCode);
//System.out.print("newGraphSoFar = ");
//outputDFSCodeListLabels(newGraphSoFar);
//System.out.println();
       if (findInFSGtree(newGraphSoFar)) {
            duplicateNodes++;
//System.out.println("FAIL EXISTS");
            // Set support to -1
            tempLinkRef.support=-1;
            }

        // Generate newfrequent sub-graph tree node and incremnent counter
        FreqSubGraphNode newNode = new FreqSubGraphNode(newCode);
        nodesAtlevelN++;

        // Create new extension list
        newNode.extensionList        = new DFScode[1];
        newNode.extensionList[0]     = newCode;
        newNode.extensionCodeList    = new int[1];
        newNode.extensionCodeList[0] = 1;
//System.out.println("Add Sibling node at top");

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

    /** Calculates support for candidate sub-graphs having arrived at right
    level.
    @param link the current location in the sub-graph tree.
    @prama code the code so far for the current candidate sub-graph.   */

    protected void calculateSupport(FreqSubGraphNode link, DFScode[] code) {
        while (link!=null) {
            if (link.support!=-1) {
                // Complete DFS code lists for candidate graph
                DFScode[] newCode = link.dfsCode.appendCode(code);
                // Loop through data set looking for graphs in which candidate
                // graph appears.
                int support = 0;
                for (int index=0;index<inputDFSdata.length;index++) {
                    if (inputDFSdata[index].codeSetInGraph(newCode)) support++;
                    }
                link.setSupport(support);
                }
//else System.out.println("link.support = " + link.support);
            link=link.siblingLink;
            }
        }

    /* ------------------------------------------------------ */
    /*                                                        */
    /*                     SEARCH METHODS                     */
    /*                                                        */
    /* ------------------------------------------------------ */

    /** Method to continue process of checking whether given DFS is in the
    given fequent sub-graph (FSG) tree or some sub-tree of it. Levels in tree
    are ordered so no need to search entire level. NOTE whereas in DFS tree
    approach nodes are ordered in each level thus providing for fast searching
    this is not the case with left most extension.
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
            // Codes the same:
            if (paritor==0) {
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
                    if (checkStructure(newListSrch_Cd,newListFSGtree)) {
                        tempLinkRef = link;
                        return(true);
                        }
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
                }
            // Next node
            link=link.siblingLink;
            counter++;
            }

        // End
        return(false);
        }
    }
