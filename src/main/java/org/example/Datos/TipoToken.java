package org.example.Datos;

public enum TipoToken {
    // Directivas
    DIRECTIVA_MODELO, DIRECTIVA_ROL, DIRECTIVA_FORMATO,
    // Palabras Reservadas
    PR_AGENTE, PR_CONTEXTO, PR_VARIABLE, PR_EJECUTAR, PR_EXPORTAR,
    // Comandos de IA
    CMD_PREGUNTAR, CMD_GENERAR, CMD_RESUMIR, CMD_ANALIZAR, CMD_TRADUCIR, CMD_CLASIFICAR, CMD_EXTRAER,
    // Conectores
    CON_SOBRE, CON_DESDE, CON_EN, CON_COMO, FLECHA, // ->
    // Identificadores y Literales
    IDENTIFICADOR, CADENA, ENTERO, DECIMAL,
    // Operadores
    IGUAL, MAS,
    // Delimitadores
    LLAVE_IZQ, LLAVE_DER, PAREN_IZQ, PAREN_DER, COMMA,
    // Funciones
    FUNC_CARGAR,
    // Control
    EOF
}