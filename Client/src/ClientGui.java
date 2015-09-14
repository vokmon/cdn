import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultCaret;

import network.Client;
import exception.ServerConnectionException;

/**
 * The graphic user interface for client
 * 
 * @author Arnon Ruangthanawes arua663
 */
public class ClientGui {
	/**
	 * The input field for proxy url
	 */
	private JTextField proxyField;

	/**
	 * The input filed for server url
	 */
	private JTextField serverField;

	/**
	 * The table for displaying the list of available files
	 */
	private JTable table;

	/**
	 * The area to display the content of the file
	 */
	private JEditorPane editorPane;

	/**
	 * The scroll pane of the {@link #editorPane}
	 */
	private JScrollPane editorScrollPane;

	/**
	 * The message area for displaying error in the client
	 */
	private JTextField messageLabel;

	/**
	 * The {@link Client} making connection to the server
	 */
	private Client client = new Client();

	/**
	 * The list of current available files
	 */
	private List<String> currentFileList;

	/**
	 * The main method of the class, and program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ClientGui obj = new ClientGui();
		obj.init();
	}

	/**
	 * Initialize the components
	 */
	public void init() {
		/* Main frame */
		JFrame frame = new JFrame("Client");
		frame.setDefaultCloseOperation(2);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, 1));
		panel.setOpaque(true);

		/* Create the input section */
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new FlowLayout());
		inputPanel.setOpaque(true);
		JLabel proxyLabel = new JLabel("Proxy URL: ", 4);
		this.proxyField = new JTextField("http://localhost:8001", 35);
		inputPanel.add(proxyLabel);
		inputPanel.add(this.proxyField);
		JLabel serverLabel = new JLabel("Server URL: ", 4);
		this.serverField = new JTextField("http://localhost:8000", 35);
		inputPanel.add(serverLabel);
		inputPanel.add(this.serverField);
		JButton goButton = new JButton("Go");
		goButton.addActionListener(new GoAction());
		inputPanel.add(goButton);
		panel.add(inputPanel);

		/* Create the message area to display error */
		this.messageLabel = new JTextField(30);
		this.messageLabel.setEditable(false);
		EmptyBorder empty = new EmptyBorder(0, 10, 0, 10);
		this.messageLabel.setBorder(empty);
		this.messageLabel.setForeground(Color.red);
		this.messageLabel.setMargin(new Insets(100, 100, 100, 100));
		panel.add(this.messageLabel);

		/*
		 * Create the table for listing available files, and displaying the
		 * content of the file
		 */
		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new BoxLayout(middlePanel, 0));
		Object[] columnNames = new String[] { "Files" };
		Object fileList[][] = {};
		@SuppressWarnings("serial")
		DefaultTableModel model = new DefaultTableModel(fileList, columnNames) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		this.table = new JTable((TableModel) model);
		JScrollPane talbeScrollPane = new JScrollPane(this.table);
		talbeScrollPane.setBorder(BorderFactory.createTitledBorder("Avaialble Files"));
		this.table.setSelectionMode(0);
		ListSelectionModel selectionModel = this.table.getSelectionModel();
		selectionModel.addListSelectionListener(new SelectFileAction());
		this.table.setFillsViewportHeight(true);
		talbeScrollPane.setPreferredSize(new Dimension(250, 400));
		middlePanel.add(talbeScrollPane);
		this.editorPane = new JEditorPane();
		this.editorPane.setEditable(false);
		this.editorPane.setSize(600, 400);
		DefaultCaret edittorPaneCaret = (DefaultCaret) this.editorPane.getCaret();
		edittorPaneCaret.setUpdatePolicy(1);
		this.editorScrollPane = new JScrollPane(this.editorPane);
		this.editorScrollPane.setBorder(BorderFactory.createTitledBorder("Content"));
		middlePanel.add(this.editorScrollPane);
		panel.add(middlePanel);

		/* Put all components to the JFrame */
		frame.getContentPane().add("Center", panel);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		frame.setResizable(false);
	}

	/**
	 * Action handler when the Go button is clicked
	 * 
	 * @author Arnon Ruangthanawes arua663
	 */
	public class GoAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					/* The proxy, and server url cannot be null or empty */
					String message = "";
					if (proxyField.getText() == null
							|| proxyField.getText().isEmpty()) {
						message = "Please enter proxy url.";
					} else if (serverField.getText() == null
							|| serverField.getText().isEmpty()) {
						message = "Please enter server url.";
					}

					try {
						client.setProxyUrl(proxyField.getText());
						client.setServerUrl(serverField.getText());

						/* Update the list of available table */
						int row = table.getSelectedRow();
						String path = null;
						if (currentFileList != null && row >= 0) {
							path = currentFileList.get(row);
						}

						DefaultTableModel model = (DefaultTableModel) table
								.getModel();
						for (int i = table.getRowCount() - 1; i >= 0; i--) {
							model.removeRow(i);
						}

						currentFileList = client.getAvailableFiles();
						Collections.sort(currentFileList);
						for (String key : currentFileList) {
							model.addRow(new String[] { key.replace("%20", " ") });
						}

						if (path != null) {
							int newRow = currentFileList.indexOf(path);
							table.setRowSelectionInterval(newRow, newRow);
						}

					} catch (IOException e) {
						message = "Error while connecting to the server : ";
						e.printStackTrace();
					} catch (ServerConnectionException e) {
						message = "Error while connecting to the server : "
								+ e.getMessage();
						e.printStackTrace();
					}

					messageLabel.setText(message);
				}
			});
		}
	}

	/**
	 * Action handler when a file is selected
	 * 
	 * @author Arnon Ruangthanawes arua663
	 */
	public class SelectFileAction implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						int row = table.getSelectedRow();
						if (row >= 0) {
							String path = currentFileList.get(row);
							String content;
							try {
								content = client.getFile(path);
								editorPane.setText(content);
								editorScrollPane.setBorder(BorderFactory.createTitledBorder("Content for "+path));
								messageLabel.setText("");
							} catch (IOException e) {
								messageLabel
										.setText("Error while connecting to the server : "
												+ e.getMessage());
								e.printStackTrace();
							} catch (ServerConnectionException e) {
								messageLabel
										.setText("Error while connecting to the server : "
												+ e.getMessage());
								e.printStackTrace();
							}
						}
					}
				});
			}
		}
	}
}