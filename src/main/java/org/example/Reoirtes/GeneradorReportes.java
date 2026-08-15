package org.example.Reoirtes;

import org.example.Datos.ErrorLexico;
import org.example.Datos.Token;

import java.io.*;
import java.util.List;

public class GeneradorReportes {

    public static void generarReporteTokensHTML(List<Token> tokens, String rutaSalida) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n<title>Reporte de Tokens - PromptZal</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f6f9; }\n");
        html.append("h1 { color: #1e3a8a; }\n");
        html.append("table { width: 100%; border-collapse: collapse; background: #fff; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }\n");
        html.append("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }\n");
        html.append("th { background-color: #1e3a8a; color: white; }\n");
        html.append("tr:hover { background-color: #f1f5f9; }\n");
        html.append("</style>\n</head>\n<body>\n");
        html.append("<h1>Reporte de Tokens Reconocidos</h1>\n");
        html.append("<table>\n<tr><th>#</th><th>Lexema</th><th>Tipo de Token</th><th>Fila</th><th>Columna</th></tr>\n");

        for (Token t : tokens) {
            html.append("<tr>")
                    .append("<td>").append(t.getId()).append("</td>")
                    .append("<td>").append(escapeHtml(t.getLexema())).append("</td>")
                    .append("<td>").append(t.getTipo()).append("</td>")
                    .append("<td>").append(t.getFila()).append("</td>")
                    .append("<td>").append(t.getColumna()).append("</td>")
                    .append("</tr>\n");
        }

        html.append("</table>\n</body>\n</html>");
        escribirArchivo(rutaSalida, html.toString());
    }

    public static void generarReporteErroresHTML(List<ErrorLexico> errores, String rutaSalida) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n<title>Reporte de Errores - PromptZal</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f6f9; }\n");
        html.append("h1 { color: #991b1b; }\n");
        html.append("table { width: 100%; border-collapse: collapse; background: #fff; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }\n");
        html.append("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }\n");
        html.append("th { background-color: #991b1b; color: white; }\n");
        html.append("tr:hover { background-color: #fef2f2; }\n");
        html.append(".no-errors { color: #166534; font-weight: bold; font-size: 1.2em; }\n");
        html.append("</style>\n</head>\n<body>\n");
        html.append("<h1>Reporte de Errores Léxicos</h1>\n");

        if (errores.isEmpty()) {
            html.append("<p class=\"no-errors\">¡No se encontraron errores léxicos en el archivo!</p>\n");
        } else {
            html.append("<table>\n<tr><th>#</th><th>Carácter / Lexema</th><th>Descripción del Error</th><th>Fila</th><th>Columna</th></tr>\n");
            int i = 1;
            for (ErrorLexico e : errores) {
                html.append("<tr>")
                        .append("<td>").append(i++).append("</td>")
                        .append("<td>").append(escapeHtml(e.getLexema())).append("</td>")
                        .append("<td>").append(e.getDescripcion()).append("</td>")
                        .append("<td>").append(e.getFila()).append("</td>")
                        .append("<td>").append(e.getColumna()).append("</td>")
                        .append("</tr>\n");
            }
            html.append("</table>\n");
        }

        html.append("</body>\n</html>");
        escribirArchivo(rutaSalida, html.toString());
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static void escribirArchivo(String ruta, String contenido) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            bw.write(contenido);
            System.out.println("Reporte generado exitosamente en: " + ruta);
        } catch (IOException e) {
            System.err.println("Error al guardar el reporte: " + e.getMessage());
        }
    }
}