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
import java.util.Scanner;

public class IniciarAnalizador {

    public IniciarAnalizador() {

    }

    public void iniciar(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("=========================================");
        System.out.println("  COMPILADOR PROMPTZAL - ANALIZADOR LÉXICO");
        System.out.println("=========================================");
        System.out.print("Ingrese la ruta del archivo .pz: \n");

        JFileChooser chooser = new JFileChooser();
        int resultado = chooser.showOpenDialog(null);
        String rutaArchivo = " ";
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            rutaArchivo = archivo.getAbsolutePath();
        }

        if (!rutaArchivo.endsWith(".pz")) {
            System.out.println("Advertencia: seleccionar un archivo con extensión .pz");
            System.exit(0);
        } else {
            try {
                String contenido = new String(Files.readAllBytes(Paths.get(rutaArchivo)));
                AnalizadorLexico lexer = new AnalizadorLexico(contenido);
                lexer.analizar();
                // Mostrar Tokens en Consola
                System.out.println("\n--- TABLA DE TOKENS EN CONSOLA ---");
                for (Token t : lexer.getListaTokens()) {
                    System.out.println(t);
                }

                System.out.println("\n--- TABLA DE ERRORES ---");

                if (lexer.getListaErrores().isEmpty()) {
                    System.out.println("¡No se encontraron errores léxicos!");
                } else {
                    for (ErrorLexico err : lexer.getListaErrores()) {
                        System.out.println(err);
                    }
                }

                // Generar Reportes HTML
                GeneradorReportes.generarReporteTokensHTML(lexer.getListaTokens(), solicitarDireccionGuardar());
                GeneradorReportes.generarReporteErroresHTML(lexer.getListaErrores(), solicitarDireccionGuardar());
                System.out.println("\nAnálisis léxico completado. Revisa la consola y los archivos HTML generados.");
            } catch (IOException e) {
                System.err.println("Error al abrir el archivo: " + e.getMessage());
                System.exit(0);
            }
        }
    }


    public String solicitarDireccionGuardar(){
        JFileChooser chooser = new JFileChooser();
        int decision = chooser.showSaveDialog(null);
        if (decision == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            if (!archivo.getName().endsWith(".html")) {
                archivo = new File(archivo.getAbsolutePath() + ".html");
            }
            return archivo.getAbsolutePath();
        }

        return null;
    }
}
