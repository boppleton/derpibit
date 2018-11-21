package utils;

import java.math.BigDecimal;

public class SingleTrade {

    String pair;
    String side;
    int amt;
    BigDecimal price;

    public SingleTrade(String pair, String side, int amt, BigDecimal price) {
        this.pair = pair;
        this.side = side;
        this.amt = amt;
        this.price = price;
    }


    public String getPair() {
        return pair;
    }

    public String getSide() {
        return side;
    }

    public int getAmt() {
        return amt;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
