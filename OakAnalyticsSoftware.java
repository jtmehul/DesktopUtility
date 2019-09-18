package com.oakanalytics.pcsoftware;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.GradientPaintTransformer;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.awt.event.*;
import java.io.*;
import java.sql.Date;
import java.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.awt.event.InputMethodListener;
import java.util.List;

import javax.swing.border.LineBorder;



public class OakAnalyticsSoftware extends Constants{
	
	// GUI element declaration 
	public static JFrame frame;
	public static JTextField txtDeviceSerialNo,txtCommand,txtTestResult;
	public static JButton btnSendCommand;
	public static JLabel lblDeviceSerialNo,lblCommand,lblScanSpeed,lblLaserIntensity,lblScannFilePath;
	public static JLabel lblTestResult,lblSelectFileTo,lblTime,lblShowTime;
	public static JTextArea txtAreaCommand;
	public static TextArea textArea;
	public static JComboBox ddlScanSpeed,ddlLaserIntensity;
	public static JCheckBox chckbxCreateNewFile,chckbxAutoScan;
	public static JButton btnLoadFileToScan,btnScan,btnSaveGraphFile,btnSaveCommand,btnBrowseFile,btnShowGraph;
	public static JMenuBar menuBar;
	public static JMenu mnFile;
	public static JFileChooser fileChoose,chooseFileGraph,chooseFileToPlot, chooseLoadCommandFile;
	public static DateTimeFormatter dtFormat;
	public static File dirc,filecreate,fileSave,fileNew,fileList[];
	public static BufferedReader brr,buferReaderList;
	public static BufferedWriter bff;
	public static FileReader fileReaderList;
	public JList listFiles;
	public DefaultListModel defaultModelList;
	public JScrollPane scrollPaneListFiles;
	public JScrollPane scrollPane;
	public XYSeries series;
	public JFreeChart chart;
	public XYDataItem dataItem;
    public SerialPort [] portNames;
    //public  OutputStream outPutStream;
    //public InputStream inPutStream;
    public JPanel panelGraph;
    public BufferedWriter writeToFile;
    public FileWriter writerFile;
    public BufferedReader bffReader;
    public File filepath;
    public LocalDateTime nowLocal;
    public JComboBox portList;
    boolean fileDirectory;
    public List<String> selectedListValue;
    public JLabel lblScanStatus;
    
   

	// Main method to invoke
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OakAnalyticsSoftware oakAnalytics = new OakAnalyticsSoftware();
					oakAnalytics.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	// Constructor 
	public OakAnalyticsSoftware() throws IOException {
		initialize();
		createLocalDirectory();
	    portListCall();

	}
	public void createLocalDirectory() throws IOException
	{
		// Creating file directory in root folder of JAR Created to store scan data
		fileDirectory = new File(strFileLocation).mkdir();
	    strAbsPath = new File("//").getAbsolutePath();
	    
		//String uuid = UUID.randomUUID().toString();
		//String serialNumberDevice = "ea9d5e37-dd5c-41d7-915c-624ec0151513";
	   /* int serialNumberDevice= mInput.read("sn".getBytes());
	    String str = String.valueOf(serialNumberDevice);
		txtDeviceSerialNo.setText(str);*/
		DateShowDDMMYYYY();
	}
	public void portListCall()
	{
		// Adding all port names to combo box
		portNames  = SerialPort.getCommPorts();
		 for(int i = 0; i<portNames.length; i++){
	        	portList.addItem(portNames[i].getSystemPortName());
	       }
		 // TO diable scan, send and save button if port is not open or blank
		 if(portList.getSelectedItem()==null)
		 {
			 btnSendCommand.setEnabled(false);
			 btnSaveCommand.setEnabled(false);
			 btnScan.setEnabled(false);
		 }else{  //Opening port to scan data
			 	chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
		 		chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		 }
	}
	// Function for Date time format in DD MM YYYYY
	public void DateShowDDMMYYYY(){
		dtFormat = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
	    LocalDateTime now = LocalDateTime.now();
	    dateToLbl = dtFormat.format(now);	
	    lblShowTime.setText(dateToLbl);
	}
	public void dateFileFormat(){
		filepath=  new File(strAbsPath);
		dtFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
		nowLocal = LocalDateTime.now();
		dateToLbl = dtFormat.format(nowLocal);
	}
	//Function to start scan and perform scanning
	public synchronized void callScanner(){
		//if(btnScan.getText().equals("Scan")){
            // Start new thread
			new Thread(){
				public void run()
				{ 
					lblScanStatus.setText("");
			    	if(chosenPort.openPort()){
			    	   mOutPut = chosenPort.getOutputStream();
			    	   try{		
			    		  // Create new file with local time stamp to store scanned data
				    	    writerFile = new FileWriter(strFileLocation+"\\"+dateToLbl + ".txt");
				    	   // Send command to scanner to start scan
				    	    mOutPut.write("va1".getBytes());
				    	    mOutPut.write("pb".getBytes()); 
				    	    mOutPut.write("sn".getBytes());

				    	    //Input stream to read scanned data
				    	    mInput = chosenPort.getInputStream();
				    	    //System.out.println(mInput.read(("sn".getBytes()));
				    	    input = new Scanner(mInput);	    	   
				    	    while(input.hasNext())
		    	   			{ 
		    	   				dataToShowOnScreen = input.nextLine();
		    	   		   System.out.println(dataToShowOnScreen);
		    	   				writerFile.write(dataToShowOnScreen+"\n");
		    	   			 if(input.hasNext("sc"))
					    	    {
					    	    	chosenPort.closePort();
					    	    	lblScanStatus.setText("Scan Complete");
		    	   					input.close();
		    	   					writerFile.close();
				    	   			System.out.println("Scanner Closed");
					    	    }
		    	   			}
			    	   }catch(Exception error){
			    		   //System.out.println(error);
			    		   }
			    	}
			    	else
			    	{
			    		System.out.println("Error");
			    	}
				}	
			}.start();
				//}	
	}
	// Function to initialize frame and its components
	private void initialize() {
		
		frame = new JFrame();
		frame.setForeground(Color.LIGHT_GRAY);
		frame.setFont(new Font("Arial", Font.BOLD, 16));
		frame.setTitle(Constants.titleOak);
		frame.setBounds(100, 100, 1200, 620);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);	
				
