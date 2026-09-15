package com.procurepilot.startup;

import com.procurepilot.auth.UserPrincipal;
import com.procurepilot.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/startups")
public class StartupController {

    private final StartupService startupService;

    public StartupController(StartupService startupService) {
        this.startupService = startupService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StartupDtos.ProfileDto>> getMyProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        StartupDtos.ProfileDto profile = startupService.getProfileByUserId(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<StartupDtos.ProfileDto>> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody StartupDtos.ProfileDto profileDto) {
        StartupDtos.ProfileDto updated = startupService.updateProfile(currentUser.getId(), profileDto);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @GetMapping("/me/documents")
    public ResponseEntity<ApiResponse<List<StartupDtos.DocumentDto>>> getMyDocuments(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<StartupDtos.DocumentDto> docs = startupService.listDocuments(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(docs));
    }

    @PostMapping("/me/documents")
    public ResponseEntity<ApiResponse<StartupDtos.DocumentDto>> uploadDocument(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam("docType") String docType,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File cannot be empty"));
        }

        try {
            String uploadsDir = "./uploads";
            File dir = new File(uploadsDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf";
            String safeFileName = UUID.randomUUID().toString() + "_" + originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path targetPath = Paths.get(uploadsDir, safeFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            StartupDtos.DocumentDto docDto = startupService.addDocument(
                    currentUser.getId(),
                    docType,
                    originalName,
                    "/uploads/" + safeFileName,
                    file.getSize(),
                    file.getContentType()
            );

            return ResponseEntity.ok(ApiResponse.ok("Document uploaded successfully", docDto));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to store file: " + e.getMessage()));
        }
    }
}
