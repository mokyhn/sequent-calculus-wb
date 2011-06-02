package chandratoueg;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author mku
 */
public class Clock {
 private AtomicLong clock;

 public Clock () {
     clock = new AtomicLong(0);
 }

 public void tick() {
     clock.incrementAndGet();
 }

 public long getTime() {
   return clock.get();
 }
}
