/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chandratoueg;

import java.util.ArrayList;

/**
 *
 * @author mku
 */

public class Network {
   ArrayList<Message> net;

   public Network () {
     net = new ArrayList();
   }


   public synchronized void snd(Message m){
       net.add(m);
   }

   public synchronized Message rcv(int who, String msgType) {
     Message m;
     int minRound   = Integer.MAX_VALUE;
     int indexOfMsg = 0;
     int i          = 0;

     if (net.isEmpty()) return null;

     for (i = 0; i < net.size(); i++) {
       m = net.get(i);
       if (m.destination == who &&
            (m.msgType == null ? msgType == null : m.msgType.equals(msgType))) {
              if (m.round < minRound) {
                  indexOfMsg = i;
                  minRound   = m.round;
              }
       }
     }

     return (Message) net.get(indexOfMsg);
   }



   @Override
   public String toString () {
       return "Size of net " + net.size();
   }
}
