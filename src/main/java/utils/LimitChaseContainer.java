package utils;

import java.util.ArrayList;

public class LimitChaseContainer {

    private static ArrayList<LimitChase> singleChaseList = new ArrayList<>();

//    private static ArrayList<LimitChaseScaled> scaledChaseList = new ArrayList<>();



    public static ArrayList<LimitChase> getSingleChaseList() {
        return singleChaseList;
    }

    public static void addChaseSingle(LimitChase chase) {
        System.out.println("adding chasesingle to list..");
        singleChaseList.add(chase);
    }

    public static void removeChaseSingle(LimitChase chase) {
        singleChaseList.remove(chase);
    }



//    public static ArrayList<LimitChaseScaled> getScaledChaseList() {
//        return scaledChaseList;
//    }

//    public static void addChaseScale(LimitChaseScaled chase) {
//        scaledChaseList.add(chase);
//    }

//    public static void removeChaseScaled(LimitChaseScaled chase) {
//        scaledChaseList.remove(chase);
//    }


}
