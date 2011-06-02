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
   
   public Network (int N, Log log) {
     int j;
     
     this.N        = N;
     this.log      = log;
     this.failure  = new Failure(N, this);
     this.inboxes  = new ConcurrentLinkedQueue[N][Message.N_MSG_TYPES];
     for (int i = 0; i < N; i++)
       for (j = 0; j < Message.N_MSG_TYPES; j++)
         this.inboxes[i][j] = new ConcurrentLinkedQueue();
   }

   public synchronized void snd(Message m) {
       inboxes[m.destination][m.msgType].add(m);  
   }

   public synchronized ConcurrentLinkedQueue<Message> rcv(int dest, byte msgType) {
     return inboxes[dest][msgType];
   }


   public synchronized  void deleteMsgOfType(int whoAmi, byte mType) {      
       inboxes[whoAmi][mType].clear();
   }
   
   //public synchronized void delete(ArrayList<Message> msgs) {
    /*Message m;
    
    for (int i = 0; i < msgs.size(); i++) {
     m = msgs.get(i);
     inboxes[m.destination].remove(m);    
    }*/
   //}

    public  synchronized void deleteAll(int whoAmI) {
        for (int i = 0; i < Message.N_MSG_TYPES; i++)
         inboxes[whoAmI][i].clear();
    }
  
       
}
