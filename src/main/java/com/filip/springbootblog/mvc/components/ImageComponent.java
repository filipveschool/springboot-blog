package com.filip.springbootblog.mvc.components;

import com.filip.springbootblog.jpa.dto.ProfileImageDTO;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Component
public class ImageComponent {

    // region User Profile MultiFile Upload Functions

    public void processProfileImage(ProfileImageDTO profileImageDTO, String userKey)
            throws IOException {

        // Reduce original image size. Thumbnailator will not modify
        // image if less than 600x600

        BufferedImage bufferedProfileImage =
                Thumbnails.of(profileImageDTO.getFile().getInputStream())
                        .forceSize(600, 600)
                        .allowOverwrite(true)
                        .outputFormat("png")
                        .asBufferedImage();

        saveProfileImage(bufferedProfileImage, userKey, false);

        // Create profile image icon. Saved to separate directory

        BufferedImage bufferedIconImage =
                Thumbnails.of(profileImageDTO.getFile().getInputStream())
                        .forceSize(32, 32)
                        .allowOverwrite(true)
                        .outputFormat("png")
                        .asBufferedImage();

        saveProfileImage(bufferedIconImage, userKey, true);
    }


    public void processProfileImage(String providerImageUrl, String userKey)
            throws IOException {

        // Reduce original image size. Thumbnailator will not modify
        // image if less than 600x600

        BufferedImage bufferedProfileImage =
                Thumbnails.of(new URL(providerImageUrl))
                        .forceSize(600, 600)
                        .allowOverwrite(true)
                        .outputFormat("png")
                        .asBufferedImage();

        saveProfileImage(bufferedProfileImage, userKey, false);

        // Create profile image icon. Saved to separate directory

        BufferedImage bufferedIconImage =
                Thumbnails.of(new URL(providerImageUrl))
                        .forceSize(32, 32)
                        .allowOverwrite(true)
                        .outputFormat("png")
                        .asBufferedImage();

        saveProfileImage(bufferedIconImage, userKey, true);
    }

    private void saveProfileImage(BufferedImage bufferedImage, String userKey, boolean isIcon) throws IOException {

        String destination = isIcon ? applicationSettings.getProfileIconPath() : applicationSettings.getProfileImagePath();
        File imageDestination = new File(destination + userKey);
        ImageIO.write(bufferedImage, "png", imageDestination);

    }

    // endregion
}
