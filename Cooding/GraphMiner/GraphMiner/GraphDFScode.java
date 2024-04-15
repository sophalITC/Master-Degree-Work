/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                        D F S   C O D E   G R A P H                         */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                          Friday 24 October 2008                            */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */


/** Data structure to hold an input graph represented in DFS code format. Input
is initially in the form of GrpahML, this is read in and stored in a pseudo
GraphML format (using the classes Graph and GraphEdge) and then converted
into Depth First Search (DFS) code format (which uses the DFScode and 
DFScodeLabel classes).
@author Frans Coenen
@version 24 October 2008          */

public class GraphDFScode {
    /** Instance of class GraphMiner (the calling object). */
    private GraphMiner newGMpan = null;
    /** Object representing binary tree containing node (vertex) data. Used to
    store label, support and index into node array data. */
    private VertexTree newVertexTree = null;
    /** Object representing binary tree containing edge data. Used to store
    label, support and index into node array data. */
    private EdgeTree newEdgeTree = null;
    /** Start node ID number array. */
    private int[] startNodeID = null;
    /** End node ID bumber array. */
    private int[] endNodeID   = null;
    /** Input data recast as ordered list of DFS codes. */
    private DFScodeLabel[] inputDFSdata = null;
    /** Support threshold in terms of minimum number of graphs in which
    a sub-graph has to appear for it to be considered frquent (1 by
    default). */
    private int minSupport = 0;

    /** Temparary global variable. */
    private int temp;

    /* ---------------------------------------------- */
    /*                                                */
    /*                  CONSTRUCTOR                   */
    /*                                                */
    /* ---------------------------------------------- */

    public GraphDFScode(int sup, GraphMiner newGM, VertexTree vTree,
                                                            EdgeTree eTree) {
        minSupport    = sup;
        newGMpan      = newGM;
        newVertexTree = vTree;
        newEdgeTree   = eTree;
        }

    /* ------------------------------------------------------- */
    /*                                                         */
    /*                  CONSTRUCTION METHODS                   */
    /*                                                         */
    /* ------------------------------------------------------- */

    /** Convets the input GraphML formay to DFS code format.
    @pram graph the given GraphML set.
    @return the number of supported DFS codes.      */

    public int convertInput2dfsCodes(Graph graph) {
        // Get number of edges in input set.
        int numEdges = graph.getNumEdges();

        // Count number of supported DFS codes.
        int numDFScodes = countInputDFScodes(numEdges,graph);

        // Dimension arrays
        inputDFSdata = new DFScodeLabel[numDFScodes];
        startNodeID  = new int[numDFScodes];
        endNodeID    = new int[numDFScodes];

        // Populate
        populateInputDFSdata(numEdges,graph);
//outputDFSCodeGraph();
        // Sort
        orderInputDFScodes();

        // Return
        return(numDFScodes);
        }

    /** Counts number of supported DFS codes in the given input graphML.
    @param numEdges the number of edges in input set.
    @param graph the given GraphML set.
    @return the number of supported DFS codes. */

    private int countInputDFScodes(int numEdges, Graph graph) {
        int numDFScodes = 0;

        // Loop
        for (int index=0;index<numEdges;index++) {
            // Start node
            String startNode = graph.getEdgeStartLabelN(index);
            int support      = newVertexTree.getNodeSupport(startNode);
            if (support>=minSupport) {
                // Edge
                String edge = graph.getEdgeLabelN(index);
                support     = newEdgeTree.getEdgeSupport(edge);
                if (support>=minSupport) {
                    // End node
                    String endNode = graph.getEdgeEndLabelN(index);
                    support        = newVertexTree.getNodeSupport(endNode);
                    if (support>=minSupport) numDFScodes++;
                    }
                }
            }

        // End
        return(numDFScodes);
        }

    /** Get the supported DFS codes in the given input graphML and populates
    the new data array.
    @param numEdges the number of edges in input set.
    @param graph the given GraphML set. */

