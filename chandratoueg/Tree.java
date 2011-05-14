package chandratoueg;

public class Tree {
 int value;

 Tree left;
 Tree right;

 // Constructor
 public Tree(int val) {
     value = val;
     left  = null;
     right = null;
 }

 public void print() {
     System.out.print(" " + value + " ");

     System.out.print("(");  // Start printing left part
     if (left != null) left.print();
     else System.out.print("nil");
     System.out.print(")");  // Stop printing left part

     System.out.print("[");
     if (right != null) right.print();
     else System.out.print("nil");
     System.out.print("]");
 }

 public void insert(int newvalue) {
     insert(newvalue, this);
 }

 public void insert(int newvalue, Tree t) {
   if (t.value == newvalue) return;

   if (t.value > newvalue) {
       if (t.left == null) t.left = new Tree(newvalue);// We found an empty leaf
       else insert(newvalue, t.left);
       return;
   }

   if (t.value < newvalue) {
       if (t.right == null) t.right = new Tree(newvalue);
       else insert(newvalue, t.right);
       return;
   }
 }

 public int search(int val) {
  if (val == value) return 1;

  if (val < value && left != null) return left.search(val);
  if (val > value && right != null) return right.search(val);

  return 0;
 }


 }
 
