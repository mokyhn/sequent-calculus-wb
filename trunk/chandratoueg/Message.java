package chandratoueg;

/**
 *
 * @author mku
 */
public class Message {
    int source;
    int destination;
    String msgType;
    Payload payload;


    public Message(int source, int destination, String mt, Payload payload){
        this.source      = source;
        this.destination = destination;
        this.msgType     = mt;       
        this.payload     = payload;
    }

    @Override
    public String toString() {
      return "Message of type " + msgType + 
             " from " + source +
             " to " + destination;
    }
}
