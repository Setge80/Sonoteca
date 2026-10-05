package es.iesejemplo.sonoteca.repositorio.ficheros;

import es.iesejemplo.sonoteca.modelo.*;
import es.iesejemplo.sonoteca.repositorio.RepositorioSonoTeca;

import java.io.File;
import java.util.*;

/**
 * UNIDAD 2 — Persistencia en ficheros.
 * <p>
 * Guarda y recupera la biblioteca completa de un fichero binario (serialización)
 * y sabe escanear una carpeta de MP3 para construir la biblioteca inicial a
 * partir de sus cabeceras ID3v1.
 * <p>
 * Complétala siguiendo los ejercicios de la práctica de la unidad 2. Todos los
 * métodos de {@link RepositorioSonoTeca} deben quedar implementados para que la
 * aplicación funcione con ella (cambia la línea correspondiente en {@code Main}).
 */
public class RepositorioFicheros implements RepositorioSonoTeca {

    private final String rutaFicheroBiblioteca;
    private Biblioteca biblioteca;
    private final List<ListaReproduccion> listas = new ArrayList<>();
    private final Map<Integer, List<Integer>> cancionesPorLista = new HashMap<>();

    /**
     * @param rutaFicheroBiblioteca ruta del fichero binario donde se guarda la biblioteca
     *                              (por ejemplo, "sonoteca.dat"). Si no existe todavía,
     *                              se debe partir de una biblioteca vacía.
     */
    public RepositorioFicheros(String rutaFicheroBiblioteca) {
        this.rutaFicheroBiblioteca = rutaFicheroBiblioteca;
        try {
            this.biblioteca = cargarBiblioteca(rutaFicheroBiblioteca);
        } catch (UnsupportedOperationException e) {
            // Todavía no has hecho el ejercicio 3 (cargarBiblioteca): empezamos con una
            // biblioteca vacía en memoria para poder probar ya los ejercicios 1 y 2.
            System.out.println("(cargarBiblioteca aún no implementado; empezando con una biblioteca vacía en memoria)");
            this.biblioteca = new Biblioteca();
        }
    }

    // ---------- Ejercicio 1: escanear un directorio en busca de MP3 ----------

