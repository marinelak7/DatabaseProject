import java.util.Comparator;
import java.io.Serializable;

/**
 * Η κλάση αυτή αναπαριστά ένα σημείο σε έναν δισδιάστατο χώρο
 */
public class Point implements Serializable {
    //x
    private double lat;
    //y
    private double lon;
    //σε ποιο μπλοκ του datafile έχει αποθηκευτεί το σημείο
    private int blockid;
    //σε ποιο slot του μπλοκ του datafile έχει αποθηκευτεί το σημείο
    private int slotid;
    //απόσταση σημείου από ορθογώνιο
    private double distance;

    /**
     * Κατασκευαστής της κλάσης Point με 4 ορίσματα
     * @param lat η παράμετρος x του σημείου
     * @param lon η παράμετρος y του σημείου
     * @param blockid το block του datafile στο οποίο είναι αποθηκευμένο το σημείο
     * @param slotid το slot του blockid του datafile στο οποίο έχε αποθηκευτεί το σημείο
     */
    public Point(double lat, double lon , int blockid, int slotid){
        this.lat=lat;
        this.lon=lon;
        this.blockid =blockid;
        this.slotid =slotid;

    }

    /**
     * Κατασκευαστής της κλάσης Point με 2 ορίσματα
     * @param lat η παράμετρος x του σημείου
     * @param lon η παράμετρος y του σημείου
     */
    public Point(double lat, double lon){
        this.lat=lat;
        this.lon=lon;
    }

    /**
     * Getter για το blockid
     */
    public int getBlockid(){
        return blockid;
    }

    /**
     * Getter για το slotid
     */
    public int getSlotid(){
        return slotid;
    }

    /**
     * Getter για το lon
     */
    public double getLon(){
        return lon;
    }

    /**
     * Getter για το lan
     */
    public double getLat(){
        return lat;
    }

    /**
     * Getter για το distance
     */
    public double getDistance_point() {
        return distance;
    }

    /**
     * Setter για το distance
     * @param distance η απόσταση ενός σημείου από ένα ορθογώνιο
     */
    public void setDistance_point(double distance) {
        this.distance = distance;
    }

    /**
     * Συνάρτηση που υπολογίζει την απόσταση Manhattan μεταξύ δύο σημείων
     * @param second_point το δεύτερο σημείο από το οποίο θέλουμε να μετρήσουμε πόσο απέχει το πρώτο σημείο
     */
    public double find_distance_from_point(Point second_point){
        return Math.abs(lat-second_point.getLat())+Math.abs(lon-second_point.getLon());
    }

    /**
     * Συνάρτηση που συγκρίνει τις τιμές lat δύο σημείων
     */
    static class PointsComparatorX implements Comparator<Point> {
        @Override
        public int compare(Point point1, Point point2) {
            return Double.compare(point2.getLat(), point1.getLat());
        }
    }

    /**
     * Συνάρτηση που συγκρίνει τις τιμές lon δύο σημείων
     */
    static class PointsComparatorY implements Comparator<Point> {
        @Override
        public int compare(Point point1, Point point2) {
            return Double.compare(point2.getLon(), point1.getLon());
        }
    }



}
