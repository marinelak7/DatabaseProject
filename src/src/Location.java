import static java.lang.Math.abs;

/**
 * Η κλάση αυτή αναπαριστά μια τοποθεσία με lat (x) και lon (y) στο osm αρχείο
 */
public class Location implements Comparable<Location>{
    private long locationid;//id της τοποθεσίας δηλαδή του κάθε κόμβου στο map.osm
    private double lat, lon;//latitude και το longitude του κάθε κόμβου στο map.osm
    private double distance;//απόσταση που απέχει μια τοποθεσία από μια άλλη

    /**
     * Constructor για την κλάση Location
     * @param locationid
     * @param lat
     * @param lon
     */
    public Location(long locationid, double lat, double lon) {
        this.locationid = locationid;
        this.lat = lat;
        this.lon = lon;
        //αρχικοποίηση του distance
        distance = Double.MAX_VALUE;
    }

    /**
     * Getter για lat
     * @return lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * Getter για lon
     * @return lon
     */
    public double getLon() {
        return lon;
    }

    /**
     * Getter για το locationid
     */
    public long getLocationid() {
        return locationid;
    }

    /**
     * Συνάρτηση που υπολογίζει την απόσταση Manhattan μεταξύ 2 σημείων
     * @param lat η παράμετρος x του άλλου σημείου
     * @param lon η παράμετρος y του άλλου σημείου
     */
    public double find_manhattan_distance_between_two_points(double lat, double lon) {
        distance = abs((lat - this.lat)) + abs(lon - this.lon);
        return distance;
    }

    /**
     * Setter για το distance
     */
    public void setDistance(double distance1){
        distance=distance1;
    }

    /**
     * Getter για το distance
     */
    public double getDistance() {
        //εάν η τιμή του distance δεν άλλαξε από αυτήν που αρχικοποιήσαμε
        if (distance == Double.MAX_VALUE) {
            System.out.println("Error with distance");
            System.exit(-1);
        }
        return distance;
    }



    @Override

    public String toString() {
        return "id:" + locationid + "   latitude= " + lat + "   longitude= " + lon+ "  distance= "+distance;
    }

    /**
     * Κάνουμε override τη συνάρτηση compareTo
     * Συνάρτηση που συγκρίνει δυο τοποθεσίες μεταξύ τους με βάση τις τιμές των αποστάσεων
     * @param ο η δεύτερη τοποθεσία
     * @return :
     * 1 αν η απόσταση της πρώτης τοποθεσίας είναι μεγαλύτερη από αυτή της δεύτερης
     *-1 αν η απόσταση της δεύτερης τοποθεσίας είναι μεγαλύτερη από αυτή της πρώτης,
     * 0 αν και οι δυο τοποθεσίες έχουν ίδιες αποστάσεις
     */
    @Override
    public int compareTo(Location ο) {
        if (distance > ο.getDistance()) {
            return 1;
        } else if (distance < ο.getDistance()) {
            return -1;
        }
        return 0;
    }
}
