import java.util.ArrayList;
import java.util.Collections;

public class KNNQuery {

    /*public ArrayList<Location> knn_without_index(ArrayList<Location> locations, Location middle, int k) {
        //ArrayList για τις τοποθεσίες που βρίσκονται οι k κοντινότεροι γείτονες
        ArrayList<Location> k_neighbors = new ArrayList<>();
        //υπολογισμός απόστασης μεταξύ της middle τοποθεσίας και όλων των υπόλοιπων τοποθεσιών (brute force)
        for (Location location : locations)
            location.find_manhattan_distance_between_two_points(middle.getLat(), middle.getLon());
        //ταξινόμηση των locations σύμφωνα με την απόσταση που απέχουν από τη middle
        Collections.sort(locations);
        //αποθήκευση εμφάνιση των k κοντινότερων γειτόνων στο k_neighbors
        for (int i = 0; i < k; i++) {
            k_neighbors.add(locations.get(i));
            Point k_distance = new Point(locations.get(i).getLat(), locations.get(i).getLon());
            k_distance.setDistance_point(k_distance.find_distance_from_point(new Point(middle.getLat(), middle.getLon())));
        }
        return k_neighbors;
    }*/
    /**
     * Σειριακή αναζήτηση για το ερώτημα κοντινότερων γειτόνων
     *
     * @param locations λιστα με τις τοποθεσίες
     * @param center    τοποθεσία από την οποία ψάχνουμε τους knn κοντινότερους γείτονες
     * @param knn       ο αριθμός επιθυμητών γειτόνων
     * @return την λίστα με τους knn γείτονες από τη center τοποθεσία
     */
    public ArrayList<Location> knn_without_index(ArrayList<Location> locations, Location center, int knn) {
        ArrayList<Location> neighbors = new ArrayList<>();  // λίστα με γείτονες

        for (Location l : locations) //για κάθε location
            l.find_manhattan_distance_between_two_points(center.getLat(), center.getLon()); //συγκρίνουμε την απόσταση Manhattan ανάμεσα στο center και τις υπόλοιπες τοποθεσίες

        Collections.sort(locations); //Εδώ κάνουμε ταξινόμηση των locations με βάση την απόσταση που συγκρίναμε προηγουμένος , ο τρόπος με τον οποίο υλοποιείται είναι μέσω της συνάρτησης compareTo που βρίσκεται στη κλάση Location

        for (int i = 0; i < knn; i++) { //Για κάθε επιθυμητό γείτονα
            neighbors.add(locations.get(i)); //βάλτο στη λίστα
            Point distance = new Point(locations.get(i).getLat(), locations.get(i).getLon()); //όρισε ένα καινούριο σημείο
            distance.setDistance_point(distance.find_distance_from_point(new Point(center.getLat(), center.getLon()))); //και κάνε set την νεα απόσταση
        }

        return neighbors; //επέστρεψε τους knn κοντινότερους γείτονες

    }
/*
   public ArrayList<Location> knn_with_index(Point middle, int k , Tree tree) {
        //Arraylist για τα σημεία που θα προκύψουν από το maxHeap
        ArrayList<Point> knn_points = new ArrayList<>();
        //διαβάζει τα δεδομένα από το datafile
        Data data=new Data();
        MinHeap minHeap = new MinHeap(100);
        MaxHeap maxHeap = new MaxHeap(k);
        //βάζουμε στη minheap τα παιδιά της ρίζας
        for (NodeOfTree node : tree.getRoot().getChildren()) {
            node.setDistance_from_point(node.getRectangle().find_distance_between_point_and_Rectangle(middle));
            minHeap.insert_to_minHeap(node);
        }

        //όσο η minheap δεν είναι άδεια
        while (!minHeap.MINHeap_Empty()) {
            //βγαίνει το στοιχείο που βρίσκεται πιο πάνω στη σωρό
            NodeOfTree pop = minHeap.remove_from_heap();
            //ψάξε το παιδί του κόμβου που βγήκε του οποίου το rectangle είναι πιο κοντά στο σημείο middle
            for (NodeOfTree child : pop.getChildren()) {
                child.setDistance_from_point(child.getRectangle().find_distance_between_point_and_Rectangle(middle));
                //αν το παιδί δεν είναι φύλλο και έχουμε λιγότερους από k γείτονες τότε βάζουμε τον κόμβο στο minHeap
                if (!child.isLeaf() && (maxHeap.getCount() < k))
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
            knn_points.add(maxHeap.popMax());
        }
        //δημιουργία ArrayList για τις τοποθεσίες των σημείων από το knn_points
        ArrayList<Location> knn_results=new ArrayList<>();
        for(int i=0;i<knn_points.size();i++){
            Location neighbor=data.find_block(knn_points.get(i).getBlockid(),knn_points.get(i).getSlotid());
            neighbor.setDistance(knn_points.get(i).getDistance_point());
            knn_results.add(neighbor);
        }
        return knn_results;
    }
*/

