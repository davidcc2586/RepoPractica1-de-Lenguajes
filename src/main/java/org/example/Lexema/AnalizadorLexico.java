package org.example.Lexema;

import org.example.Datos.ErrorLexico;
import org.example.Datos.TipoToken;
import org.example.Datos.Token;

import java.util.ArrayList;
import java.util.List;

public class AnalizadorLexico {
    private String entrada;
    private int posicion;
    private int fila;
    private int columna;
    private int contadorTokens;

    private List<Token> listaTokens;
    private List<ErrorLexico> listaErrores;

    public AnalizadorLexico(String entrada) {
        this.entrada = entrada != null ? entrada : "";
        this.posicion = 0;
        this.fila = 1;
        this.columna = 1;
        this.contadorTokens = 1;
        this.listaTokens = new ArrayList<>();
        this.listaErrores = new ArrayList<>();
    }

    public void analizar() {
        while (posicion < entrada.length()) {
            char c = entrada.charAt(posicion);

            // Manejo de espacios en blanco y saltos de línea
            if (c == '\n') {
                fila++;
                columna = 1;
                posicion++;
                continue;
            }
            if (c == '\r' || c == '\t' || c == ' ') {
                avanzar();
                continue;
            }

            // Comentarios de Línea (//) y Bloque (/* */)
            if (c == '/' && posicion + 1 < entrada.length()) {
                char sig = entrada.charAt(posicion + 1);
                if (sig == '/') {
                    while (posicion < entrada.length() && entrada.charAt(posicion) != '\n') {
                        posicion++;
                    }
                    continue;
                } else if (sig == '*') {
                    posicion += 2;
                    columna += 2;
                    while (posicion < entrada.length()) {
                        if (entrada.charAt(posicion) == '\n') {
                            fila++;
                            columna = 1;
                            posicion++;
                        } else if (entrada.charAt(posicion) == '*' && posicion + 1 < entrada.length() && entrada.charAt(posicion + 1) == '/') {
                            posicion += 2;
                            columna += 2;
                            break;
                        } else {
                            avanzar();
                        }
                    }
                    continue;
                }
            }

            // -------------------------------------------------------------
            // ESTADO q0 -> Transiciones directas del AFD
            // -------------------------------------------------------------

            // Delimitadores y Operadores
            if (c == '{') { agregarToken("{", TipoToken.LLAVE_IZQ, columna); avanzar(); continue; }
            if (c == '}') { agregarToken("}", TipoToken.LLAVE_DER, columna); avanzar(); continue; }
            if (c == '(') { agregarToken("(", TipoToken.PAREN_IZQ, columna); avanzar(); continue; }
            if (c == ')') { agregarToken(")", TipoToken.PAREN_DER, columna); avanzar(); continue; }
            if (c == ',') { agregarToken(",", TipoToken.COMMA, columna); avanzar(); continue; }
            if (c == '=') { agregarToken("=", TipoToken.IGUAL, columna); avanzar(); continue; }
            if (c == '+') { agregarToken("+", TipoToken.MAS, columna); avanzar(); continue; }

            // Transición q0 -> FLECHA (->)
            if (c == '-' && posicion + 1 < entrada.length() && entrada.charAt(posicion + 1) == '>') {
                int colInicio = columna;
                posicion += 2;
                columna += 2;
                listaTokens.add(new Token(contadorTokens++, "->", TipoToken.FLECHA, fila, colInicio));
                continue;
            }

            // Transición q0 -> DIRECTIVAS (@)
            if (c == '@') {
                int colInicio = columna;
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                avanzar();

                while (posicion < entrada.length() && esLetra(entrada.charAt(posicion))) {
                    sb.append(entrada.charAt(posicion));
                    avanzar();
                }

                String lex = sb.toString();
                switch (lex) {
                    case "@modelo": listaTokens.add(new Token(contadorTokens++, lex, TipoToken.DIRECTIVA_MODELO, fila, colInicio)); break;
                    case "@rol": listaTokens.add(new Token(contadorTokens++, lex, TipoToken.DIRECTIVA_ROL, fila, colInicio)); break;
                    case "@formato": listaTokens.add(new Token(contadorTokens++, lex, TipoToken.DIRECTIVA_FORMATO, fila, colInicio)); break;
                    default:
                        listaErrores.add(new ErrorLexico(lex, "Directiva desconocida o inválida", fila, colInicio));
                        break;
                }
                continue;
            }

            // Transición q0 -> CADENAS ("...")
            if (c == '"') {
                int colInicio = columna;
                int filaInicio = fila;
                StringBuilder sb = new StringBuilder();
                sb.append('"');
                avanzar();

                boolean cerrada = false;
                while (posicion < entrada.length()) {
                    char actual = entrada.charAt(posicion);
                    if (actual == '\n') {
                        break;
                    }
                    sb.append(actual);
                    avanzar();
                    if (actual == '"') {
                        cerrada = true;
                        break;
                    }
                }

                if (cerrada) {
                    listaTokens.add(new Token(contadorTokens++, sb.toString(), TipoToken.CADENA, filaInicio, colInicio));
                } else {
                    listaErrores.add(new ErrorLexico(sb.toString(), "Cadena sin cerrar", filaInicio, colInicio));
                }
                continue;
            }

            // Transición q0 -> NÚMEROS (Enteros / Decimales)
            if (esDigito(c)) {
                int colInicio = columna;
                StringBuilder sb = new StringBuilder();
                boolean tienePunto = false;

                while (posicion < entrada.length()) {
                    char actual = entrada.charAt(posicion);
                    if (esDigito(actual)) {
                        sb.append(actual);
                        avanzar();
                    } else if (actual == '.' && !tienePunto) {
                        if (posicion + 1 < entrada.length() && esDigito(entrada.charAt(posicion + 1))) {
                            tienePunto = true;
                            sb.append(actual);
                            avanzar();
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }

                String lex = sb.toString();
                TipoToken tipo = tienePunto ? TipoToken.DECIMAL : TipoToken.ENTERO;
                listaTokens.add(new Token(contadorTokens++, lex, tipo, fila, colInicio));
                continue;
            }

            // Transición q0 -> IDENTIFICADORES / PALABRAS RESERVADAS / COMANDOS / CONECTORES
            if (esLetra(c) || c == '_') {
                int colInicio = columna;
                StringBuilder sb = new StringBuilder();

                while (posicion < entrada.length()) {
                    char actual = entrada.charAt(posicion);
                    if (esLetra(actual) || esDigito(actual) || actual == '_') {
                        sb.append(actual);
                        avanzar();
                    } else {
                        break;
                    }
                }

                String lex = sb.toString();
                TipoToken tipo = clasificarPalabra(lex);
                listaTokens.add(new Token(contadorTokens++, lex, tipo, fila, colInicio));
                continue;
            }

            // Estado de Error Léxico (Carácter no perteneciente al alfabeto del lenguaje)
            listaErrores.add(new ErrorLexico(String.valueOf(c), "Carácter no reconocido", fila, columna));
            avanzar();
        }
    }

    private TipoToken clasificarPalabra(String lex) {
        switch (lex) {
            case "AGENTE": return TipoToken.PR_AGENTE;
            case "contexto": return TipoToken.PR_CONTEXTO;
            case "variable": return TipoToken.PR_VARIABLE;
            case "EJECUTAR": return TipoToken.PR_EJECUTAR;
            case "EXPORTAR": return TipoToken.PR_EXPORTAR;
            case "PREGUNTAR": return TipoToken.CMD_PREGUNTAR;
            case "GENERAR": return TipoToken.CMD_GENERAR;
            case "RESUMIR": return TipoToken.CMD_RESUMIR;
            case "ANALIZAR": return TipoToken.CMD_ANALIZAR;
            case "TRADUCIR": return TipoToken.CMD_TRADUCIR;
            case "CLASIFICAR": return TipoToken.CMD_CLASIFICAR;
            case "EXTRAER": return TipoToken.CMD_EXTRAER;
            case "SOBRE": return TipoToken.CON_SOBRE;
            case "DESDE": return TipoToken.CON_DESDE;
            case "EN": return TipoToken.CON_EN;
            case "COMO": return TipoToken.CON_COMO;
            case "CARGAR": return TipoToken.FUNC_CARGAR;
            default: return TipoToken.IDENTIFICADOR;
        }
    }

    private void avanzar() {
        posicion++;
        columna++;
    }

    private void agregarToken(String lexema, TipoToken tipo, int colInicio) {
        listaTokens.add(new Token(contadorTokens++, lexema, tipo, fila, colInicio));
    }

    private boolean esLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    public List<Token> getListaTokens() { return listaTokens; }
    public List<ErrorLexico> getListaErrores() { return listaErrores; }
}