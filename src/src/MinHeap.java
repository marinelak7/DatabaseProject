
/**
 * Η κλάση αυτή υλοποιεί το MinHeap
 */
public class MinHeap {
    //Ορίζεται μια σωρός μέσω πίνακα με κόμβους
    private NodeOfTree[] Heap;
    //Πλήθος σημείων του πίνακα heap
    private int count ;

    //Μέγιστη χωρητικότητα σωρού
    private int maximum_capacity ;

    /**
     * Constructor για το minheap
     */
    public MinHeap(int max)
    {   this.count = 0;
        this.maximum_capacity = max;
        Heap = new NodeOfTree[this.maximum_capacity + 1];
        NodeOfTree temp_Node = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
        temp_Node.setDistance_from_point(Double.MIN_VALUE);
        Heap[0] = temp_Node;
    }

    /**
     * Μέθοδος που ελέγχει αν η σωρός είναι άδεια
     */
    public boolean MINHeap_Empty (){
        if (count == 0){
            return true;
        }
        return false ;
    }

    //Στις παρακάτω συναρτήσεις βρίσκω τη θέση του γονίου , του αριστερου παιδίου και του δεξιού , δεδομένου ότι η πρώτη θέση του MINHeap είναι δεσμευμένη απ'τη ρίζα
    /**
     Μέθοδος που επιστρέφει το που βρίσκεται ο γονιός
     */
    private int parent (int node) {
        return node/2;
    }

    /**
     * Μέθοδος που επιστρέφει το που βρίσκεται το αριστερό παιδί
     */
    private int left_child (int node) {
        return node*2;
    }

    /**
     *  Μέθοδος που επιστρέφει το που βρίσκεται το δεξί παιδί
     */
    private int right_child (int node) {
        return (node*2) + 1 ;
    }


    /**
     * Μέθοδος που επιστρέφει true αν ο κόμβος είναι φύλλο ή false αν δεν είναι
     */
    private boolean isLeaf(int node) {
        if (left_child(node) >= count ){
            return true;
        }
        return false;
    }

    /**
     * Μέθοδος που υλοποιεί Swap ανάμεσα σε δύο κόμβους στο heap
     */
    private void swap(int node1, int node2)
    {
        NodeOfTree temp_node;
        temp_node = Heap[node1];
        Heap[node1] = Heap[node2];
        Heap[node2] = temp_node;
    }

    /**
     * Μέθοδος που αφαιρεί και επιστρέφει τον κόμβο που βρίσκεται πιο ψηλά στη σωρό
     */
    public NodeOfTree remove_from_heap()
    {
        NodeOfTree removable = Heap[1] ;
        Heap[1] = Heap[count -- ];
        implementation(1);
        return removable ;
    }

  /**
     * Υλοποιεί τον αλγόριθμο minHeap σε ένα υποδέντρο
     */

  private void implementation(int node) {

      while (!isLeaf(node) && (Heap[node].getDistance_from_point() > Heap[left_child(node)].getDistance_from_point()
              || Heap[node].getDistance_from_point() > Heap[right_child(node)].getDistance_from_point())) {

          int leftChild = left_child(node);
          int rightChild = right_child(node);


          if (Heap[leftChild].getDistance_from_point() < Heap[rightChild].getDistance_from_point()) {
              swap(node, leftChild);
              node = leftChild;
          } else {
              swap(node, rightChild);
              node = rightChild;
          }
      }
  }



    /**
     * Μέθοδος που εισάγει έναν καινούργιο κόμβο στη σωρό
     */
    public void insert_to_minHeap(NodeOfTree n1) {

        //Αν επιτρέπεται η εισαγωγή
        if (count < maximum_capacity) {
            count +=1 ; //αυξηση τη θεση στη σωρο
            Heap[count] = n1 ; //βαλε τον νεο κομβο

            int current = count; //αρχικοποιησε μια προσωρινη μεταβλητη με τη τρεχουσα θεση
            int parent = parent(current);

            // Ανταλλάξτε τον νέο κόμβο με τον γονέα του εφόσον είναι μικρότερος
            while (current > 1 && Heap[current].getDistance_from_point()< Heap[parent].getDistance_from_point()) {
                swap(current, parent);
                current = parent;
                parent = parent(current);
            }

        }

        return ; //τοτε δεν μπορουμε να βαλουμε καποιο στοιχειο
    }


}
