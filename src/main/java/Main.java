import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ServerMaster masterServer = new ServerMaster();

        try {
            masterServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
