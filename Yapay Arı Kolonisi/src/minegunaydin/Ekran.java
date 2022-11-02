package minegunaydin;

import minegunaydin.nesneler.*;

import javax.swing.*;
import java.util.List;

public class Ekran extends JFrame implements UIEvents {
    private JPanel panel;
    private JPanel panel1;
    private JTextField txt_ıterasyon_sayisi;
    private JTextField txt_durdurma_cozum_degeri;
    private JButton başlatButton;
    private JLabel lbl_siralama;
    private JLabel lbl_kisa_mesafe;
    private JLabel lbl_enuzun_mesafe;
    private JPanel canvas;
    private JTextField txt_inaktif_ari;
    private JTextField txt_ari_sayisi;
    private JTextField txt_max_ziyaret;
    private JTextField txt_gozcu_ari;
    private JTextField txt_aktif_ari;


    public Ekran(){
        Hive hive = new Hive();
        setResizable(false);
        add(panel);
        setSize(App.CERCEVE_GENISLIK,App.CERCEVE_YUKSEKLIK);
        setLocationRelativeTo(null);
        canvas.add(hive);
        setVisible(true);
        setTitle("Mine Günaydın Yapay Arı Kolonisi Algoritması");

        başlatButton.addActionListener(actionEvent -> {
            if(hive.isStarted()){
                hive.simulasyonDurdur();
                başlatButton.setText("BAŞLAT");
            }else{
                if(konfigurasyonuAyarla(hive)) {
                    hive.simulasyonuBaslat();
                    başlatButton.setText("DURDUR");
                }
            }
        });

    }

    public static void main(String[] args) {
        new Ekran().setVisible(true);
    }

    boolean konfigurasyonuAyarla(Hive hive){
        int ariSayisi = getInt(txt_ari_sayisi, "Arı Sayısı ");
        if(ariSayisi == -1){
            return false;
        }

        int aktifArı = getInt(txt_aktif_ari, "Aktif Arı Sayısı ");
        if(aktifArı == -1){
            return false;
        }

        int inaktifAktifArı = getInt(txt_inaktif_ari, "İnaktif Arı Sayısı ");
        if(inaktifAktifArı == -1){
            return false;
        }

        int gozcuAri = getInt(txt_gozcu_ari, "Gözcü Arı Sayısı ");
        if(gozcuAri == -1){
            return false;
        }



        int maxIterasyonSayisi = getInt(txt_ıterasyon_sayisi, "Max Iterasyon Sayisi ");
        if(maxIterasyonSayisi == -1){
            return false;
        }

        int maxZiyaret = getInt(txt_max_ziyaret, "Max Ziyaret Sayisi ");
        if(maxZiyaret == -1){
            return false;
        }

        double durdurma_degeri = getDouble(txt_durdurma_cozum_degeri, "Durdurma ");
        if(durdurma_degeri == -1){
            return false;
        }

        hive.setConfiguration(ariSayisi, aktifArı, inaktifAktifArı, gozcuAri, maxIterasyonSayisi, maxZiyaret, durdurma_degeri, this);

        return true;
    }




    double getDouble(JTextField field, String alanAdi){
        double deger = -1;
        if(field.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null, "Lütfen " + alanAdi + " değerini boş bırakmayın", "Uyarı", JOptionPane.ERROR_MESSAGE);
            return deger;
        }

        try{
            deger =  Double.parseDouble(field.getText().trim());
            if(deger < 0){
                return  -1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return deger;
    }

    int getInt(JTextField field, String alanAdi){
        int deger = -1;
        if(field.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null, "Lütfen " + alanAdi + " değerini boş bırakmayın", "Uyarı", JOptionPane.ERROR_MESSAGE);
            return deger;
        }

        try{
            deger =  Integer.parseInt(field.getText().trim());
            if(deger <= 0){
                JOptionPane.showMessageDialog(null, "Lütfen " + alanAdi + " değerini 0 dan büyük girin.", "Uyarı", JOptionPane.ERROR_MESSAGE);
                return  -1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return deger;
    }

    @Override
    public void enKısaYoluCiz(List<Sehir> sehirler, double distance) {

        SwingUtilities.invokeLater(() -> {
            lbl_kisa_mesafe.setText(String.format("%.2f", distance));
            String siralama = "";
            for(Sehir sehir : sehirler){
                siralama += sehir.getSehirAdi() + " => \n";
            }

            lbl_siralama.setText(siralama);
        });

    }

    @Override
    public void enUzunMesafeCiz(Double distance) {
        SwingUtilities.invokeLater(() -> lbl_enuzun_mesafe.setText(String.format("%.2f", distance)));
    }

    @Override
    public void simulasyonTamamlandi(SIMULATION_RESULT result) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(result.equals(SIMULATION_RESULT.MAX_ITERASYON)){
                    JOptionPane.showMessageDialog(null, "Max Iterasyon Sayısına Ulaşıldı.", "Tamamlandı", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "Hedeflenen değere ulaşıldı.", "Tamamlandı", JOptionPane.INFORMATION_MESSAGE);
                }
                başlatButton.setText("BAŞLAT");
            }
        });
    }
}
