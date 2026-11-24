package com.interfazmaestro;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginApp extends JFrame {
    
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JPanel mainPanel;
    private JButton btnLogin;
    
    public LoginApp() {
        setTitle("ClassConnect");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false); // Para mantener decoración para poder minimizar/cerrar
        
        // Fondo general
        mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY); // Mejor calidad de renderizado
                
                int w = getWidth();
                int h = getHeight();
                
                // Gradiente
                Color color1 = new Color(25, 25, 112); // Azul marino oscuro
                Color color2 = new Color(93, 50, 165); // Morado suave
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                
                // Círculos decorativos con transparencia
                g2d.setColor(new Color(255, 255, 255, 30)); // Blanco con transparencia
                g2d.fillOval(-100, -100, 400, 400); // Círculo superior izquierdo
                g2d.fillOval(w - 300, h - 300, 400, 400); // Círculo inferior derecho
            }
        };
        
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Margen alrededor
        
        // Panel del formulario
        JPanel formContainer = crearPanelFormulario();
        
        GridBagConstraints gbc = new GridBagConstraints(); // Configuración para centrar el formulario
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        mainPanel.add(formContainer, gbc);
        
        setContentPane(mainPanel);
        setVisible(true);
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        // Sombra
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200, 100), 1, true),
                new EmptyBorder(50, 60, 50, 60)
            )
        ));
        
        // Efecto de sombra con borde redondeado
        panel.setOpaque(true);
        
        // ICONO del sistema
        JLabel lblLogo = new JLabel("🎓", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // TÍTULO
        JLabel lblTitulo = new JLabel("ClassConnect"); // Nombre de la aplicación
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(new Color(25, 25, 112));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel("Iniciar Sesión");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(120, 120, 120));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(lblLogo);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(lblTitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(lblSubtitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // CAMPO: Usuario
        JLabel lblUsuario = new JLabel("Usuario / Correo");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuario.setForeground(new Color(70, 70, 70));
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT); // AÑADIDO

        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsuario.setPreferredSize(new Dimension(400, 50));
        txtUsuario.setMaximumSize(new Dimension(400, 50));
        txtUsuario.setAlignmentX(Component.CENTER_ALIGNMENT); // AÑADIDO
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        
        // Animación de focus mejorada
        txtUsuario.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180), 3, true),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        });
        
        panel.add(lblUsuario);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(txtUsuario);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // CAMPO: Contraseña
        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPassword.setForeground(new Color(70, 70, 70)); // Azul oscuro
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT); 

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.setPreferredSize(new Dimension(400, 50));
        txtPassword.setMaximumSize(new Dimension(400, 50));
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT); 
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        txtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtPassword.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180), 3, true),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtPassword.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        });
        
        txtPassword.addActionListener(e -> realizarLogin());
        
        panel.add(lblPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(txtPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 35)));
        
        // BOTÓN: Iniciar Sesión 
        btnLogin = new JButton("INICIAR SESIÓN");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(new Color(60, 179, 113)); // Verde medio
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setPreferredSize(new Dimension(400, 55));
        btnLogin.setMaximumSize(new Dimension(400, 55));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Efecto hover con animación
        btnLogin.addMouseListener(new MouseAdapter() {
            Timer timer;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                timer = new Timer(10, new ActionListener() {
                    int alpha = 113;
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        if (alpha > 87) {
                            alpha -= 2;
                            btnLogin.setBackground(new Color(60, 179, alpha));
                        } else {
                            timer.stop();
                        }
                    }
                });
                timer.start();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (timer != null) timer.stop();
                btnLogin.setBackground(new Color(60, 179, 113));
            }
        });
        
        btnLogin.addActionListener(e -> realizarLogin());
        
        panel.add(btnLogin);
        panel.add(Box.createRigidArea(new Dimension(0, 30))); 
        
        // CREDENCIALES DE PRUEBA
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(248, 249, 250)); 
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        infoPanel.setMaximumSize(new Dimension(400, 100));
        
        JLabel lblInfo = new JLabel("Credenciales de prueba:");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblCredProf = new JLabel("Profesor: profesor@escuela.com / pass123");
        lblCredProf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCredProf.setForeground(new Color(100, 100, 100));
        lblCredProf.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblCredPadre = new JLabel("Padre: padre@correo.com / pass123");
        lblCredPadre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCredPadre.setForeground(new Color(100, 100, 100));
        lblCredPadre.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoPanel.add(lblInfo);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        infoPanel.add(lblCredProf);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(lblCredPadre);
        
        panel.add(infoPanel);
        
        return panel;
    }
    
    private void realizarLogin() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor completa todos los campos");
            return;
        }
        
        // Mostrar indicador de carga
        btnLogin.setEnabled(false);
        btnLogin.setText("VERIFICANDO...");
        
        // Verificar credenciales contra la base de datos sin bloquear la interfaz
        new SwingWorker<Modelos.Usuario, Void>() {
            @Override
            protected Modelos.Usuario doInBackground() throws Exception {
                return new Modelos.LoginDB().iniciarSesion(usuario, password);
            }

            @Override
            protected void done() {
                try {
                    Modelos.Usuario u = get();
                    if (u == null) {
                        mostrarError("Usuario o contraseña incorrectos");
                    } else if ("profesor".equals(u.getRol()) || "admin".equals(u.getRol())) {
                        abrirDashboard("Profesor");
                        return;
                    } else if ("padre".equals(u.getRol())) {
                        abrirDashboard("Padre/Madre");
                        return;
                    } else {
                        mostrarError("Este portal es solo para docentes y padres/madres.");
                    }
                } catch (Exception ex) {
                    Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                    mostrarError("No se pudo conectar a la base de datos.\n" + causa.getMessage()
                            + "\n\nRevisa las variables CLASSCONNECT_DB_URL, CLASSCONNECT_DB_USER y CLASSCONNECT_DB_PASS.");
                }
                btnLogin.setEnabled(true);
                btnLogin.setText("INICIAR SESIÓN");
            }
        }.execute();
    }

    private void abrirDashboard(String tipoUsuario) {
        if (tipoUsuario.equals("Profesor")) {
            new DashboardProfesorApp();
        } else if (tipoUsuario.equals("Padre/Madre")) {
            new com.interfazpadre.DashboardPadreApp();
        }
        this.dispose();
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this,
            mensaje,
            "Error de Autenticación",
            JOptionPane.ERROR_MESSAGE);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new LoginApp());
    }
}