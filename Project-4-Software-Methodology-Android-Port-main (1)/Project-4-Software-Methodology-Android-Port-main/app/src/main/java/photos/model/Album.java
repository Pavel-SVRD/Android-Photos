package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Represents a photo album that contains a collection of photos.
 * Provides methods to add, remove, and query photos, as well as
 * retrieve date range information for the album.
 * 
 * 
 * @author Jess
 * @author Pavel
 */
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private List<Photo> photos;

    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }
    
    /**
     * Returns the name of the album.
     * 
     * @return the album name
     */
    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public int getPhotoCount() {
        return photos.size();
    }
    
    /**
     * Adds a photo to the album if it does not already exist.
     * 
     * @param photo the photo to add
     * @return true if the photo was added, false if it already exists
     */
    public boolean addPhoto(Photo photo) {
        if (photo == null) return false;
        if (hasPhoto(photo)) return false;

        photos.add(photo);
        return true;
    }
    
     /**
     * Removes a photo from the album.
     * 
     * @param photo the photo to remove
     * @return true if the photo was removed, false if it did not exist
     */
     public boolean removePhoto(Photo photo) {
        if (photo == null) return false;
        return photos.remove(photo);
    }

    public boolean hasPhoto(Photo photo) {
        if (photo == null) return false;
        return photos.stream()
                .anyMatch(p -> p.getFilePath().equals(photo.getFilePath()));
    }
    
     /**
     * Returns the earliest date among the photos in the album.
     * 
     * @return the earliest photo date, or null if the album is empty
     */
    public Calendar getEarliestDate() {
        if (photos.isEmpty()) return null;

        Calendar earliest = photos.get(0).getDate();

        for (Photo p : photos) {
            if (p.getDate().before(earliest)) {
                earliest = p.getDate();
            }
        }
        return earliest;
    }
    
    /**
     * Returns the latest date among the photos in the album.
     * 
     * @return the latest photo date, or null if the album is empty
     */
     public Calendar getLatestDate() {
        if (photos.isEmpty()) return null;

        Calendar latest = photos.get(0).getDate();

        for (Photo p : photos) {
            if (p.getDate().after(latest)) {
                latest = p.getDate();
            }
        }
        return latest;
    }
    
    /**
     * Returns a string representing the date range of the photos in the album.
     * If the album is empty, returns "No photos". If all photos have the same
     * date, returns that single date.
     * 
     * @return a string representing the date range
     */
    public String getDateRangeString() {
        if (photos.isEmpty()) {
            return "No photos";
        }

        Calendar earliest = getEarliestDate();
        Calendar latest = getLatestDate();

        if (earliest == null || latest == null) {
            return "No dates";
        }

        String earliestStr = formatDate(earliest);
        String latestStr = formatDate(latest);

        return earliestStr.equals(latestStr)
                ? earliestStr
                : earliestStr + " - " + latestStr;
    }
    private String formatDate(Calendar date) {
        return String.format(
                "%02d/%02d/%04d",
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.YEAR)
        );
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Album album = (Album) obj;
        return name.equals(album.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
