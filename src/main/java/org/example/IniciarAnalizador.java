package org.example;

import org.example.Datos.ErrorLexico;
import org.example.Datos.Token;
import org.example.Lexema.AnalizadorLexico;
import org.example.Reoirtes.GeneradorReportes;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IniciarAnalizador {

    public IniciarAnalizador() {

    }

    public void iniciar() {
        System.out.println("=========================================");
        System.out.println("  COMPILADOR PROMPTZAL - ANALIZADOR LÉXICO");
        System.out.println("=========================================");
        System.out.println("Seleccione el archivo .pz a analizar...");

        JFileChooser chooser = new JFileChooser();
        int resultado = chooser.showOpenDialog(null);

        if (resultado != JFileChooser.APPROVE_OPTION) {
            System.out.println("Selección de archivo cancelada.");
            return;
        }

        File archivo = chooser.getSelectedFile();
        String rutaArchivo = archivo.getAbsolutePath();

        if (!rutaArchivo.endsWith(".pz")) {
            System.out.println("Error: Debe seleccionar un archivo con extensión .pz");
            return;
        }

        try {
            String contenido = new String(Files.readAllBytes(Paths.get(rutaArchivo)));
            AnalizadorLexico lexer = new AnalizadorLexico(contenido);
            lexer.analizar();

            // Mostrar Tokens en Consola
            System.out.println("\n--- TABLA DE TOKENS EN CONSOLA ---");
            for (Token t : lexer.getListaTokens()) {
                System.out.println(t);
            }

            // Mostrar Errores en Consola
            System.out.println("\n--- TABLA DE ERRORES ---");
            if (lexer.getListaErrores().isEmpty()) {
                System.out.println("¡No se encontraron errores léxicos!");
            } else {
                for (ErrorLexico err : lexer.getListaErrores()) {
                    System.out.println(err);
                }
            }

            // Generar Reportes HTML en un solo paso de selección
            guardarReportesHTML(lexer.getListaTokens(), lexer.getListaErrores());

            System.out.println("\nAnálisis léxico completado exitosamente.");

        } catch (IOException e) {
            System.err.println("Error al abrir el archivo: " + e.getMessage());
        }
    }

    private void guardarReportesHTML(java.util.List<Token> tokens, java.util.List<ErrorLexico> errores) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccione el nombre base y ubicación para guardar los reportes");
        chooser.setSelectedFile(new File("reporte"));

        int decision = chooser.showSaveDialog(null);
        if (decision == JFileChooser.APPROVE_OPTION) {
            File seleccionado = chooser.getSelectedFile();
            String rutaBase = seleccionado.getAbsolutePath();

            // Eliminar .html si el usuario lo escribió para armar los dos nombres
            if (rutaBase.endsWith(".html")) {
                rutaBase = rutaBase.substring(0, rutaBase.length() - 5);
            }

            String rutaTokens = rutaBase + "_tokens.html";
            String rutaErrores = rutaBase + "_errores.html";

            GeneradorReportes.generarReporteTokensHTML(tokens, rutaTokens);
            GeneradorReportes.generarReporteErroresHTML(errores, rutaErrores);
        } else {
            System.out.println("Guardado de reportes cancelado por el usuario.");
        }
    }
}