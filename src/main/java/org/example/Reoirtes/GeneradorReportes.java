package org.example.Reoirtes;

import org.example.Datos.ErrorLexico;
import org.example.Datos.TipoToken;
import org.example.Datos.Token;

import java.io.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GeneradorReportes {

    public static void generarReporteTokensHTML(List<Token> tokens, String rutaSalida) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("<title>Reporte de Tokens - PromptZal</title>\n");
        html.append("<style>\n");
        html.append("  :root { --primary: #1e40af; --bg: #f8fafc; --card-bg: #ffffff; --text: #0f172a; --border: #e2e8f0; }\n");
        html.append("  body { font-family: 'Inter', system-ui, -apple-system, sans-serif; background-color: var(--bg); color: var(--text); margin: 0; padding: 40px 20px; }\n");
        html.append("  .container { max-width: 1100px; margin: 0 auto; background: var(--card-bg); padding: 32px; border-radius: 16px; box-shadow: 0 10px 25px -5px rgba(0,0,0,0.05); border: 1px solid var(--border); }\n");
        html.append("  .header { border-bottom: 2px solid var(--border); padding-bottom: 16px; margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; }\n");
        html.append("  h1 { color: var(--primary); font-size: 1.75rem; margin: 0; font-weight: 700; display: flex; align-items: center; gap: 10px; }\n");
        html.append("  .badge-total { background: #eff6ff; color: #1e40af; font-size: 0.875rem; font-weight: 600; padding: 6px 12px; border-radius: 9999px; border: 1px solid #bfdbfe; }\n");
        html.append("  table { width: 100%; border-collapse: separate; border-spacing: 0; border-radius: 12px; overflow: hidden; border: 1px solid var(--border); }\n");
        html.append("  th, td { padding: 14px 18px; text-align: left; text-size-adjust: 100%; }\n");
        html.append("  th { background-color: #f1f5f9; color: #475569; font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.05em; border-bottom: 1px solid var(--border); }\n");
        html.append("  tr:not(:last-child) td { border-bottom: 1px solid var(--border); }\n");
        html.append("  tr:hover { background-color: #f8fafc; }\n");
        html.append("  .code { font-family: 'Fira Code', 'Consolas', monospace; font-size: 0.9rem; background: #f1f5f9; padding: 3px 8px; border-radius: 6px; color: #0f172a; border: 1px solid #e2e8f0; display: inline-block; }\n");
        html.append("  .token-badge { font-weight: 600; font-size: 0.75rem; padding: 4px 10px; border-radius: 6px; text-transform: uppercase; letter-spacing: 0.03em; display: inline-block; }\n");
        html.append("  /* Badges por tipo */\n");
        html.append(" .badge-directiva  { background: #fae8ff; color: #86198f; } /* Purpura */ \n");
        html.append(" .badge-keyword    { background: #e0e7ff; color: #3730a3; } /* Azul añil */ \n");
        html.append(" .badge-comando    { background: #fce7f3; color: #9d174d; } /* Rosa / Fucsia */ \n");
        html.append(" .badge-conector   { background: #e0f2fe; color: #0369a1; } /* Celeste */ \n");
        html.append(" .badge-id         { background: #dcfce7; color: #166534; } /* Verde */ \n");
        html.append(" .badge-string     { background: #ffedd5; color: #9a3412; } /* Naranja */ \n");
        html.append(" .badge-number     { background: #fef3c7; color: #92400e; } /* Ámbar / Amarillo */ \n");
        html.append(" .badge-operator   { background: #f3e8ff; color: #6b21a8; } /* Violeta */ \n");
        html.append(" .badge-delimitador{ background: #f1f5f9; color: #334155; } /* Gris oscuro */ \n");
        html.append(" .badge-default    { background: #f1f5f9; color: #64748b; } /* Gris neutro */ \n");
        html.append("</style>\n</head>\n<body>\n");

        html.append("<div class=\"container\">\n");
        html.append("  <div class=\"header\">\n");
        html.append("    <h1>⚡ Reporte de Tokens Reconocidos</h1>\n");
        html.append("    <span class=\"badge-total\">Total: ").append(tokens.size()).append(" tokens</span>\n");
        html.append("  </div>\n");

        html.append("  <table>\n");
        html.append("    <thead>\n<tr><th>#</th><th>Lexema</th><th>Tipo de Token</th><th>Fila</th><th>Columna</th></tr>\n</thead>\n");
        html.append("    <tbody>\n");

        for (Token t : tokens) {
            String tokenTipo = String.valueOf(t.getTipo());
            String claseBadge = obtenerClaseCssPorTipo(t.getTipo());

            html.append("<tr>")
                    .append("<td>").append(t.getId()).append("</td>")
                    .append("<td><span class=\"code\">").append(escapeHtml(t.getLexema())).append("</span></td>")
                    .append("<td><span class=\"token-badge ").append(claseBadge).append("\">").append(tokenTipo).append("</span></td>")
                    .append("<td>").append(t.getFila()).append("</td>")
                    .append("<td>").append(t.getColumna()).append("</td>")
                    .append("</tr>\n");
        }

        html.append("    </tbody>\n  </table>\n");
        html.append("</div>\n</body>\n</html>");
        escribirArchivo(rutaSalida, html.toString());
    }

    public static void generarReporteErroresHTML(List<ErrorLexico> errores, String rutaSalida) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("<title>Reporte de Errores - PromptZal</title>\n");
        html.append("<style>\n");
        html.append("  :root { --danger: #dc2626; --bg: #f8fafc; --card-bg: #ffffff; --text: #0f172a; --border: #e2e8f0; }\n");
        html.append("  body { font-family: 'Inter', system-ui, -apple-system, sans-serif; background-color: var(--bg); color: var(--text); margin: 0; padding: 40px 20px; }\n");
        html.append("  .container { max-width: 1100px; margin: 0 auto; background: var(--card-bg); padding: 32px; border-radius: 16px; box-shadow: 0 10px 25px -5px rgba(0,0,0,0.05); border: 1px solid var(--border); }\n");
        html.append("  .header { border-bottom: 2px solid var(--border); padding-bottom: 16px; margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; }\n");
        html.append("  h1 { color: var(--danger); font-size: 1.75rem; margin: 0; font-weight: 700; display: flex; align-items: center; gap: 10px; }\n");
        html.append("  table { width: 100%; border-collapse: separate; border-spacing: 0; border-radius: 12px; overflow: hidden; border: 1px solid var(--border); }\n");
        html.append("  th, td { padding: 14px 18px; text-align: left; }\n");
        html.append("  th { background-color: #fef2f2; color: #991b1b; font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.05em; border-bottom: 1px solid var(--border); }\n");
        html.append("  tr:not(:last-child) td { border-bottom: 1px solid var(--border); }\n");
        html.append("  tr:hover { background-color: #fff1f2; }\n");
        html.append("  .code-error { font-family: 'Fira Code', 'Consolas', monospace; font-size: 0.9rem; background: #fef2f2; padding: 3px 8px; border-radius: 6px; color: #991b1b; border: 1px solid #fecaca; display: inline-block; font-weight: 600; }\n");
        html.append("  .no-errors { text-align: center; padding: 40px 20px; background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 12px; color: #166534; font-size: 1.1rem; font-weight: 600; display: flex; align-items: center; justify-content: center; gap: 8px; }\n");
        html.append("</style>\n</head>\n<body>\n");

        html.append("<div class=\"container\">\n");
        html.append("  <div class=\"header\">\n");
        html.append("    <h1>⚠️ Reporte de Errores Léxicos</h1>\n");
        html.append("  </div>\n");

        if (errores.isEmpty()) {
            html.append("  <div class=\"no-errors\"><span></span> ¡Excelente! No se encontraron errores léxicos en el archivo.</div>\n");
        } else {
            html.append("  <table>\n");
            html.append("    <thead>\n<tr><th>#</th><th>Carácter / Lexema</th><th>Descripción del Error</th><th>Fila</th><th>Columna</th></tr>\n</thead>\n");
            html.append("    <tbody>\n");
            int i = 1;
            for (ErrorLexico e : errores) {
                html.append("<tr>")
                        .append("<td>").append(i++).append("</td>")
                        .append("<td><span class=\"code-error\">").append(escapeHtml(e.getLexema())).append("</span></td>")
                        .append("<td>").append(escapeHtml(e.getDescripcion())).append("</td>")
                        .append("<td>").append(e.getFila()).append("</td>")
                        .append("<td>").append(e.getColumna()).append("</td>")
                        .append("</tr>\n");
            }
            html.append("    </tbody>\n  </table>\n");
        }

        html.append("</div>\n</body>\n</html>");
        escribirArchivo(rutaSalida, html.toString());
    }

    private static String obtenerClaseCssPorTipo(TipoToken tipo) {
        if (tipo == null) return "badge-default";

        switch (tipo) {
            // Directivas
            case DIRECTIVA_MODELO:
            case DIRECTIVA_ROL:
            case DIRECTIVA_FORMATO:
                return "badge-directiva";

            // Palabras Reservadas
            case PR_AGENTE:
            case PR_CONTEXTO:
            case PR_VARIABLE:
            case PR_EJECUTAR:
            case PR_EXPORTAR:
                return "badge-keyword";

            // Comandos de IA
            case CMD_PREGUNTAR:
            case CMD_GENERAR:
            case CMD_RESUMIR:
            case CMD_ANALIZAR:
            case CMD_TRADUCIR:
            case CMD_CLASIFICAR:
            case CMD_EXTRAER:
                return "badge-comando";

            // Conectores
            case CON_SOBRE:
            case CON_DESDE:
            case CON_EN:
            case CON_COMO:
            case FLECHA:
                return "badge-conector";

            // Identificadores y Funciones
            case IDENTIFICADOR:
            case FUNC_CARGAR:
                return "badge-id";

            // Literales
            case CADENA:
                return "badge-string";
            case ENTERO:
            case DECIMAL:
                return "badge-number";

            // Operadores
            case IGUAL:
            case MAS:
                return "badge-operator";

            // Delimitadores
            case LLAVE_IZQ:
            case LLAVE_DER:
            case PAREN_IZQ:
            case PAREN_DER:
            case COMMA:
                return "badge-delimitador";

            // Control / Fin de Archivo
            case EOF:
            default:
                return "badge-default";
        }
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

    public static void generarReporteEstadisticasHTML(List<Token> tokens, List<ErrorLexico> errores, int totalLineas, String rutaSalida) {
        // Arreglo de contadores del mismo tamaño que el Enum
        TipoToken[] tipos = TipoToken.values();
        int[] conteo = new int[tipos.length];

        // Incrementar el contador según el ordinal de cada tipo de token
        for (Token t : tokens) {
            if (t.getTipo() != null) {
                conteo[t.getTipo().ordinal()]++;
            }
        }

        int totalTokens = tokens.size();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("<title>Reporte de Estadísticas - PromptZal</title>\n");
        html.append("<style>\n");
        html.append("  :root { --primary: #1e40af; --bg: #f8fafc; --card-bg: #ffffff; --text: #0f172a; --border: #e2e8f0; }\n");
        html.append("  body { font-family: 'Inter', system-ui, -apple-system, sans-serif; background-color: var(--bg); color: var(--text); margin: 0; padding: 40px 20px; }\n");
        html.append("  .container { max-width: 1000px; margin: 0 auto; background: var(--card-bg); padding: 32px; border-radius: 16px; box-shadow: 0 10px 25px -5px rgba(0,0,0,0.05); border: 1px solid var(--border); }\n");
        html.append("  .header { border-bottom: 2px solid var(--border); padding-bottom: 16px; margin-bottom: 28px; }\n");
        html.append("  h1 { color: var(--primary); font-size: 1.75rem; margin: 0; font-weight: 700; }\n");
        html.append("  h2 { font-size: 1.2rem; color: #334155; margin: 32px 0 16px 0; font-weight: 600; }\n");

        // Estilos para el dashboard (tarjetas métricas)
        html.append("  .metrics-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; margin-bottom: 32px; }\n");
        html.append("  .metric-card { background: #f8fafc; border: 1px solid var(--border); padding: 20px; border-radius: 12px; display: flex; flex-direction: column; gap: 8px; }\n");
        html.append("  .metric-card.error { background: #fef2f2; border-color: #fecaca; }\n");
        html.append("  .metric-label { font-size: 0.85rem; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: 0.05em; }\n");
        html.append("  .metric-card.error .metric-label { color: #991b1b; }\n");
        html.append("  .metric-value { font-size: 2rem; font-weight: 800; color: #0f172a; }\n");
        html.append("  .metric-card.error .metric-value { color: #dc2626; }\n");

        // Estilos de la tabla y barras de progreso
        html.append("  table { width: 100%; border-collapse: separate; border-spacing: 0; border-radius: 12px; overflow: hidden; border: 1px solid var(--border); }\n");
        html.append("  th, td { padding: 14px 18px; text-align: left; }\n");
        html.append("  th { background-color: #f1f5f9; color: #475569; font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.05em; border-bottom: 1px solid var(--border); }\n");
        html.append("  tr:not(:last-child) td { border-bottom: 1px solid var(--border); }\n");
        html.append("  tr:hover { background-color: #f8fafc; }\n");
        html.append("  .progress-bar-bg { background: #e2e8f0; height: 8px; border-radius: 9999px; width: 100%; overflow: hidden; margin-top: 6px; }\n");
        html.append("  .progress-bar-fill { background: var(--primary); height: 100%; border-radius: 9999px; }\n");

        // Badges por tipo
        html.append("  .token-badge { font-weight: 600; font-size: 0.75rem; padding: 4px 10px; border-radius: 6px; text-transform: uppercase; letter-spacing: 0.03em; display: inline-block; }\n");
        html.append("  .badge-directiva { background: #fae8ff; color: #86198f; }\n");
        html.append("  .badge-keyword   { background: #e0e7ff; color: #3730a3; }\n");
        html.append("  .badge-comando   { background: #fce7f3; color: #9d174d; }\n");
        html.append("  .badge-conector  { background: #e0f2fe; color: #0369a1; }\n");
        html.append("  .badge-id        { background: #dcfce7; color: #166534; }\n");
        html.append("  .badge-string    { background: #ffedd5; color: #9a3412; }\n");
        html.append("  .badge-number    { background: #fef3c7; color: #92400e; }\n");
        html.append("  .badge-operator  { background: #f3e8ff; color: #6b21a8; }\n");
        html.append("  .badge-delimitador{ background: #f1f5f9; color: #334155; }\n");
        html.append("  .badge-default   { background: #f1f5f9; color: #64748b; }\n");
        html.append("</style>\n</head>\n<body>\n");

        html.append("<div class=\"container\">\n");
        html.append("  <div class=\"header\">\n");
        html.append("    <h1>📊 Resumen Estadístico del Análisis</h1>\n");
        html.append("  </div>\n");

        // Dashboard con métricas clave
        html.append("  <div class=\"metrics-grid\">\n");
        html.append("    <div class=\"metric-card\">\n");
        html.append("      <span class=\"metric-label\">Líneas Analizadas</span>\n");
        html.append("      <span class=\"metric-value\">").append(totalLineas).append("</span>\n");
        html.append("    </div>\n");

        html.append("    <div class=\"metric-card\">\n");
        html.append("      <span class=\"metric-label\">Tokens Reconocidos</span>\n");
        html.append("      <span class=\"metric-value\">").append(totalTokens).append("</span>\n");
        html.append("    </div>\n");

        html.append("    <div class=\"metric-card ").append(errores.isEmpty() ? "" : "error").append("\">\n");
        html.append("      <span class=\"metric-label\">Errores Léxicos</span>\n");
        html.append("      <span class=\"metric-value\">").append(errores.size()).append("</span>\n");
        html.append("    </div>\n");
        html.append("  </div>\n");

        // Tabla de frecuencias
        html.append("  <h2>Frecuencia por Tipo de Token</h2>\n");
        html.append("  <table>\n");
        html.append("    <thead>\n<tr><th>Tipo de Token</th><th>Cantidad Detectada</th><th>Distribución</th></tr>\n</thead>\n");
        html.append("    <tbody>\n");

        for (int i = 0; i < tipos.length; i++) {
            if (conteo[i] > 0) {
                TipoToken tipo = tipos[i];
                int cantidad = conteo[i];
                double porcentaje = totalTokens > 0 ? ((double) cantidad / totalTokens) * 100 : 0;
                String claseBadge = obtenerClaseCssPorTipo(tipo);

                html.append("<tr>")
                        .append("<td><span class=\"token-badge ").append(claseBadge).append("\">").append(tipo).append("</span></td>")
                        .append("<td><strong>").append(cantidad).append("</strong></td>")
                        .append("<td style=\"width: 40%;\">")
                        .append("  <div style=\"font-size: 0.8rem; color: #64748b;\">").append(String.format("%.1f", porcentaje)).append("%</div>")
                        .append("  <div class=\"progress-bar-bg\"><div class=\"progress-bar-fill\" style=\"width: ").append(String.format("%.1f", porcentaje)).append("%;\"></div></div>")
                        .append("</td>")
                        .append("</tr>\n");
            }
        }

        html.append("    </tbody>\n  </table>\n");
        html.append("</div>\n</body>\n</html>");

        escribirArchivo(rutaSalida, html.toString());
    }
}