package com.interfazpadre;

import Modelos.Alumno;
import dao.AlumnoDAO;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;

public class PerfilEstudianteApp extends JFrame {
    
    private AlumnoDAO alumnoDAO;
    private Alumno estudiante;
    // IMPORTANTE: Asegúrate que exista un estudiante con ID 1 en tu base de datos
    private int idEstudianteActual = Modelos.Sesion.getIdEstudiante() > 0 ? Modelos.Sesion.getIdEstudiante() : 1;
    
    // Paleta de Colores
    private static final Color PRIMARY = new Color(93, 50, 165); 
    private static final Color PRIMARY_LIGHT = new Color(42, 82, 152); 
    private static final Color BG_COLOR = new Color(240, 242, 245); 
    private static final Color TEXT_DARK = new Color(50, 50, 50);
    private static final Color TEXT_MUTED = new Color(130, 130, 130);

    public PerfilEstudianteApp() {
        // Cargar datos
        alumnoDAO = new AlumnoDAO();
        estudiante = alumnoDAO.obtenerPorId(idEstudianteActual);
        
        if(estudiante == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el alumno ID " + idEstudianteActual + " en la base de datos.\nVerifica e intenta nuevamente.");
            estudiante = new Alumno();
            estudiante.setNombre("Desconocido");
            estudiante.setApellido("");
            estudiante.setGrado("N/A");
            estudiante.setSeccion("");
        }

        setTitle("ClassConnect - " + estudiante.getNombreCompleto());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Layout Principal
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null); 
        layeredPane.setBackground(BG_COLOR);
        layeredPane.setOpaque(true);
        
        // 1. Banner
        JPanel banner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), 0, PRIMARY_LIGHT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        banner.setBounds(0, 0, 3000, 180); 
        
        JButton btnVolver = new JButton("← Volver");
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVolver.setBounds(30, 30, 180, 40);
        btnVolver.addActionListener(e -> this.dispose());
        layeredPane.add(btnVolver, JLayeredPane.PALETTE_LAYER);
        
        // 2. Tarjeta Perfil
        JPanel profileCard = crearTarjetaPerfil();
        profileCard.setBounds(50, 100, 350, 550); 
        
        // 3. Tabs
        JTabbedPane tabbedPane = crearTabsContenido();
        tabbedPane.setBounds(430, 190, 800, 550); 
        
        layeredPane.add(banner, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(profileCard, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(tabbedPane, JLayeredPane.PALETTE_LAYER);
        
        // Responsive simple
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                banner.setSize(w, 180);
                if (w > 850) tabbedPane.setSize(w - 480, 550);
            }
        });

        setContentPane(layeredPane);
        setVisible(true);
    }
    
    private JPanel crearTarjetaPerfil() {
        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
        
        String iniciales = (estudiante.getNombre() != null && estudiante.getNombre().length() > 0) ? 
                           estudiante.getNombre().substring(0,1) : "X";
        
        JLabel avatar = new JLabel(iniciales, SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 48));
        avatar.setForeground(PRIMARY);
        avatar.setBorder(BorderFactory.createLineBorder(PRIMARY, 3));
        avatar.setBounds(100, 30, 150, 150);
        
        JLabel lblNombre = new JLabel(estudiante.getNombreCompleto(), SwingConstants.CENTER);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNombre.setBounds(20, 190, 310, 30);
        
        JLabel lblGrado = new JLabel(estudiante.getGradoCompleto(), SwingConstants.CENTER);
        lblGrado.setBounds(20, 220, 310, 20);
        
        // Datos Rápidos
        JPanel infoList = new JPanel(new GridLayout(3, 1, 0, 15));
        infoList.setOpaque(false);
        infoList.setBounds(30, 300, 290, 200);
        
        infoList.add(crearItem("Matrícula", estudiante.getMatricula()));
        infoList.add(crearItem("Correo", estudiante.getCorreo()));
        infoList.add(crearItem("Dirección", estudiante.getDireccion()));

        card.add(avatar);
        card.add(lblNombre);
        card.add(lblGrado);
        card.add(infoList);
        return card;
    }
    
    private JPanel crearItem(String titulo, String valor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l1 = new JLabel(titulo);
        l1.setForeground(TEXT_MUTED);
        JLabel l2 = new JLabel(valor != null ? valor : "No registrado");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(l1, BorderLayout.WEST);
        p.add(l2, BorderLayout.EAST);
        return p;
    }

    private JTabbedPane crearTabsContenido() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Información General", crearTabGeneral());
        // Deshabilitamos Médico e Historial porque NO están en la base de datos
        tabs.addTab("Médico (No Disponible)", new JPanel()); 
        return tabs;
    }
    
    private JPanel crearTabGeneral() {
        JPanel p = new JPanel(new GridLayout(4, 1, 10, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        p.add(new JLabel("NOTA: La base de datos actual no contiene información de padres ni médica."));
        p.add(crearItem("Dirección", estudiante.getDireccion()));
        p.add(crearItem("Usuario Asociado", estudiante.getCorreo()));
        
        return p;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PerfilEstudianteApp::new);
    }
}