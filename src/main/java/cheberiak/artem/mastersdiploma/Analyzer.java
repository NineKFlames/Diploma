package cheberiak.artem.mastersdiploma;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Analyzer {
    private BufferedImage changeMap = null;

    private int changedPixelsAmount = 0;

    private static int darken(int rgbOriginal) {
        return new Color(rgbOriginal).darker().getRGB();
    }

    private int calculateDiff(int rgbOriginal, int rgbTested) {
        if (rgbOriginal == rgbTested) {
            return darken(rgbOriginal);
        } else {
            changedPixelsAmount++;
            return Color.ORANGE.getRGB();
        }
    }

    public void analyze(BufferedImage original, BufferedImage tested) {
        int width = original.getWidth() > tested.getWidth() ? original.getWidth() : tested.getWidth();
        int height = original.getHeight() > tested.getHeight() ? original.getHeight() : tested.getHeight();
        changeMap = new BufferedImage(width, height, original.getType());
        changedPixelsAmount = 0;

        for (int lineIndex = 0; lineIndex < height; lineIndex++)
            for (int pixelIndex = 0; pixelIndex < width; pixelIndex++) {
                changeMap.setRGB(pixelIndex,
                                 lineIndex,
                                 calculateDiff(original.getRGB(pixelIndex, lineIndex),
                                               tested.getRGB(pixelIndex, lineIndex)));
            }
    }

    public int getChangedPixelsAmount() {
        return changedPixelsAmount;
    }

    public BufferedImage getChangeMap() {
        return changeMap;
    }
}
