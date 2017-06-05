/*
 * the main model for my tunes
 */
package BLL;

import DAL.FileManager;
import BLL.Sound.SoundManagerException;
import BLL.Sound.SoundManager;
import BE.Playlist;
import BE.Song;
import BLL.PlaylistSearch.ISearchMatch;
import BLL.PlaylistSearch.ISearchType;
import BLL.PlaylistSearch.SearchTypeTitle;
import BLL.PlaylistSearch.PlaylistSearch;
import java.io.IOException;
import BLL.PlaylistSearch.SearchMatchContains;
import BLL.PlaylistSearch.SearchTypeArtistName;
import BLL.Sound.ISoundEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dennis, Alex, Niels and Tim.
 */
public class ModelMyTunes
{

    /**
     * Instance variables.
     */
    private SoundManager soundManager;
    private SongsManager songsManager;
    private PlaylistManager playlistManager;
    private PreferenceManager preferenceManager;
    private FileManager fileManager;
    private GenreManager genreManager;
    private LoadFromMusicManager loadFromMusicManager;

    private Playlist selectedPlaylist;
    private Song selectedSong;
    //private ArrayList<Song> changedSongs;

    /**
     * Constructor.
     */
    public ModelMyTunes()
    {

        //Set default values of instance variables.
        soundManager = SoundManager.getInstance();
        songsManager = SongsManager.getInstance();
        playlistManager = PlaylistManager.getInstance();
        fileManager = FileManager.getInstance();
        preferenceManager = PreferenceManager.getInstance();
        genreManager = GenreManager.getInstance();
        loadFromMusicManager = LoadFromMusicManager.getInstance();
        try
        {
            Synchronizer sync = Synchronizer.getInstance();
            sync.start();
            AutoSyncer autoSync = new AutoSyncer();
            autoSync.start();

        }
        catch (MyTunesException ex)
        {
            throw new MyTunesException("ERROR - Synchronizer failed to execute correctly.");
        }

    }

    /**
     * Sets the selected Song in MyTunes.
     *
     * @param song - Song to set as selected.
     */
    private void setSelectedSong(Song song)
    {
        selectedSong = song;
    }

    /**
     * Sets the selected Playlist in MyTunes.
     *
     * @param playlist - Playlist to set as selected.
     */
    private void setSelectedPlaylist(Playlist playlist)
    {
        selectedPlaylist = playlist;
    }

    /**
     * Gets the selected playlist.
     *
     * @return the selected playlist.
     */
    public Playlist getSelectedPlaylist()
    {
        return selectedPlaylist;
    }

    /**
     * Gets the selected song.
     *
     * @return the selected song.
     */
    public Song getSelectedSong()
    {
        return selectedSong;
    }

    /**
     * Sets the selected playlist in the model out from the given index from the
     * playlist manager.
     *
     * @param index - Index of the playlist in the list.
     */
    public void onSelectedPlaylist(int index)
    {

        setSelectedPlaylist(playlistManager.get(index));
    }

    /**
     * Selects a song in the songs manager, and set it as the selected song in
     * the model.
     *
     * @param index - Index of the song in the list.
     */
    public void onSelectedSong(int index)
    {

        setSelectedSong(songsManager.get(index));
    }

    /**
     * Adds the given song to MyTunes, it adds the given song to the
     * songManager.
     *
     * @param song - Song to add MyTunes.
     */
    public void newSong(Song song)
    {
        songsManager.add(song);
        stop();
    }

    /**
     * This method will edit the given song in the model, by updating the song
     * in the playlits and the songs list.
     *
     * @param song - Song to edit in MyTunes.
     */
    public void editSong(Song song)
    {

        //Update the songs list with the new information.
        songsManager.update(song);

        //Update all playlist with the new song information.
        for (Playlist playlist : playlistManager.getAllPlaylists())
        {
            playlist.updateSong(song);
        }

        stop();
    }

    /**
     * Removes the given song from MyTunes, it removes the song from both the
     * playlits and the songs list.
     *
     * @param song - Song to remove from MyTunes.
     * @param deleteFile - True or false the files should be deleted.
     */
    public void removeSong(Song song, boolean deleteFile)
    {

        songsManager.remove(song);

        //Loop through each playlist.
        for (Playlist playlist : playlistManager.getAllPlaylists())
        {
            playlist.removeAllSong(song);
        }

        //Delete file ?
        if (deleteFile)
        {
            fileManager.delete(song.getPath());
        }

        stop();
    }

    /**
     * This method gets all the songs from the songs list.
     *
     * @return a list of all songs.
     */
    public List<Song> getAllSongs()
    {
        return songsManager.getAllSongs();
    }

