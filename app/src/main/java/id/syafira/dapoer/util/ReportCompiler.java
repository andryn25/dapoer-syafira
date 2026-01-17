package id.syafira.dapoer.util;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

/**
 * Kelas utilitas untuk mengompilasi file JRXML menjadi format Jasper.
 * Digunakan untuk memproses desain laporan JasperReports agar siap dijalankan.
 */
public class ReportCompiler {

    /**
     * Mengompilasi file JRXML ke format .jasper.
     * 
     * @param jrxmlPath  Path absolut ke file .jrxml.
     * @param jasperPath Path absolut untuk file output .jasper yang dihasilkan.
     * @throws JRException Jika terjadi kesalahan saat kompilasi.
     */
    public static void compile(String jrxmlPath, String jasperPath) throws JRException {
        JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);
    }

    /**
     * Mengompilasi file JRXML dan mengembalikan path file .jasper hasil kompilasi.
     * Nama file output akan mengikuti nama file input dengan ekstensi yang diubah.
     * 
     * @param jrxmlPath Path absolut ke file .jrxml.
     * @return Path ke file .jasper hasil kompilasi.
     * @throws JRException Jika terjadi kesalahan saat kompilasi.
     */
    public static String compile(String jrxmlPath) throws JRException {
        String jasperPath = jrxmlPath.replace(".jrxml", ".jasper");
        compile(jrxmlPath, jasperPath);
        return jasperPath;
    }
}
