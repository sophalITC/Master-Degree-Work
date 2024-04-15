/* -------------------------------------------------------------------------- */
/*                                                                            */
/*                          G - S P A N   E D G E                             */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                          Wednesday 1 October 2008                          */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */


/** Binary tree node to hold graph edge details.
@author Frans Coenen
@version 2 October 2008          */

public class EdgeTreeNode {
    /** Identifier label for edge. */
    private String label = null;
    /** Index number into array of edge ordered according to frequency. This is
    also the numeric identifier for the edge. An index of -1 indicates that no 
    index has been assigned, this is the default value on start up but later
    indicates that the edge is not supported.         */
    private int index = -1;
    /** Support count (i.e. number of times edge occues in the input
    dara set). */
    private int support = 1;   
    /** Flag set to one if the support count has been oncremneted with
    respect to a particular input graph. This is done because we only 
    want one support incremnet per class. Set to true bt default. */
    private boolean hasBeenCountedFlag = true;
    /** Left branch. */
    public EdgeTreeNode leftBranch = null;
    /** Right branch. */
    public EdgeTreeNode rightBranch = null;

    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/

    /** One argument constructor.
    @param lab the label for the edge. */

    public EdgeTreeNode(String lab) {
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

    /** Get the edge label.
    @return the label. */

    public String getLabel() {
        return(label);
        }

    /** Get the edge support.
    @return the support value. */

    public int getSupport() {
        return(support);
        }

    /** Get the index into the ordered edge array value .
    @return the index value. */

    public int getIndex() {
        return(index);
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