		lblDeviceSerialNo = new JLabel("Device Serial No:");
		lblDeviceSerialNo.setBounds(23, 35, 103, 14);
		frame.getContentPane().add(lblDeviceSerialNo);
		
		portList = new JComboBox();
	    portList.setBounds(23, 266, 103, 20);
		portList.setVisible(true);
		frame.getContentPane().add(portList);
		 
		panelGraph = new JPanel();
		panelGraph.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelGraph.setBackground(Color.WHITE);
		panelGraph.setBounds(605, 60, 558, 407);
		panelGraph.setLayout(new java.awt.BorderLayout());
		frame.getContentPane().add(panelGraph);
		 
		txtDeviceSerialNo = new JTextField();
		txtDeviceSerialNo.setBackground(Color.WHITE);
		txtDeviceSerialNo.setEditable(false);
		txtDeviceSerialNo.setBounds(129, 33, 236, 17);
		txtDeviceSerialNo.setColumns(10);
		frame.getContentPane().add(txtDeviceSerialNo);
		
		lblCommand = new JLabel("Command:");
		lblCommand.setBounds(23, 125, 66, 14);
		frame.getContentPane().add(lblCommand);
		
		txtCommand = new JTextField();
		txtCommand.setBounds(91, 122, 103, 19);
		frame.getContentPane().add(txtCommand);
		txtCommand.setColumns(10);
		
		txtAreaCommand = new JTextArea();
		txtAreaCommand.setEditable(false);
		txtAreaCommand.setBounds(23, 60, 341, 54);
		frame.getContentPane().add(txtAreaCommand);
		
		btnSendCommand = new JButton("Send");
		btnSendCommand.setBounds(204, 121, 79, 23);
	    frame.getContentPane().add(btnSendCommand);
		
		lblScanSpeed = new JLabel("Scan Speed");
		lblScanSpeed.setBounds(23, 163, 66, 14);
		frame.getContentPane().add(lblScanSpeed);
		
		ddlScanSpeed = new JComboBox();
		ddlScanSpeed.setModel(new DefaultComboBoxModel(new String[] {"10", "15", "20", "25", "30"}));
		ddlScanSpeed.setBounds(101, 160, 55, 20);
		frame.getContentPane().add(ddlScanSpeed);
		
		lblLaserIntensity = new JLabel("Laser Intensity");
		lblLaserIntensity.setBounds(188, 166, 95, 14);
		frame.getContentPane().add(lblLaserIntensity);
		
		ddlLaserIntensity = new JComboBox();
		ddlLaserIntensity.setModel(new DefaultComboBoxModel(new String[] {"10", "20", "30", "40", "50", "60"}));
		ddlLaserIntensity.setBounds(293, 163, 55, 20);
		frame.getContentPane().add(ddlLaserIntensity);
		
		btnLoadFileToScan = new JButton("Load Command File");
		btnLoadFileToScan.setBounds(23, 200, 125, 23);
		frame.getContentPane().add(btnLoadFileToScan);
		
