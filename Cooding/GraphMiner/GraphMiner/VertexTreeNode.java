/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                        G - S P A N   V E R T A X                           */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                          Friday 8 February 2008                            */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */


/** Binary tree node to hold graph vertex details:
@author Frans Coenen
@version 6 February 2008          */

public class VertexTreeNode {
    /** Identifier label for vertex. */
    private String label = null;
    /** Index number into array of nodes ordered according to frequency. This
    is also the numeric identifier for the node (vertex). An index of -1 
    indicates that no index has been assigned, this is the default value on 
    start up but later is used to indicates that the node is not supported. */
    private int index = -1;
    /** Support count (i.e. number of times vertex occues in the input
    dara set). */
    private int support = 1;
    /** Flag set to one if the support count has been oncremneted with
    respect to a particular input graph. This is done because we only 
    want one support incremnet per class. Set to true bt default. */
    private boolean hasBeenCountedFlag = true;
    /** Left branch. */
    public VertexTreeNode leftBranch = null;
    /** Right branch. */
    public VertexTreeNode rightBranch = null;

    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/

    /** One argument constructor.
    @param lab the label for the node. */

    public VertexTreeNode(String lab) {
        label = lab;
        }

    /* -------------------------------------------------- */
    /*                                                    */
    /*                        METHODS                     */
    /*                                                    */
    /* -------------------------------------------------- */

    /** Increments the support count by 1. */

    public void incSupport() {
        if (!hasBeenCountedFlag) {
            support = support+1;
            hasBeenCountedFlag = true;
            }
        }

    /* ------------------------------------------------------ */
    /*                                                        */
    /*                        SET METHODS                     */
    /*                                                        */
    /* ------------------------------------------------------ */

    /** Sets the value for the index field.
    @param i the index field value. */

    public void setIndex(int i) {
        index = i;
        }

    /** Sets the has been counted flag to false. */

    public void setHasBeenCountedFlagToFalse() {
        hasBeenCountedFlag = false;
        }

    /* ------------------------------------------------------ */
    /*                                                        */
    /*                        GET METHODS                     */
    /*                                                        */
    /* ------------------------------------------------------ */

    /** Get the vertex label.
    @return the label. */

    public String getLabel() {
        return(label);
        }

    /** Get the vertex support.
    @return the support value. */

    public int getSupport() {
        return(support);
        }

    /** Get the index into the ordered vertex array value .
    @return the index value. */

    public int getIndex() {
        return(index);
        }

    /** Get the vertex index amd support.
    @return two cell array holding the index and support values. */

    public int[] getIndexAndSupport() {
        int[] result = new int[2];
        result[0] = support;
        result[1] = index;

        // Reyurn
        return(result);
        }

    /* ------------------------------------------------- */
    /*                                                   */
    /*                        OUTPUT                     */
    /*                                                   */
    /* ------------------------------------------------- */

    /** To string method.
    @return the constructed output string. */

    public String toString() {
        String s = "label = " + label + ", index = " + index +
                                     ", support = " + support;

        // End
        return(s);
        }
    }

