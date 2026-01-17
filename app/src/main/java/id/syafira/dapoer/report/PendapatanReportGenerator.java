package id.syafira.dapoer.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import java.awt.Color;

/**
 * Revenue Report - Daily Income Summary
 */
public class PendapatanReportGenerator {

        private static final String JRXML_FILE = "laporan_pendapatan.jrxml";

        public static String generateJRXML() throws JRException {
                JasperDesign jasperDesign = new JasperDesign();
                jasperDesign.setName("LaporanPendapatan");
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
                                                "DATE(tanggal) as tanggal, " +
                                                "COUNT(id_transaksi) as jumlah_transaksi, " +
                                                "SUM(total_bayar) as total_pendapatan " +
                                                "FROM transaksi " +
                                                "GROUP BY DATE(tanggal) " +
                                                "ORDER BY tanggal DESC");
                jasperDesign.setQuery(query);

                // Fields
                // Fields
                ReportGeneratorHelper.addField(jasperDesign, "tanggal", java.sql.Date.class);
                ReportGeneratorHelper.addField(jasperDesign, "jumlah_transaksi", Long.class);
                ReportGeneratorHelper.addField(jasperDesign, "total_pendapatan", Long.class);

                // Title Band
                JRDesignBand titleBand = ReportGeneratorHelper.createTitleBand(jasperDesign, "LAPORAN PENDAPATAN");
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

                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Tanggal", 5, 8, 200, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Jumlah Transaksi", 205, 8, 150, 20, Color.WHITE,
                                HorizontalTextAlignEnum.RIGHT);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Total Pendapatan", 355, 8, 175, 20, Color.WHITE,
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

                ReportGeneratorHelper.addTextField(detailBand, "$F{tanggal}", 5, 2, 200, 20, "EEEE, dd MMMM yyyy",
                                null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{jumlah_transaksi}", 205, 2, 150, 20, "#,##0",
                                HorizontalTextAlignEnum.RIGHT);
                ReportGeneratorHelper.addTextField(detailBand, "$F{total_pendapatan}", 355, 2, 175, 20, "Rp #,##0",
                                HorizontalTextAlignEnum.RIGHT);

                ((JRDesignSection) jasperDesign.getDetailSection()).addBand(detailBand);

                // Summary Band for grand totals
                JRDesignBand summaryBand = new JRDesignBand();
                summaryBand.setHeight(40);

                JRDesignLine summaryLine = new JRDesignLine();
                summaryLine.setX(0);
                summaryLine.setY(5);
                summaryLine.setWidth(535);
                summaryLine.setHeight(0);
                summaryLine.setForecolor(ReportGeneratorHelper.COLOR_PRIMARY);
                summaryBand.addElement(summaryLine);

                ReportGeneratorHelper.addStaticText(summaryBand, "GRAND TOTAL:", 5, 15, 200, 20, 12f, true);

                // Variable for total transactions
                JRDesignVariable totalTrxVar = new JRDesignVariable();
                totalTrxVar.setName("totalTransactions");
                totalTrxVar.setValueClass(Long.class);
                totalTrxVar.setCalculation(net.sf.jasperreports.engine.type.CalculationEnum.SUM);
                totalTrxVar.setExpression(new JRDesignExpression("$F{jumlah_transaksi}"));
                jasperDesign.addVariable(totalTrxVar);

                // Variable for total revenue
                JRDesignVariable totalRevVar = new JRDesignVariable();
                totalRevVar.setName("grandTotalRevenue");
                totalRevVar.setValueClass(Long.class);
                totalRevVar.setCalculation(net.sf.jasperreports.engine.type.CalculationEnum.SUM);
                totalRevVar.setExpression(new JRDesignExpression("$F{total_pendapatan}"));
                jasperDesign.addVariable(totalRevVar);

                ReportGeneratorHelper.addTextField(summaryBand, "$V{totalTransactions}", 205, 15, 150, 20, "#,##0",
                                HorizontalTextAlignEnum.RIGHT, 12f, true);
                ReportGeneratorHelper.addTextField(summaryBand, "$V{grandTotalRevenue}", 355, 15, 175, 20, "Rp #,##0",
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
