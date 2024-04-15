/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                            V E R E X   T R E E                             */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                          Friday 24 October 2008                            */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/** Binary Tree structure in which to store vertex (node) information. 
Generated from input data. Tree alows fast look up to access values such as 
vertex support (the number of occaision that an vertex/npde apears in the 
transaction graph set, one count per graph).
@author Frans Coenen
@version 24 October 2008          */

public class VertexTree {
    /** Start reference to a binary tree containing node (vertex) data. Used to
    store label, support and index into node array data. */
    private VertexTreeNode nodeStart = null;
    /** Ordered (according to frequency) array of vertices. */
    public VertexTreeNode[] nodeArray = null;

    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/

    /** Default constructor. */

    public VertexTree() {
        }

    /* ------------------------------------------------------------------ */
    /*                                                                    */
    /*                        TREE GENERATION METHODS                     */
    /*                                                                    */
    /* ------------------------------------------------------------------ */

    /** Creates a vertex tree.
    @param graphData the graph inpur in GraphML format. */

    public void createVertexTree(Graph[] graphData) {
        countVertexSupport(graphData);
        }

    /** Counts vertex support, each graph is processed and vertex and data 
    stored in a binary tree ordered according to node label; (i) if a node
    is already recorded the count is incremented by 1, otherwise (ii) node or 
    edge is added to the appropriate binary tree and the count set to 1.
    @param graphData the graph inpur in GraphML format. */

    private void countVertexSupport(Graph[] graphData) {
        // Process graph data set
        for (int i=0;i<graphData.length;i++) {
            // Set node "has been counted" fields to N0.
            setHasBeenCountedFlags();
            // Process nodes in individual graph
            int end = graphData[i].getNumNodes();
            for (int j=0;j<end;j++) {
                // Get node data and add to node tree
                String label = graphData[i].getNodeLabelN(j);
                updateNodeTree(label);
                }
            }
        }

    /** Sets has been counted flags to "no". */

    private void setHasBeenCountedFlags() {
        setHasBeenCountedFlags(nodeStart);
        }

    /** Continues process of setting has been counted flags to "no" in
    vertex tree .
    @param linkref the current location in vertex tree. */

    private void setHasBeenCountedFlags(VertexTreeNode linkRef) {
        // Update current node if it exists
        if (linkRef!=null) {
            linkRef.setHasBeenCountedFlagToFalse();
            // Process left branch (if it exists)
            if (linkRef.leftBranch!=null)
                                 setHasBeenCountedFlags(linkRef.leftBranch);
            // Process left branch  (if it exists)
            if (linkRef.rightBranch!=null)
                                setHasBeenCountedFlags(linkRef.rightBranch);
            }
        }

    /** Updates the node binary tree with the given label; if tree empty
    add as route, otherwise process tree.
    @param label the given label. */

    private void updateNodeTree(String label) {
        if (nodeStart==null) nodeStart = new VertexTreeNode(label);
        else updateNodeTree(nodeStart,label);
        }

    /** Continues process of updating the node binary tree with the given label.
    @param linkref the current location in the tree.
    @param label the given label. */

    private void updateNodeTree(VertexTreeNode linkRef, String label) {
        // Get comparitor
        int comparitor = label.compareTo(linkRef.getLabel());

        // If same increment support
        if (comparitor==0) linkRef.incSupport();
        // else process left branch
        else if (comparitor<0) {
            // No left branch so create new left branch
            if (linkRef.leftBranch==null)
                                    linkRef.leftBranch=new VertexTreeNode(label);
            // Proceed down left branch
            else updateNodeTree(linkRef.leftBranch,label);
            }
        // else process right branch
        else {
            // No right branch so create new right branch
            if (linkRef.rightBranch==null)
                                    linkRef.rightBranch=new VertexTreeNode(label);
            // Proceed down right branch
            else updateNodeTree(linkRef.rightBranch,label);
            }
        }

    /* ------------------------------------------------------------------- */
    /*                                                                     */
    /*                        ARRAY GENERATION METHODS                     */
    /*                                                                     */
    /* ------------------------------------------------------------------- */

    /** Creates an array of vertices ordered according to frequency. The index
    number for each mode is its identifier.
    @param minSupport the minimum support threshold in terms of the number of
    graphs.   */

