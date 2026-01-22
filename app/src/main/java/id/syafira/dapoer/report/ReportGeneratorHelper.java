package id.syafira.dapoer.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;

import java.awt.Color;
import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class for Report Generators to specific standardized layout and
 * components.
 */
public class ReportGeneratorHelper {

    public static final String REPORTS_DIR = Paths.get(
            System.getProperty("user.dir"),
            "src", "main", "resources", "reports").toString() + File.separator;

    public static final Color COLOR_PRIMARY = new Color(16, 185, 129);
    public static final Color COLOR_HEADER_BG = new Color(236, 253, 245);
    public static final Color COLOR_BORDER = new Color(209, 250, 229);

    public static JRDesignBand createTitleBand(JasperDesign jasperDesign, String reportTitle) {
        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(150);

        JRDesignRectangle headerBg = new JRDesignRectangle();
        headerBg.setX(0);
        headerBg.setY(0);
        headerBg.setWidth(535);
        headerBg.setHeight(90);
        headerBg.setBackcolor(COLOR_HEADER_BG);
        headerBg.setMode(net.sf.jasperreports.engine.type.ModeEnum.OPAQUE);
        titleBand.addElement(headerBg);

        try {
            JRDesignImage logo = new JRDesignImage(jasperDesign);
            logo.setX(10);
            logo.setY(15);
            logo.setWidth(60);
            logo.setHeight(60);
            logo.setScaleImage(net.sf.jasperreports.engine.type.ScaleImageEnum.RETAIN_SHAPE);

            // Expression yang akan di-evaluate saat runtime
            JRDesignExpression logoExpression = new JRDesignExpression();
            logoExpression.setText(
                    "this.getClass().getResource(\"/images/logo_icon.png\")");
            logo.setExpression(logoExpression);

            titleBand.addElement(logo);
        } catch (Exception e) {
            System.err.println("Warning: Logo tidak dapat dimuat - " + e.getMessage());
        }

        JRDesignStaticText companyName = new JRDesignStaticText();
        companyName.setText("DAPOER SYAFIRA");
        companyName.setX(50);
        companyName.setY(10);
        companyName.setWidth(445);
        companyName.setHeight(35);
        companyName.setFontSize(20f);
        companyName.setBold(true);
        companyName.setForecolor(COLOR_PRIMARY);
        companyName.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        titleBand.addElement(companyName);

        // Address
        JRDesignStaticText address = new JRDesignStaticText();
        address.setText("Kp. Sudimampir, Jl. Teratai Rt 03/03 No. 49,\n" +
                "Kel. Cimanggis, Kec. Bojonggede, Kab. Bogor\n" +
                "Telp. 0858-8841-2088 Email. lmsas84atsyah@gmail.com");
        address.setX(50);
        address.setY(40);
        address.setWidth(445);
        address.setHeight(45);
        address.setFontSize(10f);
        address.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        titleBand.addElement(address);

        JRDesignStaticText titleText = new JRDesignStaticText();
        titleText.setText(reportTitle);
        titleText.setX(50);
        titleText.setY(110);
        titleText.setWidth(445);
        titleText.setHeight(25);
        titleText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        titleText.setFontSize(16f);
        titleText.setBold(true);
        titleBand.addElement(titleText);

        JRDesignLine separatorLine = new JRDesignLine();
        separatorLine.setX(0);
        separatorLine.setY(145);
        separatorLine.setWidth(535);
        separatorLine.setHeight(0);
        separatorLine.setForecolor(COLOR_PRIMARY);
        titleBand.addElement(separatorLine);

        return titleBand;
    }

    public static JRDesignBand createPageFooter() {
        JRDesignBand pageFooter = new JRDesignBand();
        pageFooter.setHeight(140);

        JRDesignLine footerSeparator = new JRDesignLine();
        footerSeparator.setX(0);
        footerSeparator.setY(10);
        footerSeparator.setWidth(535);
        footerSeparator.setHeight(0);
        footerSeparator.setForecolor(COLOR_BORDER);
        pageFooter.addElement(footerSeparator);

        addSignatureSection(pageFooter, 50, "Mengetahui,");
        addSignatureSection(pageFooter, 350, "Dibuat Oleh,");

        JRDesignTextField pageNumber = new JRDesignTextField();
        pageNumber.setExpression(new JRDesignExpression("\"Page \" + $V{PAGE_NUMBER}"));
        pageNumber.setX(0);
        pageNumber.setY(125);
        pageNumber.setWidth(535);
        pageNumber.setHeight(10);
        pageNumber.setFontSize(8f);
        pageNumber.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        pageFooter.addElement(pageNumber);

        return pageFooter;
    }

