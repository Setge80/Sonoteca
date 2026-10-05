package es.iesejemplo.sonoteca.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import es.iesejemplo.sonoteca.modelo.Biblioteca;
import es.iesejemplo.sonoteca.modelo.Cancion;

import java.lang.reflect.Type;
import java.util.List;

/**
 * UNIDAD 1 - EJERCICIO 3 (canciones sueltas) y UNIDAD 2 - EJERCICIO 4 (biblioteca completa).
 * <p>
 * Utilidades de conversión entre los objetos del modelo y JSON, usando Gson.
 */
public class JsonUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonUtils() {
        // clase de utilidades: no se instancia
    }

    // ----- Unidad 1: una canción suelta -----

    public static String exportarCancion(Cancion cancion) {
        return GSON.toJson(cancion);
    }

    public static Cancion importarCancion(String json) {
        return GSON.fromJson(json, Cancion.class);
    }

    public static String exportarLista(List<Cancion> canciones) {
        return GSON.toJson(canciones);
    }

    public static List<Cancion> importarLista(String json) {
        Type tipoLista = new TypeToken<List<Cancion>>() {}.getType();
        return GSON.fromJson(json, tipoLista);
    }

    // ----- Unidad 2: la biblioteca completa -----

    /**
     * Escribe la biblioteca completa en un fichero de texto en formato JSON legible.
     */
    public static void exportarBibliotecaJson(Biblioteca biblioteca, String rutaFichero) {
        // TODO: implementar (usar GSON.toJson(...) y un PrintWriter/FileWriter) (unidad 2, ejercicio 4).
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 4)");
    }

    /**
     * Lee un fichero JSON con una biblioteca completa y la reconstruye.
     */
    public static Biblioteca importarBibliotecaJson(String rutaFichero) {
        // TODO: implementar (leer el fichero completo y usar GSON.fromJson(...)) (unidad 2, ejercicio 4).
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 4)");
    }
}
