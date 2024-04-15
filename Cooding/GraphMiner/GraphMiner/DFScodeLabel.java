/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                       D F S   C O D E   L A B E L                          */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                          Wednesday 1 October 2008                          */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/** Depth First Search (DFS) Code lebels data structure. The label comprises
start and end vertices, and an edge. The vertices and edge are stired 
according to their ID numbers. ID numbers can be converted to lables by using 
the vertx and edge trees as appropriate.
@author Frans Coenen
@version 7 October 2008          */

public class DFScodeLabel {   
    /** Identifier label for start node. */
    private int startNodeLabel = -1;
    /** Identifier label for edge. */
    private int edgeLabel      = -1;
    /** Identifier label for start node. */
    private int endNodeLabel   = -1;

    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/

    /** three argument constructor.
    @param eLabel the label for the edge.
    @param snLabel the label for the start node.
    @param enLabel the label for the end node. */

    public DFScodeLabel(int snLabel, int eLabel, int enLabel) {
        startNodeLabel = snLabel;
        edgeLabel      = eLabel;
        endNodeLabel   = enLabel;
        }

    /* -------------------------------------------------- */
    /*                                                    */
    /*                        METHODS                     */
    /*                                                    */
    /* -------------------------------------------------- */

    /** Comapres the "this" DFS codes with the given code and returns true if
    the first is lexicographically before the second.
    @param code the second DFS code.
    @return true if first is before or equals to second and false otherwise. */

    public boolean isBefore(DFScodeLabel code) {
        // Test start nodes
        if (startNodeLabel>code.getStartNodeLabel()) return(false);
        // Test edges if (start nodes the same)
        if (startNodeLabel==code.getStartNodeLabel()) {
            if (edgeLabel>code.getEdgeLabel()) return(false);
            // Test end nodes if (edges and start nodes the same)
            if (edgeLabel==code.getEdgeLabel()) {
                if (endNodeLabel>code.getEndNodeLabel()) return(false);
                }
            }

        // Otherwise return true;
        return(true);
        }

    /** Compares given code with this code label.
    @param newCode given DFS code.
    @return -1 if new code lexicographically before this code, 0 if new code
    is same as this code, and 1 if new code is after this code.  */

    public int comparesWith(DFScode newCode) {

        // Test strat node label
        int newStartNodeL = newCode.getStartNodeLabel();
        if (newStartNodeL<startNodeLabel) return(-1);
        if (newStartNodeL>startNodeLabel) return(1);

        // Same so test edge
        int newEdgeLabel = newCode.getEdgeLabel();
        if (newEdgeLabel<edgeLabel) return(-1);
        if (newEdgeLabel>edgeLabel) return(1);

        // Same so test end node label
        int newEndNodeL = newCode.getEndNodeLabel();
        if (newEndNodeL<endNodeLabel) return(-1);
        if (newEndNodeL>endNodeLabel) return(1);

        // End
        return(0);
        }

    /** Compares given code label (the search code) with this code label (the
    comparitor code).
    @param newCode given DFS code.
    @return -1 if search code is lexicographically before this code, 0 if 
    search code is same as this code, and 1 if search code is after this code.  */

    public int comparesWith(DFScodeLabel newCode) {

        // Test strat node label
        int newStartNodeL = newCode.getStartNodeLabel();
        if (newStartNodeL<startNodeLabel) return(-1);
        if (newStartNodeL>startNodeLabel) return(1);

        // Same so test edge
        int newEdgeLabel = newCode.getEdgeLabel();
        if (newEdgeLabel<edgeLabel) return(-1);
        if (newEdgeLabel>edgeLabel) return(1);

        // Same so test end node label
        int newEndNodeL = newCode.getEndNodeLabel();
        if (newEndNodeL<endNodeLabel) return(-1);
        if (newEndNodeL>endNodeLabel) return(1);

        // End
        return(0);
        }

    /* ------------------------------------------------------ */
    /*                                                        */
    /*                        GET METHODS                     */
    /*                                                        */
    /* ------------------------------------------------------ */

    
    /** Get the the value for the start node label field.
    @return the start node label. */

    public int getStartNodeLabel() {
        return(startNodeLabel);
        }

    /** Get the edge label.
    @return the label. */

    public int getEdgeLabel() {
        return(edgeLabel);
        }
    
    /** Get the the value for the end node label field.
    @return the end node label. */

    public int getEndNodeLabel() {
        return(endNodeLabel);
        }

    /* ------------------------------------------------- */
    /*                                                   */
    /*                        OUTPUT                     */
    /*                                                   */
    /* ------------------------------------------------- */

    /** To string method.
    @return the constructed output string. */

    public String toString() {
        String s = startNodeLabel + "," + edgeLabel + "," +
                 endNodeLabel;

        // End
        return(s);
        }          
    
    /** To string method with labels.    
    @param vTree reference to vertex tree object.
    @param eTree reference to edge tree object.
    @return the constructed output string. */

    public String toStringLabel(VertexTree vTree, EdgeTree eTree) {
        String s = vTree.getLabel(startNodeLabel) + "," + 
                eTree.getLabel(edgeLabel) + "," + vTree.getLabel(endNodeLabel);

        // End
        return(s);
        }
    }

