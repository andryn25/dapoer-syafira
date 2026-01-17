package id.syafira.dapoer.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import java.awt.Color;

/**
 * Order Report - Customer Orders/Pesanan
 */
public class OrderReportGenerator {

        private static final String JRXML_FILE = "laporan_pesanan.jrxml";

        public static String generateJRXML() throws JRException {
                JasperDesign jasperDesign = new JasperDesign();
                jasperDesign.setName("LaporanPesanan");
                jasperDesign.setPageWidth(595);
                jasperDesign.setPageHeight(842);
                jasperDesign.setColumnWidth(535);
                jasperDesign.setLeftMargin(30);
                jasperDesign.setRightMargin(30);
                jasperDesign.setTopMargin(20);
                jasperDesign.setBottomMargin(20);

                // SQL Query
                JRDesignQuery query = new JRDesignQuery();
                query.setText(
                                "SELECT " +
                                                "CONCAT('ORD-', LPAD(o.id_order, 3, '0')) as kode_order, " +
                                                "o.tgl_pesan, " +
                                                "o.tgl_kirim, " +
                                                "p.nama as nama_pelanggan, " +
                                                "o.jenis_pesanan, " +
                                                "o.total, " +
                                                "o.catatan " +
                                                "FROM `order` o " +
                                                "LEFT JOIN pelanggan p ON o.id_pelanggan = p.id_pelanggan " +
                                                "ORDER BY o.tgl_pesan DESC");
                jasperDesign.setQuery(query);

                // Fields
                // Fields
                ReportGeneratorHelper.addField(jasperDesign, "kode_order", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "tgl_pesan", java.sql.Timestamp.class);
                ReportGeneratorHelper.addField(jasperDesign, "tgl_kirim", java.sql.Timestamp.class);
                ReportGeneratorHelper.addField(jasperDesign, "nama_pelanggan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "jenis_pesanan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "total", Integer.class);
                ReportGeneratorHelper.addField(jasperDesign, "catatan", String.class);

                // Title Band
                // Title Band
                JRDesignBand titleBand = ReportGeneratorHelper.createTitleBand(jasperDesign, "LAPORAN PESANAN");
                jasperDesign.setTitle(titleBand);

                // Column Header
                JRDesignBand columnHeaderBand = new JRDesignBand();
                columnHeaderBand.setHeight(35);

                JRDesignRectangle colHeaderBg = new JRDesignRectangle();
                colHeaderBg.setX(0);
                colHeaderBg.setY(0);
                colHeaderBg.setWidth(535);
                colHeaderBg.setHeight(30);
                colHeaderBg.setBackcolor(ReportGeneratorHelper.COLOR_PRIMARY);
                colHeaderBg.setMode(net.sf.jasperreports.engine.type.ModeEnum.OPAQUE);
                columnHeaderBand.addElement(colHeaderBg);

                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Kode", 5, 8, 50, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Tgl Pesan", 55, 8, 70, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Tgl Kirim", 125, 8, 70, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Pelanggan", 195, 8, 90, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Jenis", 285, 8, 60, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Total", 345, 8, 80, 20, Color.WHITE,
                                HorizontalTextAlignEnum.RIGHT);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Catatan", 435, 8, 100, 20, Color.WHITE);

                jasperDesign.setColumnHeader(columnHeaderBand);

                // Detail Band
                JRDesignBand detailBand = new JRDesignBand();
                detailBand.setHeight(25);

                JRDesignLine bottomBorder = new JRDesignLine();
                bottomBorder.setX(0);
                bottomBorder.setY(24);
                bottomBorder.setWidth(535);
                bottomBorder.setHeight(0);
                bottomBorder.setForecolor(ReportGeneratorHelper.COLOR_BORDER);
                detailBand.addElement(bottomBorder);

                ReportGeneratorHelper.addTextField(detailBand, "$F{kode_order}", 5, 2, 50, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{tgl_pesan}", 55, 2, 70, 20, "dd/MM/yy", null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{tgl_kirim}", 125, 2, 70, 20, "dd/MM/yy", null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{nama_pelanggan}", 195, 2, 90, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{jenis_pesanan}", 285, 2, 60, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{total}", 345, 2, 80, 20, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT);

                // Catatan field (might be long, truncated)
                JRDesignTextField catatanField = new JRDesignTextField();
                catatanField.setExpression(new JRDesignExpression("$F{catatan}"));
                catatanField.setX(435);
                catatanField.setY(2);
                catatanField.setWidth(100);
                catatanField.setHeight(20);
                catatanField.setFontSize(9f);
                detailBand.addElement(catatanField);

                ((JRDesignSection) jasperDesign.getDetailSection()).addBand(detailBand);

                // Page Footer
                JRDesignBand pageFooter = ReportGeneratorHelper.createPageFooter();
                jasperDesign.setPageFooter(pageFooter);

                // Save
                String outputPath = ReportGeneratorHelper.REPORTS_DIR + JRXML_FILE;
                JRXmlWriter.writeReport(jasperDesign, outputPath, "UTF-8");
                return outputPath;
        }

}
