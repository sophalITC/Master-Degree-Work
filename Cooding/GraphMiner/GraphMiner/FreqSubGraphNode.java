import java.util.ArrayList;

/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                F R E Q U E N T   S U B   G R A P H   N O D E               */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                          Thursday 23 Ocyober 2008                          */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/** Tree node structure in which to store Frequent Sub Graphs (FSGs). Node 
represents an edge encoded using a Depth First Search (DFS) code, i.e. start 
and end node ID numbers, start and end node label identifiers and an edge 
label identifier. Note that initially the tree will include candidate frequent 
sub graphs that turn out not to be frequent:
@author Frans Coenen
@version 23 October 2008          */

/*  Four different codes for how a frequent sub graph tree can be extended:
0 No extension (N).
1 Child and sibling extension (CS), first leading to another code 1 node, second
  to a code 2 node (sibling leading to sibling).
2 Sibling only extension (S1), i.e. sibling leading to sibling node where the
  sibling is also a code 2 node (producess a "chain" of siblings).
3 Sibling extension (S2), sibling nodes has both child and sibling extensions,
  e.e. sibling node is a code 1 node.


For Right Most Extension (RMT) a fourth code is rrquired:
4 Child only extension (C1), i.e. child leading to child node where the
sibling is also a code 4 node (producess an "ancestor" chain).
Note that in the RMT mode code 1 (CS) is also used except that in this case the
child code leads to a code 5 (C1).   */

public class FreqSubGraphNode {
    /** DFS code for node. */
    public DFScode dfsCode = null;
    /** List of DFS code to be extended according to their extension codes.
    List is a set of references to nodes in the frequent subgraph tree that
    could include this node. */
    public DFScode[] extensionList = null;
    /** Extension codes. */
    public int[] extensionCodeList = null;
    /** Support value */
    public int support = 0;
    /** Reference to sibling node. */
    public FreqSubGraphNode siblingLink = null;
    /** Reference to child node. */
    public FreqSubGraphNode childLink = null;
    /** Supported graph's id **/
    public ArrayList<Integer> supportList = null;

    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/

    /** One argument constructor.
    @param code a given dfs code. */

    public FreqSubGraphNode(DFScode code) {
        dfsCode = code;
        }

    /** Two argument constructor for creating start 1 edge nodes with given
    support. Note that code is automatically addede to extension list with an 
    extemsion vcode of 1 --- Child and Sibling (CS)
    @param code dfs code.
    @patam sup the associated support value. */

    public FreqSubGraphNode(DFScode code, int sup) {
        dfsCode = new DFScode(0,1,code.getDFScodeLabel());
        support = sup;
        // Create extension list (extension code is 1 by default).
        extensionList        = new DFScode[1];
        extensionList[0]     = dfsCode;
        extensionCodeList    = new int[1];
        extensionCodeList[0] = 1;
        }

    public FreqSubGraphNode(DFScode code, ArrayList<Integer> supList) {
    	this(code, supList.size());
    	supportList = supList;
        }

    /* -------------------------------------------------- */
    /*                                                    */
    /*                        METHODS                     */
    /*                                                    */
    /* -------------------------------------------------- */

    /** Adds the given node and extension code to to end of the given
    extension list and associated extension code list.
    @param oldList the given extension list.
    @param oldCodeList associated extension code list.
    @param node the node to be added.
    @param extCode extension code to be added. */

    public void addToExtensionLists(DFScode[] oldList, int[] oldCodeList,
                                              DFScode node, int extCode)  {
        extensionList     = new DFScode[oldList.length+1];
        extensionCodeList = new int[oldCodeList.length+1];

        // Loop
        int index=0;
        for (;index<oldList.length;index++) {
            extensionList[index]     = oldList[index];
            extensionCodeList[index] = oldCodeList[index];
            }

        // End
        extensionList[index]     = node;
        extensionCodeList[index] = extCode;
        }

    /** Copies given node and extension code list.
    @param oldList the given extension list.
    @param oldCodeList associated extension code list. */

    public void copyExtensionLists(DFScode[] oldList, int[] oldCodeList)  {
        extensionList     = new DFScode[oldList.length];
        extensionCodeList = new int[oldCodeList.length];

        // Loop
        for (int index=0;index<oldList.length;index++) {
            extensionList[index]     = oldList[index];
            extensionCodeList[index] = oldCodeList[index];
            }
        }

