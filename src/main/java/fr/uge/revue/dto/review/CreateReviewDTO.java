package fr.uge.revue.dto.review;

import org.springframework.web.multipart.MultipartFile;


public record CreateReviewDTO(
        String title,
        String commentary,
        String code,
        String test,
        MultipartFile codeFile,
        MultipartFile testFile
) {}
