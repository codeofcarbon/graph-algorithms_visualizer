package visualizer;

import java.io.*;

public class Storage {

    static void serialize(Object object, String fileName) {
        try (var outStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))) {
            outStream.writeObject(object);
        } catch (IOException e) {
            System.err.println("Graph saving error" + e.getMessage());
        }
    }

    static Object deserialize(String fileName) {
        try (var inStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)))) {
            return inStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Graph loading error" + e.getMessage());
            return null;
        }
    }
}
