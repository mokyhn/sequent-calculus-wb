package chandratoueg;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
  int     decide;      // Output: final decision value
  
  
  boolean stop = false;
  
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
             
            net.delete(msgs);
             
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

         while (!gotAMessage && !failure.fd_DS(p, c_p) && failure.amIalive(p)) {
             msgs = net.rcv(p, "phase2");
             for (int i=0; i < msgs.size(); i++) {
               m = msgs.get(i);
               if (m.source == c_p) {
                 gotAMessage = true;
                 estimate_p  = m.payload.estimate;
                 ts_p        = r_p;
                 net.snd(new Message(p, c_p, "ack", null));
                 net.delete(m);
                 break;
               }               
             }
         }
         
         if (!gotAMessage) net.snd(new Message(p, c_p, "nack", null));
     }
     
     public void Phase4() {
         ArrayList<Message> msgs = null;

         if (p == c_p) {
          
          // Wait for replies   
          while (net.rcv(p, "ack").size() + net.rcv(p, "nack").size() < (N+1)/2 ) 
          { // Busy wait
          }
         
          if (net.rcv(p, "ack").size() >= (N+1)/2) {
           R_broadcast(p, r_p, estimate_p);
          }
         }
          
         
     }
     
     public void R_broadcast(int p, int r, int estimate) {
         for (int i = 0; i < N; i++) {
            net.snd(new Message(p, i, "decide", new Payload(r, estimate, -2)));
         }
     }
     
     public void pr(String text) {
      System.out.println("Agent " + p + " says " + text);
     }
     
     public void chandraToueg() {
       

        while (state_p.equals("undecided") && failure.amIalive(p)) {
         r_p = r_p + 1;
         c_p = (r_p % N);

         tick(); // Time passes
         
         pr("Phase1 begin");
         Phase1();
         pr("Phase1 end");
         
         pr("Phase2 begin");
         Phase2();
         pr("Phase2 end");
    
         pr("Phase3 begin");
         Phase2();
         pr("Phase3 end");
         
         pr("Phase4 begin");
         Phase4();
         pr("Phase4 end");
         
      }
     }
   
   @Override
    public void run() { chandraToueg();  }
  
  
  }

  private class RBlistener extends Thread {
      ArrayList<Message> done = new ArrayList();
      
      public void R_deliver() {
           ArrayList<Message> msgs;
           Message m;
           int i, j;
           
           
           while (failure.amIalive(p) && !stop) {
             msgs = net.rcv(p, "decide");
             if (msgs != null) {
                 for (i = 0; i < msgs.size(); i++) {
                  m = msgs.get(i);
                  if (!done.contains(m)) {
                      for (j = 0; j < N; j++) {
                        net.snd(new Message(m.source, j, "decide", m.payload));
                      }
                      done.add(m);
                  }

                  if (state_p.equals("undecided")) {
                   state_p = "decided";
                   decide  = m.payload.estimate;
                   failure.IamDone(p);
                  }   
               }
             }
             //net.delete(msgs); // Done with these packages
           }
           
           
           
     }

     @Override
     public void run() {  R_deliver();  }
  }



    @Override
  public void run() {
    ChandraToueg ct = new ChandraToueg();
    RBlistener rb   = new RBlistener();
    
    ct.start();
    rb.start();

        try {
          ct.join();
        } catch (InterruptedException ex) {System.out.println("Exception");}
    }

  
}
