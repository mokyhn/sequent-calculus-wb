package chandratoueg;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author mku
 */
public class Agent extends Thread {
    public LocalState l;
    private GlobalState g;

    public Agent(int p, GlobalState g) {
        this.g = g;
        this.l = new LocalState(p);
    }
    
    // Update global and local time
    private void tick() {
        g.failure.globalClock.tick();
        l.localClock.tick();
       if (l.localClock.getTime() % 500 == 0) System.out.println("." + l.localClock.getTime());
    }
    
    
    public LocalState getLocalState() {
        return l;
    }

    public boolean go() {
        tick();
        return g.failure.amIalive(l.p) && !g.failure.amIdone(l.p);
    }

    public void Phase1() {
        pr("Phase1 begin");
        g.net.snd(new Message(l.p, l.c_p, "phase1", new Payload(l.r_p, l.estimate_p, l.ts_p)));
        pr("Phase1 end");
    }

    public void Phase2() {
        boolean gotMessages = false;
        ArrayList<Message> msgs = new ArrayList();
        Message m;
        int t = Integer.MIN_VALUE;
        int i;
        
        int test = 0;

        pr("Phase2 begin");

        if (l.p == l.c_p) {
            while (!gotMessages) {
                test++;
                if (test == 10) {
                 System.out.print("phase 2 " + l.p + ".");
                 test = 0;
                }
                msgs = g.net.rcv(l.p, "phase1");
                if (msgs.size() >= (g.N + 1) / 2) {
                    gotMessages = true;
                }
            }

            // Find best estimate
            for (i = 0; i < msgs.size(); i++) {
                m = msgs.get(i);
                if (m.payload.ts > t) {
                    l.estimate_p = m.payload.estimate;
                    t            = m.payload.ts;
                }
            }

            g.net.delete(msgs);
            

            // Send it to all
            for (i = 0; i < g.N; i++) {
                g.net.snd(new Message(l.p, i, "phase2", new Payload(l.r_p, l.estimate_p, -1)));
            }
        }

        pr("Phase2 end");

    }

    public void Phase3() {
        boolean gotAMessage = false;
        ArrayList<Message> msgs;
        Message m;
 
        int test = 0;
        
        pr("Phase3 begin");

        while (!gotAMessage && !g.failure.fd_DS(l.p, l.c_p)) {
           test++;
            if (test == 10) {
            System.out.print("phase 3 " + l.p + ".");
            test = 0;
           }
            msgs = g.net.rcv(l.p, "phase2");
            for (int i = 0; i < msgs.size(); i++) {
                m = msgs.get(i);
                if (m.source == l.c_p) {
                    gotAMessage = true;
                    l.estimate_p = m.payload.estimate;
                    l.ts_p = l.r_p;
                    g.net.snd(new Message(l.p, l.c_p, "ack", null));
                    g.net.delete(m);
                    break;
                }
            }

        }

        if (!gotAMessage) {
            g.net.snd(new Message(l.p, l.c_p, "nack", null));
        }

        pr("Phase3 end");

    }

    public void Phase4() {
        int test = 0;
        
        pr("Phase4 begin");
        if (l.p == l.c_p ) {

            // Wait for replies   
            while (g.net.rcv(l.p, "ack").size() + g.net.rcv(l.p, "nack").size() < (g.N + 1) / 2 ) { // Busy wait
             test++;
             if (test == 10) {
                 System.out.print("phase4 " + l.p + ".");
                 test = 0;
             }
             
            }

            if (g.net.rcv(l.p, "ack").size() >= (g.N + 1) / 2 ) {
                R_broadcast(l.p, l.r_p, l.estimate_p);
            }
        }
        pr("Phase4 end");
        pr(g.failure.toString());

    }

    public void R_broadcast(int p, int r, int estimate) {
        for (int i = 0; i < g.N; i++) {
            g.net.snd(new Message(p, i, "decide", new Payload(r, estimate, -2)));
        }
    }

    public void pr(String text) {
        System.out.println("Agent " + l.p + " says " + text);
    }

    public void chandraToueg() {
        while (l.state_p.equals("undecided") && go()) {
            l.r_p = l.r_p + 1;
            l.c_p = (l.r_p % g.N);

            tick(); // Time passes

            System.out.println("Agent " + l.p + " enters round " + l.r_p);

            if (go()) {
                Phase1();
            }
            if (go()) {
                Phase2();
            }
            if (go()) {
                Phase2();
            }
            if (go()) {
                Phase4();
            }
        }
    }

    @Override
    public void run() {
        chandraToueg();
    }
}
