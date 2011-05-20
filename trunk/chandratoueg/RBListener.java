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
        this.g = g;
        this.l = l;
    }

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