/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//use
//        http://download.oracle.com/javase/7/docs/api/java/util/concurrent/ThreadLocalRandom.html
// Instead

// Example int r = ThreadLocalRandom.current().nextInt(4, 77);

package chandratoueg;

/**
 *
 * @author mku
 */
public class OldRandom {
  long m_w = 281503;
  long m_z = 210579;
  long random_seed = 281503;

  synchronized long get_random()
  {
    m_z = 36969 * (m_z & 65535) + (m_z >> 16);
    m_w = 18000 * (m_w & 65535) + (m_w >> 16);
    return (m_z << 16) + m_w;  /* 32-bit result */
   }

  synchronized float get_random_float() {
      float a = (float) get_random();
      float b = (float) get_random();

      return Math.min(a/b, b/a);
  }

  synchronized boolean get_random_bit() {
      return get_random_float() > 0.5;
  }



}
