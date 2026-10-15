package es.iesejemplo.sonoteca.repositorio.ficheros;

import es.iesejemplo.sonoteca.modelo.*;
import es.iesejemplo.sonoteca.repositorio.RepositorioSonoTeca;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;

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
    private static final int TAM_CABECERA = 128;

    /**
     * @param rutaFicheroBiblioteca ruta del fichero binario donde se guarda la biblioteca
     *                              (por ejemplo, "sonoteca.dat"). Si no existe todavía,
     *                              se debe partir de una biblioteca vacía.
     */
    public RepositorioFicheros(String rutaFicheroBiblioteca) {
        this.rutaFicheroBiblioteca = rutaFicheroBiblioteca;
        try {
            this.biblioteca = cargarBiblioteca(rutaFicheroBiblioteca);
        } catch (UnsupportedOperationException | IOException | ClassNotFoundException e) {
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
        if (rutaBase == null || rutaBase.isBlank()) {
            return List.of();
        }

        Path ruta=Path.of(rutaBase);
        if (!Files.isDirectory(ruta)) {
            return List.of();
        }

        try (Stream<Path> rutas = Files.walk(ruta)) {
            List<File> mp3 = rutas
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".mp3"))
                    .sorted()
                    .map(Path::toFile)
                    .toList();
            return mp3;

        } catch (IOException e) {
            System.err.println("No se pudo recorrer el directorio: " + e.getMessage());
        }
        return List.of();
    }


    // ---------- Ejercicio 2: leer la cabecera ID3v1 ----------

    /**
     * Lee los últimos 128 bytes de ficheroMp3 (cabecera ID3v1) y construye la Cancion
     * correspondiente (creando también, si hace falta, Artista/Album/Genero).
     * Si el fichero no tiene cabecera ID3v1 válida, devuelve null.
     */
    public Cancion leerCabeceraID3(File ficheroMp3) {
       Path ruta=ficheroMp3.toPath();
       byte[] bloque=new byte[TAM_CABECERA];

       try {
           if (Files.size(ruta) < TAM_CABECERA) {
               return null;
           }

           try (RandomAccessFile raf = new RandomAccessFile(ficheroMp3, "r")) {
               raf.seek(raf.length() - TAM_CABECERA);
               raf.readFully(bloque);

               //Leemos el bloque
               String etiqueta = campo(bloque, 0, 3);
               if (!etiqueta.equals("TAG")) {
                   return null;
               } else {
                   String cancionTitulo = campo(bloque, 3, 30);
                   String artista = campo(bloque, 33, 30);
                   String albumTitulo = campo(bloque, 63, 30);
                   String anio = campo(bloque, 93, 4);
                   int anioInt = 0;
                   try {
                       anioInt = Integer.parseInt(anio);
                   } catch (Exception e) {
                       System.err.println("No se puedo leer año (tomara valor por defecto): " + e.getMessage());
                   }
                   //Creamos t0do lo necesario para Cancion

                   //El genero es el ultimo byte (https://id3.org/ID3v1)
                   int codigoGenero = bloque[127] & 0xFF;
                   String nombreGenero="";

                   switch (codigoGenero) {
                       case 1:  nombreGenero = "Classic Rock"; break;
                       case 13: nombreGenero = "Pop"; break;
                       case 17: nombreGenero = "Rock"; break;
                       default: nombreGenero = "Género " + codigoGenero; break;
                   }
                   Genero g = obtenerCrearGenero(nombreGenero);

                   //Lo demas
                   Artista art = obtenerCrearArtista(artista, null);
                   Album a = obtenerCrearAlbum(albumTitulo, anioInt, art, g);
                   return new Cancion(cancionTitulo, 0, 1, a);
               }
           }
       }
        catch (IOException e)
        {
            System.err.println("No se pudo leer el archivo MP3: " + e.getMessage());
            return null;
        }
    }

    //Auxiliar
    private static String campo(byte[] bloque, int desde, int longitud) {
        String resultado=new String(bloque, desde,longitud, StandardCharsets.ISO_8859_1 );
        int primerCero=resultado.indexOf("\0");
        if(primerCero!=-1){
            resultado=resultado.substring(0,primerCero);
        }
        return resultado.trim();
    }

    // ---------- Ejercicio 3: serializar/deserializar la biblioteca ----------

    public void guardarBiblioteca(Biblioteca biblioteca, String rutaFichero) throws IOException {
        Path ruta= Path.of(rutaFichero).toAbsolutePath();
        System.out.println("Ruta encontrada: "+ruta);
        Path padre= ruta.getParent();
        try {
            if (padre != null) {
                Files.createDirectories(padre);
            }
            try (ObjectOutputStream salida = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(ruta)))){
                salida.writeObject(biblioteca);
            }
        } catch (IOException e) {
            throw new IOException("Error al guardar la biblioteca en: " + ruta, e);
        }
    }

    public Biblioteca cargarBiblioteca(String rutaFichero) throws IOException, ClassNotFoundException{
        Path ruta= Path.of(rutaFichero).toAbsolutePath();
        Biblioteca b=new Biblioteca();
        if (Files.notExists(ruta)) {
            return new Biblioteca();
        }

        try (ObjectInputStream entrada = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(ruta)))) {
            b = (Biblioteca) entrada.readObject();
            return b;
        }
        catch (ClassNotFoundException | IOException e)
        {
            System.err.println("Error: " + e.getMessage());
            return new Biblioteca();
        }
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
    private int siguienteIdDisponibleListas(List<ListaReproduccion> listasRepro) {
        return listasRepro.stream().mapToInt(ListaReproduccion::getId).max().orElse(0)+1;
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
        boolean existeAlbum=biblioteca.getCanciones().stream().anyMatch(c->c.getAlbum().getTitulo().equalsIgnoreCase(cancion.getAlbum().getTitulo()));
        boolean existeArtista=biblioteca.getCanciones().stream().anyMatch(c->c.getAlbum().getArtista().getNombre().equalsIgnoreCase(cancion.getAlbum().getArtista().getNombre()));
        boolean existeGenero=biblioteca.getCanciones().stream().anyMatch(c->c.getAlbum().getGenero().getNombre().equalsIgnoreCase(cancion.getAlbum().getGenero().getNombre()));
        boolean Existe= existeAlbum && existeArtista && existeGenero;

        if(Existe)
        {
            return;
        }

        //Si no existe, se crea todo lo necesario para crear una instancia de Cancion
        Genero genero= obtenerCrearGenero(cancion.getAlbum().getGenero().getNombre());
        Artista artista= obtenerCrearArtista(cancion.getAlbum().getArtista().getNombre(),
                cancion.getAlbum().getArtista().getNacionalidad());
        Album album=obtenerCrearAlbum(cancion.getAlbum().getTitulo(),cancion.getAlbum().getAnio(),artista,genero);

        cancion.setAlbum(album);
        cancion.setId(siguienteIdDisponibleCancion(biblioteca.getCanciones()));

        biblioteca.getCanciones().add(cancion);
    }

    @Override
    public void actualizarCancion(Cancion cancion) {
        for (int i = 0; i < biblioteca.getCanciones().size(); i++) {
            Cancion c = biblioteca.getCanciones().get(i);
            if (cancion.getId() == c.getId()) {
                biblioteca.getCanciones().set(i, cancion);
            }
        }
    }

    @Override
    public void eliminarCancion(int idCancion) {
        Cancion cancionAEliminar=
                biblioteca.getCanciones()
                        .stream()
                        .filter(c -> c.getId() == idCancion)
                        .findFirst()
                        .orElse(null);
        if (cancionAEliminar != null) {
            biblioteca.getCanciones().remove(cancionAEliminar);
        }
    }

    @Override
    public List<ListaReproduccion> listarListas() {
        return biblioteca.getListasReproduccion();
    }

    @Override
    public void altaLista(ListaReproduccion lista) {
        lista.setId(siguienteIdDisponibleListas(biblioteca.getListasReproduccion()));
        biblioteca.getListasReproduccion().add(lista);
    }

    @Override
    public void anyadirCancionALista(int idLista, int idCancion, int posicion) {
        int indiceInsercion=posicion-1;

        ListaReproduccion lista=biblioteca.getListasReproduccion()
                .stream()
                .filter(l->l.getId()==idLista)
                .findFirst()
                .orElse(null);
        Cancion cancion=biblioteca.getCanciones()
                .stream()
                .filter(c->c.getId()==idCancion)
                .findFirst()
                .orElse(null);

        if (cancion!=null && lista!=null && indiceInsercion<=lista.getCanciones().size() && indiceInsercion>=0)
        {
            lista.getCanciones().add(indiceInsercion,cancion);
        }
    }

    @Override
    public List<Cancion> cancionesDeLista(int idLista) {
        List<Cancion> cancionesLista=new ArrayList<>();
        ListaReproduccion lista= biblioteca.getListasReproduccion().stream()
                .filter(l->l.getId()==idLista)
                .findFirst()
                .orElse(null);

        if (lista!=null)
        {
            cancionesLista=lista.getCanciones();
        }
        return cancionesLista;
    }
}
