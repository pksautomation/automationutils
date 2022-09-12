package com.innovaccer.utils.fileutils;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.v2.LoggerUtils;
import org.apache.xml.utils.XMLChar;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XMLUtils {

    private Config config;
    private LoggerUtils loggerHelper;

    public XMLUtils(Config testConfig) {
        config = testConfig;
        loggerHelper = new LoggerUtils(config);
    }

    /**
     * *
     *
     * @param xmlString -> Input XML String to be written in the file
     * @param fileName  -> File Name to be given to the file
     * @return file path of the newly created XML file
     */
    public String convertStringToXmlFile(String xmlString, String fileName) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        String path = null;
        loggerHelper.logComment("Converting String to XML File");
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            path = config.downloadPath + fileName;
            StreamResult result = new StreamResult(new File(path));
            transformer.transform(source, result);
        } catch (Exception e) {
            loggerHelper.logException(e);
        }
        loggerHelper.logComment("String converted to XML file");
        return path;
    }

    /**
     * *
     *
     * @param path     -> XML Path to be read
     * @param fileName -> File Name of the XML File
     * @return file path of the newly created XML file
     */
    public String trimXmlFile(String path, String fileName) {
        String newPath = null;
        try {
            String xmlString = convertXmlStringIntoString(path);
            loggerHelper.logComment("trimming leading and trailing spaces in string");
            String trimmedXMLString = xmlString.trim();
            newPath = convertStringToXmlFile(trimmedXMLString, fileName);
        } catch (Exception e) {
            loggerHelper.logException(e);
        }
        return newPath;
    }

    /**
     * *
     *
     * @param path -> File Path of the XML File
     * @return XML String from the XML File
     */
    private String convertXmlStringIntoString(String path) {
        StringBuilder sb = new StringBuilder();
        loggerHelper.logComment("Converting XML File to String");
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                sb.append(currentLine);
            }
            bufferedReader.close();
        } catch (IOException e) {
            loggerHelper.logException(e);
        }
        loggerHelper.logComment("XML file converted to String");
        return sb.toString();
    }

    /**
     * *
     *
     * @param file -> File of the XML File
     * @return file path of the newly created XML file
     */
    public String stripInvalidXmlCharacters(File file) {
        StringBuilder fileData = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file.getPath()));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            reader.close();
            String input = fileData.toString();
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (XMLChar.isValid(c)) {
                    stringBuilder.append(c);
                }
            }
        } catch (IOException e) {
            loggerHelper.logException(e);
        }
        String xmlStr = stringBuilder.toString();
        return convertStringToXmlFile(xmlStr, file.getName());
    }

}