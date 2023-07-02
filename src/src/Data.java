import java.io.*;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;

/**
 * Αυτή η κλάση χρησιμοποιείται για να φορτώσουμε και να διαβάσουμε το ήδη υπάρχον αρχείο datafile
 */
public class Data {
    //συνολικός αριθμός από εισαγωγές
    private int all_entries;

    /**
     * Getter για το total_entries
     */
    public int getAll_entries() {
        return all_entries;
    }

    //Συνάρτηση που διαβάζει ένα αρχείο με χωρητικότητα 32ΚΒ ανά μπλοκ και εξάγει τα locations
    public ArrayList<Location> Read_Data() {
        //Λίστα για να βάλω τα στοιχεία που θα διαβάσω από το datafile
        ArrayList<Location> locationsArrayList = new ArrayList<>();
        try {

            //Διαβάζει δυαδικά δεδομένα από ένα αρχείο με όνομα "datafile.txt" και άλλους τύπους
            File file = new File("datafile.txt");
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream inputstream = new DataInputStream(fileInputStream);

            long id = 0;
            double lat = 0, lon = 0;


            //το πρώτο μπλοκ με τα μεταδεδομένα είναι 20 Bytes
            //data_bytes ο πίνακας με τα Bytes
            byte[] data_bytes = new byte[20];


            //0 = starting index και 20 = τελευταίο στοιχείο
            inputstream.read(data_bytes, 0, 20); //ο αριθμός των byte που πραγματικά διαβάστηκαν

            //Επειδή είναι Integers πιάνουν 4 bytes
            //αυτή η γραμμή κώδικα αντιγράφει τα πρώτα 4 byte από τον πίνακα data_bytes και τα αποθηκεύει στον αντίστοιχο πίνακα κάθε φορά
            // ξεκινώντας από το ευρετήριο 0.
            byte[] number_of_blocks = new byte[4]; //Ο συνολικός αριθμός απο μπλοκ
            byte[] number_of_entries = new byte[4]; //Ο συνολικός αριθμός εγγραφών
            byte[] records_per_block = new byte[4]; //Ο αριθμός εγγραφών σε κάθε μπλοκ


            int bytes = 0; //μετρητής των bytes

            //να σημειωθεί ότι παραλείπουμε τα 8 πρώτα bytes σε κάθε μπλοκ
            //γιατί τα bytes αυτά είναι τα id του κάθε μπλοκ τα οποία δεν χρειαζόμαστε
            for(int counter=8;counter<20;counter++){
                //ανά 4 bytes αλλάζουν οι μεταβλητές
                if(bytes==4){
                    bytes=0;
                }
                //αποθήκευση του συνολικού αριθμού απο μπλοκς
                if(counter<12){
                    number_of_blocks[bytes]=data_bytes[counter];
                }
                //αποθήκευση του συνολικού αριθμού εισαγωγών
                else if(counter<16){
                    number_of_entries[bytes]=data_bytes[counter];
                }
                //αποθήκευση αριθμού εισαγωγών ανα μπλοκ
                else{
                    records_per_block[bytes]=data_bytes[counter];
                }
                bytes++;
            }


            //Τα δεδομένα έχουν διαβαστεί ως μια σειρά από bytes
            ByteBuffer bfMeta = ByteBuffer.wrap(number_of_blocks); //Τυλίγω τον πίνακα τύπου byte και μπορεί να χρησιμοποιηθεί για ανάγνωση, εγγραφή σε και χειρισμό δυαδικών δεδομένων.
            IntBuffer lgMeta = bfMeta.asIntBuffer(); //Τα κάνουμε integers
            int blocks_number = lgMeta.get(); //Παίρνει το value και το βάζει σε ακέραια μεταβλητή

            //Επαναλαμβάνω και για τα υπόλοιπα
            bfMeta = ByteBuffer.wrap(number_of_entries);
            lgMeta = bfMeta.asIntBuffer();
            all_entries = lgMeta.get();
            bfMeta = ByteBuffer.wrap(records_per_block);
            lgMeta = bfMeta.asIntBuffer();
            int block_records = lgMeta.get();

            //η ανάγνωση γίνεται ανα μπλοκ
            for (int o = 0; o < blocks_number; o++) {

                //διαβάζονται 32ΚΒ μπλοκ
                byte[] buffer = new byte[32768]; //32 * 1024
                inputstream.read(buffer, 0, 32768);

                //κάθε εισαγωγή περιλαμβάνει 3 μεταβλητές από 8 byte η κάθε μία

                //ΟΙ ΠΙΝΑΚΕΣ ΕΙΝΑΙ ΔΙΣΔΙΑΣΤΑΤΟΙ ΓΙΑΤΙ ΕΧΟΥΜΕ 1365 ΕΓΓΡΑΦΕΣ ΣΕ ΚΑΘΕ ΜΠΛΟΚ ΔΛΔ ΤΟΠΟΘΕΣΙΕΣ ΩΣ ΓΡΑΜΜΕΣ ΠΟΥ ΑΠΟΤΕΛΟΥΝΤΑΙ ΑΠΟ 8 BYTES Η ΚΑΘΕ ΜΙΑ
                byte[][] IDs = new byte[block_records][8];
                byte[][] Latitudes = new byte[block_records][8];
                byte[][] Longitudes = new byte[block_records][8];

                //Αρχικοποίηση βοηθητικών μεταβλητών
                int k = 0;
                int l = 0;
                int i = 0;
                int count = 0;


                for (int m = 8; m < 32768; m++) {
                    if (i == 24) { //όταν τα στοιχεια της εγγραφης συμπληρωθουν, παμε στην επομενη
                        count++;
                        i = 0;
                        l = 0;
                        k = 0;
                    }
                    if (i < 8) {
                        IDs[count][i] = buffer[m];
                    } else if (i < 16) {
                        Latitudes[count][k] = buffer[m];
                        k++;

                    } else {
                        Longitudes[count][l] = buffer[m];
                        l++;
                    }
                    i++;
                }

                //Για κάθε εγγραφή στο μπλοκ
                for (int c = 0; c < block_records; c++) {
                    ByteBuffer bf = ByteBuffer.wrap(IDs[c]);//Τυλίγω τον πίνακα τύπου byte και μπορεί να χρησιμοποιηθεί για ανάγνωση, εγγραφή σε και χειρισμό δυαδικών δεδομένων.
                    LongBuffer lg = bf.asLongBuffer(); //Γίνονται long
                    long buf_id = lg.get(); //Παίρνω το value και το βάζω σε μεταβλητή

                    //Επαναλαμβάνω την διαδικασία και για τα υπόλοιπα
                    bf = ByteBuffer.wrap(Latitudes[c]);
                    DoubleBuffer bs = bf.asDoubleBuffer();
                    Double buf_lat = bs.get();

                    bf = ByteBuffer.wrap(Longitudes[c]);
                    DoubleBuffer bl = bf.asDoubleBuffer();
                    double buf_lon = bl.get();

                    if (buf_id == 0 && buf_lat == 0 && buf_lon == 0) { //δεν υπάρχουν πλέον έγκυρες εγγραφές στο τρέχον μπλοκ
                        break; //βγες απo την for
                    }
                    locationsArrayList.add(new Location(buf_id, buf_lat, buf_lon)); //Βάλτο στην λίστα με τα locations που διαβάστηκαν απ'το datafile
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationsArrayList; //Επέστρεψε την λίστα με τα locations που έχουν διαβαστεί απ'το datafile
    }

    /**
     * Συνάρτηση που παίρνει ως ορίσματα ένα συγκεκριμένο blockid και slotid
     * και επιστρέφει την τοποθεσία στην οποία δείχνουν
     * @param blockid το id ενός μπλοκ μέσα στο οποίο αναζητούμε μια τοποθεσία
     * @param slotid το slot στο οποίο έχει αποθηκευτεί η πληροφορία που ψάχνουμε
     * */
    public Location find_block(int blockid, int slotid) {
        /*RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile("datafile.txt", "rw");
            accessFile.skipBytes(20); //μεταδεδομενα
            accessFile.skipBytes(32768 * --blockid); //ολα τα blockid
            accessFile.skipBytes(8); //blockid
            accessFile.skipBytes(24 * slotid); //ολα τα slotid
            //κρατάμε μόνο το id του location , lat , lon που μας χρειαζεται
            long id = accessFile.readLong();
            double lat = accessFile.readDouble();
            double lon = accessFile.readDouble();
            Location location = new Location(id, lat, lon); //οριζουμε ενα location
            return location; //το επιστρέφουμε

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;*/

        RandomAccessFile file = null; //είναι μια κλάση της Java που παρέχει μεθόδους για ανάγνωση και εγγραφή σε ένα αρχείο με τυχαίο τρόπο
        Location location = null; //αρχικοποιώ ένα location κενό

        try {
            file = new RandomAccessFile("datafile.txt", "rw"); //rw = υποδηλώνει ότι το αρχείο μπορεί να διαβαστεί και να γραφτεί


            file.skipBytes(20); //Skip μεταδεδομένα


            file.skipBytes(32768 * --blockid); //Skip τα blockid


            file.skipBytes(8); //Skip blockid


            file.skipBytes(24 *slotid); //Skip τα slotid

            //Ώστε να μείνουν τα location id , lat και lon που χρειαζόμαστε
            //Διάβασε τα
            long id = file.readLong();
            double lat = file.readDouble();
            double lon = file.readDouble();

            //Φτιάξε ένα location με τα χαρακτηριστικά από πάνω
            location = new Location(id, lat, lon);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return location;//Επέστρεψε το location που βρήκες στο συγκεκριμένο blockid και slotid, εκτός και αν δεν υπάρχει επέστρεψε null
    }


}
