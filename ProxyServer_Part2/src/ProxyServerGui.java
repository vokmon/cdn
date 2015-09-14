import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import listener.ProxyServerListener;
import network.ProxyServer;

/**
 * User interface for Proxy Sever
 * 
 * @author Arnon  Ruangthanawes arua663
 *
 */
public class ProxyServerGui implements ProxyServerListener {

	/**
	 * The text area displaying activity occurs in the server
	 */
	private JTextArea textArea;

	/**
	 * The port number field
	 */
	private JTextField portField;
	
	/**
	 * The table displaying list of cache file
	 */
	private JTable table;
	
	/**
	 * The editor pane
	 */
	private JEditorPane editorPane;
	
	/**
	 * The start server button;
	 */
	private JButton startButton;
	
	/**
	 * The stop server button;
	 */
	private JButton stopButton;
	
	/**
	 * The clear cache button
	 */
	private JButton clearCacheButton;
	
	/**
	 * The {@link ProxyServer}
	 */
	private ProxyServer proxyServer;
	
	/**
	 * Cache file list
	 */
	private List<String> currentCacheList;
	
	/**
	 * Logger
	 */
	Logger logger = Logger.getLogger(ProxyServerGui.class.getName());
	
	/**
	 * Constructor
	 */
	public ProxyServerGui() {
		proxyServer = new ProxyServer();
		FileHandler fileLog;
		try {
			fileLog = new FileHandler("proxyserver.log", 90240, 10);
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
		ProxyServerGui obj = new ProxyServerGui();
		obj.init();
		obj.proxyServer.addListener(obj);
	}
	
	/**
	 * Create the GUI
	 */
	public void init() {
		/* Initialize the main JFrame */
		JFrame frame = new JFrame("Proxy Server");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
              proxyServer.stopServer();
              proxyServer.clearCache();
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
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setOpaque(true);
		
		JPanel inputProxyPanel = new JPanel();
		inputProxyPanel.setLayout(new FlowLayout());
		JLabel  portNumberLabel = new JLabel("Port number: ", JLabel.RIGHT);
		portField = new JTextField(ProxyServer.DEFAULT_PORT, 6);
		inputProxyPanel.add(portNumberLabel);
		inputProxyPanel.add(portField);
		
		inputPanel.add(inputProxyPanel);
		panel.add(inputPanel);
		
		/* Construct the text area for displaying the activity at the server */
		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS));
		
		JPanel cacheFilePanel = new JPanel();
		cacheFilePanel.setLayout(new BoxLayout(cacheFilePanel, BoxLayout.X_AXIS));
		cacheFilePanel.setBorder(BorderFactory.createTitledBorder("Cache"));
		
		String columnNames[] = { "Cache File" };
		Object fileList[][] = {};
		@SuppressWarnings("serial")
		DefaultTableModel model = new DefaultTableModel(fileList, columnNames) {
			public boolean isCellEditable(int row, int column) {
			       return false;
			    }
		};
		table = new JTable(model);
		JScrollPane talbeScrollPane = new JScrollPane(table);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel selectionModel = table.getSelectionModel();
		selectionModel.addListSelectionListener(new SelectCacheFileAction());
		
		table.setFillsViewportHeight(true);
		talbeScrollPane.setPreferredSize(new Dimension(150, 400));
		cacheFilePanel.add(talbeScrollPane);
		
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setSize(500, 400);
		DefaultCaret edittorPaneCaret = (DefaultCaret) editorPane.getCaret();
		edittorPaneCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		
		cacheFilePanel.add(editorScrollPane);
		middlePanel.add(cacheFilePanel);
		
		textArea = new JTextArea(15, 40);
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setFont(Font.getFont(Font.SANS_SERIF));
		JScrollPane scroller = new JScrollPane(textArea);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(BorderFactory.createTitledBorder("Log"));
		middlePanel.add(scroller);
		panel.add(middlePanel);
		
		/* Construct the Start and Stop server buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		startButton = new JButton("Start Server");
		startButton.addActionListener(new StartServerAction());
		stopButton = new JButton("Stop Server");
		stopButton.addActionListener(new StopServerAction());
		clearCacheButton = new JButton("Clear cache");
		clearCacheButton.addActionListener(new ClearCacheAction());
		
		stopButton.setEnabled(false);
		clearCacheButton.setEnabled(false);
		
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(clearCacheButton);
		
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
					
					
					try {
						message = proxyServer.startServer(portNumber);
						portField.setEnabled(false);
						startButton.setEnabled(false);
						stopButton.setEnabled(true);
						clearCacheButton.setEnabled(true);
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
					proxyServer.stopServer();
					portField.setEnabled(true);
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
	 * Action handler when the cache is cleared.
	 * 
	 * @author Arnon  Ruangthanawes arua663
	 *
	 */
	public class ClearCacheAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					proxyServer.clearCache();
			}
		});
		}
	}
	
	/**
	 * Action handler when a file is selected
	 * 
	 * @author Arnon  Ruangthanawes arua663
	 */
	public class SelectCacheFileAction implements ListSelectionListener {
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
	    	if (! e.getValueIsAdjusting()){
	    		SwingUtilities.invokeLater(new Runnable() {
				    public void run() {
					    int row = table.getSelectedRow();
					    if (row >= 0) {
					    	String path = currentCacheList.get(row);
						    String content = proxyServer.getContent(path);
						    editorPane.setText(content);
					    }
					    
				    }
		    	});
	    	}
	    }
	}
	
	@Override
	public void requestFile(final String filePath, final Date date) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		      String message = String.format("User request: file %s at %s", filePath, date.toString());
		      textArea.append(message+"\n");
		      logger.info(message);
		    }
		  });	
	}

	@Override
	public void clearCache() {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	String message = "The proxy cache is clear";
		    	textArea.append(message+"\n");
		    	editorPane.setText("");
		    	DefaultTableModel model = (DefaultTableModel) table.getModel();
				for (int i = table.getRowCount() -1; i >=0; i--) {
					model.removeRow(i);
				}
		    	logger.info(message);
		    }
		});
	}

	@Override
	public void cacheUpdate(final List<String> listOfCacheFiles) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	int row = table.getSelectedRow();
			    String path = null;
			    if (currentCacheList != null && row >= 0) {
			    	path = currentCacheList.get(row);
			    }
			    
		    	DefaultTableModel model = (DefaultTableModel) table.getModel();
				for (int i = table.getRowCount() -1; i >=0; i--) {
					model.removeRow(i);
				}
			
				currentCacheList = listOfCacheFiles;
				Collections.sort(currentCacheList);
				for(String key : currentCacheList) {
					model.addRow(new String[] {key.replace("%20", " ")});
				}
				
				if (path != null) {
					int newRow = currentCacheList.indexOf(path);
					table.setRowSelectionInterval(newRow, newRow);
				}
		    }
		});
	}

	@Override
	public void percentageFromCache(final double percentageFromCache, final String fileName) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		      String message = String.format("response: %.2f%s of file %s was contructed with the cached data", 
		    		  percentageFromCache,
		    		  "%",
		    		  fileName);
		      textArea.append(message+"\n");
		      logger.info(message);
		    }
		  });
		
	}
}
