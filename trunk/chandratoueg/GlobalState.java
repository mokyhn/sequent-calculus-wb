package chandratoueg;

/**
 *
 * @author mku
 */
public class GlobalState {
   int     N;           // Total number of agents
   Network net;
   Failure failure;
   Log     log;
 
   public GlobalState(int N) {
    this.N       = N;
    this.log     = new Log(true);

    this.failure = new Failure(N, log);
    this.net     = new Network(N, failure, log);
   }
}
