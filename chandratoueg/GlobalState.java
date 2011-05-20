/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chandratoueg;

/**
 *
 * @author mku
 */
public class GlobalState {
   int     N;           // Total number of agents
   Network net;
   Failure failure;
 
   public GlobalState(int N) {
    this.N       = N;
    this.net     = new Network(N);
    this.failure = new Failure(N, net);
   }
}
