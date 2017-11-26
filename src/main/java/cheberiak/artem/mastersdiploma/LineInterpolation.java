package cheberiak.artem.mastersdiploma;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class LineInterpolation {
    public static BufferedImage deinterlaceLineInterpolation(BufferedImage field, boolean isFieldEven) {
        BufferedImage returnValue =
                new BufferedImage(field.getWidth(), field.getHeight(), field.getType());
        Graphics returnValueGraphics = returnValue.getGraphics();

        if (isFieldEven) {
            interpolateFromEvenField(field, returnValueGraphics);
        } else {
            interpolateFromOddField(field, returnValueGraphics);
        }

        returnValueGraphics.dispose();

        return returnValue;
    }

    private static void interpolateFromOddField(BufferedImage field, Graphics returnValueGraphics) {
        returnValueGraphics.drawImage(field.getSubimage(0, 0, field.getWidth(), 1),
                                      0,
                                      0,
                                      null);
        returnValueGraphics.drawImage(getInterpolatedLine(field.getSubimage(0,
                                                                            field.getHeight() - 2,
                                                                            field.getWidth(),
                                                                            1)),
                                      0,
                                      field.getHeight() - 1,
                                      null);

        for (int lineIndex = 1; lineIndex < field.getHeight() - 1; lineIndex += 2) {
            BufferedImage evenLineHigher = field.getSubimage(0, lineIndex - 1, field.getWidth(), 1);
            BufferedImage evenLineLower = field.getSubimage(0, lineIndex + 1, field.getWidth(), 1);
            returnValueGraphics.drawImage(getInterpolatedLine(evenLineHigher, evenLineLower),
                                          0,
                                          lineIndex,
                                          null);
            returnValueGraphics.drawImage(field.getSubimage(0, lineIndex + 1, field.getWidth(), 1),
                                          0,
                                          lineIndex + 1,
                                          null);
        }
    }

    private static void interpolateFromEvenField(BufferedImage field, Graphics returnValueGraphics) {
        BufferedImage firstEvenLine = field.getSubimage(0, 1, field.getWidth(), 1);
        returnValueGraphics.drawImage(getInterpolatedLine(firstEvenLine),
                                      0,
                                      0,
                                      null);
        returnValueGraphics.drawImage(firstEvenLine,
                                      0,
                                      1,
                                      null);

        for (int lineIndex = 2; lineIndex < field.getHeight(); lineIndex += 2) {
            BufferedImage evenLineHigher = field.getSubimage(0, lineIndex - 1, field.getWidth(), 1);
            BufferedImage evenLineLower = field.getSubimage(0, lineIndex + 1, field.getWidth(), 1);
            returnValueGraphics.drawImage(getInterpolatedLine(evenLineHigher, evenLineLower),
                                          0,
                                          lineIndex,
                                          null);
            returnValueGraphics.drawImage(field.getSubimage(0, lineIndex + 1, field.getWidth(), 1),
                                          0,
                                          lineIndex + 1,
                                          null);
        }
    }

    private static BufferedImage getInterpolatedLine(BufferedImage line) {
        BufferedImage returnValue = new BufferedImage(line.getWidth(), 1, line.getType());

        for (int pixelIndex = 1; pixelIndex < returnValue.getWidth() - 1; pixelIndex++) {
            returnValue.setRGB(pixelIndex,
                               0,
                               getInterpolatedPixel(line, pixelIndex));
        }

        returnValue.setRGB(0, 0, getInterpolatedPixelFromPixelList(
                Arrays.asList(new Color(line.getRGB(0, 0), true),
                              new Color(line.getRGB(1, 0), true))));
        returnValue.setRGB(returnValue.getWidth() - 1, 0, getInterpolatedPixelFromPixelList(
                Arrays.asList(new Color(line.getRGB(returnValue.getWidth() - 1, 0), true),
                              new Color(line.getRGB(returnValue.getWidth() - 2, 0), true))));
        return returnValue;
    }

    private static int getInterpolatedPixel(BufferedImage line, int pixelIndex) {
        java.util.List<Color> neighborPixels = Arrays.asList(
                new Color(line.getRGB(pixelIndex - 1, 0), true),
                new Color(line.getRGB(pixelIndex, 0), true),
                new Color(line.getRGB(pixelIndex + 1, 0), true));
        return getInterpolatedPixelFromPixelList(neighborPixels);
    }

    private static BufferedImage getInterpolatedLine(BufferedImage oddLineHigher,
                                                     BufferedImage oddLineLower) {
        BufferedImage returnValue = new BufferedImage(oddLineHigher.getWidth(), 1, oddLineHigher.getType());

        for (int pixelIndex = 1; pixelIndex < returnValue.getWidth() - 1; pixelIndex++) {
            returnValue.setRGB(pixelIndex,
                               0,
                               getInterpolatedPixel(oddLineHigher, oddLineLower, pixelIndex));
        }

        returnValue.setRGB(0, 0, getInterpolatedPixelFromPixelList(
                Arrays.asList(new Color(oddLineHigher.getRGB(0, 0), true),
                              new Color(oddLineHigher.getRGB(1, 0), true),
                              new Color(oddLineLower.getRGB(0, 0), true),
                              new Color(oddLineLower.getRGB(1, 0), true))));
        returnValue.setRGB(returnValue.getWidth() - 1, 0, getInterpolatedPixelFromPixelList(
                Arrays.asList(new Color(oddLineHigher.getRGB(returnValue.getWidth() - 1, 0), true),
                              new Color(oddLineHigher.getRGB(returnValue.getWidth() - 2, 0), true),
                              new Color(oddLineLower.getRGB(returnValue.getWidth() - 1, 0), true),
                              new Color(oddLineLower.getRGB(returnValue.getWidth() - 2, 0), true))));
        return returnValue;
    }

    private static int getInterpolatedPixel(BufferedImage oddLineHigher, BufferedImage oddLineLower, int pixelIndex) {
        java.util.List<Color> neighborPixels = Arrays.asList(
                new Color(oddLineHigher.getRGB(pixelIndex - 1, 0), true),
                new Color(oddLineHigher.getRGB(pixelIndex, 0), true),
                new Color(oddLineHigher.getRGB(pixelIndex + 1, 0), true),
                new Color(oddLineLower.getRGB(pixelIndex - 1, 0), true),
                new Color(oddLineLower.getRGB(pixelIndex, 0), true),
                new Color(oddLineLower.getRGB(pixelIndex + 1, 0), true));
        return getInterpolatedPixelFromPixelList(neighborPixels);
    }

    private static int getInterpolatedPixelFromPixelList(List<Color> neighborPixels) {
        int r = 0;
        int g = 0;
        int b = 0;
        int a = 0;

        for (Color pixel : neighborPixels) {
            r += pixel.getRed();
            g += pixel.getGreen();
            b += pixel.getBlue();
            a += pixel.getAlpha();
        }

        r /= neighborPixels.size();
        g /= neighborPixels.size();
        b /= neighborPixels.size();
        a /= neighborPixels.size();

        return new Color(r, g, b, a).getRGB();
    }
}
