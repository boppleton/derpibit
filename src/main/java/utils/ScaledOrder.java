package utils;

import data.BidAsk;
import gui.GUI;
import gui.ScaledOrderPanel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class ScaledOrder extends CustomOrder {

    private boolean side;
    private double totalAmount;
    private int totalOrders;
    private double upperPrice;
    private double lowerPrice;
    private String distribution;
    private int weight;
    private boolean reduceonly;


    private static ArrayList<SingleTrade> trades = new ArrayList<>();

    private static String tradesString = "";

    public ScaledOrder(boolean side, double totalAmount, int totalOrders, double upperPrice, double lowerPrice, String distribution, int weight, boolean reduceonly) {
        this.side = side;
        this.totalAmount = totalAmount;
        this.totalOrders = totalOrders;
        this.upperPrice = upperPrice;
        this.lowerPrice = lowerPrice;
        this.distribution = distribution;
        this.weight = weight;
        this.reduceonly = reduceonly;

        build();
    }

    public ArrayList<SingleTrade> getTrades() {
        return trades;
    }

    public void build() {


        String pair = GUI.getInstance().getPair();

        String orderType = side ? "Buy" : "Sell";

        int numberOfOrders = totalOrders;


        double totalSize = totalAmount;

        ArrayList<Double> amounts = new ArrayList<>();
        ArrayList<Double> prices = new ArrayList<>();

        //
        // get prices
        //
        double rangeAmt = upperPrice - lowerPrice;
        double steps = rangeAmt / (numberOfOrders - 1);

        for (int i = 0; i < numberOfOrders; i++) {
            if (i == 0) {
                prices.add(Formatter.getpoint5round(upperPrice).doubleValue());
            } else if (i == numberOfOrders - 1) {
                prices.add(Formatter.getpoint5round(lowerPrice).doubleValue());
            } else {

                double bd = lowerPrice + (steps * i);

                bd = Formatter.getpoint5round(bd).doubleValue();


                prices.add(bd);
            }
        }

        Collections.sort(prices);

        Collections.reverse(prices);



        //
        // get amounts
        //
        ArrayList<Double> distributedTotal = new ArrayList<>();
        double allSum = 0;

        double singleOrderAmt = 0;
        if (distribution.contains("flat")) {

            singleOrderAmt = totalSize / numberOfOrders;

            BigDecimal bd = new BigDecimal(Double.toString(singleOrderAmt));
            bd = bd.setScale(4, RoundingMode.HALF_EVEN);

            singleOrderAmt = bd.doubleValue();

            for (int i = 0; i < numberOfOrders; i++) {
                distributedTotal.add(singleOrderAmt);

            }

        } else if (distribution.contains("randomizer")) {
            Random random = new Random();




            boolean add = true;
            double diff = 0;

            if (numberOfOrders > 1) {

                for (int i = 0; (numberOfOrders % 2 == 0 ? i < numberOfOrders : i < numberOfOrders - 1); i++) {


                    singleOrderAmt = totalSize / numberOfOrders;


                    if (add) {
                        double ran = random.nextDouble() * (weight*.1);
                        System.out.println("slider- " + weight + "ran- " + ran);
                        double thisOne = singleOrderAmt * (ran + 1);
                        distributedTotal.add(i, thisOne);
                        diff = singleOrderAmt - thisOne;
                        add = false;
                    } else {
                        distributedTotal.add(singleOrderAmt + diff);
                        add = true;
                    }
                }
                if (distributedTotal.size() < numberOfOrders) {
                    distributedTotal.add(singleOrderAmt);
                }
            } else {
                distributedTotal.add(singleOrderAmt);
            }


            for (int i = 0; i < distributedTotal.size(); i++) {
                //add all
                allSum += distributedTotal.get(i);
            }


        } else if (distribution.contains("up") || distribution.contains("down")) {

            ArrayList<Double> pricePointPercentages = new ArrayList<>();

            // Min and max percentage of the amount allocated per price point
            double minPercentage = 0.04;
//            System.out.println("minPercent: " + minPercentage);
            double maxPercentage = 0.04 * (1.5+(weight));
//            System.out.println("macxPercent: " + maxPercentage);


            for (int i = 0; i < numberOfOrders; i++) {
                pricePointPercentages.add(i, (minPercentage + (i * (maxPercentage - minPercentage)) / (numberOfOrders + 1)));
            }


            double leftover = 0;
            double distributionSum = 0;
            for (Double d : pricePointPercentages) {
                distributionSum += d;
            }

            for (int i = 0; i < pricePointPercentages.size(); i++) {

                double val = (pricePointPercentages.get(i) * totalSize) / distributionSum + leftover;


                distributedTotal.add(val);

            }

            for (int i = 0; i < distributedTotal.size(); i++) {
                //add all
                allSum += distributedTotal.get(i);
            }

            if (distribution.contains("up")) {
                Collections.reverse(distributedTotal);
            }

            singleOrderAmt = totalSize / numberOfOrders;

            BigDecimal bd = BigDecimal.valueOf(singleOrderAmt);
            bd = bd.setScale(0, RoundingMode.HALF_UP);
            singleOrderAmt = bd.doubleValue();
            for (int i = 0; i < numberOfOrders; i++) {
                amounts.add(singleOrderAmt);
            }

        }






        trades.clear();

        tradesString = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < prices.size(); i++)

        {
            trades.add(new SingleTrade(pair, orderType, (distributedTotal.get(i).intValue()<1?1:distributedTotal.get(i).intValue()), BigDecimal.valueOf(Formatter.roundForSymbol(prices.get(i), pair) )));
            sb.append(orderType.toLowerCase() + " " + new BigDecimal(distributedTotal.get(i)).setScale(0, RoundingMode.HALF_DOWN) + " at " + Formatter.roundForSymbol(prices.get(i), pair) + (reduceonly?" -reduce":"") + "\n");

        }

        tradesString = sb.toString();
        ScaledOrderPanel.setScaledOrderText(tradesString);

//        GUI.enableStartButton();


        for (int i = 0; i < prices.size(); i++) {

//            System.out.println("price: " + prices.get(i) + " amt: " + distributedTotal.get(i) + " total: " + allSum);

        }

    }

    private void startButton() throws InterruptedException {
        System.out.println("getting price roundingscale");

        SingleTrade t = trades.get(0);

        int roundscale = 0;

        int amtscale = 0;


        System.out.println(roundscale);

        System.out.println("starting orders..");



        if ((t.side == "Buy" && t.price.doubleValue() > BidAsk.getBid() || (t.side == "Sell" && t.price.doubleValue() < BidAsk.getAsk()))) {

//            GUI.getInstance().updateTopToolbarText("!! order would execute immediately");

        } else {

//            ArrayList<Bitmex.PlaceOrderCommand> orders = new ArrayList<>();

            for (SingleTrade trade : trades) {

//                orders.add(new Bitmex.PlaceOrderCommand(trade.pair.toUpperCase(), trade.side == Order.OrderType.BID?"Buy":"Sell", trade.amt, trade.price, null, "Limit", null, "ParticipateDoNotInitiate", null, null));
            }

//            BitmexRest.placeBatch(orders);

        }


    }




}
