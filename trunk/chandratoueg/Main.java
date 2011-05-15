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

       int N = 50;
       Network net = new Network(N);
       Failure f   = new Failure(N);
       Clock c     = new Clock();

       // Agent a1 amd a2 are on the same net
       Agent agents[] = new Agent[N];

       int j;

       for (j = 0; j < N; j++)
       {
         agents[j] = new Agent(j, net, f, c, N);
         agents[j].start();
       }

         for (j = 0; j < N; j++)
       { System.out.println(" " + agents[j].p + " " + agents[j].isAlive());
       }
       
       for (j = 0; j < N; j++)
           agents[j].join();
       
       System.out.println("Hej hej");

       for (j = 0; j < N; j++)
       { System.out.println(" " + agents[j].p + " " + agents[j].isAlive());
       }

       System.out.println();
       System.out.println();
       System.out.println(f.toString());

       System.out.println(c.getTime());
    }
}