    /**
     * Υλοποίηση ερωτήματος κοντινότερων γειτόνων με χρήση καταλόγου
     * @param middle σημείο
     * @param k ο αριθμός επιθυμητών γειτόνων
     * @param tree
     *  @return την λίστα με τους knn γείτονες
     */
    public ArrayList<Location> knn_with_index(Point middle, int k, Tree tree) {

        ArrayList<Point> points_knn = new ArrayList<>();
        Data data = new Data();
        MinHeap minheap = new MinHeap(100);
        MaxHeap maxheap = new MaxHeap(k);

        //Για καθε παιδι της ριζας
        for (NodeOfTree node : tree.getRoot().getChildren()) {
            //βαζουμε στην minheap τα παιδια της ριζας
            double distance = node.getRectangle().find_distance_between_point_and_Rectangle(middle);
            node.setDistance_from_point(distance);
            minheap.insert_to_minHeap(node);
        }

        while (!minheap.MINHeap_Empty()) { //οσο η minheap δεν ειναι αδεια
            NodeOfTree popped = minheap.remove_from_heap(); //βγαινει το στοιχειο που ειναι πιο πανω στη σωρο
            for (NodeOfTree children : popped.getChildren()) { //καθε παιδί του κόμβου που βγήκε
                children.setDistance_from_point(children.getRectangle().find_distance_between_point_and_Rectangle(middle)); //του οποιου το ορθογωνιο ειναι πιο κοντα στο middle
                if (!children.isLeaf()) { //εαν το παιδί του κομβου που βγηκε δεν ειναι φυλλο
                    if (maxheap.getCount() < k) { //εαν δεν εχει γεμισει το maxheap
                        minheap.insert_to_minHeap(children); //βαζουμε το παιδι στο minheap
                    }
                } else if (!children.isLeaf() && maxheap.getMax().getDistance_point() >= children.getDistance_from_point()) { //εαν το παιδι του κομβου που βγηκε δεν ειναι φυλλο
                    //και εχουμε k γειτονες , τοτε πρεπει να κρατησουμε τους κοντινοτερους
                    minheap.insert_to_minHeap(children);
                } else { //αν το παιδι ειναι φυλλο
                    //προσθεσε τα σημεια του στο maxheap
                    ArrayList<Point> max_heap_points = children.getPoints();
                    for (Point point : max_heap_points) {
                        point.setDistance_point(point.find_distance_from_point(middle));
                        maxheap.insert_to_maxHeap(point);
                    }

                }


            }


        }

        for (int i=0; i<k ; i++ ){ //βαζουμε στη λιστα με knn points τα σημεια που εχουν γινει εξτρακτ απτο maxheap
            points_knn.add(maxheap.popMax()) ;
        }
        ArrayList<Location> knn_query_results = new ArrayList<>() ; //τοποθεσιες των σημειων των knn points
        for (int i=0; i<points_knn.size(); i++){
            Location n = data.find_block(points_knn.get(i).getBlockid() , points_knn.get(i).getSlotid() );
            n.setDistance(points_knn.get(i).getDistance_point());
            knn_query_results.add(n);
        }
        return knn_query_results;

    }

}





