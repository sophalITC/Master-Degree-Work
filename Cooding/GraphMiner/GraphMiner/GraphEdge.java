/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                                G R A P H                                   */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                          Wednesday 6 February 2008                         */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */


/** Data structure to hold a single graph edge. Instances of this class are
included in the Graph data structure used to store a GrpahML represented
input graph
@author Frans Coenen
@version 6 February 2008          */

public class GraphEdge {
    /** Identifier for from node. */
    private int fromNode = 0;    
    /** Identifier for to node. */
    private int toNode   = 0;
    /** Edge label. */
    private String label = null;
    /** Support (used during gSpan process). */
    private int support = 0;

    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/

    /** Three argument constructor.
    @param source the given edges start point.
    @param target the given edges end point.
    @param eLabel the given edge lable. */

    public GraphEdge(int source, int target, String eLable) {
        fromNode = source;
        toNode   = target;
        label    = eLable;
        }

    /* ------------------------------------------------- */
    /*                                                   */
    /*                        OUTPUT                     */
    /*                                                   */
    /* ------------------------------------------------- */

    /** To string method.
    @returm the constructed output string. */

    public String toString() {
        String s = "from = " + fromNode + ", to = " + toNode + ", label = " + 
                                                                       label;

        // End
        return(s);
        }

    /* ------------------------------------------------------ */
    /*                                                        */
    /*                  SET AND GET METHODS                   */
    /*                                                        */
    /* ------------------------------------------------------ */
    
    /** Gets edge label for node "n".
    @return the node label.      */

    public String getEdgeLabel() {
        return(label);
        }
    
    /** Gets from node ID.
    @return the from node ID.      */

    public int getFromNodeID() {
        return(fromNode);
        }    
    
    /** Gets end node ID.
    @return the end node ID.      */

    public int getToNodeID() {
        return(toNode);
        }
    }

