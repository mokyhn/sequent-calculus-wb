package chandratoueg;

/**
 *
 * @author mku
 */
public class Message implements Cloneable {
    int     source;
    int     destination;
    int     msgType;
    Payload payload;

    static final byte N_MSG_TYPES  = 5;
    static final byte PHASE1       = 0;
    static final byte PHASE2       = 1;
    static final byte PHASE3ACK    = 2;
    static final byte PHASE3NACK   = 3;
    static final byte PHASE4DECIDE = 4;
    
    static String msgTypeToString(byte mType) {
     return "";
    }
    

    public Message(int source, int destination, int mt, Payload payload){
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
      String p = "";
      
      if (payload != null) p = payload.toString();
        
        return "msgtype=" + msgType + 
             ":" + source + " ---> " + destination +
              " " + p;
    }
}
