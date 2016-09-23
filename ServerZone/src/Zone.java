import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class Zone {

    String name = null;
    InetAddress multicastAddr = null;
    InetAddress petitionAddr = null;

    DatagramSocket serverSocket;
    MulticastSocket multicastSocket;

    String multicastIP;
    int multicastPort;
    String petitionIP;
    int petitionPort;

    LinkedList<String> distribumons;

    public void initalServerStart() throws IOException {
        this.distribumons = new LinkedList<String>();
        this.distribumons.add("alpha-centauri:20");
        this.distribumons.add("canis-mayoris:50");

        this.createZoneServer();

        System.out.println("Server Starting on IP: "+this.petitionIP+":"+this.petitionPort);
        System.out.println("Multicast Starting on IP "+this.multicastIP+":"+this.multicastPort);


        this.serverSocket = new DatagramSocket(this.petitionPort);

        String zoneServerName = this.name;
        Thread menu = new Thread()  {
            public void run() {
                while (true) {
                    System.out.print("[Servidor Zona: " + zoneServerName + "] (1) Publicar distribumon\n> ");
                    Scanner scan = new Scanner(System.in);
                    int s = scan.nextInt(); scan.nextLine();
                    if (s == 1) {
                        System.out.println("[Servidor Zona: " + zoneServerName + "] Introducir nombre");
                        System.out.print("> ");
                        String name = scan.nextLine().trim();

                        System.out.println("[Servidor Zona: " + zoneServerName + "] Introducir nivel");
                        System.out.print("> ");
                        String level = scan.next();

                        System.out.println("[Servidor Zona: " + zoneServerName + "] Se ha publicado el Distribumon: " + name);
                        System.out.println("******");
                        System.out.println("id: " + 0);
                        System.out.println("nombre: " + name);
                        System.out.println("nivel: " + level);

                        Zone.this.distribumons.add(name + ":" + level);
                        try {
                            sendMessage(name);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        menu.start();

        while(true){
            byte[] incomingBuffer = new byte[2048];
            DatagramPacket datagramPacket = new DatagramPacket(incomingBuffer,incomingBuffer.length);
            System.out.println("Waiting for Connections....");
            this.serverSocket.receive(datagramPacket); //HERE IT STOPS AND WAITS FOR CLIENT
            int clientPort = datagramPacket.getPort();
            InetAddress clientAddress = datagramPacket.getAddress();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(datagramPacket.getData());
            String data = outputStream.toString().trim();

            System.out.println("Client Connection Accepted");
            System.out.println("Data: "+ data);

            System.out.println("Starting thread");
            final String parameter = data;
            Thread t = new Thread(new Runnable() {
                String p = parameter;
                public void run() {
                    System.out.println("Request: " + p);
                    if (p.equals("capture")) Zone.this.capture(clientAddress, clientPort);
                    if (p.equals("list")) Zone.this.listDistribumons(clientAddress, clientPort);
                    if (p.equals("view")) Zone.this.viewDistribumons(clientAddress, clientPort);
                }
            });
            t.start();
        }
    }

    private void viewDistribumons(InetAddress clientAddress, int clientPort) {


        String message = "ALL DISTRIBUMONS FOUND HAHAHA";
        this.sendMessageToMulticast(message);


        String response = "view";
        DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
        try {
            Zone.this.serverSocket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server sent packet with msg: " + message);

    }

    private void sendMessageToMulticast(String message){
        InetAddress multicastAddress = null;
        try {
            multicastAddress = InetAddress.getByName(this.multicastIP);
            DatagramSocket datagramSocket = new DatagramSocket();
            DatagramPacket msgPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, multicastAddress, this.multicastPort);
            datagramSocket.send(msgPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void createZoneServer() {

        //TODO MENU AND LOGIC HERE
        this.setName("Zona 1");
        this.setPetitionIP("192.168.8.101");
        this.setPetitionPort(4448);

        this.setMulticastIP("224.0.0.3");
        this.setMulticastPort(4449);
    }

    public Zone(){
        super();
    }


    public Zone(String name, String multiCastIP, String petitionIP) throws SocketException, UnknownHostException {

        this.serverSocket = new DatagramSocket();
        this.name = name;
        this.multicastAddr = InetAddress.getByName(multiCastIP);
        this.petitionAddr = InetAddress.getByName(petitionIP);

    }
//
//    public void start() throws IOException {
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//        String location = "[SERVIDOR ZONA "+this.getName()+"]: ";
//
//        System.out.println(location + " Publicar Distribumon ");
//
//        System.out.println(location + " Introducir nombre ");
//        System.out.print("> ");
//        String distribumonName = bufferedReader.readLine();
//
//        System.out.println(location + " Introducir level ");
//        System.out.print("> ");
//        String distribumonLevel = bufferedReader.readLine();
//
//        Distribumon distribumon = new Distribumon(Math.random(), distribumonName, distribumonLevel);
//        DatagramSocket zoneSocket = new DatagramSocket();
//
//        String message = "New Distribumon Created"+" "+distribumon.getName()+distribumon.getLevel();
//
//        DatagramPacket msgPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, this.multicastAddr, this.port);
//        this.serverSocket.send(msgPacket);
//        System.out.println("Pokemon Sent");
////
////            System.out.println(location + " Ingresar IP Servidor Central : ");
//
////            centralServerIP = bufferedReader.readLine();
////
////            System.out.println(location + "Introducir Nombre de Zona a explorar :");
////            System.out.print("> ");
////            zone = bufferedReader.readLine();
//
//
//
//    }
//
    public void sendMessage(String message) throws UnknownHostException {


        // Create a packet that will contain the data
        // (in the form of bytes) and send it.
        // ACA EMPIEZA JUAN PABLO
        InetAddress multicastAddress = InetAddress.getByName(this.multicastIP);

        DatagramPacket msgPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, multicastAddress, this.multicastPort);

        try {
            serverSocket.send(msgPacket);
            System.out.println("Server sent packet with msg: " + message);
            Thread.sleep(500);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
//
//    public void SendPokemonToBroadCast() throws IOException {
//        String msg = "NEW POKEMON FOUND: PIKACHU?";
//        DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),msg.getBytes().length, this.multicastAddr, this.port);
//        DatagramSocket zoneSocket = new DatagramSocket();
//        zoneSocket.send(msgPacket);
//    }

    private void capture(InetAddress clientAddress, Integer clientPort) {
        String response;
        if (this.distribumons.isEmpty()) {
            response = "error;no quedan distribumones";
        }else {
            // TODO add random chance of capture
            response = "capture;" + this.distribumons.remove();
            // response = "miss;aweonao";
        }
        DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
        try {
            Zone.this.serverSocket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listDistribumons(InetAddress clientAddress, Integer clientPort) {
        String response = "list";
        for (Object d: this.distribumons){
            response += ";" + (String)d;
        }
        System.out.println(response);
        DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
        try {
            Zone.this.serverSocket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getMulticastAddr() {
        return multicastAddr;
    }

    public void setMulticastAddr(InetAddress multicastAddr) {
        this.multicastAddr = multicastAddr;
    }

    public InetAddress getPetitionAddr() {
        return petitionAddr;
    }

    public void setPetitionAddr(InetAddress petitionAddr) {
        this.petitionAddr = petitionAddr;
    }

    public String getMulticastIP() {
        return multicastIP;
    }

    public void setMulticastIP(String multicastIP) {
        this.multicastIP = multicastIP;
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public void setMulticastPort(int multicastPort) {
        this.multicastPort = multicastPort;
    }

    public String getPetitionIP() {
        return petitionIP;
    }

    public void setPetitionIP(String petitionIP) {
        this.petitionIP = petitionIP;
    }

    public int getPetitionPort() {
        return petitionPort;
    }

    public void setPetitionPort(int petitionPort) {
        this.petitionPort = petitionPort;
    }
}
