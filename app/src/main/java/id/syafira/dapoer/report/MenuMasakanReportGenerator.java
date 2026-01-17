package id.syafira.dapoer.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import java.awt.Color;

/**
 * Enhanced Generator for Menu Masakan Report with professional design
 */
public class MenuMasakanReportGenerator {

        private static final String JRXML_FILE = "laporan_menu_masakan.jrxml";

        public static String generateJRXML() throws JRException {
                // 1. Create JasperDesign
                JasperDesign jasperDesign = new JasperDesign();
                jasperDesign.setName("LaporanMenuMasakan");
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
                                "SELECT CONCAT('MNU-', LPAD(id_masakan, 3, '0')) as id_masakan, " +
                                                "nama_masakan, harga, status_masakan, stok " +
                                                "FROM menu_masakan ORDER BY nama_masakan");
                jasperDesign.setQuery(query);

                // 3. Add Fields
                // 3. Add Fields
                ReportGeneratorHelper.addField(jasperDesign, "id_masakan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "nama_masakan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "harga", Integer.class);
                ReportGeneratorHelper.addField(jasperDesign, "status_masakan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "stok", Integer.class);

                // 4. Enhanced Title Band (Header Section)
                JRDesignBand titleBand = ReportGeneratorHelper.createTitleBand(jasperDesign, "LAPORAN MENU MASAKAN");
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
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Kode", 5, 8, 60, 20, true, null, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Nama Masakan", 65, 8, 180, 20, true, null,
                                Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Harga", 245, 8, 100, 20, true,
                                HorizontalTextAlignEnum.RIGHT, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Status", 345, 8, 90, 20, true,
                                HorizontalTextAlignEnum.CENTER, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Stok", 435, 8, 95, 20, true,
                                HorizontalTextAlignEnum.CENTER, Color.WHITE);

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
                ReportGeneratorHelper.addTextField(detailBand, "$F{id_masakan}", 5, 2, 60, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{nama_masakan}", 65, 2, 180, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{harga}", 245, 2, 100, 20, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT);
                ReportGeneratorHelper.addTextField(detailBand, "$F{status_masakan}", 345, 2, 90, 20, null,
                                HorizontalTextAlignEnum.CENTER);
                ReportGeneratorHelper.addTextField(detailBand, "$F{stok}", 435, 2, 95, 20, null,
                                HorizontalTextAlignEnum.CENTER);

                ((JRDesignSection) jasperDesign.getDetailSection()).addBand(detailBand);

                // 7. Page Footer with signature section (appears at bottom of every page)
                JRDesignBand pageFooter = ReportGeneratorHelper.createPageFooter();
                jasperDesign.setPageFooter(pageFooter);

                // 9. Save JRXML
                // 9. Save JRXML
                String outputPath = ReportGeneratorHelper.REPORTS_DIR + JRXML_FILE;
                JRXmlWriter.writeReport(jasperDesign, outputPath, "UTF-8");

                return outputPath;
        }

}
