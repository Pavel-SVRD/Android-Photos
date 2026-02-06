package photos.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Represents a photo with a file path, caption, date, and associated tags.
 * 
 * <p>Provides methods to manage tags, captions, and retrieve photo metadata.</p>
 * 
 * @author Jess
 * @author Pavel
 */
public class Photo implements Serializable {
    
   /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /** File path of the photo. */
    private String filePath;

    /** Caption for the photo. */
    private String caption;

    /** Date the photo was taken or last modified. */
    private Calendar date;

    /** List of tags associated with the photo. */
    private List<Tag> tags;
    
    /**
     * Constructs a new Photo object with the given file path.
     * 
     * <p>The caption is initialized as empty, and the date is set to the file's
     * last modification time if it exists.</p>
     * 
     * @param filePath the file path to the photo
     */
    public Photo(String filePath) {
        this.filePath = filePath;
        this.caption = "";
        this.tags = new ArrayList<>();
        
        //set date from file's last modification time
        File file = new File(filePath);
        this.date = Calendar.getInstance();
        if (file.exists()) {
            this.date.setTimeInMillis(file.lastModified());
        }
        this.date.set(Calendar.MILLISECOND, 0);
    }
    
    /** 
     * Returns the file path of the photo. 
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }
    
    /** 
     * Returns the caption of the photo. 
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }
    
    /** 
     * Sets the caption for the photo. 
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    /** 
     * Returns the date the photo was taken or last modified. 
     * @return the date as a Calendar object
     */
    public Calendar getDate() {
        return date;
    }
    
    /** 
     * Returns the date as a formatted string (MM/DD/YYYY HH:MM:SS). 
     * @return the formatted date string
     */
    public String getDateString() {
        return String.format("%02d/%02d/%04d %02d:%02d:%02d",
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.YEAR),
                date.get(Calendar.HOUR_OF_DAY),
                date.get(Calendar.MINUTE),
                date.get(Calendar.SECOND));
    }
    
    /** 
     * Returns the list of tags associated with this photo. 
     * @return the list of tags
     */
    public List<Tag> getTags() {
        return tags;
    }
    
     /**
     * Adds a tag to the photo if it does not already exist.
     * 
     * @param tag the Tag to add
     * @return true if the tag was added, false if it already exists
     */
    public boolean addTag(Tag tag) {
        if (hasTag(tag)) {
            return false;
        }
        tags.add(tag);
        return true;
    }
    
    /**
     * Removes a tag from the photo.
     * 
     * @param tag the Tag to remove
     * @return true if the tag was removed, false otherwise
     */
    public boolean removeTag(Tag tag) {
        return tags.remove(tag);
    }
    
    /**
     * Checks if the photo has the specified tag.
     * 
     * @param tag the Tag to check
     * @return true if the photo contains the tag, false otherwise
     */
    public boolean hasTag(Tag tag) {
        return tags.stream().anyMatch(t -> 
            t.getName().equals(tag.getName()) && t.getValue().equals(tag.getValue()));
    }
    
    /**
     * Returns a comma-separated string of all tags associated with the photo.
     * 
     * @return formatted string of tags, or "No tags" if none exist
     */
    public String getTagsString() {
        if (tags.isEmpty()) {
            return "No tags";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(tags.get(i).toString());
        }
        return sb.toString();
    }
    
    /**
     * Checks if the photo file exists on disk.
     * 
     * @return true if the file exists, false otherwise
     */
    public boolean fileExists() {
        return new File(filePath).exists();
    }
    
    /**
     * Returns the file name of the photo.
     * 
     * @return the file name
     */
    public String getFileName() {
        return new File(filePath).getName();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Photo photo = (Photo) obj;
        return filePath.equals(photo.filePath);
    }
    
    @Override
    public int hashCode() {
        return filePath.hashCode();
    }
    
    @Override
    public String toString() {
        return getFileName();
    }
}
