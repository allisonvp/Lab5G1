package pe.pucp.tel306.firebox.Clases;

public class Files {
    private long size;
    private String path;

    public Files(long size, String path) {
        this.size = size;
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
