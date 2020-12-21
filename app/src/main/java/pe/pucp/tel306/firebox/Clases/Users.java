package pe.pucp.tel306.firebox.Clases;

public class Users {
    private String nombre;
    private String tipo;
    private long capacidad;
    private long capacidadusada;

    public Users(String nombre, String tipo, long capacidad, long capacidadusada) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.capacidadusada = capacidadusada;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public long getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(long capacidad) {
        this.capacidad = capacidad;
    }

    public long getCapacidadusada() {
        return capacidadusada;
    }

    public void setCapacidadusada(long capacidadusada) {
        this.capacidadusada = capacidadusada;
    }
}
