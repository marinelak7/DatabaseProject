import java.util.ArrayList;
import java.util.Collections;

public class KNNQuery {

    public ArrayList<Location> knn_without_index(ArrayList<Location> locations, Location middle, int k) {
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
    }


}
