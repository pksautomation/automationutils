package com.innovaccer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *@author pramod.singh
 */
public class TestDataReader
{

	String filename;

	private FileOutputStream fileOut = null;

	private FileInputStream fis = null;

	String path;
	private String sheetName;
	private Config testConfig;

	private ArrayList<List<String>> testData;

	public TestDataReader(Config testConfig)
	{
		this.testConfig = testConfig;
	}

	TestDataReader(Config testConfig, String sheetName)
	{
		String path = testConfig.getRunTimeProperty("TestDataSheet");
		readFile(testConfig, sheetName, path);
	}

	protected TestDataReader(Config testConfig, String sheetName, String path)
	{
		readFile(testConfig, sheetName, path);
	}

	private String convertHSSFCellToString(HSSFCell cell, FormulaEvaluator evaluator)
	{
		String value = null;
		try
		{
			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
			{
				value = Double.toString(cell.getNumericCellValue());
			}
			else
				if (cell.getCellType() == Cell.CELL_TYPE_STRING)
				{
					value = cell.getRichStringCellValue().toString();
				}
				else
					if (cell.getCellType() == Cell.CELL_TYPE_FORMULA)
					{
						HSSFDataFormatter formatter = new HSSFDataFormatter();
						value = formatter.formatCellValue(cell, evaluator); 
					}
					else
						if (cell.getCellType() == Cell.CELL_TYPE_ERROR)
						{
							value = "";
						}
						else
							if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN)
							{
								value = Boolean.toString(cell.getBooleanCellValue());
							}
							else
								if (cell.getCellType() == Cell.CELL_TYPE_BLANK)
								{
									value = "";
								}

		}
		catch (NullPointerException ex)
		{
			value = "";
		}
		return value;
	}
	
	/**
	 * Gets the cell value.
	 *
	 * @param cell the cell
	 * @return the cell value
	 */
	private String getCellValue( XSSFCell cell )
	{
		if (cell != null)
		{
			switch (cell.getCellTypeEnum())
			{
				case BLANK:
						return null;
				case BOOLEAN:
					return String.valueOf( cell.getBooleanCellValue() );
				case NUMERIC:
					return String.valueOf( ( int ) cell.getNumericCellValue() );
				case STRING:
					return cell.getRichStringCellValue().toString();
				default:
					return null;

			}
		}
		return null;
	}

	private String convertXSSFCellToString(XSSFCell cell)
	{
		String value = null;
		try
		{
			if (cell.getCellTypeEnum() == CellType.NUMERIC)
			{
				value = Double.toString(cell.getNumericCellValue());
			}
			else
				if (cell.getCellTypeEnum() == CellType.STRING)
				{
					value = cell.getRichStringCellValue().toString();
				}
				else
					if (cell.getCellTypeEnum() == CellType._NONE)
					{
						value = "";
					}
					else
						if (cell.getCellTypeEnum() == CellType.BOOLEAN)
						{
							value = Boolean.toString(cell.getBooleanCellValue());
						}
						else
							if (cell.getCellTypeEnum() == CellType.BLANK)
							{
								value = "";
							}
							else
								if (cell.getCellTypeEnum() == CellType.ERROR)
								{
									value = "";
								}

		}
		catch (NullPointerException ex)
		{
			value = "";
		}
		return value;
	}

	public String GetcolumnData(String column, String value, String path, int columnNum, Boolean newPennyFlow)
	{
		String data = "";
		column = column.trim();
		List<String> headerRow = testData.get(0);
		HSSFWorkbook workbook = null;
		HSSFSheet sheet = null;
		filename = path;

		try
		{
			fis = new FileInputStream(filename);
			workbook = new HSSFWorkbook(fis);
			sheet = workbook.getSheet(sheetName);
			/*
			 * while (workbook.) { XSSFRow row = (XSSFRow) rows.next(); List
			 * <String> data = new ArrayList<String>(); for(int
			 * z=0;z<row.getLastCellNum();z++) { String str
			 * =convertXSSFCellToString((XSSFCell)row.getCell(z));
			 * data.add(str); } testData.add(data); }
			 */

			if (headerRow.get(columnNum).equals(column))
			{

				int rowNum = 0;

				int totalRows = getRecordsNum();
				if (newPennyFlow)
				{
					totalRows = totalRows - 1;
				}
				System.out.println(totalRows);
				for (rowNum = totalRows; rowNum >= 1; --rowNum)
				{

					data = GetData(rowNum, column);
					if (data.equals(value))
					{
						data = GetData(rowNum, column);
						String row = String.valueOf(rowNum);
						testConfig.putRunTimeProperty("Row", row);
						testConfig.logPass(column + " contains the value " + value);
						break;
					}
				}
			}

		}
		catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return data;
	}

	/**
	 * Gets the data value in column=correspondingColumnToRead for the row where
	 * value=inputColumnValue and column=inputColumn
	 * 
	 * @param inputColumn
	 *            Column name corresponding to which value is to be read
	 * @param inputColumnValue
	 *            Column value for which value is to be read
	 * @param correspondingColumnToRead
	 *            Column name whose value is required
	 * @return corresponding column value
	 */
	public String GetCorrespondingColumnValue(String inputColumn, String inputColumnValue, String correspondingColumnToRead)
	{
		String correspondingColumnValue = "";

		List<String> headerRow = testData.get(0);
		for (int col = 0; col < headerRow.size(); col++)
		{
			if (headerRow.get(col).equalsIgnoreCase(inputColumn))
			{
				try
				{
					int row = 1;
					List<String> rowToRead = null;
					String inputval = null;
					while (true)
					{
						rowToRead = testData.get(row);
						inputval = rowToRead.get(col);
						if (inputval.equalsIgnoreCase(inputColumnValue))
						{
							correspondingColumnValue = GetData(row, correspondingColumnToRead);
							return correspondingColumnValue;
						}
						row++;
					}

				}
				catch (Exception e)
				{
					return "";
				}
			}
		}

		return correspondingColumnValue;
	}

	/**
	 * Returns the Excel sheet data value. It will get the current environment
	 * and read the value in column 'column-<environment>', if it blank then it
	 * will return value in 'column' (returns {skip} if the excel value is
	 * blank, which means no operation)
	 * 
	 * @param row
	 *            Excel Row number to read
	 * @param column
	 *            Excel column name to read
	 * @return The value read
	 */
	public String GetCurrentEnvironmentData(int row, String column)
	{
		String env = testConfig.getRunTimeProperty("Environment");
		String value = GetData(row, column + "-" + env);
		if (value.equalsIgnoreCase("{skip}"))
		{
			value = GetData(row, column);
		}
		return value;
	}

	/**
	 * Returns the Excel sheet data value (returns {skip} if the excel value is
	 * blank, which means no operation)
	 * 
	 * @param row
	 *            Excel Row number to read
	 * @param column
	 *            Excel column name to read
	 * @return The value read
	 */
	public String GetData(int row, String column){
		return GetData(row, column, true);
	}
	public String GetData(int row, String column, boolean isTrim)
	{
		String data = "";
		List<String> headerRow = testData.get(0);
		List<String> dataRow = testData.get(row);

		for (int i = 0; i < headerRow.size(); i++)
		{
			if (headerRow.get(i).equalsIgnoreCase(column))
			{
				try
				{
					data = dataRow.get(i);
				}
				catch (IndexOutOfBoundsException e)
				{
					data = "";
				}
				break;
			}
		}

		if(isTrim)
			data = data.trim();

		if (data.equals(""))
		{
			data = "{skip}";
			return data;
		}
		else
		{
			if (data.contains("{empty}"))
				data = data.replace("{empty}", "");
			if (data.contains("{space}"))
				data = data.replace("{space}", " ");

			while (data.contains("{random"))
			{
				int start = data.indexOf("Num:") + 4;
				int end = data.indexOf("}");
				int length = Integer.parseInt(data.substring(start, end));

				if (data.contains("{randomAlphaNum:" + length + "}"))
					data = data.replace("{randomAlphaNum:" + length + "}", Helper.generateRandomAlphaNumericString(length));
				if (data.contains("{randomAlphabetsNum:" + length + "}"))
					data = data.replace("{randomAlphabetsNum:" + length + "}", Helper.generateRandomAlphabetsString(length));
				if (data.contains("{randomNum:" + length + "}"))
					data = data.replace("{randomNum:" + length + "}", Long.toString(Helper.generateRandomNumber(length)));
			}
		}

		if (testConfig.debugMode)
			Log.Comment("Reading '" + sheetName + "' row-" + row + " column-" + column + " value:-'" + data + "'", testConfig);
		return data;
	}

	/**
	 * Returns the Excel header value
	 * 
	 * @return The Header value at index i
	 */
	public String GetHeaderData(int i)
	{
		String data = "";
		List<String> dataRow = testData.get(0);

		try
		{
			data = dataRow.get(i);
		}
		catch (IndexOutOfBoundsException e)
		{
			data = "";
		}

		data = data.trim();

		if (data.equals(""))
		{
			data = "{skip}";
			return data;
		}

		return data;
	}

	/**
	 * This method returns the number of records present in the datasheet
	 * @return number of records
	 */
	public int getRecordsNum()
	{
		return testData.size();
	}

	/**
	 * This method returns the number of columns of the datasheet
	 * (It counts the header and returns the number)
	 * @return number of columns
	 */
	public int getColumnNum(){
		List<String> headerRow = testData.get(0);
		return headerRow.size();
	}

	// Ignore number format exception in reading String/Float value from excel
	// if excel cell value is blank and format is not string
	public String ignoreNumberFormatException(String returnType, String value)
	{
		if (value.equalsIgnoreCase("{skip}"))
		{
			switch (returnType)
			{
			case "stringType":
				return "";

			case "floatType":
				return "0.0";

			default:
				return value;
			}
		}
		else
			return value;
	}

	private void readFile(Config testConfig, String sheetName, String path)
	{
		this.testConfig = testConfig;
		int index = path.lastIndexOf("//");
		if (index != -1)
			testConfig.logComment("Read:-'" + path.substring(path.lastIndexOf("//")) + "', Sheet:- '" + sheetName + "'");
		else
			testConfig.logComment("Read:-'" + path + "', Sheet:- '" + sheetName + "'");

		filename = path;
		testData = new ArrayList<List<String>>();

		try
		{
			if (filename.endsWith(".xlsx"))
			{
				XSSFWorkbook workbook = null;
				XSSFSheet sheet = null;

				fis = new FileInputStream(filename);

				workbook = new XSSFWorkbook(fis);
				sheet = workbook.getSheet(sheetName);
				Iterator<Row> rows = sheet.rowIterator();
				while (rows.hasNext())
				{
					XSSFRow row = (XSSFRow) rows.next();
					List<String> data = new ArrayList<String>();
					for (int z = 0; z < row.getLastCellNum(); z++)
					{
						String str = convertXSSFCellToString(row.getCell(z));
						data.add(str);
					}
					testData.add(data);
				}
				this.sheetName = sheetName;
			}
			else
				if (filename.endsWith(".csv"))
				{

					BufferedReader CSVFile = null;
					String dataRow = null;
					ArrayList<String> datatemp = new ArrayList<String>();
					try
					{
						CSVFile = new BufferedReader(new FileReader(path));
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}

					try
					{
						dataRow = CSVFile.readLine();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

					while (dataRow != null)
					{

						String[] dataArray = dataRow.split(",");

						List<String> data = new ArrayList<String>();
						for (int z = 0; z < dataArray.length; z++)
						{
							String str = dataArray[z];
							data.add(str);
						}
						testData.add(data);

						try
						{
							dataRow = CSVFile.readLine();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					try
					{
						CSVFile.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
		}
		catch (FileNotFoundException  e)
		{
			testConfig.logException(e);
		}catch (IOException e) {
			testConfig.logException(e);
		}
		catch (Exception e)
		{
			testConfig.logException(e);
		}
		finally
		{
			if (fis != null)
			{
				try
				{
					fis.close();
				}
				catch (IOException e)
				{
					testConfig.logException(e);
				}
			}
		}
	}

}