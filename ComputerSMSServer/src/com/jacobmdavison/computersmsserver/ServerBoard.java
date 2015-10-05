package com.jacobmdavison.computersmsserver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.io.StringWriter;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class ServerBoard extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextPane messagesArea;
	private JButton sendButton;
	private JTextField message;
	private JTextField number;
	private JButton startServer;
	private TCPServer mServer;
	private DiffieHellmanModule diffieHellmanModule;
	private StyledDocument doc;

	public ServerBoard() {

		super("ServerBoard");

		JPanel panelFields = new JPanel();
		panelFields.setLayout(new BoxLayout(panelFields, BoxLayout.X_AXIS));

		JPanel panelFields2 = new JPanel();
		panelFields2.setLayout(new BoxLayout(panelFields2, BoxLayout.X_AXIS));

		JPanel panelFields3 = new JPanel();
		panelFields3.setLayout(new BoxLayout(panelFields3, BoxLayout.X_AXIS));
		JPanel panelFields4 = new JPanel();
		panelFields4.setLayout(new BoxLayout(panelFields4, BoxLayout.X_AXIS));

		// here we will have the text messages screen
		messagesArea = new JTextPane();
		messagesArea.setEditable(false);
		EmptyBorder eb = new EmptyBorder(new Insets(5, 5, 5, 5));
		messagesArea.setBorder(eb);
		messagesArea.setMargin(new Insets(5, 5, 5, 5));
		messagesArea.setPreferredSize(new Dimension(400, 300));
		messagesArea.setText("");

		doc = messagesArea.getStyledDocument();

		// Scroll pane
		JScrollPane scrollPane = new JScrollPane(messagesArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get the message from the text view
				String messageText = message.getText();

				// Get the phone number from the text view
				String phoneNumber = number.getText();
				phoneNumber = phoneNumber.replaceAll("[^\\d]", "");

				// add message to the message area
				appendToPane("\n" + phoneNumber + ": " + messageText,
						Color.BLACK);
				SMSMessage smsMessage = new SMSMessage();
				smsMessage.setNumber(phoneNumber);
				smsMessage.setBody(messageText);
				StringWriter writer = new StringWriter();

				// Serialize into XML
				try {
					JAXBContext jaxbContext = JAXBContext
							.newInstance(SMSMessage.class);
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
					jaxbMarshaller.marshal(smsMessage, writer);
				} catch (JAXBException e1) {

					System.out.println(e1.toString());
				}

				// send the message to the client
				mServer.sendMessage(writer.toString());
				// clear text
				message.setText("");
			}
		});
		sendButton.setEnabled(false);

		startServer = new JButton("Start");
		startServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				diffieHellmanModule = new DiffieHellmanModule();
				// disable the start button
				startServer.setEnabled(false);

				// creates the object OnMessageReceived asked by the TCPServer
				// constructor
				mServer = new TCPServer(ServerBoard.this,
						new TCPServer.OnMessageReceived() {
							@Override
							// this method declared in the interface from
							// TCPServer class is implemented here
							// this method is actually a callback method,
							// because it will run every time when it will be
							// called from
							// TCPServer class (at while)
							public void messageReceived(String message) {
								try {
									JAXBContext jaxbContext = JAXBContext
											.newInstance(SMSMessage.class);
									Unmarshaller jaxbUnmarshaller = jaxbContext
											.createUnmarshaller();
									StringReader reader = new StringReader(
											message);
									SMSMessage incomingMessage = (SMSMessage) jaxbUnmarshaller
											.unmarshal(reader);
									if (incomingMessage.getType().equals("sms")) {
										appendToPane(
												"\n "
														+ incomingMessage
																.getNumber()
														+ ": "
														+ incomingMessage
																.getBody(),
												Color.BLUE);
									} else if (incomingMessage.getType()
											.equals("call")) {
										appendToPane(
												"\n "
														+ incomingMessage
																.getBody()
														+ " from "
														+ incomingMessage
																.getNumber(),
												Color.RED);
									} else {
										appendToPane("\n Invalid Message",
												Color.RED);
									}
								} catch (JAXBException e1) {
									System.out.println(e1.toString());
								}
							}
						}, diffieHellmanModule);

				mServer.start();

			}
		});

		// the box where the user enters the text (EditText is called in
		// Android)
		number = new JTextField();
		number.setPreferredSize(new Dimension(400, 20));

		// the box where the user enters the text (EditText is called in
		// Android)
		message = new JTextField();
		message.setSize(200, 20);

		// add the buttons and the text fields to the panel
		panelFields.add(scrollPane);
		panelFields.add(startServer);

		panelFields2.add(new JLabel(" Phone Number: "));
		panelFields2.add(number);

		panelFields3.add(new JLabel(" Message: "));
		panelFields3.add(message);

		panelFields4.add(sendButton);

		getContentPane().add(panelFields);
		getContentPane().add(panelFields2);
		getContentPane().add(panelFields3);
		getContentPane().add(panelFields4);

		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		setSize(400, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void enableSendButton() {
		sendButton.setEnabled(true);
	}

	public void appendToPane(String msg, Color c) {
		SimpleAttributeSet attribute = new SimpleAttributeSet();
		StyleConstants.setForeground(attribute, c);
		try {
			doc.insertString(doc.getLength(), msg, attribute);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
