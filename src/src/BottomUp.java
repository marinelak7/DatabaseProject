import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Αυτή η κλάση υλοποιεί την μαζική κατασκεύη δένδρου - Bottom up
 */
public class BottomUp {
    //λίστα με φύλλα
    private ArrayList<Point> points;
    //μέγιστος αριθμός θέσεων σε κάθε κόμβο
    private final int max = 5;

    /**
     * Κατασκευαστής της κλάσης BottomUp
     */
    public BottomUp()
    {
        points = new ArrayList<>();
    }

    /**
     * Συνάρτηση που παίρνει όλα τα σημεία και τα ταξινομεί και μετά τα χωρίζει σε ομάδες των 5 σημείων
     * τις βάζει σε κουτάκια
     * @throws Exception
     */
    public void build() throws Exception {
        //διαβάζουμε τον χάρτη
        load();
        //γίνεται ταξινόμηση των σημείων ως προς x
        Collections.sort(points, Comparator.comparing(Point::getLat));
        //λίστα που προσθέτουμε τα μπλοκ
        ArrayList<Point> block = new ArrayList<>();
        //χτίζουμε το δέντρο
        ArrayList <NodeOfTree> rsn = new ArrayList<>();
        //μεταβλητές για να κρατήσουμε την πάνω και την κάτω γωνία για το ορθογώνιο
        double x1,x2,y1,y2;
        for (int i = 0; i< points.size(); i++)
        {
            //προσθέτουμε σημεία στο μπλοκ μέχρι να φράσουμε τα 5
            block.add(points.get(i));
            //αν το πλήθος των σημείων φτάσει τα 5 τότε
            //σταματάει και πηγαίνει και βρίσκει την πάνω δεξιά και κάτω αριστερή γωνία
            if(i % max == 0) {
                //μικρότερο σημείο στον άξονα x (το παίρνουμε από το σημείο που μπήκε πρώτο στη λίστα)
                x1 = block.get(0).getLat();
                //μεγαλύτερο σημείο στον άξονα x (το παίρνουμε από το σημείο που μπήκε τελευταίο στη λίστα)
                x2 = block.get(block.size()-1).getLat();
                //πρέπει να βρούμε το min max
                y1 = block.stream().min(Comparator.comparing(Point::getLon)).get().getLon();
                y2 = block.stream().max(Comparator.comparing(Point::getLon)).get().getLon();
                //προσθήκη του κόμβου στο δέντρο
                rsn.add(new NodeOfTree(x1,x2,y1,y2));
                rsn.get(rsn.size()-1).add_children_nodes(block);
                //αδειάζει το μπλοκ για να μπουν τα επόμενα 5 σημεία
                block = new ArrayList<>();
            }
        }
        //κρατάει το μέγεθος του αντιγράφου
        //σταματάει μόλις γεμίσει το συγκεκριμένο επίπεδο στο οποίο βρισκόμασταν
        int count = rsn.size();
        ArrayList<NodeOfTree> temp = new ArrayList<>(rsn);
        //υπολογίζεται το ταβάνι της διαίρεσης μεταξύ του μεγέθους του δέντρου
        //και του upper_limit
        count = (int)Math.ceil((double)count/ max);
        //χτίσιμο συγκεκριμένου επιπέδου
        for (int i = 0; i < count; i++) {
            //σε περίπτωση που υπάρχουν λιγότερο από 5 στοιχεία στο μπλοκ θα τρέχει
            int test = Math.min(temp.size(),5);
            double y = Double.MAX_VALUE,yy = Double.MIN_VALUE;
            //υπολογίζω μεγαλύτερο και μικρότερο y
            for (int j = 0; j <test; j++) {
                if(temp.get(j).getRectangle().getY1() < y)
                    y = temp.get(j).getRectangle().getY1();
                if(temp.get(j).getRectangle().getY2()> yy)
                    yy = temp.get(j).getRectangle().getY2();
            }
            //προσθέτω τα τετράγωνα στο δέντρο
            rsn.add(new NodeOfTree(temp.get(0).getRectangle().getX1(),temp.get(Math.min(temp.size()-1,4)).getRectangle().getX2(),y,yy));
            for (int j = 0; j <test; j++) {
                rsn.get(rsn.size()-1).add_new_child_node(temp.get(j));
            }
            //άδειασμα στοιχείων
            for (int j = 0; j <test; j++) {
                temp.remove(0);
            }

        }
        //χτίσιμο ολόκληρου δέντρου
        while (count > 1) // mexri na ktisti olo
        {
            temp = new ArrayList<>();
            int test = Math.min(count, 5);
            count = (int)Math.ceil((double)count/ max);
            for (int i = 0; i < count; i++) {

                temp = new ArrayList<>();
                for (int k = 0; k <test ; k++) {
                    temp.add(rsn.get(rsn.size() -1 -k));
                }
                //υπολογισμός μεγαλύτερου και μικρότερου y
                double y = Double.MAX_VALUE, yy = Double.MIN_VALUE;
                for (int j = 0; j < temp.size(); j++) {
                    if (temp.get(j).getRectangle().getY1() < y)
                        y = temp.get(j).getRectangle().getY1();
                    if (temp.get(j).getRectangle().getY2() > yy)
                        yy = temp.get(j).getRectangle().getY2();
                }
                //προσθήκη στο δέντρο
                rsn.add(new NodeOfTree(temp.get(0).getRectangle().getX1(), temp.get(temp.size() - 1).getRectangle().getX2(), y, yy));

                for (int j = 0; j < temp.size(); j++)
                    rsn.get(rsn.size() - 1).add_new_child_node(temp.get(j));

            }

        }
        //υπολογισμός μεγαλύτερου και μικρότερου y
        double y = Double.MAX_VALUE,yy = Double.MIN_VALUE;
        for (NodeOfTree rstarNode : temp) {
            if (rstarNode.getRectangle().getY1() < y)
                y = rstarNode.getRectangle().getY1();
            if (rstarNode.getRectangle().getY2() > yy)
                yy = rstarNode.getRectangle().getY2();
        }
        //προσθήκη στη ρίζα
        NodeOfTree root =new NodeOfTree(temp.get(0).getRectangle().getX1(),temp.get(temp.size()-1).getRectangle().getX2(),y,yy);
        for (int i = 0; i < temp.size(); i++) {
            root.add_new_child_node(temp.get(i));
        }
    }

