package pe.pucp.tel306.firebox.Clases;

import java.io.Serializable;

public class Files implements Serializable {
    private String name="";
    private long size=0;
    private String path="";
    private String date="";
    public Files(){

    }

    public Files(String name, long size, String path, String date) {
        this.name = name;
        this.size = size;
        this.path = path;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
