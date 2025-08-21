package app.entre_jugones;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa un evento creado por un creador, con posible asociación a
 * colaboradores.
 */
public class ModelEvento {

    private int id;
    private int idCreador;
    private String nombre;
    private String capitulo;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalTime horaInicio;   // Nueva columna
    private LocalTime horaFin;      // Nueva columna

    // Constructores
    public ModelEvento() {
    }

    public ModelEvento(int id, int idCreador, String nombre, String capitulo, String descripcion,
            LocalDate fechaInicio, LocalDate fechaFin,
            LocalTime horaInicio, LocalTime horaFin) {
        this.id = id;
        this.idCreador = idCreador;
        this.nombre = nombre;
        this.capitulo = capitulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCreador() {
        return idCreador;
    }

    public void setIdCreador(int idCreador) {
        this.idCreador = idCreador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCapitulo() {
        return capitulo;
    }

    public void setCapitulo(String capitulo) {
        this.capitulo = capitulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    @Override
    public String toString() {
        return nombre != null ? nombre : "";
    }

}
