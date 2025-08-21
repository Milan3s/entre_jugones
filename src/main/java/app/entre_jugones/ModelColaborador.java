package app.entre_jugones;

/**
 * Representa un colaborador que puede estar asociado a uno o más eventos.
 */
public class ModelColaborador {

    private int id;
    private String alias;
    private String canalTwitch;

    // Constructores
    public ModelColaborador() {}

    public ModelColaborador(int id, String alias, String canalTwitch) {
        this.id = id;
        this.alias = alias;
        this.canalTwitch = canalTwitch;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCanalTwitch() {
        return canalTwitch;
    }

    public void setCanalTwitch(String canalTwitch) {
        this.canalTwitch = canalTwitch;
    }

    @Override
    public String toString() {
        return alias;
    }
}