    private void populateInputDFSdata(int numEdges, Graph graph) {
        int count = 0;

        // Loop
        for (int index=0;index<numEdges;index++) {
            // START NODE: Get start node literal, get ref to start node in
            // node binary tree, get support, check support
            String startNode     = graph.getEdgeStartLabelN(index);
            VertexTreeNode startRef = newVertexTree.getNodeReference(startNode);
            int support          = startRef.getSupport();
            if (support>=minSupport) {
                // END: Get edge literal, get ref to edge in edge binaray tree,
                // get support, check support
                String edge       = graph.getEdgeLabelN(index);
                EdgeTreeNode edgeRef = newEdgeTree.getEdgeReference(edge);
                support           = edgeRef.getSupport();
                if (support>=minSupport) {
                    // END NODE: Get end node literal, get ref to end node in
                    // node binary tree, get support, check support
                    String endNode     = graph.getEdgeEndLabelN(index);
                    VertexTreeNode endRef =
                                      newVertexTree.getNodeReference(endNode);
                    support            = endRef.getSupport();
                    if (support>=minSupport) {
                        startNodeID[count]  = graph.getFromNodeID(index);
                        endNodeID[count]    = graph.getToNodeID(index);
                        int startIndex      = startRef.getIndex();
                        int edgeIndex       = edgeRef.getIndex();
                        int endIndex        = endRef.getIndex();
//System.out.println("startNodeID[count] = " + startNodeID[count] +
//", endNodeID[count] = " + endNodeID[count]);
                        inputDFSdata[count] =
                                          newGMpan.getDFScodeLabel(startIndex,
                                                          edgeIndex,endIndex);
                        count++;
                        }
                    }
                }
            }
        }

    /** Orders (sorts)  list of input DFS codes for given graph.
    @param graph the given graph  */

    private void orderInputDFScodes() {
        boolean isSorted = false;

        // Loop
        while (!isSorted) {
            isSorted = true;
            for (int index=0;index<inputDFSdata.length-1;index++) {
                if (!inputDFSdata[index].isBefore(inputDFSdata[index+1])) {
                    isSorted = false;
                    int temp1             = startNodeID[index];
                    int temp2             = endNodeID[index];
                    DFScodeLabel temp     = inputDFSdata[index];
                    startNodeID[index]    = startNodeID[index+1];
                    endNodeID[index]      = endNodeID[index+1];
                    inputDFSdata[index]   = inputDFSdata[index+1];
                    startNodeID[index+1]  = temp1;
                    endNodeID[index+1]    = temp2;
                    inputDFSdata[index+1] = temp;
                    }
                }
            }
        }

    /** Prunes unsupported one edge sub-graphs from graph DFS code
    @param the new numbee of DFS codes in the graph. */

    public int pruneInputData() {
        int counter=0;

        // Loop and identify codes to be removed
        for (int index=0;index<inputDFSdata.length;index++) {
            if (!newGMpan.findCodeInTree(inputDFSdata[index]))
                                                    inputDFSdata[index]=null;
            else counter++;
            }

        // Recast array
        DFScodeLabel[] tempDFSdata = new DFScodeLabel[counter];
        int[] tempStartNodeID      = new int[counter];
        int[] tempEndNodeID        = new int[counter];
        int newIndex=0;
        for (int index=0;index<inputDFSdata.length;index++) {
            if (inputDFSdata[index]!=null) {
                tempDFSdata[newIndex]     = inputDFSdata[index];
                tempStartNodeID[newIndex] = startNodeID[index];
                tempEndNodeID[newIndex]   = endNodeID[index];
                newIndex++;
                }
            }

        // End
        inputDFSdata = tempDFSdata;
        startNodeID  = tempStartNodeID;
        endNodeID    = tempEndNodeID;
        return(counter);
        }

    /* ------------------------------------------------- */
    /*                                                   */
    /*                  SEARCH METHODS                   */
    /*                                                   */
    /* ------------------------------------------------- */

    /** Top level method that commences search for given set of DFS codes
    (typically representing a candidate sub-graph) in the input graph set.
    @param searchList the given DFS code list.
    @return true if found, false otherwise. */

