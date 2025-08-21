package app.entre_jugones;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GlobalDAO {

    // ----------------- CREADORES -----------------

    public static List<ModelCreador> obtenerCreadores() {
        List<ModelCreador> lista = new ArrayList<>();
        String sql = "SELECT id, alias, canal_twitch FROM creadores ORDER BY alias";

        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (obtenerCreadores)");
                return lista;
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    ModelCreador c = new ModelCreador(
                            rs.getInt("id"),
                            rs.getString("alias"),
                            rs.getString("canal_twitch")
                    );
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener creadores: " + e.getMessage());
        }
        return lista;
    }

    public static boolean insertarCreador(ModelCreador creador) {
        String sql = "INSERT INTO creadores (alias, canal_twitch) VALUES (?, ?)";
        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (insertarCreador)");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, creador.getAlias());
                ps.setString(2, creador.getCanalTwitch());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar creador: " + e.getMessage());
            return false;
        }
    }

    public static boolean actualizarCreador(ModelCreador creador) {
        String sql = "UPDATE creadores SET alias = ?, canal_twitch = ? WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (actualizarCreador)");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, creador.getAlias());
                ps.setString(2, creador.getCanalTwitch());
                ps.setInt(3, creador.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar creador: " + e.getMessage());
            return false;
        }
    }

    public static boolean eliminarCreador(int id) {
        String sql = "DELETE FROM creadores WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (eliminarCreador)");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar creador: " + e.getMessage());
            return false;
        }
    }

    // ----------------- EVENTOS -----------------

    public static List<ModelEvento> listarTodosLosEventos() {
        List<ModelEvento> lista = new ArrayList<>();
        String sql = "SELECT * FROM eventos ORDER BY fecha_inicio DESC";

        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (listarTodosLosEventos)");
                return lista;
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    ModelEvento evento = new ModelEvento(
                            rs.getInt("id"),
                            rs.getInt("id_creador"),
                            rs.getString("nombre"),
                            rs.getString("capitulo"),
                            rs.getString("descripcion"),
                            rs.getDate("fecha_inicio").toLocalDate(),
                            rs.getDate("fecha_fin").toLocalDate(),
                            rs.getTime("hora_inicio") != null ? rs.getTime("hora_inicio").toLocalTime() : null,
                            rs.getTime("hora_fin") != null ? rs.getTime("hora_fin").toLocalTime() : null
                    );
                    lista.add(evento);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar eventos: " + e.getMessage());
        }
        return lista;
    }

    public static List<ModelEvento> obtenerEventosPorCreador(int idCreador) {
        List<ModelEvento> lista = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE id_creador = ? ORDER BY fecha_inicio DESC";

        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (obtenerEventosPorCreador)");
                return lista;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idCreador);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ModelEvento evento = new ModelEvento(
                            rs.getInt("id"),
                            rs.getInt("id_creador"),
                            rs.getString("nombre"),
                            rs.getString("capitulo"),
                            rs.getString("descripcion"),
                            rs.getDate("fecha_inicio").toLocalDate(),
                            rs.getDate("fecha_fin").toLocalDate(),
                            rs.getTime("hora_inicio") != null ? rs.getTime("hora_inicio").toLocalTime() : null,
                            rs.getTime("hora_fin") != null ? rs.getTime("hora_fin").toLocalTime() : null
                    );
                    lista.add(evento);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener eventos por creador: " + e.getMessage());
        }
        return lista;
    }

    public static boolean insertarEvento(ModelEvento evento) {
        String sql = "INSERT INTO eventos (id_creador, nombre, capitulo, descripcion, fecha_inicio, fecha_fin, hora_inicio, hora_fin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (insertarEvento)");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, evento.getIdCreador());
                ps.setString(2, evento.getNombre());
                ps.setString(3, evento.getCapitulo());
                ps.setString(4, evento.getDescripcion());
                ps.setDate(5, Date.valueOf(evento.getFechaInicio()));
                ps.setDate(6, Date.valueOf(evento.getFechaFin()));
                ps.setTime(7, evento.getHoraInicio() != null ? Time.valueOf(evento.getHoraInicio()) : null);
                ps.setTime(8, evento.getHoraFin() != null ? Time.valueOf(evento.getHoraFin()) : null);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar evento: " + e.getMessage());
            return false;
        }
    }

    public static boolean actualizarEvento(ModelEvento evento) {
        String sql = "UPDATE eventos SET id_creador = ?, nombre = ?, capitulo = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ?, hora_inicio = ?, hora_fin = ? WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (actualizarEvento)");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, evento.getIdCreador());
                ps.setString(2, evento.getNombre());
                ps.setString(3, evento.getCapitulo());
                ps.setString(4, evento.getDescripcion());
                ps.setDate(5, Date.valueOf(evento.getFechaInicio()));
                ps.setDate(6, Date.valueOf(evento.getFechaFin()));
                ps.setTime(7, evento.getHoraInicio() != null ? Time.valueOf(evento.getHoraInicio()) : null);
                ps.setTime(8, evento.getHoraFin() != null ? Time.valueOf(evento.getHoraFin()) : null);
                ps.setInt(9, evento.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar evento: " + e.getMessage());
            return false;
        }
    }

    public static boolean eliminarEvento(int idEvento) {
        String sql = "DELETE FROM eventos WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (eliminarEvento)");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEvento);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar evento: " + e.getMessage());
            return false;
        }
    }

    // ----------------- COLABORADORES -----------------

    public static List<ModelColaborador> obtenerTodosLosColaboradores() {
        List<ModelColaborador> lista = new ArrayList<>();
        String sql = "SELECT id, alias, canal_twitch FROM colaboradores ORDER BY alias";

        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (obtenerTodosLosColaboradores)");
                return lista;
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    ModelColaborador colab = new ModelColaborador(
                            rs.getInt("id"),
                            rs.getString("alias"),
                            rs.getString("canal_twitch")
                    );
                    lista.add(colab);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener colaboradores: " + e.getMessage());
        }
        return lista;
    }

    public static List<ModelColaborador> obtenerColaboradoresPorEvento(int idEvento) {
        List<ModelColaborador> lista = new ArrayList<>();
        String sql = "SELECT c.id, c.alias, c.canal_twitch FROM colaboradores c JOIN evento_colaborador ec ON c.id = ec.id_colaborador WHERE ec.id_evento = ?";

        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (obtenerColaboradoresPorEvento)");
                return lista;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEvento);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ModelColaborador colab = new ModelColaborador(
                            rs.getInt("id"),
                            rs.getString("alias"),
                            rs.getString("canal_twitch")
                    );
                    lista.add(colab);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener colaboradores por evento: " + e.getMessage());
        }
        return lista;
    }

    public static boolean insertarColaborador(ModelColaborador colab) {
        String sql = "INSERT INTO colaboradores (alias, canal_twitch) VALUES (?, ?)";
        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (insertarColaborador)");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, colab.getAlias());
                ps.setString(2, colab.getCanalTwitch());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar colaborador: " + e.getMessage());
            return false;
        }
    }

    public static boolean actualizarColaborador(ModelColaborador colab) {
        String sql = "UPDATE colaboradores SET alias = ?, canal_twitch = ? WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (actualizarColaborador)");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, colab.getAlias());
                ps.setString(2, colab.getCanalTwitch());
                ps.setInt(3, colab.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar colaborador: " + e.getMessage());
            return false;
        }
    }

    public static boolean eliminarColaborador(int id) {
        String sql = "DELETE FROM colaboradores WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (eliminarColaborador)");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar colaborador: " + e.getMessage());
            return false;
        }
    }

    // ----------------- RELACIÓN EVENTO - COLABORADOR -----------------

    public static boolean asignarColaboradorAEvento(int idColaborador, int idEvento) {
        String sql = "INSERT INTO evento_colaborador (id_evento, id_colaborador) VALUES (?, ?)";
        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (asignarColaboradorAEvento)");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEvento);
                ps.setInt(2, idColaborador);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al asignar colaborador a evento: " + e.getMessage());
            return false;
        }
    }

    public static boolean eliminarAsignacionColaborador(int idEvento, int idColaborador) {
        String sql = "DELETE FROM evento_colaborador WHERE id_evento = ? AND id_colaborador = ?";
        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null) {
                System.err.println("[ADVERTENCIA] BD no disponible (eliminarAsignacionColaborador)");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEvento);
                ps.setInt(2, idColaborador);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar asignación: " + e.getMessage());
            return false;
        }
    }
}
