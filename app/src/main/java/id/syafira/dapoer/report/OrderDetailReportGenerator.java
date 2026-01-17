package id.syafira.dapoer.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import java.awt.Color;

/**
 * Order Detail Report - Master-Detail showing order items
 */
public class OrderDetailReportGenerator {

        private static final String JRXML_FILE = "laporan_order_detail.jrxml";

        private static final Color COLOR_GROUP_BG = new Color(243, 244, 246);

        public static String generateJRXML() throws JRException {
                JasperDesign jasperDesign = new JasperDesign();
                jasperDesign.setName("LaporanOrderDetail");
                jasperDesign.setPageWidth(595);
                jasperDesign.setPageHeight(842);
                jasperDesign.setColumnWidth(535);
                jasperDesign.setLeftMargin(30);
                jasperDesign.setRightMargin(30);
                jasperDesign.setTopMargin(20);
                jasperDesign.setBottomMargin(20);

                // SQL Query - JOIN order + detail_order + menu + pelanggan
                JRDesignQuery query = new JRDesignQuery();
                query.setText(
                                "SELECT " +
                                                "o.id_order, " +
                                                "CONCAT('ORD-', LPAD(o.id_order, 3, '0')) as kode_order, " +
                                                "o.tgl_pesan, " +
                                                "COALESCE(p.nama, 'Walk-in') as nama_pelanggan, " +
                                                "o.jenis_pesanan, " +
                                                "o.total as total_order, " +
                                                "m.nama_masakan, " +
                                                "d.qty, " +
                                                "(d.subtotal / d.qty) as harga_satuan, " +
                                                "d.subtotal " +
                                                "FROM `order` o " +
                                                "LEFT JOIN pelanggan p ON o.id_pelanggan = p.id_pelanggan " +
                                                "JOIN detail_order d ON o.id_order = d.id_order " +
                                                "JOIN menu_masakan m ON d.id_masakan = m.id_masakan " +
                                                "ORDER BY o.id_order DESC, d.id_detail");
                jasperDesign.setQuery(query);

                // Fields
                // Fields
                ReportGeneratorHelper.addField(jasperDesign, "id_order", Integer.class);
                ReportGeneratorHelper.addField(jasperDesign, "kode_order", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "tgl_pesan", java.sql.Timestamp.class);
                ReportGeneratorHelper.addField(jasperDesign, "nama_pelanggan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "jenis_pesanan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "total_order", Integer.class);
                ReportGeneratorHelper.addField(jasperDesign, "nama_masakan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "qty", Integer.class);
                ReportGeneratorHelper.addField(jasperDesign, "harga_satuan", Integer.class);
                ReportGeneratorHelper.addField(jasperDesign, "subtotal", Integer.class);

                // Group by id_order (untuk master-detail)
                JRDesignGroup orderGroup = new JRDesignGroup();
                orderGroup.setName("OrderGroup");
                orderGroup.setExpression(new JRDesignExpression("$F{id_order}"));
                orderGroup.setMinHeightToStartNewPage(100);
                jasperDesign.addGroup(orderGroup);

                // Title Band
                JRDesignBand titleBand = ReportGeneratorHelper.createTitleBand(jasperDesign, "LAPORAN DETAIL PESANAN");
                jasperDesign.setTitle(titleBand);

                // Group Header (Order Information)
                JRDesignBand groupHeaderBand = new JRDesignBand();
                groupHeaderBand.setHeight(80);

                // Background for order header
                JRDesignRectangle groupHeaderBg = new JRDesignRectangle();
                groupHeaderBg.setX(0);
                groupHeaderBg.setY(0);
                groupHeaderBg.setWidth(535);
                groupHeaderBg.setHeight(75);
                groupHeaderBg.setBackcolor(COLOR_GROUP_BG);
                groupHeaderBg.setMode(net.sf.jasperreports.engine.type.ModeEnum.OPAQUE);
                groupHeaderBand.addElement(groupHeaderBg);

                // Order info
                // Order info
                ReportGeneratorHelper.addStaticText(groupHeaderBand, "Kode Order:", 10, 5, 80, 15, 11f, true);
                ReportGeneratorHelper.addTextField(groupHeaderBand, "$F{kode_order}", 95, 5, 100, 15, null, null, 12f,
                                true);

                ReportGeneratorHelper.addStaticText(groupHeaderBand, "Tanggal:", 10, 23, 80, 15, 10f, false);
                ReportGeneratorHelper.addTextField(groupHeaderBand, "$F{tgl_pesan}", 95, 23, 150, 15,
                                "dd MMMM yyyy HH:mm",
                                null, 10f, false);

                ReportGeneratorHelper.addStaticText(groupHeaderBand, "Pelanggan:", 10, 41, 80, 15, 10f, false);
                ReportGeneratorHelper.addTextField(groupHeaderBand, "$F{nama_pelanggan}", 95, 41, 150, 15, null, null,
                                10f,
                                false);

                ReportGeneratorHelper.addStaticText(groupHeaderBand, "Jenis:", 10, 59, 80, 15, 10f, false);
                ReportGeneratorHelper.addTextField(groupHeaderBand, "$F{jenis_pesanan}", 95, 59, 100, 15, null, null,
                                10f,
                                false);

                // Line separator
                JRDesignLine groupSeparator = new JRDesignLine();
                groupSeparator.setX(0);
                groupSeparator.setY(78);
                groupSeparator.setWidth(535);
                groupSeparator.setHeight(0);
                groupSeparator.setForecolor(ReportGeneratorHelper.COLOR_PRIMARY);
                groupHeaderBand.addElement(groupSeparator);

                ((JRDesignSection) orderGroup.getGroupHeaderSection()).addBand(groupHeaderBand);

                // Column Header for items
                JRDesignBand columnHeaderBand = new JRDesignBand();
                columnHeaderBand.setHeight(25);

                JRDesignRectangle colHeaderBg = new JRDesignRectangle();
                colHeaderBg.setX(0);
                colHeaderBg.setY(0);
                colHeaderBg.setWidth(535);
                colHeaderBg.setHeight(20);
                colHeaderBg.setBackcolor(ReportGeneratorHelper.COLOR_PRIMARY);
                colHeaderBg.setMode(net.sf.jasperreports.engine.type.ModeEnum.OPAQUE);
                columnHeaderBand.addElement(colHeaderBg);

                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Menu", 10, 3, 250, 15, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Qty", 260, 3, 60, 15, Color.WHITE,
                                HorizontalTextAlignEnum.CENTER);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Harga", 320, 3, 100, 15, Color.WHITE,
                                HorizontalTextAlignEnum.RIGHT);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Subtotal", 420, 3, 110, 15, Color.WHITE,
                                HorizontalTextAlignEnum.RIGHT);

                jasperDesign.setColumnHeader(columnHeaderBand);

                // Detail Band (Items)
                JRDesignBand detailBand = new JRDesignBand();
                detailBand.setHeight(22);

                JRDesignLine itemBorder = new JRDesignLine();
                itemBorder.setX(0);
                itemBorder.setY(21);
                itemBorder.setWidth(535);
                itemBorder.setHeight(0);
                itemBorder.setForecolor(ReportGeneratorHelper.COLOR_BORDER);
                detailBand.addElement(itemBorder);

                ReportGeneratorHelper.addTextField(detailBand, "$F{nama_masakan}", 10, 2, 250, 18, null, null, 10f,
                                false);
                ReportGeneratorHelper.addTextField(detailBand, "$F{qty}", 260, 2, 60, 18, "#,##0",
                                HorizontalTextAlignEnum.CENTER, 10f, false);
                ReportGeneratorHelper.addTextField(detailBand, "$F{harga_satuan}", 320, 2, 100, 18, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT, 10f,
                                false);
                ReportGeneratorHelper.addTextField(detailBand, "$F{subtotal}", 420, 2, 110, 18, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT, 10f,
                                false);

                ((JRDesignSection) jasperDesign.getDetailSection()).addBand(detailBand);

                // Group Footer (Total)
                JRDesignBand groupFooterBand = new JRDesignBand();
                groupFooterBand.setHeight(35);

                JRDesignLine footerLine = new JRDesignLine();
                footerLine.setX(0);
                footerLine.setY(5);
                footerLine.setWidth(535);
                footerLine.setHeight(0);
                footerLine.setForecolor(ReportGeneratorHelper.COLOR_PRIMARY);
                groupFooterBand.addElement(footerLine);

                ReportGeneratorHelper.addStaticText(groupFooterBand, "TOTAL ORDER:", 320, 10, 100, 18, 12f, true);
                ReportGeneratorHelper.addTextField(groupFooterBand, "$F{total_order}", 420, 10, 110, 18, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT,
                                12f, true);

                ((JRDesignSection) orderGroup.getGroupFooterSection()).addBand(groupFooterBand);

                // Page Footer
                JRDesignBand pageFooter = ReportGeneratorHelper.createPageFooter();
                jasperDesign.setPageFooter(pageFooter);

                // Save
                String outputPath = ReportGeneratorHelper.REPORTS_DIR + JRXML_FILE;
                JRXmlWriter.writeReport(jasperDesign, outputPath, "UTF-8");
                return outputPath;
        }

}
