import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class ServerClient {

    DatagramSocket clientSocket;
    DatagramSocket zoneServerPetitionSocket;
    int port = 4445;
    int multicastPort = 4449;
    String ipPetition;
    int portPetition;

    String centralServerIP;
    String centralServerPort;
    String zone;

    private ArrayList<String> distribumons;

    public static void main(String args[]){
        System.setProperty("java.net.preferIPv4Stack", "true");
        ServerClient clientServer = new ServerClient();
        clientServer.start();
    }

    public void start() {
        this.distribumons = new ArrayList<String>();
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
            this.centralServerIP = "10.6.43.79";
            this.centralServerPort = "4445";
            this.zone = "Zona 1";


            String[] answerFromServer = connectToCentralServer(this.centralServerIP, this.centralServerPort, this.zone);
            if(answerFromServer == null){
                System.out.println("No answer from Server");
                return;
            }

            String code = answerFromServer[0];

            if(code.equals("200")) {

                Thread menu = new Thread()  {
                    private void showConsole(){
                        System.out.println("[CLIENTE] Consola");
                        System.out.println("[CLIENTE] (1) Listar Distribumones en Zona");
                        System.out.println("[CLIENTE] (2) Cambiar Zona");
                        System.out.println("[CLIENTE] (3) Capturar Distribumon");
                        System.out.println("[CLIENTE] (4) Listar Distribumones Capturados");
                        System.out.print("> ");
                    }

                    public void run() {
                        while (true) {
                            this.showConsole();
                            Scanner scan = new Scanner(System.in);
                            Integer s = scan.nextInt();

                            try {
                                     if (s == 1) ServerClient.this.makePetition("list");
                                else if (s == 2) System.out.println("Cambiar de zona al ql");
                                else if (s == 3) ServerClient.this.makePetition("capture");
                                else if (s == 4) ServerClient.this.viewMyDistribumons();
                                else             System.out.println("Opcion invalida");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };

                MulticastSocket clientMulticastSocket = this.initalZoneServerConnection(answerFromServer);

                menu.start();

                while(true) {
                    byte[] multicastBuffer = new byte[2048];
                    DatagramPacket msgFromMultiCast = new DatagramPacket(multicastBuffer, multicastBuffer.length);
                    clientMulticastSocket.receive(msgFromMultiCast);
                    String multicastMessage = new String(multicastBuffer, 0, multicastBuffer.length).trim();
                    System.out.println(multicastMessage);
                }
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
        this.portPetition = Integer.parseInt(portPetition);
        this.ipPetition = petitionAddress;

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

    private String readInput() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        return line;
    }

    private void connectToZoneServer(String ipPetition, String portPetition) throws IOException {
//        Integer port = Integer.parseInt(portPetition);
//        this.zoneServerPetitionSocket = new DatagramSocket();
//        InetAddress zoneServerPetitionAddress = InetAddress.getByName(ipPetition);
//
//        String data = "Dame un thread po ql";
//        byte[] sendData = data.getBytes();
//        DatagramPacket datagramPacket = new DatagramPacket(sendData, sendData.length, zoneServerPetitionAddress, port);
//        this.zoneServerPetitionSocket.send(datagramPacket);
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

    private void makePetition(String petition) throws IOException {
        // Request
        DatagramSocket zoneServerPetitionSocket = new DatagramSocket();
        InetAddress zoneServerPetitionAddress = InetAddress.getByName(this.ipPetition);
        Integer port = this.portPetition;
        byte[] sendData = petition.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(sendData, sendData.length, zoneServerPetitionAddress, port);
        zoneServerPetitionSocket.send(datagramPacket);

        // Response
        byte[] incomingBuffer = new byte[2048];
        DatagramPacket recievePacket = new DatagramPacket(incomingBuffer, incomingBuffer.length);
        zoneServerPetitionSocket.receive(recievePacket);
        String response = new String(recievePacket.getData()).trim();
        String[] split = response.split(";");
        if (split[0].equals("capture")) {
            String[] d = split[1].split(":");
            System.out.println("Has capturado un " + d[0] + " nivel " + d[1]);
            this.distribumons.add(split[1]);
        }else if (split[0].equals("list")){
            String[] distribumons = response.substring(5).split(";");
            for(String d: distribumons){
                String[] dist = d.split(":");
                System.out.println(dist[0] + " (Nivel " + dist[1] + ")");
            }
        }else if (split[0].equals("error")){
            System.out.println("Error: " + split[1]);
        }else {
            System.out.println("Dont know what you doin");
        }

    }

    private void viewMyDistribumons(){
        for(Object d : this.distribumons) {
            String element = (String) d;
            String split[] = element.split(":");
            System.out.println(split[0] + " (Nivel " + split[1] + ")");
        }
    }
}
