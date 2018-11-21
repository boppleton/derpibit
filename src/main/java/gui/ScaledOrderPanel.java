package gui;

import data.BidAsk;
import utils.ScaledOrder;
import utils.SingleTrade;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class ScaledOrderPanel extends JPanel {

    public ScaledOrder getScaledorder() {
        return scaledorder;
    }

    public ScaledOrder scaledorder;

    private JPanel mainPanel;

    GridBagConstraints gbc = new GridBagConstraints();

    private JRadioButton bidsButton;

    private JRadioButton asksButton;
    private JSpinner totalContractsField;
    private JSpinner numOrdersSpinner;
    private JSpinner upperPriceSpinner;
    private JSpinner lowerPriceSpinner;
    private JComboBox<String> distributionCombo;
    private JSlider weightSlider;

    private static JTextArea ordersArea;

    private JButton startButton;


    private JRadioButton priceButton;
    private JRadioButton percentButton;

    private JPanel upperPricePanel;
    private JPanel lowerPricePanel;

    private JPanel pricePercentPanel;
    private JPanel priceGapPanel;

    private JSpinner pricePercentSpinner;
    private JSpinner priceGapSpinner;

    private boolean buildDialog;

    public ScaledOrderPanel(boolean buildDialog) {

        this.buildDialog = buildDialog;

        setLayout(new GridBagLayout());

        setBorder(BorderFactory.createTitledBorder("scaled order"));

        makePanels();


    }

    public String getScaledOrderSerial() {

        String scaledSerial = "[scaledorder]"
                + "<side>" + bidsButton.isSelected()
                + "<total>" + totalContractsField.getValue().toString()
                + "<qty>" + numOrdersSpinner.getValue().toString()
                + "<percent>" + pricePercentSpinner.getValue().toString()
                + "<gap>" + priceGapSpinner.getValue().toString()
                + "<dist>" + distributionCombo.getSelectedItem().toString()
                + "<weight>" + weightSlider.getValue();


        return scaledSerial;

    }


    private void makePanels() {

        mainPanel = new JPanel(new GridBagLayout());
//        mainPanel.setBorder(BorderFactory.createTitledBorder("mainpanel"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(mainPanel, gbc);


        leftBuildPanel();

        buildScaledOrder();

        rightPreviewPanel();

        // spacers
        JPanel bottomspacer = new JPanel();
        gbc.gridy++;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
//        add(bottomspacer, gbc);

        JPanel rightspacer = new JPanel();
        gbc.gridx++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
//        add(rightspacer, gbc);


        listeners();

    }

    private void listeners() {

        bidsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildScaledOrder();
            }
        });

        asksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildScaledOrder();
            }
        });

        totalContractsField.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                buildScaledOrder();
            }
        });

        numOrdersSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                buildScaledOrder();
            }
        });

        upperPriceSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                buildScaledOrder();
            }
        });

        lowerPriceSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                buildScaledOrder();
            }
        });

        distributionCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildScaledOrder();
            }
        });

        weightSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                buildScaledOrder();
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildScaledOrder();

                try {
                    startButtonPressed();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });

        priceGapSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                buildScaledOrder();
            }
        });

        pricePercentSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                buildScaledOrder();
            }
        });

        priceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildScaledOrder();
            }
        });

        percentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildScaledOrder();
            }
        });
    }

    private void startButtonPressed() throws InterruptedException {


        ArrayList<SingleTrade> trades = scaledorder.getTrades();

        SingleTrade t = trades.get(0);

        int roundscale = 0;

        int amtscale = 0;


        System.out.println(roundscale);

        System.out.println("starting orders..");


        if (bidsButton.isSelected() && t.getPrice().doubleValue() > BidAsk.getBid() || (asksButton.isSelected() && t.getPrice().doubleValue() < BidAsk.getAsk())) {

//            GUI.getInstance().updateTopToolbarText("!! order would execute immediately");

        } else {

//            ArrayList<Bitmex.PlaceOrderCommand> orders = new ArrayList<>();

            for (SingleTrade trade : trades) {

//                orders.add(new Bitmex.PlaceOrderCommand(GUI.getInstance().getPair(), trade.getSide() == Order.OrderType.BID ? "Buy" : "Sell", trade.getAmt(), trade.getPrice(), null, "Limit", null, "ParticipateDoNotInitiate", null, null));
            }

//            BitmexRest.placeBatch(orders);

        }


    }

    private void buildScaledOrder() {

        if (priceButton.isSelected()) {
            scaledorder = new ScaledOrder(bidsButton.isSelected(),
                    Double.parseDouble(totalContractsField.getValue().toString()),
                    (int) numOrdersSpinner.getValue(),
                    Double.parseDouble(upperPriceSpinner.getValue().toString()),
                    Double.parseDouble(lowerPriceSpinner.getValue().toString()),
                    distributionCombo.getSelectedItem().toString(),
                    weightSlider.getValue());
        } else {

            double upper;
            double lower;

            double gap = (Double.parseDouble(priceGapSpinner.getValue().toString()) * .01) * BidAsk.getBid();

            if (bidsButton.isSelected()) {
                //if bids, upper is currentbid - gap
                upper = (BidAsk.getBid()) - gap;
                lower = upper * (1 - (Double.parseDouble(pricePercentSpinner.getValue().toString()) * .01));
            } else {
                //if asks, lower is currentask + gap
                lower = (BidAsk.getAsk()) + gap;
                upper = lower * (1 + (Double.parseDouble(pricePercentSpinner.getValue().toString()) * .01));
            }

            scaledorder = new ScaledOrder(bidsButton.isSelected(),
                    Double.parseDouble(totalContractsField.getValue().toString()),
                    (int) numOrdersSpinner.getValue(),
                    upper,
                    lower,
                    distributionCombo.getSelectedItem().toString(),
                    weightSlider.getValue());
        }


        scaledorder.build();

    }

    public static void setScaledOrderText(String s) {


        SwingUtilities.invokeLater(() -> {

            ordersArea.setText(s);


        });


    }

    private void rightPreviewPanel() {

        // right panel
        JPanel rightPanel = new JPanel(new BorderLayout());
//        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createTitledBorder("preview"));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;
        gbc.weighty = 1;
        mainPanel.add(rightPanel, gbc);


        // start button
        startButton = new JButton("place orders");
        startButton.setVisible(!buildDialog);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("placing scaled order");
//                new ScaledOrder(5000);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = 0;
        gbc.insets = null;
//        gbc.insets = new Insets(2,5,2,5);
        rightPanel.add(startButton, BorderLayout.NORTH);

//
//        JLabel exchangeLabel = new JLabel("deribit");
//
//        gbc.gridy = 1;
//        gbc.anchor = GridBagConstraints.CENTER;
//        topPanel.add(exchangeLabel, gbc);
//
//        JLabel instrumentLabel = new JLabel("XBT/USD");
//
//        gbc.gridy = 2;
//        gbc.anchor = GridBagConstraints.CENTER;
//        topPanel.add(instrumentLabel, gbc);


        ordersArea = new JTextArea(10, 1);

        ordersArea.setText("");

        scaledorder.build();

        JScrollPane ordersScrollpane = new JScrollPane(ordersArea);
        ordersScrollpane.setPreferredSize(new Dimension(150, 100));
        ordersScrollpane.setMinimumSize(new Dimension(200, 100));

        gbc.gridy = 1;
        gbc.weighty = 1;
        rightPanel.add(ordersScrollpane, BorderLayout.CENTER);


    }

    private void leftBuildPanel() {

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createTitledBorder("build"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(leftPanel, gbc);

        // buyorsell radios
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "side"));

        bidsButton = new JRadioButton("buy");
        bidsButton.setSelected(true);
        buttonsPanel.add(bidsButton);
        buttonsPanel.add(Box.createHorizontalStrut(40));

        bidsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "side"));


                distributionCombo.setSelectedItem("down");
            }
        });

        buttonsPanel.add(Box.createHorizontalStrut(10));

        asksButton = new JRadioButton("sell");
        buttonsPanel.add(asksButton);

        asksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.red), "side"));


                distributionCombo.setSelectedItem("up");
            }
        });

        ButtonGroup buysellGroup = new ButtonGroup();
        buysellGroup.add(bidsButton);
        buysellGroup.add(asksButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        leftPanel.add(buttonsPanel, gbc);


        // total contracts
        JPanel totalContractsPanel = new JPanel(new BorderLayout());
        totalContractsPanel.setBorder(BorderFactory.createTitledBorder("total amount"));
        totalContractsField = new JSpinner(new SpinnerNumberModel(5000, .001, 10000000, 1000));
        totalContractsPanel.add(totalContractsField, BorderLayout.CENTER);
        gbc.weightx = 1;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(totalContractsPanel, gbc);


        // # of orders
        JPanel numOrdersPanel = new JPanel(new BorderLayout());
        numOrdersPanel.setBorder(BorderFactory.createTitledBorder("# of orders"));
        numOrdersSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 200, 1));
        numOrdersPanel.add(numOrdersSpinner, BorderLayout.CENTER);
        gbc.gridy++;
        leftPanel.add(numOrdersPanel, gbc);


        // price or % panel
        JPanel priceTypePanel = new JPanel();
        priceTypePanel.setLayout(new BoxLayout(priceTypePanel, BoxLayout.X_AXIS));
        priceTypePanel.setBorder(BorderFactory.createTitledBorder("price type"));

        priceButton = new JRadioButton("price");
        priceButton.setEnabled(!buildDialog);
        priceTypePanel.add(priceButton);
        priceTypePanel.add(Box.createHorizontalStrut(40));


        priceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                scaledorder.build();

                pricePercentPanel.setVisible(false);
                priceGapPanel.setVisible(false);

                upperPricePanel.setVisible(true);
                lowerPricePanel.setVisible(true);
            }
        });

        priceTypePanel.add(Box.createHorizontalStrut(5));

        percentButton = new JRadioButton("%");
        percentButton.setSelected(true);
        priceTypePanel.add(percentButton);

        percentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                scaledorder.build();

                upperPricePanel.setVisible(false);
                lowerPricePanel.setVisible(false);

                pricePercentPanel.setVisible(true);
                priceGapPanel.setVisible(true);

            }
        });

        ButtonGroup pricetypeGroup = new ButtonGroup();
        pricetypeGroup.add(priceButton);
        pricetypeGroup.add(percentButton);


        gbc.gridy++;
        gbc.weightx = 0;
        if (!buildDialog) {
            leftPanel.add(priceTypePanel, gbc);
        }

