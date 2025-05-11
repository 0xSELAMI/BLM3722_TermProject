package com.dealership.ui;

import com.dealership.db.DatabaseUtil;

import javax.swing.*;
import java.awt.*;
// import java.awt.event.ActionEvent; // Kullanılmıyorsa kaldırılabilir
// import java.awt.event.ActionListener; // Kullanılmıyorsa kaldırılabilir

public class LoginDialog extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private boolean authenticated = false;

    public LoginDialog(Frame parent) {
        super(parent, "Yetkili Girişi", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Kullanıcı Adı:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; txtUsername = new JTextField(15); add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Şifre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; txtPassword = new JPasswordField(15); add(txtPassword, gbc);

        JButton btnLogin = new JButton("Giriş Yap");
        btnLogin.addActionListener(e -> authenticate());

        JButton btnResetDb = new JButton("Veritabanını Sıfırla");
        btnResetDb.addActionListener(e -> resetDatabase());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnResetDb);
        buttonPanel.add(btnLogin);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
         addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (!authenticated) {
                    System.exit(0);
                }
            }
        });
    }

    private void authenticate() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if ("admin".equals(username) && "admin".equals(password)) {
            authenticated = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Geçersiz kullanıcı adı veya şifre!", "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
            authenticated = false;
        }
    }

    private void resetDatabase() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Veritabanındaki tüm tablolar silinecek ve şema yeniden oluşturulacaktır.\nBu işlem geri alınamaz! Devam etmek istiyor musunuz?",
                "Veritabanı Sıfırlama Onayı",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseUtil.clearDatabase();
                DatabaseUtil.initializeSchema();
                JOptionPane.showMessageDialog(this, "Veritabanı başarıyla sıfırlandı ve şema yeniden oluşturuldu.\nUygulama yeniden başlatıldığında örnek veriler eklenecektir.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Veritabanı sıfırlanırken bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
