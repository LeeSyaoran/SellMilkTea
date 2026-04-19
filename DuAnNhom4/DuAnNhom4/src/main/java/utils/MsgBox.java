package utils;

import javax.swing.JOptionPane;
import java.awt.Component;

public class MsgBox {
    public static void alert(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message,
                "Hệ thống quản lý", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parentComponent, String message) {
        int result = JOptionPane.showConfirmDialog(parentComponent, message,
                "Hệ thống quản lý",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    public static String prompt(Component parentComponent, String message) {
        return JOptionPane.showInputDialog(parentComponent, message,
                "Hệ thống quản lý", JOptionPane.INFORMATION_MESSAGE);
    }
}