    /** Copies given node and extension code list up to and inclufing given
    end index.
    @param oldList the given extension list.
    @param oldCodeList associated extension code list.
    @param endIndex the given end index, */

    public void copyExtensionListsUpToIndex(DFScode[] oldList,
                                        int[] oldCodeList, int endIndex)  {
        extensionList     = new DFScode[endIndex+1];
        extensionCodeList = new int[endIndex+1];

        // Loop
        for (int index=0;index<extensionList.length;index++) {
            extensionList[index]     = oldList[index];
            extensionCodeList[index] = oldCodeList[index];
            }
        }
        
    /** Copies given node and extension code list up to and inclufing given
    end index and then adds new node and code.
    @param oldList the given extension list.
    @param oldCodeList associated extension code list.
    @param endIndex the given end index.
    @param node the node to be added.
    @param extCode extension code to be added.	 */

    public void copyAndAppendExtensionListsUpToIndex(DFScode[] oldList,
                                        int[] oldCodeList, int endIndex,
                                              DFScode node, int extCode)  {
        extensionList     = new DFScode[endIndex+2];
        extensionCodeList = new int[endIndex+2];

        // Loop
        int end = endIndex+1;
        for (int index=0;index<end;index++) {
            extensionList[index]     = oldList[index];
            extensionCodeList[index] = oldCodeList[index];
            }
            
        // Finiish 
        extensionList[end]     = node;
        extensionCodeList[end] = extCode;
        }

    /* ------------------------------------------------------ */
    /*                                                        */
    /*                        SET METHODS                     */
    /*                                                        */
    /* ------------------------------------------------------ */

    /** Set the value for the support field to the given value.
    @param sup the given support value. */

    public void setSupport(int sup) {
        support=sup;
        }

    public void setSupportList(ArrayList<Integer> supList){
    	supportList = supList;
    	}
    /* ------------------------------------------------------ */
    /*                                                        */
    /*                        GET METHODS                     */
    /*                                                        */
    /* ------------------------------------------------------ */

    /* ------------------------------------------------- */
    /*                                                   */
    /*                        OUTPUT                     */
    /*                                                   */
    /* ------------------------------------------------- */

    /** To string method.
    @return the constructed output string. */

    public String toString() {
        // DFS codes
        String s = " " + dfsCode;

        // Nodes to be extended
        if (extensionList==null) s = s + " null ";
        else {
            s = s + " [";
            for (int index=0;index<extensionList.length;index++) {
                if (index>0) s = s + ", ";
                s = s + extensionList[index] + " " +
                                    extCodeToString(extensionCodeList[index]);
                }
            s = s + "] ";
            }

        // End
        s = s + "support = " + support;
        return(s);
        }
    
    /** Converts extension code to a string for output purposes.
    @param extCode the given extension code.
    @return the return code. */

    private String extCodeToString(int extCode) {
        String s = null;

        // Switch
        switch (extCode) {
            case 0: s="N"; break;
            case 1: s="CS"; break;
            case 2: s="S1"; break;
            case 3: s="S2"; break; // Used with FSG tree only
            case 4: s="C1"; break; // Used with RME only
            }

        // End
        return(s);
        }
        
    /** To string method withstring labels.     
    @param vTree reference to vertex tree object.
    @param eTree reference to edge tree object.
    @return the constructed output string. */

    public String toStringLabels(VertexTree vTree, EdgeTree eTree) {
        // DFS codes
        String s = " " + dfsCode.toStringLabels(vTree,eTree);

        // Nodes to be extended
        if (extensionList==null) s = s + " null ";
        else {
            s = s + " [";
            for (int index=0;index<extensionList.length;index++) {
                if (index>0) s = s + ", ";
                s = s + extensionList[index] + " " +
                                    extCodeToString(extensionCodeList[index]);
                }
            s = s + "] ";
            }

        // End
        s = s + "support = " + support;
        if((supportList != null) && !supportList.isEmpty()){
        	s = s + ", (" + (supportList.get(0) + 1);
        	for(int i=1; i < supportList.size(); i++){
        		s = s + "," + (supportList.get(i) + 1);
        		}
        	s = s + ")";
        	}
        return(s);
        }
    }

