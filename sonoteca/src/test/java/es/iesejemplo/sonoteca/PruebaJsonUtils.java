package es.iesejemplo.sonoteca;

import es.iesejemplo.sonoteca.modelo.Cancion;

import java.util.ArrayList;
import java.util.List;

import static es.iesejemplo.sonoteca.util.JsonUtils.*;

public class PruebaJsonUtils
{
    public static void main(String[] args)
    {
        Cancion c1 = new Cancion("Bohemian Rhapsody", 354, 1, null);
        Cancion c2 = new Cancion("Imagine", 183, 3, null);

        String json1=exportarCancion(c1);
        String json2=exportarCancion(c2);

        System.out.println("Comprobación legibilidad del JSON:");
        System.out.println(json1);
        System.out.println(json2);

        Cancion c1Import=importarCancion(json1);
        Cancion c2Import=importarCancion(json2);

        System.out.println("Comprobación igualdad del JSON:");
        System.out.println(c1Import.equals(c1));
        System.out.println(c2Import.equals(c2));

        List<Cancion> canciones=new ArrayList<>();
        canciones.add(c1);
        canciones.add(c2);
        String jsonLista=exportarLista(canciones);
        List<Cancion> cancionesCopia=importarLista(jsonLista);

        System.out.println("Comprobación legibilidad del JSON (Lista):");
        System.out.println(jsonLista);

        System.out.println("Comprobación igualdad del JSON (Lista):");
        System.out.println(canciones.equals(cancionesCopia));
    }
}