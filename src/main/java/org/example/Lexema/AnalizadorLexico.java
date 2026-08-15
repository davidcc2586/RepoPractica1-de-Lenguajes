package org.example.Lexema;

import org.example.Datos.ErrorLexico;
import org.example.Datos.TipoToken;
import org.example.Datos.Token;

import java.util.*;

public class AnalizadorLexico {
    private String entrada;
    private int posicion;
    private int fila;
    private int columna;
    private int contadorTokens;

    private List<Token> listaTokens;
    private List<ErrorLexico> listaErrores;

    public AnalizadorLexico(String entrada) {
        this.entrada = entrada;
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

            if (c == '\n') {
                fila++;
                columna = 1;
                posicion++;
                continue;
            } else if (c == '\r' || c == '\t' || c == ' ') {
                avanzar();
                continue;
            }

            // Comentarios
            if (c == '/' && posicion + 1 < entrada.length()) {
                char siguiente = entrada.charAt(posicion + 1);
                if (siguiente == '/') {
                    while (posicion < entrada.length() && entrada.charAt(posicion) != '\n') {
                        posicion++;
                    }
                    continue;
                } else if (siguiente == '*') {
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

            // 3. Delimitadores
            if (c == '{') { agregarToken("{", TipoToken.LLAVE_IZQ); avanzar(); continue; }
            if (c == '}') { agregarToken("}", TipoToken.LLAVE_DER); avanzar(); continue; }
            if (c == '(') { agregarToken("(", TipoToken.PAREN_IZQ); avanzar(); continue; }
            if (c == ')') { agregarToken(")", TipoToken.PAREN_DER); avanzar(); continue; }
            if (c == ',') { agregarToken(",", TipoToken.COMMA); avanzar(); continue; }
            if (c == '=') { agregarToken("=", TipoToken.IGUAL); avanzar(); continue; }
            if (c == '+') { agregarToken("+", TipoToken.MAS); avanzar(); continue; }

            // Conector
            if (c == '-' && posicion + 1 < entrada.length() && entrada.charAt(posicion + 1) == '>') {
                int colInicio = columna;
                posicion += 2;
                columna += 2;
                listaTokens.add(new Token(contadorTokens++, "->", TipoToken.FLECHA, fila, colInicio));
                continue;
            }

            //Directivas (@modelo, @rol, @formato)
            if (c == '@') {
                reconocerDirectiva();
                continue;
            }

            // Cadenas entre comillas dobles
            if (c == '"') {
                reconocerCadena();
                continue;
            }

            // Números (Enteros y Decimales)
            if (EsDigito(c)) {
                reconocerNumero();
                continue;
            }

            // Identificadores, Palabras Reservadas, Comandos, Conectores y Funciones
            if (EsLetra(c) || c == '_') {
                reconocerPalabraClaveOIdentificador();
                continue;
            }

            // Si nada de lo anterior coincide -> ERROR LÉXICO
            listaErrores.add(new ErrorLexico(String.valueOf(c), "Carácter no reconocido", fila, columna));
            avanzar();
        }
    }

    private void reconocerDirectiva() {
        int colInicio = columna;
        StringBuilder sb = new StringBuilder();
        sb.append(entrada.charAt(posicion)); // '@'
        avanzar();

        while (posicion < entrada.length() && EsLetra(entrada.charAt(posicion))) {
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
    }

    private void reconocerCadena() {
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
    }

    private void reconocerNumero() {
        int colInicio = columna;
        StringBuilder sb = new StringBuilder();
        boolean tienePunto = false;

        while (posicion < entrada.length()) {
            char c = entrada.charAt(posicion);
            if (EsDigito(c)) {
                sb.append(c);
                avanzar();
            } else if (c == '.' && !tienePunto) {
                // Verificar si después del punto hay un dígito
                if (posicion + 1 < entrada.length() && EsDigito(entrada.charAt(posicion + 1))) {
                    tienePunto = true;
                    sb.append(c);
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
    }

    private void reconocerPalabraClaveOIdentificador() {
        int colInicio = columna;
        StringBuilder sb = new StringBuilder();

        while (posicion < entrada.length()) {
            char c = entrada.charAt(posicion);
            if (EsLetra(c) || EsDigito(c) || c == '_') {
                sb.append(c);
                avanzar();
            } else {
                break;
            }
        }

        String lex = sb.toString();
        TipoToken tipo = clasificarPalabra(lex);
        listaTokens.add(new Token(contadorTokens++, lex, tipo, fila, colInicio));
    }

    private TipoToken clasificarPalabra(String lex) {
        switch (lex) {
            // Palabras reservadas
            case "AGENTE": return TipoToken.PR_AGENTE;
            case "contexto": return TipoToken.PR_CONTEXTO;
            case "variable": return TipoToken.PR_VARIABLE;
            case "EJECUTAR": return TipoToken.PR_EJECUTAR;
            case "EXPORTAR": return TipoToken.PR_EXPORTAR;
            // Comandos de IA
            case "PREGUNTAR": return TipoToken.CMD_PREGUNTAR;
            case "GENERAR": return TipoToken.CMD_GENERAR;
            case "RESUMIR": return TipoToken.CMD_RESUMIR;
            case "ANALIZAR": return TipoToken.CMD_ANALIZAR;
            case "TRADUCIR": return TipoToken.CMD_TRADUCIR;
            case "CLASIFICAR": return TipoToken.CMD_CLASIFICAR;
            case "EXTRAER": return TipoToken.CMD_EXTRAER;
            // Conectores
            case "SOBRE": return TipoToken.CON_SOBRE;
            case "DESDE": return TipoToken.CON_DESDE;
            case "EN": return TipoToken.CON_EN;
            case "COMO": return TipoToken.CON_COMO;
            // Funciones
            case "CARGAR": return TipoToken.FUNC_CARGAR;
            // Si no coincide con nada, es un identificador
            default: return TipoToken.IDENTIFICADOR;
        }
    }

    private void avanzar() {
        posicion++;
        columna++;
    }

    private void agregarToken(String lexema, TipoToken tipo) {
        listaTokens.add(new Token(contadorTokens++, lexema, tipo, fila, columna));
    }

    private boolean EsLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean EsDigito(char c) {
        return c >= '0' && c <= '9';
    }

    public List<Token> getListaTokens() { return listaTokens; }
    public List<ErrorLexico> getListaErrores() { return listaErrores; }
}
