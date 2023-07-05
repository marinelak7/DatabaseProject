import javax.swing.tree.TreeNode;
import java.io.*;
import java.util.ArrayList;
import java.util.Stack;


/**
 * Χτίσιμο του δένδρου R*
 */
public class Tree {


    //ρίζα του δέντρου
    private NodeOfTree root;

    //μέγιστος αριθμός θέσεων σε κάθε κόμβο
    private int max = 5;

    //ελάχιστος αριθμός θέσεων σε κάθε κόμβο
    private int min = 2; // (max.children/2) - 1

    //Λίστα που κρατάει τους κόμβους δηλαδή την διαδρομή μέσα στο δέντρο για να μπει ένα σημείο
    private ArrayList<NodeOfTree> node_path;

    //αριθμός εισαγωγών
    private int total_records;

    public int previous_records;





    /**
     * Κατασκευαστής του δέντρου
     * @param root η ρίζα του δέντρου
     */
    public Tree(NodeOfTree root) {
        this.root = root;
    }


    public int getTotal_records() {
        return total_records;
    }

    /**
     * Setter για το πλήθος των εισαγωγών
     * @param records νέος αριθμός εισαγωγών
     */
    public void setTotal_records(int records) {
        this.total_records = records;
    }

    /**
     * Getter για τη ρίζα
     */
    public NodeOfTree getRoot() {
        return root;
    }

    public void setRoot(NodeOfTree root) {
        this.root = root;
    }

    /**
     * Συνάρτηση που ελέγχει εάν ο κόμβος στον οποίο είμαστε είναι στο τελευταίο επίπεδο, δηλαδή έχει μέσα όλο φύλλα
     */
    public boolean AllLeaf(ArrayList<NodeOfTree> nodes)
    {

        for (NodeOfTree n: nodes ) {
            if(!n.isLeaf())
                return false;
        }
        return true;
    }



    /**
     * Συνάρτηση που υλοποιεί τον αλγόριθμο ChooseSubtree
     * @param node ένας κόμβος
     * @param new_point σημείο που θέλουμε να προσθέσουμε
     * @return έναν κόμβο στον οποίο θα προστεθεί το καινούργιο σημείο
     */
    public NodeOfTree ChooseSubtree(NodeOfTree node, Point new_point) {
        NodeOfTree currentNode = node;
        node_path.add(node);
        //αν ο κόμβος που μπήκε σαν όρισμα είναι φύλλο τότε, επιστρέφει τον ίδιο τον κόμβο
        if (currentNode.isLeaf()) {
            return currentNode;
        } else {
            // αρχικοποίηση καινούργιου παιδιού
            NodeOfTree selectedChild = null;
            double minimumExtend = Double.MAX_VALUE;
            //υπολογισμός του πόσο θα αλλάξει το ορθογώνιο του κάθε παιδιού του node μετά την εισαγωγή του νέου στοιχείου

            for (NodeOfTree child : currentNode.getChildren()) {
                double area = child.getRectangle().set_new_area(new_point.getLat(), new_point.getLon()) - child.getRectangle().getArea();

                if (area < minimumExtend) {
                    minimumExtend = area;
                    selectedChild = child;
                }
            }
            //καλείται αναδρομικά η συνάρτηση με όρισμα τον κόμβο
            return ChooseSubtree(selectedChild, new_point);
        }

    }



    /**
     * Συνάρτηση που χωρίζει έναν κόμβο σε δύο
     * Υλοποιήθηκε σύμφωνα με το paper του R*Tree που αναρτήθηκε στη σελίδα του μαθήματος
     * @param
     * @return μια ArrayList που περιέχει τους δύο νέους κόμβους
     */

