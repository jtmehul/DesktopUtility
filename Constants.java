package com.oakanalytics.pcsoftware;

import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.swing.*;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import com.fazecast.jSerialComm.SerialPort;

public class Constants {
	
	// Strings declaration to use
	public static String titleOak = "Oak Analytics";
	public static String getTextofCommand;
	//public static String fileSavePath = "C://OakPCSoftware//Oak";
	//public static String graphFileSave = "C://OakPCSoftware//Oak";
	public static String fileName;
	public static String dateToLbl;
	public static String line;
	public static String newline;
	public static String strSaveFileGraph;
	public static String strSaveCommand;
	public static String laserIntensity;
	public static String scanSpeed;
	public static String checkBoxCreateNewFile;
	public static String checkBoxAutoScan;
	public static String strfileNameSelectedForList;
	public static String fileNameSelected;
	public static String strShowFileName;
	public static String strShowFileData;
	public static String strNewLineList;
	public static String strstringIwant;
	public static String stringIwant;
	public static String strStartCommand, strStopCommand, dataToShowOnScreen, strSelectedPortToListen;	
	public static String strFileLocation = "OakSoftware";
	public static String [] newArray;
	public static String [] rowArray;
	public static double rowX,rowY;
	public static SerialPort chosenPort;
	public static OutputStream mOutPut;
	public static OutputStream newOutPut;
	public static InputStream mInput;
    public static String data;
    public static BufferedWriter writeToFile;
    public static File filepath;
    public static LocalDateTime now = LocalDateTime.now();
    public static Scanner input;
    public static String strPushButtonCommand;
    public static InputStreamReader inputR;
    public static BufferedReader brrReaderForScanner;
    public static DateTimeFormatter dtFormat;
    public LocalDateTime nowLocal;
    public String strPath = "C://OakPCSoftware//";
    public String strAbsPath;
    public String strListAbsPath;
    

	// Function for Date time format YYYY MM DD
	public void DateShowYYYYMMDD(){
		dtFormat = DateTimeFormatter.ofPattern("HH-mm-ss yyyy-MM-dd");
	    LocalDateTime now = LocalDateTime.now();
	    dateToLbl = dtFormat.format(now);
	}
	


}