    public void genOrderedVertexArray(int minSupport) {
        // Get number of vertices and dimension node array
        int numNodes = getNumberOfNodes();
        nodeArray = new VertexTreeNode[numNodes];

        // Copy content of node (vertex) tree into the array, eaxh cell
        // in array points to a vertex tree node.
        int startIndex=0;
        copyNodeTree2array(startIndex,nodeStart);

        // Sort
        sortNodeArray();

        // Prune array
        int count = countSupportedNodes(minSupport);  
        System.out.println("Number of frequent vertices = " + count);
        if (count>0) {
            VertexTreeNode[] temp = new VertexTreeNode[count];
            for (int index=0;index<temp.length;index++)
                                             temp[index] = nodeArray[index];
            nodeArray = temp;
            }
        else nodeArray = null;
        }
    
    /** Copies node binary tree nodes to node array.
    @param index the current index in the node array.
    @param linkRef the current location in the node tree.
    @return the updated index. */

    private int copyNodeTree2array(int index, VertexTreeNode linkRef) {
        nodeArray[index] = linkRef;
        // Left branch
        if (linkRef.leftBranch!=null) index = copyNodeTree2array(index+1,
                                                          linkRef.leftBranch);
        // Right branch
        if (linkRef.rightBranch!=null) index = copyNodeTree2array(index+1,
                                                         linkRef.rightBranch);
        // End
        return(index);
        }

    /** Sorts node array so that node with highest support is first, etc.  */

    private void sortNodeArray() {
        boolean isSorted = false;

        // Loop
        while (!isSorted) {
            isSorted = true;
            for (int index=0;index<nodeArray.length-1;index++) {
                if (nodeArray[index].getSupport() <
                                           nodeArray[index+1].getSupport()) {
                    isSorted = false;
                    VertexTreeNode temp   = nodeArray[index];
                    nodeArray[index]   = nodeArray[index+1];
                    nodeArray[index+1] = temp;
                    }
                }
            }
        }

    /** Counts Supported nodes.  
    @param minSupport the minimum support threshold in terms of the number of
    graphs.
    @return the number of supported nodes.    */

    private int countSupportedNodes(int minSupport) {
        int count =0;
        for (int index=0;index<nodeArray.length;index++) {
            if (nodeArray[index].getSupport()>=minSupport) count++;
            }

        // End
        return(count);
        }

    /** Adds index numbers to vertex binary tree. The index number is the index
    in the vetex array (ordered according to frequency). Knowing this index
    facilitates fast look up in the DFS code tree although it does require
    a tree walk of the binary tree. */

    public void addIndexNumsToVertexTree() {
        for (int index=0;index<nodeArray.length;index++) {
            String label = nodeArray[index].getLabel();
            addIndexNumsToVertexTree(label,index);
            }
        }

    /* ------------------------------------------------------ */
    /*                                                        */
    /*                        SET METHODS                     */
    /*                                                        */
    /* ------------------------------------------------------ */

     /** Commences process of adding an index number, into the vertex array, to
    the vertex binary tree given a particular edge label (allows index to be
    easilly obtained).
    @param vertexLabel the label for the given vertex.
    @param index the index to be included. */

    public void addIndexNumsToVertexTree(String vertexLabel, int index) {
        addIndexNumsToVertexTree(vertexLabel,nodeStart,index);
        }

    /** Continues process of adding an index number to vertex binary tree.
    @param linkRef the current location in the vertex tree.
    @param index the index to be included. */

    private void addIndexNumsToVertexTree(String vertexLabel, 
                                              VertexTreeNode linkRef, int index) {
        // Get comparitor
        int comptr = vertexLabel.compareTo(linkRef.getLabel());

        // If same set index value
        if (comptr==0) linkRef.setIndex(index);
        // else process left branch
        else if (comptr<0) addIndexNumsToVertexTree(vertexLabel,
                                                    linkRef.leftBranch,index);
        // else process right branch
        else addIndexNumsToVertexTree(vertexLabel,linkRef.rightBranch,index);
        }

    /* ------------------------------------------------------ */
    /*                                                        */
    /*                        GET METHODS                     */
    /*                                                        */
    /* ------------------------------------------------------ */

    /** Counts number of nodes in node tree. (Used to dimension noe attay.)
    @return the number of nodes.       */

