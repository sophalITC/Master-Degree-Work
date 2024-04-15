/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                             D F S   C O D E                                */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                          Wednesday 1 October 2008                          */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/** Depth First Search (DFS) Code data structure. Code comprises unique start 
and end vertex/node numbers (the verex numbers are allocated in a depth first 
search manner), vertex/mnde labels and the edge label. The last three are 
collectively refered to as the "DFS code label".
@author Frans Coenen
@version 7 October 2008          */

public class DFScode {
    /** Identifier for start node. */
    private int startNode = 0;
    /** Identifier for end node. */
    private int endNode = 1;
    /** The DFS code label (start node identifier, edge identifier and end
    node identifier. */
    private DFScodeLabel dfsCode = null;

    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/

    /** One argument constructor (assumes code is start of a new graph).
    @param code the DFS code . */

    public DFScode(DFScodeLabel cd) {
        dfsCode = cd;
        }

    /** Three argument constructor, uses default start and end node numbers of 
    0 and 1.
    @param startNodeLabel the identifier for the start node.
    @param edgeLabel the identifier for the edge.
    @param endNodeLabel the identifier for the end node. */

    public DFScode(int startNodeLabel, int edgeLabel, int endNodeLabel) {
        dfsCode = new DFScodeLabel(startNodeLabel,edgeLabel,endNodeLabel);
        }

    /** Three argument constructor.
    @param code the given DFS code label.
    @param sNode start node number.
    @param eNode end node number.   */

    public DFScode(int sNode, int eNode, DFScodeLabel code) {
        dfsCode   = code;
        startNode = sNode;
        endNode   = eNode;
        }

    /** five argument constructor.
    @param sNode start node number.
    @param eNode end node number.
    @param startNodeLabel the identifier for the start node.
    @param edgeLabel the identifier for the edge.
    @param endNodeLabel the identifier for the end node.  */

    public DFScode(int sNode, int eNode, int startNodeLabel, int edgeLabel,
                                                           int endNodeLabel) {
        startNode      = sNode;
        endNode        = eNode;
        dfsCode = new DFScodeLabel(startNodeLabel,edgeLabel,endNodeLabel);
        }

    /* -------------------------------------------------- */
    /*                                                    */
    /*                COMPARISON METHODS                  */
    /*                                                    */
    /* -------------------------------------------------- */

    /** Compares given code label with this code label.
    @param newCode given DFS code.
    @return -1 if lexicographically before, 0 if same, and 1 if after. */

    public int comparesWith(DFScode newCode) {
        return(dfsCode.comparesWith(newCode));
        }

    /* -------------------------------------------------- */
    /*                                                    */
    /*                   UTILITY METHODS                  */
    /*                                                    */
    /* -------------------------------------------------- */

    /** Appends the this code to the given list of codes.
    @param list the list of DFS codes so far.
    @return the extended list. */

    public DFScode[] appendCode(DFScode[] list) {
        DFScode[] newList;

        // Process
        if (list==null) {
            newList    = new DFScode[1];
            newList[0] = this;
            }
        else {
            newList   = new DFScode[list.length+1];
            int index = 0;
            for (;index<list.length;index++) newList[index]=list[index];
            newList[index] = this;
            }

        // End
        return(newList);
        }

    /* ------------------------------------------------------ */
    /*                                                        */
    /*                        GET METHODS                     */
    /*                                                        */
    /* ------------------------------------------------------ */

    /** Gets the DFS code label.
    @return the DFS code label. */
                                    
    public DFScodeLabel getDFScodeLabel() {
        return(dfsCode);
        }

    /** Gets identifier (number) for start node.
    @return the label. */

    public int getStartNode() {
        return(startNode);
        }

    /** Gets identifier (number) for end node.
    @return the label. */

    public int getEndNode() {
        return(endNode);
        }

    /** Gets the start node label reference.
    @return the label. */

    public int getStartNodeLabel() {
        return(dfsCode.getStartNodeLabel());
        }

    /** Gets the edge label reference.
    @return the label. */

    public int getEdgeLabel() {
        return(dfsCode.getEdgeLabel());
        }

    /** Gets the end node label reference.
    @return the label. */

    public int getEndNodeLabel() {
        return(dfsCode.getEndNodeLabel());
        }

    /* ------------------------------------------------- */
    /*                                                   */
    /*                        OUTPUT                     */
    /*                                                   */
    /* ------------------------------------------------- */

    /** To string method.
    @return the constructed output string. */

    public String toString() {
        String s = "[" + startNode + "," + endNode + "," + dfsCode + "]";
        // End
        return(s);
        }

    /** To string method with strimg labels.    
    @param vTree reference to vertex tree object.
    @param eTree reference to edge tree object.
    @return the constructed output string. */

    public String toStringLabels(VertexTree vTree, EdgeTree eTree) {
        String s = "[" + startNode + "," + endNode + "," + 
                                    dfsCode.toStringLabel(vTree,eTree) + "]";
        // End
        return(s);
        }
    }

