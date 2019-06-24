package utils;

import data.BidAsk;
import gui.GUI;
import websocket.DeribitWebsocketClient;

import java.security.NoSuchAlgorithmException;

public class LimitChase {

    private boolean active;

    private String label;

    private long placedId = -1;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;

    }

    public long getPlacedId() {
        return placedId;
    }

    private int size;

    private boolean buy;

    public int getSize() {
        return size;
    }

    public boolean isBuy() {
        return buy;
    }

    public LimitChase(double size, boolean buy) throws InterruptedException {

        this.size = (int) size;

        this.buy = buy;



        active = true;

        label = "chase" + Math.random();


        Thread t = new Thread(()->{


        System.out.println("start limitchase " + buy + " " + size);

//        System.out.println("bid: " + BidAsk.getBid());

        try {
            DeribitWebsocketClient.getInstance().limit(buy, size, buy?BidAsk.getBid()+30:1, label, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("sleep 1s to wait for limitchase initial id");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            placedId = DeribitWebsocketClient.getInstance().getLastOrderEventID();

//        System.out.println("placedId: " + placedId);

        Thread tloop = new Thread(() -> {

            while (active) {

//                System.out.println("chaseloop");

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                System.out.println("get orderstate " + placedId);

                try {
                    DeribitWebsocketClient.getInstance().orderState(String.valueOf(placedId));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

//                System.out.println("update " + placedId);

                try {
                    DeribitWebsocketClient.getInstance().ammend(String.valueOf(placedId), size, buy?BidAsk.getBid()+90:1);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        });
        tloop.start();

        });
        t.start();

    }

    public void closeOut() throws NoSuchAlgorithmException {

        DeribitWebsocketClient.getInstance().cancelOrder(String.valueOf(placedId));

    }
}
