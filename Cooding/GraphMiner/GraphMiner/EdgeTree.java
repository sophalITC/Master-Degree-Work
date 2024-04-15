/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                             E D G E   T R E E                              */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                          Friday 24 October 2008                            */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/** Binary Tree structure in which to store edge information. Generated from 
input data. Tree alows fast look up to access values such as edge support (the 
number of occaision that an edge apears in the transaction graph set, one count 
per graph).
@author Frans Coenen
@version 24 October 2008          */

public class EdgeTree {
    /** Start reference to a binary tree containing edge data. Used to
    store label, support and index into edge array data. */
    private EdgeTreeNode edgeStart = null;           
    /** Ordered (according to frequency) array of edges. */
    public EdgeTreeNode[] edgeArray = null;

    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/

    /** Default constructor. */

    public EdgeTree() {
        }

    /* ------------------------------------------------------------------ */
    /*                                                                    */
    /*                        TREE GENERATION METHODS                     */
    /*                                                                    */
    /* ------------------------------------------------------------------ */

    /** Creates a edge tree.
    @param graphData the graph inpur in GraphML format. */

    public void createEdgeTree(Graph[] graphData) {
        countEdgeSupport(graphData);
        }

    /** Counts edge support, each graph is processed and the edge data stored
    in a binary tree ordered according to edge label; (i) if an edge is already
    recorded count is incremented by 1, otherwise (ii) edge is added to the
    appropriate binary tree and the count set to 1.
    @param graphData the graph input set in GraphML format. */

    private void countEdgeSupport(Graph[] graphData) {
        // Process graph data set graph by graph
        for (int i=0;i<graphData.length;i++) {
            // Set node "has been counted" fields to N0.
            setHasBeenCountedFlags();
            // Process edges in individual graph
            int end = graphData[i].getNumEdges();
//System.out.println("end = " + end);
            for (int j=0;j<end;j++) {
                // Get edge label if any and if found update edfge tree
                String label = graphData[i].getEdgeLabelN(j);
//System.out.println("label = " + label);
                updateEdgeTree(label);
                }
            }
        }

    /** Sets has been counted flags to "false". */

    private void setHasBeenCountedFlags() {
        setHasBeenCountedFlags(edgeStart);
        }

    /** Continues process of setting has been counted flags to "false" in
    vertex tree .
    @param linkref the current location in vertex tree. */

