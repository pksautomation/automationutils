package com.innovaccer.utils.filehandlers;

import com.innovaccer.utils.Config;
import com.innovaccer.utils.Log;
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

public class XmlFileHandler {

    public static Config config;

    public XmlFileHandler(Config testConfig) {
        config = testConfig;
    }

    public static String convertStringToXmlFile(String xmlString, String fileName) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        String path = null;
        Log.Comment("Converting String to XML File", config);
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
            config.logException(e);
        }
        Log.Comment("String converted to XML file", config);
        return path;
    }

    public static String trimXmlFile(Config testConfig, String path, String fileName) {
        String newPath = null;
        try {
            String xmlString = convertXmlStringIntoString(path);
            Log.Comment("trimming leading and trailing spaces in string", testConfig);
            String trimmedXMLString = xmlString.trim();
            newPath = convertStringToXmlFile(trimmedXMLString, fileName);
        } catch (Exception e) {
            testConfig.logException(e);
        }
        return newPath;
    }

    private static String convertXmlStringIntoString(String path) {
        StringBuilder sb = new StringBuilder();
        Log.Comment("Converting XML File to String", config);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                sb.append(currentLine);
            }
            bufferedReader.close();
        } catch (IOException io) {
            config.logException(io);
        }
        Log.Comment("XML file converted to String", config);
        return sb.toString();
    }

    public static String stripInvalidXmlCharacters(File file) {
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
            e.printStackTrace();
        }
        String xmlStr = stringBuilder.toString();
        return convertStringToXmlFile(xmlStr, file.getName());
    }




}