    public ArrayList<NodeOfTree> ChooseSplitAxis(NodeOfTree node) {
        //αρχικοποίηση δύο ΚΥΡΙΩΝ κόμβων
        NodeOfTree Node1 = new NodeOfTree(0, 0, 0, 0);
        NodeOfTree Node2 = new NodeOfTree(0, 0, 0, 0);
        //περίμετρος που θα βρεθεί όταν ταξινομήσουμε τα σημεία σύμφωνα με τα x τους
        double perimeter_of_X = 0;
        //περίμετρος που θα βρεθεί όταν ταξινομήσουμε τα σημεία σύμφωνα με τα y τους
        double perimeter_of_Y = 0;
        //υπολογισμός του overlap
        //σκοπός είναι να πάρει την μικρότερη δυνατή τιμή
        double minimum_overlap = Double.MAX_VALUE;

        //αν ο κόμβος node είναι φύλλο
        if (node.getChildren().size() == 0) {
            //ταξινόμηση των σημείων του κόμβου ως προς τον άξονα x (απο το μικρότερο στο μεγαλύτερο)
            node.getPoints().sort(new Point.PointsComparatorX());
            for (int k = 1; k < max - 2 * min + 2; k++) { //k<3
                //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ καινούργιων κόμβων
                NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                int b = 0;
                //χωρίζουμε τα σημεία που ήταν στον αρχικό κόμβο
                //τοποθετούμε τα μισά στον πρώτο ΠΡΟΣΩΡΙΝΟ κόμβο

                for (; b < min - 1 + k; b++) {
                    node1.add_new_point(node.getPoints().get(b));
                }
                //και τα άλλα μισά στον δεύτερο ΠΡΟΣΩΡΙΝΟ κόμβο
                for (; b < max + 1; b++) {
                    node2.add_new_point(node.getPoints().get(b));
                }

                // υπολογίζουμε το άθροισμα των περιμέτρων των δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων όταν ταξινομομούμε σύμφωνα με στο x
                double sum_per_X = node1.getRectangle().getPerimeter() + node2.getRectangle().getPerimeter();
                perimeter_of_X += sum_per_X;
            }
            //κάνουμε την ίδια διαδικασία απλά τώρα ταξινομούμε τα προς y
            //ταξινόμηση των σημείων του κόμβου ως προς τον άξονα y (απο το μικρότερο στο μεγαλύτερο)
            node.getPoints().sort(new Point.PointsComparatorY());
            for (int k = 1; k < max - 2 * min + 2; k++) {
                //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων
                NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                int b = 0;
                //χωρίζουμε τα σημεία που ήταν στον αρχικό κόμβο
                //τοποθετούμε τα μισά στον πρώτο ΠΡΟΣΩΡΙΝΟ κόμβο
                for (; b < min - 1 + k; b++) {
                    node1.add_new_point(node.getPoints().get(b));
                }
                //και τα άλλα μισά στον δεύτερο ΠΡΟΣΩΡΙΝΟ κόμβο
                for (; b < max + 1; b++) {
                    node2.add_new_point(node.getPoints().get(b));
                }
                //άθροισμα των περιμέτρων των δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων όταν ταξινομούμε σύμφωνα με το y
                double sum_per_Y = node1.getRectangle().getPerimeter() + node2.getRectangle().getPerimeter();
                perimeter_of_Y += sum_per_Y;
            }
            //διαλέγουμε τη μικρότερη τιμή περιμέτρων που έχει προκύψει
            //αν η πρώτη περίμετρος (ως προς x) είναι μικρότερη τότε
            if (perimeter_of_X < perimeter_of_Y) {
                // γίνεται πάλι ταξινόμηση των σημείων του κόμβου ως προς τον άξονα x (μικρότερο προς μεγαλύτερο)
                node.getPoints().sort(new Point.PointsComparatorX());
                //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων
                for (int k = 1; k < max - 2 * min + 2; k++) {
                    NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                    NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                    int b = 0;
                    //χωρίζουμε πάλι τα σημεία στους δύο νέους ΠΡΟΣΩΡΙΝΟΥΣ κόμβους
                    for (; b < min - 1 + k; b++) {
                        node1.add_new_point(node.getPoints().get(b));
                    }
                    for (; b < max + 1; b++) {
                        node2.add_new_point(node.getPoints().get(b));
                    }
                    //όμως τώρα υπολογίζουμε το overlap που σχηματίζεται μεταξύ των ΠΡΟΣΩΡΙΝΩΝ κόμβων (όχι την περίμετρο)
                    //όταν ταξινομούμε ως προς x
                    double overlapx = node1.getRectangle().calculate_overlap(node2.getRectangle());
                    //κρατάμε το μικρότερο πιθανό overlap και δίνουμε τιμές στα στους δύο ΚΥΡΙΟΥΣ κόμβους
                    if (overlapx < minimum_overlap) {
                        minimum_overlap = overlapx;
                        Node1 = node1;
                        Node2 = node2;
                    }
                    //αν το overlap που βρήκαμε τώρα είναι ίσο με το minimum_overlap τότε συγκρίνουμε
                    //α) το εμβαδό του ορθογωνίου που προκύπτει από την πρόσθεση των ορθογωνίων που σχηματίζουν οι δύο ΚΥΡΙΟΙ κόμβοι
                    //β) το εμβαδό του ορθογωνίου που προκύπτει από την πρόσθεση των ορθογωνίων που σχηματίζουν οι δύο ΠΡΟΣΩΡΙΝΟΙ κόμβοι
                    if(overlapx==minimum_overlap){
                        double area1= Node1.getRectangle().getArea() + Node2.getRectangle().getArea();
                        double area2= node1.getRectangle().getArea() + node2.getRectangle().getArea();
                        //αν το β) είναι μικρότερο τότε δίνουμε καινούργιες τιμές στους ΚΥΡΙΟΥΣ κόμβους
                        if(area2<area1){
                            Node1=node1;
                            Node2=node2;
                        }
                    }
                }
            }
            //αν η δεύτερη περίμετρος είναι μικρότερη
            else {
                // γίνεται πάλι ταξινόμηση των σημείων του κόμβου ως προς τον άξονα y (μικρότερο προς μεγαλύτερο)
                node.getPoints().sort(new Point.PointsComparatorY());
                for (int k = 1; k < max - 2 * min + 2; k++) {
                    //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων
                    NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                    NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                    int b = 0;
                    for (; b < min - 1 + k; b++) {
                        node1.add_new_point(node.getPoints().get(b));
                    }
                    for (; b < max + 1; b++) {
                        node2.add_new_point(node.getPoints().get(b));
                    }
                    //όμως τώρα υπολογίζουμε το overlap που σχηματίζεται μεταξύ των ΠΡΟΣΩΡΙΝΩΝ κόμβων (όχι την περίμετρο)
                    //όταν ταξινομούμε ως προς y
                    double overlapy = node1.getRectangle().calculate_overlap(node2.getRectangle());
                    //κρατάμε το μικρότερο πιθανό overlap και δίνουμε τιμές στα στους δύο ΚΥΡΙΟΥΣ κόμβους
                    if (overlapy < minimum_overlap) {
                        minimum_overlap = overlapy;
                        Node1 = node1;
                        Node2 = node2;
                    }
                    //αν το overlap που βρήκαμε τώρα είναι ίσο με το minimum overlap τότε συγκρίνουμε
                    //α) το εμβαδό του ορθογωνίου που προκύπτει από την πρόσθεση των ορθογωνίων που σχηματίζουν οι δύο ΚΥΡΙΟΙ κόμβοι
                    //β) το εμβαδό του ορθογωνίου που προκύπτει από την πρόσθεση των ορθογωνίων που σχηματίζουν οι δύο ΠΡΟΣΩΡΙΝΟΙ κόμβοι
                    if(overlapy==minimum_overlap){
                        double area1= Node1.getRectangle().getArea() + Node2.getRectangle().getArea();
                        double area2= node1.getRectangle().getArea() + node2.getRectangle().getArea();
                        //αν το β) είναι μικρότερο τότε δίνουμε καινούργιες τιμές στους ΚΥΡΙΟΥΣ κόμβους
                        if(area2<area1){
                            Node1=node1;
                            Node2=node2;
                        }
                    }
                }
            }
        }
        //αν ο κόμβος δεν είναι φύλλο
        else {
            //κάνε ταξινόμηση των κόμβων σύμφωνα με τις τιμές των ορθογωνίων τους ως προς τον άξονα x
            // (από την πιο μικρή τιμή προς την πιο μεγάλη)
            node.getChildren().sort(new NodeOfTree.ComparatorX());
            for (int k = 1; k < max - 2 * min + 2; k++) {
                //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων
                NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                int b = 0;
                for (; b < min - 1 + k; b++) {
                    node1.add_new_child_node(node.getChildren().get(b));
                }
                for (; b < max + 1; b++) {
                    node2.add_new_child_node(node.getChildren().get(b));
                }
                //άθροισμα των περιμέτρων των δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων όταν ταξινομομούμε σύμφωνα με στο x
                double sum_per_X = node1.getRectangle().getPerimeter() + node2.getRectangle().getPerimeter();
                perimeter_of_X += sum_per_X;
            }
            //κάνε ταξινόμηση των κόμβων σύμφωνα με τις τιμές των ορθογωνίων τους ως προς τον άξονα y
            // (από την πιο μικρή τιμή προς την πιο μεγάλη)
            node.getChildren().sort(new NodeOfTree.RectangleComparatorY());
            for (int k = 1; k < max - 2 * min + 2; k++) {
                //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων
                NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                int b = 0;
                for (; b < min - 1 + k; b++) {
                    node1.add_new_child_node(node.getChildren().get(b));
                }
                for (; b < max + 1; b++) {
                    node2.add_new_child_node(node.getChildren().get(b));
                }
                //άθροισμα των περιμέτρων των δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων όταν ταξινομομούμε σύμφωνα με στο y
                double sum_per_Y = node1.getRectangle().getPerimeter() + node2.getRectangle().getPerimeter();
                perimeter_of_Y += sum_per_Y;
            }
            //πάλι διαλέγουμε τη μικρότερη τιμή περιμέτρων
            //αν η πρώτη περίμετρος είναι μικρότερο τότε
            if (perimeter_of_X < perimeter_of_Y) {
                //γίνεται πάλι ταξινόμηση των κόμβων σύμφωνα με τις τιμές των ορθογωνίων τους ως προς τον άξονα x
                node.getChildren().sort(new NodeOfTree.ComparatorX());
                for (int k = 1; k < max - 2 * min + 2; k++) {
                    //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων
                    NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                    NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                    int b = 0;
                    for (; b < min - 1 + k; b++) {
                        node1.add_new_child_node(node.getChildren().get(b));
                    }
                    for (; b < max + 1; b++) {
                        node2.add_new_child_node(node.getChildren().get(b));
                    }
                    //υπολογίζουμε το overlap που σχηματίζεται μεταξύ των ΠΡΟΣΩΡΙΝΩΝ κόμβων (όχι την περίμετρο)
                    // όταν ταξινομούμε ως προς x
                    double overlapX = node1.getRectangle().calculate_overlap(node2.getRectangle());
                    //κρατάμε το μικρότερο πιθανό overlap και δίνουμε τιμές στα στους δύο ΚΥΡΙΟΥΣ κόμβους
                    if (overlapX < minimum_overlap) {
                        minimum_overlap = overlapX;
                        Node1 = node1;
                        Node2 = node2;
                    }
                    if(overlapX==minimum_overlap){
                        double area1= Node1.getRectangle().getArea() + Node2.getRectangle().getArea();
                        double area2= node1.getRectangle().getArea() + node2.getRectangle().getArea();
                        if(area2<area1){
                            Node1=node1;
                            Node2=node2;
                        }
                    }
                }
            }
            //αν η δεύτερη περίμετρος είναι μικρότερη
            else {
                //γίνεται πάλι ταξινόμηση των κόμβων σύμφωνα με τις τιμές των ορθογωνίων τους ως προς τον άξονα y
                node.getChildren().sort(new NodeOfTree.RectangleComparatorY());
                for (int k = 1; k < max - 2 * min + 2; k++) {
                    //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων
                    NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                    NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                    int b = 0;
                    for (; b < min - 1 + k; b++) {
                        node1.add_new_child_node(node.getChildren().get(b));
                    }
                    for (; b < max + 1; b++) {
                        node2.add_new_child_node(node.getChildren().get(b));
                    }
                    //υπολογισμός του overlap που σχηματίζεται μεταξύ των ΠΡΟΣΩΡΙΝΩΝ κόμβων (όχι την περίμετρο)
                    //όταν ταξινομούμε ως προς y
                    double overlapY = node1.getRectangle().calculate_overlap(node2.getRectangle());
                    //κρατάμε το μικρότερο πιθανό overlap και δίνουμε τιμές στα στους δύο ΚΥΡΙΟΥΣ κόμβους
                    if (overlapY < minimum_overlap) {
                        minimum_overlap = overlapY;
                        Node1 = node1;
                        Node2 = node2;
                    }
                    if(overlapY==minimum_overlap){
                        double area1= Node1.getRectangle().getArea() + Node2.getRectangle().getArea();
                        double area2= node1.getRectangle().getArea() + node2.getRectangle().getArea();
                        if(area2<area1){
                            Node1=node1;
                            Node2=node2;
                        }
                    }
                }
            }
        }

        ArrayList<NodeOfTree> split = new ArrayList<>(2);
        split.add(Node1);
        split.add(Node2);
        return split;
    }


