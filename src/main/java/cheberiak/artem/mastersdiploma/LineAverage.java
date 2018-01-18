package cheberiak.artem.mastersdiploma;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LineAverage implements Deinterlacer {
    public static BufferedImage deinterlace(BufferedImage field, boolean isFieldEven) {
        BufferedImage returnValue =
                new BufferedImage(field.getWidth(), field.getHeight(), field.getType());
        Graphics returnValueGraphics = returnValue.getGraphics();

        if (isFieldEven) {
            averageFromEvenField(field, returnValueGraphics);
        } else {
            averageFromOddField(field, returnValueGraphics);
        }

        returnValueGraphics.dispose();

        return returnValue;
    }

    private static void averageFromOddField(BufferedImage field, Graphics returnValueGraphics) {
        returnValueGraphics.drawImage(field.getSubimage(0, 0, field.getWidth(), 1),
                                      0,
                                      0,
                                      null);
        returnValueGraphics.drawImage(field.getSubimage(0,
                                                        field.getHeight() - 2,
                                                        field.getWidth(),
                                                        1),
                                      0,
                                      field.getHeight() - 1,
                                      null);

        for (int lineIndex = 1; lineIndex < field.getHeight() - 1; lineIndex += 2) {
            BufferedImage evenLineHigher = field.getSubimage(0, lineIndex - 1, field.getWidth(), 1);
            BufferedImage evenLineLower = field.getSubimage(0, lineIndex + 1, field.getWidth(), 1);
            returnValueGraphics.drawImage(getAverageLine(evenLineHigher, evenLineLower),
                                          0,
                                          lineIndex,
                                          null);
            returnValueGraphics.drawImage(field.getSubimage(0, lineIndex + 1, field.getWidth(), 1),
                                          0,
                                          lineIndex + 1,
                                          null);
        }
    }

    private static void averageFromEvenField(BufferedImage field, Graphics returnValueGraphics) {
        BufferedImage firstEvenLine = field.getSubimage(0, 1, field.getWidth(), 1);
        returnValueGraphics.drawImage(firstEvenLine,
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
            returnValueGraphics.drawImage(getAverageLine(evenLineHigher, evenLineLower),
                                          0,
                                          lineIndex,
                                          null);
            returnValueGraphics.drawImage(field.getSubimage(0, lineIndex + 1, field.getWidth(), 1),
                                          0,
                                          lineIndex + 1,
                                          null);
        }
    }

    private static BufferedImage getAverageLine(BufferedImage oddLineHigher,
                                                BufferedImage oddLineLower) {
        BufferedImage returnValue = new BufferedImage(oddLineHigher.getWidth(), 1, oddLineHigher.getType());

        for (int pixelIndex = 0; pixelIndex < returnValue.getWidth(); pixelIndex++) {
            returnValue.setRGB(pixelIndex,
                               0,
                               getAveragePixel(oddLineHigher, oddLineLower, pixelIndex));
        }

        return returnValue;
    }

    private static int getAveragePixel(BufferedImage oddLineHigher, BufferedImage oddLineLower, int pixelIndex) {
        Color pixelHigher = new Color(oddLineHigher.getRGB(pixelIndex, 0), true);
        Color pixelLower = new Color(oddLineLower.getRGB(pixelIndex, 0), true);

        int r = (pixelHigher.getRed() + pixelLower.getRed()) / 2;
        int g = (pixelHigher.getGreen() + pixelLower.getGreen()) / 2;
        int b = (pixelHigher.getBlue() + pixelLower.getBlue()) / 2;
        int a = (pixelHigher.getAlpha() + pixelLower.getAlpha()) / 2;

        return new Color(r, g, b, a).getRGB();
    }
}
