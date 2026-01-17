package id.syafira.dapoer.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

/**
 * Receipt Report Generator for Thermal Printers or Small Slips
 */
public class ReceiptReportGenerator {

        private static final String JRXML_FILE = "laporan_struk.jrxml";

        public static String generateJRXML() throws JRException {
                JasperDesign jasperDesign = new JasperDesign();
                jasperDesign.setName("StrukPembayaran");
                // Smaller width for receipt (approx 80mm)
                jasperDesign.setPageWidth(230);
                jasperDesign.setPageHeight(842); // Long enough for many items
                jasperDesign.setColumnWidth(210);
                jasperDesign.setLeftMargin(10);
                jasperDesign.setRightMargin(10);
                jasperDesign.setTopMargin(10);
                jasperDesign.setBottomMargin(10);

                // Parameters
                JRDesignParameter orderIdParam = new JRDesignParameter();
                orderIdParam.setName("ORDER_ID");
                orderIdParam.setValueClass(Integer.class);
                jasperDesign.addParameter(orderIdParam);

                JRDesignParameter bayarParam = new JRDesignParameter();
                bayarParam.setName("UANG_BAYAR");
                bayarParam.setValueClass(Long.class);
                jasperDesign.addParameter(bayarParam);

                JRDesignParameter kembaliParam = new JRDesignParameter();
                kembaliParam.setName("KEMBALIAN");
                kembaliParam.setValueClass(Long.class);
                jasperDesign.addParameter(kembaliParam);

                // Query - Specific for one order with customer info
                JRDesignQuery query = new JRDesignQuery();
                query.setText(
                                "SELECT " +
                                                "o.id_order, " +
                                                "CONCAT('ORD-', LPAD(o.id_order, 3, '0')) as kode_order, " +
                                                "o.tgl_pesan, " +
                                                "u.nama_user as kasir, " +
                                                "COALESCE(p.nama, 'Walk-in') as nama_pelanggan, " +
                                                "o.jenis_pesanan, " +
                                                "o.catatan, " +
                                                "m.nama_masakan, " +
                                                "d.qty, " +
                                                "(d.subtotal / d.qty) as harga, " +
                                                "d.subtotal, " +
                                                "o.total as total_akhir " +
                                                "FROM `order` o " +
                                                "JOIN detail_order d ON o.id_order = d.id_order " +
                                                "JOIN menu_masakan m ON d.id_masakan = m.id_masakan " +
                                                "JOIN user u ON o.id_user = u.id_user " +
                                                "LEFT JOIN pelanggan p ON o.id_pelanggan = p.id_pelanggan " +
                                                "WHERE o.id_order = $P{ORDER_ID}");
                jasperDesign.setQuery(query);

                // Fields
                ReportGeneratorHelper.addField(jasperDesign, "id_order", Integer.class);
                ReportGeneratorHelper.addField(jasperDesign, "kode_order", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "tgl_pesan", java.sql.Timestamp.class);
                ReportGeneratorHelper.addField(jasperDesign, "kasir", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "nama_pelanggan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "jenis_pesanan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "catatan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "nama_masakan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "qty", Integer.class);
                ReportGeneratorHelper.addField(jasperDesign, "harga", Integer.class);
                ReportGeneratorHelper.addField(jasperDesign, "subtotal", Integer.class);
                ReportGeneratorHelper.addField(jasperDesign, "total_akhir", Integer.class);

                // Header Band (Store Info + Transaction Info)
                JRDesignBand titleBand = new JRDesignBand();
                titleBand.setHeight(135);

                ReportGeneratorHelper.addStaticText(titleBand, "DAPOER SYAFIRA", 0, 0, 210, 20, 14f, true);
                ((JRDesignStaticText) titleBand.getElements()[0])
                                .setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);

