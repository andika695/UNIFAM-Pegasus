package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import model.Chat;
import model.ArrayList; // Gunakan ArrayList buatan sendiri
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class FXMLFamilyChatController {

    @FXML
    private TextField inputMessage;

    @FXML
    private Button sendButton;

    @FXML
    private VBox chatBox;

    private ArrayList<Chat> messages = new ArrayList<>();
    private final String FILE_PATH = "chat.xml";

    @FXML
    public void initialize() {
        messages = loadMessages();
        refreshChat();
        sendButton.setOnAction(e -> handleSend());
    }

    @FXML
    private void handleSend() {
        String text = inputMessage.getText().trim();
        if (!text.isEmpty()) {
            Chat msg = new Chat("Family", text);
            messages.add(msg);
            addMessageToUI(msg);
            saveMessages();
            inputMessage.clear();
        }
    }

    private void addMessageToUI(Chat message) {
        Label label = new Label(message.getSender() + ": " + message.getContent());
        label.setWrapText(true);
        label.setStyle("-fx-background-color: #fff1f6; -fx-padding: 10; -fx-background-radius: 10;");

        label.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                ContextMenu menu = new ContextMenu();

                MenuItem editItem = new MenuItem("Edit");
                editItem.setOnAction(e -> {
                    TextInputDialog dialog = new TextInputDialog(message.getContent());
                    dialog.setTitle("Edit Message");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Edit:");
                    dialog.showAndWait().ifPresent(newText -> {
                        message.setContent(newText);
                        saveMessages();
                        refreshChat();
                    });
                });

                MenuItem deleteItem = new MenuItem("Delete");
                deleteItem.setOnAction(e -> {
                    messages.remove(message);
                    saveMessages();
                    refreshChat();
                });

                menu.getItems().addAll(editItem, deleteItem);
                menu.show(label, event.getScreenX(), event.getScreenY());
            }
        });

        chatBox.getChildren().add(label);
    }

    private void refreshChat() {
        chatBox.getChildren().clear();
        for (int i = 0; i < messages.size(); i++) {
            Chat msg = (Chat) messages.get(i); // Cast karena arrayList.get() return-nya Object
            addMessageToUI(msg);
        }
    }

    private void saveMessages() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("messages");
            doc.appendChild(root);

            for (int i = 0; i < messages.size(); i++) {
                Chat msg = (Chat) messages.get(i);
                Element messageElem = doc.createElement("message");

                Element sender = doc.createElement("sender");
                sender.appendChild(doc.createTextNode(msg.getSender()));
                messageElem.appendChild(sender);

                Element content = doc.createElement("content");
                content.appendChild(doc.createTextNode(msg.getContent()));
                messageElem.appendChild(content);

                root.appendChild(messageElem);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(FILE_PATH));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Chat> loadMessages() {
        ArrayList<Chat> loadedMessages = new ArrayList<>();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return loadedMessages;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList nodeList = doc.getElementsByTagName("message");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element elem = (Element) nodeList.item(i);
                String sender = elem.getElementsByTagName("sender").item(0).getTextContent();
                String content = elem.getElementsByTagName("content").item(0).getTextContent();
                loadedMessages.add(new Chat(sender, content));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loadedMessages;
    }
}