    /**
     * Συνάρτηση που εισάγει ένα καινούργιο σημείο στο δέντρο
     * @param new_point σημείο που πρέπει να προσθέσουμε στο δέντρο
     */
    public void add_in_tree(Point new_point) {


        node_path = new ArrayList<>();
        NodeOfTree node = ChooseSubtree(root, new_point);
        //αν το πλήθος των σημειων του κόμβου είναι μικρότερο από τον μέγιστο αριθμό παιδιών που μπορεί να έχει ένας κόμβος
        //τότε προσθέτουμε σε αυτόν το σημείο αυτό και ανανεώνουμε το μονοπάτι
        if (node.getPoints().size() < max) {
            node.add_new_point(new_point);
            updatePathDimensions(new_point);

        }
        //αλλιώς αν το πλήθος των παιδιών ξεπερνάει τον μέγιστο αριθμό παιδιών που μπορεί να έχει ένας κόμβος
        //πρέπει να κάνουμε split
        else {
            node.add_new_point(new_point);
            //κάνουμε split μόνο τη ρίζα
            if (node_path.size() == 1) { //εαν εχουμε μονο ριζα
                ArrayList<NodeOfTree> splitted_node = ChooseSplitAxis(node);
                NodeOfTree newRoot = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                newRoot.add_new_child_node(splitted_node.get(0));
                newRoot.add_new_child_node(splitted_node.get(1));
                root = newRoot;
                updatePathDimensions(new_point);

            }
            //κάνουμε split μεγαλύτερο μέρος του δέντρου
            else { //εαν δεν εχουμε μονο την ριζα
                for (int j = node_path.size() - 1; j >= 0; j--) {
                    //αν βρει φύλλο
                    if (node_path.get(j).isLeaf()) {
                        for (int l = 0; l < node_path.get(j - 1).getChildren().size(); l++) {
                            //γίνεται αναζήτηση και διαγραφή του παιδιού που θέλουμε να κάνουμε split
                            //και στη θέση αυτού θα βάλουμε τα δύο καινούργια "χωρισμένα" παιδιά
                            if (node_path.get(j - 1).getChildren().get(l) == node_path.get(j)) {
                                node_path.get(j - 1).getChildren().remove(l);
                                break;
                            }
                        }
                        //προσθήκη καινούργιου παιδιού
                        ArrayList<NodeOfTree> temp = ChooseSplitAxis(node_path.get(j));
                        node_path.get(j - 1).add_new_child_node(temp.get(0));
                        node_path.get(j - 1).add_new_child_node(temp.get(1));
                    }
                    //αν δεν βρει φύλλο
                    else {
                        //αν το πλήθος των παιδιών ξεπερνάει το όριο
                        if (node_path.get(j).getChildren().size() > max) {
                            //αν βρεις φύλλο
                            if (j - 1 >= 0) {
                                for (int l = 0; l < node_path.get(j - 1).getChildren().size(); l++) {
                                    //γίνεται αναζήτηση και διαγραφή του παιδιού που θέλουμε να κάνουμε split
                                    //και στη θέση αυτού θα βάλουμε τα δύο καινούργια "χωρισμένα" παιδιά
                                    if (node_path.get(j - 1).getChildren().get(l) == node_path.get(j)) {
                                        node_path.get(j - 1).getChildren().remove(l);
                                        break;
                                    }
                                }
                                //προσθήκη καινούργιου παιδιού
                                ArrayList<NodeOfTree> split = ChooseSplitAxis(node_path.get(j));
                                node_path.get(j - 1).add_new_child_node(split.get(0));
                                node_path.get(j - 1).add_new_child_node(split.get(1));
                            }
                            //τότε χρειαζόμαστε καινούργια ρίζα
                            else {
                                ArrayList<NodeOfTree> splitAxis = ChooseSplitAxis(node_path.get(j));
                                NodeOfTree newRoot = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                                newRoot.add_new_child_node(splitAxis.get(0));
                                newRoot.add_new_child_node(splitAxis.get(1));
                                root = newRoot;
                            }

                        }
                    }
                    updatePathDimensions(new_point);

                }
            }
        }
        previous_records = total_records ;
        total_records++;
    }

    /**
     * Βοηθητική μέθοδος για ανανέωση μονοπατιού
     * @param new_point
     */
    private void updatePathDimensions(Point new_point) {
        for (int i = 0; i < node_path.size(); i++) {
            node_path.get(i).getRectangle().set_New_Dimensions(new_point.getLat(), new_point.getLon());
        }
    }


}