    private void setHasBeenCountedFlags(EdgeTreeNode linkRef) {
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

    /** Updates the edge binary tree with the given label; if tree empty
    add as route, otherwise process tree.
    @param label the given label. */

    private void updateEdgeTree(String label) {
        if (edgeStart==null) edgeStart = new EdgeTreeNode(label);
        else updateEdgeTree(edgeStart,label);
        }

    /** Continues process of updating the edge binary tree with the given label.
    @param linkref the current location in the tree.
    @param label the given label. */

    private void updateEdgeTree(EdgeTreeNode linkRef, String label) {
        // Get comparitor
        int comparitor = label.compareTo(linkRef.getLabel());

        // If same increment support
        if (comparitor==0) linkRef.incSupport();
        // else process left branch
        else if (comparitor<0) {
            // No left branch so create new left branch
            if (linkRef.leftBranch==null)
                                    linkRef.leftBranch=new EdgeTreeNode(label);
            // Proceed down left branch
            else updateEdgeTree(linkRef.leftBranch,label);
            }
        // else process right branch
        else {
            // No right branch so create new right branch
            if (linkRef.rightBranch==null)
                                    linkRef.rightBranch=new EdgeTreeNode(label);
            // Proceed down right branch
            else updateEdgeTree(linkRef.rightBranch,label);
            }
        }

    /* ------------------------------------------------------------------- */
    /*                                                                     */
    /*                        ARRAY GENERATION METHODS                     */
    /*                                                                     */
    /* ------------------------------------------------------------------- */
    
    /** Create an ordered array of edges. 
    @param minSupport the minimum support threshold in terms of the number of
    graphs.  */

    public void genOrderedEdgeArray(int minSupport) {
        // Get number of edges and dimension edge array
        int numEdges = getNumberOfEdges();
        edgeArray = new EdgeTreeNode[numEdges];

        // Copy content of edge tree into array
        int startIndex=0;
        copyEdgeTree2array(startIndex,edgeStart);

        // Sort edge array
        sortEdgeArray();

        // Prune array
        int count = countSupportedEdges(minSupport);
        System.out.println("Number of frequent edges = " + count);
        if (count>0) {
            EdgeTreeNode[] temp = new EdgeTreeNode[count];
            for (int index=0;index<temp.length;index++)
                                             temp[index] = edgeArray[index];
            edgeArray = temp;
            }
        else edgeArray = null;
//outputEdgeArray();
        }

    /** Copies edge binary tree nodes to edge array.
    @param index the current index in the edge array.
    @param linkRef the current location in the edge tree.
    @return the updated index. */

    private int copyEdgeTree2array(int index, EdgeTreeNode linkRef) {
        edgeArray[index] = linkRef;
        // Left branch
        if (linkRef.leftBranch!=null) index = copyEdgeTree2array(index+1,
                                                          linkRef.leftBranch);
        // Right branch
        if (linkRef.rightBranch!=null) index = copyEdgeTree2array(index+1,
                                                         linkRef.rightBranch);
        // End
        return(index);
        }

    /** Sorts edge array so that edge with highest support is first, etc.  */

    private void sortEdgeArray() {
        boolean isSorted = false;

        // Loop
        while (!isSorted) {
            isSorted = true;
            for (int index=0;index<edgeArray.length-1;index++) {
                if (edgeArray[index].getSupport() <
                                           edgeArray[index+1].getSupport()) {
                    isSorted = false;
                    EdgeTreeNode temp     = edgeArray[index];
                    edgeArray[index]   = edgeArray[index+1];
                    edgeArray[index+1] = temp;
                    }
                }
            }
        }

    /** Counts Supported edges.  
    @param minSupport the minimum support threshold in terms of the number of
    graphs.
    @return the number of supported edges.    */

    private int countSupportedEdges(int minSupport) {
        int count =0;
        for (int index=0;index<edgeArray.length;index++) {
            if (edgeArray[index].getSupport()>=minSupport) count++;
            }

        // End
        return(count);
        }

    /** Adds index numbers to edge binary tree. The index number is the index
    in the edge array (ordered according to frequency). Knowing this index
    facilitates fast look up in the DFS code tree although it does require
    a tree walk of the binary tree. */

    public void addIndexNumsToEdgeTree() {
        for (int index=0;index<edgeArray.length;index++) {
            String label = edgeArray[index].getLabel();
            addIndexNumsToEdgeTree(label,index);
            }
        }

    /* ------------------------------------------------------ */
    /*                                                        */
    /*                        SET METHODS                     */
    /*                                                        */
    /* ------------------------------------------------------ */
    
    /** Commences process of adding an index number, into the edge array, to the
    edge binary tree given a particular edge label (allows index to be easilly 
    obtained).
    @param edgeLabel the label for the given edge.
    @param index the index to be included. */

    public void addIndexNumsToEdgeTree(String edgeLabel, int index) {
        addIndexNumsToEdgeTree(edgeLabel,edgeStart,index);
        }

    /** Continues process of adding index number to edge binary tree given
    a particular edge label.  
    @param edgeLabel the label gor the given
    @param linkRef the current location in the edge tree.
    @param index the index to be included. */

    private void addIndexNumsToEdgeTree(String edgeLabel, EdgeTreeNode linkRef, 
                                                                 int index) {
        // Get comparitor
        int comptr = edgeLabel.compareTo(linkRef.getLabel());

        // If same set index value
        if (comptr==0) linkRef.setIndex(index);
        // else process left branch
        else if (comptr<0) addIndexNumsToEdgeTree(edgeLabel,linkRef.leftBranch,
                                                                        index);
        // else process right branch
        else addIndexNumsToEdgeTree(edgeLabel,linkRef.rightBranch,index);
        }

    /* ------------------------------------------------------ */
    /*                                                        */
    /*                        GET METHODS                     */
    /*                                                        */
    /* ------------------------------------------------------ */

    /** Counts number of nodes in edge tree.
    @return the number of nodes.       */

    private int getNumberOfEdges() {
        int countSoFar=0;
        // Process
        if (edgeStart!=null) countSoFar =
                                     getNumberOfEdges(countSoFar+1,edgeStart);
        // Return
        return(countSoFar);
        }

    /** Continues process of counting number of nodes in edge node binary tree.
    @param countSoFar the count so far
    @param linkRef the current location in the tree. */

    private int getNumberOfEdges(int countSoFar, EdgeTreeNode linkRef) {
        // Left branch
        if (linkRef.leftBranch!=null) countSoFar =
                             getNumberOfEdges(countSoFar+1,linkRef.leftBranch);
        // Right branch
        if (linkRef.rightBranch!=null) countSoFar =
                            getNumberOfEdges(countSoFar+1,linkRef.rightBranch);

        // Return
        return(countSoFar);
        }         

    /** Gets the support value for a particular vertex by walking the
    vertex binary tree.
    @param label the label for the given edge.
    @return rge associated support value. */

    public int getEdgeSupport(String label) {
        return(getEdgeSupport(edgeStart,label));
        }

    /** Continues process of getting the support value for a particular vertex
    by walking the vertex binary tree.
    @param linkRef the current location in the edge tree.
    @param label the label for the given edge.
    @return rge associated support value.  */

    private int getEdgeSupport(EdgeTreeNode linkRef, String label) {
        int support;

        // Get comparitor
        int comptr = label.compareTo(linkRef.getLabel());

        // If get support
        if (comptr==0) support = linkRef.getSupport();
        // else process left branch
        else if (comptr<0) support = getEdgeSupport(linkRef.leftBranch,label);
        // else process right branch
        else support = getEdgeSupport(linkRef.rightBranch,label);

        // End
        return(support);
        }
                          
    /** Gets the reference for a particular edge by walking the edge binary
    tree.
    @param label the label for the given edge.
    @return the associated reference. */

    public EdgeTreeNode getEdgeReference(String label) {
        return(getEdgeReference(edgeStart,label));
        }

    /** Continues process of getting the support value for a particular vertex
    by walking the vertex binary tree.
    @param linkRef the current location in the edge tree.
    @param label the label for the given edge.
    @return the associated reference value.  */

    private EdgeTreeNode getEdgeReference(EdgeTreeNode linkRef, String label) {
        // Get comparitor
        int comptr = label.compareTo(linkRef.getLabel());

        // If ound return reference
        if (comptr==0) return(linkRef);
        // else process left branch
        else if (comptr<0) return(getEdgeReference(linkRef.leftBranch,label));
        // else process right branch
        else return(getEdgeReference(linkRef.rightBranch,label));
        }

    /** Gets number of supported verices. */
    
    public int getNumFrequentEdges() {
        return(edgeArray.length);
        }        
    
    /** Get the edge label associated with the given edge ID number.
    @param index the ID number of the edge
    @return the label. */

    public String getLabel(int index) {
        return(edgeArray[index].getLabel());
        }

    /* ------------------------------------------------- */
    /*                                                   */
    /*                        OUTPUT                     */
    /*                                                   */
    /* ------------------------------------------------- */

    /** Starts process of outputting edge binary tree. */

    public void outputEdgeTree() {
        System.out.println("EDGE TREE");
        outputEdgeTree("1",edgeStart);
        System.out.println();
        }

    /** Continues process of outputting edge binary tree.
    @param num the edge identifier.
    @param linkRef the current location in the tree. */

    private void outputEdgeTree(String num, EdgeTreeNode linkRef) {
        System.out.println("[" + num + "] " + linkRef);

        // Left branch
        if (linkRef.leftBranch!=null) outputEdgeTree(num+".1",
                                                          linkRef.leftBranch);

        // Right branch
        if (linkRef.rightBranch!=null) outputEdgeTree(num+".2",
                                                         linkRef.rightBranch);
        }
    
    /** Outputs edge array */

    public void outputEdgeArray() {
        System.out.println("EDGE ARRAY");
        for (int index=0;index<edgeArray.length;index++) {
            System.out.println("[" + index + "] " + edgeArray[index]);
            }
        System.out.println();
        }     
    
    /** Returns the edge label associatec with a particular node index (ID
    number).
    @param index the given index.
    @return the output string. */

    public String toString(int index) {
        return(edgeArray[index].getLabel());
        }
    }

