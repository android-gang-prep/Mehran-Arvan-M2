package com.example.mehranarvanm2;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlParserData {
    private DocumentBuilder documentBuilder;
    private DocumentBuilderFactory factory;
    private Document document;

    public XmlParserData(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        this.factory = DocumentBuilderFactory.newInstance();
        documentBuilder=factory.newDocumentBuilder();
        document=documentBuilder.parse(inputStream);
    }
    public XmlParserData() throws ParserConfigurationException {
        this.factory = DocumentBuilderFactory.newInstance();
        documentBuilder=factory.newDocumentBuilder();

    }
    public void set(String data) throws IOException, SAXException {
        document=documentBuilder.parse(data);
    }



    public List<String> get(String string, boolean date) {
        NodeList parameters = document.getElementsByTagName("parameter");
        for (int i = 0; i < parameters.getLength(); i++) {
            Node node = parameters.item(i);
            String value = node.getAttributes().getNamedItem("name").getNodeValue();

            if (value.startsWith(string)) {
                return readParameter(node, date);
            }
        }
        return new ArrayList<>();
    }

    private List<String> readParameter(Node node, boolean date) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node location = nodeList.item(i);
            String tagName = location.getNodeName();
            if (tagName.equals("location")) {
                if (date) return readDates(location);
                else return readLocation(location);
            }
        }
        return new ArrayList<>();
    }

    private List<String> readLocation(Node node) {
        List<String> items = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node location = nodeList.item(i);
            String tagName = location.getNodeName();
            String value = location.getTextContent();

            if (tagName.equals("value")) items.add(value);
        }
        return items;
    }

    private List<String> readDates(Node node) {
        List<String> items = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node location = nodeList.item(i);
            String tagName = location.getNodeName();
            String value = location.getAttributes().getNamedItem("date").getNodeValue();


            if (tagName.equals("value")) items.add(value);
        }
        return items;
    }

}
