package photos.model;
import java.io.Serializable;

/**
 * Represents a tag associated with a photo.
 * 
 * <p>A tag consists of a name (type) and a value, and can be used to categorize or describe photos.</p>
 * 
 * @author Jess
 * @author Pavel
 */
public class Tag implements Serializable {
    
     /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /** The name or type of the tag. */
    private String name;

    /** The value of the tag. */
    private String value;
    
    /**
     * Constructs a new Tag with the specified name and value.
     * 
     * @param name  the type of the tag
     * @param value the value associated with the tag
     */
    public Tag(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    /**
     * Returns the name of the tag.
     * 
     * @return the tag name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the tag.
     * 
     * @param name the new tag name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns the value of the tag.
     * 
     * @return the tag value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Sets the value of the tag.
     * 
     * @param value the new tag value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Checks if this tag is equal to another object.
     * 
     * @param obj the object to compare
     * @return true if the object is a Tag with the same name and value, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag tag = (Tag) obj;
        return name.equals(tag.name) && value.equals(tag.value);
    }

    /**
     * Returns a hash code value for the tag.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return 31 * name.hashCode() + value.hashCode();
    }

    /**
     * Returns a string representation of the tag in the format "name=value".
     * 
     * @return the string representation of the tag
     */
    @Override
    public String toString() {
        return name + "=" + value;
    }
}
