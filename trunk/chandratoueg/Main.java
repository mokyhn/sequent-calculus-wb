package chandratoueg;


public class Main {
   static long  waTime;
   static long  scTime;
   static GlobalState  g;  
   static Agent agents[];
   static RBListener rblisten[];
   
   public static void doRun(int iter, int N, long waTime, long scTime) throws InterruptedException {
     int p;
     int decide = -1;
       
     Main.waTime = waTime;
     Main.scTime = scTime;
     Main.g = new GlobalState(N, waTime, scTime);
     agents        = new Agent[N];
     rblisten = new RBListener[N];
     
     g.log.relayToScreen(false);

     System.out.print("Iteration: " + iter + " Running N = " + N + ", waTime = " + waTime + " and scTime = " + scTime + "...");
     
     for (p = 0; p < N; p++)
       {
         agents[p] = new Agent(p, g);
         agents[p].start();
         rblisten[p] = new RBListener(g, agents[p].getLocalState());
         rblisten[p].start();
       }
     
     
       
     for (p = 0; p < N; p++) {
           agents[p].join();
      }

     // Check agreement
       for (p = 0; p < N; p++)
           if (g.failure.amIdone(p)) {
             if (agents[p].l.decide < 0) {
              System.out.println("An agent did not decide a value...");
              System.out.println(g.log.toString());
              System.out.println(g.failure.toString());
             }
               
             if (decide == -1) {
               decide = agents[p].l.decide;
               continue;
             }
             
             if (decide >= 0) {
               if (agents[p].l.decide != decide) {
                System.out.println("A disagreement occurred!");
                System.out.println(g.log.toString());
                System.out.println(g.failure.toString());
               }
             }
           }

      System.out.println(); 
   }
    
    
   public static void main(String[] args) throws InterruptedException {
    int noRuns = 5;
    
    for (int i = 0; i < noRuns; i++)
       doRun(i, 10, 10, 10);
       // Add staticstics information!
       
    }
}
