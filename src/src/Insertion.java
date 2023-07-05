import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Insertion {

    /**
     * Συνάρτηση που προσθέτει μια νέα τοποθεσία στη λίστα με τις τοποθεσίες και στο datafile
     * @param location η τοποθεσία τύπου Location που θέλουμε να προσθέσουμε
     * @param locations λίστα με τις ήδη υπάρχουσες τοποθεσίες
     */
    public void add_location(Location location, ArrayList<Location> locations) {
        //προσθέτουμε την τοποθεσία στη λίστα με τις υπόλοιπες τοποθεσίες
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

            int blockSize = 32 * 1024; // 32KB
            int entriesPerBlock = blockSize / 24; // Κάθε entry είναι 24 bytes
            int numBlocks = (size / entriesPerBlock) + 1; //Ο αριθμός του μπλόκ που βρισκόμαστε


            dos.writeInt(numBlocks);// Αριθμός μπλοκς
            dos.writeInt(size);// Συνολικός αριθμός εισαγωγών
            dos.writeInt(entriesPerBlock);// Εισαγωγές ανά μπλοκ


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

                        dos.writeChars(String.format("b%03d", blocks_Number));
                    } else {
                        if (blocks_Number < 99) {
                            dos.writeChars(String.format("b%03d", blocks_Number));
                        } else {
                            dos.writeChars(String.format("b%03d", blocks_Number));
                        }
                    }

                }
                Location loc = locations.get(x);
                //γράφουμε τα αποτελέσματα στο datafile
                dos.writeLong(loc.getLocationid());
                dos.writeDouble(loc.getLat());
                dos.writeDouble(loc.getLon());
                count++;
            }
            dos.close();
        } catch (
                IOException e) {
            System.out.println("IOException : " + e);


        }

    }


}




