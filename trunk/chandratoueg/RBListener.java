package chandratoueg;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author mku
 */
public class RBListener extends Thread {

    GlobalState g;
    LocalState l;
    ArrayList<Message> done = new ArrayList();

    public RBListener(GlobalState g, LocalState l) {
        setDaemon(true); //Marks this thread as either a daemon thread (true) or a user thread (false). 
                         //The Java Virtual Machine exits when the only threads running are all daemon threads.
 
        this.g = g;
        this.l = l;
    }

    public void R_deliver() {
        ConcurrentLinkedQueue<Message> msgs;
        Message m;
        int i, j;

        while (g.failure.amIalive(l.p) && !g.failure.amIdone(l.p)) {
            msgs = g.net.rcv(l.p, Message.PHASE4DECIDE);
            for (i = 0; i < msgs.size(); i++) {
                m = (Message) (msgs.toArray())[i];
                if (!done.contains(m)) {
                    for (j = 0; j < g.N && g.failure.amIalive(l.p); j++) {
                        g.net.snd(new Message(m.source, j, Message.PHASE4DECIDE, m.payload));
                    }
                    done.add(m);
                }

                if (l.state_p.equals("undecided")) {
                    l.state_p = "decided";
                    l.decide = m.payload.estimate;
                    g.failure.IamDone(l.p);
                }
            }
            //g.net.delete(msgs); // Done with these packages
        }
    }

    @Override
    public void run() {
        R_deliver();
    }
}