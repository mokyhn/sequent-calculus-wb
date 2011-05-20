package chandratoueg;


public class Main {

   public static void main(String[] args) throws InterruptedException {
       Tree t1 = new Tree(10);
       Tree t2 = new Tree(20);
       Tree t3 = new Tree(30);

       int i;

       t2.left  = t1;
       t2.right = t3;

       t2.insert(45);


       t2.insert(10);

       t2.print();

       //System.out.print(t2.search(45));

       System.out.println();

       int N = 10;
       GlobalState  g   = new GlobalState(N);
   
       //System.out.println("(10+1)/2 = " + ((10+1)/2)); 
       //if (1==1) return;
       
       //System.out.println(Integer.MAX_VALUE);
       //if (1==1) return;
       
       // Agent a1 amd a2 are on the same net
       Agent agents[]        = new Agent[N];
       RBListener rblisten[] = new RBListener[N];
       int p;
       int j;

       for (p = 0; p < N; p++)
       {
         agents[p] = new Agent(p, g);
         agents[p].start();
         rblisten[p] = new RBListener(g, agents[p].getLocalState());
         rblisten[p].start();
       }

       
       
       for (j = 0; j < N; j++)
           agents[j].join();

       System.out.println("----------------------------");
       System.out.println("\n");
       
       for (j = 0; j < N; j++) {
           if (g.failure.amIdone(j))
            System.out.println("Agent " + j + " decided " + agents[j].l.decide);
       }
       
       
       System.out.println(g.failure.toString());
       
       // Add agreement check!
       // Add staticstics information!
       
    }
}
