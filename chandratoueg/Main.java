package chandratoueg;


public class Main {

   public static void main(String[] args) throws InterruptedException {
       int p;
       
       
       if (args.length == 0) {
         System.out.println("Please supply a number of agents...");
         System.exit(0);
       }
       
       int N = Integer.parseInt(args[0]);
       
       System.out.println("N = " + N);
       
       
       GlobalState  g   = new GlobalState(N);
       g.log.disable();
       g.log.relayToScreen(false);
       Agent agents[]        = new Agent[N];
       RBListener rblisten[] = new RBListener[N];
       
       
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
