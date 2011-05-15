/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chandratoueg;

/**
 *
 * @author mku
 */
public class Agent extends Thread {
  Network net;
  Failure failure;
  Clock   globalClock;
  Clock   localClock;

  // Chandra and Toueg variables
  int     p;        // Id of this agent
  int     N;        // Total number of agents
  int     estimate_p;  // p's current estimate
  String  state_p;     // State variable
  int     r_p;         // Current round number
  int     ts_p;        // The last round estimate was updated
  int     c_p;         // coordinator id
  
  // Update global and local time
  private void tick() {
     globalClock.tick();
     localClock.tick();
  }

  public Agent(int i, Network net, Failure f, Clock c, int N) {
      this.p           = i;
      this.net         = net;
      this.failure     = f;
      this.globalClock = c;
      this.localClock  = new Clock();
      this.N           = N;

      // Initialization of algorithm variables
      estimate_p      = p;      // The suggested value is the id of the process
      state_p         = "undecided"; //
      r_p             = 0;           // We start in round 0
      ts_p            = 0;           // The estimate was last updated in round 0
  }
 
   private class ChandraToueg extends Thread {
       int kl1;
  
     public void Phase1() {
       net.snd(new Message(p, c_p, "phase1", new Payload(r_p,  estimate_p, ts_p)));
     }      
 
     public void Phase2() {
     }
     
     public void Phase3() {
     }
     
     public void Phase4() {
     }
     
     public void chandraToueg() {
       

        while (state_p.equals("undecided")) {
         r_p = r_p + 1;
         c_p = (r_p % N);


         // Phase 2
         if (p == c_p) {
         }

      }
     }
   
   @Override
    public void run() { chandraToueg();  }
  
  
  }

  private class RBlistener extends Thread {
     public void listen() {
      int  kl2;

      for (kl2 = 0; kl2 < 10; kl2++)
          System.out.println(p + " RBListener");
     }

     @Override
     public void run() { listen();  }
  }



    @Override
  public void run() {
    ChandraToueg ct = new ChandraToueg();
    RBlistener rb   = new RBlistener();
    
    ct.start();
    rb.start();
    }

  
}
