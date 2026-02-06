package photos.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DataManager handles saving and loading albums and photos.
 * All user-management functionality has been removed.
 * 
 * This class now acts purely as an album/photo persistence layer.
 * @author Jess
 * @author Pavel
 */
public class DataManager {

    /** Directory where album data is stored */
    private static final String DATA_DIR = "data/albums/";

    /** Directory containing stock photos */
    private static final String STOCK_DIR = "data/";

    /** Singleton instance */
    private static DataManager instance;

    /** All albums currently loaded in memory */
    private List<Album> albums;

    /** Private constructor for singleton */
    private DataManager() {
        ensureDirectoryExists(DATA_DIR);
        albums = loadAllAlbums();
    }

    /** Get singleton instance */
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }


    /** Get a copy of all albums */
    public List<Album> getAlbums() {
        return new ArrayList<>(albums);
    }

    /** Add an album */
    public void addAlbum(Album album) {
        albums.add(album);
        saveAlbum(album);
    }

    /** Remove an album */
    public void deleteAlbum(Album album) {
        albums.remove(album);
        File file = new File(DATA_DIR + album.getName() + ".dat");
        if (file.exists()) file.delete();
    }

    /** Create a stock album if not present */
    public void initializeStock() {
        if (getAlbumByName("stock") != null) return;

        Album stock = new Album("stock");

        String[] stockPhotos = {
            "cherrystock.jpg",
            "desertstock.jpg",
            "Meadowstock.jpg",
            "Nightstock.png",
            "scarystock.jpg",
            "Snowystock.jpg"
        };

        for (String name : stockPhotos) {
            File file = new File(STOCK_DIR + name);
            if (file.exists()) {
                Photo p = new Photo(file.getPath());
                p.setCaption("Stock: " + name);
                stock.addPhoto(p);
            }
        }

        addAlbum(stock);
    }

    /** Helper: find an album by name */
    public Album getAlbumByName(String name) {
        for (Album a : albums) {
            if (a.getName().equals(name)) return a;
        }
        return null;
    }

    /** Save a single album */
    public void saveAlbum(Album album) {
        ensureDirectoryExists(DATA_DIR);
        File file = new File(DATA_DIR + album.getName() + ".dat");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(album);
        } catch (IOException e) {
            System.err.println("ERROR SAVING ALBUM: " + e.getMessage());
        }
    }

    /** Load all album files into memory */
    private List<Album> loadAllAlbums() {
        List<Album> list = new ArrayList<>();
        File[] files = new File(DATA_DIR).listFiles((d, n) -> n.endsWith(".dat"));

        if (files == null) return list;

        for (File file : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Album a = (Album) ois.readObject();
                list.add(a);
            } catch (IOException | ClassNotFoundException ignored) {}
        }

        return list;
    }

    

    /** Save all albums */
    public void saveAll() {
        for (Album a : albums) {
            saveAlbum(a);
        }
    }



    /** Ensure that a directory exists before writing to it */
    private void ensureDirectoryExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
    }
}
