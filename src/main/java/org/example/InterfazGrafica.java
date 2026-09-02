package org.example;

import org.example.Datos.ErrorLexico;
import org.example.Datos.Token;
import org.example.Lexema.AnalizadorLexico;
import org.example.Reoirtes.GeneradorAFD;
import org.example.Reoirtes.GeneradorReportes;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;

public class InterfazGrafica extends JFrame {

    private JTextArea editorTexto;
    private JTable tablaTokens;
    private JTable tablaErrores;
    private DefaultTableModel modeloTokens;
    private DefaultTableModel modeloErrores;
    private JLabel labelEstado;

    private AnalizadorLexico lexerUltimo;
    private int totalLineasCodigo = 0;

    public InterfazGrafica() {
        setTitle("IDE PromptZal - Analizador Léxico Profesional");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponentes();
    }

    private void initComponentes() {
        // --- MENÚ SUPERIOR ---
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemAbrir = new JMenuItem("Abrir (.pz)");
        JMenuItem itemGuardar = new JMenuItem("Guardar (.pz)");

        itemAbrir.addActionListener(e -> abrirArchivo());
        itemGuardar.addActionListener(e -> guardarArchivo());

        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);

        // --- PANEL PRINCIPAL DIVISION (EDITOR Y TABLAS) ---
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPrincipal.setResizeWeight(0.5);

        // Editor de Código
        editorTexto = new JTextArea();
        editorTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollEditor = new JScrollPane(editorTexto);
        scrollEditor.setBorder(BorderFactory.createTitledBorder("Editor de Código PromptZal (.pz)"));

        // Tablas de Salida
        JTabbedPane pestañasResultados = new JTabbedPane();

        modeloTokens = new DefaultTableModel(new Object[]{"#", "Lexema", "Tipo Token", "Fila", "Columna"}, 0);
        tablaTokens = new JTable(modeloTokens);
        pestañasResultados.addTab("Tokens Reconocidos", new JScrollPane(tablaTokens));

        modeloErrores = new DefaultTableModel(new Object[]{"#", "Lexema / Carácter", "Descripción", "Fila", "Columna"}, 0);
        tablaErrores = new JTable(modeloErrores);
        pestañasResultados.addTab("Errores Léxicos", new JScrollPane(tablaErrores));

        splitPrincipal.setTopComponent(scrollEditor);
        splitPrincipal.setBottomComponent(pestañasResultados);

        // --- BARRA DE HERRAMIENTAS Y BOTONES ---
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnAnalizar = new JButton("▶ Analizar Código");
        JButton btnReportes = new JButton("📊 Exportar Reportes HTML");
        JButton btnVerAFD = new JButton("🕸 Generar & Ver AFD");

        btnAnalizar.addActionListener(e -> ejecutarAnalisis());
        btnReportes.addActionListener(e -> exportarReportes());
        btnVerAFD.addActionListener(e -> generarYMostrarAFD());

        toolBar.add(btnAnalizar);
        toolBar.addSeparator();
        toolBar.add(btnReportes);
        toolBar.add(btnVerAFD);

        // --- BARRA DE ESTADO ---
        labelEstado = new JLabel(" Estado: Listo.");
        labelEstado.setBorder(BorderFactory.createEtchedBorder());

        add(toolBar, BorderLayout.NORTH);
        add(splitPrincipal, BorderLayout.CENTER);
        add(labelEstado, BorderLayout.SOUTH);
    }

    private void abrirArchivo() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                String contenido = new String(Files.readAllBytes(f.toPath()));
                editorTexto.setText(contenido);
                labelEstado.setText(" Archivo cargado: " + f.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir el archivo: " + ex.getMessage());
            }
        }
    }

    private void guardarArchivo() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (!f.getName().endsWith(".pz")) {
                f = new File(f.getAbsolutePath() + ".pz");
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                bw.write(editorTexto.getText());
                labelEstado.setText(" Archivo guardado en: " + f.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
            }
        }
    }

    private void ejecutarAnalisis() {
        String texto = editorTexto.getText();
        if (texto.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El editor está vacío.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        lexerUltimo = new AnalizadorLexico(texto);
        lexerUltimo.analizar();

        // Calcular total de líneas
        totalLineasCodigo = texto.split("\r\n|\r|\n").length;

        // Limpiar y Llenar Tabla Tokens
        modeloTokens.setRowCount(0);
        for (Token t : lexerUltimo.getListaTokens()) {
            modeloTokens.addRow(new Object[]{t.getId(), t.getLexema(), t.getTipo(), t.getFila(), t.getColumna()});
        }

        // Limpiar y Llenar Tabla Errores
        modeloErrores.setRowCount(0);
        int i = 1;
        for (ErrorLexico err : lexerUltimo.getListaErrores()) {
            modeloErrores.addRow(new Object[]{i++, err.getLexema(), err.getDescripcion(), err.getFila(), err.getColumna()});
        }

        labelEstado.setText(String.format(" Análisis completado. Tokens: %d | Errores: %d", lexerUltimo.getListaTokens().size(), lexerUltimo.getListaErrores().size()));
    }

    private void exportarReportes() {
        if (lexerUltimo == null) {
            JOptionPane.showMessageDialog(this, "Primero debe ejecutar el análisis.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccione la carpeta y nombre base para guardar reportes");
        chooser.setSelectedFile(new File("reporte_promptzal"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String base = chooser.getSelectedFile().getAbsolutePath();
            if (base.endsWith(".html")) base = base.substring(0, base.length() - 5);

            GeneradorReportes.generarReporteTokensHTML(lexerUltimo.getListaTokens(), base + "_tokens.html");
            GeneradorReportes.generarReporteErroresHTML(lexerUltimo.getListaErrores(), base + "_errores.html");
            GeneradorReportes.generarReporteEstadisticasHTML(lexerUltimo.getListaTokens(), lexerUltimo.getListaErrores(), totalLineasCodigo, base + "_estadisticas.html");

            JOptionPane.showMessageDialog(this, "Se han generado exitosamente los 3 reportes HTML.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void generarYMostrarAFD() {
        String rutaDot = "afd.dot";
        String rutaPng = "afd.png";

        boolean exito = GeneradorAFD.exportarImagenAFD(rutaDot, rutaPng);
        if (exito) {
            JFrame frameImagen = new JFrame("Grafo del AFD - PromptZal");
            frameImagen.setSize(800, 600);
            frameImagen.setLocationRelativeTo(this);
            JLabel labelImg = new JLabel(new ImageIcon(rutaPng));
            frameImagen.add(new JScrollPane(labelImg));
            frameImagen.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo generar la imagen con Graphviz.\nAsegúrese de tener instalado 'dot' en el PATH del sistema.", "Error Graphviz", JOptionPane.ERROR_MESSAGE);
        }
    }
}