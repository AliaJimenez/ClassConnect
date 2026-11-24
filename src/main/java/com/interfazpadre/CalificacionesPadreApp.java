package com.interfazpadre;

import dao.AlumnoDAO;
import dao.CalificacionDAO;
import Modelos.Alumno;
import Modelos.Calificacion;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


public class CalificacionesPadreApp extends JFrame {
    
    // ID del estudiante (en producción vendría de login)
    private int idEstudianteActual = Modelos.Sesion.getIdEstudiante() > 0 ? Modelos.Sesion.getIdEstudiante() : 1;
    
    private JTable tablaCalificaciones;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbPeriodo;
    private JComboBox<String> cmbMateria;
    private JLabel lblPromedioGeneral, lblMejorMateria, lblPeorMateria, lblEstadoGeneral;
    
    // DAOs
    private CalificacionDAO calificacionDAO;
    private AlumnoDAO alumnoDAO;
    
    // Datos del estudiante
    private Alumno estudiante;
    
    // Colores del sistema
    private static final Color PRIMARY_COLOR = new Color(25, 25, 112);
    private static final Color SECONDARY_COLOR = new Color(70, 130, 180);
    private static final Color SUCCESS_COLOR = new Color(60, 179, 113);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 50);
    private static final Color INFO_COLOR = new Color(147, 112, 219);
    private static final Color LIGHT_BG = new Color(248, 249, 250);
    
    public CalificacionesPadreApp() throws Exception {
        // Inicializar DAOs
        calificacionDAO = new CalificacionDAO();
        alumnoDAO = new AlumnoDAO();
        
        // Cargar datos del estudiante
        cargarDatosEstudiante();
        
        setTitle("ClassConnect - Calificaciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(240, 242, 245);
                Color color2 = new Color(255, 255, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        
        // HEADER
        JPanel headerPanel = crearHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // CONTENIDO
        JPanel contentPanel = crearContenido();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        setVisible(true);
        
        // Cargar calificaciones iniciales
        cargarCalificaciones();
    }
    
    private void cargarDatosEstudiante() {
        estudiante = alumnoDAO.obtenerPorId(idEstudianteActual);
        if (estudiante == null) {
            JOptionPane.showMessageDialog(this,
                "No se pudo cargar la información del estudiante",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, SECONDARY_COLOR),
            new EmptyBorder(25, 40, 25, 40)
        ));
        
        // Título con nombre real del estudiante
        String nombreEstudiante = estudiante != null ? estudiante.getNombreCompleto() : "Estudiante";
        JLabel lblTitulo = new JLabel("Calificaciones de " + nombreEstudiante);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(PRIMARY_COLOR);
        
        // Panel de botones derecho
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        botonesPanel.setBackground(Color.WHITE);
        
        // Botón Actualizar
        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnActualizar.setBackground(SECONDARY_COLOR);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setBorderPainted(false);
        btnActualizar.setPreferredSize(new Dimension(140, 45));
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> {
            try {
                cargarCalificaciones();
            } catch (Exception ex) {
                System.getLogger(CalificacionesPadreApp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
        
        // Botón Volver
        JButton btnVolver = new JButton("← Volver");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVolver.setBackground(new Color(150, 150, 150));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setPreferredSize(new Dimension(120, 45));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> {
            // Aquí iría la navegación al dashboard
            this.dispose();
        });
        
        botonesPanel.add(btnActualizar);
        botonesPanel.add(btnVolver);
        
        header.add(lblTitulo, BorderLayout.WEST);
        header.add(botonesPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel crearContenido() {
        JPanel content = new JPanel(new BorderLayout(0, 25));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 40, 40, 40));
        
        // Panel de controles superiores
        JPanel controlesPanel = crearPanelControles();
        content.add(controlesPanel, BorderLayout.NORTH);
        
        // Panel dividido (estadísticas y tabla)
        JSplitPane splitPane = crearSplitPane();
        content.add(splitPane, BorderLayout.CENTER);
        
        return content;
    }
    
    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        panel.setOpaque(false);
        
        // Selector de Período
        JPanel periodoPanel = new JPanel(new BorderLayout(10, 0));
        periodoPanel.setOpaque(false);
        
        JLabel lblPeriodo = new JLabel("Período Académico:");
        lblPeriodo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPeriodo.setForeground(new Color(70, 70, 70));
        
        String[] periodos = {
            "Todo el año",
            "1er trimestre",
            "2do trimestre", 
            "3er trimestre"
        };
        cmbPeriodo = new JComboBox<>(periodos);
        cmbPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbPeriodo.setPreferredSize(new Dimension(200, 45));
        cmbPeriodo.setBackground(Color.WHITE);
        cmbPeriodo.addActionListener(e -> {
            try {
                cargarCalificaciones();
            } catch (Exception ex) {
                System.getLogger(CalificacionesPadreApp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
        
        periodoPanel.add(lblPeriodo, BorderLayout.WEST);
        periodoPanel.add(cmbPeriodo, BorderLayout.CENTER);
        
        panel.add(periodoPanel);
        
        return panel;
    }
    
    private JSplitPane crearSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        
        // Panel superior: Estadísticas
        JPanel panelStats = crearPanelEstadisticas();
        splitPane.setTopComponent(panelStats);
        
        // Panel inferior: Tabla de calificaciones
        JPanel panelTabla = crearPanelTabla();
        splitPane.setBottomComponent(panelTabla);
        
        return splitPane;
    }
    
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 25, 0));
        
        Object[] promedioData = crearStatCard("Promedio General", "0.0/100", SECONDARY_COLOR, "Cargando...");
        JPanel promedioPanel = (JPanel) promedioData[0];
        lblPromedioGeneral = (JLabel) promedioData[1];
        
        Object[] mejorData = crearStatCard("Mejor Materia", "---", SUCCESS_COLOR, "0.0");
        JPanel mejorPanel = (JPanel) mejorData[0];
        lblMejorMateria = (JLabel) mejorData[1];
        
        Object[] peorData = crearStatCard("Necesita Mejorar", "---", WARNING_COLOR, "0.0");
        JPanel peorPanel = (JPanel) peorData[0];
        lblPeorMateria = (JLabel) peorData[1];
        
        Object[] estadoData = crearStatCard("Estado General", "...", INFO_COLOR, "Cargando");
        JPanel estadoPanel = (JPanel) estadoData[0];
        lblEstadoGeneral = (JLabel) estadoData[1];
        
        panel.add(promedioPanel);
        panel.add(mejorPanel);
        panel.add(peorPanel);
        panel.add(estadoPanel);
        
        return panel;
    }
    
    private Object[] crearStatCard(String titulo, String valor, Color color, String subtitulo) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color.brighter().brighter());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2, true),
            new EmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(80, 80, 80));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValor.setForeground(color.darker().darker());
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(100, 100, 100));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(lblTitulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblValor);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblSubtitulo);
        
        return new Object[]{card, lblValor};
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel("Detalle de Calificaciones por Materia");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(PRIMARY_COLOR);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        JPanel tablaPanel = crearTablaCalificaciones();
        panel.add(tablaPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearTablaCalificaciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true));

        // Modelo de tabla
        String[] columnas = {"Materia", "Promedio", "Estado", "Observación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Crear tabla
        tablaCalificaciones = new JTable(modeloTabla);
        tablaCalificaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaCalificaciones.setRowHeight(50);
        tablaCalificaciones.setSelectionBackground(new Color(230, 240, 255));
        tablaCalificaciones.setShowVerticalLines(false);
        tablaCalificaciones.setGridColor(new Color(230, 230, 230));

        JTableHeader header = tablaCalificaciones.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));

        // Anchos de columna
        tablaCalificaciones.getColumnModel().getColumn(0).setPreferredWidth(200);
        tablaCalificaciones.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaCalificaciones.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaCalificaciones.getColumnModel().getColumn(3).setPreferredWidth(350);

        JScrollPane scrollPane = new JScrollPane(tablaCalificaciones);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    /**
     * CARGAR CALIFICACIONES DESDE LA BASE DE DATOS
     */
    private void cargarCalificaciones() throws Exception {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        // Obtener período seleccionado
        String periodoSeleccionado = (String) cmbPeriodo.getSelectedItem();
        String periodo = periodoSeleccionado.equals("Todo el año") ? null : periodoSeleccionado;
        
        // Obtener calificaciones
        List<Calificacion> calificaciones = calificacionDAO.obtenerPorEstudiante(idEstudianteActual);
        
        // Agrupar por materia y calcular promedios
        java.util.Map<String, java.util.List<Calificacion>> porMateria = new java.util.HashMap<>();
        for (Calificacion c : calificaciones) {
            if (periodo == null || periodo.equals(c.getPeriodo())) {
                porMateria.computeIfAbsent(c.getNombreAsignatura(), k -> new java.util.ArrayList<>()).add(c);
            }
        }
        
        // Llenar tabla con promedios por materia
        for (String materia : porMateria.keySet()) {
            List<Calificacion> notas = porMateria.get(materia);
            double promedio = notas.stream().mapToDouble(Calificacion::getCalificacion).average().orElse(0.0);
            String observacion = notas.get(notas.size() - 1).getObservacion();
            String estado = promedio >= 90 ? "Excelente" : promedio >= 80 ? "Bueno" : "Regular";
            
            modeloTabla.addRow(new Object[]{
                materia,
                String.format("%.1f", promedio),
                estado,
                observacion != null ? observacion : "Sin observaciones"
            });
        }
        
        // Actualizar estadísticas
        actualizarEstadisticas(periodo);
    }
    
    /**
     * ACTUALIZAR ESTADÍSTICAS
     */
    private void actualizarEstadisticas(String periodo) throws Exception {
        // Promedio general
        double promedio = calificacionDAO.obtenerPromedio(idEstudianteActual, periodo);
        lblPromedioGeneral.setText(String.format("%.1f/100", promedio));
        
        // Mejor materia
        Calificacion mejor = calificacionDAO.obtenerMejorMateria(idEstudianteActual, periodo);
        if (mejor != null) {
            lblMejorMateria.setText(mejor.getNombreAsignatura());
        }
        
        // Peor materia
        Calificacion peor = calificacionDAO.obtenerPeorMateria(idEstudianteActual, periodo);
        if (peor != null) {
            lblPeorMateria.setText(peor.getNombreAsignatura());
        }
        
        // Estado general
        String estado = promedio >= 90 ? "Excelente" : promedio >= 80 ? "Bueno" : "Regular";
        lblEstadoGeneral.setText(estado);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                new CalificacionesPadreApp();
            } catch (Exception ex) {
                System.getLogger(CalificacionesPadreApp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
    }
}