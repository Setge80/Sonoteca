package es.iesejemplo.sonoteca.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import es.iesejemplo.sonoteca.modelo.Biblioteca;
import es.iesejemplo.sonoteca.modelo.Cancion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

//Unidad 2. Ejercicio 4, para que funcionen las fechas.
class LocalDateAdapter extends TypeAdapter<LocalDate> {
    @Override
    public void write(JsonWriter out, LocalDate valor) throws IOException {
        if (valor == null) {
            out.nullValue();
        } else {
            out.value(valor.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "2024-05-17"
        }
    }
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return LocalDate.parse(in.nextString());
    }
}


/**
 * UNIDAD 1 - EJERCICIO 3 (canciones sueltas) y UNIDAD 2 - EJERCICIO 4 (biblioteca completa).
 * <p>
 * Utilidades de conversión entre los objetos del modelo y JSON, usando Gson.
 */
public class JsonUtils {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

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
        Type tipoLista = new TypeToken<List<Cancion>>() {
        }.getType();
        return GSON.fromJson(json, tipoLista);
    }

    // ----- Unidad 2: la biblioteca completa -----

    /**
     * Escribe la biblioteca completa en un fichero de texto en formato JSON legible.
     */
    public static void exportarBibliotecaJson(Biblioteca biblioteca, String rutaFichero) throws IOException {
        Path ruta = Path.of(rutaFichero).toAbsolutePath();
        Path padre = ruta.getParent();
        if (padre != null) {
            Files.createDirectories(padre);
        }
        try (BufferedWriter escritor = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            GSON.toJson(biblioteca, escritor);
        }
    }

    /**
     * Lee un fichero JSON con una biblioteca completa y la reconstruye.
     */
    public static Biblioteca importarBibliotecaJson(String rutaFichero) throws IOException {
        Path ruta = Path.of(rutaFichero).toAbsolutePath();
        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            Biblioteca b = GSON.fromJson(lector, Biblioteca.class);
            return b != null ? b : new Biblioteca();
        }
    }
}
