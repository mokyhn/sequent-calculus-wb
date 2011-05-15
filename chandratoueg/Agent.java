/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chandratoueg;

import java.util.ArrayList;

/**
 *
 * @author mku
 */
public class Agent extends Thread {
  Network net;
  Failure failure;
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
     failure.globalClock.tick();
     localClock.tick();
  }

  public Agent(int i, Network net, Failure f, int N) {
      this.p           = i;
      this.net         = net;
      this.failure     = f;
      this.localClock  = new Clock();
      this.N           = N;

      // Initialization of algorithm variables
      estimate_p      = p;      // The suggested value is the id of the process
      state_p         = "undecided"; //
      r_p             = 0;           // We start in round 0
      ts_p            = 0;           // The estimate was last updated in round 0
  }
 
   private class ChandraToueg extends Thread {
  
     public void Phase1() {
       net.snd(new Message(p, c_p, "phase1", new Payload(r_p,  estimate_p, ts_p)));
     }      
 
     public void Phase2() {
         boolean gotMessages = false;
         ArrayList<Message> msgs = null;
         Message            m;
         int t           = Integer.MIN_VALUE;
         int i;
         
         if (p == c_p) {
             while (!gotMessages && failure.amIalive(p)) {
               msgs = net.rcv(p, "phase1");
               if (msgs.size() >= (N+1)/2) gotMessages = true;
             }
             
             // Find best estimate
             for (i=0; i < msgs.size(); i++) {
                  m = msgs.get(i);
                  if (m.payload.ts > t) {
                      estimate_p = m.payload.estimate;
                      t          = m.payload.ts;
                  }
             }
             
             //TODO: Code that removes messages from the net here.
             
             // Send it to all
             for (i=0; i < N; i++) {
               net.snd(new Message(p, i, "phase2", new Payload(r_p, estimate_p, -1)));
             }
         }
     }
     
     public void Phase3() {  
         boolean gotAMessage = false;
         ArrayList<Message> msgs = null;
         Message m;

         while (!gotAMessage && !failure.fd_DS(p, c_p)) {
             msgs = net.rcv(p, "phase2");
             for (int i=0; i < msgs.size(); i++) {
               m = msgs.get(i);
               if (m.source == c_p) {
                 gotAMessage = true;
                 estimate_p  = m.payload.estimate;
                 ts_p        = r_p;
                 net.snd(new Message(p, c_p, "ack", null));
                 break;
               }               
             }
         }
         
         if (!gotAMessage) net.snd(new Message(p, c_p, "nack", null));
     }
     
     public void Phase4() {
     }
     
     public void pr(String text) {
      System.out.println("Agent " + p + " says " + text);
     }
     
     public void chandraToueg() {
       

        while (state_p.equals("undecided") && failure.amIalive(p)) {
         r_p = r_p + 1;
         c_p = (r_p % N);

         pr("Phase1 begin");
         Phase1();
         pr("Phase1 end");
         
         pr("Phase2 begin");
         Phase2();
         pr("Phase2 end");
    
         pr("Phase3 begin");
         Phase2();
         pr("Phase3 end");
      }
     }
   
   @Override
    public void run() { chandraToueg();  }
  
  
  }

  private class RBlistener extends Thread {
     public void listen() {
           
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
