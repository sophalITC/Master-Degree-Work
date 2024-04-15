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


/** Data structure to hold graph represente in GraphML format (used during 
initial graph input). Includes array of isntances of the class GrapgEdge.
@author Frans Coenen
@version 6 February 2008          */

public class Graph {
    /** Reference, into the inputGraphML array, that marks the start of the 
    individual block of GraphML for this graph. */
    private int graphRef = 0;
    /** Reference, into the inputGraphML array, that marks the start of the
    node block of GraphML for this graph. */
    private int nodeRef = 0;            
    /** Reference, into the inputGraphML array, that marks the start of the 
    edge block of GraphML for this graph. */
    public int edgeRef = 0;
    /** Number of nodes (verices) in this graph. */
    private int numNodes = 0;
    /** Number of edges in this graph. */
    public int numEdges = 0;
    /** Array of node labels ordered according to sequencing in Graph ML
    input. */
    private String nodeLabels[] = null;
    /** Array of edge structures ordered according to sequencing in Graph ML
    input. */
    private GraphEdge edges[] = null;

    /* ---------------------------------------------- */
    /*                                                */
    /*                  SET METHODS                   */
    /*                                                */
    /* ---------------------------------------------- */

    /** Sets the graph ref field to the given value.
    @param gRef the given reference (an index into the input array). */

    public void setGraphRef(int gRef) {
        graphRef = gRef;
        }

    /** Sets the node ref field to the given value.
    @param nRef the given reference (an index into the input array). */

    public void setNodeRef(int nRef) {
        nodeRef = nRef;
        }

    /** Sets the edge (link) ref field to the given value.
    @param lRef the given reference (an index into the input array). */

    public void setEdgeRef(int eRef) {
        edgeRef = eRef;
        }

    /** Sets the number of nodes field to the given value, and at the same
    time dimensions the nodeLabels array.
    @param nNodes the given number of nodes. */

    public void setNumNodes(int nNodes) {
        numNodes   = nNodes;
        nodeLabels = new String[numNodes];
        }

    /** Sets the number of edges (links) field to the given value, and at the
    same time dimensions the edges array structure.
    @param nLinksN the given number of nodes. */

    public void setNumEdges(int nEdges) {
        numEdges = nEdges;
        edges    = new GraphEdge[numEdges];
        }

    /** Sets node label array field at index i to the given value.
    @param nLabel the given node lable.
    @param index the given index into the ode label array. */

    public void setNodeLabel(String nLabel, int index) {
        nodeLabels[index] = nLabel;
        }

    /** Sets edge array field at index i to the given value.
    @param source the given edges start point.
    @param target the fiven edges end point.
    @param eLable the given edge lable.
    @param index the given index into the ode label array. */

    public void setEdge(int source, int target, String eLable,
                                                           int index) {
        edges[index] = new GraphEdge(source,target,eLable);
        }             

    /* ---------------------------------------------- */
    /*                                                */
    /*                  GET METHODS                   */
    /*                                                */
    /* ---------------------------------------------- */

    /** Gets the value of the graph ref field; the reference into the 
    inputGraphML array that narks the start of the individual block of GraphML 
    for this graph.
    @retutn the given reference (an index into the input array). */

    public int getGraphRef() {
        return(graphRef);
        }

    /** Gets the value of the node ref field.
    @retutn the given reference (an index into the input array). */

    public int getNodeRef() {
        return(nodeRef);
        }

    /** Gets the value of the edge (link) ref field.
    @return the given reference (an index into the input array). */

    public int getEdgeRef() {
        return(edgeRef);
        }

    /** Gets the value of the number of nodes field.
    @retutn the number of nodes in this graph. */

    public int getNumNodes() {
        return(numNodes);
        }

    /** Gets the value of the number of edges field.
    @retutn the number of edges in this graph. */

    public int getNumEdges() {
        return(numEdges);
        }

    /** Gets node label for node "n".
    @param n the node index into the node labels array. *
    @return the node label.      */

    public String getNodeLabelN(int n) {
        return(nodeLabels[n]);
        }

    /** Gets edge label for edge "n".
    @param n the edge index into the edge labels array. *
    @return the edge label.      */

    public String getEdgeLabelN(int n) {
        return(edges[n].getEdgeLabel());
        }
    
    /** Gets start node label for edge "n".
    @param n the edge index into the edge labels array. *
    @return the edge label.      */

    public String getEdgeStartLabelN(int n) {
        int index = edges[n].getFromNodeID();
//System.out.println("FromNodeID = " + index);
        return(nodeLabels[index]);
        }           
    
    /** Gets end node label for edge "n".
    @param n the edge index into the edge labels array. *
    @return the edge label.      */

    public String getEdgeEndLabelN(int n) {
        int index = edges[n].getToNodeID();
        return(nodeLabels[index]);
        }
    
    /** Gets the from node ID for edge "n".
    @param n the edge index into the edge labels array.
    @return the from node ID.      */

    public int getFromNodeID(int n) {
        int index = edges[n].getFromNodeID();
        return(index);
        }

    /** Gets end node ID.
    @param n the edge index into the edge labels array.
    @return the end node ID.      */

    public int getToNodeID(int n) {
        int index = edges[n].getToNodeID();
        return(index);
        }
        
    /* ------------------------------------------------- */
    /*                                                   */
    /*                        OUTPUT                     */
    /*                                                   */
    /* ------------------------------------------------- */

    /** To string method.
    @returm the constructed output string. */

    public String toString() {
        // Make string
        String s = "Graph ref. = " + graphRef + "\n" +
                   "Node ref.  = " + nodeRef + "\n" +
                   "Num. nodes = " + numNodes + "\n" +
                   "Edge ref.  = " + edgeRef + "\n" +
                   "Num. edges = " + numEdges + "\n";

        // Add nodes to string
        if (nodeLabels!=null) {
            for (int i=0;i<nodeLabels.length;i++) {
                if (i>0) s = s + "," + nodeLabels[i];
                else     s = s + "[" + nodeLabels[i];
                }
            s = s + "]\n";
            }

        // Add edges to string
        if (edges!=null) {
            for (int i=0;i<edges.length;i++) s = s + "\t" + edges[i] + "\n";
            }

        // Return
        return(s);
        }
    }

