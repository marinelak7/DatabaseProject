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




}
