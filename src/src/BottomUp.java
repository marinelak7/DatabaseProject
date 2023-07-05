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
    public void mass_build() throws Exception {
        //διαβάζουμε τον χάρτη
        load();
        //γίνεται ταξινόμηση των σημείων ως προς lat
        Collections.sort(points, Comparator.comparing(Point::getLat));

        ArrayList<Point> blocks = new ArrayList<>(); //λίστα που προσθέτουμε τα μπλοκ

        ArrayList <NodeOfTree> temp_nodes = new ArrayList<>(); //λίστα που κρατάμε τους κόμβους

        double x1,x2,y1,y2; //χαρακτηριστικά ορθογωνίου
        for (int i = 0; i< points.size(); i++)
        {
            //προσθέτουμε σημεία στο μπλοκ μέχρι να φτάσουμε τα 5 , αφού έχουμε 5 θέσεις το μέγιστο σε κάθε κόμβο
            blocks.add(points.get(i));

            //αν το πλήθος των σημείων φτάσει τα 5 τότε
            if(i % max == 0) {

                x1 = blocks.get(0).getLat();  //μικρότερο σημείο στον άξονα x -> το σημείο που μπήκε πρώτο στη λίστα

                int blocks_size = blocks.size()-1;
                x2 = blocks.get(blocks_size).getLat(); //μεγαλύτερο σημείο στον άξονα x -> το σημείο που μπήκε τελευταίο στη λίστα

                //Υλογίζουμε το min και max στον άξονα y
                y1 = blocks.stream().min(Comparator.comparing(Point::getLon)).get().getLon();
                y2 = blocks.stream().max(Comparator.comparing(Point::getLon)).get().getLon();


                //προσθήκη του κόμβου
                temp_nodes.add(new NodeOfTree(x1,x2,y1,y2));
                int nodes_size = temp_nodes.size() -1 ;
                temp_nodes.get(nodes_size).add_children_nodes(blocks);

                //αδειάζει το μπλοκ για να μπουν τα επόμενα 5 σημεία
                blocks = new ArrayList<>();
                //και επαναλαμβάνεται η διαδικασία μέχρις ότου μπουν όλα τα σημεία στους κόμβους-φύλλα
            }
        }

        //κρατάει το μέγεθος του αντιγράφου
        //σταματάει μόλις γεμίσει το συγκεκριμένο επίπεδο στο οποίο βρισκόμασταν
        int count = temp_nodes.size();
        ArrayList<NodeOfTree> temp = new ArrayList<>(temp_nodes);

        count = (int)Math.ceil((double)count/ max); //υπολογίζουμε πόσους κόμβους θα έχουμε με βάση το ταβάνι max=5 που είναι τα points που χωράνε σε έναν κόμβο

        for (int i = 0; i < count; i++) {

            int test = Math.min(temp.size(),5); //πόσα στοιχεία βρίσκονται στον κόμβο ;
            double y = Double.MAX_VALUE;
            double _y = Double.MIN_VALUE;
            //υπολογίζω μεγαλύτερο και μικρότερο y
            for (int j = 0; j <test; j++) {

                calculate_min_max_y(j,y,_y,temp);
            }
            //προσθέτω τα τετράγωνα στο δέντρο
            temp_nodes.add(new NodeOfTree(temp.get(0).getRectangle().getX1(),temp.get(Math.min(temp.size()-1,4)).getRectangle().getX2(),y,_y));

            for (int j = 0; j <test; j++) {
                temp_nodes.get(temp_nodes.size()-1).add_new_child_node(temp.get(j));
            }
            //άδειασμα στοιχείων
            for (int j = 0; j <test; j++) {
                temp.remove(0);
            }

        }


        while (count > 1) // όσο υπάρχει διαθέσιμος κόμβος -> χτίσε το δένδρο
        {
            temp = new ArrayList<>();
            int test = Math.min(count, 5);
            count = (int)Math.ceil((double)count/ max);

            for (int i = 0; i < count; i++) {

                temp = new ArrayList<>();
                for (int k = 0; k <test ; k++) {
                    temp.add(temp_nodes.get(temp_nodes.size() -1 -k));
                }
                //υπολογισμός μεγαλύτερο και μικρότερο y
                double y = Double.MAX_VALUE;
                double _y = Double.MIN_VALUE;
                for (int j = 0; j < temp.size(); j++) {
                    calculate_min_max_y(j,y,_y,temp);
                }
                //προσθήκη στο δέντρο
                temp_nodes.add(new NodeOfTree(temp.get(0).getRectangle().getX1(), temp.get(temp.size() - 1).getRectangle().getX2(), y, _y));

                for (int j = 0; j < temp.size(); j++)
                    temp_nodes.get(temp_nodes.size() - 1).add_new_child_node(temp.get(j));

            }

        }

        //υπολογισμός μεγαλύτερου και μικρότερου y
        double y = Double.MAX_VALUE;
        double _y = Double.MIN_VALUE;
        for (NodeOfTree rstarNode : temp) {
            if (rstarNode.getRectangle().getY1() < y)
                y = rstarNode.getRectangle().getY1();
            if (rstarNode.getRectangle().getY2() > _y)
                _y = rstarNode.getRectangle().getY2();
        }
        //προσθήκη στη ρίζα
        NodeOfTree root =new NodeOfTree(temp.get(0).getRectangle().getX1(),temp.get(temp.size()-1).getRectangle().getX2(),y,_y);
        for (int i = 0; i < temp.size(); i++) {
            root.add_new_child_node(temp.get(i));
        }
    }

    /**
     * Υπολογισμός μικρότερου και μεγαλύτερου y
     * @param j
     * @param y
     * @param _y
     * @param temp
     */
    private void calculate_min_max_y ( int j , double y , double _y , ArrayList<NodeOfTree> temp){
            if (temp.get(j).getRectangle().getY1() < y)
                y = temp.get(j).getRectangle().getY1();
            if (temp.get(j).getRectangle().getY2() > _y)
                _y = temp.get(j).getRectangle().getY2();
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
