package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI  extends JFrame {

    private static GUI gui;

    public GUI() {
        gui = this;


        setTitle("derpibit v1.21");

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel mainPanel = new JPanel(new GridBagLayout());
        setContentPane(mainPanel);


        JTabbedPane tabs = new JTabbedPane();

        tabs.add("scaled orders", new ScaledOrderPanel(false));

        //scaled order panel
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(tabs, gbc);


        tabs.add("limit chase", new LimitChasePanel());



//
//        ///// buy or sell radios
//        JPanel buyOrSellRadioPanel = new JPanel();
//        buyOrSellRadioPanel.setLayout(new BoxLayout(buyOrSellRadioPanel, BoxLayout.X_AXIS));
//        buyOrSellRadioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "side"));
//
//        gbc.gridx = 0; gbc.gridy = 0;
//        gbc.weightx = 1; gbc.weighty = 1;
//        gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.NONE;
//        scalePanel.add(buyOrSellRadioPanel, gbc);
//
//        JRadioButton buyRadio = new JRadioButton("buy");
//        buyRadio.setSelected(true);
//        JRadioButton sellRadio = new JRadioButton("sell");
//
//        buyOrSellRadioPanel.add(buyRadio);
//        buyOrSellRadioPanel.add(sellRadio);
//
//        ButtonGroup buysellGroup = new ButtonGroup();
//        buysellGroup.add(buyRadio); buysellGroup.add(sellRadio);
//
//        buyRadio.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                buyOrSellRadioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "side"));
//            }
//        });
//
//        sellRadio.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                buyOrSellRadioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.red), "side"));
//            }
//        });
//        ///////////////////////////////////////////////////////




    }

    public static GUI getInstance() {
        return gui;
    }

    public String getPair() {
        return "BTC-PERPETUAL";
    }
}
