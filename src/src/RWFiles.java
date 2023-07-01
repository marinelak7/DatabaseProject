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


public class RWFiles {

    public int blockID;

    public RWFiles()  {
        blockID = 0;
    }

    /**
     * Φορτώνει τα δεδομένα απο το .osm αρχείο και δημιουργεί το datafile με τα ήδη υπάρχοντα δεδομένα
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public void load_osm() throws IOException, SAXException, ParserConfigurationException {

        Tree tree = new Tree(new NodeOfTree(Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE));
        //χρησιμοποιείται για την εγγραφή δυαδικών δεδομένων σε ένα αρχείο
        FileOutputStream fos = new FileOutputStream("datafile.txt"); //αρχείο στο οποίο θέλω να γράψω
        DataOutputStream dos = new DataOutputStream(fos); //περιτυλίγει την υπάρχουσα ροή εξόδου fos και παρέχει πρόσθετες μεθόδους για την εγγραφή δεδομένων σε συγκεκριμένες μορφές

        int blockSize = 32 * 1024; // 32KB
        int entriesPerBlock = blockSize / 24; // Κάθε entry είναι 24 bytes

        //τα πρώτα 4 ψηφία είναι το blockid
        dos.writeChar('b');
        dos.writeChar('0');
        dos.writeChar('0');
        dos.writeChar('0');

        dos.writeInt(1234);   //συνολικός αριθμός από μπλοκς
        dos.writeInt(1234);
        dos.writeInt(entriesPerBlock);   //συνολικός αριθμός από εισαγωγές


        int blocks_Number = 1; //αριθμος των blocks
        dos.writeChar('b');
        dos.writeChar('0');
        dos.writeChar('0');
        dos.writeChar((char) blocks_Number);
        int count = 0; //αριθμος εγγραφων

        //δημιουργία document builder -> αναλυση εγγραφων xml
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); //Obtain an instance of `DocumentBuilderFactory` using the static `newInstance()` method.
        DocumentBuilder builder = factory.newDocumentBuilder(); //Create a `DocumentBuilder` instance from the factory using the `newDocumentBuilder()` method.
        Document document = builder.parse(new File("map.osm")); //Χρησιμοποιήστε το αντικείμενο `DocumentBuilder` για να αναλύσετε έγγραφα XML.

        //επιστρέφει το ριζικό στοιχείο του εγγράφου XML
        document.getDocumentElement().normalize(); //διαδικασία κανονικοποίησης της δομής του εγγράφου XML

        //αρχικοποίηση ρίζας
        Element root = document.getDocumentElement();

        //δημιουργία node list
        NodeList nodeList = document.getElementsByTagName("node"); //αναζητά στο έγγραφο XML στοιχεία με το καθορισμένο όνομα ετικέτας = node

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i); //ανάκτηση ενός κόμβου από τη λίστα σε μια συγκεκριμένη θέση.

            if (node.getNodeType() == Node.ELEMENT_NODE) { //αποτιμάται σε true αν ο τύπος του κόμβου είναι κόμβος στοιχείου, και false διαφορετικά.


                if (count == 1365) { //όταν γεμίσει ενα block
                    count = 0; //Ξεκίνα απο την αρχή για να ξεκινήσει και καινούριο block
                    blocks_Number++;
                    //εμφάνιση του μπλοκ id
                    dos.writeChar('b');
                    //όταν αλλάζουμε μπλοκ, εμφανίζεται το id του καινούργιου μπλοκ στην αρχή
                    if (blocks_Number < 10) {
                        /*
                        dos.writeChar('0');
                        dos.writeChar('0');
                        dos.writeChar((char) blocks_Number);*/
                        dos.writeChars(String.format("b%03d", blocks_Number)); // Block ID
                    } else {
                        //String bns = String.valueOf(blocks_Number);
                        //char[] tmp = bns.toCharArray();
                        if (blocks_Number < 99) {
                            /*
                            dos.writeChar('0');
                            dos.writeChar(tmp[0]);
                            dos.writeChar((char) tmp[1]);*/
                            dos.writeChars(String.format("b%03d", blocks_Number)); // Block ID
                        } else {
                            /*
                            dos.writeChar((char) tmp[0]);
                            dos.writeChar((char) tmp[1]);
                            dos.writeChar((char) tmp[2]);*/
                        }
                    }

                }
                //εμφάνιση κάθε σημείου
                Element eElement = (Element) node;
                //Παίρνω το id, lat, lon
                long id = Long.parseLong(eElement.getAttribute("id"));
                double latitude = Double.parseDouble(eElement.getAttribute("lat"));
                double longitude = Double.parseDouble(eElement.getAttribute("lon"));
                //Τα βάζω στο datafile
                dos.writeLong(id);
                dos.writeDouble(latitude);
                dos.writeDouble(longitude);
                //πρσθήκη του σημείου στο δέντρο
                tree.add_in_tree(new Point(latitude,longitude,blocks_Number,count));
                count++;
            }
        }
        //αποθήκευση του δέντρου στο Indexfile
        write_in_index_file(tree);
    }

    /**
     * Συνάρτηση που περνά το δένδρο R* στο indexfile ως Object
     * @param tree
     */
    public void write_in_index_file(Tree tree) {

        try {
            FileOutputStream myWriter = new FileOutputStream("indexfile.txt");
            ObjectOutputStream objectOut = new ObjectOutputStream(myWriter);
            objectOut.writeObject(tree.getRoot());
            objectOut.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing in file");
            e.printStackTrace();
        }
    }

    /**
     * Συνάρτηση που παίρνει το δένδρο R* από το indexfile ως Object
     * @param tree
     */
    public void read_from_index_file(Tree tree) {
        try {
            FileInputStream myWriter = new FileInputStream("indexfile.txt");
            ObjectInputStream objectOut = new ObjectInputStream(myWriter);
            tree.setRoot((NodeOfTree) objectOut.readObject());
            objectOut.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }



    /*public void add_location(Location location, ArrayList<Location> locations) {
        //προσθέτουμε την τοποθεσία στη λίστα με τις υπόλοιπες τοποθεσίες
        //επαναλαμβάνουμε την ίδια διαδικασία με πριν
        locations.add(location);
        int size = locations.size();
        try {
            FileOutputStream fos = new FileOutputStream("datafile.txt");
            DataOutputStream dos = new DataOutputStream(fos);
            //τα πρώτα 4 ψηφία αντιπροσωπεύουν το blockid
            dos.writeChar('b');
            dos.writeChar('0');
            dos.writeChar('0');
            dos.writeChar('0');
            //Κάθε εισαγωγή περιλαμβάνει 24 bytes
            //άρα για να δημιουργήσουμε μπλοκς με χωρητικότητα 32ΚΒ πρέπει να έχουμε 1365 εισαγωγές
            //επιπλέον τα 8 πρώτα bytes του κάθε μπλοκ αντιπροσωπεύουν το blockid
            dos.writeInt(size/1365+1);
            dos.writeInt(size);
            dos.writeInt(1365);
            int blocks_Number = 1;
            //εμφάνιση μπλοκ id
            dos.writeChar('b');
            dos.writeChar('0');
            dos.writeChar('0');
            dos.writeChar((char) blocks_Number);
            int count = 0;
            for (int x = 0; x < locations.size(); x++) {
                if (count == 1365) {
                    //ο αριθμός εισαγωγών σε κάθε μπλοκ είναι 1365
                    //αρα το μέγεθος του κάθε μπλοκ είναι 32ΚΒ
                    count = 0;
                    blocks_Number++;
                    //εμφάνιση μπλοκ id
                    dos.writeChar('b');
                    if (blocks_Number < 10) {
                        dos.writeChar('0');
                        dos.writeChar('0');
                        dos.writeChar((char) blocks_Number);
                    } else {
                        String bns = String.valueOf(blocks_Number);
                        char[] tmp = bns.toCharArray();
                        if (blocks_Number < 99) {
                            dos.writeChar('0');
                            dos.writeChar((char) tmp[0]);
                            dos.writeChar((char) tmp[1]);
                        } else {
                            dos.writeChar((char) tmp[0]);
                            dos.writeChar((char) tmp[1]);
                            dos.writeChar((char) tmp[2]);
                        }
                    }

                }
                //γράφουμε τα αποτελέσματα στο datafile
                dos.writeLong(locations.get(x).getLocationid());
                dos.writeDouble(locations.get(x).getLat());
                dos.writeDouble(locations.get(x).getLon());
                count++;
            }
            dos.close();
        } catch (
                IOException e) {
            System.out.println("IOException : " + e);


        }

    }*/
}
