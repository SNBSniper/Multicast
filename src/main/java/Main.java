import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Master masterServer = new Master();

        try {
            masterServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
