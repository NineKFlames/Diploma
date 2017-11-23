package cheberiak.artem.mastersdiploma;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        try {
            CliHandler handler = new CliHandler(args);
            handler.parse();
            CommandLine commandLine = handler.getCmd();
            BufferedImage result = null;

            if (commandLine.hasOption(CliHandler.ODD_FIELD_PATH_OPTION_STRING)) {
                URL oddFieldURL = Main.class.getClassLoader()
                                            .getResource(commandLine.getOptionValue(
                                                    CliHandler.ODD_FIELD_PATH_OPTION_STRING));
                result = deinterlaceLineInterpolation(loadImage(oddFieldURL), false);
            } else if (commandLine.hasOption(CliHandler.EVEN_FIELD_PATH_OPTION_STRING)) {
                URL oddFieldURL = Main.class.getClassLoader()
                                            .getResource(commandLine.getOptionValue(
                                                    CliHandler.EVEN_FIELD_PATH_OPTION_STRING));
                result = deinterlaceLineInterpolation(loadImage(oddFieldURL), true);
            }

            writeImage(result, commandLine.getOptionValue(CliHandler.RESULT_OPTION_STRING));
        } catch (Exception e) {
            logger.error(e);
            System.exit(1);
        }
    }

    private static void writeImage(BufferedImage img, String pathname) throws IOException {
        String[] split = pathname.split("\\.");
        ImageIO.write(img, split[split.length - 1], new File(pathname));
    }

    private static BufferedImage loadImage(URL resource) throws IOException {
        return ImageIO.read(resource);
    }

    private static BufferedImage deinterlaceLineDuplication(BufferedImage field, boolean isFieldEven) {
        BufferedImage returnValue =
                new BufferedImage(field.getWidth(), field.getHeight(), field.getType());
        Graphics returnValueGraphics = returnValue.getGraphics();

        if (isFieldEven) {
            for (int lineIndex = 1; lineIndex < field.getHeight(); lineIndex += 2) {
                BufferedImage evenLine = field.getSubimage(0, lineIndex, field.getWidth(), 1);
                returnValueGraphics.drawImage(evenLine, 0, lineIndex - 1, null);
                returnValueGraphics.drawImage(evenLine, 0, lineIndex, null);
            }
        } else {
            for (int lineIndex = 0; lineIndex < field.getHeight(); lineIndex += 2) {
                BufferedImage oddLine = field.getSubimage(0, lineIndex, field.getWidth(), 1);
                returnValueGraphics.drawImage(oddLine, 0, lineIndex, null);
                returnValueGraphics.drawImage(oddLine, 0, lineIndex + 1, null);
            }
        }

        returnValueGraphics.dispose();

        return returnValue;
    }

    private static BufferedImage deinterlaceLineInterpolation(BufferedImage field, boolean isFieldEven) {
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
        returnValueGraphics.drawImage(getInterpolatedLine(field.getSubimage(0, field.getHeight() - 2, field.getWidth(), 1)),
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

        returnValue.setRGB(0, 0, getIterpolatedPixelFromPixelList(
                Arrays.asList(new Color(line.getRGB(0, 0), true),
                              new Color(line.getRGB(1, 0), true))));
        returnValue.setRGB(returnValue.getWidth() - 1, 0, getIterpolatedPixelFromPixelList(
                Arrays.asList(new Color(line.getRGB(returnValue.getWidth() - 1, 0), true),
                              new Color(line.getRGB(returnValue.getWidth() - 2, 0), true))));
        return returnValue;
    }

    private static int getInterpolatedPixel(BufferedImage line, int pixelIndex) {
        List<Color> neighborPixels = Arrays.asList(
                new Color(line.getRGB(pixelIndex - 1, 0), true),
                new Color(line.getRGB(pixelIndex, 0), true),
                new Color(line.getRGB(pixelIndex + 1, 0), true));
        return getIterpolatedPixelFromPixelList(neighborPixels);
    }

    private static BufferedImage getInterpolatedLine(BufferedImage oddLineHigher,
                                                     BufferedImage oddLineLower) {
        BufferedImage returnValue = new BufferedImage(oddLineHigher.getWidth(), 1, oddLineHigher.getType());
        
        for (int pixelIndex = 1; pixelIndex < returnValue.getWidth() - 1; pixelIndex++) {
            returnValue.setRGB(pixelIndex,
                               0,
                               getInterpolatedPixel(oddLineHigher, oddLineLower, pixelIndex));
        }

        returnValue.setRGB(0, 0, getIterpolatedPixelFromPixelList(
                Arrays.asList(new Color(oddLineHigher.getRGB(0, 0), true),
                              new Color(oddLineHigher.getRGB(1, 0), true),
                              new Color(oddLineLower.getRGB(0, 0), true),
                              new Color(oddLineLower.getRGB(1, 0), true))));
        returnValue.setRGB(returnValue.getWidth() - 1, 0, getIterpolatedPixelFromPixelList(
                Arrays.asList(new Color(oddLineHigher.getRGB(returnValue.getWidth() - 1, 0), true),
                              new Color(oddLineHigher.getRGB(returnValue.getWidth() - 2, 0), true),
                              new Color(oddLineLower.getRGB(returnValue.getWidth() - 1, 0), true),
                              new Color(oddLineLower.getRGB(returnValue.getWidth() - 2, 0), true))));
        return returnValue;
    }

    private static int getInterpolatedPixel(BufferedImage oddLineHigher, BufferedImage oddLineLower, int pixelIndex) {
        List<Color> neighborPixels = Arrays.asList(
                new Color(oddLineHigher.getRGB(pixelIndex - 1, 0), true),
                new Color(oddLineHigher.getRGB(pixelIndex, 0), true),
                new Color(oddLineHigher.getRGB(pixelIndex + 1, 0), true),
                new Color(oddLineLower.getRGB(pixelIndex - 1, 0), true),
                new Color(oddLineLower.getRGB(pixelIndex, 0), true),
                new Color(oddLineLower.getRGB(pixelIndex + 1, 0), true));
        return getIterpolatedPixelFromPixelList(neighborPixels);
    }

    private static int getIterpolatedPixelFromPixelList(List<Color> neighborPixels) {
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
