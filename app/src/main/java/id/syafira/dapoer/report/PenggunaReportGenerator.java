package id.syafira.dapoer.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import java.awt.Color;

/**
 * Enhanced Generator for Pengguna/User Report with professional design
 */
public class PenggunaReportGenerator {

    private static final String JRXML_FILE = "laporan_pengguna.jrxml";

    public static String generateJRXML() throws JRException {
        // 1. Create JasperDesign
        JasperDesign jasperDesign = new JasperDesign();
        jasperDesign.setName("LaporanPengguna");
        jasperDesign.setPageWidth(595); // A4 width
        jasperDesign.setPageHeight(842); // A4 height
        jasperDesign.setColumnWidth(535);
        jasperDesign.setLeftMargin(30);
        jasperDesign.setRightMargin(30);
        jasperDesign.setTopMargin(20);
        jasperDesign.setBottomMargin(20);

        // 2. Add SQL Query with JOIN and formatted ID
        JRDesignQuery query = new JRDesignQuery();
        query.setText(
                "SELECT CONCAT('USR-', LPAD(u.id_user, 3, '0')) as id_user, " +
                        "u.username, u.nama_user, l.nama_level " +
                        "FROM user u JOIN level_user l ON u.id_level = l.id_level " +
                        "ORDER BY u.username");
        jasperDesign.setQuery(query);

        // 3. Add Fields
        ReportGeneratorHelper.addField(jasperDesign, "id_user", String.class); // Changed to String for USR-XXX format
        ReportGeneratorHelper.addField(jasperDesign, "username", String.class);
        ReportGeneratorHelper.addField(jasperDesign, "nama_user", String.class);
        ReportGeneratorHelper.addField(jasperDesign, "nama_level", String.class);

        // 4. Enhanced Title Band (Header Section)
        JRDesignBand titleBand = ReportGeneratorHelper.createTitleBand(jasperDesign, "LAPORAN PENGGUNA");
        jasperDesign.setTitle(titleBand);

        // 5. Enhanced Column Header Band with borders
        JRDesignBand columnHeaderBand = new JRDesignBand();
        columnHeaderBand.setHeight(35);

        // Header background
        JRDesignRectangle colHeaderBg = new JRDesignRectangle();
        colHeaderBg.setX(0);
        colHeaderBg.setY(0);
        colHeaderBg.setWidth(535);
        colHeaderBg.setHeight(30);
        colHeaderBg.setBackcolor(ReportGeneratorHelper.COLOR_PRIMARY);
        colHeaderBg.setMode(net.sf.jasperreports.engine.type.ModeEnum.OPAQUE);
        columnHeaderBand.addElement(colHeaderBg);

        // Column headers with white text
        ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Kode", 5, 8, 60, 20, Color.WHITE);
        ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Username", 65, 8, 120, 20, Color.WHITE);
        ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Nama Pengguna", 185, 8, 200, 20, Color.WHITE);
        ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Level", 385, 8, 145, 20, Color.WHITE);

        jasperDesign.setColumnHeader(columnHeaderBand);

        // 6. Enhanced Detail Band with borders
        JRDesignBand detailBand = new JRDesignBand();
        detailBand.setHeight(25);

        // Row borders
        JRDesignLine bottomBorder = new JRDesignLine();
        bottomBorder.setX(0);
        bottomBorder.setY(24);
        bottomBorder.setWidth(535);
        bottomBorder.setHeight(0);
        bottomBorder.setForecolor(ReportGeneratorHelper.COLOR_BORDER);
        detailBand.addElement(bottomBorder);

        // Data fields
        ReportGeneratorHelper.addTextField(detailBand, "$F{id_user}", 5, 2, 60, 20, null, null);
        ReportGeneratorHelper.addTextField(detailBand, "$F{username}", 65, 2, 120, 20, null, null);
        ReportGeneratorHelper.addTextField(detailBand, "$F{nama_user}", 185, 2, 200, 20, null, null);
        ReportGeneratorHelper.addTextField(detailBand, "$F{nama_level}", 385, 2, 145, 20, null, null);

        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(detailBand);

        // 7. Page Footer with signature section (appears at bottom of every page)
        JRDesignBand pageFooter = ReportGeneratorHelper.createPageFooter();
        jasperDesign.setPageFooter(pageFooter);

        // 8. Save JRXML
        String outputPath = ReportGeneratorHelper.REPORTS_DIR + JRXML_FILE;
        JRXmlWriter.writeReport(jasperDesign, outputPath, "UTF-8");

        return outputPath;
    }
}
