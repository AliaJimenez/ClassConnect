package com.interfazpadre;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ProgresoPadreApp extends JFrame {
    
    private final int idEstudiante = Modelos.Sesion.getIdEstudiante() > 0 ? Modelos.Sesion.getIdEstudiante() : 1;
    private final dao.CalificacionDAO calificacionDAO = new dao.CalificacionDAO();
    private final java.util.Map<String, Double> promedios = calificacionDAO.obtenerPromediosPorMateria(idEstudiante, null);
    private final String nombreEstudiante = obtenerNombre();

    private String obtenerNombre() {
        Modelos.Alumno a = new dao.AlumnoDAO().obtenerPorId(idEstudiante);
        return a != null ? a.getNombre() + " " + a.getApellido() : "Estudiante";
    }

    private double promedioGeneral() {
        return promedios.values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private static String fmt(double v) {
        return String.format(java.util.Locale.US, "%.1f", v);
    }
    
    // Paleta de Colores
    private static final Color BG_COLOR = new Color(240, 244, 248); // Gris azulado muy suave
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(30, 41, 59); // Slate dark
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139); // Slate gray
    private static final Color ACCENT_COLOR = new Color(79, 70, 229); // Indigo vibrante
    // Colores de estado (Notas)
    private static final Color GRADE_EXCELLENT = new Color(16, 185, 129); // Esmeralda
    private static final Color GRADE_GOOD = new Color(245, 158, 11); // Ámbar
    private static final Color GRADE_LOW = new Color(239, 68, 68); // Rojo
    
    public ProgresoPadreApp() {
        setTitle("ClassConnect");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 800));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        
        mainPanel.add(crearHeader(), BorderLayout.NORTH);
        
        JScrollPane scroll = new JScrollPane(crearCuerpo());
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scroll, BorderLayout.CENTER);

        JPanel headerPanel = crearHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 2, 0, new Color(226, 232, 240)),
            new EmptyBorder(20, 30, 20, 30)
        ));
        
        // Títulos
        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Progreso Académico");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(TEXT_PRIMARY);
        
        JLabel lblSub = new JLabel("Año Escolar 2024-2025 • " + nombreEstudiante);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(TEXT_SECONDARY);
        
        titles.add(lblTitulo);
        titles.add(lblSub);
        
        JButton btnVolver = new JButton("← Volver");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVolver.setBackground(new Color(150, 150, 150));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setPreferredSize(new Dimension(120, 45));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnVolver.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnVolver.setBackground(new Color(120, 120, 120));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnVolver.setBackground(new Color(150, 150, 150));
            }
        });
        
        btnVolver.addActionListener(e -> {
            new DashboardPadreApp();
            this.dispose();
        });
        
        header.add(titles, BorderLayout.WEST);
        header.add(btnVolver, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel crearCuerpo() {
        JPanel body = new JPanel(new BorderLayout(0, 30));
        body.setBackground(BG_COLOR);
        body.setBorder(new EmptyBorder(30, 40, 40, 40));
        
        // 1. Sección de Tarjetas de Resumen (KPIs)
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 25, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.setPreferredSize(new Dimension(0, 140));
        
        long aprobadas = promedios.values().stream().filter(v -> v >= 70).count();
        String asistencia = "-", detalle = "Sin registros";
        try {
            int[] r = new dao.AsistenciaDAO().resumenEstudiante(idEstudiante);
            if (r[0] > 0) {
                asistencia = Math.round(r[1] * 100.0 / r[0]) + "%";
                detalle = (r[0] - r[1]) + " falta(s) en " + r[0] + " días";
            }
        } catch (Exception ex) {
            System.out.println("No se pudo cargar la asistencia: " + ex.getMessage());
        }
        kpiPanel.add(crearKpiCard("Promedio General", promedios.isEmpty() ? "-" : fmt(promedioGeneral()), "Promedio del año escolar", ACCENT_COLOR, "📈"));
        kpiPanel.add(crearKpiCard("Materias Aprobadas", aprobadas + "/" + promedios.size(),
                promedios.isEmpty() ? "Sin calificaciones" : Math.round(aprobadas * 100.0 / promedios.size()) + "% aprobado", GRADE_EXCELLENT, "📚"));
        kpiPanel.add(crearKpiCard("Asistencia", asistencia, detalle, new Color(14, 165, 233), "✅")); // Azul cielo
        
        body.add(kpiPanel, BorderLayout.NORTH);
        
        //Sección Principal (Lista de Materias y Panel Lateral)
        JPanel contentGrid = new JPanel(new BorderLayout(30, 0));
        contentGrid.setOpaque(false);
        
        //Lista de Materias --
        JPanel materiasPanel = crearListaMaterias();
        contentGrid.add(materiasPanel, BorderLayout.CENTER);
        
        //Análisis y Recomendaciones --
        JPanel sidePanel = crearPanelLateral();

        // Dimensiones fijas para el panel lateral
        sidePanel.setPreferredSize(new Dimension(380, 0));
        contentGrid.add(sidePanel, BorderLayout.EAST);
        
        body.add(contentGrid, BorderLayout.CENTER);
        
        return body;
    }
    
    private JPanel crearListaMaterias() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Desglose por Asignatura");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        container.add(lblTitle, BorderLayout.NORTH);
        
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        
        if (promedios.isEmpty()) {
            list.add(new JLabel("Aún no hay calificaciones registradas."));
        }
        for (java.util.Map.Entry<String, Double> e : promedios.entrySet()) {
            int nota = (int) Math.round(e.getValue());
            String comentario = nota >= 90 ? "Excelente dominio de la materia"
                    : nota >= 80 ? "Buen desempeño, sigue así"
                    : nota >= 70 ? "Aprobado, puede mejorar con práctica"
                    : "Necesita refuerzo en esta materia";
            list.add(crearFilaMateria(e.getKey(), nota, comentario));
            list.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        
        container.add(list, BorderLayout.CENTER);
        return container;
    }
    
    private JPanel crearFilaMateria(String materia, int nota, String comentario) {
        JPanel row = new JPanel(new BorderLayout(20, 0));
        row.setBackground(CARD_BG);
        row.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true), // Borde sutil
            new EmptyBorder(20, 25, 20, 25)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        //Info Izquierda (Nombre y Comentario)
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 5));
        info.setOpaque(false);
        
        JLabel lblName = new JLabel(materia);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(TEXT_PRIMARY);
        
        JLabel lblComment = new JLabel(comentario);
        lblComment.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblComment.setForeground(TEXT_SECONDARY);
        
        info.add(lblName);
        info.add(lblComment);
        
        //Centro (Barra de Progreso)
        JPanel progressContainer = new JPanel(new BorderLayout(0, 5));
        progressContainer.setOpaque(false);
        progressContainer.setBorder(new EmptyBorder(10, 0, 10, 0)); // Centrar verticalmente
        
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(nota);
        bar.setPreferredSize(new Dimension(200, 10)); // Barra más fina y elegante
        bar.setBorderPainted(false);
        bar.setBackground(new Color(241, 245, 249)); // Fondo de barra muy claro
        
        //Color dinámico
        Color barColor;
        if (nota >= 90) barColor = GRADE_EXCELLENT;
        else if (nota >= 80) barColor = GRADE_GOOD;
        else barColor = GRADE_LOW;
        
        bar.setForeground(barColor);
        
        //Etiqueta pequeña sobre la barra (opcional, aquí solo pongo la barra limpia)
        progressContainer.add(bar, BorderLayout.CENTER);
        
        //Derecha (Nota Grande)
        JPanel gradePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        gradePanel.setOpaque(false);
        gradePanel.setPreferredSize(new Dimension(80, 50));
        
        JLabel lblNota = new JLabel(String.valueOf(nota));
        lblNota.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNota.setForeground(barColor); // La nota tiene el color del estado
        
        gradePanel.add(lblNota);
        
        row.add(info, BorderLayout.WEST);
        row.add(progressContainer, BorderLayout.CENTER);
        row.add(gradePanel, BorderLayout.EAST);
        
        return row;
    }
    
    //RESUMEN SUPERIOR)
    
    private JPanel crearKpiCard(String titulo, String valor, String subtitulo, Color color, String icono) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        
        card.setBorder(new CompoundBorder(
            new MatteBorder(0, 5, 0, 0, color),
            new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 25, 20, 25)
            )
        ));
        
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblIcon = new JLabel(icono);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel lblTitle = new JLabel(titulo.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(TEXT_SECONDARY);
        
        top.add(lblTitle, BorderLayout.CENTER);
        top.add(lblIcon, BorderLayout.EAST);
        
        JLabel lblValue = new JLabel(valor);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(TEXT_PRIMARY);
        lblValue.setBorder(new EmptyBorder(10, 0, 5, 0));

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSub.setForeground(color); // Color del tema
        
        card.add(top, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);
        
        return card;
    }
    
    
    private JPanel crearPanelLateral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // Card de Análisis
        JPanel cardAnalysis = new JPanel(new BorderLayout());
        cardAnalysis.setBackground(CARD_BG);
        cardAnalysis.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel lblTitle = new JLabel("Análisis Pedagógico");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JEditorPane txtAnalysis = new JEditorPane();
        txtAnalysis.setContentType("text/html");
        txtAnalysis.setEditable(false);
        txtAnalysis.setOpaque(false);
        Modelos.Calificacion mejor = calificacionDAO.obtenerMejorMateria(idEstudiante, null);
        Modelos.Calificacion peor = calificacionDAO.obtenerPeorMateria(idEstudiante, null);
        String primerNombre = nombreEstudiante.split(" ")[0];
        String analisis = promedios.isEmpty()
            ? "<p>Todavía no hay calificaciones suficientes para generar un análisis.</p>"
            : "<p><b>FORTALEZAS:</b><br>" + primerNombre + " obtiene su mejor resultado en <b>" + mejor.getNombreAsignatura()
                + "</b> con un promedio de <b>" + fmt(mejor.getCalificacion()) + "</b>.</p><br>"
              + "<p><b>ÁREAS DE ENFOQUE:</b><br>Se recomienda reforzar <b>" + peor.getNombreAsignatura()
                + "</b> (promedio " + fmt(peor.getCalificacion()) + ").</p><br>"
              + "<p><b>RESUMEN:</b><br>Promedio general de <b>" + fmt(promedioGeneral()) + "</b> en "
                + promedios.size() + " materias.</p>";
        txtAnalysis.setText("<html><body style='font-family: Segoe UI; color: #475569; font-size: 11px;'>" + analisis + "</body></html>");
        
        cardAnalysis.add(lblTitle, BorderLayout.NORTH);
        cardAnalysis.add(txtAnalysis, BorderLayout.CENTER);
        
        // Botón Descargar PDF
        JButton btnDownload = new JButton("Descargar Boletín");
        estilizarBoton(btnDownload, ACCENT_COLOR, Color.WHITE);
        btnDownload.addActionListener(e -> descargarBoletin());
        
        JPanel btnWrapper = new JPanel(new BorderLayout());
        btnWrapper.setOpaque(false);
        btnWrapper.setBorder(new EmptyBorder(20, 0, 0, 0));
        btnWrapper.add(btnDownload, BorderLayout.CENTER);
        cardAnalysis.add(btnWrapper, BorderLayout.SOUTH);
        
        panel.add(cardAnalysis);
        
        return panel;
    }
    
    // Helper para botones
    private void estilizarBoton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
    }

    /** Guarda un boletín de calificaciones en texto plano. */
    private void descargarBoletin() {
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new java.io.File("boletin_" + nombreEstudiante.replace(' ', '_') + ".txt"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (java.io.PrintWriter out = new java.io.PrintWriter(selector.getSelectedFile(), "UTF-8")) {
            out.println("BOLETÍN DE CALIFICACIONES — ClassConnect");
            out.println("Estudiante: " + nombreEstudiante);
            out.println("Fecha: " + java.time.LocalDate.now());
            out.println();
            promedios.forEach((materia, nota) -> out.printf("%-25s %6s%n", materia, fmt(nota)));
            out.println();
            out.println("Promedio general: " + fmt(promedioGeneral()));
            JOptionPane.showMessageDialog(this, "Boletín guardado en:\n" + selector.getSelectedFile().getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el boletín: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(ProgresoPadreApp::new);
    }
}