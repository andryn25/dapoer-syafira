package id.syafira.dapoer.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import java.awt.Color;

/**
 * Sales Analysis Report - Best Selling Menu Items
 */
public class PenjualanPerMenuReportGenerator {

        private static final String JRXML_FILE = "laporan_penjualan_per_menu.jrxml";

        public static String generateJRXML() throws JRException {
                JasperDesign jasperDesign = new JasperDesign();
                jasperDesign.setName("LaporanPenjualanPerMenu");
                jasperDesign.setPageWidth(595);
                jasperDesign.setPageHeight(842);
                jasperDesign.setColumnWidth(535);
                jasperDesign.setLeftMargin(30);
                jasperDesign.setRightMargin(30);
                jasperDesign.setTopMargin(20);
                jasperDesign.setBottomMargin(20);

                // SQL Query with aggregation
                JRDesignQuery query = new JRDesignQuery();
                query.setText(
                                "SELECT " +
                                                "CONCAT('MNU-', LPAD(m.id_masakan, 3, '0')) as kode, " +
                                                "m.nama_masakan, " +
                                                "SUM(do.qty) as total_terjual, " +
                                                "SUM(do.subtotal) as total_pendapatan " +
                                                "FROM menu_masakan m " +
                                                "JOIN detail_order do ON m.id_masakan = do.id_masakan " +
                                                "JOIN `order` o ON do.id_order = o.id_order " +
                                                "JOIN transaksi t ON o.id_order = t.id_order " +
                                                "GROUP BY m.id_masakan, m.nama_masakan " +
                                                "ORDER BY total_terjual DESC");
                jasperDesign.setQuery(query);

                // Fields
                // Fields
                ReportGeneratorHelper.addField(jasperDesign, "kode", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "nama_masakan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "total_terjual", Long.class);
                ReportGeneratorHelper.addField(jasperDesign, "total_pendapatan", Long.class);

                // Title Band
                JRDesignBand titleBand = ReportGeneratorHelper.createTitleBand(jasperDesign,
                                "LAPORAN PENJUALAN PER MENU");
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

                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Kode", 5, 8, 70, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Nama Menu", 75, 8, 250, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Terjual", 325, 8, 100, 20, Color.WHITE,
                                HorizontalTextAlignEnum.RIGHT);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Total Pendapatan", 425, 8, 105, 20, Color.WHITE,
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

                ReportGeneratorHelper.addTextField(detailBand, "$F{kode}", 5, 2, 70, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{nama_masakan}", 75, 2, 250, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{total_terjual}", 325, 2, 100, 20, "#,##0",
                                HorizontalTextAlignEnum.RIGHT);
                ReportGeneratorHelper.addTextField(detailBand, "$F{total_pendapatan}", 425, 2, 105, 20, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT);

                ((JRDesignSection) jasperDesign.getDetailSection()).addBand(detailBand);

                // Summary Band for totals
                JRDesignBand summaryBand = new JRDesignBand();
                summaryBand.setHeight(40);

                JRDesignLine summaryLine = new JRDesignLine();
                summaryLine.setX(0);
                summaryLine.setY(5);
                summaryLine.setWidth(535);
                summaryLine.setHeight(0);
                summaryLine.setForecolor(ReportGeneratorHelper.COLOR_PRIMARY);
                summaryBand.addElement(summaryLine);

                ReportGeneratorHelper.addStaticText(summaryBand, "TOTAL:", 75, 15, 250, 20, 12f, true);

                // Variable for total quantity
                JRDesignVariable totalQtyVar = new JRDesignVariable();
                totalQtyVar.setName("totalQuantity");
                totalQtyVar.setValueClass(Long.class);
                totalQtyVar.setCalculation(net.sf.jasperreports.engine.type.CalculationEnum.SUM);
                totalQtyVar.setExpression(new JRDesignExpression("$F{total_terjual}"));
                jasperDesign.addVariable(totalQtyVar);

                // Variable for total revenue
                JRDesignVariable totalRevVar = new JRDesignVariable();
                totalRevVar.setName("totalRevenue");
                totalRevVar.setValueClass(Long.class);
                totalRevVar.setCalculation(net.sf.jasperreports.engine.type.CalculationEnum.SUM);
                totalRevVar.setExpression(new JRDesignExpression("$F{total_pendapatan}"));
                jasperDesign.addVariable(totalRevVar);

                ReportGeneratorHelper.addTextField(summaryBand, "$V{totalQuantity}", 325, 15, 100, 20, "#,##0",
                                HorizontalTextAlignEnum.RIGHT, 12f, true);
                ReportGeneratorHelper.addTextField(summaryBand, "$V{totalRevenue}", 425, 15, 105, 20, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT, 12f, true);

                jasperDesign.setSummary(summaryBand);

                // Page Footer
                JRDesignBand pageFooter = ReportGeneratorHelper.createPageFooter();
                jasperDesign.setPageFooter(pageFooter);

                // Save
                String outputPath = ReportGeneratorHelper.REPORTS_DIR + JRXML_FILE;
                JRXmlWriter.writeReport(jasperDesign, outputPath, "UTF-8");
                return outputPath;
        }

}
