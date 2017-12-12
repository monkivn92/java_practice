package com.vuongpv.submaker;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.event.ActionEvent;


import java.io.File;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.util.Duration;
import java.util.List;
import javafx.concurrent.*;

public class SubMakerController
{
    @FXML
    private TextArea textEditor;

    @FXML
    private Pane videoPane;

    @FXML
    private Label pendingLabel;

    @FXML
    private MediaView mediaView;

    @FXML
    private Button txt_src_btn;

    @FXML
    private Button vid_src_btn;

    @FXML
    private Button load_saved_file_btn;

    @FXML
    private Button save_proj_btn;

    @FXML
    private Button play_vid_btn;

    @FXML
    private Slider progress_bar_vid;

    @FXML
    private Label time_line_vid;

    private Stage stage;

    private Service<Void> bgthread;
    private MediaPlayer mp;

    private boolean atEndOfMedia = false;
    private Duration duration;
    private boolean stopRequested = false;

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    @FXML
    public void selectSourceText(ActionEvent e)
    {
        textEditor.setText(selectSourceText());
    }

    @FXML
    public void selectVideoFile(ActionEvent e)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Video Files", "*.mp4")
        );

        File file = fileChooser.showOpenDialog(stage);

        if(file != null)
        {
            Media m = new Media(file.toURI().toString());
            mp = new MediaPlayer(m);
            mediaView.setMediaPlayer(mp);
            mediaView.fitWidthProperty().bind(videoPane.widthProperty());
            mediaView.fitHeightProperty().bind(videoPane.heightProperty());
            videoPane.getChildren().remove(pendingLabel);
            setUpMediaController();
        }

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
                //textEditor.textProperty().unbind();//prevent error-prone
            }
        });

        //textEditor.textProperty().bind(bgthread.messageProperty());

        bgthread.restart();
    }

    public String selectSourceText()
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

    public void parseSaveFile()
    {

        String txt = textEditor.getText().replace("svn", "SVN");
        textEditor.setText(txt);
    }

    public void setUpMediaController()
    {
        play_vid_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                MediaPlayer.Status status = mp.getStatus();

                if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED)
                {
                    // don't do anything in these states
                    return;
                }

                if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY  || status == MediaPlayer.Status.STOPPED)
                {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia)
                    {
                        mp.seek(mp.getStartTime());
                        atEndOfMedia = false;
                    }
                    mp.play();

                }
                else
                {
                    mp.pause();

                }
            }
        });//play btn

        mp.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov)
            {
                updateValues();
            }
        });

        mp.setOnPlaying(new Runnable() {
            public void run()
            {
                if (stopRequested)
                {
                    mp.pause();
                    stopRequested = false;
                }
                else
                {
                    play_vid_btn.setText("||");
                }
            }
        });

        mp.setOnPaused(new Runnable() {
            public void run()
            {
                System.out.println("onPaused");
                play_vid_btn.setText(">");
            }
        });

        mp.setOnReady(new Runnable() {
            public void run()
            {
                duration = mp.getMedia().getDuration();
                updateValues();
            }
        });

        mp.setOnEndOfMedia(new Runnable() {
            public void run()
            {

                play_vid_btn.setText(">");
                stopRequested = true;
                atEndOfMedia = true;

            }
        });


        //slide event
        progress_bar_vid.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov)
            {
                if (progress_bar_vid.isValueChanging())
                {
                    // multiply duration by percentage calculated by slider position
                    mp.seek(duration.multiply(progress_bar_vid.getValue() / 100.0));
                }
            }
        });




    }

    protected void updateValues()
    {
        /*
        We have replaced the long sleep with code that executes in a worker thread.
        After sleeping for three seconds, the worker thread calls the runLater() method of the
        javafx.application.Platform class, passing it another Runnable  that toggles the rounded corners of
        the rectangle. Because the long-running computation is done in a worker thread,
        the event handler is not blocking the JavaFX application thread.
        The change of fill is now reflected immediately in the UI.
        Because the Platform.runLater() call causes the Runnable to
        be executed on the JavaFX application thread,
        the change to the rounded corners is reflected in the UI after three seconds.
        The reason we have to execute the
        Runnable on the JavaFX application thread is that it modifies the state of a live scene.
        */

        if (time_line_vid != null && progress_bar_vid != null )
        {
            Platform.runLater(new Runnable() {
                public void run()
                {
                    Duration currentTime = mp.getCurrentTime();

                    time_line_vid.setText(formatTime(currentTime, duration));

                    progress_bar_vid.setDisable(duration.isUnknown());

                    if (!progress_bar_vid.isDisabled()  && duration.greaterThan(Duration.ZERO)
                            && !progress_bar_vid.isValueChanging())
                    {
                        progress_bar_vid.setValue(currentTime.toMillis()/duration.toMillis() * 100.0);
                    }

                }
            });
        }
    }


    private static String formatTime(Duration elapsed, Duration duration)
    {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);

        if (elapsedHours > 0)
        {
            intElapsed -= elapsedHours * 60 * 60;
        }

        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60  - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO))
        {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0)
            {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60  - durationMinutes * 60;
            if (durationHours > 0)
            {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            }
            else
            {
                return String.format("%02d:%02d/%02d:%02d", elapsedMinutes,
                        elapsedSeconds, durationMinutes, durationSeconds);
            }
        }
        else
        {
            if (elapsedHours > 0)
            {
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            }
            else
            {
                return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
            }
        }
    }



}
