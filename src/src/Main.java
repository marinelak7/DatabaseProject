import java.util.ArrayList;

public class  Main {

    private static Tree tree = new Tree(new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE));

    public static void main(String[] args) throws Exception {

        ArrayList<Location> locations = null; //Μια λίστα που θα αποθηκεύει τις τοποθεσίες



        //Βήμα 1. Διαβάζουμε το map.osm αρχείο και φτιάχνουμε το datafile και το indexfile
        //datafile -> αποτελείται από blocks μεγέθους 32KB και περιλαμβάνει τις εγγραφές
        //indexfile -> οργανώνει τις εγγραφές που είναι αποθηκευμένες στο datafile

        RWFiles osm = new RWFiles();
        osm.load_osm();

        //---------------------------------------------------------------

        //Βήμα 2. Αφού φτιάξαμε το datafile πρέπει να το διαβάσουμε

        Data data = new Data();
        locations = data.Read_Data(); // <- φορτώθηκαν οι τοποθεσίες του map.osm στην λίστα με τις τοποθεσίες

        //---------------------------------------------------------------

        //Βήμα 3. Ανακτάται το δένδρο από το indexfile
        //Υπολογίζουμε τον χρόνο κατασκευής του καταλόγου με στοιχεία ένα-προς-ένα

        System.out.println("Κατασκευή καταλόγου με στοιχεία ένα-προς-ένα");
        long starting_time= System.currentTimeMillis();
        tree.setTotal_records(data.getAll_entries());
        osm.read_from_index_file(tree);
        long ending_time= System.currentTimeMillis() - starting_time ;
        System.out.println("Χρόνος που χρειάστηκε: " + ending_time + "ms\n");
        System.out.println("!-----------------------------------------------");

        //----------------------------------------------------------------


        //Εδώ ξεκινούν οι απαιτούμενες λειτουργικότητες

        //αρχικοποίηση τιμών για να τρέξει το ερώτημα
        double lat = 35.416235; //x
        double lon = 28.113573; //y
        long id =828871311; //id τοποθεσίας


        //Μηδενίζω κάθε φορά που ξεκινάει νέα λειτουργικότητα τους χρόνους εκκίνησης και τερματισμού
        starting_time = 0;
        ending_time = 0;


        //1. Εισαγωγή -> Δυναμική ενημέρωση για εισαγωγή στο δένδρο

        Location new_location = new Location(id, lat, lon);
        System.out.println("\nΕισάγεται νέα τοποθεσία: " + new_location);
        starting_time = System.currentTimeMillis();
        int size = locations.size();
        Insertion insertion = new Insertion();
        insertion.add_location(new_location , locations);
        Data data2 = new Data();
        locations = data2.Read_Data();
        if(size !=locations.size())
            System.out.println("H τοποθεσία προστέθηκε επιτυχώς.");
        else
            System.out.println("Η τοποθεσία δεν προστέθηκε.");
        ending_time = System.currentTimeMillis() - starting_time;
        System.out.println("Χρόνος που χρειάστηκε: " + ending_time + "ms\n");
        System.out.println("!-----------------------------------------------");

        starting_time = 0;
        ending_time = 0;

        //----------------------------------------------------------------

        //lat = 40.0584068; //x
        //lon = 22.5659899; //y
        //id =970005045; //id τοποθεσίας

        //2. Διαγραφή -> Δυναμική ενημέρωση για διαγραφή από το δένδρο

        Location new_location2 = new Location(id, lat, lon);
        System.out.println("Διαγραφή τοποθεσίας " + new_location2.toString());
        starting_time = System.currentTimeMillis();
        size = locations.size();
        Deletion delete = new Deletion();
        delete.deletion(id,locations,tree);

        if(size !=locations.size())
            System.out.println("Η τοποθεσία διαγράφτηκε επιτυχώς.");
        else
            System.out.println("Η τοποθεσία δεν διαγράφτηκε.");
        ending_time = System.currentTimeMillis() - starting_time;
        System.out.println("Χρόνος που χρειάστηκε: " + ending_time + "ms");
        System.out.println("!-----------------------------------------------");


        starting_time = 0;
        ending_time = 0;

        //----------------------------------------------------------------

        //3. Ερώτημα περιοχής χωρίς χρήση καταλόγου
        double range = 45;
        int count = 0;
        System.out.println("\nΕρώτημα περιοχής χωρίς την χρήση καταλόγου για range=" + range + " lat= " + lat + " lon= " + lon );
        starting_time = System.currentTimeMillis();
      RangeQuery range_query = new RangeQuery();
        range_query = new RangeQuery();
        ArrayList<Location> locations_in_range = range_query.range_query_without_index(new Location(-1, lat, lon), range, locations);
        for (Location neighbor : locations_in_range) {
          //System.out.println(neighbor.toString());
          //count++;
        }
      //System.out.println(count);
        ending_time = System.currentTimeMillis() - starting_time;
        System.out.println("Χρόνος που χρειάστηκε: " + ending_time + "ms");
        System.out.println("!-----------------------------------------------");

        starting_time = 0;
        ending_time = 0;

        //----------------------------------------------------------------


        //4. Ερώτημα περιοχής με την χρήση καταλόγου
        System.out.println("\n Ερώτημα περιοχής με χρήση καταλόγου για range=" + range +" lat=" + lat + " lon=" + lon);
        starting_time = System.currentTimeMillis();
        //ArrayList<Location> range_query_index = tree.range_query_with_index(new Point(lat,lon) , range);
        RangeQuery range_query2 = new RangeQuery();
        ArrayList<Location> range_query_index = range_query2.range_query_with_index(new Point(lat,lon) , range ,tree);
        for (Location neighbor2 : range_query_index){
      //    System.out.println(neighbor2.toString());
        }
        ending_time = System.currentTimeMillis() - starting_time;
        System.out.println("Χρόνος που χρειάστηκε: " + ending_time + "ms");
        System.out.println("!-----------------------------------------------");

        starting_time = 0;
        ending_time = 0;

        //----------------------------------------------------------------

        //5. Ερώτημα πλησιέστερων γειτόνων χωρίς την χρήση καταλόγου
        int k = 5;
        System.out.println("\n Ερώτημα πλησιέστερων γειτόνων χωρίς χρήση καταλόγου για k=" + k +" lat=" + lat + " lon=" + lon);
        starting_time = System.currentTimeMillis();
        KNNQuery knn_query1 = new KNNQuery();
        Location middle = new Location(-1,lat,lon);
        ArrayList<Location> knn_query = knn_query1.knn_without_index(locations , middle , k );
        for (Location neighbor3 : knn_query){
            System.out.println(neighbor3.toString());
        }
        ending_time = System.currentTimeMillis() - starting_time;
        System.out.println("Χρόνος που χρειάστηκε: " + ending_time + "ms");
        System.out.println("!-----------------------------------------------");

        starting_time = 0;
        ending_time = 0;

        //----------------------------------------------------------------

        //6. Ερώτημα πλησιέστερων γειτόνων με την χρήση καταλόγου

        //----------------------------------------------------------------

        //7.Μαζική κατασκευή του δένδρου bottom-up

        System.out.println("\nMαζική κατασκευή του δένδρου bottom-up");
        starting_time = System.currentTimeMillis();
        BottomUp b = new BottomUp();
        b.build();
        ending_time = System.currentTimeMillis() - starting_time;
        System.out.println("Χρόνος που χρειάστηκε: " + ending_time + "ms");
        System.out.println("!-----------------------------------------------");

        starting_time = 0;
        ending_time = 0;














    }

}
