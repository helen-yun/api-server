package com.platfos.pongift.zxing.service;

import com.google.zxing.*;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.*;
import com.google.zxing.pdf417.PDF417Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.platfos.pongift.util.Util;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;

@Service
public class ZxingService {
    public byte[] generateCodeImage(String text, BarcodeFormat format, int width, int height) throws WriterException, IOException, IllegalArgumentException, NotFoundException {
        return generateCodeImage(text, format, width, height, 0, "#ffffff", "#000000");
    }
    public byte[] generateCodeImage(String text, BarcodeFormat format, int width, int height, int margin, String bgColor, String fgColor) throws WriterException, IOException, IllegalArgumentException, NotFoundException {
        Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
        //hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hintMap.put(EncodeHintType.MARGIN, margin);

        Writer writer;
        switch(format) {
            case EAN_8:  writer = new EAN8Writer(); break;
            case EAN_13: writer = new EAN13Writer(); break;
            case UPC_A: writer = new UPCAWriter(); break;
            case QR_CODE: writer = new QRCodeWriter(); break;
            case CODE_39: writer = new Code39Writer(); break;
            case CODE_128: writer = new Code128Writer(); break;
            case ITF: writer = new ITFWriter(); break;
            case PDF_417: writer = new PDF417Writer(); break;
            case CODABAR: writer = new CodaBarWriter(); break;
            case DATA_MATRIX: writer = new DataMatrixWriter(); break;
            case AZTEC: writer = new AztecWriter(); break;
            default: throw new IllegalArgumentException("No encoder available for format " + format);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        BitMatrix bitMatrix = writer.encode(text, format, width, height, hintMap);


        // Create buffered image to draw to
        BufferedImage image = new BufferedImage(width,
                height, BufferedImage.TYPE_INT_RGB);

        // Iterate through the matrix and draw the pixels to the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayValue = (bitMatrix.get(x, y) ? 0 : 1) & 0xff;
                image.setRGB(x, y, (grayValue == 0 ? 0 : 0xFFFFFF));
            }
        }

        BufferedImageLuminanceSource bils = new BufferedImageLuminanceSource(image);
        HybridBinarizer hb = new HybridBinarizer(bils);

        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(hex2Rgb(fgColor).getRGB(), hex2Rgb(bgColor).getRGB());
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(hb.getBlackMatrix(), matrixToImageConfig);


        int txtMargin = 4;
        int fontSize = 14;
        int cvHeight = image.getHeight() + fontSize + txtMargin;
        BufferedImage newImage = new BufferedImage(image.getWidth(), cvHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImage.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(bufferedImage, 0, 0, hex2Rgb(bgColor), null);

        g.setPaint(hex2Rgb(bgColor));
        g.fillRect(0, image.getHeight(), image.getWidth(), cvHeight);

        Font f = new Font("NanumGothic", Font.PLAIN, fontSize);
        FontRenderContext frc = image.getGraphics().getFontMetrics().getFontRenderContext();
        Rectangle2D rect = f.getStringBounds(text, frc);
        g.setColor(Color.WHITE);

        // add the watermark text
        g.setFont(f);
        g.setColor(hex2Rgb(fgColor));

        if(Util.isNumeric(text)){
            text = String.join("-", text.split("(?<=\\G.{4})"));
        }

        g.drawString(text,
                (int)Math.ceil((image.getWidth()/2)-((rect.getWidth())/2)),
                (int)Math.ceil(cvHeight-txtMargin));
        g.dispose();

        ImageIO.write(newImage, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }
}
