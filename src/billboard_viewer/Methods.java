package billboard_viewer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;


public class Methods {


    private BufferedImage produceImageBuffer(String pictureUrl, String pictureData) throws IOException {
        BufferedImage imageBuffer = null;

        if (pictureUrl == null) {
            // process base64 image
            byte[] imageBytes = Base64.getDecoder().decode(pictureData);
            ByteArrayInputStream imageBytesStream = new ByteArrayInputStream(imageBytes);
            imageBuffer = ImageIO.read(imageBytesStream);
        }
        else {
            // process image from URL
            URL url = new URL(pictureUrl);
            imageBuffer = ImageIO.read(url);
        }
        // Sample image goes here
        return imageBuffer;
    }
}


