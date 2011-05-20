package chandratoueg;

import java.util.ArrayList;

/**
 *
 * @author mku
 */
public class Agent extends Thread {
   public  LocalState  l;
   private GlobalState g;
  
  // Update global and local time
  private void tick() {
     g.failure.globalClock.tick();
     l.localClock.tick();
  }

  public Agent(int p, GlobalState g) {
    this.g = g;
    this.l = new LocalState(p);
  }
 
   private class ChandraToueg extends Thread {
     public boolean go() {
      tick();
      return g.failure.amIalive(l.p);
     }
     
       
     public void Phase1() {
       pr("Phase1 begin");
       if (go()) g.net.snd(new Message(l.p, l.c_p, "phase1", new Payload(l.r_p,  l.estimate_p, l.ts_p)));
       pr("Phase1 end");      
     }      
 
     public void Phase2() {
         boolean gotMessages = false;
         ArrayList<Message> msgs = new ArrayList();
         Message            m;
         int t           = Integer.MIN_VALUE;
         int i;
         
         pr("Phase2 begin");

         
         if (l.p == l.c_p && go()) {
             while (!gotMessages && go()) {
               msgs = g.net.rcv(l.p, "phase1");
               if (msgs.size() >= (g.N+1)/2) gotMessages = true;
             }
             
             // Find best estimate
             for (i=0; i < msgs.size() && go(); i++) {
                  m = msgs.get(i);
                  if (m.payload.ts > t) {
                      l.estimate_p = m.payload.estimate;
                      t            = m.payload.ts;
                  }
             }
             
            if (go()) g.net.delete(msgs);
             
             // Send it to all
             for (i=0; i < g.N && go(); i++) {
               g.net.snd(new Message(l.p, i, "phase2", new Payload(l.r_p, l.estimate_p, -1)));
             }
         }

         pr("Phase2 end");

     }
     
     public void Phase3() {  
         boolean gotAMessage = false;
         ArrayList<Message> msgs;
         Message m;
 
         pr("Phase3 begin");

         while (!gotAMessage && !g.failure.fd_DS(l.p, l.c_p) && go()) {
             msgs = g.net.rcv(l.p, "phase2");
             for (int i=0; i < msgs.size() && go(); i++) {
               m = msgs.get(i);
               if (m.source == l.c_p) {
                 gotAMessage = true;
                 l.estimate_p  = m.payload.estimate;
                 l.ts_p        = l.r_p;
                 g.net.snd(new Message(l.p, l.c_p, "ack", null));
                 g.net.delete(m);
                 break;
               }               
             }
             
         }
                 
         if (!gotAMessage && go()) g.net.snd(new Message(l.p, l.c_p, "nack", null));

         pr("Phase3 end");

     }
     
     public void Phase4() {
       pr("Phase4 begin");
         if (l.p == l.c_p) {
          
          // Wait for replies   
          while (g.net.rcv(l.p, "ack").size() + g.net.rcv(l.p, "nack").size() < (g.N+1)/2 && go()) 
          { // Busy wait
          }
         
          if (g.net.rcv(l.p, "ack").size() >= (g.N+1)/2 && go()) {
           R_broadcast(l.p, l.r_p, l.estimate_p);
          }
         }
      pr("Phase4 end");          
     }
     
     public void R_broadcast(int p, int r, int estimate) {
         for (int i = 0; i < g.N && go(); i++) {
            g.net.snd(new Message(p, i, "decide", new Payload(r, estimate, -2)));
         }
     }
     
     public void pr(String text) {
      System.out.println("Agent " + l.p + " says " + text);
     }
     
     public void chandraToueg() {
       

        while (!g.failure.stopAll && l.state_p.equals("undecided") && go()) {
         l.r_p = l.r_p + 1;
         l.c_p = (l.r_p % g.N);

         tick(); // Time passes
         
         System.out.println("Agent " + l.p + " enters round " + l.r_p);
         
         if (go()) Phase1();         
         if (go()) Phase2();    
         if (go()) Phase2();         
         if (go()) Phase4();
         
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
           
           System.out.println(g.failure.stopAll + " " + l.p);

           while (!g.failure.stopAll && g.failure.amIalive(l.p)) {
              System.out.println(g.failure.stopAll + " " + l.p);
               msgs = g.net.rcv(l.p, "decide");
                 for (i = 0; i < msgs.size() && g.failure.amIalive(l.p); i++) {
                  m = msgs.get(i);
                  if (!done.contains(m)) {
                      for (j = 0; j < g.N && g.failure.amIalive(l.p); j++) {
                        g.net.snd(new Message(m.source, j, "decide", m.payload));
                      }
                      done.add(m);
                  }

                  if (l.state_p.equals("undecided")) {
                   l.state_p = "decided";
                   l.decide  = m.payload.estimate;
                   g.failure.IamDone(l.p);
                  }   
               }
             g.net.delete(msgs); // Done with these packages
           }
           
           
           
     }

     @Override
     public void run() {  R_deliver();  }
  }



    @Override
  public void run() {
    ChandraToueg ct = new ChandraToueg();
    RBlistener rb   = new RBlistener();
        rb.start();

    ct.start();

        try {
          ct.join();
        } catch (InterruptedException ex) {System.out.println("Exception");}
    }

  
}
