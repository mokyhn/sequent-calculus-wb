package chandratoueg;

/**
 *
 * @author mku
 */
public class Payload {
    int round;
    int estimate;
    int ts;
  
    public Payload(int round, int estimate, int ts) {
     this.round    = round;
     this.estimate = estimate;
     this.ts       = ts;
    }
    
}
