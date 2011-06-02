package chandratoueg;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author mku
 */
public class Network {
   int N;
   Failure failure;
   
   Log log;
   
   private ConcurrentLinkedQueue<Message>[][] inboxes;
   
   public Network (int N, Failure failure, Log log) {
     int j;
     
     this.N        = N;
     this.failure  = failure;
     this.log      = log;
     this.failure  = failure;
     this.inboxes  = new ConcurrentLinkedQueue[N][Message.N_MSG_TYPES];
     for (int i = 0; i < N; i++)
       for (j = 0; j < Message.N_MSG_TYPES; j++)
         this.inboxes[i][j] = new ConcurrentLinkedQueue();
   }

   public synchronized void snd(Message m) {
       if (!failure.fd_P(m.destination)) { 
        inboxes[m.destination][m.msgType].add(m);  
        //if (m.msgType != Message.PHASE4DECIDE)
        log.add(m.toString());
       }
   }

   public synchronized ConcurrentLinkedQueue<Message> rcv(int dest, byte msgType) {
     return inboxes[dest][msgType];
   }
       
}
