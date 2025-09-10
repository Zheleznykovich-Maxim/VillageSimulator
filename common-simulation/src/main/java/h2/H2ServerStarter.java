package h2;

import org.h2.tools.Server;

public class H2ServerStarter {
    public static void main(String[] args) throws Exception {
        Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
        System.out.println("H2 TCP server started at port 9092");
    }
}