    public boolean codeSetInGraph(DFScode[] searchList) {
//System.out.print("codeSetInGraph: searchList = ");
//outputDFSCodeListLabels(searchList);
//System.out.println();
        // Attempt to find first code in search list in this graph
        int sIndex = codeInGraph(searchList[0].getDFScodeLabel());
//System.out.println("First code index = " + sIndex);
        // If not found return false
        if (sIndex<0) {
//System.out.println("No found return false");
            return(false);
            }
        // If found and no more codes in serach list return true
        if (searchList.length==1) {
//System.out.println("No more codes true");
            return(true);
            }
        // Else process next code in search list and if eventually successful
        // return true. But, first generate start and end node ID number lists
        // for structure checking (no point in structure checking for first
        // code in search list). List 1 is the structure list for the search
        // code list, list 2 for the graph list.
        int[] list1 = new int[2];
        list1[0]    = searchList[0].getStartNode();
        list1[1]    = searchList[0].getEndNode();
        int[] list2 = new int[2];
        list2[0]    = startNodeID[sIndex];
        list2[1]    = endNodeID[sIndex];
//System.out.print("Continue: list1 = ");
//outputArray(list1);
//System.out.print(", list2 = ");
//outputArray(list2);
//System.out.println();
        // We also need to know which codes in the this graph have already
        // been matched up (we should not match a code in the this graph with
        // more that one code in the search list.
        int[] listOfDoneCodes = new int[1];
        listOfDoneCodes[0]    = sIndex;
        if (codeSetInGraph(searchList,1,listOfDoneCodes,list1,list2))
                                                                 return(true);

        // Else try and find another match for first code in this graph and
        // repeate. Temp variable set in codeInGraph/1 method. (temp varoiable
        // set in codeInGraph/1 method.
        int cEnd  = searchList[0].getEndNodeLabel();
//System.out.println("Look for more matches (temp = " +
//temp + ", searchIndex = 0 )");
        for (int index=sIndex+1;index<temp;index++) {
            sIndex= findEnd(cEnd,index,temp);
//System.out.println("index = " + index + ", sIndex = " + sIndex);
            // If not found return false
            if (sIndex<0) return(false);
            else {
                // If found and no more codes in serach list return true
                if (searchList.length==1) return(true);
                // Else process next code and if successfull return true.
                // First generate start and end node ID number lists.
                list2[0] = startNodeID[index];
                list2[1] = endNodeID[index];
                listOfDoneCodes[0] = sIndex;
                if (codeSetInGraph(searchList,1,listOfDoneCodes,list1,list2))
                                                                return(true);
                }
            }

        // End
//System.out.println("Return false");
        return(false);
        }

    /** Check rest of given set of DFS search codes against graph.
    @param searchList the given set of DFS search codes.
    @param searchIndex the current index in the given set of DFS codes.
    @param listOfDoneCodes codes that have already been checked in the graph.
    @param list1 the structure list for the search code as processed sofar.
    @param list2 the structure list for the graph nodes matched sofar.
    @return true if all found, false otherwise.  */

