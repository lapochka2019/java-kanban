import server.HttpTaskServer;
import service.Managers;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.startServer();
    }
}
