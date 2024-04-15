/* -------------------------------------------------------------------------- */
/*                                                                            */
/*              L U C S - K D D  g S p a n   A L G O R I T H M                */
/*                                                                            */
/*                              Frans Coenen                                  */
/*                                                                            */
/*                        Sunday 14 December 2008                             */
/*                       Modified: 26 January 2009                            */
/*                                                                            */
/*                      Department of Computer Science                        */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/** LUCS-KDD version of Yan and Han's gSpan algorithm using the Right Most 
Extension (RME) strategy to grow candidate sub-graphs.

Compile using: javac GspanApp.java

Run using java GraphMLreaderApp                                    */


class GspanApp {

    /** main method to commence processing.
    @param args the com and line argument list. */

    public static void main(String args[]) {
        // Call constructor and process command line arguments
        GspanRME newGspan = new GspanRME(args);

        // Start gSpan graph mining algorith using RME
	double time1 = (double) System.currentTimeMillis();
        newGspan.startGraphMiner();
	newGspan.outputDuration(time1,(double) System.currentTimeMillis());

        // Output on completion
        //newGspan.outputFSGtreeLabels();
        newGspan.outputNumFSGtreeNodes();
        }
    }