    private boolean codeSetInGraph(DFScode[] searchList, int searchIndex,
                        int[] listOfDoneCodes, int[] list1, int[] list2) {
//System.out.print("---------------------------\n" +
//"codeSetInGraph*: searchList = ");
//outputDFSCodeListLabels(searchList);
//System.out.print(", searchIndex = " + searchIndex + "\n\tlistOfDoneCodes = ");
//outputArray(listOfDoneCodes);
//System.out.print(", list1 = ");
//outputArray(list1);
//System.out.print(", list2 = ");
//outputArray(list2);
//System.out.println();
        // Define new structuire lists.
        int[] newList1 = null;
        int[] newList2 = null;

        // Start by attemting to finding current DFS code in search list
        // (indicatedby teh serach index) in his graph.
        int sIndex = codeInGraph(searchList[searchIndex].getDFScodeLabel());
//System.out.println("sIndex (for search index " + searchIndex + ") = " + sIndex);
        // If not found return false.
        if (sIndex<0) return(false);
        // If found check if not used before to match to another row in thw
        // search list (each row in this graph can only be matched once).
        if (!isInList(listOfDoneCodes,sIndex)) {
//System.out.println("Not in done list");
            // check structure (first create new structure lists), newList1 is
            // for the search codes, newList2 for the graphCodes.
            newList1 = appendToList(list1,
                                       searchList[searchIndex].getStartNode(),
                                        searchList[searchIndex].getEndNode());
            newList2 = appendToList(list2,startNodeID[sIndex],
                                                           endNodeID[sIndex]);
//System.out.print("newList1 = ");
//outputArray(newList1);
//System.out.print(", newList2 = ");
//outputArray(newList2);
//System.out.println();
            if (checkStructure(newList1,newList2)) {
//System.out.println("Structure test = true");
                // Structure test true. If no more search codes return true
                if (searchList.length==searchIndex+1) return(true);
                // Otherwise process rest of codes in search list.
                else {
                    // Add current this graph row index to list of done codes
                    int[] newListOfDoneCodes =
                                    appendToList(listOfDoneCodes,sIndex);
                    if (codeSetInGraph(searchList,searchIndex+1,
                                                  newListOfDoneCodes,newList1,
                                                      newList2)) return(true);
                    }
                }
//else System.out.println("Structure test = false");
            }
//else System.out.println("Index " + sIndex + " tested before");

        // Else try and find another match for first code and repeate. Temp
        // variable set in codeInGraph/1 method.
//System.out.println("Look for more matches (temp = " +
//temp + ", searchIndex = " + searchIndex + ")");
        int cEnd  = searchList[searchIndex].getEndNodeLabel();
        for (int index=sIndex+1;index<temp;index++) {
            // Assign new value to sIndex
            sIndex = findEnd(cEnd,index,temp);
//System.out.println("index = " + index + ", sIndex = " + sIndex);
            // If not found return false
            if (sIndex<0) return(false);
            // Test if found before
            if (!isInList(listOfDoneCodes,sIndex)) {
                // Check Structure, first update structure lists
                newList1 = appendToList(list1,
                                       searchList[searchIndex].getStartNode(),
                                        searchList[searchIndex].getEndNode());
                newList2 = appendToList(list2,startNodeID[sIndex],
                                                           endNodeID[sIndex]);
                if (checkStructure(newList1,newList2)) {
                    // If no more search codes return true
                    if (searchList.length==searchIndex+1) return(true);
                    // Else process next code and if successfull return true.
                    // First revise list of done codes.
                    int[] newListOfDoneCodes =
                                    appendToList(listOfDoneCodes,sIndex);
                    if (codeSetInGraph(searchList,searchIndex+1,
                                                  newListOfDoneCodes,newList1,
                                                      newList2)) return(true);
                    }
//else System.out.println("Structure test = false");
                }
//else System.out.println("Index " + sIndex + " tested before");
             }
//System.out.println("Return false");
        // Got to end and found no solution
        return(false);
        }

    /** Finds a particular DFS code label in the graph list.
    @param code the given code.
    @return index of code if code found in graph, -1 otherwise. */

    public int codeInGraph(DFScodeLabel code) {
//System.out.print("codeInGraph: code = " +
//code.toStringLabel(newVertexTree,newEdgeTree));
//System.out.println();
        // Find block of DFS codes label list that contain start node for the
        // search code
        int cStart  = code.getStartNodeLabel();
        int sIndex1 = findStartIndexForStartNode(cStart);
        if (sIndex1<0) return(-1); // Not found
        int sIndex2 = findEndIndexForStartNode(cStart,sIndex1+1);

        // Find block of DFS codes in graph that contain edge for the
        // search code
        int cEdge   = code.getEdgeLabel();
        int gIndex1 = findStartIndexForEdge(cEdge,sIndex1,sIndex2);
        if (gIndex1<0) return(-1); // Not found
        int gIndex2 = findEndIndexForEdge(cEdge,gIndex1+1,sIndex2);
        // Temporarily store value.
        temp        = gIndex2;

        // Now find end node in block.
        int cEnd  = code.getEndNodeLabel();
        return(findEnd(cEnd,gIndex1,gIndex2));
        }

    /** Finds start index of block for start node.
    @param cStart the start node label for the setach code.
    @return the index in the graph DFS code set or -1 if not found. */

