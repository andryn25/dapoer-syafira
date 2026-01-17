package id.syafira.dapoer.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignConditionalStyle;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import java.awt.Color;

/**
 * Stock Alert Report - Menu Items with Low Stock
 */
public class StokAlertReportGenerator {

        private static final String JRXML_FILE = "laporan_stok_alert.jrxml";

        private static final Color COLOR_DANGER = new Color(239, 68, 68); // Red for alerts

        public static String generateJRXML() throws JRException {
                JasperDesign jasperDesign = new JasperDesign();
                jasperDesign.setName("LaporanStokAlert");
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
                                                "CONCAT('MNU-', LPAD(id_masakan, 3, '0')) as kode, " +
                                                "nama_masakan, " +
                                                "stok, " +
                                                "CASE " +
                                                "  WHEN stok <= 0 THEN 'HABIS' " +
                                                "  WHEN stok < 20 THEN 'MENIPIS' " +
                                                "  ELSE 'AMAN' " +
                                                "END as status_stok " +
                                                "FROM menu_masakan " +
                                                "WHERE stok < 20 " +
                                                "ORDER BY stok ASC, nama_masakan");
                jasperDesign.setQuery(query);

                // Fields
                // Fields
                ReportGeneratorHelper.addField(jasperDesign, "kode", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "nama_masakan", String.class);
                ReportGeneratorHelper.addField(jasperDesign, "stok", Integer.class);
                ReportGeneratorHelper.addField(jasperDesign, "status_stok", String.class);

                // Title Band
                JRDesignBand titleBand = ReportGeneratorHelper.createTitleBand(jasperDesign, "LAPORAN ALERT STOK");
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
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Nama Menu", 75, 8, 300, 20, Color.WHITE);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Stok", 375, 8, 80, 20, Color.WHITE,
                                HorizontalTextAlignEnum.CENTER);
                ReportGeneratorHelper.addHeaderText(columnHeaderBand, "Status", 455, 8, 75, 20, Color.WHITE,
                                HorizontalTextAlignEnum.CENTER);

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
                ReportGeneratorHelper.addTextField(detailBand, "$F{nama_masakan}", 75, 2, 300, 20, null, null);
                ReportGeneratorHelper.addTextField(detailBand, "$F{stok}", 375, 2, 80, 20, null,
                                HorizontalTextAlignEnum.CENTER);

                // Conditional Style for Status
                JRDesignStyle normalStyle = new JRDesignStyle();
                normalStyle.setName("StatusStyle");
                normalStyle.setDefault(true);
                normalStyle.setFontSize(10f);

                JRDesignConditionalStyle dangerStyle = new JRDesignConditionalStyle();
                dangerStyle.setConditionExpression(new JRDesignExpression("$F{stok} < 20"));
                dangerStyle.setForecolor(COLOR_DANGER);
                dangerStyle.setBold(true);

                normalStyle.addConditionalStyle(dangerStyle);
                jasperDesign.addStyle(normalStyle);

                // Status field with conditional coloring
                JRDesignTextField statusField = new JRDesignTextField();
                statusField.setExpression(new JRDesignExpression("$F{status_stok}"));
                statusField.setX(455);
                statusField.setY(2);
                statusField.setWidth(75);
                statusField.setHeight(20);
                statusField.setStyle(normalStyle);
                statusField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
                detailBand.addElement(statusField);

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
