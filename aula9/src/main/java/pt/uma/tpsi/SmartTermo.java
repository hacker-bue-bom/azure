package pt.uma.tpsi;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
//azure
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.Message;

import java.io.IOException;
import java.net.URISyntaxException;

public class SmartTermo extends JFrame{
    // panels
    private JPanel mainPanel;
    private JPanel statusPanel;
    private JPanel tempPanel;
    private JPanel humdPanel;
    private JScrollPane mainScrollPanel;
    private JPanel actualTempPanel;
    private JPanel changeTempFormatPanel;
    private JPanel desiredTempPanel;
    private JPanel actualHumPanel;
    private JPanel onOffPanel;
    private JPanel desiredHumPanel;
    private JPanel scrollPanel;

    // text fields
    private JTextField actualTempTextField;
    private JTextField desiredTempTextField;
    private JTextField actualHumTextField;
    private JTextField desiredHumTextField;
    private JTextField onOffTextField;
    private JTextField dateTextField;
    private JTextArea scrollTextArea;

    // labels
    private JLabel actualTempLabel;
    private JLabel desiredTempLabel;
    private JLabel actualHumLabel;
    private JLabel desiredHumLabel;
    private JLabel mainLabel;

    // buttons
    private JButton increaseTempButton;
    private JButton decreaseTempButton;
    private JButton changeTempFormatButton;
    private JButton increaseHumButton;
    private JButton decreaseHumButton;


    // attributes
    private float actualTemp;
    private float desiredTemp;
    private int tempFormat;
    private float actualHum;
    private float desiredHum;
    private Date date;
    private String loggedMessages;
    private Timer timer;

    //azure
    private String conString= "HostName=Nick-Iago-Projeto-2023.azure-devices.net;DeviceId=dispositivoID;SharedAccessKey=Fc46NVSF3wdOOup89GmGWnmPL5fqGgccdfMk9NqqRL8=";
    private IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    private DeviceClient client;
    private TimerTask task;
    private Timer timerSendMsg;

    // constructors
    public SmartTermo(String title) throws URISyntaxException, IOException {
        // create panel
        super(title);
        this.setContentPane(mainPanel);
        this.pack();
        // closing operation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logEvents("App closed");
                System.exit(0);
            }
        });
        // initializations
        actualTemp = 22; // represents in C
        desiredTemp = 22; // represents in C
        actualHum = 50;
        desiredHum = 50;
        tempFormat = 0; // 0 -> C, other -> F
        loggedMessages = "";
        updateTextFields();
        logEvents("The app started");

        // timer task
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Random randomNumber = new Random();
                if (actualTemp < desiredTemp && randomNumber.nextDouble() <= 0.8)
                    actualTemp += 0.5;
                else if (randomNumber.nextDouble() >= 0.7)
                    actualTemp -= 0.5;
                updateTextFields();
            }
        };
        timer = new Timer("timer for  simulation");
        timer.schedule(tt, 1000, 1000);

        // button actions
        changeTempFormatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tempFormat == 0){
                    tempFormat = 1; // F
                    logEvents("Temperature changed format to F");
                }
                else{
                    tempFormat = 0; // C
                    logEvents("Temperature changed format to C");
                }
                updateTextFields();
            }
        });
        increaseTempButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualTemp += 0.5;
                desiredTemp += 0.5;
                updateTextFields();
                logEvents("Temperature increased to " + actualTemp + " ºC");
            }
        });
        decreaseTempButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualTemp -= 0.5;
                desiredTemp -= 0.5;
                updateTextFields();
                logEvents("Temperature decreased to " + actualTemp + " ºC");
            }
        });
        increaseHumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualHum += 0.5;
                desiredHum += 0.5;
                updateTextFields();
                logEvents("Humidity increased to " + actualHum + "%");
            }
        });
        decreaseHumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualHum -= 0.5;
                desiredHum -= 0.5;
                updateTextFields();
                logEvents("Humidity decreased to " + actualHum + "%");
            }
        });
        //azure
        client = new DeviceClient(conString, protocol);
        client.open();
    }

    // methods
    private void updateTextFields(){
        // temperature
        if (tempFormat == 0){
            actualTempTextField.setText(String.valueOf(actualTemp) + " ºC");
            desiredTempTextField.setText(String.valueOf(desiredTemp) + " ºC");
            changeTempFormatButton.setText("ºF");
        }
        else {
            actualTempTextField.setText(String.valueOf(actualTemp * 9 / 5 + 32) + " ºF");
            desiredTempTextField.setText(String.valueOf(desiredTemp * 9 / 5 + 32) + " ºF");
            changeTempFormatButton.setText("ºC");
        }
        // humidity
        actualHumTextField.setText(String.valueOf(actualHum + "%"));
        desiredHumTextField.setText(String.valueOf(desiredHum + "%"));
        // date
        date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateTextField.setText(simpleDateFormat.format(date));
        // heating indicator
        if (actualTemp < desiredTemp)
            onOffTextField.setText("On");
        else
            onOffTextField.setText("Off");
    }

    private void logEvents(String informationToLog){
        date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyy hh:mm:ss");
        loggedMessages += simpleDateFormat.format(date) + ": " + informationToLog + '\n';
        scrollTextArea.setText(loggedMessages);
    }

    private void sendMessages() {

    }

    //azure
    public void start() {
        task = new TimerTask() {
            @Override
            public void run() {
                sendMessages
            }
        };
        timerSendMsg = new Timer();
        timerSendMsg.schedule(task);
    }

    // main
    public static void main(String[] args) {
        JFrame smartTermo = new SmartTermo("TPSI");
        smartTermo.setVisible(true);
    }
}
