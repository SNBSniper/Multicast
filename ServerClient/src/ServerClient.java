import java.io.*;
import java.net.*;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class ServerClient {

    DatagramSocket clientSocket;
    DatagramSocket zoneServerPetitionSocket;
    int port = 4445;
    int multicastPort = 4446;

    public static void main(String args[]){
        System.setProperty("java.net.preferIPv4Stack", "true");
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
            centralServerIP = "192.168.8.101";
            String centralServerPort = "4445";
            zone = "Zona 1";


            String[] answerFromServer = connectToCentralServer(centralServerIP,centralServerPort, zone);
            if(answerFromServer == null){
                System.out.println("No answer from Server");
                return;
            }

            String code = answerFromServer[0];

            if(code.equals("200")){

                MulticastSocket clientMulticastSocket = this.initalZoneServerConnection(answerFromServer);

                while(true){
                    byte[] multicastBuffer = new byte[2048];
                    DatagramPacket msgFromMultiCast = new DatagramPacket(multicastBuffer, multicastBuffer.length);
                    clientMulticastSocket.receive(msgFromMultiCast);
                    String multicastMessage = new String(multicastBuffer, 0, multicastBuffer.length).trim();
                    System.out.println(multicastMessage);
                }
                // ACA EMPIEZA JUAN PABLO


                //Subscribe to Multicast;
            }else{
                //fail Logic
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

    private MulticastSocket initalZoneServerConnection(String[] answerFromServer){

        String message = answerFromServer[1];
        String ipMulticast = answerFromServer[2];
        String petitionAddress = answerFromServer[3];
        String portPetition = answerFromServer[4];

        System.out.println("Server: " + message);
        MulticastSocket clientMultiCastSocket = this.subscribeToMulticast(ipMulticast);
        try {
            this.connectToZoneServer(petitionAddress, portPetition);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientMultiCastSocket;
    }

    private String[] connectToCentralServer(String centralServerIP, String centralServerPort, String zone) throws IOException {

        Integer port = Integer.parseInt(centralServerPort);
        this.clientSocket = new DatagramSocket();
        InetAddress centralServerAddress = InetAddress.getByName(centralServerIP);

        byte[] incomingBuffer = new byte[2048];

        byte[] sendData = zone.getBytes();

        System.out.println("Connecting to Central Server....");
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

        return split;

    }

    private void showConsole(){
        System.out.println("[CLIENTE] Consola");
        System.out.println("[CLIENTE] (1) Listar Distribumones en Zona");
        System.out.println("[CLIENTE] (2) Cambiar Zona");
        System.out.println("[CLIENTE] (3) Capturar Distribumon");
        System.out.println("[CLIENTE] (4) Listar Distribumones Capturados");
    }

    private String readInput() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        return line;
    }

    private void connectToZoneServer(String ipPetition, String portPetition) throws IOException {
        Integer port = Integer.parseInt(portPetition);
        this.zoneServerPetitionSocket = new DatagramSocket();
        InetAddress zoneServerPetitionAddress = InetAddress.getByName(ipPetition);

        String data = "Dame un thread po ql";
        byte[] sendData = data.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(sendData, sendData.length, zoneServerPetitionAddress, port);
        this.zoneServerPetitionSocket.send(datagramPacket);
    }

    private MulticastSocket subscribeToMulticast(String ipMultiCast) {
        try {
            InetAddress multicastAddress = InetAddress.getByName(ipMultiCast);

            MulticastSocket clientMultiCastSocket = new MulticastSocket(this.multicastPort);
            clientMultiCastSocket.joinGroup(multicastAddress);
            return clientMultiCastSocket;

        } catch (UnknownHostException e) {
            System.out.println("NO HOST FOUND FOR : " + ipMultiCast);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException: "+ ipMultiCast);
            e.printStackTrace();
        }
        return null;

    }


}
