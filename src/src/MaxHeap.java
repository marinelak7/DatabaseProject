
/**
 * Η κλάση αυτή υλοποιεί το MaxHeap
 */
public class MaxHeap {
    //πίνακας από Points που περιέχει όλα τα στοιχεία που ανήκουν στη σωρό
    private Point[] Heap;
    //πλήθος σημείων που υπάρχουν στη σωρό
    private int count;
    //μέγιστη χωρητικότητα της σωρού
    private int maximum_capacity;

    /**
     * Constructor για το maxheap
     */
    public MaxHeap(int maximum_size) {
        this.maximum_capacity = maximum_size;
        this.count = 0;
        Heap = new Point[this.maximum_capacity + 1];
        Point point =  new Point(Double.MIN_VALUE, Double.MIN_VALUE);
        point.setDistance_point(Double.MAX_VALUE);
        Heap[0] = point;
    }

    /**
     * Getter για το count
     */
    public int getCount(){
        return count;
    }
    /**
     * Μέθοδος που επιστρέφει true αν ο κόμβος είναι φύλλο ή false αν δεν είναι
     */
    private boolean isLeaf(int node) {
        if (left_child(node) >= count) {
            return true;
        }
        return false;
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
     * Μέθοδος που υλοποιεί Swap ανάμεσα σε δύο κόμβους στο heap
     */
    private void swap(int node1, int node2) {
        Point temp;
        temp = Heap[node1];
        Heap[node1] = Heap[node2];
        Heap[node2] = temp;
    }

    /**
     * Μέθοδος που επιστρέφει το σημείο που υπάρχει στην κορυφή της σωρού
     */
    public Point getMax(){
        return Heap[1];
    }

    /**
     * Μέθοδος που αφαιρεί το max σημείο από τη σωρό
     */
    public Point popMax() {
        Point pop = Heap[1];
        Heap[1] = Heap[count];
        implementation(1);
        count--;
        return pop;
    }

    /**
     * Μέθοδος που εφαρμόζει τον αλγόριθμο MaxHeap σε ένα υποδέντρο
     * Με τη μέθοδο αυτή έχουμε ως αποτέλεσμα το δεξί και αριστερό  υποδέντρο του κόμβου node να είναι heaped και το μόνο που μένει είναι να φτιάξουμε τη ρίζα
     */
    private void implementation(int node) {

        while (!isLeaf(node) && (Heap[node].getDistance_point() < Heap[left_child(node)].getDistance_point()
                || Heap[node].getDistance_point() < Heap[right_child(node)].getDistance_point())) {

            int leftChild = left_child(node);
            int rightChild = right_child(node);


            if (Heap[leftChild].getDistance_point() > Heap[rightChild].getDistance_point()) {
                swap(node, leftChild);
                node = leftChild;
            } else {
                swap(node, rightChild);
                node = rightChild;
            }
        }
    }

    /*
    private void implementation(int node) {
        if (isLeaf(node))
            return;
        if (Heap[node].getDistance_point() < Heap[find_left_child(node)].getDistance_point() ||
                Heap[node].getDistance_point() < Heap[find_right_child(node)].getDistance_point()) {

            if (Heap[find_left_child(node)].getDistance_point() > Heap[find_right_child(node)].getDistance_point()) {
                swap(node, find_left_child(node));
                implementation(find_left_child(node));
            } else {
                swap(node, find_right_child(node));
                implementation(find_right_child(node));
            }
        }
    }*/

    /**
     * Μέθοδος που προσθέτει ένα καινούργιο στοιχείο στη σωρό
     */
    public void insert_to_maxHeap(Point p2) {

        if (count == maximum_capacity) { //η σωρος ειναι γεματη
            if (p2.getDistance_point() <= getMax().getDistance_point()) //εαν το καινουργιο σημειο ειναι πιο κοντα απο οτι το πιο μακρινο που ειναι αποθηκευμενο στο heap[1]
                popMax(); //πρεπει να βγαλουμε ενα σημειο για να μπει το καινουριο
            else
                return;
        }


        count += 1; //αυξηση τη θεση στη σωρο
        Heap[count] = p2; //βαλε τον νεο κομβο

        int current = count; //αρχικοποιησε μια προσωρινη μεταβλητη με τη τρεχουσα θεση
        int parent = parent(current);

        // Ανταλλάξτε τον νέο κόμβο με τον γονέα του εφόσον είναι μικρότερος
        while (current > 1 && Heap[current].getDistance_point() > Heap[parent].getDistance_point()) {
            swap(current, parent);
            current = parent;
            parent = parent(current);
        }


    }

    /*
    public void insert_to_maxHeap(Point new_point) { //insert
        //αν το καινούργιο σημείο είναι πιο κοντά από ότι είναι το πιο μακρινό σημείο
        //και η στοίβα είναι γεμάτη τότε πρέπει να βγάλουμε ένα σημείο να προσθέσουμε
        //το καινούργιο
        if (count== maximum_capacity){
            if(new_point.getDistance_point()<=getMax().getDistance_point())
                popMax();
            else
                return ;
        }
        Heap[++count] = new_point;
        //αναπροσαρμογή και διόρθωση της σειράς των στοιχείων στη σωρό
        int current = count;
        while (Heap[current].getDistance_point() > Heap[parent(current)].getDistance_point() ) {
            swap(current, parent(current));
            current = parent(current);
        }
    }*/

}