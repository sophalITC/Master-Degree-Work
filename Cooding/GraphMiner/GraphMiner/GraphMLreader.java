/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                               G   S P A N                                  */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                           Wednesday 6 February 2008                        */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/** Graph ML reader, loads a GraphML represented graph set into primary 
storage.
@author Frans Coenen
@version 6 February 2008          */
         

// Java packages
import java.io.*;
import java.util.*;

// Java GUI packages
import javax.swing.*;

public class GraphMLreader extends JFrame {

    /*------------------------------------------------------------------------*/
    /*                                                                        */
    /*                                   FIELDS                               */
    /*                                                                        */
    /*------------------------------------------------------------------------*/

    /** Array to hold input data from data file.  */
    private String[] codeFile = null;
    /** Command line argument for data file name. */
    private String fileName = null;
    /** Error flag used when checking command line arguments (default =
    <TT>true</TT>). */
    private boolean noErrorFlag = true;
    /** The input stream, instance of class <TT>BufferedReader</TT>. */
    private BufferedReader fileInput;
    /** Array to hold graphs. */
    protected Graph[] graphData = null;
    /** Total number of nodes in graph set. */
    protected int totalNumNodes = 0;
    /** Total number of edges in graph set. */
    protected int totalNumEdges = 0;


    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/

    /** Constructor with command line arguments to be process.
    @param args the command line arguments (array of String instances). */

    public GraphMLreader(String[] args) {
	// Process command line arguments
	for(int index=0;index<args.length;index++) idArgument(args[index]);

	// If command line arguments read successfully (errorFlag set to
        // "true") check validity of arguments
	if (noErrorFlag) CheckInputArguments();
	else outputMenu();
	}

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                           S T A R T                              */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /** Starts the GraphML raeder process.                              */

    public void startGraphMLreader() {
        int size = getNumberOfLines();
        codeFile = new String[size];
        readInputFile(); 
//outputCodeFile();

        // Process
        processFile();
//outputGraphData();      

        // Output
        outputStatistics();
        }

    /** Processes the input file. */

    private void processFile() {
        // Count "graphml" tags and dimension graphs array.
        int size  = countGraphMLtags();
        graphData = new Graph[size];
        for (int i=0;i<graphData.length;i++) graphData[i] = new Graph();
        // Get graph references.
        recordGraphRefs();
        // Get node data.
        recordNodeRefs();
        countNumNodes();
        getNodeDetails();
        // Get edge data.
        recordEdgeRefs();
        countNumEdges();
        getEdgeDetails();
        }

    /* ----------------------------------------------------------------- */
    /*                                                                   */
    /*                        FILE PROCESSING METHODS                    */
    /*                                                                   */
    /* ----------------------------------------------------------------- */

    /** Counts the number of "graphml" tags.
    @return number of tags.            */

    private int countGraphMLtags() {
        int counter=0;

        // Loop
        for(int i=0;i<codeFile.length;i++) {
            String s = codeFile[i].toLowerCase();
            if (s.indexOf("<graphml")>=0) counter++;
            }

        // End
        return(counter);
        }

    /** Records the indexes into the codeFile array where each graph starts. */

    private void recordGraphRefs() {
        int counter=0;

        // Loop
        for(int i=0;i<codeFile.length;i++) {
            String s = codeFile[i].toLowerCase();
            if (s.indexOf("<graphml")>=0) {
                graphData[counter].setGraphRef(i);
                counter++;
                }
            }
        }

    /** Records the indexes into the codeFile array where each node block
    starts. */

    private void recordNodeRefs() {
        for (int i=0;i<graphData.length;i++) {
            for (int j=graphData[i].getGraphRef();j<codeFile.length;j++) {
                String s = codeFile[j].toLowerCase();
                if (s.indexOf("<node")>=0) {
                    graphData[i].setNodeRef(j);
                    break;
                    }
                }
            }
        }

    /** Gets number of nodes. */

    private void countNumNodes() {
        for (int i=0;i<graphData.length;i++) {
            int counter=1;
            for (int j=graphData[i].getNodeRef()+1;j<codeFile.length;j++) {
                String s = codeFile[j].toLowerCase();
                if (s.indexOf("<node")>=0) counter++;
                else break;
                }
            graphData[i].setNumNodes(counter);
            // Increment total
            totalNumNodes = totalNumNodes + counter;
            }
        }

    /** Gets node details. */

