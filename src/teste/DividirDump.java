package teste;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;

public class DividirDump {
	
	static String _output_dir = "/home/thalisson/Programas/PIBIC/Divisão de um dump simples/output/";

	public static void main(String[] args) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			try {
				System.out.println("=============builder parse=============");
				Document document = builder.parse(new File("/home/thalisson/Programas/PIBIC/Wikification/dump-starwars-pages-articles.xml"));
				System.out.println("=============builder parse done=============");
				//Normalize the XML Structure; It's just too important !!
				//document.getDocumentElement().normalize();
				System.out.println("2");
				
				//Here comes the root node
				Element root = document.getDocumentElement();
				System.out.println(root.getNodeName());
				
				//Get all <pages>
				System.out.println("=============Get page tag=============");
				NodeList nList = document.getElementsByTagName("page");
				System.out.println("=============Get page tag done=============");
				System.out.println("nList length: " + nList.getLength());
				
				int pageValidate = 0;
				for(int temp = 0; temp < nList.getLength(); temp++) {
					Node node = nList.item(temp);
					if(node.getNodeType() == Node.ELEMENT_NODE) {
						Element pageElement = (Element) node;
						
						//Encontrando a tag quality e criando arquivo
						if(pageElement.getElementsByTagName("ranking").getLength() > 0) {
							pageValidate++;
							System.out.println("Title: " + pageElement.getElementsByTagName("title").item(0).getTextContent());
							System.out.println(pageElement.getElementsByTagName("quality"));
							System.exit(1);
							/*
							pageValidate++;
							//Transformando conteúdo da tag em String.
							StringWriter sw = new StringWriter();
							Transformer t = TransformerFactory.newInstance().newTransformer();
							t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
							t.transform(new DOMSource(node), new StreamResult(sw));
							
							//Escrevendo conteúdo em um arquivo.
							PrintWriter file = new PrintWriter(_output_dir + "page" + temp + ".xml", "UTF-8");
							file.print(sw.toString());
							file.close();*/
						}
						
					}
						
				}
				System.out.println("page validos: " + pageValidate);
				System.out.println("=============PROGRAMA FINALIZADO=============");
			
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		

	}

}
