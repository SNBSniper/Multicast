package cl.utfsm.client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class Client {

    private DatagramSocket clientSocket;
    private String ipPetition;
    private int portPetition;

    private String centralServerIP;
    private String centralServerPort;
    private Boolean hasZone = false;
    private MulticastSocket zoneServerMulticastSocket;
    private String zoneServerMulticastIP;
    private Thread multicastThread;

    private ArrayList<String> distribumons;

    public static void main(String args[]){
        System.setProperty("java.net.preferIPv4Stack", "true");
        Client clientServer = new Client();
        clientServer.start();
    }

    public void start() {
        this.distribumons = new ArrayList<String>();
        Scanner scan = new Scanner(System.in);

        System.out.println("[Cliente] Ingresar IP Servidor Central");
        System.out.print("> ");
        this.centralServerIP = scan.nextLine().trim();

        System.out.println("[Cliente] Ingresar Puerto Servidor Central");
        System.out.print("> ");
        this.centralServerPort = scan.nextLine().trim();
        try {
            this.changeZone();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {
            this.showConsole();
            Integer s = scan.nextInt();

            try {
                if (Client.this.hasZone) {
                    if (s == 1) Client.this.makePetition("list");
                    else if (s == 2) Client.this.changeZone();
                    else if (s == 3) Client.this.makePetition("capture");
                    else if (s == 4) Client.this.viewMyDistribumons();
                    else System.out.println("Opcion invalida");
                } else {
                    if (s == 1) Client.this.changeZone();
                    else System.out.println("Opcion invalida");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void showConsole() {
        if (!Client.this.hasZone) {
            System.out.println("[Cliente] (1) Cambiar Zona\n> ");
            return;
        }
        System.out.println("[Cliente] (1) Listar Distribumones en Zona");
        System.out.println("[Cliente] (2) Cambiar Zona");
        System.out.println("[Cliente] (3) Capturar Distribumon");
        System.out.println("[Cliente] (4) Listar Distribumones Capturados");
        System.out.print("> ");
    }

    private void changeZone() throws IOException {
        System.out.println("[Cliente] Introducir Nombre de Zona a explorar, Ej: Casa Central, San Joaquin");
        System.out.print("> ");
        Scanner scan = new Scanner(System.in);
        String zone = scan.nextLine().trim();

        // Leave previous group
        if (this.multicastThread != null && this.multicastThread.isAlive())
            this.multicastThread.interrupt();
        if (this.zoneServerMulticastSocket != null && ! this.zoneServerMulticastSocket.isClosed()){
            this.zoneServerMulticastSocket.leaveGroup(InetAddress.getByName(this.zoneServerMulticastIP));
            this.zoneServerMulticastSocket.close();
        }
        this.hasZone = false;

        String[] answerFromServer = connectToCentralServer(this.centralServerIP, this.centralServerPort, zone);
        if(answerFromServer == null){
            System.out.println("[Cliente] No answer from Server");
            return;
        }
        String code = answerFromServer[0];

        if(code.equals("200")) {
            this.hasZone = true;
            System.out.println("[Cliente] Connection Succesfull to Zone Server");
            this.zoneServerMulticastSocket = this.initalZoneServerConnection(answerFromServer);
            this.multicastThread = new Thread() {
                public void run() {
                    try {
                        while(true) {
                            byte[] multicastBuffer = new byte[2048];
                            DatagramPacket msgFromMultiCast = new DatagramPacket(multicastBuffer, multicastBuffer.length);
                            try {
                                Client.this.zoneServerMulticastSocket.receive(msgFromMultiCast);
                            }catch(SocketException e) {
                                e.getMessage();
                                return;
                            }
                            String multicastMessage = new String(multicastBuffer, 0, multicastBuffer.length).trim();
                            System.out.println("[Cliente] Aparece nuevo Distribumon!: " + multicastMessage);
                        }
                    }
                    catch (UnknownHostException e) {
                        System.out.println("[Cliente] Hostname/IP could not be found in the network");
                        e.printStackTrace();
                    }catch (IOException e) {
                        System.out.println("[Cliente] Couldn't open socket for I/O Operation");
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            this.multicastThread.start();
        }
    }

    private MulticastSocket initalZoneServerConnection(String[] answerFromServer){

        String message = answerFromServer[1];
        String ipMulticast = answerFromServer[2];
        String portMulticast = answerFromServer[3];
        String petitionAddress = answerFromServer[4];
        String portPetition = answerFromServer[5];

        this.zoneServerMulticastIP = ipMulticast;
        System.out.println("Zone Server: " + message);
        MulticastSocket clientMultiCastSocket = this.subscribeToMulticast(ipMulticast, portMulticast);
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

        //SENDING
        DatagramPacket datagramPacket = new DatagramPacket(sendData, sendData.length, centralServerAddress, port);
        this.clientSocket.send(datagramPacket);

        //RECEIVING
        DatagramPacket recievePacket = new DatagramPacket(incomingBuffer, incomingBuffer.length);
        this.clientSocket.receive(recievePacket);
        String recievedString = new String(recievePacket.getData()).trim();
        String[] split = recievedString.split(";");

        return split;
    }

    private String readInput() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        return line;
    }

    private MulticastSocket subscribeToMulticast(String ipMultiCast, String portMulticast) {
        try {
            InetAddress multicastAddress = InetAddress.getByName(ipMultiCast);
            Integer multicastPort = Integer.parseInt(portMulticast);
            MulticastSocket clientMultiCastSocket = new MulticastSocket(multicastPort);
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
        System.out.println("Petition sent: " + this.ipPetition + ":" +port);

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
        } else {
            if (split[0].equals("error")) {
                System.out.println("Error: " + split[1]);
            } else {
                System.out.println("Dont know what you doin");
            }
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
