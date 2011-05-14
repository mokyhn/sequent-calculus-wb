/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chandratoueg;

/**
 *
 * @author mku
 */
public class Message {
    int source;
    int destination;
    int round;
    String msgType;
    int estimate;
    int ts;


    public Message(int source, int destination, String mt, int r, int e, int ts){
        this.source      = source;
        this.destination = destination;
        round    = r;
        msgType  = mt;
        estimate = e;
        this.ts  = ts;
    }

    @Override
    public String toString() {
      return "Message of type " + msgType + 
             " from " + source +
             " to " + destination +
             " in round " + round; // TODO: More here
    }
}
