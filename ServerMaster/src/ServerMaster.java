import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class ServerMaster {

    InetAddress localNetworkIp;

    int serverPort = 4445;
    List<ServerZone> zoneServers;
    DatagramSocket serverSocket;

    public void start() throws IOException {
        BufferedReader bufferedReader = null;
//            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//            String location="[Servidor Central]: ";
//            System.out.println(location + "Agrega Servidor de Zona ");
//
//            System.out.println(location +"Nombre : ");
//            System.out.print("> ");
//            String name = bufferedReader.readLine();
//
//            System.out.println(location +"IP Multicast :");
//            System.out.print("> ");
//            String multicastIP = bufferedReader.readLine();
//
//            System.out.println(location +": IP Peticiones :");
//            System.out.print("> ");
//            String petitionIP = bufferedReader.readLine();
//
//            System.out.println(location +": Puerto Peticiones :");
//            System.out.print("> ");
//            String petitionPort = bufferedReader.readLine();

        String name = "Zona 1";
        String multicastIP = "224.0.0.3";
        String petitionIP = "192.168.0.12";
        String petitionPort = "4445";


        this.localNetworkIp = InetAddress.getByName("192.168.0.12");

        System.out.println("Server Starting on IP: "+this.localNetworkIp+":"+this.serverPort);
        System.out.println("Creating Zone Server: " + name);
        createZoneServer(name,multicastIP, petitionIP);

        System.out.println("Waiting for Connections....");
        serverSocket = new DatagramSocket(this.serverPort);
        byte[] outgoingBuffer = new byte[2048];
        byte[] incomingBuffer = new byte[2048];


        while(true){ // As of now, Single Threaded, Later Make Multi Thread.
            //Socket socket = listener.accept(); //Ok here the server stops to listen for incoming comnnections and shit.

            DatagramPacket datagramPacket = new DatagramPacket(incomingBuffer,incomingBuffer.length);

            serverSocket.receive(datagramPacket);
            int clientPort = datagramPacket.getPort();
            InetAddress clientAddress = datagramPacket.getAddress();


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(datagramPacket.getData());
            String data = outputStream.toString().trim();

            System.out.println("Client Connection Accepted");
            System.out.println("Data: "+ data);

            System.out.println("Requesting Zone: "+ data);
            ServerZone lookup = null;
            for (ServerZone i  : this.zoneServers){
                if(i.getName().equals(data)  )
                    lookup = i;
            }


            if(lookup == null){
                String response = "404;Zone : "+data +" not found on the server";
                System.out.println(response);
                DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                this.serverSocket.send(responsePacket);
            }else{
                String response = "200; Zone "+data +" found on Server ;"+lookup.getMulticastAddr()+";"+lookup.getPetitionAddr()+";"+lookup.getPort();
                System.out.println(response);
                DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                this.serverSocket.send(responsePacket);
            }




        }



    }

    private void createZoneServer(String name, String multicastIP, String petitionIP) {
        if(this.zoneServers == null)
            this.zoneServers = new ArrayList<ServerZone>();
        try {
            ServerZone zone = new ServerZone(name,multicastIP, petitionIP);
            zoneServers.add(zone);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("Zone Server Created Succesfully");
    }
}
