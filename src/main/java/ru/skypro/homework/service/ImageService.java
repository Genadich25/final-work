package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entities.Image;

import java.io.IOException;

public interface ImageService {

    void uploadImage(MultipartFile image, String email, Integer id) throws IOException;

    Image getImageById(Integer id);

}
