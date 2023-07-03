import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/**
 * Η κλάση αυτή υλοποιεί ερωτήματα περιοχής και κοντινότερων γειτόνων χωρίς index
 */
public class RangeQuery {



    /*
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
    }*/
    /**
     * Ερωτήματα περιοχής με σειριακή αναζήτηση χωρίς χρήση καταλόγου
     * @param center
     * @param radius
     * @param locations
     * @return λίστα με locations
     */
    public static ArrayList<Location> range_query_without_index ( Location center , double radius , ArrayList<Location> locations){

        ArrayList<Location> locationsInRange = new ArrayList<>(); //Φτιάχνω μια λίστα με τα locations που βρίσκονται μέσα στον κύκλο γύρω απο το στοιχειο center

        for (Location location : locations) { //Για κάθε location στη λίστα με τα locations εφάρμοσε σειριακή αναζήτηση
            if (isWithinRadius(location, center, radius)) { //Αν η τοποθεσία βρίσκεται εντός της ακτίνας πρόσθεσε την στην λίστα
                locationsInRange.add(location);
            }
        }

        return locationsInRange;
    }

    /**
     * Συνάρτηση που υπολογίζει την απόσταση μεταξύ δυο σημείων και την συγκρίνει με την ακτίνα
     * Αν είναι μικρότερη ή ίση επιστρέφει true αλλιώς false
     * @param location
     * @param center
     * @param radius
     */
    private static boolean isWithinRadius(Location location, Location center, double radius) {

        double manhattanDistance = location.find_manhattan_distance_between_two_points(center.getLat(), center.getLon());

        return manhattanDistance <= radius; //boolean
    }

    /**
     * Ερωτήματα περιοχής με χρήση του καταλόγου indexfile
     * @param middle
     * @param radius
     * @param tree
     */
    public ArrayList<Location> range_query_with_index(Point middle, double radius , Tree tree) {
        ArrayList<Location> rangeResults = new ArrayList<>();
        ArrayList<Point> points = new ArrayList<>();
        Data data = new Data(); //διαβάζω απο το datafile

        //Αν η απόσταση ρίζας από το σημείο middle <= ακτίνα
        if (tree.getRoot().getRectangle().find_distance_between_point_and_Rectangle(middle) <= radius) {
            Stack<NodeOfTree> dfs = new Stack<>(); //Φτιάξε στοίβα
            dfs.push(tree.getRoot()); //βάλε μέσα την ρίζα

            while (!dfs.empty()) { //όσο η στοίβα δεν είναι άδεια
                NodeOfTree node = dfs.pop(); //πάρε το πρώτο στοιχείο της στοίβας

                for (int i = 0; i < node.getChildren().size(); i++) { //Για κάθε παιδί του κόμβου
                    if (node.getChildren().get(i).isLeaf()) { //Εαν το παιδί του κόμβου που είχαμε κάνει pop είναι φύλλο
                        processLeafNode(node.getChildren().get(i), middle, radius, points);
                    } else { //Εαν το παιδί του κόμβου που είχαμε κάνει pop δεν είναι φύλλο
                        processNonLeafNode(node.getChildren().get(i), middle, radius, dfs);
                    }
                }
            }
        }

        for (int i = 0; i < points.size(); i++) { //Διατρέχουμε την λίστα με τα Points
            //Βάλε στην λίστα με τα results τα locations που πληρούν τις προυποθέσεις
            Location loc = data.find_block(points.get(i).getBlockid(), points.get(i).getSlotid());
            loc.find_manhattan_distance_between_two_points(middle.getLat(), middle.getLon());
            rangeResults.add(loc);
        }

        return rangeResults;
    }

    /**
     * Συνάρτηση που γεμίζει την λίστα points αν η απόσταση των σημείων του φύλλου είναι μικρότερη ή ίση της ακτίνας
     * @param node
     * @param middle
     * @param radius
     * @param points
     */
    private void processLeafNode(NodeOfTree node, Point middle, double radius, ArrayList<Point> points) {
        for (int k = 0; k < node.getPoints().size(); k++) { //Για κάθε σημείο του φύλλου
            if (node.getPoints().get(k).find_distance_from_point(middle) <= radius) { //Αν η απόσταση των σημείων του φύλλου είναι <= ακτίνα
                points.add(node.getPoints().get(k)); //Βάλε τα σημεία αυτά στη λίστα
            }
        }
    }

    /**
     * Συνάρτηση που γεμίζει την στόιβα με παιδιά αν η απόσταση των ορθογωνίων των παιδιών είναι μικρότερη ή ίση της ακτίνας
     * @param node
     * @param middle
     * @param radius
     * @param dfs
     */
    private void processNonLeafNode(NodeOfTree node, Point middle, double radius, Stack<NodeOfTree> dfs) {
        if (node.getRectangle().find_distance_between_point_and_Rectangle(middle) <= radius) { //Αν η απόσταση των ορθογωνίων των παιδιών <= ακτίνα
            dfs.push(node); //Βάλε στη στοίβα το παιδί
        }
    }



}