//        //bid or indicator combo
//        JComboBox<String> priceOrIndicatorCombo = new JComboBox<>();
//
////        for (SingleIndicator ind : IndicatorsContainer.getAllIndicators()) {
////            priceOrIndicatorCombo.addItem(ind.getName());
////        }
//
//        gbc.gridy++;
//        if (buildDialog) {
//            leftPanel.add(priceOrIndicatorCombo, gbc);
//        }


        // price percent
        pricePercentPanel = new JPanel(new BorderLayout());
        pricePercentPanel.setBorder(BorderFactory.createTitledBorder("% range"));
        pricePercentSpinner = new JSpinner(new SpinnerNumberModel(1, .01, 100, .05));
        pricePercentPanel.add(pricePercentSpinner, BorderLayout.CENTER);
        gbc.gridy++;
        leftPanel.add(pricePercentPanel, gbc);
        // price gap
        priceGapPanel = new JPanel(new BorderLayout());
        priceGapPanel.setBorder(BorderFactory.createTitledBorder("gap %"));
        priceGapSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, .05));
        priceGapPanel.add(priceGapSpinner, BorderLayout.CENTER);
        gbc.gridy++;
        leftPanel.add(priceGapPanel, gbc);


        // upper price
        upperPricePanel = new JPanel(new BorderLayout());
        upperPricePanel.setBorder(BorderFactory.createTitledBorder("upper price"));
        upperPriceSpinner = new JSpinner(new SpinnerNumberModel(2000, 1, 1000000, 10));
        upperPricePanel.add(upperPriceSpinner, BorderLayout.CENTER);
        gbc.gridy++;
        leftPanel.add(upperPricePanel, gbc);
        // lower price
        lowerPricePanel = new JPanel(new BorderLayout());
        lowerPricePanel.setBorder(BorderFactory.createTitledBorder("lower price"));
        lowerPriceSpinner = new JSpinner(new SpinnerNumberModel(1000, 1, 1000000, 10));
        lowerPricePanel.add(lowerPriceSpinner, BorderLayout.CENTER);
        gbc.gridy++;
        leftPanel.add(lowerPricePanel, gbc);

        upperPricePanel.setVisible(false);
        lowerPricePanel.setVisible(false);

        // distribution
        JPanel distributionPanel = new JPanel(new GridBagLayout());
        distributionPanel.setBorder(BorderFactory.createTitledBorder("distribution"));

        distributionCombo = new JComboBox<>();
        distributionCombo.addItem("flat");
        distributionCombo.addItem("up");
        distributionCombo.addItem("down");

        distributionCombo.setSelectedItem("down");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        distributionPanel.add(distributionCombo, gbc);


        weightSlider = new JSlider(1, 20, 1);
        weightSlider.setPreferredSize(new Dimension(150, 30));

        JPanel sliderPane = new JPanel(new GridBagLayout());

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        sliderPane.add(weightSlider, gbc);

        gbc.gridy++;
        distributionPanel.add(sliderPane, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        leftPanel.add(distributionPanel, gbc);


        //reduceonly
        JCheckBox reduceOnlyCheckbox = new JCheckBox("reduce-only");

        gbc.gridy++;
        add(reduceOnlyCheckbox, gbc);


        //retry overload
        JCheckBox retryOnOverload = new JCheckBox("retry on overload");
        retryOnOverload.setSelected(true);
        gbc.gridy++;
        add(retryOnOverload, gbc);

    }


}
