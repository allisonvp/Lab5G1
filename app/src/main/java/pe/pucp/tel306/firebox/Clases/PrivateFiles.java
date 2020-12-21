package pe.pucp.tel306.firebox.Clases;

public class PrivateFiles extends Files {
    private String password;

    public PrivateFiles(String name, long size, String path, String date, String password) {
        super(name, size, path, date);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
