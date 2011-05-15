package chandratoueg;

import java.util.ArrayList;

/**
 *
 * @author mku
 */

public class Network {
   int N;
   ArrayList<Message> net;
   Failure            failure;

   public Network (int N) {
     this.N = N;
     this.net     = new ArrayList();
     this.failure = new Failure(N);
   }


   public synchronized void snd(Message m){
       net.add(m);
   }

   public synchronized ArrayList<Message> rcv(int who, String msgType) {
     Message m;
     ArrayList<Message> res = new ArrayList();
     int i          = 0;

     if (net.isEmpty()) return null;

     for (i = 0; i < net.size(); i++) {
       m = net.get(i);
       if(m.destination == who      &&
          m.msgType.equals(msgType) &&
          !failure.fd_P(m.source));
       {  // Add quasi reliable communication here.
            res.add(m);
       }
     }

     return res;
   }



   @Override
   public String toString () {
       return "Size of net " + net.size();
   }
}
