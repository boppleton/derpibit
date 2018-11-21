package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Formatter {

    public static double getRoundedPrice(double p) {

        if (p % 0.5 == 0) {
            return p;
        } else {
            return Math.floor(p);
        }


    }

    public static BigDecimal getethpointoh5round(double p) {


        if (p % 0.05 == 0) {
            return new BigDecimal(p).setScale(2, RoundingMode.HALF_EVEN);
        } else {


            return new BigDecimal(p - (p%0.05)).setScale(2, RoundingMode.HALF_EVEN);
        }
    }

    public static BigDecimal getpoint5round(double p) {
        if (p % 0.5 == 0) {
            return new BigDecimal(p).setScale(1, RoundingMode.UP);
        } else if (p % 1 > 0.7) {

            return new BigDecimal(Math.ceil(p)).setScale(1, RoundingMode.UP);

        } else if (p % 1 < 0.3) {

            return new BigDecimal(Math.floor(p)).setScale(1, RoundingMode.UP);

        } else {

            return new BigDecimal(p-(p%1) + 0.5).setScale(1, RoundingMode.UP);
        }
    }

    public static int getNumber(String text) {

        text = text.replaceAll("\\D", "");

        return Integer.parseInt(text);

    }

    public static double roundForSymbol(double price, String pair) {

        if (pair.contains("XBTUSD") || pair.contains("XBT")){
            return Formatter.getpoint5round(price).doubleValue();
        } else if (pair.contains("ETHUSD")){
            return Formatter.getethpointoh5round(price).doubleValue();
        }  else if (pair.contains("BCH")) {
            return new BigDecimal(price).setScale(4, RoundingMode.HALF_EVEN).doubleValue();

        } else {
            return new BigDecimal(price).setScale(8, RoundingMode.HALF_EVEN).doubleValue();
        }



    }

    //todo: for adding new pairs need this   ^  &  v

    public static double getGapForPair(String pair) {

        if (pair.contains("XBT")) {
            return 0.5;
        } else if (pair.contains("ETH")) {
            return .05;
        } else if (pair.contains("BCH")) {
            return 0.0001;
        } else {
            return 0;
        }

    }
}
