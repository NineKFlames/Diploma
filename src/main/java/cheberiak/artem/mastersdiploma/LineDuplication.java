package cheberiak.artem.mastersdiploma;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LineDuplication implements Deinterlacer {
    public static BufferedImage deinterlace(BufferedImage field, boolean isFieldEven) {
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
}