		lblScannFilePath = new JLabel("File Path: ");
		lblScannFilePath.setBounds(158, 204, 207, 14);
		frame.getContentPane().add(lblScannFilePath);
		
		btnScan = new JButton("Scan");
		btnScan.setBounds(156, 265, 97, 23);
		frame.getContentPane().add(btnScan);
		
		chckbxCreateNewFile = new JCheckBox("Create New File");
		chckbxCreateNewFile.setBounds(23, 230, 113, 23);
		frame.getContentPane().add(chckbxCreateNewFile);
		
		chckbxAutoScan = new JCheckBox("Auto Scan");
		chckbxAutoScan.setBounds(158, 230, 95, 23);
		frame.getContentPane().add(chckbxAutoScan);
		
		btnSaveGraphFile = new JButton("Save Graph File");
		btnSaveGraphFile.setBounds(605, 478, 127, 23);
		btnSaveGraphFile.setVisible(false);
		frame.getContentPane().add(btnSaveGraphFile);
		
		lblTestResult = new JLabel("Test Result:");
		lblTestResult.setBounds(977, 482, 67, 14);
		frame.getContentPane().add(lblTestResult);
		
		txtTestResult = new JTextField();
		txtTestResult.setBounds(1054, 480, 92, 20);
		txtTestResult.setEnabled(false);
		frame.getContentPane().add(txtTestResult);
		txtTestResult.setColumns(10);
		
		lblSelectFileTo = new JLabel("Select File");
		lblSelectFileTo.setBounds(385, 35, 60, 14);
		frame.getContentPane().add(lblSelectFileTo);
		
		lblTime = new JLabel("Date and Time:");
		lblTime.setBounds(916, 35, 92, 14);
		frame.getContentPane().add(lblTime);
		
		lblShowTime = new JLabel("");
		lblShowTime.setBounds(1024, 35, 127, 14);
		frame.getContentPane().add(lblShowTime);
		
		btnSaveCommand = new JButton("Save");
		btnSaveCommand.setBounds(293, 121, 72, 23);
		frame.getContentPane().add(btnSaveCommand);
		
		btnBrowseFile = new JButton("Browse File");
		btnBrowseFile.setBounds(474, 31, 89, 23);
		frame.getContentPane().add(btnBrowseFile);
		
		// Menu bar to show different menu
		menuBar = new JMenuBar();
		menuBar.setToolTipText("Select Option from Menu");
		frame.setJMenuBar(menuBar);
		
		mnFile = new JMenu("Options");
		menuBar.add(mnFile);
		
		JMenuItem mntmReport = new JMenuItem("Report");
		mnFile.add(mntmReport);
		
		JMenuItem mntmGoldenSignature = new JMenuItem("Golden Signature");
		mnFile.add(mntmGoldenSignature);
		
		JMenuItem mntmUserSetTolerance = new JMenuItem("User Tolerance");
		mnFile.add(mntmUserSetTolerance);
		
		JMenuItem mntmNumberOfOutliers = new JMenuItem("Number of Outliers");
		mnFile.add(mntmNumberOfOutliers);
		fileChoose = new JFileChooser();
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(385, 60, 210, 407);
		frame.getContentPane().add(scrollPane);
		
		JList listFiles = new JList();
		scrollPane.setViewportView(listFiles);
		
		btnShowGraph = new JButton("Show Graph");
		btnShowGraph.setVisible(false);
		btnShowGraph.setBounds(605, 31, 113, 23);
		frame.getContentPane().add(btnShowGraph);
		
		lblScanStatus = new JLabel("");
		lblScanStatus.setForeground(Color.RED);
		lblScanStatus.setBounds(263, 269, 112, 14);
		frame.getContentPane().add(lblScanStatus);
		
