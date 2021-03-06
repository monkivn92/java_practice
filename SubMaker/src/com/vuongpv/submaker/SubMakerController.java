package com.vuongpv.submaker;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.ActionEvent;


import java.io.*;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javafx.concurrent.*;

public class SubMakerController
{
    @FXML
    private VBox textEditor;

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
    private Button star_time_btn;

    @FXML
    private Button end_time_btn;

    @FXML
    private Slider progress_bar_vid;

    @FXML
    private Label time_line_vid;

    private Stage stage;

    private Service<Void> bgthread;
    private MediaPlayer mp;
    private String video_path;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private boolean stopRequested = false;

    private ContextMenu contextMenu;

    private CustomTextField selectedTxTF;

    private Stage  modalStage;
    private VBox modalVbox;
    private HBox modalHbox1;
    private  HBox modalHbox2;
    private Label modalLabel1, modalLabel2;
    private TextField modalTxtF1, modalTxtF2;

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    @FXML
    public void selectSourceText(ActionEvent e)
    {
        String txt = selectSourceTextss();
        textEditor.getChildren().clear();
        //A Task Which Modifies The Scene Graph
        Task<Void> task = new Task<Void>() {
            @Override protected Void call() throws Exception {

                //if (isCancelled()) break;

                StringTokenizer strtok  =new StringTokenizer(txt,".");

                if(strtok.countTokens() > 0)
                {

                    createContextMenu();
                    while (strtok.hasMoreTokens())
                    {
                        CustomTextField ctf = new CustomTextField(strtok.nextToken());
                        ctf.setContextMenu(contextMenu);
                        ctf.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

                            @Override
                            public void handle(ContextMenuEvent event) {

                                contextMenu.show(ctf, event.getScreenX(), event.getScreenY());
                            }
                        });

                        ctf.setOnMouseClicked((MouseEvent me)->{
                            selectedTxTF = (CustomTextField) me.getSource();
                        });

                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                textEditor.getChildren().add(ctf);
                            }
                        });
                    }
                }



                return null;
            }
        };

        new Thread(task).start();
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
            video_path = file.toURI().toString();
            setUpMediaController(video_path);
        }

    }

    @FXML
    public void  loadSavedFile()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");

        File selectedFile = fileChooser.showOpenDialog(stage);
        String targetPath = selectedFile.getAbsolutePath();

        if(targetPath != null && targetPath.indexOf(".data") != -1)
        {
            textEditor.getChildren().clear();

            Task<Void> task = new Task<Void>() {

                @Override protected Void call() throws Exception
                {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run()
                        {
                            try
                            {
                                FileInputStream fi = new FileInputStream(new File(targetPath));
                                ObjectInputStream oi = new ObjectInputStream(fi);

                                ProjectDataObject target = (ProjectDataObject) oi.readObject();

                                setUpMediaController(target.getVidPath());

                                if(contextMenu == null)
                                {
                                    createContextMenu();
                                }

                                target.getTxtdata().forEach((CustomTextField ctf)->{
                                    ctf.setText(ctf.getTxtData());
                                    ctf.setContextMenu(contextMenu);
                                    ctf.setOnMouseClicked((MouseEvent me)->{
                                        selectedTxTF = (CustomTextField) me.getSource();
                                    });
                                    textEditor.getChildren().add(ctf);
                                });
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            catch (ClassNotFoundException e)
                            {
                                e.printStackTrace();
                            }

                        }
                    });

                    return null;
                }
            };

            new Thread(task).start();

        }

    }

    @FXML
    public void handleSetStartTime()
    {
        if(selectedTxTF != null && mp != null)
        {
            play_vid_btn.fire();
            selectedTxTF.setStartTime((long) mp.getCurrentTime().toMillis());

        }
    }

    @FXML
    public void handleSetEndTime()
    {
        if(selectedTxTF != null && mp != null)
        {
            play_vid_btn.fire();
            selectedTxTF.setEndTime((long) mp.getCurrentTime().toMillis());
        }
    }

    @FXML
    public void saveProject()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");

        File selectedFile = fileChooser.showOpenDialog(stage);
        String savePath = selectedFile.getAbsolutePath();

        if(savePath != null && savePath.indexOf(".data") != -1)
        {
            saveFileToDisk(savePath);
        }
    }

    public void saveFileToDisk(String spath)
    {
        //A Task Which Modifies The Scene Graph
        Task<Void> task = new Task<Void>() {

            @Override protected Void call() throws Exception
            {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        ProjectDataObject sobj = new ProjectDataObject();
                        sobj.setVidPath(video_path);
                        sobj.parseTxtdata(textEditor.getChildren());

                        try
                        {
                            // write object to file
                            OutputStream fos = Files.newOutputStream(Paths.get(spath));
                            ObjectOutputStream oos = new ObjectOutputStream(fos);
                            oos.writeObject(sobj);
                            oos.close();

                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                    }
                });

                return null;
            }
        };

        new Thread(task).start();
    }

    public String selectSourceTextss()
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
            Charset charset = Charset.forName("UTF-8");
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



    public void setUpMediaController(String video_path)
    {

        Media m = new Media(video_path);
        mp = new MediaPlayer(m);
        mediaView.setMediaPlayer(mp);
        mediaView.fitWidthProperty().bind(videoPane.widthProperty());
        mediaView.fitHeightProperty().bind(videoPane.heightProperty());
        videoPane.getChildren().remove(pendingLabel);

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

    public void createContextMenu()
    {
        if(contextMenu == null)
        {
            contextMenu = new ContextMenu();
            MenuItem item1 = new MenuItem("Edit");
            item1.setOnAction((ActionEvent e)->{
                handleContextMenuEvent("edit");
            });

            MenuItem item2 = new MenuItem("Remove");
            item2.setOnAction((ActionEvent e)->{
                handleContextMenuEvent("remove");
            });

            contextMenu.getItems().addAll(item1, item2);

        }

    }

    public void handleContextMenuEvent(String task)
    {

        if("remove".equalsIgnoreCase(task))
        {
            if(selectedTxTF != null)
            {
                textEditor.getChildren().remove(selectedTxTF);
            }
        }

        if("edit".equalsIgnoreCase(task) && selectedTxTF != null)
        {
            if(modalStage == null)
            {
                modalStage = new Stage();
                modalStage.initModality(Modality.APPLICATION_MODAL);
                modalStage.initOwner(stage);

                modalLabel1 = new Label("Start Time:");
                modalLabel2 = new Label("End Time:");

                modalTxtF1 = new TextField();
                modalTxtF1.setText(String.valueOf(selectedTxTF.getStartTime()));

                modalTxtF2 = new TextField();
                modalTxtF2.setText(String.valueOf(selectedTxTF.getEndTime()));

                modalHbox1 = new HBox(modalLabel1, modalTxtF1);
                modalHbox2 = new HBox(modalLabel2, modalTxtF2);

                Button updateBtn = new Button("Update");
                updateBtn.setOnAction((ActionEvent e)->{
                    selectedTxTF.setStartTime(Long.parseLong(modalTxtF1.getText()));
                    selectedTxTF.setEndTime(Long.parseLong(modalTxtF2.getText()));
                    modalStage.hide();
                });

                modalVbox = new VBox(modalHbox1, modalHbox2,updateBtn);
                Scene modalScene = new Scene(modalVbox, 300, 200);

                modalStage.setScene(modalScene);
                modalStage.show();

            }
            else
            {
                modalTxtF2.setText(String.valueOf(selectedTxTF.getEndTime()));
                modalTxtF1.setText(String.valueOf(selectedTxTF.getStartTime()));
                modalStage.show();
            }
        }

    }


}


