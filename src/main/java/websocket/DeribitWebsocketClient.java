package websocket;

import data.BidAsk;
import gui.GUI;
import gui.ScaledOrderPanel;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import utils.Formatter;
import utils.LimitChase;
import utils.LimitChaseContainer;
import utils.Lines;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DeribitWebsocketClient extends WebSocketClient {

    private int currentPosition = 0;
    private boolean currentSide;


    private static DeribitWebsocketClient instance;

    private String k, s = null;

    private String lastSentMessageType = "";

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

        getIndex();

//        getOpenOrders();

//        limit(true, 1, 2000, "");
//
//        Thread.sleep(5000);
//
//        System.out.println("--------trying to ammend ");
//
//        ammend("8487450076", 1, 4560);

//        cancelOrder("8463849496");
//        cancelAll();


    }

    private void startPositionsThread() {

        Thread t = new Thread(() -> {

            for (; ; ) {

//                System.out.println("position check");

                try {
                    getPositions();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    getOpenOrders();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(2000);
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
    private void post(String type, ArrayList<String> argnames, ArrayList<String> argvalues, String stringIndices, boolean publicc) throws NoSuchAlgorithmException {

        long nonce = System.currentTimeMillis();

        StringBuilder sig = new StringBuilder(
                "_=" + nonce + "&" +
                        "_ackey=" + k + "&" +
                        "_acsec=" + s + "&" +
                        "_action=" + "/api/v1/" + (publicc?"public":"private") + "/" + type);

        for (int i = 0; i < argnames.size(); i++) {
            sig.append("&" + argnames.get(i) + "=" + argvalues.get(i));
        }

        String mac = encode(sig.toString());

        StringBuilder limitString = new StringBuilder("{\"action\": \"/api/v1/" + (publicc?"public":"private") + "/" + type + "\", \"arguments\": " +
                "{");

        for (int i = 0; i < argnames.size(); i++) {
            if (i > 0) {
                limitString.append(",");
            }
            limitString.append("\"" + argnames.get(i) + "\":" + (stringIndices.contains(String.valueOf(i)) ? "\"" : "") + argvalues.get(i) + (stringIndices.contains(String.valueOf(i)) ? "\"" : ""));
        }

        limitString.append("}, \"sig\":\""
                + k + "." + nonce + "." + mac + "\"}");

//        System.out.println(limitString.toString());

        send(limitString.toString());

    }


    ////
    //  amment, clean
    ///
    public void ammend(String id, double contracts, double price) throws NoSuchAlgorithmException {

//        System.out.println("ammending " + id + ", " + contracts + ", " + price);

        ArrayList<String> argnames = new ArrayList<>();
        ArrayList<String> argvalues = new ArrayList<>();

        argnames.add("orderId");
        argvalues.add(id);

        argnames.add("post_only");
        argvalues.add("true");

        argnames.add("price");
        argvalues.add(Double.valueOf(String.valueOf((Formatter.getpoint5round(price).doubleValue() % 1 == 0 ? Formatter.getpoint5round(price).intValue() : Formatter.getpoint5round(price)))).toString());

        argnames.add("quantity");
        argvalues.add(String.valueOf((int) contracts));


        post("edit", argnames, argvalues, "0", false);


    }

    ////
    // get index
    //
    public void getIndex() throws NoSuchAlgorithmException {

        ArrayList<String> argnames = new ArrayList<>();
        ArrayList<String> argvalues = new ArrayList<>();


        post("index", argnames, argvalues, "0", true);
    }


    ////
    // cancel single order, clean
    //
    public void orderState(String id) throws NoSuchAlgorithmException {

        ArrayList<String> argnames = new ArrayList<>();
        ArrayList<String> argvalues = new ArrayList<>();

        argnames.add("orderId");
        argvalues.add(id);

        post("orderstate", argnames, argvalues, "0", false);
    }


    ////
    //  limit, clean
    ///
    public void limit(boolean buy, double contracts, double price, String s, boolean reduceOnly) throws NoSuchAlgorithmException {

        ArrayList<String> argnames = new ArrayList<>();
        ArrayList<String> argvalues = new ArrayList<>();

        argnames.add("instrument");
        argvalues.add(GUI.getInstance().getPair());

        argnames.add("post_only");
        argvalues.add("true");

        argnames.add("price");
        argvalues.add(String.valueOf((Formatter.getpoint5round(price).doubleValue() % 1 == 0 ? Formatter.getpoint5round(price).intValue() : Formatter.getpoint5round(price))));

        argnames.add("quantity");
        argvalues.add(String.valueOf((int) contracts));

        argnames.add("reduce_only");
        argvalues.add(reduceOnly?"true":"false");


        post(buy ? "buy" : "sell", argnames, argvalues, "0", false);


    }

    ////
    // get open orders
    //
    public void getOpenOrders() throws NoSuchAlgorithmException {

        ArrayList<String> argnames = new ArrayList<>();
        ArrayList<String> argvalues = new ArrayList<>();

        argnames.add("instrument");
        argvalues.add(GUI.getInstance().getPair());

        lastSentMessageType = "getopenorders";

        post("getopenorders", argnames, argvalues, "0", false);
    }

    ////
    //  get positions
    ///

    public void getPositions() throws NoSuchAlgorithmException {
        ArrayList<String> argnames = new ArrayList<>();
        ArrayList<String> argvalues = new ArrayList<>();

//        argnames.add("orderId");
//        argvalues.add(id);

        lastSentMessageType = "positions";

        post("positions", argnames, argvalues, "0", false);
    }

    ////
    // cancel single order, clean
    //
    public void cancelOrder(String id) throws NoSuchAlgorithmException {

        ArrayList<String> argnames = new ArrayList<>();
        ArrayList<String> argvalues = new ArrayList<>();

        argnames.add("orderId");
        argvalues.add(id);

        post("cancel", argnames, argvalues, "0", false);
    }

    ////
    //  cancel all, clean
    ///
    public void cancelAll() throws NoSuchAlgorithmException {

        ArrayList<String> argnames = new ArrayList<>();
        ArrayList<String> argvalues = new ArrayList<>();

        argnames.add("instrument");
        argvalues.add(GUI.getInstance().getPair());

        post("cancelall", argnames, argvalues, "0", false);

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
        } else if (!message.contains("commission") && message.contains("\"type\"") && message.contains("\"orderId\"") && message.contains("\"label\"") && message.contains("\"filledQuantity\"") && message.contains("\"created\"")) {
            getOpenOrdersMessage(message);

        } else if (message.contains("\"result\":[]")) {
            //empty position
//            System.out.println("blank message - " + message);
//            System.out.println("last thing - " + lastSentMessageType);

            if (lastSentMessageType.contains("position")) {
                positionMessage(message);
            } else if (lastSentMessageType.contains("getopenorders")) {
                getOpenOrdersMessage(message);
            }

            //order event
        } else if (message.contains("\"result\":{\"order\":{\"orderId\":") && message.contains("\"state\":\"open\",\"postOnly\":true") && message.contains("commission")) {

            orderEventMessage(message);

        } else if (message.contains("result\":{\"btc\"")) {
            indexMessage(message);
        }


    }

    private void indexMessage(String message) {

        String indexPrice = message.substring(message.indexOf("btc\":") + 5, message.indexOf(",\"edp\""));

        double price = Double.parseDouble(indexPrice);

        BidAsk.setBid(price);
        BidAsk.setAsk(price);

        ScaledOrderPanel.updatePriceSpinners(price-10, price-100);

    }

    private static long lastOrderEventID = 0;

    public long getLastOrderEventID() {
        return lastOrderEventID;
    }

    private void orderEventMessage(String message) {

        System.out.println("order event " + message);

        String id = message.substring(message.indexOf("orderId\":") + 9, message.indexOf(",\"type"));

        String state = message.substring(message.indexOf("state\":\"") + 8, message.indexOf("\",\"post"));

        System.out.println("id: " + id + " state: " + state);

        for (LimitChase chase : LimitChaseContainer.getSingleChaseList()) {
            if (String.valueOf(chase.getPlacedId()).contains(id) && chase.isActive() && state.contains("filled")) {

                chase.setActive(false);

                LimitChaseContainer.removeChaseSingle(chase);

            }
        }

        lastOrderEventID = Long.parseLong(id.replace("\"", ""));

        System.out.println("lasteventid: " + lastOrderEventID);


    }

    private void getOpenOrdersMessage(String message) {

        String[] orders = message.split("orderId\"");

        ScaledOrderPanel.updateOpenOrdersSize(orders.length - 1);

        ArrayList<String> ordersStrings = new ArrayList<>();

        for (String s : orders) {

            if (!s.contains("\"result\":[")) {

//                System.out.println("order: " + s);

                String id = "";

                String contracts = "";
                String side = "";
                String price = "";

                String filledAmt = "";

                String state = "";


                try {

//                    System.out.println("try id..");
//                    System.out.println(s.substring(1, s.indexOf(",\"type\":\"limit\",\"instrument\"")));

                    id = s.substring(1, s.indexOf(",\"type\":\"limit\",\"instrument\""));

                    contracts = s.substring(s.indexOf("\"quantity\":") + 11, s.indexOf(",\"filledQuantity"));
                    side = s.substring(s.indexOf("direction\":\"") + 12, s.indexOf("\",\"price"));
                    price = s.substring(s.indexOf("\"price\":") + 8, s.indexOf(",\"label\""));
                    filledAmt = s.substring(s.indexOf("\"filledQuantity\":") + 17, s.indexOf(",\"filledAmount\""));

                    state = s.substring(s.indexOf("\"state\":\"") + 9, s.indexOf("\",\"created"));

//                    System.out.println("order id: " + id + " state: " + state);

                } catch (Exception e) {
//                    e.printStackTrace();
                }

                for (LimitChase chase : LimitChaseContainer.getSingleChaseList()) {
                    if (chase.getPlacedId() == Long.parseLong(id) && chase.isActive() && state.contains("filled")) {
                        chase.setActive(false);
                        LimitChaseContainer.removeChaseSingle(chase);
                    }
                }

//            System.out.println("adding " + side + " " + contracts + " @ " + price + " - " + (Integer.parseInt(filledAmt) != 0 ? Integer.parseInt(contracts)/Integer.parseInt(filledAmt) : 0)  + "% filled ");

                ordersStrings.add(side + " " + contracts + " @ " + price + " - " + (Integer.parseInt(filledAmt) != 0 ? Integer.parseInt(contracts) / Integer.parseInt(filledAmt) : 0) + "% filled ");

            }
        }

        ScaledOrderPanel.addToOpenOrders(ordersStrings);
    }

    private void positionMessage(String message) {

//        System.out.println(message);

        String contracts = "";
        String side = "";
        String entry = "";
        String liq = "";
        String upnl = "";
        String rpnl = "";

        String markPrice = "";

        try {

            contracts = message.substring(message.indexOf("\"size\":") + 7, message.indexOf(",\"amount"));
            side = message.substring(message.indexOf("direction\":\"") + 12, message.indexOf("\",\"sizeBtc"));
            entry = message.substring(message.indexOf("\"averagePrice\":") + 15, message.indexOf(",\"direction"));
            liq = message.substring(message.indexOf("\"estLiqPrice\":") + 14, message.indexOf(",\"markPrice"));

            upnl = message.substring(message.indexOf("\"profitLoss\":") + 13, message.indexOf("}],\"message\""));
            rpnl = message.substring(message.indexOf("realizedPl\":") + 12, message.indexOf(",\"estLiqPr"));

        } catch (Exception e) {

//            e.printStackTrace();

        }

//        System.out.println("got position - " + message);

        if (!message.contains("\"result\":[]")) {
            ScaledOrderPanel.updatePosition(side, Double.valueOf(contracts), Double.valueOf(entry), Double.valueOf(liq), Double.valueOf(upnl), Double.valueOf(rpnl));

//            System.out.println("current position: " + Integer.valueOf(contracts));

            currentPosition = Integer.valueOf(contracts);

            currentSide = side.contains("buy");
        } else {
//            System.out.println("setting 0 position");
            ScaledOrderPanel.updatePosition("flat", 0, 0, 0, 0, 0);
        }
    }

    private void tradeMessage(String message) {

        try {

            String[] trades = message.split("\"quantity\":");

//            System.out.println("new trade bunch:");

            for (String s : trades) {

                if (!s.contains("\"result\":[")) {

                    s = s.substring(0, 150);

//                    System.out.println("trade: " + s);

                    String contracts = "";
                    String side = "";
                    String price = "";

                    try {
//                        contracts = s.substring(message.indexOf("\"quantity\":") + 11, s.indexOf(",\"amount"));
//                        side = s.substring(message.indexOf("direction\":\"") + 12, s.indexOf("\",\"orderId"));


                        price = s.substring(s.indexOf(",\"price\":", 0) + 9, s.indexOf(",\"direction", 0));

//                        System.out.println("pr: " + price);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                System.out.println("[" + side + "] contracts[" + contracts + "] price[" + price + "]");

                    if (side.contains("bid")) {
                        BidAsk.setAsk(Double.valueOf(price));
                        BidAsk.setBid(Double.valueOf(price) - 0.5);
                    } else {
                        BidAsk.setBid(Double.valueOf(price));
                        BidAsk.setAsk(Double.valueOf(price) + 0.5);
                    }

//                    System.out.println("trade price: " + price);

                    ScaledOrderPanel.updateCurrentBid(Double.valueOf(price));
                }
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

//        System.out.println("sending " + "{\"action\": \"/api/v1/private/subscribe\", \"arguments\": {\"event\": [\"trade\",\"my_trade\",\"user_order\"],\"instrument\": [\"BTC-PERPETUAL\"]}, \"sig\":\"" + k + "." + nonce + "." + mac + "\" }");

        send("{\"action\": \"/api/v1/private/subscribe\", \"arguments\": {\"event\": [\"trade\",\"my_trade\",\"user_order\"],\"instrument\": [\"BTC-PERPETUAL\"]}, \"sig\":\"" + k + "." + nonce + "." + mac + "\" }");

    }


    //extras
    @Override
    public void onOpen(ServerHandshake handshakedata) {

        System.out.println("open");

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onError(Exception ex) {
    }

    public static DeribitWebsocketClient getInstance() {
        return instance;
    }

    public static String encode(final String clearText) throws NoSuchAlgorithmException {
        return new String(
                Base64.getEncoder().encode(MessageDigest.getInstance("SHA-256").digest(clearText.getBytes(StandardCharsets.UTF_8))));
    }

    public void closeSomeOfPosition(int value) throws InterruptedException {

//        System.out.println("current pos: " + currentPosition);

//        System.out.println("close " + value + "%: " + (Math.abs(currentPosition)*value)/100);

        int closeAmt = (Math.abs(currentPosition)*value)/100;

        LimitChaseContainer.addChaseSingle(new LimitChase(closeAmt, currentPosition<0));
    }
}
