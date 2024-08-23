package core;

import javax.swing.*;

public class Helper {
    public static void setTheme(){
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
            if (info.getName().equals("Nimbus")){
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    throw new RuntimeException(e);

                }
                break;
            }
        }
        UIManager.put("OptionPane.okButtonText", "Tamam");
    }
// JTextField Dolumu boşmu kontrol ediliyor
    public static boolean isFieldEmpty(JTextField field){
        return field.getText().trim().isEmpty();
    }

    public static boolean isFieldListEmpty(JTextField[] fields){
        for (JTextField field : fields){
            if (isFieldEmpty(field)) return true;
        }
        return false;
    }
    //----------------------------------------------------------------
    // Mail kutusunda Mail mi var kontrolü
    public static boolean isEmailValid(String mail){
        // info@patika.dev
        // @ olacak, @'ten önce bir değer, @'ten Sonra bir nokta olacak ve bir değer olacak

        if (mail == null || mail.trim().isEmpty()) return false ;

        if (!mail.contains("@")) return false; // @ değeri var mı yı kontrol eder.
        // @'den önce ve sonra değer olup olmadığını kontrol et
        String[] parts = mail.split("@");
        if(parts.length != 2) return false;

        if (parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) return false;
        //-------------------------------------------------------------------------------

        if (!parts[1].contains(".")) return false; //Text'te @ değerinden sonra nokta var mıyı kontrol eder.


        //Noktadan önce ve sonra değer olup olmadığını kontrol et
        String[] domainParts = parts[1].split("\\.");
        if (domainParts.length < 2) return false;


        if (domainParts[0].trim().isEmpty() || domainParts[1].trim().isEmpty()) return false;
        // -------------------------------------------------------------------------
        return true;
    }


    public static void showMsg(String message){
        String msg;
        String title;
        switch (message) {
            case "fill" -> {
                msg = "Lütfen tüm alanlarını doldurunuz!";
                title = "HATA!";
            }
            case "done" -> {
                msg = "İşlem Başarılı !";
                title = "Sonuç";
            }
            case "error" -> {
                msg = "Bir hata oluştu";
                title = "HATA!";
            }
            default -> {
                msg = message;
                title = "Mesaj";
            }
        }
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE );

    }



}
