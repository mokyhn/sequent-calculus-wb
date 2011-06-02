package chandratoueg;

/**
 *
 * @author mku
 */
public class GlobalState {
   int     N;           // Total number of agents
   Clock   clock;
   Network net;
   Failure failure;
   Log     log;
 
   /**
    * 
    * @param N Total number of agents
    * @param waTime Weak Accuracy time - time after which the unreliable failure detector fd_DS obtains weak accuracy.
    * @param scTime Strong Completeness time - time after which the unreliable failure detector fd_DS obtains the strong completeness property.
    */
   public GlobalState(int N, long waTime, long scTime) {
    this.N       = N;
    this.log     = new Log(true);
    this.clock   = new Clock();
    this.failure = new Failure(N, clock, waTime, scTime, log);
    this.net     = new Network(N, failure, log);
   }
}
