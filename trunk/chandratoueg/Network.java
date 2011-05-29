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

   
   private ConcurrentLinkedQueue<Message>[] inboxes;

    
   
   public Network (int N, Log log) {
     this.N        = N;
     this.log      = log;
     this.failure  = new Failure(N, this);
     this.inboxes  = new ConcurrentLinkedQueue[N];
     for (int i = 0; i < N; i++)
         this.inboxes[i] = new ConcurrentLinkedQueue();
   }

   public synchronized  void snd(Message m) {
       inboxes[m.destination].add(m);  
   }

   public synchronized  ArrayList<Message> rcv(int dest, String msgType) {

     if (inboxes[dest].isEmpty()) return res;

     it = inboxes[dest].iterator();
     
     while(it.hasNext()) {
       m = it.next();
       
       if(m.msgType.equals(msgType));
       {  
         if(!m.msgType.equals(msgType)) {
          System.out.println("Oh nooooo" );
          System.exit(0);
         }

           res.add(m);
       }
     }

     return res;
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
