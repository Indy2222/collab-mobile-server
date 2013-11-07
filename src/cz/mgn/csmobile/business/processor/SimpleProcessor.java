/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mgn.csmobile.business.processor;

import cz.mgn.csmobile.business.UserData;
import cz.mgn.csmobile.persistence.HasMessage;
import cz.mgn.csmobile.persistence.Identificator;
import cz.mgn.csmobile.persistence.User;
import cz.mgn.csmobile.server.Answerer;
import cz.mgn.csmobile.server.ClientConnectionInterface;
import cz.mgn.csmobile.server.Dispetcher;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Martin Indra <aktive at seznam.cz>
 */
public class SimpleProcessor implements ClientConnectionInterface {

    protected Answerer answerer = null;
    protected DocumentBuilder documentBuilder;
    protected UserData userData = new UserData();

    public SimpleProcessor(Answerer answerer) {
        this.answerer = answerer;
        init();
    }

    private void init() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SimpleProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy() {
        userData.destroy();
    }

    public void parse(String query) {
        ByteArrayInputStream fakeStream = null;
        try {
            fakeStream = new ByteArrayInputStream(query.getBytes("UTF-8"));
            Document doc = documentBuilder.parse(fakeStream);
            doc.getDocumentElement().normalize();
            processCommands(doc.getDocumentElement());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SimpleProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(SimpleProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SimpleProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fakeStream.close();
            } catch (IOException ex) {
                Logger.getLogger(SimpleProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected void answer(Document document) {
        try {
            byte[] bytes = XMLUtils.XMLToString(document).getBytes("UTF-8");
            answerer.answer(bytes);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SimpleProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void processCommands(Element commandsElement) {
        NodeList commands = commandsElement.getChildNodes();

        for (int i = 0; i < commands.getLength(); i++) {
            processCommand(commands.item(i));
        }
    }

    protected void processCommand(Node commandNode) {
        String nodeName = commandNode.getNodeName();

        if (XMLConstants.TAG_LOGIN.equals(nodeName)) {
            processLogin(commandNode);
        } else if (XMLConstants.TAG_REGISTER.equals(nodeName)) {
            processRegister(commandNode);
        } else if (XMLConstants.TAG_ADD_IDENTIFICATOR_PHONE_NUMBER.equals(nodeName)) {
            processAddIdentificator(commandNode, Identificator.TYPE_PHONE_NUMBER);
        } else if (XMLConstants.TAG_SEARCH_IDENTIFICATOR_PHONE_NUMBER.equals(nodeName)) {
            processSearchIdentificator(commandNode, Identificator.TYPE_PHONE_NUMBER);
        } else if (XMLConstants.TAG_MESSAGE.equals(nodeName)) {
            sendMessage(commandNode);
        } else if (XMLConstants.TAG_MESSAGES_LIST.equals(nodeName)) {
            getMessages(commandNode);
        }
    }

    protected void getMessages(Node node) {
        if (!userData.isLoggedIn()) {
            return;
        }

        Document doc = documentBuilder.newDocument();
        Element rootElement = doc.createElement(XMLConstants.TAG_DOCUMENT);
        doc.appendChild(rootElement);

        Element tag = doc.createElement(XMLConstants.TAG_MESSAGES_LIST);

        List<HasMessage> messages = userData.getMessages();
        for (HasMessage message : messages) {
            Element elm = doc.createElement(XMLConstants.TAG_MESSAGE);

            elm.setAttribute(XMLConstants.PARAMETER_MESSAGE_AUTHOR, message.getMessageId().getAuthor().getNick());
            elm.setAttribute(XMLConstants.PARAMETER_MESSAGE_READED, "" + message.getReaded());
            elm.setAttribute(XMLConstants.PARAMETER_MESSAGE_TIME, "" + message.getMessageId().getTime().getTime());
            elm.setTextContent(message.getMessageId().getText());

            tag.appendChild(elm);
        }

        rootElement.appendChild(tag);
        answer(doc);
    }

    protected void sendMessage(Node node) {
        System.out.println("a");
        if (!userData.isLoggedIn()) {
            return;
        }

        System.out.println("b");

        ArrayList<String> targets = new ArrayList<String>();
        String text = "";
        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (XMLConstants.TAG_SEND_MESSAGE_TARGETS.equals(n.getNodeName())) {
                NodeList children2 = n.getChildNodes();
                for (int j = 0; j < children2.getLength(); j++) {
                    Element user = (Element) children2.item(j);
                    targets.add(user.getAttribute(XMLConstants.PARAMETER_USER_NICK));
                }
            } else if (XMLConstants.TAG_SEND_MESSAGE_TEXT.equals(n.getNodeName())) {
                NodeList children2 = n.getChildNodes();
                for (int j = 0; j < children2.getLength(); j++) {
                    text = children2.item(j).getTextContent();
                }
            }
        }

        System.out.println("sending = " + text);
        userData.addChatMessage(text, targets);
    }

    protected void processSearchIdentificator(Node node, String type) {
        if (!userData.isLoggedIn()) {
            return;
        }
        Element element = (Element) node;
        String value = element.getAttribute(XMLConstants.PARAMETER_IDENTIFICATOR_VALUE);
        ArrayList<User> users = userData.searchIdentificator(type, value);

        Document doc = documentBuilder.newDocument();
        Element rootElement = doc.createElement(XMLConstants.TAG_DOCUMENT);
        doc.appendChild(rootElement);

        Element searchResults = doc.createElement(XMLConstants.TAG_USER_SEARCH_RESULTS);

        for (User user : users) {
            Element userNode = doc.createElement(XMLConstants.TAG_USER);
            userNode.setAttribute(XMLConstants.PARAMETER_USER_NAME, user.getName());
            userNode.setAttribute(XMLConstants.PARAMETER_USER_NICK, user.getNick());
            searchResults.appendChild(userNode);
        }

        rootElement.appendChild(searchResults);
        answer(doc);
    }

    protected void processAddIdentificator(Node node, String type) {
        if (!userData.isLoggedIn()) {
            return;
        }
        Element element = (Element) node;
        String value = element.getAttribute(XMLConstants.PARAMETER_IDENTIFICATOR_VALUE);
        userData.addIdentificator(type, value);
    }

    protected void processRegister(Node node) {
        try {
            Element element = (Element) node;
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] publicKey = decoder.decodeBuffer(element.getAttribute(XMLConstants.PARAMETER_REGISTER_PUBLIC_KEY));
            String nick = element.getAttribute(XMLConstants.PARAMETER_REGISTER_NICK);
            String name = element.getAttribute(XMLConstants.PARAMETER_REGISTER_NAME);

            int success = userData.register(nick, name, publicKey);
            if (success == 0) {
                registerSucessful();
            } else {
                answerRegisterError(success);
            }
        } catch (IOException ex) {
            Logger.getLogger(SimpleProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void registerSucessful() {
        Document doc = documentBuilder.newDocument();
        Element rootElement = doc.createElement(XMLConstants.TAG_DOCUMENT);
        doc.appendChild(rootElement);

        Element tag = doc.createElement(XMLConstants.TAG_REGISTER_SUCCESSFUL);
        rootElement.appendChild(tag);

        answer(doc);
    }

    protected void answerRegisterError(int errorCode) {
        Document doc = documentBuilder.newDocument();
        Element rootElement = doc.createElement(XMLConstants.TAG_DOCUMENT);
        doc.appendChild(rootElement);

        Element error = doc.createElement(XMLConstants.TAG_REGISTER_ERROR);
        if (errorCode == UserData.REGISTER_NICK_IS_NOT_FREE) {
            error.appendChild(doc.createTextNode("Nick is already assigned to somebody else!"));
        } else {
            error.appendChild(doc.createTextNode("Registration failed!"));
        }
        rootElement.appendChild(error);

        answer(doc);
    }

    protected void processLogin(Node node) {
        try {
            Element element = (Element) node;
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] publicKey = decoder.decodeBuffer(element.getAttribute(XMLConstants.PARAMETER_LOGIN_PUBLIC_KEY));
            String nick = element.getAttribute(XMLConstants.PARAMETER_LOGIN_NICK);
            User user = userData.loginByNickAndPublicKey(nick, publicKey);
            if (user != null) {
                loginSucessful(user);
            } else {
                answerLoginError();
            }
        } catch (IOException ex) {
            Logger.getLogger(SimpleProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void loginSucessful(User user) {
        //TODO: use real
        byte[] publicKey = {1, 2, 3, 4, 5, 6, 7, 8};
        BASE64Encoder encoder = new BASE64Encoder();
        String publicKeyString = encoder.encode(publicKey);

        Document doc = documentBuilder.newDocument();
        Element rootElement = doc.createElement(XMLConstants.TAG_DOCUMENT);
        doc.appendChild(rootElement);

        Element tag = doc.createElement(XMLConstants.TAG_LOGIN_SUCCESSFUL);
        tag.appendChild(doc.createTextNode(publicKeyString));
        rootElement.appendChild(tag);

        answer(doc);
    }

    protected void answerLoginError() {
        Document doc = documentBuilder.newDocument();
        Element rootElement = doc.createElement(XMLConstants.TAG_DOCUMENT);
        doc.appendChild(rootElement);

        Element error = doc.createElement(XMLConstants.TAG_LOGIN_ERROR);
        error.appendChild(doc.createTextNode("Login failed!"));
        rootElement.appendChild(error);

        answer(doc);
    }

    @Override
    public void connectionClosed(Dispetcher eventSource) {
        destroy();
    }
}