class CustomTextField extends TextArea implements Serializable
{
    private long start_time;
    private long end_time;
    private String txtdata;
    private static final long serialVersionUID = 1868L;

    public long getStartTime()
    {
        return start_time;
    }

    public void setStartTime(long start_time)
    {
        this.start_time = start_time;

    }

    public long getEndTime()
    {
        return end_time;
    }

    public void setEndTime(long end_time)
    {
        this.end_time = end_time;
    }

    public String getTxtData()
    {
        return this.txtdata;
    }


    CustomTextField()
    {
        super();
    }

    CustomTextField(String txt)
    {
        super(txt);
        this.txtdata = txt;
    }


}

class ProjectDataObject implements  Serializable
{
    private String video_path;

    private List<CustomTextField> txtdata = new ArrayList<>();
    private static final long serialVersionUID = 1888968L;

    public void setVidPath(String vp)
    {
        this.video_path = vp;
    }

    public String getVidPath()
    {
        return this.video_path;
    }

    public List<CustomTextField> getTxtdata()
    {
        return txtdata;
    }

    public void parseTxtdata(ObservableList ovb)
    {
        int ovb_size = ovb.size();
        System.out.println(ovb_size);
        if(ovb_size > 0)
        {
            ovb.forEach((item)->{
                txtdata.add((CustomTextField) item);
            });
        }
    }


}