    private static void addSignatureSection(JRDesignBand band, int x, String label) {
        @SuppressWarnings("deprecation")
        String currentDate = new SimpleDateFormat("EEEE dd MMMM yyyy", new java.util.Locale("id", "ID"))
                .format(new Date());

        JRDesignStaticText date = new JRDesignStaticText();
        date.setText("Jakarta, " + currentDate);
        date.setX(x);
        date.setY(20);
        date.setWidth(150);
        date.setHeight(15);
        date.setFontSize(9f);
        date.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        band.addElement(date);

        JRDesignStaticText signatureLabel = new JRDesignStaticText();
        signatureLabel.setText(label);
        signatureLabel.setX(x);
        signatureLabel.setY(50);
        signatureLabel.setWidth(150);
        signatureLabel.setHeight(15);
        signatureLabel.setFontSize(10f);
        band.addElement(signatureLabel);

        JRDesignLine signatureLine = new JRDesignLine();
        signatureLine.setX(x);
        signatureLine.setY(105);
        signatureLine.setWidth(150);
        signatureLine.setHeight(0);
        band.addElement(signatureLine);

        JRDesignStaticText name = new JRDesignStaticText();
        name.setText("(________________)");
        name.setX(x);
        name.setY(110);
        name.setWidth(150);
        name.setHeight(15);
        name.setFontSize(9f);
        name.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        band.addElement(name);
    }

    public static void addField(JasperDesign design, String name, Class<?> valueClass) throws JRException {
        JRDesignField field = new JRDesignField();
        field.setName(name);
        field.setValueClass(valueClass);
        try {
            design.addField(field);
        } catch (JRException e) {
            // Ignore if field already exists (though design should be empty initially)
            // But standard behavior involves throwing so we rethrow
            throw e;
        }
    }

    public static void addHeaderText(JRDesignBand band, String text, int x, int y, int width, int height,
            Color color) {
        addHeaderText(band, text, x, y, width, height, color, null);
    }

    // Overloaded for compatibility with MenuMasakanReportGenerator which uses
    // boolean bold first
    public static void addHeaderText(JRDesignBand band, String text, int x, int y, int width, int height,
            boolean bold, HorizontalTextAlignEnum align, Color color) {
        JRDesignStaticText staticText = new JRDesignStaticText();
        staticText.setText(text);
        staticText.setX(x);
        staticText.setY(y);
        staticText.setWidth(width);
        staticText.setHeight(height);
        staticText.setBold(bold);
        staticText.setFontSize(11f);
        if (color != null) {
            staticText.setForecolor(color);
        }
        if (align != null) {
            staticText.setHorizontalTextAlign(align);
        }
        band.addElement(staticText);
    }

    public static void addHeaderText(JRDesignBand band, String text, int x, int y, int width, int height,
            Color color, HorizontalTextAlignEnum align) {
        addHeaderText(band, text, x, y, width, height, true, align, color);
    }

    public static void addTextField(JRDesignBand band, String expression, int x, int y, int width, int height,
            String pattern, HorizontalTextAlignEnum align) {
        JRDesignTextField textField = new JRDesignTextField();
        textField.setExpression(new JRDesignExpression(expression));
        textField.setX(x);
        textField.setY(y);
        textField.setWidth(width);
        textField.setHeight(height);
        textField.setFontSize(10f); // Default font size
        if (pattern != null) {
            textField.setPattern(pattern);
        }
        if (align != null) {
            textField.setHorizontalTextAlign(align);
        }
        band.addElement(textField);
    }

    // Overloaded to accept font size and bold if needed, or we adapt the callers to
    // use the simpler one if possible.
    // OrderDetailReportGenerator uses: addTextField(groupHeaderBand,
    // "$F{kode_order}", 95, 5, 100, 15, null, null, 12f, true);
    public static void addTextField(JRDesignBand band, String expression, int x, int y, int width, int height,
            String pattern, HorizontalTextAlignEnum align, float fontSize, boolean bold) {
        JRDesignTextField textField = new JRDesignTextField();
        textField.setExpression(new JRDesignExpression(expression));
        textField.setX(x);
        textField.setY(y);
        textField.setWidth(width);
        textField.setHeight(height);
        textField.setFontSize(fontSize);
        textField.setBold(bold);
        if (pattern != null) {
            textField.setPattern(pattern);
        }
        if (align != null) {
            textField.setHorizontalTextAlign(align);
        }
        band.addElement(textField);
    }

    public static void addStaticText(JRDesignBand band, String text, int x, int y, int width, int height,
            float fontSize, boolean bold) {
        JRDesignStaticText staticText = new JRDesignStaticText();
        staticText.setText(text);
        staticText.setX(x);
        staticText.setY(y);
        staticText.setWidth(width);
        staticText.setHeight(height);
        staticText.setFontSize(fontSize);
        staticText.setBold(bold);
        band.addElement(staticText);
    }
}
