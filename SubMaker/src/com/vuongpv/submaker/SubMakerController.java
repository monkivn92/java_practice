package com.vuongpv.submaker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javafx.concurrent.*;

public class SubMakerController
{
    @FXML
    private TextArea textEditor;

    @FXML
    private MediaView mediaView;

    @FXML
    private Button txt_src_btn;

    @FXML
    private Button vid_src_btn;

    @FXML
    private Button load_saved_file_btn;

    @FXML
    private Button play_vid_btn;

    @FXML
    private Button pause_vid_btn;

    @FXML
    private Button save_proj_btn;

    private Stage stage;

    private Service<Void> bgthread;

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    @FXML
    public void selectSourceText(ActionEvent e)
    {
        textEditor.setText(selectSingleFile());
    }

    public String selectSingleFile()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        File file = fileChooser.showOpenDialog(stage);

        String file_content = null;

        if(file != null)
        {
            Path fp = Paths.get(file.getAbsolutePath());
            Charset charset = Charset.forName("UTF-16");
            try
            {
                List<String> fcontent = Files.readAllLines(fp, charset);
                if(fcontent.size()>0)
                {
                    file_content = fcontent.toString();
                }
            }
            catch(IOException exc)
            {
                exc.printStackTrace();
            }
        }

        return file_content;

    }

    @FXML
    public void  loadSavedFile()
    {
        bgthread = new Service<Void>(){
            @Override
            protected Task<Void> createTask()
            {
                return new Task<Void>()
                {
                    @Override
                    protected Void call() throws Exception
                    {
                        //updateMessage(selectSingleFile());
                        System.out.println("In Service Task");
                        parseSaveFile();
                        return null;
                    }
                };
            }
        };

        bgthread.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
            @Override
            public void handle(WorkerStateEvent event)
            {
                System.out.println("Import source text done!");
                //textEditor.textProperty().unbind();
            }
        });

        //textEditor.textProperty().bind(bgthread.messageProperty());

        bgthread.restart();
    }

    public void parseSaveFile()
    {

        String txt = textEditor.getText().replace("svn", "SVN");
        textEditor.setText(txt);
    }


}
