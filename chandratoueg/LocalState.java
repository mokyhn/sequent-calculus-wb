/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chandratoueg;

/**
 *
 * @author mku
 */
public class LocalState {
    // Chandra and Toueg variables
    int     p;           // Id of this agent
    int     estimate_p;  // p's current estimate
    String  state_p;     // State variable
    int     r_p;         // Current round number
    int     ts_p;        // The last round estimate was updated
    int     c_p;         // coordinator id
    int     decide;      // Output: final decision value

    Clock   localClock;

    
    public LocalState(int p) {
        this.p          = p;
        this.estimate_p = p;
        this.state_p    = "undecided"; //
        this.r_p        = 0;           // We start in round 0
        this.ts_p       = 0;           // The estimate was last updated in round 0
        this.localClock = new Clock();
    }
    
}
