package com.github.neder_land.gamecenter.client.view;

import com.github.neder_land.gamecenter.client.api.Client;
import com.github.neder_land.gamecenter.client.controller.Connect;
import com.github.neder_land.gamecenter.client.controller.Login;
import com.github.neder_land.gamecenter.client.controller.Post;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class ClientWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextArea txt;
    private JTextField txtip;
    private JTextField txtport;
    private JTextField txtlogin;
    private JTextField txtSend;
    private static ClientWindow instance;
    private JButton btnSend;
    private JButton btnConnect;
    private JButton btnLogin;
    private boolean disposed = false;

    public ClientWindow() {
        if (instance != null) {
            instance.setVisible(false);
            instance.dispose();
            instance.disposed = true;
            instance = null;
        }
        setTitle("GameCenterJavaVertest");
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        txt = new JTextArea();
        txt.setText("ok...");
        JScrollPane scroll = new JScrollPane(txt);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setViewportView(txt);
        txtip = new JTextField();
        txtip.setText("106.13.23.151");
        txtip.setColumns(10);

        txtport = new JTextField();
        txtport.setText("233");
        txtport.setColumns(10);

        txtlogin = new JTextField();

        addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window has been closed.
             *
             * @param e WindowEvent
             */
            @Override
            public void windowClosed(WindowEvent e) {
                ClientWindow.this.disposed = true;
                Client.getClient().handleExit(0);
                instance = null;
            }
        });
        btnConnect = new JButton("connect");
        btnConnect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Connect.execute(txtip, txtport);
            }
        });

        btnLogin = new JButton("Login");
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Login.execute(ClientWindow.this, txtlogin);
            }
        });

        btnLogin.setEnabled(false);
        btnLogin.setFocusPainted(false);

        txtSend = new JTextField();
        txtSend.setText("Login|10|lacjavatest|3FE2Z45A8G2D");
        txtSend.setColumns(10);

        btnSend = new JButton("send");
        btnSend.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Post.execute(txtSend.getText(), ClientWindow.this);
            }
        });

        btnSend.setEnabled(false);
        btnSend.setFocusPainted(false);
        GroupLayout gl_contentPane = new GroupLayout(contentPane);

        gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(
                Alignment.TRAILING,
                gl_contentPane.createSequentialGroup().addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addComponent(txtSend, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addComponent(txtlogin, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(btnLogin, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                        .addGroup(Alignment.LEADING,
                                gl_contentPane.createSequentialGroup()
                                        .addComponent(txtip, GroupLayout.PREFERRED_SIZE, 294,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(btnConnect, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                        .addGroup(Alignment.LEADING,
                                gl_contentPane.createSequentialGroup()
                                        .addComponent(txtport, GroupLayout.PREFERRED_SIZE, 294,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(btnConnect, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                        .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)).addContainerGap()));

        gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                        .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                .addComponent(txtip, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnConnect))
                        .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                .addComponent(txtport, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnConnect))
                        .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                .addComponent(txtlogin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnLogin))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING).addComponent(btnSend)
                                .addComponent(txtSend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE))));

        contentPane.setLayout(gl_contentPane);
        instance = this;
    }

    public static boolean opening() {
        return instance != null;
    }

    public void markAvailable() {
        btnSend.setEnabled(true);
        btnSend.setFocusPainted(true);
        btnLogin.setEnabled(true);
        btnLogin.setFocusPainted(true);
    }

    public void markUnavailable() {
        btnSend.setEnabled(false);
        btnSend.setFocusPainted(false);
        btnLogin.setEnabled(false);
        btnLogin.setFocusPainted(false);
    }

    public void appendText(String in) {
        txt.append("\n" + in);
    }

    public static ClientWindow get() {
        return instance;
    }

    public boolean isDisposed() {
        return disposed;
    }
}