    /**
     * Φορτώνει τα δεδομένα απο το .osm αρχείο και παίρνει ολα τα σημεία που έχει μέσα και τα βάζει στη λίστα points
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    private void load() throws IOException, SAXException, ParserConfigurationException {
        //δημιουργία document builder -> αναλυση εγγραφων xml
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); //Δημιουργήστε ένα στιγμιότυπο του `DocumentBuilderFactory` χρησιμοποιώντας τη στατική μέθοδο `newInstance()`.
        DocumentBuilder builder = factory.newDocumentBuilder(); //Δημιουργία του `DocumentBuilder` στιγμιότυπο χρησιμοποιώντας την μέθοδο `newDocumentBuilder()`.


        Document document = builder.parse(new File("map2.osm")); //Χρησιμοποιήστε το αντικείμενο `DocumentBuilder` για να αναλύσετε έγγραφα XML.

        //επιστρέφει το ριζικό στοιχείο του εγγράφου XML
        document.getDocumentElement().normalize(); //διαδικασία κανονικοποίησης της δομής του εγγράφου XML

        //ρίζα
        Element root = document.getDocumentElement();

        //δημιουργία node list
        NodeList nList = document.getElementsByTagName("node"); //αναζητά στο έγγραφο XML στοιχεία με το καθορισμένο όνομα ετικέτας = node

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp); //ανάκτηση ενός κόμβου από τη λίστα σε μια συγκεκριμένη θέση.

            if (node.getNodeType() == Node.ELEMENT_NODE) { //αποτιμάται σε true αν ο τύπος του κόμβου είναι κόμβος στοιχείου, και false διαφορετικά.
                //εμφάνιση κάθε σημείου
                Element eElement = (Element) node;
                double latitude = Double.parseDouble(eElement.getAttribute("lat"));
                double longitude = Double.parseDouble(eElement.getAttribute("lon"));
                points.add(new Point(latitude,longitude));

            }

        }
    }

}
