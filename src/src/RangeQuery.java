import java.util.ArrayList;

public class RangeQuery {

    /**
     * Συνάρτηση που υλοποιεί σειριακά το ερώτημα περιοχής
     * Επιστρέφει τους γείτονες που βρίσκονται μέσα στον "κύκλο" γύρω από το στοιχείο middle
     * @param middle η τοποθεσία από την οποία μετράμε την ακτίνα
     * @param radius η ακτίνα εντός της οποίας ψάχνουμε τις τοποθεσίες
     * @param locations όλες οι τοποθεσίες
     */
    public static ArrayList<Location> range_query_without_index(Location middle, double radius, ArrayList<Location> locations) {
        //ArrayList για τις τοποθεσίες που βρίσκονται οι γείτονες εντός της ακτίνας
        ArrayList<Location> locations_in_range = new ArrayList<>();
        //αναζήτηση τοποθεσιών που είναι εντός της ακτίνας
        for (int i = 0; i < locations.size(); i++) {
            //όσες αποστάσεις manhattan μεταξύ του middle και των υπόλοιπων τοποθεσιών βρίσκονται εντός της ακτίνας
            // τότε πρόσθεσε τις τοποθεσίες αυτές στο locations_in_range (brute force)
            if (locations.get(i).find_manhattan_distance_between_two_points(middle.getLat(), middle.getLon()) <= radius)
                locations_in_range.add(locations.get(i));
        }
        return locations_in_range;
    }



}
