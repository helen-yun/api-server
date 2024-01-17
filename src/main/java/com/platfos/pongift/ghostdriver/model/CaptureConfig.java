package com.platfos.pongift.ghostdriver.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptureConfig {
    /** capture x 좌표 **/
    private int x;
    /** capture y 좌표 **/
    private int y;
    /** capture 너비 좌표 **/
    private int width;
    /** capture 높이 좌표 **/
    private int height;

    /**
     * Default Config
     * @return
     */
    public static CaptureConfig defaultConfig() {
        CaptureConfig captureConfig = new CaptureConfig();
        captureConfig.setX(0);
        captureConfig.setY(0);
        captureConfig.setWidth(-1);
        captureConfig.setHeight(-1);

        return captureConfig;
    }
}
