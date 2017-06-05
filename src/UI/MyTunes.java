/*
 * This class is the main GUI for the program, it is the first window to be displayed to the user,
 * and the users main experience of the program as it presents most of the features.
 */
package UI;

import BE.Song;
import BLL.ModelMyTunes;
import BLL.MyTunesException;
import BLL.Sound.SoundEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import UI.DragAndDrop.DragAndDropEvent;
import UI.DragAndDrop.TableRowTransferHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.DropMode;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @authors Dennis, Niels, Alex and Tim.
 */
public class MyTunes extends javax.swing.JFrame
{

    /**
     * Instance variables.
     */
    private final ModelMyTunes model;
    private SongTableModel songTable;
    private PlaylistTableModel playlistSongTable;
    private PlaylistsTableModel playlistsTableModel;
    private final String searchBarHintText = "Search here...";

    /**
     * Constructor. Creates new form MyTunes Initiates the GUI, sets up its
     * components, design and general settings.
     */
    public MyTunes()
    {
        setGUIDesign();
        initComponents();

        //Set default values of instance varialbes.
        model = new ModelMyTunes();

        //Add models to ui tables. Ex. Songs tabel.
        setModels();

        //Add listeners to ui elements.
        setListeners();

        //Fix MAC problem when closing window.
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //Load settings.
        loadStartupSettings();

        //Hide the clear button.
        clearBtn.setVisible(false);

        //Drag and drop.
        EnableDragAndDrop();

        setupTimer();
        setSearchCompatibleWithEnterButton();
    }

