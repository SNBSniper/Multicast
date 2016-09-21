import java.io.IOException;

/**
 * Created by danielftapiar on 9/20/16.
 */
public class Main {

    public static void main (String args[]) throws IOException {

        ServerZone serverZone = new ServerZone();
        serverZone.initalServerStart();
//        serverZone.waitForConnection();
//        serverZone.start();

    }
}
