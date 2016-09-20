import java.io.*;
import java.net.*;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class ServerClient {

    DatagramSocket clientSocket;
    int port = 4445;

    public static void main(String args[]){

        ServerClient clientServer = new ServerClient();
        clientServer.start();
    }

    public void start() {
        BufferedReader bufferedReader = null;

        try {
            String centralServerIP;
            String zone;
//            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//            String location = "[CLIENTE]: ";
//
//            System.out.println(location + " Ingresar IP Servidor Central : ");
//            System.out.print("> ");
//            centralServerIP = bufferedReader.readLine();
//
//            System.out.println(location + "Introducir Nombre de Zona a explorar :");
//            System.out.print("> ");
//            zone = bufferedReader.readLine();

            //centralServerIP = "192.168.0.12";
            centralServerIP = "192.168.31.241";
            String centralServerPort = "4445";
            zone = "Zona 1";


            String answerFromServer = connectToCentralServer(centralServerIP,centralServerPort, zone);
            if(answerFromServer == null){
                System.out.println("No answer from Server");
                return;
            }

            String[] split = answerFromServer.split(";");
            Integer code = Integer.parseInt(split[0]);
            String msg = split[1];
            String ipMultiCast=null;
            String ipPetition=null;
            String portPetition=null;

            if(code == 404)
                System.out.println(msg);
            if(code == 200){
                System.out.println(msg);
                ipMultiCast = split[2];
                ipPetition = split[3];
                portPetition = split[4];


            }

            System.out.println("Exiting Gracefully");







        }
        catch (UnknownHostException e) {
            System.out.println("Hostname/IP could not be found in the network");
            e.printStackTrace();
        }catch (IOException e) {
            System.out.println("Couldn't open socket for I/O Operation");
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String connectToCentralServer(String centralServerIP, String centralServerPort, String zone) throws IOException {

        Integer port = Integer.parseInt(centralServerPort);
        this.clientSocket = new DatagramSocket();
        InetAddress centralServerAddress = InetAddress.getByName(centralServerIP);
        byte[] outgoingBuffer = new byte[2048];
        byte[] incomingBuffer = new byte[2048];

        byte[] sendData = zone.getBytes();

        System.out.println("Connecting to Server....");
        System.out.println("Sending : "+ zone);
        //SENDING
        DatagramPacket datagramPacket = new DatagramPacket(sendData, sendData.length, centralServerAddress, port);
        this.clientSocket.send(datagramPacket);

        //RECEIVING

        DatagramPacket recievePacket = new DatagramPacket(incomingBuffer, incomingBuffer.length);
        this.clientSocket.receive(recievePacket);
        String recievedString = new String(recievePacket.getData()).trim();
        System.out.println("FROM SERVER: " + recievedString);
        String[] split = recievedString.split(";");
        String code = split[0];

        if(code.equals("200")){
            String message = split[1];
            String ipMulticast = split[2];
            String petitionAddress = split[3];
            String portMulticast = split[4];

            this.showConsole();
            // ACA EMPIEZA JUAN PABLO


            //Subscribe to Multicast;
        }else{
            //fail Logic
        }

        System.out.println("Code: "+ split[0]);


        return recievedString;

    }

    private void showConsole(){
        System.out.println("[CLIENTE] Consola");
        System.out.println("[CLIENTE] (1) Listar Distribumones en Zona");
        System.out.println("[CLIENTE] (2) Cambiar Zona");
        System.out.println("[CLIENTE] (3) Capturar Distribumon");
        System.out.println("[CLIENTE] (4) Listar Distribumones Capturados");
    }

    private void readInput(){

    }
}