    private void getNodeDetails() {   
        for (int i=0;i<graphData.length;i++) {
            int j   = graphData[i].getNodeRef();
            int end = j + graphData[i].getNumNodes();
            for (int counter=0;j<end;j++,counter++) {
                String s   = codeFile[j].toLowerCase();
                int index1 = s.indexOf("<data key");
                index1     = s.indexOf(">",index1)+1;
                int index2 = s.indexOf("</data>",index1);
                String lab = s.substring(index1,index2);
//System.out.println("'" + lab + "'");
                graphData[i].setNodeLabel(lab,counter);
                }
            }
        }

    /** Records the indexes into the codeFile array where each edge block
    starts. */

    private void recordEdgeRefs() {
        for (int i=0;i<graphData.length;i++) {
            for (int j=graphData[i].getGraphRef();j<codeFile.length;j++) {
                String s = codeFile[j].toLowerCase();
                if (s.indexOf("<edge")>=0) {
                    graphData[i].setEdgeRef(j);
                    break;
                    }
                }
            }
        }    

    /** Gets number of edges. */

    private void countNumEdges() {
        for (int i=0;i<graphData.length;i++) {
            int counter=1;
            for (int j=graphData[i].getEdgeRef()+1;j<codeFile.length;j++) {
                String s = codeFile[j].toLowerCase();
                if (s.indexOf("<edge")>=0) counter++;
                else break;
                }
            graphData[i].setNumEdges(counter);     
            // Increment total
            totalNumEdges = totalNumEdges + counter;
            }
        }          

    /** Gets node details. */

    private void getEdgeDetails() {
        for (int i=0;i<graphData.length;i++) {
            int j   = graphData[i].getEdgeRef();
            int end = j + graphData[i].getNumEdges();
//System.out.println("j = " + j + ", end = " + end);
            for (int counter=0;j<end;j++,counter++) {
                String s   = codeFile[j].toLowerCase();
                // Find source
                int index1 = s.indexOf("source");
                index1     = s.indexOf("\"",index1)+1;
                int index2 = s.indexOf("\"",index1);
                String lab = s.substring(index1,index2);
                int source = Integer.parseInt(lab);
                // Find target
                index1     = s.indexOf("target",index2);
                index1     = s.indexOf("\"",index1)+1;
                index2     = s.indexOf("\"",index1);
                lab        = s.substring(index1,index2);
                int target = Integer.parseInt(lab);
                // Find label
                index1     = s.indexOf("<data key",index2);
                index1     = s.indexOf(">",index1)+1;
                index2     = s.indexOf("</data>",index1);
                lab        = s.substring(index1,index2);
                // Set
//System.out.println("source = " + source + ", target = " + target +
//", lab = " + lab + ", counter = " + counter);
                graphData[i].setEdge(source,target,lab,counter);
                }
            }
        }

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                        COMMAND LINE ARGUMENTS                    */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /* IDENTIFY ARGUMENT */
    /** Identifies nature of individual command line agruments:
    -F = file name. */

    protected void idArgument(String argument) {
        noErrorFlag = true;

        // Check arguments
	if (argument.length()<3) {
	    JOptionPane.showMessageDialog(null,"Command line argument \"" +
	             argument + "\" too short.","COMMAND LINE INPUT ERROR",
			                        JOptionPane.ERROR_MESSAGE);
            noErrorFlag = false;
            }
        else if (argument.charAt(0) == '-') {
	    char flag = argument.charAt(1);
	    argument = argument.substring(2,argument.length());
	    switch (flag) {
	        case 'F':  // Data input file name
	    	    fileName = argument;
		    break;
	        default:
	            JOptionPane.showMessageDialog(null,"Unrecognise command " +
		    	       "line  argument: \"" + flag + argument + "\"'.",
			 "COMMAND LINE INPUT ERROR",JOptionPane.ERROR_MESSAGE);
		    noErrorFlag = false;
	        }
            }
        else {
	    JOptionPane.showMessageDialog(null,"All command line arguments " +
    				     "must commence with a '-' character ('" +
			  	   argument + "')","COMMAND LINE INPUT ERROR",
                                                   JOptionPane.ERROR_MESSAGE);
            noErrorFlag = false;
            }
	}

    /* CHECK INPUT ARGUMENTS */
    /** Invokes methods to check values associate with command line
    arguments */

    protected void CheckInputArguments() {
	// Check file name
	checkFileName();

	// Return
	if (noErrorFlag) outputSettings();
	else outputMenu();
	}