    /**
     * Add the ability to press enter when using the search option.
     */
    private void setSearchCompatibleWithEnterButton()
    {
        filterTxt.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    filterBtn1.doClick();
                }
            }
        });
    }

    /**
     * Set the GUI design to display the "Nimbus" style, look and feel.
     *
     * @throws MyTunesException
     */
    private void setGUIDesign() throws MyTunesException
    {
        //set design
        try
        {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (Exception e)
        {
            throw new MyTunesException("ERROR - Unable to set GUI design correctly.");
        }
    }

    /**
     * Sets up a recurring timer to update the song table. This timer is used in
     * connection with the autoSync feature and only activated if autoSync is
     * enabled in settings.
     */
    private void setupTimer()
    {
        //setup timer + timertask.
        Timer refreshTable = new Timer();
        TimerTask tasknew = new TimerTask()
        {

            @Override
            public void run()
            {
                if (model.getSetting("SYNCHRONIZE_ACTIVATED").equalsIgnoreCase("true")
                        && !clearBtn.isVisible() //if a search is being done, don't update table.
                        )
                {
                    updateSongTable();
                }
            }
        };
        //start recurring timer
        refreshTable.schedule(tasknew, 6000, 6000);
    }

    /**
     * Enables drag and drop on playlistSongTable, and sets the
     * playlistSongTable to the designated drop zone and attaches a dragAndDrop
     * listener.
     *
     */
    private void EnableDragAndDrop()
    {
        playlistTable.setDragEnabled(true); //enables drag and drop
        playlistTable.setDropMode(DropMode.INSERT_ROWS);
        playlistTable.setTransferHandler(new TableRowTransferHandler(playlistTable));
        playlistSongTable.addDragAndDropListener(new DragAndDropEvent()
        {

            @Override
            public void onDrag()
            {
                model.setPlaylistList(playlistSongTable.getList());
                updatePlaylistSongTable();
            }
        });

    }

    /**
     * Adds models to the tables in the UI. Ex. Songs table.
     */
    private void setModels()
    {

        //Create tabel models for Playlist, Songs, Playlist table.
        songTable = new SongTableModel(model.getAllSongs());
        songsTable.setModel(songTable);

        playlistsTableModel = new PlaylistsTableModel(model.getAllPlaylists());
        playlistsTable.setModel(playlistsTableModel);

        playlistSongTable = new PlaylistTableModel(new ArrayList<>());
        playlistTable.setModel(playlistSongTable);
    }

    /**
     * Adds listeners to UI elements like the tables in the UI.
     */
    private void setListeners()
    {

        //Add selection listener to songtable and playlist table.
        songsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent lse)
            {
                if (lse.getValueIsAdjusting())
                {
                    return;
                }
                onSelectedSong(lse);
            }
        });

        playlistsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent lse)
            {
                if (lse.getValueIsAdjusting())
                {
                    return;
                }
                onSelectedPlaylist(lse);
            }
        });

        //Add listener to soundManager through the model, to be notified by
        //changes in the music.
        model.addSoundEventListener(new SoundEventListener()
        {
            /**
             * This event is called when the music gets paused.
             */
            @Override
            public void onPaused()
            {
                //Update current player status.
                updateCurrentStateLbl("...Is Paused");

                //Update play button image.
                playBtn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/Resources/play.png")));
            }

            /**
             * This event is called when the music stops playing, like a song
             * and so on.
             */
            @Override
            public void onStopped()
            {
                //Update current label to display current playing song.
                updateCurrentLbl("(NONE)");

                //Update play button image.
                playBtn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/Resources/play.png")));
            }

            /**
             * This event is called when the music starts playing a new song or
             * from paused state.
             *
             * @param index - Index of the song begins to play.
             * @param song - Song object of the song which is playing.
             */
            @Override
            public void onPlaying(int index, Song song)
            {
                //Update current label to display current playing song.
                updateCurrentLbl(song.getTitle());
                updateCurrentStateLbl("...Is Playing");

                //Update play button image.
                playBtn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/Resources/pause.png")));
            }
        });

    }

    /**
     * This method is called from the event listener on the playlist table.
     *
     * @param lse - ListSelectionEvent
     */
    private void onSelectedPlaylist(ListSelectionEvent lse)
    {
        int rowIndex = playlistsTable.getSelectedRow();
        if (rowIndex != -1)  // something is selected
        {
            //Notify the model about the selection of a playlist.
            model.onSelectedPlaylist(playlistsTable.convertRowIndexToModel(rowIndex));

            //Change state of buttons.
            editPlaylistsButton.setEnabled(true);
            deletePlaylistsButton.setEnabled(true);
            updatePlaylistSongTable();
        }
    }

    /**
     * This method is called from the event listener on the song table.
     *
     * @param lse - ListSelectionEvent
     */
    private void onSelectedSong(ListSelectionEvent lse)
    {
        int rowIndex = songsTable.getSelectedRow();
        if (rowIndex != -1)  // something is selected
        {
            model.onSelectedSong(songsTable.convertRowIndexToModel(rowIndex));

            //Change state of buttons.
            editSongTable.setEnabled(true);
            deleteSongButton.setEnabled(true);
        }
    }

    /**
     * Updates the song table, by updating the model with a new list.
     */
    public void updateSongTable()
    {
        int selectedSongRow = songsTable.getSelectedRow(); //get row selected.
        if (model.getAllSongs() != null)
        {
            songTable.setList(model.getAllSongs()); //update list, selection is lost.
        }
        if (songsTable.getRowCount() > 0 //if conditions are met.
                && selectedSongRow < songsTable.getRowCount()
                && !playlistTable.hasFocus()
                && model.getAllSongs() != null
                && selectedSongRow != -1)
        {
            //select same row which was selected before the table updated.
            songsTable.setRowSelectionInterval(
                    selectedSongRow,
                    selectedSongRow); //select song in songlist
        }
    }

    /**
     * Updates the playlist song table, by updating the model with a new list.
     */
    public void updatePlaylistSongTable()
    {
        int selectedPlaylistSongRow = playlistTable.getSelectedRow(); //get row selected.
        if (model.getSelectedPlaylist() != null)
        {
            playlistSongTable.setList(model.getSelectedPlaylist().getSongList()); //update list, selection is lost.
        }
        if (playlistTable.getRowCount() > 0 //if conditions are met.
                && selectedPlaylistSongRow < playlistTable.getRowCount()
                && !songsTable.hasFocus()
                && model.getSelectedPlaylist() != null
                && selectedPlaylistSongRow != -1)
        {
            //select same row which was selected before the table updated.
            playlistTable.setRowSelectionInterval(
                    selectedPlaylistSongRow,
                    selectedPlaylistSongRow); //select song in songlist
        }
    }

    /**
     * Updates the playlist table, by updating the model with a new list.
     */
    public void updatePlaylistsTable()
    {
        int selectedPlaylistRow = playlistsTable.getSelectedRow(); //get row selected.
        if (model.getAllPlaylists() != null)
        {
            playlistsTableModel.setList(model.getAllPlaylists()); //update list, selection is lost.
        };
        if (playlistsTable.getRowCount() > 0 //if conditions are met.
                && selectedPlaylistRow < playlistsTable.getRowCount()
                && model.getAllPlaylists() != null
                && selectedPlaylistRow != -1)
        {
            //select same row which was selected before the table updated.
            playlistsTable.setRowSelectionInterval(
                    selectedPlaylistRow,
                    selectedPlaylistRow); //select row in playlist
        }
        //if bottom most row was deleted, move one row up in selection.
        else if (playlistsTable.getRowCount() > 0
                && model.getAllPlaylists() != null
                && selectedPlaylistRow != -1)
        {
            playlistsTable.setRowSelectionInterval(
                    selectedPlaylistRow - 1,
                    selectedPlaylistRow - 1);
        }
    }

    /**
     * Updates the current label with the given text. This is used to display
     * which song is playing or was playing last.
     *
     * @param message - Text to display in current label.
     */
    public void updateCurrentLbl(String message)
    {
        currentLbl.setText(message);
    }

    /**
     * Updates the current state label with the given text. This is to display
     * whether the song last playing is paused or playing.
     *
     * @param message - Text to display in current label.
     */
    public void updateCurrentStateLbl(String message)
    {
        currentState.setText(message);
    }

    /**
     * Display the given message as an info message in the message dialog. This
     * is used for informational pop up messages.
     *
     * @param message - Message to display.
     */
    public void displayMessage(String message)
    {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays the given error message in an error message dialog. This is used
     * for error pop up messages.
     *
     * @param message - Message to display.
     */
    public void displayErrorMessage(String message)
    {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Notifies the model about the window is closing and MyTunes is closing.
     */
    private void onClose()
    {
        //Notify the model about the application is closing.
        model.close();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel6 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        nextBtn = new javax.swing.JButton();
        prevBtn = new javax.swing.JButton();
        playBtn1 = new javax.swing.JButton();
        volSlider1 = new javax.swing.JSlider();
        jPanel10 = new javax.swing.JPanel();
        currentState = new javax.swing.JLabel();
        currentLbl = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        clearBtn = new javax.swing.JButton();
        filterTxt = new javax.swing.JTextField();
        filterBtn1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        playlistsTable = new javax.swing.JTable();
        newPlaylistsButton = new javax.swing.JButton();
        editPlaylistsButton = new javax.swing.JButton();
        deletePlaylistsButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        playlistTable = new javax.swing.JTable();
        upPlaylistButton1 = new javax.swing.JButton();
        downPlaylistButton1 = new javax.swing.JButton();
        playlistDeleteButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        songsTable = new javax.swing.JTable();
        addBtn = new javax.swing.JButton();
        newSongButton = new javax.swing.JButton();
        editSongTable = new javax.swing.JButton();
        deleteSongButton = new javax.swing.JButton();
        closeButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btnSettings1 = new javax.swing.JButton();
        importFromFolder = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MyTunes");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel6.setPreferredSize(new java.awt.Dimension(483, 129));
        jPanel6.setLayout(new java.awt.GridLayout(1, 0));

        nextBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/Resources/next-right.png"))); // NOI18N
        nextBtn.setBorderPainted(false);
        nextBtn.setContentAreaFilled(false);
        nextBtn.setFocusPainted(false);
        nextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBtnActionPerformed(evt);
            }
        });

        prevBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/Resources/next-left.png"))); // NOI18N
        prevBtn.setBorderPainted(false);
        prevBtn.setContentAreaFilled(false);
        prevBtn.setFocusPainted(false);
        prevBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevBtnActionPerformed(evt);
            }
        });

        playBtn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/Resources/play.png"))); // NOI18N
        playBtn1.setBorderPainted(false);
        playBtn1.setContentAreaFilled(false);
        playBtn1.setFocusPainted(false);
        playBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playBtn1ActionPerformed(evt);
            }
        });

        volSlider1.setValue(100);
        volSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                volSlider1StateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(prevBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(playBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(volSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(129, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(playBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prevBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nextBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(volSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        currentState.setText("...Is Playing");
        currentState.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        currentLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentLbl.setText("(NONE)");
        currentLbl.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(139, 139, 139)
                .addComponent(currentLbl)
                .addGap(0, 0, 0)
                .addComponent(currentState)
                .addContainerGap(140, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(currentLbl)
                    .addComponent(currentState))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        clearBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/Resources/Ps-x-button.png"))); // NOI18N
        clearBtn.setBorderPainted(false);
        clearBtn.setContentAreaFilled(false);
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBtnActionPerformed(evt);
            }
        });

        filterTxt.setText("Search here...");
        filterTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                filterTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                filterTxtFocusLost(evt);
            }
        });

        filterBtn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/Resources/search.png"))); // NOI18N
        filterBtn1.setBorderPainted(false);
        filterBtn1.setContentAreaFilled(false);
        filterBtn1.setFocusPainted(false);
        filterBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterBtn1ActionPerformed(evt);
            }
        });

        jLabel7.setText("Filter:");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(0, 202, Short.MAX_VALUE)
                .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(filterTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(filterBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filterTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearBtn)
                    .addComponent(filterBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel3);

        jPanel7.setLayout(new java.awt.GridLayout(1, 0));

        jPanel8.setLayout(new java.awt.GridLayout(1, 0));

        playlistsTable.setAutoCreateRowSorter(true);
        playlistsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        playlistsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                playlistsTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(playlistsTable);

        newPlaylistsButton.setText("New...");
        newPlaylistsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPlaylistsButtonActionPerformed(evt);
            }
        });

        editPlaylistsButton.setText("Edit...");
        editPlaylistsButton.setEnabled(false);
        editPlaylistsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPlaylistsButtonActionPerformed(evt);
            }
        });

        deletePlaylistsButton.setText("Delete");
        deletePlaylistsButton.setEnabled(false);
        deletePlaylistsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletePlaylistsButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Playlists");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(newPlaylistsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editPlaylistsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deletePlaylistsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 96, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newPlaylistsButton)
                    .addComponent(editPlaylistsButton)
                    .addComponent(deletePlaylistsButton))
                .addGap(24, 24, 24))
        );

        jPanel8.add(jPanel1);

        playlistTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        playlistTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                playlistTableMouseClicked(evt);
            }
        });
        playlistTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                playlistTableFocusGained(evt);
            }
        });
        jScrollPane2.setViewportView(playlistTable);

        upPlaylistButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/Resources/arrowUp.png"))); // NOI18N
        upPlaylistButton1.setBorderPainted(false);
        upPlaylistButton1.setContentAreaFilled(false);
        upPlaylistButton1.setFocusPainted(false);
        upPlaylistButton1.setPreferredSize(new java.awt.Dimension(67, 25));
        upPlaylistButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upPlaylistButton1ActionPerformed(evt);
            }
        });

        downPlaylistButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/Resources/arrowDown.png"))); // NOI18N
        downPlaylistButton1.setBorderPainted(false);
        downPlaylistButton1.setContentAreaFilled(false);
        downPlaylistButton1.setFocusPainted(false);
        downPlaylistButton1.setPreferredSize(new java.awt.Dimension(67, 25));
        downPlaylistButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downPlaylistButton1ActionPerformed(evt);
            }
        });

        playlistDeleteButton.setText("Delete");
        playlistDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playlistDeleteButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Songs on playlist");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(upPlaylistButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(downPlaylistButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                                .addComponent(playlistDeleteButton))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(upPlaylistButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(downPlaylistButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playlistDeleteButton))
                .addGap(25, 25, 25))
        );

        jPanel8.add(jPanel2);

        jPanel7.add(jPanel8);

        songsTable.setAutoCreateRowSorter(true);
        songsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title", "Artist", "Genre", "Length"
            }
        ));
        songsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                songsTableMouseClicked(evt);
            }
        });
        songsTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                songsTableFocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(songsTable);

        addBtn.setText("Add");
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        newSongButton.setText("New...");
        newSongButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSongButtonActionPerformed(evt);
            }
        });

        editSongTable.setText("Edit...");
        editSongTable.setEnabled(false);
        editSongTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSongTableActionPerformed(evt);
            }
        });

        deleteSongButton.setText("Delete");
        deleteSongButton.setEnabled(false);
        deleteSongButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSongButtonActionPerformed(evt);
            }
        });

        closeButton1.setText("Close");
        closeButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Songs");

        btnSettings1.setText("Settings");
        btnSettings1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSettings1ActionPerformed(evt);
            }
        });

        importFromFolder.setText("Synchronize with default folder");
        importFromFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importFromFolderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(addBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSettings1))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(newSongButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editSongTable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteSongButton)
                        .addGap(18, 18, 18)
                        .addComponent(importFromFolder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                        .addComponent(closeButton1)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(btnSettings1))
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(addBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 378, Short.MAX_VALUE)))
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(newSongButton)
                        .addComponent(editSongTable)
                        .addComponent(deleteSongButton)
                        .addComponent(importFromFolder))
                    .addComponent(closeButton1))
                .addGap(24, 24, 24))
        );

        jPanel7.add(jPanel9);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 1239, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Listener for the NewSong button in the UI. Creates a new modal window
     * with the option to add the file location and information of a single
     * song, the song can then be saved to the song list. The song table is
     * updated after the window closes, to reflect the possible change.
     *
     * @param evt
     */
    private void newSongButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSongButtonActionPerformed

        //Stop music.
        model.stop();

        //Create a new SongWindow.
        SongWindow win = new SongWindow(this, true, false, model);
        win.setVisible(true);

        if (win.getSong() != null)
        //Notify the model about the new song object.
        {
            model.newSong(win.getSong());
            clearBtn.doClick();
        }

        updateSongTable();
    }//GEN-LAST:event_newSongButtonActionPerformed

    /**
     * Listener for the EditSong button in the UI. Creates a new modal window
     * with the option to edit the file location and information of a single
     * song, the edited song can then be saved to the song list. The song table
     * is updated after the window closes, to reflect the possible change.
     *
     * @param evt
     */
    private void editSongTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editSongTableActionPerformed

        //Stop music.
        model.stop();

        SongWindow win = new SongWindow(this, true, true, model);
        win.setVisible(true);

        if (win.getSong() != null)
        {
            //Notify the model about the change to the song.
            model.editSong(win.getSong());
            clearBtn.doClick();
        }

        updateSongTable();
        updatePlaylistSongTable();
    }//GEN-LAST:event_editSongTableActionPerformed
    /**
     * Button that can delete the currently selected song from myTunes. The user
     * is also prompted whether the song is to be deleted from the HDD or not.
     * JOption panes are used to ask the user to confirm the deletion. Updates
     * the tables afterwards to reflect the change.
     *
     * @param evt
     */
    private void deleteSongButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSongButtonActionPerformed
        //Variable holding the user reply to the first JOption question.
        int response1 = JOptionPane.YES_OPTION;
        //Variable holding the user reply to the second JOption question.
        int response2 = JOptionPane.YES_OPTION;

        if (model.getSetting("SONGDELETION_CONFIRMATION").equalsIgnoreCase("false"))
        {
            //setup the first JOptionPane
            Object[] options1 =
            {
                "Yes",
                "Cancel",
            };
            response1 = JOptionPane.showOptionDialog(null,
                    "Are you sure you wish to delete the song from myTunes?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options1, //The array holding the text of the buttons
                    options1[0]);
            //If the user confirms he wants to delete the song, ask if he also wants to delete it from the HDD.
            if (response1 == JOptionPane.YES_OPTION)
            {
                Object[] options2 =
                {
                    "Yes",
                    "No",
                };
                //setup the second JOptionPane
                response2 = JOptionPane.showOptionDialog(null,
                        "Do you also wish to delete the song from your hard drive?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, //do not use a custom Icon
                        options2, //the titles of buttons
                        options2[0]); //default button title
            }
        }
        if (model.getSetting("SONGDELETION_CONFIRMATION").equalsIgnoreCase("true")
                && model.getSetting("SONGDELETIONHDDYes_CONFIRMATION").equalsIgnoreCase("false")
                && model.getSetting("SONGDELETIONHDDNo_CONFIRMATION").equalsIgnoreCase("false"))
        {
            Object[] options2 =
            {
                "Yes",
                "No",
            };
            //setup the second JOptionPane
            response2 = JOptionPane.showOptionDialog(null,
                    "Do you also wish to delete the song from your hard drive?",
                    "Confirm",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, //do not use a custom Icon
                    options2, //the titles of buttons
                    options2[0]); //default button title
        }
        if (model.getSetting("SONGDELETIONHDDNo_CONFIRMATION").equalsIgnoreCase("true"))
        {
            response2 = JOptionPane.NO_OPTION;
        }

        //If the user replied yes to the first question, and a song is selected, do the following.
        if (response1 == JOptionPane.YES_OPTION && model.getSelectedSong() != null)
        {
            //Stop music.
            model.stop();

            //If the user replied yes to the second question, do the following.
            if (response2 == JOptionPane.YES_OPTION)
            {
                try
                {
                    //Notify the model about the deletion.
                    model.removeSong(model.getSelectedSong(), true);

                    //Display message.
                    displayMessage("File successfully deleted from hard drive.");
                }
                catch (MyTunesException ex)
                {
                    //Display error message.
                    displayErrorMessage("Unable to delete song from hard drive");
                }

            }
            else
            {
                //remove the song the user selected.
                //Notify the model about the deletion.
                model.removeSong(model.getSelectedSong(), false);
            }
        }
        //if song was not selected but the user wanted to delete a song.
        else if (response1 == JOptionPane.YES_OPTION)
        {
            //Display error message.
            displayErrorMessage("No song selected.");
        }

        //Update the song table after the song was deleted.
        updateSongTable();
        updatePlaylistSongTable();
        updatePlaylistsTable();
    }//GEN-LAST:event_deleteSongButtonActionPerformed

    /**
     * Creates a window that prompts the user to enter a name. That name is then
     * used to create a new empty playlist. The playlist created is afterwards
     * added to and displayed in the list of playlists.
     *
     * @param evt
     */
    private void newPlaylistsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newPlaylistsButtonActionPerformed
    {//GEN-HEADEREND:event_newPlaylistsButtonActionPerformed
        //Stop music.
        model.stop();

        PlaylistWindow win = new PlaylistWindow(this, true, null);
        win.setVisible(true);

        if (win.getPlaylistName() != null)
        {
            try
            {

                //Notify the model about the new playlist.
                model.newPlaylist(win.getPlaylistName());

            }
            catch (MyTunesException ex)
            {

                //Display error message.
                displayErrorMessage(ex.getMessage());
            }

            updatePlaylistsTable();
        }
    }//GEN-LAST:event_newPlaylistsButtonActionPerformed

    /**
     * Adds the selected song to the selected playlist. If the song is already
     * in the playlist, the user is asked to confirm that the song should be
     * added again. A JOption pane is used to ask the user for confirmation.
     *
     * @param evt
     */
    private void addBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addBtnActionPerformed
    {//GEN-HEADEREND:event_addBtnActionPerformed

        //Do the following if a song & playlist is selected.
        if (model.getSelectedSong() != null && model.getSelectedPlaylist() != null)
        {
            //if the song is already in the playlist, 
            //ask the user for confirmation on whether or not to add it again.
            if (!model.getSelectedPlaylist().notInPlaylistAlready(model.getSelectedSong()))
            {
                //set up the dialog button text
                Object[] options1 =
                {
                    "Yes",
                    "No",
                };
                int response = JOptionPane.showOptionDialog(null,
                        "Song already in playlist, add it again?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options1, //The array holding the text of the buttons
                        options1[0]);
                //if the user confirms he wishes to add the song, add the song.
                if (response == JOptionPane.YES_OPTION)
                {
                    //Notify the model about the user wants to add the 
                    //selected song to the playlist.
                    model.selectedPlaylistAddSong();
                }
            }
            //if the song was not already in the playlist, simply add it.
            else if (model.getSelectedPlaylist().notInPlaylistAlready(model.getSelectedSong()))
            {
                //Notify the model about the user wants to add the 
                //selected song to the playlist.
                model.selectedPlaylistAddSong();
            }

            updatePlaylistSongTable();
            updatePlaylistsTable();
        }
        //if a song wasn't selected, write an error message.
        else
        {
            //Display error message.
            displayErrorMessage("Please make sure you select both a playlist and a song to add.");
        }
    }//GEN-LAST:event_addBtnActionPerformed

    /**
     * Move up button event, this event is connected to the move up button to
     * playlist, it will move up the song selected in the playlist song table.
     * The selection is moved with the song.
     *
     * @param evt
     */
    private void upPlaylistButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_upPlaylistButton1ActionPerformed
    {//GEN-HEADEREND:event_upPlaylistButton1ActionPerformed
        //Stop music.
        model.stop();

        if (playlistTable.getSelectedRow() != -1)
        {

            //Notiy the ui labout the move up off the selected song.
            model.moveUp(playlistTable.convertRowIndexToModel(playlistTable.getSelectedRow()));
        }
        int selectedSongRow = playlistTable.getSelectedRow();
        if (playlistTable.getRowCount() > 0
                && selectedSongRow - 1 < playlistTable.getRowCount()
                && model.getAllPlaylists() != null
                && selectedSongRow > 0
                && playlistDeleteButton.isEnabled())
        {
            playlistTable.setRowSelectionInterval(
                    selectedSongRow - 1,
                    selectedSongRow - 1);
        }
        updatePlaylistSongTable();
    }//GEN-LAST:event_upPlaylistButton1ActionPerformed

    /**
     * Delete button for the playlist song table. Deletes the selected song from
     * the Playlist song table. The 2 relevant tables are updated to reflect the
     * change.
     *
     * @param evt
     */
    private void playlistDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playlistDeleteButtonActionPerformed

        //Stop music.
        model.stop();

        //Get the selected row in table.
        int row = playlistTable.getSelectedRow();

        if (row != -1)
        {

            model.selectedPlaylistRemoveSong(playlistTable.convertRowIndexToModel(row));
        }

        updatePlaylistSongTable();
        updatePlaylistsTable();
    }//GEN-LAST:event_playlistDeleteButtonActionPerformed

    /**
     * Delete playlist button, this event is connected to the delete playlist
     * button, which deletes a playlist frm MyTunes. The user is asked to
     * confirm the deletion of the playlist before it happens. The relevant
     * tables are updated to reflect the change.
     *
     * @param evt
     */
    private void deletePlaylistsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deletePlaylistsButtonActionPerformed
    {//GEN-HEADEREND:event_deletePlaylistsButtonActionPerformed

        //Variable to hold the response from the user.
        int response = -1;
        //Setup the JOptionPane buttons
        Object[] options1 =
        {
            "Yes",
            "Cancel",
        };
        //ask the user
        response = JOptionPane.showOptionDialog(null,
                "Are you sure you wish to delete the entire playlist from myTunes?\nThe songs will not be deleted.",
                "Confirm",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options1, //The array holding the text of the buttons
                options1[0]);
        //If the user confirms he wants to delete the playlist, delete it.
        if (response == JOptionPane.YES_OPTION)
        {
            //Stop music.
            model.stop();

            //Get selected row.
            int row = playlistsTable.getSelectedRow();

            if (row != -1)
            {
                //Notify the model about the deletion of the playlist.
                model.deletePlaylist(row);
            }

            updatePlaylistsTable();
            playlistSongTable.setList(new ArrayList<>());
        }

    }//GEN-LAST:event_deletePlaylistsButtonActionPerformed

    /**
     * Edit playlist button, this event is connected to the edit playlist button
     * and allows for editing the name of a playlist.
     *
     * @param evt
     */
    private void editPlaylistsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_editPlaylistsButtonActionPerformed
    {//GEN-HEADEREND:event_editPlaylistsButtonActionPerformed
        //Stop music.
        model.stop();

        //Get selected row.
        int row = playlistsTable.getSelectedRow();

        if (row != -1)
        {

            PlaylistWindow w = new PlaylistWindow(this, true, model.getSelectedPlaylist());
            w.setVisible(true);

            //Notify the model about the renaming.
            if (w.getPlaylistName() != null)
            {
                model.renamePlaylist(w.getPlaylistName());
            }
        }
        updatePlaylistsTable();
    }//GEN-LAST:event_editPlaylistsButtonActionPerformed

    /**
     * Button that opens up the settings window. The program settings can be
     * edited here. The settings are saved when the window is closed down.
     *
     * @param evt
     */
    private void btnSettings1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSettings1ActionPerformed
    {//GEN-HEADEREND:event_btnSettings1ActionPerformed
        SettingsWindow stnsWin = new SettingsWindow(this, true, model);
        stnsWin.setVisible(true);

        model.saveSettings();
    }//GEN-LAST:event_btnSettings1ActionPerformed

    /**
     * This event is connected to the close button, and is called when the
     * button is clicked. The program settings, playlists and songs will be
     * saved to files. Afterwards the program shuts itself down.
     *
     * @param evt
     */
    private void closeButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeButton1ActionPerformed
    {//GEN-HEADEREND:event_closeButton1ActionPerformed
        //Call method before closing.
        onClose();
        dispose();
        System.exit(0);
    }//GEN-LAST:event_closeButton1ActionPerformed

    /**
     * Move down button event, this event is connected to the move down button
     * to playlist, it will move down the song selected in the playlist song
     * table. The selection is moved with the song.
     *
     * @param evt
     */
    private void downPlaylistButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downPlaylistButton1ActionPerformed

        //Stop music.
        model.stop();

        if (playlistTable.getSelectedRow() != -1)
        {

            //Notiy the ui labout the move up off the selected song.
            model.moveDown(playlistTable.convertRowIndexToModel(playlistTable.getSelectedRow()));
        }
        int selectedSongRow = playlistTable.getSelectedRow();
        if (playlistTable.getRowCount() > 0
                && selectedSongRow + 1 < playlistTable.getRowCount()
                && model.getAllPlaylists() != null
                && selectedSongRow != -1
                && playlistDeleteButton.isEnabled())
        {
            playlistTable.setRowSelectionInterval(
                    selectedSongRow + 1,
                    selectedSongRow + 1);
        }
        updatePlaylistSongTable();
    }//GEN-LAST:event_downPlaylistButton1ActionPerformed

    /**
     * This event is called when the user double clicks on a playlist in the
     * playlists table. It starts the first song in the playlist selected, if
     * already playing the song will reset.
     *
     * @param evt
     */
    private void playlistsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playlistsTableMouseClicked

        //Check if double click.
        if (evt.getClickCount() == 2)
        {

            //Check if someting is selected.
            if (model.getSelectedPlaylist() != null)
            {

                try
                {

                    //Get selected item in playlist.
                    int row = playlistTable.getSelectedRow();

                    if (row != -1)
                    {
                        model.playSongInPlaylist(playlistTable.convertRowIndexToModel(row));
                    }
                    else
                    //Play the first song in the playlist.
                    {
                        model.playSongInPlaylist(0);
                    }

                }
                catch (MyTunesException ex)
                {
                    //Display error message.
                    displayErrorMessage(ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_playlistsTableMouseClicked

    /**
     * This event is called when the user double clicks on a song in the
     * playlist table. It starts the selected song, if already playing the song
     * will reset.
     *
     * @param evt
     */
    private void playlistTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playlistTableMouseClicked

        //Check if double click.
        if (evt.getClickCount() == 2)
        {

            //Check if a playlist is selected.
            if (model.getSelectedPlaylist() != null)
            {

                //Get selected song.
                int row = playlistTable.getSelectedRow();

                //Check if a song were selected.
                if (row != -1)
                {

                    try
                    {

                        //Notify the model to play the given song in the
                        //selected playlist.
                        model.playSongInPlaylist(playlistTable.convertRowIndexToModel(row));

                    }
                    catch (MyTunesException ex)
                    {
                        //Display error message.
                        displayErrorMessage(ex.getMessage());
                    }
                }
            }
        }
    }//GEN-LAST:event_playlistTableMouseClicked

    /**
     * This event is called when the user double clicks on the songlist table.
     * It starts the selected song, if already playing the song will reset.
     *
     * @param evt
     */
    private void songsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_songsTableMouseClicked
        //Check if double click.
        if (evt.getClickCount() == 2)
        {
            //Get selected song.
            int row = songsTable.getSelectedRow();

            if (row != -1)
            {
                try
                {
                    //The playlist have not been sorted keep playing.
                    if (!clearBtn.isVisible() && songsTable.getRowSorter().getSortKeys().isEmpty())
                    {
                        //Play songs in the songs list.
                        model.playSongInSongs(songsTable.convertRowIndexToModel(row));
                    }
                    else if (clearBtn.isVisible() && songsTable.getRowSorter().getSortKeys().isEmpty())
                    {
                        //Notify the model to update to the new filtered list.
                        model.setPlayingPlaylist(songTable.getList(), row);
                    }
                    else
                    {
                        //Create list to give to the model to play.
                        List<Song> result = new ArrayList<>();

                        //Get the list in the model of the songs table.
                        List<Song> songs = songTable.getList();

                        //Loop through each list in the songstable and set the
                        //correct order.
                        for (int i = 0; i < songs.size(); i++)
                        {
                            result.add(songs.get(songsTable.convertRowIndexToModel(i)));

                            //Play playlist.
                            model.setPlayingPlaylist(result, row);
                        }
                    }
                }
                catch (MyTunesException ex)
                {
                    //Display error message.
                    displayErrorMessage(ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_songsTableMouseClicked
    /**
     * Checks if the songs in the default directory are all in the playlist. If
     * not,they will be added.
     *
     * @param evt
     */
    private void importFromFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importFromFolderActionPerformed
        int syncResponse = JOptionPane.NO_OPTION;
        Object[] options =
        {
            "Yes",
            "Cancel",
        };
        syncResponse = JOptionPane.showOptionDialog(null,
                "- WARNING -"
                + "\nIf you have many songs in your music folder"
                + "\nsynchronizing might take a few minutes."
                + "\nAre you sure you wish to synchronize at this time?",
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options, //The array holding the text of the buttons
                options[0]);
        //If the user confirms he wants to delete the song, ask if he also wants to delete it from the HDD.
        if (syncResponse == JOptionPane.YES_OPTION)
        {
            model.loadFromMusic();
            updateSongTable();
        }
    }//GEN-LAST:event_importFromFolderActionPerformed

    /**
     * If the program is shut down through the top right corner x, click the
     * close button to do the program closure actions.
     *
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        closeButton1.doClick();
    }//GEN-LAST:event_formWindowClosing

    /**
     * If the playlist song table gains focus, de-select the main songlist,
     * disable the buttons related to selecting songs in it and activate the
     * buttons relevant for the playlist song table.
     *
     * @param evt
     */
    private void playlistTableFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_playlistTableFocusGained
    {//GEN-HEADEREND:event_playlistTableFocusGained
        songsTable.clearSelection();
        playlistDeleteButton.setEnabled(true);
        deleteSongButton.setEnabled(false);
        editSongTable.setEnabled(false);
    }//GEN-LAST:event_playlistTableFocusGained

    /**
     * If the main songlist table gains focus, de-select the playlist song
     * table, disable the buttons related to selecting songs in it and activate
     * the buttons relevant for the main songlist table.
     *
     * @param evt
     */
    private void songsTableFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_songsTableFocusGained
    {//GEN-HEADEREND:event_songsTableFocusGained
        playlistTable.clearSelection();
        playlistDeleteButton.setEnabled(false);
        deleteSongButton.setEnabled(true);
        editSongTable.setEnabled(true);
    }//GEN-LAST:event_songsTableFocusGained

    /**
     * Changes the song from the current to the next song, Also moves the
     * selection if the table selected is the main song list.
     *
     * @param evt
     */
    private void nextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBtnActionPerformed
        if (deleteSongButton.isVisible())
        {
            int selectedSongRow = songsTable.getSelectedRow();
            if (songsTable.getRowCount() > 0
                    && selectedSongRow + 1 < songsTable.getRowCount()
                    && model.getAllPlaylists() != null
                    && selectedSongRow != -1)
            {
                songsTable.setRowSelectionInterval(
                        selectedSongRow + 1,
                        selectedSongRow + 1);
            }
        }

        try
        {
            //Notifiy the model that the user wants to skip song.
            model.next();
        }
        catch (MyTunesException ex)
        {
            //Display error message.
            displayErrorMessage(ex.getMessage());
        }
    }//GEN-LAST:event_nextBtnActionPerformed
    /**
     * Changes the song from the current to the previous song, Also moves the
     * selection if the table selected is the main song list.
     *
     * @param evt
     */
    private void prevBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevBtnActionPerformed
        if (deleteSongButton.isVisible())
        {
            int selectedSongRow = songsTable.getSelectedRow();
            if (songsTable.getRowCount() > 0
                    && selectedSongRow - 1 < songsTable.getRowCount()
                    && model.getAllPlaylists() != null
                    && selectedSongRow > 0)
            {
                songsTable.setRowSelectionInterval(
                        selectedSongRow - 1,
                        selectedSongRow - 1);
            }
        }

        try
        {

            //Notifiy the model about the user wants to skip song.
            model.previous();

        }
        catch (MyTunesException ex)
        {

            //Display error message.
            displayErrorMessage(ex.getMessage());
        }
    }//GEN-LAST:event_prevBtnActionPerformed
    /**
     * Plays music.
     *
     * @param evt
     */
    private void playBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playBtn1ActionPerformed
        try
        {
            //Notify the model about the user wants to here music.
            model.play();
        }
        catch (MyTunesException ex)
        {
            //Display error message.
            displayErrorMessage(ex.getMessage());
        }
    }//GEN-LAST:event_playBtn1ActionPerformed
    /**
     * This event is connected to volume slider to control the volume of the
     * song mytunes is playing.
     *
     * @param evt
     */
    private void volSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_volSlider1StateChanged
        //Notify the model about the volume change.
        model.volume(volSlider1.getValue());
    }//GEN-LAST:event_volSlider1StateChanged
    /**
     * clears the search text field and updates the main song table to show all
     * songs.
     *
     * @param evt
     */
    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
        if (!filterTxt.getText().trim().isEmpty() || clearBtn.isVisible())
        {
            filterTxt.setText(null);
            clearBtn.setVisible(false);
            updateSongTable();
        }
    }//GEN-LAST:event_clearBtnActionPerformed

    /**
     * If the search field contains the hint text when it gains focus, set the
     * field to contain no text.
     *
     * @param evt
     */
    private void filterTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filterTxtFocusGained
        if (filterTxt.getText().equalsIgnoreCase(searchBarHintText))
        {
            filterTxt.setText("");
        }
    }//GEN-LAST:event_filterTxtFocusGained

    /**
     * If the search field contains no text when it loses focus, set the field
     * to contain its hint text.
     *
     * @param evt
     */
    private void filterTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filterTxtFocusLost
        if (filterTxt.getText().equalsIgnoreCase(""))
        {
            filterTxt.setText(searchBarHintText);
        }
    }//GEN-LAST:event_filterTxtFocusLost

    /**
     * Starts a search through the songs known to MyTunes. The search is based
     * on the text in the search field.
     *
     * @param evt
     */
    private void filterBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBtn1ActionPerformed
        if (!filterTxt.getText().trim().isEmpty()
                && !filterTxt.getText().equalsIgnoreCase(searchBarHintText))
        {
            clearBtn.setVisible(true);
            songTable.setList(model.searchSong(filterTxt.getText().trim()));
        }
        else if (filterTxt.getText().trim().isEmpty())
        {
            clearBtn.setVisible(false);
            updateSongTable();
        }
    }//GEN-LAST:event_filterBtn1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JButton btnSettings1;
    private javax.swing.JButton clearBtn;
    private javax.swing.JButton closeButton1;
    private javax.swing.JLabel currentLbl;
    private javax.swing.JLabel currentState;
    private javax.swing.JButton deletePlaylistsButton;
    private javax.swing.JButton deleteSongButton;
    private javax.swing.JButton downPlaylistButton1;
    private javax.swing.JButton editPlaylistsButton;
    private javax.swing.JButton editSongTable;
    private javax.swing.JButton filterBtn1;
    private javax.swing.JTextField filterTxt;
    private javax.swing.JButton importFromFolder;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton newPlaylistsButton;
    private javax.swing.JButton newSongButton;
    private javax.swing.JButton nextBtn;
    private javax.swing.JButton playBtn1;
    private javax.swing.JButton playlistDeleteButton;
    private javax.swing.JTable playlistTable;
    private javax.swing.JTable playlistsTable;
    private javax.swing.JButton prevBtn;
    private javax.swing.JTable songsTable;
    private javax.swing.JButton upPlaylistButton1;
    private javax.swing.JSlider volSlider1;
    // End of variables declaration//GEN-END:variables

    /**
     * Sets up the program according to the startup settings loaded.
     *
     */
    private void loadStartupSettings()
    {
        //Load settings.
        if (model.getSetting("WINDOW_STARTUP") != null)
        {

            //Maximisze the window if setting is true.
            if (model.getSetting("WINDOW_STARTUP").equalsIgnoreCase("True"))
            {
                this.setExtendedState(this.MAXIMIZED_BOTH);
            }
        }
        else
        {
            //Display error message.
            displayErrorMessage("Unable to locate settings preferences correctly.");
        }
    }
}
