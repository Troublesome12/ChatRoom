import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;

public class ChatClientGUI implements ActionListener{

	private JFrame frame;
	private JTextField messageField;
	BufferedReader in;
    PrintWriter out;
    JButton sendButton;
    JMenuItem mntmExit, mntmHelp, mntmAbout;
    JTextArea textArea;

	public static void main(String[] args) {
		ChatClientGUI window = new ChatClientGUI();
		window.frame.setVisible(true);	
		window.Connect();
	}

	public ChatClientGUI() {
		initialize();
	}

	
	private void initialize() {
		frame = new JFrame();
		frame.setSize(650,450);
		frame.setResizable(false);
		frame.getContentPane().setFont(new Font("Dialog", Font.PLAIN, 14));
		frame.getContentPane().setLayout(null);
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Dialog", Font.PLAIN, 14));
		textArea.setEditable(false);
		textArea.setBounds(10, 10, 630, 350);
		
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		scroll.setBounds(10, 10, 630, 350);
		frame.getContentPane().add(scroll);
		
		messageField = new JTextField();
		messageField.setEditable(false);
		messageField.setFont(new Font("Dialog", Font.PLAIN, 14));
		messageField.setBounds(10, 370, 520, 30);
		frame.getContentPane().add(messageField);
		messageField.setColumns(10);
		
		sendButton = new JButton("Send");
		sendButton.setEnabled(false);
		sendButton.setFont(new Font("Dialog", Font.BOLD, 14));
		sendButton.setBounds(540, 370, 100, 30);
		frame.getContentPane().add(sendButton);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("Dialog", Font.BOLD, 14));
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setFont(new Font("Dialog", Font.BOLD, 14));
		menuBar.add(mnFile);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.setFont(new Font("Dialog", Font.BOLD, 14));
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setFont(new Font("Dialog", Font.BOLD, 14));
		menuBar.add(mnHelp);
		
		mntmHelp = new JMenuItem("Help");
		mntmHelp.setFont(new Font("Dialog", Font.BOLD, 14));
		mnHelp.add(mntmHelp);
		
		mntmAbout = new JMenuItem("About");
		mntmAbout.setFont(new Font("Dialog", Font.BOLD, 14));
		mnHelp.add(mntmAbout);
		
		sendButton.addActionListener(this);
		messageField.addActionListener(this);
		mntmExit.addActionListener(this);
		mntmHelp.addActionListener(this);
		mntmAbout.addActionListener(this);
	}
	
	public void Send(){
		out.println(messageField.getText());
        messageField.setText("");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==messageField)
			Send();
		else if(e.getSource()==sendButton)
			Send();
		else if(e.getSource()==mntmExit){
			System.exit(0);
		}		
	}
	
	public void Connect(){
		try {
			sendButton.setEnabled(true);
			server();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private String getServerAddress() {
        return JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "TroubleSome", JOptionPane.QUESTION_MESSAGE);
	}
	
	private String getName() {
        return JOptionPane.showInputDialog(frame, "Enter a user name:", "Name Selector", JOptionPane.PLAIN_MESSAGE);
    }
	
	public void server() throws IOException{
		
		// Make connection and initialize streams
        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress, 9002);
        
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		
		// Process all messages from server, according to the protocol.
		while (true) {
			String line = in.readLine();
			if (line.startsWith("SUBMITNAME")) {
				String name = getName(); 
				out.println(name);
				frame.setTitle(name);
			} else if (line.startsWith("NAMEACCEPTED")) {
				messageField.setEditable(true);
				
				line = in.readLine();
				while(line!=null){
					textArea.append(line + "\n");
					line = in.readLine();
				}
			}if (!line.startsWith("NAMEACCEPTED") && !line.startsWith("SUBMITNAME")) {
				textArea.append(line + "\n");
			}
		}
	}
}