    public int getNumberOfNodes() {
        int countSoFar=0;
        // Process
        if (nodeStart!=null) countSoFar =
                                     getNumberOfNodes(countSoFar+1,nodeStart);
        // Return
        return(countSoFar);
        }

    /** Continues process of counting number of nodes in node node binary tree.
    @param countSoFar the count so far
    @param linkRef the current location in the tree. */

    private int getNumberOfNodes(int countSoFar, VertexTreeNode linkRef) {
        // Left branch
        if (linkRef.leftBranch!=null) countSoFar =
                            getNumberOfNodes(countSoFar+1,linkRef.leftBranch);
        // Right branch
        if (linkRef.rightBranch!=null) countSoFar =
                            getNumberOfNodes(countSoFar+1,linkRef.rightBranch);

        // Return
        return(countSoFar);
        }

    /** Gets the support value for a particular vertex by walking the
    vertex binary tree.
    @param label the label for the given node.
    @return rge associated support value. */

    public int getNodeSupport(String label) {
        return(getNodeSupport(nodeStart,label));
        }

    /** Continues process of getting the support value for a particular vertex
    by walking the vertex binary tree.
    @param linkRef the current location in the edge tree.
    @param label the label for the given node.
    @return rge associated support value.  */

    private int getNodeSupport(VertexTreeNode linkRef, String label) {
        int support;

        // Get comparitor
//System.out.println("label = " + label + ", linkRef.getLabel() = " +
//linkRef.getLabel());
        int comptr = label.compareTo(linkRef.getLabel());

        // If get support
        if (comptr==0) support = linkRef.getSupport();
        // else process left branch
        else if (comptr<0) support = getNodeSupport(linkRef.leftBranch,label);
        // else process right branch
        else support = getNodeSupport(linkRef.rightBranch,label);

        // End
        return(support);
        }

    /** Gets the reference for a particular vertex by walking the vertex binary
    tree.
    @param label the label for the given node.
    @return the associated reference. */

    public VertexTreeNode getNodeReference(String label) {
        return(getNodeReference(nodeStart,label));
        }

    /** Continues process of getting the support value for a particular vertex
    by walking the vertex binary tree.
    @param linkRef the current location in the vertex tree.
    @param label the label for the given node.
    @return rthe associated reference value.  */

    private VertexTreeNode getNodeReference(VertexTreeNode linkRef, String label) {
        // Get comparitor
        int comptr = label.compareTo(linkRef.getLabel());

        // If ound return reference
        if (comptr==0) return(linkRef);
        // else process left branch
        else if (comptr<0) return(getNodeReference(linkRef.leftBranch,label));
        // else process right branch
        else return(getNodeReference(linkRef.rightBranch,label));
        }

    /** Gets number of supported vertices. */
    
    public int getNumFrequentNodes() {
        return(nodeArray.length);
        }
    
    /** Get the vertex label associated with the given vertex ID number.
    @param index the ID number of the vertex
    @return the label. */

    public String getLabel(int index) {
        return(nodeArray[index].getLabel());
        }

    /* ------------------------------------------------- */
    /*                                                   */
    /*                        OUTPUT                     */
    /*                                                   */
    /* ------------------------------------------------- */

    /** Starts process of outputting node binary tree. */

    public void outputNodeTree() {
        System.out.println("VERTEX TREE");
        outputNodeTree("1",nodeStart);
        System.out.println();
        }

    /** Continues process of outputting node binary tree.
    @param num the node identifier.
    @param linkRef the current location in the tree. */

    private void outputNodeTree(String num, VertexTreeNode linkRef) {
        System.out.println("[" + num + "] " + linkRef);

        // Left branch
        if (linkRef.leftBranch!=null) outputNodeTree(num+".1",
                                                          linkRef.leftBranch);

        // Right branch
        if (linkRef.rightBranch!=null) outputNodeTree(num+".2",
                                                         linkRef.rightBranch);
        }
    /** Outputs node array */

    public void outputNodeArray() {
        System.out.println("NODE ARRAY");
        for (int index=0;index<nodeArray.length;index++) {
            System.out.println("[" + index + "] " + nodeArray[index]);
            }
        System.out.println();
        }
    
    /** Returns the vertex label associatec with a particular node index (ID
    number).
    @param index the given index.
    @return the output string. */

    public String toString(int index) {
        return(nodeArray[index].getLabel());
        }
    }

