package websocket;

import data.BidAsk;
import gui.GUI;
import gui.ScaledOrderPanel;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import utils.Formatter;
import utils.Lines;
import utils.ScaledOrder;


import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DeribitWebsocketClient extends WebSocketClient {

    private static DeribitWebsocketClient instance;

    private String k,s = null;

    public DeribitWebsocketClient(String k, String s) throws URISyntaxException, InterruptedException, NoSuchAlgorithmException {
        super(new URI("wss://www.deribit.com/ws/api/v1/"));

        instance = this;

        String[] acc = Lines.getAccount();

        this.k = acc[0];
        this.s = acc[1];

        connectBlocking();

//        System.out.println(k + " --- " + s);

        initialSubs();

        startPositionsThread();

//        limit(false, 5, 7000);

//        Thread.sleep(5000);

//        cancelOrder("8463849496");
//        cancelAll();



    }

    private void startPositionsThread() {

        Thread t = new Thread(()->{

            for (;;) {

//                System.out.println("position check");

                try {
                    getPositions();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
        t.start();
    }


    ////
    //   main post-er, maybe cleanup a bit?
    ///
    private void post(String type, ArrayList<String> argnames, ArrayList<String> argvalues, String stringIndices) throws NoSuchAlgorithmException {

        long nonce = System.currentTimeMillis();

        StringBuilder sig = new StringBuilder(
                "_=" + nonce + "&" +
                "_ackey=" + k + "&" +
                "_acsec=" + s + "&" +
                "_action=" + "/api/v1/private/" + type);

        for (int i = 0; i < argnames.size(); i++) {
            sig.append("&" + argnames.get(i) + "=" + argvalues.get(i));
        }

        String mac = encode(sig.toString());

        StringBuilder limitString = new StringBuilder("{\"action\": \"/api/v1/private/" + type + "\", \"arguments\": " +
                "{");

        for (int i = 0; i < argnames.size(); i++) {
            if (i > 0) {
                limitString.append(",");
            }
            limitString.append("\"" + argnames.get(i) + "\":" + (stringIndices.contains(String.valueOf(i))?"\"":"") + argvalues.get(i) + (stringIndices.contains(String.valueOf(i))?"\"":"") );
        }

               limitString.append("}, \"sig\":\""
                + k + "." + nonce + "." + mac + "\"}");

//        System.out.println(limitString.toString());

        send(limitString.toString());

    }





    ////
    //  limit, clean
    ///
    public void limit(boolean buy, double contracts, double price) throws NoSuchAlgorithmException {

        ArrayList<String> argnames = new ArrayList<>();
        ArrayList<String> argvalues = new ArrayList<>();

        argnames.add("instrument");
        argvalues.add(GUI.getInstance().getPair());

        argnames.add("post_only");
        argvalues.add("true");

        argnames.add("price");
        argvalues.add(String.valueOf((Formatter.getpoint5round(price).doubleValue()%1==0?Formatter.getpoint5round(price).intValue():Formatter.getpoint5round(price))));

        argnames.add("quantity");
        argvalues.add(String.valueOf((int)contracts));



        post(buy?"buy":"sell", argnames, argvalues, "0");


    }

    ////
    //  get positions
    ///

    public void getPositions() throws NoSuchAlgorithmException {
        ArrayList<String> argnames = new ArrayList<>();
        ArrayList<String> argvalues = new ArrayList<>();

//        argnames.add("orderId");
//        argvalues.add(id);

        post("positions", argnames, argvalues, "0");
    }

    ////
    // cancel single order, clean
    //
    public void cancelOrder(String id) throws NoSuchAlgorithmException {

        ArrayList<String> argnames = new ArrayList<>();
        ArrayList<String> argvalues = new ArrayList<>();

        argnames.add("orderId");
        argvalues.add(id);

        post("cancel", argnames, argvalues, "0");
    }
    ////
    //  cancel all, clean
    ///
    public void cancelAll() throws NoSuchAlgorithmException {

        ArrayList<String> argnames = new ArrayList<>();
        ArrayList<String> argvalues = new ArrayList<>();

        argnames.add("instrument");
        argvalues.add(GUI.getInstance().getPair());

        post("cancelall", argnames, argvalues, "0");

    }



    ////// ws messages
    @Override
    public void onMessage(String message) {

//        System.out.println(message);

        if (message.contains("\"success\":true,\"message\":\"subscribed\"")) {
            System.out.println("successfully connected to deribit websocket");
        } else if (message.contains("trade_event")) {
            tradeMessage(message);
        } else if (message.contains("estLiqPrice") && message.contains("realizedPl") && message.contains("maintenanceMargin")) {
            positionMessage(message);
        } else if (message.contains("\"result\":[]")) {
            //empty position
            positionMessage(message);

        }





    }

    private void positionMessage(String message) {

        String contracts = "";
        String side = "";
        String entry = "";
        String liq = "";
        String upnl = "";
        String rpnl = "";


        try {

            contracts = message.substring(message.indexOf("\"size\":") + 7, message.indexOf(",\"amount"));
            side = message.substring(message.indexOf("direction\":\"") + 12, message.indexOf("\",\"sizeBtc"));
            entry = message.substring(message.indexOf("\"averagePrice\":") + 15, message.indexOf(",\"direction"));
            liq = message.substring(message.indexOf("\"estLiqPrice\":") + 14, message.indexOf(",\"markPrice"));

            upnl = message.substring(message.indexOf("\"floatingPl\":") + 13, message.indexOf(",\"realizedPl"));
            rpnl = message.substring(message.indexOf("realizedPl\":") + 12, message.indexOf(",\"estLiqPr"));

        } catch (Exception e) {

        }

//        System.out.println("got position - " + message);

        if (!message.contains("\"result\":[]")) {
            ScaledOrderPanel.updatePosition(side, Double.valueOf(contracts), Double.valueOf(entry), Double.valueOf(liq), Double.valueOf(upnl), Double.valueOf(rpnl));

        } else {
//            System.out.println("setting 0 position");
            ScaledOrderPanel.updatePosition("flat", 0, 0, 0,0,0);
        }
    }

    private void tradeMessage(String message) {

        try {

            String[] trades = message.split("\"quantity\":");

//            System.out.println("new trade bunch:");

            for (String s : trades) {
//                System.out.println("trade: " + s);

                String contracts = message.substring(message.indexOf("\"quantity\":") + 11, message.indexOf(",\"amount"));
                String side = message.substring(message.indexOf("direction\":\"") + 12, message.indexOf("\",\"orderId"));
                String price = message.substring(message.indexOf("price\":") + 7, message.indexOf(",\"direction"));

//                System.out.println("[" + side + "] contracts[" + contracts + "] price[" + price + "]");

                if (side.contains("bid")) {
                    BidAsk.setAsk(Double.valueOf(price));
                    BidAsk.setBid(Double.valueOf(price) - 0.5);
                } else {
                    BidAsk.setBid(Double.valueOf(price));
                    BidAsk.setAsk(Double.valueOf(price) + 0.5);
                }

                ScaledOrderPanel.updateCurrentBid(Double.valueOf(price));

            }

//            System.out.println("new bidask: " + BidAsk.getBid() + "/" + BidAsk.getAsk());




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // startup sockets
    private void initialSubs() throws NoSuchAlgorithmException {

        long nonce = System.currentTimeMillis();

        String sig = "_=" + nonce + "&" +
                "_ackey=" + k + "&" +
                "_acsec=" + s + "&" +
                "_action=" + "/api/v1/private/subscribe" + "&" +
                "event=" + "trademy_tradeuser_order" + "&" +
                "instrument=" + "BTC-PERPETUAL";

        String mac = encode(sig);

        send("{\"action\": \"/api/v1/private/subscribe\", \"arguments\": {\"event\": [\"trade\",\"my_trade\",\"user_order\"],\"instrument\": [\"BTC-PERPETUAL\"]}, \"sig\":\"" + k + "." + nonce + "." + mac + "\" }");

    }



    //extras
    @Override
    public void onOpen(ServerHandshake handshakedata) {

        System.out.println("open");

    }
    @Override
    public void onClose(int code, String reason, boolean remote) { }
    @Override
    public void onError(Exception ex) { }

    public static DeribitWebsocketClient getInstance() {
        return instance;
    }

    public static String encode(final String clearText) throws NoSuchAlgorithmException {
        return new String(
                Base64.getEncoder().encode(MessageDigest.getInstance("SHA-256").digest(clearText.getBytes(StandardCharsets.UTF_8))));
    }

}