    private int findStartIndexForStartNode(int cStart) {
        // Loop
        for (int index=0;index<inputDFSdata.length;index++) {
            int gStart = inputDFSdata[index].getStartNodeLabel();
            if (gStart>cStart) return(-1);
            if (gStart==cStart) return(index);
            }

        // End
        return(-1);
        }

    /** Finds the end index of block, in the set of DFS codes representing the
    given graph, for start node .
    @param cStart the start node label for the search code.
    @param sIndex the start index for the block.
    @return the end index for the start node block of DFS cods. */

    private int findEndIndexForStartNode(int cStart, int sIndex) {
        // Loop
        for (int index=sIndex;index<inputDFSdata.length;index++) {
            int gStart = inputDFSdata[index].getStartNodeLabel();
            if (gStart!=cStart) return(index);
            }

        // End
        return(inputDFSdata.length);
        }

    /** Finds start index of block for edge with start node block.
    @param cEdge the edge label for the search code.
    @param sIndex the start index for the block containing the start node.
    @param eIndex the end index for the block containing the start node.
    @return the index in the graph DFS code set or -1 if not found. */

    private int findStartIndexForEdge(int cEdge, int sIndex1, int sIndex2) {
        // Loop
        for (int index=sIndex1;index<sIndex2;index++) {
            int gEdge = inputDFSdata[index].getEdgeLabel();
            if (gEdge>cEdge) return(-1);
            if (gEdge==cEdge) return(index);
            }

        // End
        return(-1);
        }

    /** Finds the end index of block for start node.
    @param cStart the edge label for the search code.
    @param sIndex the start index for the start block containing the edge.
    @param eIndex the end index for the block containing the start node.
    @return the index in the graph DFS code set or -1 if not found. */

    private int findEndIndexForEdge(int cEdge, int sIndex, int eIndex) {
        // Loop
        for (int index=sIndex;index<eIndex;index++) {
            int gEdge = inputDFSdata[index].getEdgeLabel();
            if (gEdge!=cEdge) return(index);
            }

        // End
        return(eIndex);
        }

    /** Searches for match between end node in search code and given block in
    graph DFS codes.
    @param cEnd the end node label for the search code.
    @param sIndex the start index for the start block containing the start node
    and edge pair.
    @param eIndex the end index for the block containing the start node
    and edge pair.
    @returm the index of the code or -1 if not found. */

    private int findEnd(int cEnd, int sIndex, int eIndex) {
        // Loop
        for (int index=sIndex;index<eIndex;index++) {
            int gEnd = inputDFSdata[index].getEndNodeLabel();
            if (gEnd==cEnd) return(index);
            if (gEnd>cEnd) return(-1);
            }

        // Default
        return(-1);
        }

    /* ---------------------------------------------- */
    /*                                                */
    /*                  GET METHODS                   */
    /*                                                */
    /* ---------------------------------------------- */

    /** Gets the number of DFS code representing the graph.
    @return the number of codes. */

    public int getNumDFScodes() {
        return(inputDFSdata.length);
        }

    /* --------------------------------------------------------- */
    /*                                                           */
    /*                        CHECK STRUCTURE                    */
    /*                                                           */
    /* --------------------------------------------------------- */

    /** Checks the structure of the seach code and comparitor structure lists. 
    If both lists must have the same format, the locations for the number N in 
    one list must be the same as for the number M in the other list. 
    WARNING: Identical piece of code Gspan class (bad practice I know!).
    @param list1 the given forst list.
    @param list2 the gicen second list.
    @return true if the two lists have the same structure and false
    otherwise. */

    private boolean checkStructure(int[] list1, int[] list2) {
//System.out.print("\tcheckStructure: list1 = ");
//outputArray(list1);
//System.out.print(", list2 = ");
//outputArray(list2);
//System.out.println();
        // Get highest number in list
        int max = 1 + getMaxNumInList(list1);

        // process numbers
        for (int num1=0;num1<max;num1++) {
//System.out.println("checkStructure: num1 = " + num1);
            boolean firstOccurance = true;
            int num2 = -1;
            // Find locations in list
            for (int index=0;index<list1.length;index++) {
                if (num1==list1[index]) {
//System.out.println("checkStructure: found; list1[index] = " + list1[index]);
                    // If first occurance get number in list2
                    if (firstOccurance) {
                        num2           = list2[index];
//System.out.println("checkStructure: first occ; num2 = " + num2);
                        firstOccurance = false;
                        }
                    else {
//System.out.println("checkStructure: Test; num2 = " + num2 +
//", list2[index] = " + list2[index]);
                        if (num2!=list2[index]) return(false);
                        }
                    }
                }
            }

        // End
        return(true);
        }