		// Listner method for list click element and show it in textarea
		listFiles.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()){
					selectedListValue = listFiles.getSelectedValuesList();
					strListAbsPath =  new File("OakSoftware\\").getAbsolutePath();
					for(String selectedValueFromScanFile:selectedListValue)
					{
						try{
							buferReaderList = new BufferedReader(new FileReader(strListAbsPath+"\\"+selectedValueFromScanFile));  
							series = new XYSeries("Graph Chart");
							dataItem = new XYDataItem(10, 8);
							   try {
								   while((stringIwant = buferReaderList.readLine())!= null){
									   if(!(stringIwant.startsWith("#") || stringIwant.startsWith("Angle")||stringIwant.startsWith("ng"))){
										   System.out.println(stringIwant);
										    newArray = stringIwant.split("\n");
											for(int i=0;i<newArray.length;i++)
											{
											   rowArray =newArray[i].split(" ");	
							                   rowX = Double.parseDouble(rowArray[0]);
											   rowY = Double.parseDouble(rowArray[1]);
											   series.add(rowX,rowY);
											}
									   }
								   }
								    XYSeriesCollection collectionDataSet = new XYSeriesCollection();
									collectionDataSet.addSeries(series);
									chart = ChartFactory.createXYLineChart("OAK PC Software Graph", "Wave-Number","Intensity", 
											                                collectionDataSet, PlotOrientation.VERTICAL, 
											                                true, true, false);
								    ChartPanel chartPanel = new ChartPanel(chart);
								    panelGraph.add(chartPanel, BorderLayout.CENTER);
								    panelGraph.setVisible(true);
								    panelGraph.validate();
									buferReaderList.close();
						        	} 
							 catch (IOException e2)
							        {  e2.printStackTrace(); }
						}
						catch(FileNotFoundException fileE)
						{ fileE.printStackTrace(); }
						//strShowFileData = strShowFileName.substring(1, strShowFileName.length()-1);
					}
				}			
			}
		});
		// Button to save commands sent to the scanner 
		btnSaveCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 DateShowYYYYMMDD();
		         try {
		        	 if(!txtAreaCommand.getText().toString().trim().equals(""))
		        	 {
		        		 BufferedWriter bff = new BufferedWriter(new FileWriter(strFileLocation+"\\Cmd"+".txt"));
							strSaveCommand = txtAreaCommand.getText();
							System.out.println(strSaveCommand);
							bff.write(strSaveCommand);	
							bff.newLine();
							bff.flush();
							bff.close();
		        	 }else{ JOptionPane.showMessageDialog(null, "No command to save"); }
				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}   	
			}
		});
		// Function to load files to the list from pre-defined driver 
		// This is the file location to browse
		btnBrowseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseFileGraph = new JFileChooser("OakSoftware\\");
				chooseFileGraph.setAcceptAllFileFilterUsed(true);
				chooseFileGraph.setMultiSelectionEnabled(true);
				//chooseFileGraph.addChoosableFileFilter(new FileNameExtensionFilter("Text", ".txt"));	
				int returnVal = chooseFileGraph.showOpenDialog(null);
				if(returnVal == JFileChooser.CANCEL_OPTION)  {
		             JOptionPane.showMessageDialog(null, "No file(s) were selected");
				}
				else
				{
					fileNew = chooseFileGraph.getSelectedFile(); 
					defaultModelList = new DefaultListModel();
					defaultModelList.clear();
					fileList = chooseFileGraph.getSelectedFiles();
						for (File fileSelected: fileList){
								strfileNameSelectedForList = fileSelected.getName();// getAbsolutePath()
								System.out.println(strfileNameSelectedForList);
								defaultModelList.addElement(strfileNameSelectedForList);
								listFiles.setModel(defaultModelList);
						   }
				}	
			}
		});
		// Button to Save Graph from Graph window
		btnSaveGraphFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		// Along with it once start command is sent then click on send button will start scanner
		btnSendCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			DateShowYYYYMMDD();	
			getTextofCommand = txtCommand.getText();			
			if(txtCommand.getText().equals("pb")|| txtCommand.getText().equals("va1"))
			{   txtAreaCommand.append(getTextofCommand + " -> " +dateToLbl+" "+"\n");
			    txtCommand.setText("");
			    dateFileFormat();
				callScanner(); 
				}
			else{   JOptionPane.showMessageDialog(null, "Please enter 'pb' command to start scan");
				    txtCommand.setText(null); }
			}
		});
		// Perform action  to start scan
		btnScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			dateFileFormat();
			callScanner();
			 laserIntensity	=ddlLaserIntensity.getSelectedItem().toString();
			 scanSpeed =ddlScanSpeed.getSelectedItem().toString();
			 if(chckbxCreateNewFile.isSelected()){
				 checkBoxCreateNewFile = chckbxCreateNewFile.getText(); }
			 if(chckbxAutoScan.isSelected()) {
				 checkBoxAutoScan = chckbxAutoScan.getText(); }
			}
		});
	// Button to load file to scan
		btnLoadFileToScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseLoadCommandFile = new JFileChooser("OakSoftware\\");
				int returnValue = chooseLoadCommandFile.showOpenDialog(null);
				if (returnValue == chooseLoadCommandFile.CANCEL_OPTION){
					System.out.println("You have clicked on cancel button");
				} else{ 
					File file = chooseLoadCommandFile.getSelectedFile();
					lblScannFilePath.setText("File Path : "+file.getName());
				}
			}
		});
	}
}
