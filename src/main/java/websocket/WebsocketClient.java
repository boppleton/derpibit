package websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public abstract class WebsocketClient extends WebSocketClient {

    String accountname;
    String exchange;

    protected ObjectMapper mapper = new ObjectMapper();

    public WebsocketClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) { }

    @Override
    public void onClose(int code, String reason, boolean remote) {

        if (remote) {
            System.out.println("Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
        }
    }

    @Override
    public void onMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void onError(Exception ex) { ex.printStackTrace(); }


    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}