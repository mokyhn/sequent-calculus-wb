package chandratoueg;

import java.util.ArrayList;

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
    }
    
    
    public LocalState getLocalState() {
        return l;
    }

    public boolean go() {
        tick();
        return g.failure.amIalive(l.p) && !g.failure.amIdone(l.p);
    }

    private void log(String s) {
     g.log.add(l.p + ": " + s);
    }
    
    public void Phase1() {
        Message m = new Message(l.p, l.c_p, "phase1", new Payload(l.r_p, l.estimate_p, l.ts_p));
        
        log("Phase1 " + m.toString());
        g.net.snd(m);
        
    }

    public void Phase2() {
        boolean gotMessages = false;
        ArrayList<Message> msgs = new ArrayList();
        Message m;
        int t = Integer.MIN_VALUE;
        int i;
        
        if (l.p == l.c_p) {
            while (!gotMessages) {
                msgs = g.net.rcv(l.p, "phase1");
                if (msgs.size() >= (g.N + 1) / 2) {
                    gotMessages = true;
                }
            }

            // Find best estimate
            for (i = 0; i < msgs.size(); i++) {
                m = msgs.get(i);
                if (m.msgType.equals("phase1") && m.payload.ts > t) {
                    l.estimate_p = m.payload.estimate;
                    t            = m.payload.ts;
                    log("Phase 2, updated estimate: " + m.toString());
                }
            }

            g.net.delete(msgs);
            

            // Send it to all
            for (i = 0; i < g.N; i++) {
                g.net.snd(new Message(l.p, i, "phase2", new Payload(l.r_p, l.estimate_p, -1)));
            }
        }
        

    }

    // The if in the body of the while loop is never executed?!
    public void Phase3() {
        boolean gotAMessage = false;
        ArrayList<Message> msgs;
        Message m;
        Message mSnd;
         

        while (!gotAMessage && !g.failure.fd_DS(l.p, l.c_p)) {
            msgs = g.net.rcv(l.p, "phase2");
            for (int i = 0; i < msgs.size(); i++) {
                m = msgs.get(i);
                if (m.source == l.c_p) {
                    gotAMessage = true;
                    l.estimate_p = m.payload.estimate;
                    l.ts_p = l.r_p;
                    mSnd = new Message(l.p, l.c_p, "ack", null);
                    log("Phase3: " + mSnd.toString());
                    g.net.snd(mSnd);
                    g.net.delete(m);
                    break;
                }
            }

        }

        if (!gotAMessage) {
            mSnd = new Message(l.p, l.c_p, "nack", null);
            log("Phase3: " + mSnd.toString());
            g.net.snd(mSnd);
        }


    }

    public void Phase4() {        
        if (l.p == l.c_p ) {

            // Wait for replies   
            while (g.net.rcv(l.p, "ack").size() + g.net.rcv(l.p, "nack").size() < (g.N + 1) / 2 ) { // Busy wait  
            }

            if (g.net.rcv(l.p, "ack").size() >= (g.N + 1) / 2 ) {
                R_broadcast(l.p, l.r_p, l.estimate_p);
                log("Phase 4: broadcast " + "r_p=" + l.r_p + " estimate=" + l.estimate_p);
                l.state_p = "decided";
                l.decide = l.estimate_p;
                g.failure.IamDone(l.p);
            }
        }

    }

    public void R_broadcast(int p, int r, int estimate) {
        for (int i = 0; i < g.N; i++) {
            g.net.snd(new Message(p, i, "decide", new Payload(r, estimate, -2)));
        }
    }

    public void pr(String text) {
       return;
    }

    public void chandraToueg() {
        while (l.state_p.equals("undecided") && go()) {
            l.r_p = l.r_p + 1;
            l.c_p = (l.r_p % g.N);

            tick(); // Time passes


            if (go()) {
                Phase1();
            }
            if (go()) {
                Phase2();
            }
            if (go()) {
                Phase3();
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
