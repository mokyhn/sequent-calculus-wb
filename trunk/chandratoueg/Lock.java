/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chandratoueg;

/**
 *
 * 
 */
public class Lock{
  
  private boolean isLocked = false;
  
  public synchronized void lock()
  throws InterruptedException{
    while(isLocked){
      wait();
    }
    isLocked = true;
  }
  
  public synchronized void unlock(){
    isLocked = false;
    notify();
  }
}    
