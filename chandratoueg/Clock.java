/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chandratoueg;

/**
 *
 * @author mku
 */
public class Clock {
 private int clock;

 public Clock () {
     clock = 0;
 }

 public synchronized void tick() {
     clock = clock + 1;
 }

 public synchronized int getTime() {
   return clock;
 }
}
