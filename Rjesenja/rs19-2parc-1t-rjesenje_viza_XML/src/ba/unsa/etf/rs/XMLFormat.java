package ba.unsa.etf.rs;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class XMLFormat {
    private ArrayList<Drzava> drzave = new ArrayList<>();
    private ArrayList<Grad> gradovi = new ArrayList<>();

    public void ucitaj(File file) throws Exception {

        //Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new Exception("Invalid format exception");
        }

        //Build Document
        Document document = null;
        try {
            document = builder.parse(file);
        } catch (SAXException | IOException e) {
            throw new Exception("Can't parse file as XML");
        }


        //Normalize the XML Structure; It's just too important !!
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();
        if (!root.getTagName().equals("drzave"))
            throw new Exception("Root tag je " + root.getTagName());


        //Get all državas
        NodeList nList = document.getElementsByTagName("drzava");

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                // Nalazimo naziv države
                Node childNode = element.getFirstChild();
                String naziv = null;
                do {
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element childElement = (Element) childNode;
                        if (childElement.getTagName().equals("naziv"))
                            naziv = childElement.getTextContent();
                    }
                    childNode = childNode.getNextSibling();
                } while(childNode != null && naziv == null);

                if (naziv == null)
                    throw new Exception("Fali naziv države");

                Drzava novaDrzava = new Drzava(0, naziv, null);
                if (element.getAttribute("viza").equals("true"))
                    novaDrzava.setViza(true);

                // Gradovi u državi
                Grad glavni = null;
                NodeList nlGradovi = element.getElementsByTagName("grad");
                for (int j = 0; j < nlGradovi.getLength(); j++) {
                    Node gNode = nlGradovi.item(j);
                    if (gNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element gElement = (Element) gNode;
                        NodeList tmp = gElement.getElementsByTagName("naziv");
                        if (tmp.getLength() != 1)
                            throw new Exception("Fali naziv grada za državu " + naziv);

                        String gNaziv = tmp.item(0).getTextContent();
                        tmp = gElement.getElementsByTagName("brojStanovnika");
                        if (tmp.getLength() != 1)
                            throw new Exception("Fali broj stanovnika za grad " + gNaziv);

                        int brojStanovnika = Integer.parseInt(tmp.item(0).getTextContent());
                        Grad noviGrad = new Grad(0, gNaziv, brojStanovnika, novaDrzava);
                        if (gElement.getAttribute("glavni").equals("true"))
                            glavni = noviGrad;
                        gradovi.add(noviGrad);
                    }
                }
                novaDrzava.setGlavniGrad(glavni);
                drzave.add(novaDrzava);
            }
        }
    }

    public void zapisi(File file)  {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();
        } catch (ParserConfigurationException err) {
            err.printStackTrace();
        }

        Element root = document.createElement("drzave");
        document.appendChild(root);

        for (Drzava drzava : drzave) {
            Element eDrzava = document.createElement("drzava");
            if (drzava.isViza()) {
                Attr vizaAttr = document.createAttribute("viza");
                vizaAttr.setValue("true");
                eDrzava.setAttributeNode(vizaAttr);
            }

            Element nazivDrzave = document.createElement("naziv");
            nazivDrzave.appendChild(document.createTextNode(drzava.getNaziv()));
            eDrzava.appendChild(nazivDrzave);

            for (Grad grad : gradovi) {
                if (grad.getDrzava().getNaziv().equals(drzava.getNaziv())) {
                    Element eGrad = document.createElement("grad");
                    if (grad.getNaziv().equals(drzava.getGlavniGrad().getNaziv())) {
                        Attr glavniAttr = document.createAttribute("glavni");
                        glavniAttr.setValue("true");
                        eGrad.setAttributeNode(glavniAttr);
                    }

                    Element nazivGrada = document.createElement("naziv");
                    nazivGrada.appendChild(document.createTextNode(grad.getNaziv()));
                    eGrad.appendChild(nazivGrada);

                    Element brojStanovnika = document.createElement("brojStanovnika");
                    brojStanovnika.appendChild(document.createTextNode(String.valueOf(grad.getBrojStanovnika())));
                    eGrad.appendChild(brojStanovnika);

                    eDrzava.appendChild(eGrad);
                }
            }

            root.appendChild(eDrzava);
        }

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);

            StreamResult streamResult = new StreamResult(file);
            transformer.transform(source, streamResult);
        } catch(TransformerException err) {
            err.printStackTrace();
        }
    }

    public ArrayList<Drzava> getDrzave() {
        return drzave;
    }

    public void setDrzave(ArrayList<Drzava> drzave) {
        this.drzave = drzave;
    }

    public ArrayList<Grad> getGradovi() {
        return gradovi;
    }

    public void setGradovi(ArrayList<Grad> gradovi) {
        this.gradovi = gradovi;
    }
}
