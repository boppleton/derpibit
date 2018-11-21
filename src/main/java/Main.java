import gui.GUI;
import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.SubstanceSlices;
import org.pushingpixels.substance.api.skin.GraphiteChalkSkin;
import websocket.DeribitWebsocketClient;

import javax.swing.*;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

public class Main {

    private static GUI gui;

    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException, URISyntaxException {




        System.out.println("[ derpibit v0.1 ]");

        gui = new GUI();

        DeribitWebsocketClient ws = new DeribitWebsocketClient("4fWKmrCpPJiqv", "FKTNALDRYDOO4QO2DB3SEQELXUZZWRLK");


        // anything inside of this will run on the Event Dispatch thread, for the java swing UI
        SwingUtilities.invokeLater(() -> {

            // skin stuff
            SubstanceCortex.GlobalScope.setSkin(new GraphiteChalkSkin());
            SubstanceCortex.GlobalScope.setFocusKind(SubstanceSlices.FocusKind.NONE);
            JFrame.setDefaultLookAndFeelDecorated(true);

            // initial size
            gui.setSize(380, 760);

            // center of screen
            gui.setLocationRelativeTo(null);

            // x button
            gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // make the main frame visible
            gui.setVisible(true);

        });


    }
}
