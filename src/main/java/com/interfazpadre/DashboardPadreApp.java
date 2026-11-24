package com.interfazpadre;

import com.interfazmaestro.LoginApp;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class DashboardPadreApp extends JFrame {
    
    private final int idEstudiante = Modelos.Sesion.getIdEstudiante() > 0 ? Modelos.Sesion.getIdEstudiante() : 1;
    private String nombrePadre = Modelos.Sesion.getNombre("Padre/Madre");
    private String nombreEstudiante = obtenerNombreEstudiante();

    private String obtenerCurso() {
        Modelos.Alumno a = new dao.AlumnoDAO().obtenerPorId(idEstudiante);
        return a != null ? a.getGrado() + " - Sec. " + a.getSeccion() : "";
    }

    private String obtenerNombreEstudiante() {
        Modelos.Alumno a = new dao.AlumnoDAO().obtenerPorId(idEstudiante);
        return a != null ? a.getNombre() + " " + a.getApellido() : "Estudiante";
    }
    
    // Colores del sistema
    private static final Color PRIMARY_COLOR = new Color(93, 50, 165);
    private static final Color SECONDARY_COLOR = new Color(70, 130, 180);
    private static final Color SUCCESS_COLOR = new Color(60, 179, 113);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color INFO_COLOR = new Color(147, 112, 219);
    private static final Color LIGHT_BG = new Color(248, 249, 250);

    private static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 235, 240), 1, true),
            new EmptyBorder(25, 25, 25, 25)
    );
    
    public DashboardPadreApp() {
        setTitle("ClassConnect");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1400, 800));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(LIGHT_BG);
        
        // Panel Lateral (Sidebar)
        JPanel sidebarPanel = crearSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Área de Contenido
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(LIGHT_BG);
        
        JPanel headerPanel = crearHeader();
        contentArea.add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = crearContenido();
        contentArea.add(contentPanel, BorderLayout.CENTER);
        
        mainPanel.add(contentArea, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        setVisible(true);
    }
    
    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(PRIMARY_COLOR);
        sidebar.setPreferredSize(new Dimension(320, 0)); // Un poco más ancho para que quepa bien la info
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));
        
        // --- 1. Logo ---
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(PRIMARY_COLOR);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblLogo = new JLabel("👨‍👩‍👧");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setForeground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel("ClassConnect");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(lblLogo);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        logoPanel.add(lblTitulo);
        
        sidebar.add(logoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // --- 2. TARJETA DEL ESTUDIANTE (Requested) ---
        JPanel studentProfileCard = new JPanel();
        studentProfileCard.setLayout(new BoxLayout(studentProfileCard, BoxLayout.Y_AXIS));
        studentProfileCard.setBackground(new Color(255, 255, 255, 30)); // Translúcido
        studentProfileCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 255, 255, 80), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        studentProfileCard.setMaximumSize(new Dimension(280, 200));
        studentProfileCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblEstName = new JLabel(nombreEstudiante);
        lblEstName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblEstName.setForeground(Color.WHITE);
        lblEstName.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblGrado = new JLabel(obtenerCurso());
        lblGrado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblGrado.setForeground(new Color(220, 220, 220));
        lblGrado.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton btnVerPerfil = new JButton("Ver Perfil Completo");
        btnVerPerfil.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnVerPerfil.setBackground(Color.WHITE);
        btnVerPerfil.setForeground(PRIMARY_COLOR);
        btnVerPerfil.setFocusPainted(false);
        btnVerPerfil.setBorderPainted(false);
        btnVerPerfil.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerPerfil.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVerPerfil.setMaximumSize(new Dimension(200, 30));
        btnVerPerfil.addActionListener(e -> abrirPerfil());
        
        studentProfileCard.add(Box.createRigidArea(new Dimension(0, 5)));
        studentProfileCard.add(lblEstName);
        studentProfileCard.add(lblGrado);
        studentProfileCard.add(Box.createRigidArea(new Dimension(0, 10)));
        studentProfileCard.add(btnVerPerfil);
        
        sidebar.add(studentProfileCard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        sidebar.add(crearSeccionTitulo("PRÓXIMOS EVENTOS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("EEEE d", new java.util.Locale("es", "DO"));
        try {
            java.util.List<dao.EventoDAO.Evento> eventos = new dao.EventoDAO().proximos(idEstudiante, 4);
            if (eventos.isEmpty()) {
                sidebar.add(crearItemAgenda("—", "Sin eventos próximos", false));
            }
            for (dao.EventoDAO.Evento ev : eventos) {
                String dia = ev.fecha.format(fmt);
                dia = Character.toUpperCase(dia.charAt(0)) + dia.substring(1);
                sidebar.add(crearItemAgenda(dia, ev.titulo, "examen".equals(ev.tipo) || "reunión".equals(ev.tipo)));
                sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        } catch (Exception ex) {
            sidebar.add(crearItemAgenda("—", "No se pudo cargar la agenda", false));
        }

        sidebar.add(Box.createVerticalGlue());
        
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(new Color(0, 0, 0, 50)); 
        profilePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        profilePanel.setMaximumSize(new Dimension(280, 110));
        
        JLabel lblNombrePadre = new JLabel(nombrePadre);
        lblNombrePadre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombrePadre.setForeground(Color.WHITE);
        lblNombrePadre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRel = new JLabel("Padre/Tutor");
        lblRel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRel.setForeground(new Color(200, 200, 220));
        lblRel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(DANGER_COLOR);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> cerrarSesion());
        
        profilePanel.add(lblNombrePadre);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        profilePanel.add(lblRel);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        profilePanel.add(btnLogout);

        sidebar.add(profilePanel);
        
        return sidebar;
    }
    
    //HELPERS VISUALES PARA EL SIDEBAR --
    
    private JLabel crearSeccionTitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(200, 200, 230)); // Lila claro
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }
    
    private JPanel crearItemAgenda(String fecha, String evento, boolean importante) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(280, 45));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // Icono o barra lateral
        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(4, 40));
        bar.setBackground(importante ? WARNING_COLOR : SUCCESS_COLOR);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblFecha.setForeground(Color.WHITE);
        
        JLabel lblEvento = new JLabel(evento);
        lblEvento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEvento.setForeground(new Color(220, 220, 220));
        
        textPanel.add(lblFecha);
        textPanel.add(lblEvento);
        
        panel.add(bar, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(25, 40, 25, 40));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel("Bienvenido, " + nombrePadre);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(33, 33, 33));
        
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new java.util.Locale("es", "DO"));
        JLabel lblFecha = new JLabel(sdf.format(new Date()));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFecha.setForeground(new Color(117, 117, 117));
        
        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setBackground(Color.WHITE);
        titleGroup.add(lblTitulo);
        titleGroup.add(Box.createRigidArea(new Dimension(0, 5)));
        titleGroup.add(lblFecha);

        leftPanel.add(titleGroup);
        header.add(leftPanel, BorderLayout.WEST);
        
        return header;
    }
    
    private JPanel crearContenido() {
        JPanel content = new JPanel(new BorderLayout(0, 25));
        content.setBackground(LIGHT_BG);
        content.setBorder(new EmptyBorder(25, 40, 40, 40));

        JPanel statsPanel = crearEstadisticasRapidas();
        content.add(statsPanel, BorderLayout.NORTH);

        JPanel gridPanel = crearGridAcciones();
        content.add(gridPanel, BorderLayout.CENTER);

        return content;
    }
    
    private JPanel crearEstadisticasRapidas() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);
        
        dao.CalificacionDAO calificacionDAO = new dao.CalificacionDAO();
        java.util.Map<String, Double> promedios = calificacionDAO.obtenerPromediosPorMateria(idEstudiante, null);
        double promedio = promedios.values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
        long aprobadas = promedios.values().stream().filter(v -> v >= 70).count();
        String asistencia = "-", detalleAsistencia = "Sin registros";
        int eventos = 0;
        try {
            int[] r = new dao.AsistenciaDAO().resumenEstudiante(idEstudiante);
            if (r[0] > 0) {
                asistencia = Math.round(r[1] * 100.0 / r[0]) + "%";
                detalleAsistencia = r[1] + " de " + r[0] + " días";
            }
            eventos = new dao.EventoDAO().proximos(idEstudiante, 50).size();
        } catch (Exception ex) {
            System.out.println("No se pudieron cargar las estadísticas: " + ex.getMessage());
        }
        String nivel = promedio >= 90 ? "Excelente rendimiento" : promedio >= 80 ? "Buen rendimiento"
                : promedio >= 70 ? "Rendimiento aceptable" : "Necesita apoyo";
        panel.add(crearStatCard("📊", "Promedio General", promedios.isEmpty() ? "-" : String.format(java.util.Locale.US, "%.1f", promedio),
                promedios.isEmpty() ? "Sin calificaciones" : nivel, SUCCESS_COLOR));
        panel.add(crearStatCard("✅", "Asistencia", asistencia, detalleAsistencia, INFO_COLOR));
        panel.add(crearStatCard("📚", "Materias Aprobadas", aprobadas + "/" + promedios.size(),
                aprobadas == promedios.size() ? "Todas al día" : "Revisar materias pendientes", SUCCESS_COLOR));
        panel.add(crearStatCard("📨", "Próximos Eventos", String.valueOf(eventos), "En la agenda escolar", WARNING_COLOR));
        
        return panel;
    }
    
    private JPanel crearStatCard(String icono, String titulo, String valor, String subtitulo, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(CARD_BORDER);

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setBackground(Color.WHITE);

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel colorIndicator = new JPanel();
        colorIndicator.setBackground(color);
        colorIndicator.setPreferredSize(new Dimension(6, 50));
        colorIndicator.setBorder(new EmptyBorder(6, 0, 6, 0));

        headerRow.add(lblIcono, BorderLayout.CENTER);
        headerRow.add(colorIndicator, BorderLayout.WEST);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(new Color(117, 117, 117));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValor.setForeground(new Color(33, 33, 33));
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitulo.setForeground(new Color(150, 150, 150));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(headerRow);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(lblTitulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblValor);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblSubtitulo);
        
        // Hover
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 251, 252));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });
        
        return card;
    }
    
    private JPanel crearGridAcciones() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setOpaque(false);
        
        panel.add(crearActionCard("📄", "Calificaciones", "Ver notas y promedios del estudiante", new Color(59, 130, 246), () -> {
            try {
                abrirCalificaciones();
            } catch (Exception ex) {
                System.getLogger(DashboardPadreApp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }));
        panel.add(crearActionCard("✓", "Asistencia", "Revisar asistencia y justificar faltas", new Color(16, 185, 129), () -> abrirAsistencia()));
        panel.add(crearActionCard("📈", "Progreso Académico", "Ver evolución y estadísticas", new Color(139, 92, 246), () -> abrirProgreso()));
        
        return panel;
    }
    
    private JPanel crearActionCard(String icono, String titulo, String descripcion, Color color, Runnable accion) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 235, 240), 1),
            new EmptyBorder(30, 25, 30, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcono.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(33, 33, 33));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(117, 117, 117));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(lblIcono);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(lblTitulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblDesc);
        card.add(Box.createVerticalGlue());
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 251, 252));
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(color, 2),
                    new EmptyBorder(30, 25, 30, 25)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(230, 235, 240), 1),
                    new EmptyBorder(30, 25, 30, 25)
                ));
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                accion.run();
            }
        });

        return card;
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro que deseas cerrar sesión?",
            "Confirmar Cierre de Sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Modelos.Sesion.cerrar();
            new LoginApp();
            this.dispose();
        }
    }
    
    // Métodos de navegación
    private void abrirCalificaciones() throws Exception { new CalificacionesPadreApp(); this.dispose(); }
    private void abrirAsistencia() { new AsistenciaPadreApp(); this.dispose(); }
    private void abrirProgreso() { new ProgresoPadreApp(); this.dispose(); }
    private void abrirPerfil() { new PerfilEstudianteApp(); this.dispose(); }
    
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new DashboardPadreApp());
    }
}