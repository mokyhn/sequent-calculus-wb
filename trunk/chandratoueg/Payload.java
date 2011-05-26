package chandratoueg;

/**
 *
 * @author mku
 */
public class Payload implements Cloneable {
    int round;
    int estimate;
    int ts;
  
    public Payload(int round, int estimate, int ts) {
     this.round    = round;
     this.estimate = estimate;
     this.ts       = ts;
    }
    
    @Override
    public String toString() {
     return "round=" + round + 
            " estimate=" + estimate +
            " time stamp=" + ts;
    }
    
    
    @Override
    public Payload clone() {
        Payload theClone = new Payload(this.round, this.estimate, this.ts);

        return theClone;
    }
    
}