    /* CHECK FILE NAME */
    /** Checks if data file name provided, if not <TT>errorFlag</TT> set
    to <TT>false</TT>. */

    protected void checkFileName() {
	if (fileName == null) {
	    JOptionPane.showMessageDialog(null,"Must specify file name (-F)",
	               "COMMAND LINE INPUT ERROR",JOptionPane.ERROR_MESSAGE);
            noErrorFlag = false;
	    }
	}

    /* ------------------------------------------------------- */
    /*                                                         */
    /*                  FILE HANDLING UTILITIES                */
    /*                                                         */
    /* ------------------------------------------------------- */

    /** Continues process of getting the number of lines in the input file.
    @return the number of lines. */

    private int getNumberOfLines() {
        // Open the file
	openInputFile(fileName);

        // Loop through file
        int counter=0;
        try {
	    String line;
	    while (true) {
                line = fileInput.readLine();
	        if (line==null) break;
	        else counter++;
	        }
            }
        catch(NullPointerException e) {
	    closeInputFile();
            }
	catch(IOException e) {
	    closeInputFile();
	    }

        // Return
        return(counter);
        }

    /** Reads input file. */

    private void readInputFile() {  
        // Open the file
	openInputFile(fileName);int counter=0;

        // Loop through file
        try {
	    for (int i=0;i<codeFile.length;i++) 
                                          codeFile[i]=fileInput.readLine();
            }
        catch(NullPointerException e) {
	    closeInputFile();
            }
	catch(IOException e) {
	    closeInputFile();
	    }
        }

    /** Opens input file using fileName (instance field).
    @param nameOfFile the filename of the file to be opened. */

    private void openInputFile(String nameOfFile) {
	try {
	    // Open file
	    FileReader file = new FileReader(nameOfFile);
	    fileInput = new BufferedReader(file);
	    }
	catch(IOException ioException) {
	    JOptionPane.showMessageDialog(null,"Error Opening File \"" +
	    		               nameOfFile + "\"","FILE INPUT ERROR",
			                         JOptionPane.ERROR_MESSAGE);
	    System.exit(1);
	    }
	}

    /** Closes input file. */

    private void closeInputFile() {
        if (fileInput != null) {
	    try {
	    	fileInput.close();
		}
	    catch (IOException ioException) {
	        JOptionPane.showMessageDialog(this,"Error Opening File",
			             "Error 4: ",JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}               

    /* ---------------------------------------------- */
    /*                                                */
    /*                  OUTPUT METHODS                */
    /*                                                */
    /* ---------------------------------------------- */

    /** Outputs graph data. */

    protected void outputGraphData() {
        System.out.println("GRAPH DATA");

        // Loop
        for (int i=0;i<graphData.length;i++) {
            if (graphData[i]!=null) System.out.println(graphData[i]);
            else System.out.println("null");
            }
        }

    /** Outputs graph statistics. */
    
    protected void outputStatistics() {
            System.out.println("INPUT STATISTICS");
            int size = graphData.length;
            System.out.println("Number of graphs  = " + size);
            System.out.println("Total Num. nodes  = " + totalNumNodes);
            System.out.println("Total Num. edges  = " + totalNumEdges);
            double average = (double) totalNumNodes / (double) size;
            System.out.println("Ave. Num. nodes   = " + twoDecPlaces(average));
            average = (double) totalNumEdges / (double) size;
            System.out.println("Ave. Num. edges   = " + twoDecPlaces(average));
            System.out.println("-------------------------");
            }

    /** Outputs code file to screen. */

    private void outputCodeFile() {
        for (int i=0;i<codeFile.length;i++) System.out.println(codeFile[i]);
        }

    /** Outputs menu for command line arguments. */

    protected void outputMenu() {
        System.out.println();
	System.out.println("-F  = File name");
	System.out.println();

	// Exit
	System.exit(1);
	}

    /** Outputs command line values provided by user. */

    protected void outputSettings() {
        System.out.println("SETTINGS\n--------");
	System.out.println("File name                = " + fileName);
	System.out.println();
        }                     

    /* -------------------------------- */
    /*                                  */
    /*        OUTPUT UTILITIES          */
    /*                                  */
    /* -------------------------------- */

    /* TWO DECIMAL PLACES */

    /** Converts given real number to real number rounded up to two decimal
    places.
    @param number the given number.
    @return the number to two decimal places. */

    protected double twoDecPlaces(double number) {
    	int numInt = (int) ((number+0.005)*100.0);
	number = ((double) numInt)/100.0;
	return(number);
	}
    }