    /**
     * @return la lista de ficheros .mp3 encontrados recursivamente a partir de rutaBase.
     *         Si rutaBase no existe o no es un directorio, devuelve una lista vacía.
     */
    public List<File> escanearDirectorio(String rutaBase) {
        // TODO: implementar (unidad 2, ejercicio 1).
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 1)");
    }

    // ---------- Ejercicio 2: leer la cabecera ID3v1 ----------

    /**
     * Lee los últimos 128 bytes de ficheroMp3 (cabecera ID3v1) y construye la Cancion
     * correspondiente (creando también, si hace falta, Artista/Album/Genero).
     * Si el fichero no tiene cabecera ID3v1 válida, devuelve null.
     */
    public Cancion leerCabeceraID3(File ficheroMp3) {
        // TODO: implementar (unidad 2, ejercicio 2).
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 2)");
    }

    // ---------- Ejercicio 3: serializar/deserializar la biblioteca ----------

    public void guardarBiblioteca(Biblioteca biblioteca, String rutaFichero) {
        // TODO: implementar con ObjectOutputStream (unidad 2, ejercicio 3).
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 3)");
    }

    public Biblioteca cargarBiblioteca(String rutaFichero) {
        // TODO: implementar con ObjectInputStream (unidad 2, ejercicio 3).
        // Si el fichero no existe todavía, devolver "new Biblioteca()" en lugar de fallar.
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 3)");
    }

    // ---------- Implementación de RepositorioSonoTeca (Ejercicio 0) ----------
    // A partir de aquí, apóyate en el objeto "biblioteca" cargado en memoria,
    // y recuerda volver a llamar a guardarBiblioteca(...) cada vez que la modifiques.

    @Override
    public List<Genero> listarGeneros() {
        return this.biblioteca.getGeneros();
    }

    @Override
    public List<Artista> listarArtistas() {
       return this.biblioteca.getArtistas();
    }

    @Override
    public List<Album> listarAlbumes() {
        return this.biblioteca.getAlbumes();
    }

    @Override
    public List<Cancion> listarCanciones() {
        return this.biblioteca.getCanciones();
    }

    @Override
    public List<Cancion> buscarCanciones(String texto) {
        List<Cancion> canciones = listarCanciones();
        List<Cancion> cancionesFiltradas=canciones.stream().filter(
                c-> c.getTitulo().toLowerCase().contains(texto.toLowerCase())).toList();
        return cancionesFiltradas;
    }

    //1.er patron: siguiente id disponible
    private int siguienteIdDisponibleCancion(List<Cancion> canciones) {
        return canciones.stream().mapToInt(Cancion::getId).max().orElse(0)+1;
    }
    private int siguienteIdDisponibleArtista(List<Artista> artistas) {
        return artistas.stream().mapToInt(Artista::getId).max().orElse(0)+1;
    }
    private int siguienteIdDisponibleGenero(List<Genero> generos) {
        return generos.stream().mapToInt(Genero::getId).max().orElse(0)+1;
    }



    //2.o patron: Obtener o crear
    private Artista obtenerCrearArtista(String nombre, String nacionalidad)
    {
        Artista artistaEncontrado = null;
        for(Artista a: biblioteca.getArtistas())
        {
            if (a.getNombre().equalsIgnoreCase(nombre))
            {
                artistaEncontrado =a;
            }
        }

        if(artistaEncontrado == null)
        {
            int nuevoId=siguienteIdDisponibleArtista(biblioteca.getArtistas());
            artistaEncontrado=new Artista(nuevoId,nombre,nacionalidad);
            biblioteca.getArtistas().add(artistaEncontrado);
        }

        return artistaEncontrado;
    }

    private Genero obtenerCrearGenero(String nombre)
    {
        Genero generoEncontrado = null;
        for(Genero g: biblioteca.getGeneros())
        {
            if (g.getNombre().equalsIgnoreCase(nombre))
            {
                generoEncontrado =g;
            }
        }

        if(generoEncontrado == null)
        {
            int nuevoId=siguienteIdDisponibleGenero(biblioteca.getGeneros());
            generoEncontrado=new Genero(nuevoId, nombre);
            biblioteca.getGeneros().add(generoEncontrado);
        }

        return generoEncontrado;
    }

    private Album obtenerCrearAlbum(String titulo, int anio, Artista artista, Genero genero)
    {
        Album albumEncontrado = null;
        for (Album a: biblioteca.getAlbumes())
        {
            if (a.getTitulo().equalsIgnoreCase(titulo))
                {
                    albumEncontrado =a;
                }
        }

        if (albumEncontrado == null)
        {
            albumEncontrado=new Album(titulo,anio,artista,genero);
            biblioteca.getAlbumes().add(albumEncontrado);
        }
        return albumEncontrado;
    }

    @Override
    public void altaCancion(Cancion cancion) {
        // TODO: implementar (unidad 2, ejercicio 0).
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 0)");
    }

    @Override
    public void actualizarCancion(Cancion cancion) {
        // TODO: implementar (unidad 2, ejercicio 0).
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 0)");
    }

    @Override
    public void eliminarCancion(int idCancion) {
        // TODO: implementar (unidad 2, ejercicio 0).
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 0)");
    }

    @Override
    public List<ListaReproduccion> listarListas() {
        // TODO: implementar (unidad 2, ejercicio 0).
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 0)");
    }

    @Override
    public void altaLista(ListaReproduccion lista) {
        // TODO: implementar (unidad 2, ejercicio 0).
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 0)");
    }

    @Override
    public void anyadirCancionALista(int idLista, int idCancion, int posicion) {
        // TODO: implementar (unidad 2, ejercicio 0).
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 0)");
    }

    @Override
    public List<Cancion> cancionesDeLista(int idLista) {
        // TODO: implementar (unidad 2, ejercicio 0).
        throw new UnsupportedOperationException("Funcionalidad no implementada (unidad 2, ejercicio 0)");
    }
}
