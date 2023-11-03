package com.adbazaar.service;

import com.adbazaar.exception.CloudinaryUploadException;
import com.adbazaar.exception.FileUnsupportedTypeException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class CloudinaryService {

    private static final String PROJECT_FOLDER = "AdBazaar";

    private static final String BOOKS_FOLDER = "/books";

    public static final String USER_FOLDER = "/users";

    private final Cloudinary cloudinary;


    public String uploadUserImage(MultipartFile file, Long userId) {
        return uploadImage(file, "avt_" + userId, PROJECT_FOLDER + USER_FOLDER);
    }

    public String uploadBookImage(MultipartFile file, String bookId) {
        return uploadImage(file, bookId, PROJECT_FOLDER + BOOKS_FOLDER);
    }

    public boolean deleteFile(String imageId) {
        try {
            var result = cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap());
            if (result.containsKey("result") && result.get("result").equals("ok")) {
                return true;
            }
        } catch (IOException e) {
            log.error("Failed to delete a file: {}", imageId, e);
        }
        return false;
    }

    private String uploadImage(MultipartFile file, String name, String folder) {
        if (!checkImageType(file)) {
            throw new FileUnsupportedTypeException("Unsupported file type!");
        }
        try {
            return cloudinary.uploader()
                    .upload(file.getBytes(), ObjectUtils.asMap(
                            "public_id", name,
                            "overwrite", true,
                            "folder", folder
                    ))
                    .get("url")
                    .toString();
        } catch (IOException e) {
            throw new CloudinaryUploadException(String.format("Failed to upload image with name: {%s}.", name), e);
        }
    }


    private boolean checkImageType(MultipartFile file) {
        Map<String, List<Byte>> signatures = Map.of(
                "*.jpeg, *.jpg", List.of((byte) 0xFF, (byte) 0xD8),
                "*.png", List.of((byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A));
        try {
            byte[] headerBytes = Arrays.copyOfRange(file.getBytes(), 0, 8);
            if (signatures.values()
                    .stream()
                    .anyMatch(signature -> IntStream.range(0, signature.size())
                            .allMatch(i -> signature.get(i).equals(headerBytes[i])))) {
                return true;
            }
            return true;
        } catch (IOException e) {
            log.error("Failed to read a file {}", file.getName(), e);
        }
        return false;
    }

}
