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

    /**
     * Κατασκευαστής του δέντρου
     * @param root η ρίζα του δέντρου
     */
    public Tree(NodeOfTree root) {
        this.root = root;
    }


    /*public int getTotal_records() {
        return total_records;
    }*/

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



    /*
    public void write_in_index_file() {

        try {
            FileOutputStream myWriter = new FileOutputStream("indexfile.txt");
            ObjectOutputStream objectOut = new ObjectOutputStream(myWriter);
            objectOut.writeObject(root);
            objectOut.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing in file");
            e.printStackTrace();
        }
    }*/


    /*
    public void read_from_index_file() {
        try {
            FileInputStream myWriter = new FileInputStream("indexfile.txt");
            ObjectInputStream objectOut = new ObjectInputStream(myWriter);
            root = (NodeOfTree) objectOut.readObject();
            objectOut.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }*/

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
            /*for (int i = 0; i < currentNode.getChildren().size(); i++) {
                double area = currentNode.getChildren().get(i).getRectangle().set_new_area(new_point.getLat(), new_point.getLon()) - Node.getChildren().get(i).getRectangle().getArea();
                if (area < minimumExtend) {
                    minimumExtend = area;
                    child = currentNode.getChildren().get(i);

                }
            }*/

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
        double perimeter_X = 0;
        //περίμετρος που θα βρεθεί όταν ταξινομήσουμε τα σημεία σύμφωνα με τα y τους
        double perimeter_Y = 0;
        //υπολογισμός του overlap
        //σκοπός είναι να πάρει την μικρότερη δυνατή τιμή
        double min_overlap = Double.MAX_VALUE;

        //αν ο κόμβος node είναι φύλλο
        if (node.getChildren().size() == 0) {
            //ταξινόμηση των σημείων του κόμβου ως προς τον άξονα x (απο το μικρότερο στο μεγαλύτερο)
            node.getPoints().sort(new Point.PointsComparatorX());
            for (int k = 1; k < max - 2 * min + 2; k++) { //k<3
                //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ καινούργιων κόμβων
                NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                int _k = 0;
                //χωρίζουμε τα σημεία που ήταν στον αρχικό κόμβο
                //τοποθετούμε τα μισά στον πρώτο ΠΡΟΣΩΡΙΝΟ κόμβο

                for (; _k < min - 1 + k; _k++) {
                    node1.add_new_point(node.getPoints().get(_k));
                }
                //και τα άλλα μισά στον δεύτερο ΠΡΟΣΩΡΙΝΟ κόμβο
                for (; _k < max + 1; _k++) {
                    node2.add_new_point(node.getPoints().get(_k));
                }

                // υπολογίζουμε το άθροισμα των περιμέτρων των δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων όταν ταξινομομούμε σύμφωνα με στο x
                double perx = node1.getRectangle().getPerimeter() + node2.getRectangle().getPerimeter();
                perimeter_X += perx;
            }
            //κάνουμε την ίδια διαδικασία απλά τώρα ταξινομούμε τα προς y
            //ταξινόμηση των σημείων του κόμβου ως προς τον άξονα y (απο το μικρότερο στο μεγαλύτερο)
            node.getPoints().sort(new Point.PointsComparatorY());
            for (int k = 1; k < max - 2 * min + 2; k++) {
                //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων
                NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                int _k = 0;
                //χωρίζουμε τα σημεία που ήταν στον αρχικό κόμβο
                //τοποθετούμε τα μισά στον πρώτο ΠΡΟΣΩΡΙΝΟ κόμβο
                for (; _k < min - 1 + k; _k++) {
                    node1.add_new_point(node.getPoints().get(_k));
                }
                //και τα άλλα μισά στον δεύτερο ΠΡΟΣΩΡΙΝΟ κόμβο
                for (; _k < max + 1; _k++) {
                    node2.add_new_point(node.getPoints().get(_k));
                }
                //άθροισμα των περιμέτρων των δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων όταν ταξινομούμε σύμφωνα με το y
                double pery = node1.getRectangle().getPerimeter() + node2.getRectangle().getPerimeter();
                perimeter_Y += pery;
            }
            //διαλέγουμε τη μικρότερη τιμή περιμέτρων που έχει προκύψει
            //αν η πρώτη περίμετρος (ως προς x) είναι μικρότερη τότε
            if (perimeter_X < perimeter_Y) {
                // γίνεται πάλι ταξινόμηση των σημείων του κόμβου ως προς τον άξονα x (μικρότερο προς μεγαλύτερο)
                node.getPoints().sort(new Point.PointsComparatorX());
                //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων
                for (int k = 1; k < max - 2 * min + 2; k++) {
                    NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                    NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                    int _k = 0;
                    //χωρίζουμε πάλι τα σημεία στους δύο νέους ΠΡΟΣΩΡΙΝΟΥΣ κόμβους
                    for (; _k < min - 1 + k; _k++) {
                        node1.add_new_point(node.getPoints().get(_k));
                    }
                    for (; _k < max + 1; _k++) {
                        node2.add_new_point(node.getPoints().get(_k));
                    }
                    //όμως τώρα υπολογίζουμε το overlap που σχηματίζεται μεταξύ των ΠΡΟΣΩΡΙΝΩΝ κόμβων (όχι την περίμετρο)
                    //όταν ταξινομούμε ως προς x
                    double overlapx = node1.getRectangle().calculate_overlap(node2.getRectangle());
                    //κρατάμε το μικρότερο πιθανό overlap και δίνουμε τιμές στα στους δύο ΚΥΡΙΟΥΣ κόμβους
                    if (overlapx < min_overlap) {
                        min_overlap = overlapx;
                        Node1 = node1;
                        Node2 = node2;
                    }
                    //αν το overlap που βρήκαμε τώρα είναι ίσο με το min_overlap τότε συγκρίνουμε
                    //α) το εμβαδό του ορθογωνίου που προκύπτει από την πρόσθεση των ορθογωνίων που σχηματίζουν οι δύο ΚΥΡΙΟΙ κόμβοι
                    //β) το εμβαδό του ορθογωνίου που προκύπτει από την πρόσθεση των ορθογωνίων που σχηματίζουν οι δύο ΠΡΟΣΩΡΙΝΟΙ κόμβοι
                    if(overlapx==min_overlap){
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
                    int _k = 0;
                    for (; _k < min - 1 + k; _k++) {
                        node1.add_new_point(node.getPoints().get(_k));
                    }
                    for (; _k < max + 1; _k++) {
                        node2.add_new_point(node.getPoints().get(_k));
                    }
                    //όμως τώρα υπολογίζουμε το overlap που σχηματίζεται μεταξύ των ΠΡΟΣΩΡΙΝΩΝ κόμβων (όχι την περίμετρο)
                    //όταν ταξινομούμε ως προς y
                    double overlapy = node1.getRectangle().calculate_overlap(node2.getRectangle());
                    //κρατάμε το μικρότερο πιθανό overlap και δίνουμε τιμές στα στους δύο ΚΥΡΙΟΥΣ κόμβους
                    if (overlapy < min_overlap) {
                        min_overlap = overlapy;
                        Node1 = node1;
                        Node2 = node2;
                    }
                    //αν το overlap που βρήκαμε τώρα είναι ίσο με το min_overlap τότε συγκρίνουμε
                    //α) το εμβαδό του ορθογωνίου που προκύπτει από την πρόσθεση των ορθογωνίων που σχηματίζουν οι δύο ΚΥΡΙΟΙ κόμβοι
                    //β) το εμβαδό του ορθογωνίου που προκύπτει από την πρόσθεση των ορθογωνίων που σχηματίζουν οι δύο ΠΡΟΣΩΡΙΝΟΙ κόμβοι
                    if(overlapy==min_overlap){
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
            node.getChildren().sort(new NodeOfTree.RectangleComparatorX());
            for (int k = 1; k < max - 2 * min + 2; k++) {
                //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων
                NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                int _k = 0;
                for (; _k < min - 1 + k; _k++) {
                    node1.add_new_child_node(node.getChildren().get(_k));
                }
                for (; _k < max + 1; _k++) {
                    node2.add_new_child_node(node.getChildren().get(_k));
                }
                //άθροισμα των περιμέτρων των δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων όταν ταξινομομούμε σύμφωνα με στο x
                double perX = node1.getRectangle().getPerimeter() + node2.getRectangle().getPerimeter();
                perimeter_X += perX;
            }
            //κάνε ταξινόμηση των κόμβων σύμφωνα με τις τιμές των ορθογωνίων τους ως προς τον άξονα y
            // (από την πιο μικρή τιμή προς την πιο μεγάλη)
            node.getChildren().sort(new NodeOfTree.RectangleComparatorY());
            for (int k = 1; k < max - 2 * min + 2; k++) {
                //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων
                NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                int _k = 0;
                for (; _k < min - 1 + k; _k++) {
                    node1.add_new_child_node(node.getChildren().get(_k));
                }
                for (; _k < max + 1; _k++) {
                    node2.add_new_child_node(node.getChildren().get(_k));
                }
                //άθροισμα των περιμέτρων των δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων όταν ταξινομομούμε σύμφωνα με στο y
                double perY = node1.getRectangle().getPerimeter() + node2.getRectangle().getPerimeter();
                perimeter_Y += perY;
            }
            //πάλι διαλέγουμε τη μικρότερη τιμή περιμέτρων
            //αν η πρώτη περίμετρος είναι μικρότερο τότε
            if (perimeter_X < perimeter_Y) {
                //γίνεται πάλι ταξινόμηση των κόμβων σύμφωνα με τις τιμές των ορθογωνίων τους ως προς τον άξονα x
                node.getChildren().sort(new NodeOfTree.RectangleComparatorX());
                for (int k = 1; k < max - 2 * min + 2; k++) {
                    //αρχικοποίηση δύο ΠΡΟΣΩΡΙΝΩΝ κόμβων
                    NodeOfTree node1 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                    NodeOfTree node2 = new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                    int _k = 0;
                    for (; _k < min - 1 + k; _k++) {
                        node1.add_new_child_node(node.getChildren().get(_k));
                    }
                    for (; _k < max + 1; _k++) {
                        node2.add_new_child_node(node.getChildren().get(_k));
                    }
                    //υπολογίζουμε το overlap που σχηματίζεται μεταξύ των ΠΡΟΣΩΡΙΝΩΝ κόμβων (όχι την περίμετρο)
                    // όταν ταξινομούμε ως προς x
                    double overlapX = node1.getRectangle().calculate_overlap(node2.getRectangle());
                    //κρατάμε το μικρότερο πιθανό overlap και δίνουμε τιμές στα στους δύο ΚΥΡΙΟΥΣ κόμβους
                    if (overlapX < min_overlap) {
                        min_overlap = overlapX;
                        Node1 = node1;
                        Node2 = node2;
                    }
                    if(overlapX==min_overlap){
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
                    int _k = 0;
                    for (; _k < min - 1 + k; _k++) {
                        node1.add_new_child_node(node.getChildren().get(_k));
                    }
                    for (; _k < max + 1; _k++) {
                        node2.add_new_child_node(node.getChildren().get(_k));
                    }
                    //υπολογισμός του overlap που σχηματίζεται μεταξύ των ΠΡΟΣΩΡΙΝΩΝ κόμβων (όχι την περίμετρο)
                    //όταν ταξινομούμε ως προς y
                    double overlapY = node1.getRectangle().calculate_overlap(node2.getRectangle());
                    //κρατάμε το μικρότερο πιθανό overlap και δίνουμε τιμές στα στους δύο ΚΥΡΙΟΥΣ κόμβους
                    if (overlapY < min_overlap) {
                        min_overlap = overlapY;
                        Node1 = node1;
                        Node2 = node2;
                    }
                    if(overlapY==min_overlap){
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

        ArrayList<NodeOfTree> splitted_node = new ArrayList<>(2);
        splitted_node.add(Node1);
        splitted_node.add(Node2);
        return splitted_node;
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

            /*for (int i = 0; i < node_path.size(); i++) {
                node_path.get(i).getRectangle().set_New_Dimensions(new_point.getLat(), new_point.getLon());
            }*/
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
                /*for (int i = 0; i < node_path.size(); i++) { //ανανεωνει το path
                    node_path.get(i).getRectangle().set_New_Dimensions(new_point.getLat(), new_point.getLon());
                }*/
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
                    /*for (int i = 0; i < node_path.size(); i++) {
                        node_path.get(i).getRectangle().set_New_Dimensions(new_point.getLat(), new_point.getLon());
                    }*/
                }
            }
        }
    }

    private void updatePathDimensions(Point new_point) {
        for (int i = 0; i < node_path.size(); i++) {
            node_path.get(i).getRectangle().set_New_Dimensions(new_point.getLat(), new_point.getLon());
        }
    }




    /**
     * Συνάρτηση που υλοποιεί τα ερωτήματα περιοχής με τη βοήθεια του indexfile
     * Επιστρέφει τους γείτονες που βρίσκονται μέσα στον "κύκλο" γύρω από το στοιχείο middle
     * Χρησιμοποιεί μια στοίβα (για τον αλγόριθμο DFS) όπου σπρώχνουμε έναν κόμβο εάν είναι μια πιθανή επιλογή να κρατήσουμε γείτονες
     * και μια ArrayList από Points όπου βάζουμε τους γείτονες
     * @param middle σημείο από το οποίο ψάχνουμε τους γείτονες
     * @param radius η ακτίνα μέσα στην οποία ψάχνουμε τα σημεία
     */
    /*
    public ArrayList<Location> range_query_with_index(Point middle, double radius) {
        //ArrayList τις τοποθεσίες που βρίσκονται μέσα στο radius
        ArrayList<Location> range_results=new ArrayList<>();
        //ArrayList με τα στοιχεία που βρίσκονται μέσα στο radius
        ArrayList<Point> points = new ArrayList<>();
        //διαβάζει από το datafile
        LoadData data=new LoadData();
        //αν η απόσταση της ρίζα από το σημείο middle είναι μικρότερο από την τιμή της ακτίνας
        //τότε βάζουμε τη ρίζα στη στοίβα
        if (root.getRectangle().find_distance_between_point_and_Rectangle(middle) <= radius) {
            Stack<NodeOfTree> dfs_stack = new Stack<>();
            dfs_stack.push(root);
            //όσο η στοίβα δεν είναι άδεια
            while (!dfs_stack.empty()) {
                //κάνουμε pop τον πρώτο κόμβο της στοίβας
                NodeOfTree node = dfs_stack.pop();
                for (int i = 0; i < node.getChildren().size(); i++) {
                    //αν ο κόμβος που βγήκε από τη στοίβα είναι φύλλο τότε
                    //ελέγχουμε αν τα σημεία που ανήκουν μέσα στον κόμβο βρίσκονται εντός της ακτίνας radius
                    //αν ισχύει αυτό τότε προσθέτουμε τα σημεία αυτά στο points
                    if (node.getChildren().get(i).isLeaf()) {
                        for (int k = 0; k < node.getChildren().get(i).getPoints().size(); k++) {
                            if (node.getChildren().get(i).getPoints().get(k).find_distance_from_point(middle) <= radius) {
                                points.add(node.getChildren().get(i).getPoints().get(k));
                            }
                        }
                    }
                    //αν ο κόμβος που βγήκε από τη στοίβα δεν είναι φύλλο τότε
                    //ελέγχουμε αν τα Rectangle των παιδιών του είναι εντός της ακτίνας radius
                    //αν ισχύει η συνθήκη τότε βάζουμε τα παιδιά του κόμβου στη στοίβα
                    else {
                        if (node.getChildren().get(i).getRectangle().find_distance_between_point_and_Rectangle(middle) <= radius) {
                            dfs_stack.push(node.getChildren().get(i));
                        }
                    }
                }
            }
        }
        //αποθηκεύουμε τα δεκτά σημεία στη range_results
        for(int i=0;i<points.size();i++){
            Location loc = (data.find_block(points.get(i).getBlockid(),points.get(i).getSlotid()));
            loc.find_manhattan_distance_between_two_points(middle.getLat(),middle.getLon());
            range_results.add(loc);
        }
        return range_results;
    }*/

    /**
     * Συνάρτηση που υλοποιεί το ερώτημα k κοντινότερων γειτόνων με τη βοήθεια του indexfile
     * Επιστρέφει τους k κοντινότερους γείτονες από το σημείο middle
     * Στη minHeap περιέχονται οι κόμβοι που μπορεί να χρειαστεί να ψάξουμε για τους πιθανούς γείτονες
     * Στην κορυφή του minHeap αποθηκεύουμε την περιοχή που είναι πιο κοντά στο σημείο middle
     * Στη maxHeap περιέχονται οι k κοντινότεροι γείτονες
     * στην κορυφή της στοίβας αποθηκεύουμε την περιοχή που ενώ ανήκει στους k κοντινότερους γείτονες βρίσκεται πιο μακριά από την middle
     * @param middle σημείο από το οποίο ψάχνουμε τους γείτονες
     * @param k αριθμός από κοντινότερους γείτονες
     * */
    /*
    public ArrayList<Location> knn_with_index(Point middle, int k) {
        //Arraylist για τα σημεία που θα προκύψουν από το maxHeap
        ArrayList<Point> knn_points = new ArrayList<>();
        //διαβάζει τα δεδομένα από το datafile
        LoadData data=new LoadData();
        MinHeap minHeap = new MinHeap(100);
        MaxHeap maxHeap = new MaxHeap(k);
        //βάζουμε στη minheap τα παιδιά της ρίζας
        for (NodeOfTree node : root.getChildren()) {
            node.setDistance_from_point(node.getRectangle().find_distance_between_point_and_Rectangle(middle));
            minHeap.insert_to_minHeap(node);
        }

        //όσο η minheap δεν είναι άδεια
        while (!minHeap.isEmpty()) {
            //βγαίνει το στοιχείο που βρίσκεται πιο πάνω στη σωρό
            NodeOfTree pop = minHeap.remove();
            //ψάξε το παιδί του κόμβου που βγήκε του οποίου το rectangle είναι πιο κοντά στο σημείο middle
            for (NodeOfTree child : pop.getChildren()) {
                child.setDistance_from_point(child.getRectangle().find_distance_between_point_and_Rectangle(middle));
                //αν το παιδί δεν είναι φύλλο και έχουμε λιγότερους από k γείτονες τότε βάζουμε τον κόμβο στο minHeap
                if (!child.isLeaf() && (maxHeap.getSize() < k))
                    minHeap.insert_to_minHeap(child);
                    //αν το παιδί δεν είναι φύλλο και έχουμε k γείτονες τότε
                    // πρέπει να κρατήσουμε του κοντινότερους και αν χρειαστεί να αλλάξουμε το maxHeap (του γείτονες δηλαδή)
                else if (!child.isLeaf()) {
                    if (maxHeap.getMax().getDistance_point() >= child.getDistance_from_point())
                        minHeap.insert_to_minHeap(child);
                }
                //αν το παιδί είναι φύλλο τότε πρόσθεσε τα σημεία του στο maxHeap
                else {
                    ArrayList<Point> points = child.getPoints();
                    for (Point point : points) {
                        point.setDistance_point(point.find_distance_from_point(middle));
                        maxHeap.insert_to_maxHeap(point);
                    }
                }
            }
        }
        //βάζουμε στο knn_points τα σημεία που έχουν προκύψει από το maxHeap
        for (int i = 0; i < k; i++) {
            knn_points.add(maxHeap.extractMax());
        }
        //δημιουργία ArrayList για τις τοποθεσίες των σημείων από το knn_points
        ArrayList<Location> knn_results=new ArrayList<>();
        for(int i=0;i<knn_points.size();i++){
            Location neighbor=data.find_block(knn_points.get(i).getBlockid(),knn_points.get(i).getSlotid());
            neighbor.setDistance(knn_points.get(i).getDistance_point());
            knn_results.add(neighbor);
        }
        return knn_results;
    }*/




    /**
     * Συνάρτηση που διαγράφει μια τοποθεσία από το δέντρο
     * @param id το id του σημείου που θέλουμε να διαγράψουμε
     * @param locations οι τοποθεσίες όλων των σημείων
     */
/*
    public void delete_from_tree(long id, ArrayList<Location> locations)
    {
        //κόμβος parent ο οποιος ειναι κενος
        NodeOfTree parent = null;
        //λίστα με όλους τους κόμβους του δέντρου
        ArrayList<NodeOfTree> temptree = new ArrayList<>();
        temptree.add(getRoot()); //μεσα στη λιστα που εφτιαξα βαζω την ριζα
        boolean flag = true;
        while (flag){
            for(NodeOfTree r : temptree) { //για καθε κομβο r που ειναι αποθηκευμενος στη λιστα temptree
                if (AllLeaf(temptree) || temptree.isEmpty()) //αν ολα ειναι φυλλα ή το temptree είναι άδειο
                {
                    flag = false;
                    break; //βγες απο την while
                }
                //ελέγχω αν ο κομβος r είναι φύλλο
                if (r.isLeaf()) {
                    //κρατάμε τον γονιό του κόμβου
                    parent = new NodeOfTree(r.getRectangle().getX1(),r.getRectangle().getX2(),r.getRectangle().getY1(),r.getRectangle().getY2()); //αρχικοποιει τον κομβο
                    Data data = new Data(); //φτιαχνει ενα data τυπου LoadData
                    ArrayList<Point> point = new ArrayList<>(r.getPoints()); // φτιαχνει μια λιστα με σημεια του κομβου r
                    //παίρνουμε όλα τα στοιχεία που είναι στον κόμβο
                    // βρίσκουμε τις τοποθεσίες των σημείων του
                    for (Point p : point) { //διασχιζει τα σημεια μεσα στον κομβο
                        Location lc = data.find_block(p.getBlockid(), p.getSlotid());
                        long check = lc.getLocationid(); //παιρνω το id της τοποθεσιας και το βαζω σε μια μεταβλητη
                        //αν κάποιο από τα σημεία έχει ίδιο id με αυτό που δίνεται ως όρισμα τότε
                        if (check == id) {

                            flag = false;
                            //δημιουργία προσωρινού δέντρου και αφαίρεση από τη λίστα το σημείο αυτό αφαιρώντας την τοποθεσία του
                            ArrayList<Point> temp = parent.getPoints();
                            ArrayList<Location> tempLoc = new ArrayList<>(locations);
                            for (Location l : tempLoc) {
                                if (l.getLocationid() == check) {
                                    locations.remove(l);
                                }
                            }
                            parent.setPoints(temp);  //clear και αρχικοποιηση
                            //έλεγχος πληρότητας:
                            //αν ο κόμβος είναι λιγότερο από το 50% γεμάτος με σημεία τότε
                            // θα πρέπει να γίνει η αναδιαμόρφωση στο δέντρο
                            if (parent.getPoints().size() <= 3) { //γιατι το max ειναι 5
                                temp = parent.getPoints();
                                parent.setPoints(new ArrayList<>());
                                //διαγραφή των τοποθεσιών που έμειναν ορφανές
                                for (Point t : temp) { //για καθε σημειο που βρισκεται μεσα στο temp
                                    for (Location l : tempLoc) { //για καθε τοποθεσια που βρισκεται το tempLoc
                                        if (l.getLocationid() == data.find_block(t.getBlockid(), t.getSlotid()).getLocationid())
                                            locations.remove(l);
                                    }
                                    //εισαγωγή των ορφανών τοποθεσιών στο δέντρο
                                    Insertion lOSM = new Insertion();
                                    lOSM.add_location(data.find_block(t.getBlockid(), t.getSlotid()), data.Read_Data());
                                }

                            }
                        }
                    }

                }
                parent = r;
            }
            temptree.remove(parent);
            temptree.addAll(parent.getChildren());

        }
    }*/

}
