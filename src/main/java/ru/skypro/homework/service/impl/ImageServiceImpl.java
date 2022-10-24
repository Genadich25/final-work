package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.Image;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.repositories.AuthorityRepository;
import ru.skypro.homework.repositories.ImageRepository;
import ru.skypro.homework.service.ImageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class ImageServiceImpl implements ImageService {

    Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final AdsRepository adsRepository;
    private final ImageRepository imageRepository;

    private final AuthorityRepository authorityRepository;

    @Value("${path.to.images.folder}")
    private String imagesDir;

    public ImageServiceImpl(AdsRepository adsRepository, ImageRepository imageRepository, AuthorityRepository authorityRepository) {
        this.adsRepository = adsRepository;
        this.imageRepository = imageRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void uploadImage(MultipartFile image, String email, Integer id) throws IOException {

        logger.info("Request from user {} for uploading image", email);

        Optional<Ads> optionalAds = adsRepository.findById(id);

        if (optionalAds.isPresent()) {
            String date = LocalDate.now().toString();
            String time = LocalTime.now().truncatedTo(ChronoUnit.MINUTES).toString().replace(":", ".");

            Ads ads = optionalAds.get();
            String title = ads.getTitle();
            Path filePath = Path.of(imagesDir + "/" + email + "/" + title, title + " " + date + " " + time + "." + getExtension(Objects.requireNonNull(image.getOriginalFilename())));
            Files.createDirectories(filePath.getParent());
            Files.deleteIfExists(filePath);

            try (InputStream is = image.getInputStream();
                 OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                 BufferedInputStream bis = new BufferedInputStream(is, 1024);
                 BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
                bis.transferTo(bos);
            }

            Image newImage = new Image();

            newImage.setAds(ads);
            newImage.setFilePath(filePath.toString());
            newImage.setFileSize(image.getSize());
            newImage.setMediaType(image.getContentType());
            newImage.setData(image.getBytes());

            Image result = imageRepository.save(newImage);

            ads.setImage("\\ads\\images\\" + result.getId().toString());
            adsRepository.save(ads);
        }
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    public Image getImageById(Integer id) {
        logger.info("Request for getting image with id: {}", id);
        Optional<Image> optionalImage = imageRepository.findById(id);
        if (optionalImage.isEmpty()) {
            return null;
        } else {
            return optionalImage.get();
        }
    }


    @Override
    public Image updateImage(Integer id, MultipartFile image) throws IOException {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String auth = authorityRepository.findAuthorityByUsername(email).getAuthority();

        logger.info("Request from user {} for updating image with id: {}", email, id);

        Optional<Image> optionalImage = imageRepository.findById(id);
        if (optionalImage.isPresent()) {

            Image image1 = optionalImage.get();

            if (!image1.getAds().getSiteUserDetails().getSiteUser().getUsername().equals(email) && auth.equals("ROLE_USER")) {
                return null;
            } else {
                String filePath = image1.getFilePath();

                Path path = Path.of(filePath);

                Files.deleteIfExists(path);

                try (InputStream is = image.getInputStream();
                     OutputStream os = Files.newOutputStream(path, CREATE_NEW);
                     BufferedInputStream bis = new BufferedInputStream(is, 1024);
                     BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
                    bis.transferTo(bos);
                }

                image1.setFileSize(image.getSize());
                image1.setMediaType(image.getContentType());
                image1.setData(image.getBytes());

                return imageRepository.save(image1);
            }
        } else {
            return null;
        }
    }


}