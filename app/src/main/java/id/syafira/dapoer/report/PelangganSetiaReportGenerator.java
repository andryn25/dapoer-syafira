package id.syafira.dapoer.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import java.awt.Color;

/**
 * Customer Loyalty Report - Top Customers by Spending
 */
public class PelangganSetiaReportGenerator {

        private static final String JRXML_FILE = "laporan_pelanggan_setia.jrxml";

        public static String generateJRXML() throws JRException {
                JasperDesign jasperDesign = new JasperDesign();
                jasperDesign.setName("LaporanPelangganSetia");
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
                                                "CONCAT('CST-', LPAD(p.id_pelanggan, 3, '0')) as kode, " +
                                                "p.nama, " +
                                                "p.telepon, " +
                                                "COUNT(t.id_transaksi) as jumlah_transaksi, " +
                                                "COALESCE(SUM(t.total_bayar), 0) as total_belanja " +
                                                "FROM pelanggan p " +
                                                "LEFT JOIN `order` o ON p.id_pelanggan = o.id_pelanggan " +
                                                "LEFT JOIN transaksi t ON o.id_order = t.id_order " +
                                                "GROUP BY p.id_pelanggan, p.nama, p.telepon " +
                                                "HAVING COUNT(t.id_transaksi) > 0 " +
                                                "ORDER BY total_belanja DESC");
                jasperDesign.setQuery(query);

                // Fields
                // Fields
                ReportGeneratorHelper.addField(jasperDesign, "kode", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "nama", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "telepon", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "jumlah_transaksi", Long.class);
                ReportGeneratorHelper.addField(jasperDesign, "total_belanja", Long.class);

                // Title Band
                // Title Band
                JRDesignBand titleBand = ReportGeneratorHelper.createTitleBand(jasperDesign, "LAPORAN PELANGGAN SETIA");
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

                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Kode", 5, 8, 60, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Nama", 65, 8, 150, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Telepon", 215, 8, 100, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Transaksi", 315, 8, 100, 20, Color.WHITE,
                                HorizontalTextAlignEnum.RIGHT);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Total Belanja", 415, 8, 115, 20, Color.WHITE,
                                HorizontalTextAlignEnum.RIGHT);

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

                ReportGeneratorHelper.addTextField(detailBand, "$F{kode}", 5, 2, 60, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{nama}", 65, 2, 150, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{telepon}", 215, 2, 100, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{jumlah_transaksi}", 315, 2, 100, 20, "#,##0",
                                HorizontalTextAlignEnum.RIGHT);
                ReportGeneratorHelper.addTextField(detailBand, "$F{total_belanja}", 415, 2, 115, 20, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT);

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
