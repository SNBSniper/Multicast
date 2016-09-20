import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
//        ServerClient clientServer = new ServerClient();
        ServerMaster masterServer = new ServerMaster();


        try {
            masterServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
