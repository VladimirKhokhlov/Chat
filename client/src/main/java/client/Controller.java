package client;

import commands.Command;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private HBox authPanel;
    @FXML
    private HBox msgPanel;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ListView<String> clientList;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String nickname;

    private Stage stage;
    private Stage regStage;
    private RegController regController;

    private static PrintWriter write;


    public void setAuthenticated(boolean authenticated) {
        msgPanel.setVisible(authenticated);
        msgPanel.setManaged(authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        if (!authenticated) {
            nickname = "";
        }
        stop();
        setTitle(nickname);
        textArea.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            stage = (Stage) textArea.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                System.out.println("bye");
                if (socket != null && !socket.isClosed()) {
                    try {
                        out.writeUTF(Command.END);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        setAuthenticated(false);
    }

    private void connect() {
        try {
            String IP_ADDRESS = "localhost";
            int PORT = 8189;
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //???????? ????????????????????????????
                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.startsWith(Command.AUTH_OK)) {
                                nickname = str.split("\\s")[1];
                                setAuthenticated(true);
                                createHistoryFile(nickname);
                                break;
                            }

                            if (str.equals(Command.END)) {
                                System.out.println("client disconnected");
                                throw new RuntimeException("server disconnected us");
                            }

                            if (str.equals(Command.REG_OK)) {
                                regController.regOk();
                            }

                            if (str.equals(Command.REG_NO)) {
                                regController.regNo();
                            }
                        } else {
                            textArea.appendText(str + "\n");
                            createHistoryFile(nickname);

                        }
                    }

                    //???????? ????????????
                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals(Command.END)) {
                                System.out.println("client disconnected");
                                break;
                            }
                            if (str.startsWith(Command.CLIENT_LIST)) {
                                String[] tokens = str.split("\\s");
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                    for (int i = 1; i < tokens.length; i++) {
                                        clientList.getItems().add(tokens[i]);
                                    }
                                });
                            }

                        } else {
                            textArea.appendText(str + "\n");
                            putMsgInHistory(str);
                        }
                    }
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    setAuthenticated(false);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createHistoryFile(String nickname) {
        try {
            boolean historyDir = new File("client\\history").mkdir();
            File file = new File("client/history/history_" + nickname + ".txt");
            write = new PrintWriter(new FileOutputStream(file, true), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        if (write != null) {
            write.close();
        }
    }

    private void putMsgInHistory(String msg) {
        write.println(msg);
    }

    @FXML
    public void sendMsg() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth() {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        String msg = String.format("%s %s %s", Command.AUTH, loginField.getText().trim(), passwordField.getText().trim());

        try {
            out.writeUTF(msg);
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTitle(String nickname) {
        if (nickname.equals("")) {
            Platform.runLater(() -> stage.setTitle("GeekChat"));
        } else {
            Platform.runLater(() -> stage.setTitle(String.format("GeekChat [ %s ]", nickname)));
        }
    }

    public void clientListClicked() {
//        System.out.println(clientList.getSelectionModel().getSelectedItem());
        String receiver = clientList.getSelectionModel().getSelectedItem();
        textField.setText(String.format("%s %s ", Command.PRV_MSG, receiver));
    }

    public void registration() {
        if (regStage == null) {
            createRegWindow();
        }
        regStage.show();
    }

    private void createRegWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/reg.fxml"));
            Parent root = fxmlLoader.load();
            regStage = new Stage();
            regStage.setTitle("GeekChat registration");
            regStage.setScene(new Scene(root, 400, 350));
            regController = fxmlLoader.getController();
            regController.setController(this);
            regStage.initModality(Modality.APPLICATION_MODAL);
            regStage.initStyle(StageStyle.UTILITY);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToReg(String login, String password, String nickname) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        String msg = String.format("%s %s %s %s", Command.REG, login, password, nickname);
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
