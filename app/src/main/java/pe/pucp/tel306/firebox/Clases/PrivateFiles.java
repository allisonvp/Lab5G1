package pe.pucp.tel306.firebox.Clases;

public class PrivateFiles extends Files {
    private String password;

    public PrivateFiles(long size, String path, String password) {
        super(size, path);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
