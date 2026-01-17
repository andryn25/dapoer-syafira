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
 * Enhanced Transaksi Detail Report with Customer and Cashier Information
 */
public class TransaksiDetailReportGenerator {

        private static final String JRXML_FILE = "laporan_transaksi_detail.jrxml";

        public static String generateJRXML() throws JRException {
                JasperDesign jasperDesign = new JasperDesign();
                jasperDesign.setName("LaporanTransaksiDetail");
                jasperDesign.setPageWidth(595);
                jasperDesign.setPageHeight(842);
                jasperDesign.setColumnWidth(535);
                jasperDesign.setLeftMargin(30);
                jasperDesign.setRightMargin(30);
                jasperDesign.setTopMargin(20);
                jasperDesign.setBottomMargin(20);

                // SQL Query with JOINs
                JRDesignQuery query = new JRDesignQuery();
                query.setText(
                                "SELECT " +
                                                "CONCAT('TRX-', LPAD(t.id_transaksi, 3, '0')) as kode_transaksi, " +
                                                "t.tanggal, " +
                                                "COALESCE(p.nama, 'Walk-in Customer') as nama_pelanggan, " +
                                                "COALESCE(p.telepon, '-') as telepon_pelanggan, " +
                                                "u.nama_user as kasir, " +
                                                "t.total_bayar " +
                                                "FROM transaksi t " +
                                                "LEFT JOIN `order` o ON t.id_order = o.id_order " +
                                                "LEFT JOIN pelanggan p ON o.id_pelanggan = p.id_pelanggan " +
                                                "LEFT JOIN user u ON t.id_user = u.id_user " +
                                                "ORDER BY t.tanggal DESC");
                jasperDesign.setQuery(query);

                // Fields
                // Fields
                ReportGeneratorHelper.addField(jasperDesign, "kode_transaksi", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "tanggal", java.sql.Timestamp.class);
                ReportGeneratorHelper.addField(jasperDesign, "nama_pelanggan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "telepon_pelanggan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "kasir", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "total_bayar", Integer.class);

                // Title Band
                JRDesignBand titleBand = ReportGeneratorHelper.createTitleBand(jasperDesign,
                                "LAPORAN TRANSAKSI DETAIL");
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
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Tanggal", 65, 8, 90, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Pelanggan", 155, 8, 100, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Telepon", 255, 8, 80, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Kasir", 335, 8, 80, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Total", 415, 8, 115, 20, Color.WHITE,
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

                ReportGeneratorHelper.addTextField(detailBand, "$F{kode_transaksi}", 5, 2, 60, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{tanggal}", 65, 2, 90, 20, "dd/MM/yy HH:mm", null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{nama_pelanggan}", 155, 2, 100, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{telepon_pelanggan}", 255, 2, 80, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{kasir}", 335, 2, 80, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{total_bayar}", 415, 2, 115, 20, "Rp #,##0",
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
