package id.syafira.dapoer.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import java.awt.Color;

/**
 * Enhanced Generator for Transaksi Report with professional design
 */
public class TransaksiReportGenerator {

        private static final String JRXML_FILE = "laporan_transaksi.jrxml";

        public static String generateJRXML() throws JRException {
                // 1. Create JasperDesign
                JasperDesign jasperDesign = new JasperDesign();
                jasperDesign.setName("LaporanTransaksi");
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
                                "SELECT CONCAT('TRX-', LPAD(id_transaksi, 3, '0')) as id_transaksi, " +
                                                "tanggal, total_bayar " +
                                                "FROM transaksi ORDER BY tanggal DESC");
                jasperDesign.setQuery(query);

                // 3. Add Fields
                // 3. Add Fields
                ReportGeneratorHelper.addField(jasperDesign, "id_transaksi", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "tanggal", java.sql.Timestamp.class);
                ReportGeneratorHelper.addField(jasperDesign, "total_bayar", Integer.class);

                // 4. Enhanced Title Band (Header Section)
                JRDesignBand titleBand = ReportGeneratorHelper.createTitleBand(jasperDesign, "LAPORAN TRANSAKSI");
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
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Kode Transaksi", 5, 8, 120, 20, true, null,
                                Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Tanggal", 125, 8, 200, 20, true, null,
                                Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Total Bayar", 325, 8, 205, 20, true,
                                HorizontalTextAlignEnum.RIGHT,
                                Color.WHITE);

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
                ReportGeneratorHelper.addTextField(detailBand, "$F{id_transaksi}", 5, 2, 120, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{tanggal}", 125, 2, 200, 20, "dd/MM/yyyy HH:mm",
                                null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{total_bayar}", 325, 2, 205, 20, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT);

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
