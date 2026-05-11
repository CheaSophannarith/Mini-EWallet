package com.fii.ewallet.profile.service;

import com.fii.ewallet.profile.dto.ProfileImageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    ProfileImageResponse uploadProfileImage(MultipartFile file, String email);

}
