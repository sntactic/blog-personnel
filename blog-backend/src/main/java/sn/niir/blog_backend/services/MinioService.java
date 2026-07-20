package sn.niir.blog_backend.services;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.public-endpoint}")
    private String publicEndpoint;

    public String uploadImage(MultipartFile file) {
        String key = "articles/" + UUID.randomUUID() + "-" + sanitizeFilename(file.getOriginalFilename());

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Échec de l'upload de l'image : " + e.getMessage(), e);
        }

        return publicEndpoint + "/" + bucket + "/" + key;
    }


    public void deleteImage(String imageUrl) {
        String key = extractKeyFromUrl(imageUrl);

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Échec de la suppression de l'image : " + e.getMessage(), e);
        }
    }

    private String extractKeyFromUrl(String imageUrl) {
        String prefix = publicEndpoint + "/" + bucket + "/";
        if (!imageUrl.startsWith(prefix)) {
            throw new IllegalArgumentException("URL d'image invalide : " + imageUrl);
        }
        return imageUrl.substring(prefix.length());
    }

    public String extractOriginalFilename(String imageUrl) {
        String key = extractKeyFromUrl(imageUrl); // "articles/{uuid}-{filename}"
        int prefixLength = "articles/".length() + 36 + 1; // "articles/" + uuid + "-"
        return key.length() > prefixLength ? key.substring(prefixLength) : key;
    }

    public String sanitizeFilename(String filename) {
        if (filename == null) return "file";
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}