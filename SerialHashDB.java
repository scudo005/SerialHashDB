import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Scanner;

public class SerialHashDB implements Serializable {

    private HashMap db;
    // select, create, remove, update

    private SerialHashDB() {
        Scanner in = new Scanner(System.in);
        System.out.println("Do you want to reload the database from a file? ");
        String answer = in.nextLine();
        in.close();
        if ((answer.equalsIgnoreCase("y")) || (answer.equalsIgnoreCase("yes"))) {
            try {
                db = reloadDB();
            } catch (IOException e) {
                System.out.println("Database reload failed due to IOException. Creating a new database.");
                db = new HashMap<Object>();
            } catch (ClassNotFoundException c) {
                System.out.println("Database reload failed due to ClassNotFoundException. Creating a new database.");
                db = new HashMap<Object>();
            }
        } else
            db = new HashMap<Object>(); // we start over
    }

    public Object select(Object obj) {
        return db.get(obj);
    }

    public Object create(Object key, Object value) throws EntryAlreadyExistsException {
        if (!db.containsKey(key)) {
            Object ret = db.put(key, value); // this ensures we don't have duplicates.
            saveDB();
        } else
            throw new EntryAlreadyExistsException(); // we did not modify anything so no need to save the db to disk
        return ret;
    }

    public Object remove(Object key) {
        return db.remove(key);
    }

    // this returns null if the key was previously mapped to a null value, or if the
    // key was not mapped.
    public Object update(Object key, Object update) {
        return db.replace(key, update);
    }

    private void saveDB() {
        FileOutputStream dbfile = new FileOutputStream("db.bin");
        ObjectOutputStream objstream = new ObjectOutputStream(dbfile);
        try {
            objstream.writeObject(db);
        } catch (IOException e) {
            throw new IOException(); // we need the db file stream to close so we throw the exception again
        } finally {
            objstream.close();
        }
    }

    private void reloadDB() throws IOException, ClassNotFoundException {
        FileInputStream dbfile = new FileInputStream("db.bin");
        ObjectInputStream objstream = new ObjectInputStream(dbfile);
        try {
            db = objstream.readObject();
        } catch (IOException e) {
            throw new IOException(); // same as saveDB()
        } catch (ClassNotFoundException c) {
            throw new ClassNotFoundException();
        } finally {
            objstream.close();
        }
    }
}

class EntryAlreadyExistsException extends RuntimeException {

}
