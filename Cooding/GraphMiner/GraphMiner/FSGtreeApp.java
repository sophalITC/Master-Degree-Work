/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                L U C S - K D D  F S G   A L G O R I T H M                  */
/*                                                                            */
/*                              Frans Coenen                                  */
/*                                                                            */
/*                         Friday 8 February 2008                             */
/*                                                                            */
/*                      Department of Computer Science                        */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/** Graph mining application using Frequrnt Sub-Graph (FSG) tree extension 
approach, as opposed to Right Most Extension (RME) approach.

Compile using: javac GspanApp.java

Run using java GraphMLreaderApp                                    */


class FSGtreeApp {

    /** main method to commence processing.
    @param args the com and line argument list. */

    public static void main(String args[]) {
        // Call constructor and process command line arguments
        GraphMiner newGminer = new GraphMiner(args);

        // Start graph FSG graph mining algorithm
	double time1 = (double) System.currentTimeMillis();
        newGminer.startGraphMiner();
	newGminer.outputDuration(time1,(double) System.currentTimeMillis());
        
        // Output on completion
		newGminer.outputNumFSGtreeNodes();
//		System.out.println("\n\n outputGSGtreeLabes");
//        newGminer.outputFSGtreeLabels();
//        System.out.println("\n\n outputFSGtree");
//        newGminer.outputFSGtree();
		System.out.println("\n\n outputFSGTreeCSV");
		String fileName = null;
		int fileArgIndex = -1;
		for(int i=0; (fileArgIndex == -1) && (i < args.length); i++){
			fileArgIndex = args[i].indexOf("-F");
			if(fileArgIndex != -1){
				fileName = args[i].substring(fileArgIndex+2, args[i].length()-3) + "csv";
			}
		}
		newGminer.outputFSGtreeCSV(fileName);
    	}
    }
