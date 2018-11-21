package data;

public class BidAsk {

    private static double bid = -1;
    private static double ask = -1;


    public static double getBid() {
        return bid;
    }

    public static void setBid(double bid) {
        BidAsk.bid = bid;
    }

    public static double getAsk() {
        return ask;
    }

    public static void setAsk(double ask) {
        BidAsk.ask = ask;
    }
}
