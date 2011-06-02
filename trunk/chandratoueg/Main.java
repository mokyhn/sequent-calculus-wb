package chandratoueg;


public class Main {

   public static void main(String[] args) throws InterruptedException {
       int N = 10;   // Total number of agents
       int p;
       long waTime = 30;
       long scTime = 30;
       GlobalState  g  = new GlobalState(N, waTime, scTime);
       Agent agents[]        = new Agent[N];
       RBListener rblisten[] = new RBListener[N];
      

       g.log.disable();
       g.log.relayToScreen(true);
       
    
       System.out.println("N = " + N);
       
       for (p = 0; p < N; p++)
       {
         agents[p] = new Agent(p, g);
         agents[p].start();
         rblisten[p] = new RBListener(g, agents[p].getLocalState());
         rblisten[p].start();
       }

       
       
       for (p = 0; p < N; p++)
           agents[p].join();

       System.out.println("----------------------------");
       System.out.println("\n");

       System.out.println(g.log.toString());

       for (p = 0; p < N; p++) {
           if (g.failure.amIdone(p))
            System.out.println("Agent " + p + " decided " + agents[p].l.decide);
       }
       
       
       System.out.println(g.failure.toString());
       
       System.out.println();
       
       
       // Add agreement check!
       // Add staticstics information!
       
    }
}