    /* Updates the structure list by adding the last two elements.
    private int[] updateStructureList(int[] list; int start, int end) {
        // Demension new list
        int[] newList = new int[list.length+2];

        // Populate
        int marker=0;
        for (;marker<list.length;marker++) newList[marker] = list1[marker];

        // Add new elements
        newList[marker] = start;
        newList[marker] = end;

        // Return
        return();
        }     */

    /* ------------------------------------------------- */
    /*                                                   */
    /*                        OUTPUT                     */
    /*                                                   */
    /* ------------------------------------------------- */

    /** Output DFS code encoded graph. */

    public void outputDFSCodeGraph() {
        for (int i=0;i<startNodeID.length;i++) {
            if (i>0) {
                System.out.print(",");
                if ((double)i%4.0==0) System.out.print("\n\t");
                }
            System.out.print("[" + startNodeID[i] + "," + endNodeID[i] +
                                             "," + inputDFSdata[i] + "]");
            }

        // Return
        System.out.println();
        }

    /** Output DFS code encoded graph substituting labels for node and edge ID
    numbers.    */

    public void outputDFSCodeGraphLabels() {
        for (int i=0;i<startNodeID.length;i++) {
            if (i>0) {
                System.out.print(",");
                if ((double)i%2.0==0) System.out.print("\n\t");
                }
            System.out.print("[" + startNodeID[i] + "," + endNodeID[i] + "," +
                 newVertexTree.getLabel(inputDFSdata[i].getStartNodeLabel()) +
                  "," + newEdgeTree.getLabel(inputDFSdata[i].getEdgeLabel()) +
             "," + newVertexTree.getLabel(inputDFSdata[i].getEndNodeLabel()) +
                                                                         "]");
            }

        // Return
        System.out.println();
        }        

    /* ---------------------------------------------------- */
    /*                                                      */
    /*                        UTILITIES                     */
    /*                                                      */
    /* ---------------------------------------------------- */
    
    /** Check is given nu,ber is in the given array.
    @param list the given array.
    @param n the given number.
    @return true if in list, false otherwise. */
    
    private boolean isInList(int[] list, int n) {
        for (int index=0;index<list.length;index++) {
            if (list[index]==n) return(true);
            }

        // End
        return(false);
        }

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
        
    /** Outputs list of DFS codes. */
    
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
        
    /** Outputs list of DFS codes using string labels. */
    
    private void outputDFSCodeListLabels(DFScode[] list) {
        if (list==null) System.out.print("null");
        else {
            // Loop
            for (int index=0;index<list.length;index++) {
                if (index>0) System.out.print("," + list[index].
                                   toStringLabels(newVertexTree,newEdgeTree));
                else System.out.print(list[index].
                                   toStringLabels(newVertexTree,newEdgeTree));
                }
            }
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

    /** Appends given number to given array.
    @param list the given array
    @param num the given number
    @return the array with the given number appended to it. */

    private int[] appendToList(int[] list, int num) {
        int[] newList = new int[list.length+1];

        // Loop
        int i = 0;
        for (;i<list.length;i++) newList[i]=list[i];

        // End
        newList[i]=num;
        return(newList);
        }

    /** Appends given numbers to given array.
    @param list the given array
    @param num1 the first given number
    @param num2 the second given mumber
    @return the array with the given numbers appended to it. */

    private int[] appendToList(int[] list, int num1, int num2) {
        int[] newList = new int[list.length+2];

        // Loop
        int i = 0;
        for (;i<list.length;i++) newList[i]=list[i];

        // End
        newList[i]   = num1;
        newList[i+1] = num2;
        return(newList);
        }
    }








