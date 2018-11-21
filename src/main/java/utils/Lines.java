package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Lines {


    public static String[] getAccount() {

        String[] acct = new String[4];

        String fileName = "accounts.txt";
        Object[] s = null;
        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {


            s = stream.toArray();

            for (int i = 0; i < s.length; i++) {
                String line = s[i].toString();
                if (line.length() != 0) {

//                    System.out.println(line);

                    acct[0] = line.substring(line.indexOf("<key>") + 5, line.indexOf("<sec>"));
                    acct[1] = line.substring(line.indexOf("<sec>") + 5, line.indexOf("<end>"));

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return acct;

    }

    private static void acctPull(String line) {




    }


}
