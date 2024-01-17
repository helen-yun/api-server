package com.platfos.pongift.ghostdriver.service;

import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.ghostdriver.model.CaptureConfig;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * PhantomJS GhostDriver(Html Screen Shot Image) Service
 */
@Service
public class GhostDriver {
    @Autowired
    ApplicationProperties properties;

    private static DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
    static {
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("takesScreenshot", true);
    }

    /**
     * Screenshot Image(byte array)
     * @param url Http Request URL
     * @param captureConfig Config
     * @return
     */
    public byte[] getScreenshotAsBytes(String url, CaptureConfig captureConfig) {
        if(!StringUtils.isEmpty(properties.getPhantomjsPath())){
            desiredCapabilities.setCapability("phantomjs.binary.path", properties.getPhantomjsPath());
        }

        WebDriver webDriver = new PhantomJSDriver(desiredCapabilities);

        //URL call
        webDriver.get(url);

        //capture set position
        if (captureConfig.getX() > 0 || captureConfig.getY() > 0) {
            webDriver.manage().window().setPosition(new Point(captureConfig.getX(), captureConfig.getY()));
        }

        //capture set size
        if (captureConfig.getWidth() > 0 && captureConfig.getHeight() > 0) {
            webDriver.manage().window().setSize(new Dimension(captureConfig.getWidth(), captureConfig.getHeight()));
        }

        //css, font 적용(javascript 이용)
        try {
            ClassPathResource resource = new ClassPathResource("/phantomjs/cssFontRule.js");
            InputStream in = resource.getInputStream();
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, length, "UTF-8"));
            }
            in.close();
            String cssFontRule = sb.toString();
            ((JavascriptExecutor) webDriver).executeScript(cssFontRule);
            Thread.sleep(1000);
        } catch (Exception e) {e.printStackTrace();}

        //get screen shot Image
        byte[] bytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
        webDriver.quit();
        return bytes;
    }

    /**
     * Screenshot Scaled Image(BufferedImage)
     * @param url Http Request URL
     * @param captureConfig Config
     * @return
     */
    public BufferedImage getScreenshotAsBufferedImage(String url, CaptureConfig captureConfig) {
        byte[] bytes = getScreenshotAsBytes(url, captureConfig);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try {
            BufferedImage captureImage = ImageIO.read(byteArrayInputStream);
            BufferedImage scaledImage = Scalr.crop(captureImage, 0, 0, captureConfig.getWidth(), captureImage.getHeight(), null);
            return scaledImage;
        } catch (IOException e) {e.printStackTrace();}
        return null;
    }

    /**
     * Screenshot Scaled Image(BufferedImage) with DefaultConfig
     * @param url Http Request URL
     * @return
     */
    public BufferedImage getScreenshotAsBufferedImage(String url) {
        return getScreenshotAsBufferedImage(url, CaptureConfig.defaultConfig());
    }
}
