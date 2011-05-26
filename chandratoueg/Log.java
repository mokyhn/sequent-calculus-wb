package chandratoueg;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author mku
 */
public class Log {
   private ConcurrentLinkedQueue<String> msgs;
   boolean relayToScreen = false;
   boolean logFlag       = false;
   
   public Log() {
        msgs = new ConcurrentLinkedQueue<String>();
        relayToScreen = false;
        logFlag = false;
   }
   
   public Log(boolean rlts) {
        msgs = new ConcurrentLinkedQueue<String>();
        logFlag = rlts;
        relayToScreen = false;
   }
   
   public void relayToScreen(boolean b) {
      relayToScreen = b;
   }
   
   public void add(String s) {
     if (relayToScreen) { System.out.println(s); }
     if (logFlag) msgs.add(s + "\n");
   }
   
   @Override
   public String toString() {
    Iterator it = msgs.iterator();
    String s = null;
    String result = new String();
           
    while (it.hasNext()) {
     s = (String) it.next();
     result = result + s;
    }
            
    return result;            
   }
   
   
}
