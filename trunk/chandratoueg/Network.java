package chandratoueg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author mku
 */

public class Network {
   int N;
   Failure failure;
   
   Log log;

   Message m;
   ArrayList<Message> res = new ArrayList();   
   Iterator<Message> it;

   
   private ConcurrentLinkedQueue<Message>[][] inboxes;

    
   
   public Network (int N, Log log) {
     int j;
     
     this.N        = N;
     this.log      = log;
     this.failure  = new Failure(N, this);
     this.inboxes  = new ConcurrentLinkedQueue[N][Message.N_MSG_TYPES];
     for (int i = 0; i < N; i++)
       for (j = 0; j < m.N_MSG_TYPES; j++)
         this.inboxes[i][j] = new ConcurrentLinkedQueue();
   }

   public synchronized  void snd(Message m) {
       inboxes[m.destination][m.msgType].add(m);  
   }

   public synchronized ConcurrentLinkedQueue<Message> rcv(int dest, byte msgType) {
     return inboxes[dest][msgType];
   }


   public synchronized  void delete(Message m) {      
          //inboxes[m.destination].remove(m);
   }
   
   public synchronized void delete(ArrayList<Message> msgs) {
    /*Message m;
    
    for (int i = 0; i < msgs.size(); i++) {
     m = msgs.get(i);
     inboxes[m.destination].remove(m);    
    }*/
   }

    public  synchronized void deleteAll(int whoAmI) {
        //inboxes[whoAmI].clear();
    }
  
       
}
