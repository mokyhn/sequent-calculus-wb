package chandratoueg;

/**
 *
 * @author mku
 */
public class Message implements Cloneable {
    int     source;
    int     destination;
    String  msgType;
    Payload payload;


    public Message(int source, int destination, String mt, Payload payload){
        this.source      = source;
        this.destination = destination;
        this.msgType     = mt;       
        this.payload     = payload;
    }

    @Override
    public Message clone() {
        Message theClone = new Message(this.source, this.destination, this.msgType, this.payload.clone());
        return theClone;
    }
    
    @Override
    public String toString() {
      return "Message of type " + msgType + 
             " from " + source +
             " to " + destination;
    }
}