    /**
     * This method creates new playlist in MyTunes.
     *
     * @param name - Name of the playlist.
     */
    public void newPlaylist(String name)
    {
        //Create a new empty playlist.
        Playlist playlist = new Playlist(name, new ArrayList<Song>());
        playlistManager.addPlaylist(playlist);
        stop();
    }

    /**
     * Deletes the given playlist in the playlist manager out from the given
     * index of the playlist in the manager.
     *
     * @param index - Index of the playlist to remove.
     */
    public void deletePlaylist(int index)
    {
        playlistManager.removePlaylist(playlistManager.get(index));
        stop();
    }

    /**
     * Renames a playlist in the playlist manager and MyTunes.
     *
     * @param name - New name.
     */
    public void renamePlaylist(String name)
    {
        playlistManager.rename(selectedPlaylist, name);
        stop();
    }

    /**
     * Gets all the playlist in MyTunes.
     *
     * @return a list of all playlists.
     */
    public List<Playlist> getAllPlaylists()
    {
        return playlistManager.getAllPlaylists();
    }

    /**
     * Moves the song up in the selected playlist.
     *
     * @param index - Index of song to move up.
     */
    public void moveUp(int index)
    {
        selectedPlaylist.moveUp(index);
        stop();
    }

    /**
     * Moves the song down in the selected playlist.
     *
     * @param index - Index of song to move down.
     */
    public void moveDown(int index)
    {
        selectedPlaylist.moveDown(index);
        stop();
    }

    /**
     * Moves the selected song from the songsmanager to the selected playlist.
     */
    public void selectedPlaylistAddSong()
    {
        selectedPlaylist.addSong(selectedSong);
        stop();
    }

    /**
     * Removes a song from the selected playlist.
     *
     * @param index - Index of the song to remove.
     */
    public void selectedPlaylistRemoveSong(int index)
    {
        selectedPlaylist.removeSong(index);
        stop();
    }

    /**
     * Begins playing music, if the music is paused it will resume the music.
     */
    public void play() throws MyTunesException
    {

        try
        {
            if (soundManager.getState() == SoundManager.State.PLAYING)
            {
                soundManager.pause();
            }
            else
            {
                soundManager.play();
            }
        }
        catch (SoundManagerException ex)
        {

            throw new MyTunesException(ex.getMessage());
        }
    }

    /**
     * Skips to the next song in the current playing playlist.
     *
     * @throws MyTunesException
     */
    public void next() throws MyTunesException
    {

        try
        {

            soundManager.next();

        }
        catch (SoundManagerException ex)
        {

            //Throw error.
            throw new MyTunesException(ex.getMessage());
        }
    }

    /**
     * Skips to the next song in the current playing playlist.
     *
     * @throws MyTunesException
     */
    public void previous() throws MyTunesException
    {

        try
        {

            soundManager.previous();

        }
        catch (SoundManagerException ex)
        {

            //Throw error.
            throw new MyTunesException(ex.getMessage());
        }
    }

    /**
     * Changes the volume of the music
     *
     * @param volume - Volume to change.
     * @throws MyTunesException
     */
    public void volume(int volume) throws MyTunesException
    {

        try
        {

            soundManager.setVolume(volume);

        }
        catch (SoundManagerException ex)
        {

            //Throw error.
            throw new MyTunesException(ex.getMessage());
        }
    }

    /**
     * Begins playing the song in the selected playlist at the given index.
     *
     * @param index
     * @throws MyTunesException
     */
    public void playSongInPlaylist(int index) throws MyTunesException
    {
        playSong(index, selectedPlaylist);
    }

    /**
     * This method should be called by the ui when the user wants play some
     * music in the songs list.
     *
     * @param index
     * @throws MyTunesException
     */
    public void playSongInSongs(int index) throws MyTunesException
    {

        //Create temporary playlist of the songs list.
        Playlist playlist = new Playlist("", songsManager.getAllSongs());

        playSong(index, playlist);
    }

    /**
     * This method plays a song in the given playlist at the speficied index.
     *
     * @param index - Index of songs to play.
     * @throws SoundManagerException
     */
    private void playSong(int index, Playlist playlist) throws MyTunesException
    {

        try
        {

            //Set playlist.
            soundManager.setPlaylist(playlist);

            //Play spefici song in playlist.
            soundManager.next(index);

        }
        catch (SoundManagerException ex)
        {

            //Throw error from sound manager.
            throw new MyTunesException(ex.getMessage());
        }
    }

    /**
     * Stops the music.
     */
    public void stop()
    {

        //Stops the music from playing
        soundManager.stop();
    }

