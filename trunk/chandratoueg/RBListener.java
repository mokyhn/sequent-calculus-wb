package chandratoueg;

import java.util.ArrayList;

/**
 *
 * @author mku
 */
public class RBListener extends Thread {

    GlobalState g;
    LocalState l;
    ArrayList<Message> done = new ArrayList();

    public RBListener(GlobalState g, LocalState l) {
        setDaemon(true); //Marks this thread as either a daemon thread or a user thread. 
                         //The Java Virtual Machine exits when the only threads running are all daemon threads.
 
        this.g = g;
        this.l = l;
    }

    public void R_deliver() {
        ArrayList<Message> msgs;
        Message m;
        int i, j;

        while (g.failure.amIalive(l.p)) {
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
                    l.decide = m.payload.estimate;
                    g.failure.IamDone(l.p);
                }
            }
            g.net.delete(msgs); // Done with these packages
        }
    }

    @Override
    public void run() {
        R_deliver();
    }
}