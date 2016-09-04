package listener.file;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * Created by tomag on 02.09.2016.
 */
public class EventedFile implements Serializable, Comparable<EventedFile> {
    private String file;
    private String event = null;
    static final long serialVersionUID = 1L;

    public EventedFile(Path p, String e) {
        this.file = p.toAbsolutePath().toString();
        this.event = e;
    }

    public EventedFile(Path p) {
        this.file = p.toAbsolutePath().toString();
    }

    public String getFile() {
        return file;
    }

    public String getEvent() {
        return event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventedFile that = (EventedFile) o;

        return file.equals(that.file);

    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public int compareTo(EventedFile o) {
        return file.equals(o.file) ? 0 : 1;
    }
}