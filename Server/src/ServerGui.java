import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

import listener.ServerListener;
import network.Server;

/**
 * User interface for Sever
 * 
 * @author Arnon  Ruangthanawes arua663
 *
 */
public class ServerGui implements ServerListener {
	/**
	 * The main from of the GUI
	 */
	private JFrame frame;

	/**
	 * The text area displaying activity occurs in the server
	 */
	private JTextArea textArea;

	/**
	 * The port number field
	 */
	private JTextField portField;
	
	/**
	 * The directory path field
	 */
	private JTextField pathField;
	
	/**
	 * The browser button
	 */
	private JButton browseButton;
	
	/**
	 * The start server button;
	 */
	private JButton startButton;
	
	/**
	 * The stop server button;
	 */
	private JButton stopButton;
	
	/**
	 * The {@link Server}
	 */
	private Server server;

	/**
	 * Logger
	 */
	Logger logger = Logger.getLogger(ServerGui.class.getName());
	
	/**
	 * Constructor
	 */
	public ServerGui() {
		server = new Server();
		FileHandler fileLog;
		try {
			fileLog = new FileHandler("server.log", 90240, 10);
			logger.addHandler(fileLog);
			fileLog.setFormatter(new LogFormatter());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		ServerGui obj = new ServerGui();
		obj.server.addListener(obj);
		obj.init();
	}

	/**
	 * Create the GUI
	 */
	public void init() {
		/* Initialize the main JFrame */
		this.frame = new JFrame("Server");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
              server.stopServer();
    		  logger.info("The server is stopped.");
    		  logger.info("Server application is closed");
            }
        });
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setOpaque(true);
		
		
		/* Construct the input panel
		 * - Port number
		 * - Directory chooser */
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new FlowLayout());
		JLabel  portNumberLabel = new JLabel("Port number: ", JLabel.RIGHT);
		portField = new JTextField(Server.DEFAULT_PORT, 6);
		inputPanel.add(portNumberLabel);
		inputPanel.add(portField);
		panel.add(inputPanel);
		
		JLabel  pathLabel = new JLabel("Directory: ", JLabel.RIGHT);
		pathField = new JTextField("", 25);
		pathField.setEditable(false);
		pathField.setBackground(Color.white);
		pathField.addMouseListener(new BrowseFolderAction());
		
		inputPanel.add(pathLabel);
		inputPanel.add(pathField);
		browseButton = new JButton("Browse");
		browseButton.addActionListener(new BrowseFolderAction());
		inputPanel.add(browseButton);
		
		
		/* Construct the text area for displaying the activity at the server */
		textArea = new JTextArea(15, 50);
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setFont(Font.getFont(Font.SANS_SERIF));
		JScrollPane scroller = new JScrollPane(textArea);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scroller);
		
		/* Construct the Start and Stop server buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		startButton = new JButton("Start Server");
		startButton.addActionListener(new StartServerAction());
		stopButton = new JButton("Stop Server");
		stopButton.addActionListener(new StopServerAction());
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);
		panel.add(buttonPanel);
		frame.getContentPane().add(BorderLayout.CENTER, panel);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		frame.setResizable(false);
	}

	/**
	 * Action handler class for starting the server
	 * 
	 * @author Arnon  Ruangthanawes arua663
	 *
	 */
	public class StartServerAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					String message = "";
					
					/* Validate the port field must be entered */
					String portText = portField.getText();
					if (portText == null || portText.isEmpty()) {
						message = "Please enter port number";
						textArea.append(message+"\n");
						logger.log(Level.SEVERE, message) ;
						return;
					}
					
					/* Validate the port field must be 4 digits */
					if (portText.length() != 4) {
						message = "Please enter 4 digits of port number";
						textArea.append(message+"\n");
						logger.log(Level.SEVERE, message);
						return;
					}
					
					/* Validate the port field must be number */
					int portNumber = 0;
					try {
						portNumber = Integer.parseInt(portText);
					}
					catch (Exception ex) {
						message = "Please 4 digits of port number";
						textArea.append(message+"\n");
						logger.log(Level.SEVERE, message, ex);
						return;
					}
					
					/* Validate the path field must be entered */
					String pathFieldString = pathField.getText();
					if (pathFieldString == null || pathFieldString.isEmpty()) {
						message = "Please enter available file directory";
						textArea.append(message+"\n");
						logger.log(Level.SEVERE, message) ;
						return;
					}
					
					try {
						message = server.startServer(portNumber, pathFieldString);
						portField.setEnabled(false);
						pathField.setEnabled(false);
						browseButton.setEnabled(false);
						startButton.setEnabled(false);
						stopButton.setEnabled(true);
					} catch (IOException e1) {
						textArea.append(e1.getMessage());
						logger.log(Level.SEVERE, "Error while starting server", e1) ;
					}
					
					logger.info(message);
					textArea.append(message+"\n");
				}
			});
			
		}
	}
	
	/**
	 * Action handler class for stopping the server
	 * 
	 * @author Arnon  Ruangthanawes arua663
	 *
	 */
	public class StopServerAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					server.stopServer();
					portField.setEnabled(true);
					pathField.setEnabled(true);
					browseButton.setEnabled(true);
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
					String message = "The server is stopped.";
					logger.info(message);
					textArea.append(message+"\n");
				}
			});
		}
	}
	
	/**
	 * Action handler class for browser folder action
	 * 
	 * @author Arnon  Ruangthanawes arua663
	 */
	public class BrowseFolderAction extends MouseAdapter implements ActionListener {
		private void popupFileBrowser() {
			JFileChooser fileChooser = new JFileChooser();
	        // For Directory
	        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	        fileChooser.setAcceptAllFileFilterUsed(false);
	 
	        int rVal = fileChooser.showOpenDialog(null);
	        if (rVal == JFileChooser.APPROVE_OPTION) {
	        	pathField.setText(fileChooser.getSelectedFile().toString());
	        }
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			popupFileBrowser();
		}
		
        @Override
        public void mouseClicked(MouseEvent e){
        	popupFileBrowser();
        }
	}

	@Override
	public void requestFileList(final List<String> fileList) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		      String message = "Request List of files: "+fileList;
		      textArea.append(message+"\n");
		      logger.info(message);
		    }
		  });
	}

	@Override
	public void requestFile(final String filePath) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		      String message = "Request file: "+filePath;
		      textArea.append(message+"\n");
		      logger.info(message);
		    }
		  });		
	}
}