                ReportGeneratorHelper.addStaticText(titleBand, "Jl. Teratai No. 49, Bogor", 0, 20, 210, 15, 8f, false);
                ((JRDesignStaticText) titleBand.getElements()[1])
                                .setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);

                JRDesignLine headerLine = new JRDesignLine();
                headerLine.setX(0);
                headerLine.setY(40);
                headerLine.setWidth(210);
                headerLine.setHeight(0);
                titleBand.addElement(headerLine);

                // Transaction Info
                ReportGeneratorHelper.addTextField(titleBand, "\"No. Transaksi : \" + $F{kode_order}", 0, 45, 210, 15,
                                null,
                                null, 8f, true);
                ReportGeneratorHelper.addTextField(titleBand,
                                "\"Tanggal : \" + new java.text.SimpleDateFormat(\"dd/MM/yy HH:mm\").format($F{tgl_pesan})",
                                0, 60, 210,
                                15, null, null, 8f, false);
                ReportGeneratorHelper.addTextField(titleBand, "\"Kasir   : \" + $F{kasir}", 0, 75, 210, 15, null, null,
                                8f,
                                false);

                ReportGeneratorHelper.addTextField(titleBand, "\"Pelanggan : \" + $F{nama_pelanggan}", 0, 95, 210, 15,
                                null,
                                null, 8f, true);
                ReportGeneratorHelper.addTextField(titleBand, "\"Tipe      : \" + $F{jenis_pesanan}", 0, 110, 210, 15,
                                null,
                                null, 8f, false);

                jasperDesign.setTitle(titleBand);

                // Column Header
                JRDesignBand columnHeader = new JRDesignBand();
                columnHeader.setHeight(5);
                JRDesignLine topSeparator = new JRDesignLine();
                topSeparator.setX(0);
                topSeparator.setY(2);
                topSeparator.setWidth(210);
                topSeparator.setHeight(0);
                columnHeader.addElement(topSeparator);
                jasperDesign.setColumnHeader(columnHeader);

                // Detail Band (Items)
                JRDesignBand detailBand = new JRDesignBand();
                detailBand.setHeight(30);

                ReportGeneratorHelper.addTextField(detailBand, "$F{nama_masakan}", 0, 2, 210, 12, null, null, 9f, true);
                ReportGeneratorHelper.addTextField(detailBand, "$F{qty} + \" x \" + $F{harga}", 10, 14, 100, 12,
                                "Rp #,##0",
                                null, 8f, false);
                ReportGeneratorHelper.addTextField(detailBand, "$F{subtotal}", 110, 14, 100, 12, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT, 8f, false);

                ((JRDesignSection) jasperDesign.getDetailSection()).addBand(detailBand);

                // Summary Band (Totals + Feedback)
                JRDesignBand summaryBand = new JRDesignBand();
                summaryBand.setHeight(130);

                JRDesignLine bottomSeparator = new JRDesignLine();
                bottomSeparator.setX(0);
                bottomSeparator.setY(5);
                bottomSeparator.setWidth(210);
                bottomSeparator.setHeight(0);
                summaryBand.addElement(bottomSeparator);

                ReportGeneratorHelper.addStaticText(summaryBand, "TOTAL:", 0, 10, 100, 15, 10f, true);
                ReportGeneratorHelper.addTextField(summaryBand, "$F{total_akhir}", 110, 10, 100, 15, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT, 10f, true);

                ReportGeneratorHelper.addStaticText(summaryBand, "BAYAR:", 0, 25, 100, 15, 9f, false);
                ReportGeneratorHelper.addTextField(summaryBand, "$P{UANG_BAYAR}", 110, 25, 100, 15, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT, 9f, false);

                ReportGeneratorHelper.addStaticText(summaryBand, "KEMBALI:", 0, 40, 100, 15, 9f, false);
                ReportGeneratorHelper.addTextField(summaryBand, "$P{KEMBALIAN}", 110, 40, 100, 15, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT, 9f, false);

                // Notes section
                ReportGeneratorHelper.addTextField(summaryBand,
                                "\"Catatan: \" + ($F{catatan} == null ? \"-\" : $F{catatan})",
                                0, 65, 210, 25, null, null, 8f, false);

                JRDesignLine footerLine = new JRDesignLine();
                footerLine.setX(30);
                footerLine.setY(95);
                footerLine.setWidth(150);
                footerLine.setHeight(0);
                summaryBand.addElement(footerLine);

                ReportGeneratorHelper.addStaticText(summaryBand, "Terima kasih sudah memesan!", 0, 100, 210, 15, 8f,
                                true);
                ((JRDesignStaticText) summaryBand.getElements()[summaryBand.getElements().length - 1])
                                .setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);

                ReportGeneratorHelper.addStaticText(summaryBand, "Instagram: @dapoersyafira", 0, 115, 210, 15, 7f,
                                false);
                ((JRDesignStaticText) summaryBand.getElements()[summaryBand.getElements().length - 1])
                                .setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);

                jasperDesign.setSummary(summaryBand);

                // Save
                String outputPath = ReportGeneratorHelper.REPORTS_DIR + JRXML_FILE;
                JRXmlWriter.writeReport(jasperDesign, outputPath, "UTF-8");
                return outputPath;
        }
}