    /**
     * Adds the given SoundEventListener to the SoundManager to allow the ui to
     * be notified about changes in the sound playing.
     *
     * @param sel - SoundEventListener to attach to soundManager.
     */
    public void addSoundEventListener(ISoundEventListener sel)
    {

        try
        {

            //Add listener to sound manager.
            soundManager.addListener(sel);

        }
        catch (SoundManagerException ex)
        {

            //Throw error.
            throw new MyTunesException(ex.getMessage());
        }
    }

    /**
     * Begins searching through the songs list and returns the result.
     *
     * @param query - Search query.
     * @return List of song from search.
     */
    public List<Song> searchSong(String query)
    {
        
        //Search type.
        ISearchType searchType = new SearchTypeTitle();

        //Search matcher.
        ISearchMatch searchMatcher = new SearchMatchContains(query);

        //Search.
        List<Song> list1 = PlaylistSearch.getInstance().search(new Playlist("", songsManager.getAllSongs()), searchType, searchMatcher);
        
        //Search by start by artist name.
        searchType = new SearchTypeArtistName();

        List<Song> list2 = PlaylistSearch.getInstance().search(new Playlist("", songsManager.getAllSongs()), searchType, searchMatcher);

        return PlaylistSearch.getInstance().mixList(list1, list2);
    }

    /**
     * Gets all the genres mytunes knows about.
     *
     * @return all genres.
     */
    public List<String> getAllGenres()
    {
        return genreManager.load();
    }

    /**
     * Adds the given genre to mytunes.
     *
     * @param genre - Genre to add.
     * @throws MyTunesException
     */
    public void addGenre(String genre) throws MyTunesException
    {
        genreManager.add(genre);
    }

    /**
     * Sets playing playlist to the given list of songs.
     *
     * @param songs - List of songs to play.
     * @param index - Index of the song play.
     */
    public void setPlayingPlaylist(List<Song> songs, int index)
    {

        try
        {

            //Play the given list of songs.
            soundManager.setPlaylist(new Playlist("", songs));

            //Start playing number one.
            soundManager.next(index);

        }
        catch (SoundManagerException ex)
        {

            //Throw error.
            throw new MyTunesException("Failed to set new playlist: " + ex.getMessage());
        }
    }
    
    /**
     * Sets the list of the selected playlist.
     * @param songs 
     */
    public void setPlaylistList(List<Song> songs)
    {
        selectedPlaylist.setSonglist(songs);
    }
    
    /**
     * loads the hole music folder and adds them to the songmanager
     */
    public void loadFromMusic(){
        try{
            List<Boolean> check = new ArrayList<>();
      
            for(Song newSong: loadFromMusicManager.Load()){
                //Look if the songManager is empty and if it is it will add a song
                if(songsManager.getAllSongs().isEmpty()){
                    songsManager.add(newSong);
                }else{
                    //look the songManagers list if there is any of the same songs
                    //now it looks at the title and the artist
                    for(Song song: songsManager.getAllSongs()){
                        check.add(song.getTitle().equals(newSong.getTitle()) && song.getArtistName().equals(newSong.getArtistName()) || song.getPath().equalsIgnoreCase(newSong.getPath()));
                    }
                    //if the check list has any true then it wont add them 
                    if(!check.contains(true)){
                        songsManager.add(newSong);
                    }
                    check = new ArrayList<>();
                }
            }
            songsManager.save();
        }catch(MyTunesException ex){
            throw new MyTunesException("ERROR - "+ ex.getMessage());
        }


    }

    /**
     * takes the filepath and look for the song and returns the information from
     * the key
     *
     * @param filePath
     * @param key
     * @return
     */
    public String loadASong(String filePath, String key)
    {
        return loadFromMusicManager.loadASong(filePath, key);
    }

    /**
     * Gets the requested setting from the prefence system.
     * @param command - Command name setting.
     * @return the setting value.
     */
    public String getSetting(String command) {
        if (!preferenceManager.getAllSettings().isEmpty()) {
            return preferenceManager.getSettingViaIndex(command);
        }else {
            return null;
        }
    }
    
    /**
     * Save settings.
     */
    public void saveSettings() {
        
        try {
            
            preferenceManager.saveSettings();
        
        } catch (IOException ex) {
            
            //
        }
    }
    
    /**
     * sets settings
     * @param key
     * @param setting
     */
    
    public void setSetting(String key, String setting){
        preferenceManager.setSettingForIndex(key, setting);
    }
    
    /**
     * This method should be called by the ui, when the user is closing MyTunes.
     */
    public void close()
    {
        try
        {
            songsManager.close();
            playlistManager.close();
            soundManager.close();
            preferenceManager.close();
        }
        catch (IOException ex)
        {
            throw new MyTunesException(ex.getMessage());
        }
    }
}
