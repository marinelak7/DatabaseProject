import java.io.Serializable;
import java.util.ArrayList;

public class Deletion {

    public void deletion (long id, ArrayList<Location> locations , Tree tree){

        NodeOfTree parent = null; //Κενός κόμβος
        ArrayList<NodeOfTree> temp_nodes = new ArrayList<>(); //Φτιάχνω μια λίστα με όλους τους κόμβους
        temp_nodes.add(tree.getRoot()) ;
        tree.setTotal_records(tree.getTotal_records()-1);
        boolean flag =true ;

        while(flag){
            for (NodeOfTree node : temp_nodes){ //για καθε κομβο r που ειναι αποθηκευμενος στη λιστα temptree
                boolean allLeaf = tree.AllLeaf(temp_nodes);
                boolean isEmpty = temp_nodes.isEmpty();

                if (allLeaf || isEmpty){ //αν ολα ειναι φυλλα ή το temptree είναι άδειο
                    flag = false;
                    break; //βγες απο την while
                }

                //ελέγχω αν ο κομβος r είναι φύλλο
                if(node.isLeaf()){
                    //κρατάμε το parent
                    parent = new NodeOfTree(node.getRectangle().getX1() , node.getRectangle().getX2() ,node.getRectangle().getY1() , node.getRectangle().getY2());//αρχικοποιει τον κομβο
                    Data data = new Data(); //φτιαχνει ενα data τυπου LoadData

                    ArrayList<Point> point = new ArrayList<>(node.getPoints());
                    //παίρνουμε όλα τα στοιχεία που είναι στον κόμβο
                    // βρίσκουμε τις τοποθεσίες των σημείων του
                    for (Point p : point){ //διασχιζει τα σημεια μεσα στον κομβο
                        Location l = data.find_block(p.getBlockid() , p.getSlotid());
                        long temp = l.getLocationid(); //παιρνω το id της τοποθεσιας και το βαζω σε μια μεταβλητη

                        //αν κάποιο από τα σημεία έχει ίδιο id με αυτό που δίνεται ως όρισμα τότε
                        if (temp == id){
                            flag = false;

                            //δημιουργία προσωρινού δέντρου και αφαίρεση από τη λίστα το σημείο αυτό αφαιρώντας την τοποθεσία του
                            ArrayList<Point> temp_point = parent.getPoints();
                            ArrayList<Location> temp_location = new ArrayList<>(locations);

                            for (Location lc : temp_location){
                                if (lc.getLocationid() == temp){
                                    locations.remove(lc);
                                }
                            }
                            parent.setPoints(temp_point); //clear και αρχικοποιηση

                            //αν ο κόμβος είναι λιγότερο από το 50% γεμάτος με σημεία τότε
                            // θα πρέπει να γίνει η αναδιαμόρφωση στο δέντρο
                            //max = 5
                            if (parent.getPoints().size() <= 3 ){
                                temp_point = parent.getPoints();
                                parent.setPoints(new ArrayList<>());

                                //διαγραφή των τοποθεσιών που έμειναν ορφανές
                                for (Point pp : temp_point){ //για καθε σημειο που βρισκεται μεσα στο temp_point
                                    for (Location lc: temp_location){ ////για καθε τοποθεσια που βρισκεται το temp_location
                                        if (lc.getLocationid() == data.find_block(pp.getBlockid() , pp.getSlotid()).getLocationid()){
                                            locations.remove(lc);
                                        }

                                    }
                                    //εισαγωγή των ορφανών τοποθεσιών στο δέντρο
                                    Insertion insert=new Insertion();
                                    insert.add_location(data.find_block(pp.getBlockid() , pp.getSlotid()) , data.Read_Data());
                                }
                            }
                        }
                    }
                }
                parent = node ;

            }
            temp_nodes.remove(parent) ;
            temp_nodes.addAll(parent.getChildren());

        }
        tree.previous_records = tree.getTotal_records();
        tree.setTotal_records(tree.previous_records - 1 );
    }




}
