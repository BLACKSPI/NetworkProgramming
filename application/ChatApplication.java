package application;

import java.io.*;
import java.net.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class ChatApplication extends Application {
   private TextField messageField = new TextField();
   private TextArea chatArea = new TextArea();
   private Button sendButton = new Button("Send");
   private Socket clientSocket;
   private PrintWriter out;
   private BufferedReader in;
   
   public void start(Stage primaryStage) throws Exception {
      // Create the UI
      chatArea.setEditable(false);
      chatArea.setWrapText(true);
      VBox chatBox = new VBox(10, chatArea, messageField, sendButton);
      chatBox.setPadding(new Insets(10));
      primaryStage.setScene(new Scene(chatBox));
      primaryStage.setTitle("Chat Application");
      primaryStage.show();
      
      // Connect to the server
      clientSocket = new Socket("localhost", 8006);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      
      // Start a new thread to handle incoming messages
      new Thread(new IncomingMessageHandler()).start();
      
      // Send messages when the user presses the "Send" button
      sendButton.setOnAction(e -> {
         String message = messageField.getText();
         out.println(message);
         messageField.clear();
      });
   }
   
   public static void main(String[] args) {
      launch(args);
   }
   
   // Thread to handle incoming messages
   class IncomingMessageHandler implements Runnable {
      public void run() {
         try {
            String message;
            while ((message = in.readLine()) != null) {
               chatArea.appendText(message + "\n");
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}

