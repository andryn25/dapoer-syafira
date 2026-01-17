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
 * Enhanced Generator for Pelanggan Report with professional design
 */
public class PelangganReportGenerator {

    private static final String JRXML_FILE = "laporan_pelanggan.jrxml";

    public static String generateJRXML() throws JRException {
        // 1. Create JasperDesign
        JasperDesign jasperDesign = new JasperDesign();
        jasperDesign.setName("LaporanPelanggan");
        jasperDesign.setPageWidth(595); // A4 width
        jasperDesign.setPageHeight(842); // A4 height
        jasperDesign.setColumnWidth(535);
        jasperDesign.setLeftMargin(30);
        jasperDesign.setRightMargin(30);
        jasperDesign.setTopMargin(20);
        jasperDesign.setBottomMargin(20);

        // 2. Add SQL Query with formatted ID
        JRDesignQuery query = new JRDesignQuery();
        query.setText(
                "SELECT CONCAT('CST-', LPAD(id_pelanggan, 3, '0')) as id_pelanggan, " +
                        "nama, telepon, alamat " +
                        "FROM pelanggan ORDER BY nama");
        jasperDesign.setQuery(query);

        // 3. Add Fields
        // 3. Add Fields
        ReportGeneratorHelper.addField(jasperDesign, "id_pelanggan", String.class);
        ReportGeneratorHelper.addField(jasperDesign, "telepon", String.class);
        ReportGeneratorHelper.addField(jasperDesign, "alamat", String.class);

        // 4. Enhanced Title Band (Header Section)
        JRDesignBand titleBand = ReportGeneratorHelper.createTitleBand(jasperDesign, "LAPORAN PELANGGAN");
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
        // Column headers with white text
        ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Kode", 5, 8, 60, 20, true, null, Color.WHITE);
        ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Nama", 65, 8, 150, 20, true, null, Color.WHITE);
        ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Telepon", 215, 8, 100, 20, true, null, Color.WHITE);
        ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Alamat", 315, 8, 215, 20, true, null, Color.WHITE);

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
        // Data fields
        ReportGeneratorHelper.addTextField(detailBand, "$F{id_pelanggan}", 5, 2, 60, 20, null, null);
        ReportGeneratorHelper.addTextField(detailBand, "$F{nama}", 65, 2, 150, 20, null, null);
        ReportGeneratorHelper.addTextField(detailBand, "$F{telepon}", 215, 2, 100, 20, null, null);
        ReportGeneratorHelper.addTextField(detailBand, "$F{alamat}", 315, 2, 215, 20, null, null);

        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(detailBand);

        // 7. Page Footer with signature section (appears at bottom of every page)
        JRDesignBand pageFooter = ReportGeneratorHelper.createPageFooter();
        jasperDesign.setPageFooter(pageFooter);

        // 8. Save JRXML
        // 8. Save JRXML
        String outputPath = ReportGeneratorHelper.REPORTS_DIR + JRXML_FILE;
        JRXmlWriter.writeReport(jasperDesign, outputPath, "UTF-8");

        return outputPath;
    }

}
