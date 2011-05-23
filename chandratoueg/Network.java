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
   private ConcurrentLinkedQueue<Message> net;
   Failure failure;
   
   ConcurrentLinkedQueue<Message>[] inboxes;

    
   
   public Network (int N) {
     this.N        = N;
     this.net      = new ConcurrentLinkedQueue();
     this.failure  = new Failure(N, this);
     this.inboxes  = new ConcurrentLinkedQueue[N];
     for (int i = 0; i < N; i++)
         this.inboxes[i] = new ConcurrentLinkedQueue();
   }

   public void snd(Message m){
       inboxes[m.destination].add(m);       
   }

   public ArrayList<Message> rcv(int dest, String msgType) {
     Message m;
     ArrayList<Message> res = new ArrayList();   
     Iterator<Message> it;

     if (inboxes[dest].isEmpty()) return res;

     it = inboxes[dest].iterator();
     
     while(it.hasNext()) {
       m = it.next();
       if(m.msgType.equals(msgType));
       {  
            res.add(m);
       }
     }

     return res;
   }


   public void delete(Message m) {      
          inboxes[m.destination].remove(m);
   }
   
   public void delete(ArrayList<Message> msgs) {
    Message m;
    
    for (int i = 0; i < msgs.size(); i++) {
     m = msgs.get(i);
     inboxes[m.destination].remove(m);    
    }
   }

   
   public void delete(int source) {
    ArrayList<Message> messages = new ArrayList();
    Message m;
    
    Iterator<Message> it = net.iterator();
   
    while (it.hasNext()) {
     m = it.next();
     if (m.source == source) messages.add(m);
    }
    
    delete(messages);
   }
   
   @Override
   public String toString () {
       return "Size of net " + net.size();
   }    
}
