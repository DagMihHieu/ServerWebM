package com.lowquality.serverwebm.service;

import org.springframework.stereotype.Service;
import com.lowquality.serverwebm.models.entity.User;
import org.springframework.security.access.AccessDeniedException;

@Service
public class PermissionService {
    public void checkCommentPermission(User currentUser, int ownerId, String action) {
        boolean isOwner = currentUser.getId().equals(ownerId);


        if (!isOwner && !isAdminOrMod(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền " + action + " comment này");
        }
    }

    public boolean isAdminOrMod(User user) {
        String role = user.getRole().getName();
        return role.equals("ADMIN") || role.equals("MOD");
    }
    public void checkUserPermission(User currentUser, int ownerId, String action ) {
        boolean isOwner = currentUser.getId().equals(ownerId);
        if (!isOwner && !isAdminOrMod(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền " + action );
        }
    }
    public void onlyModAndAdmin(User currentUser, String action) {
        if (!isAdminOrMod(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền " + action );
        }
    }
    public void checkAddMangaPermission(User currentUser ) {
        if ( !isUploader(currentUser) && !isAdminOrMod(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền thêm truyện." );
        }
    }
    public boolean isAdmin(User currentUser) {
        String role = currentUser.getRole().getName();
        return role.equals("ADMIN");
    }
    public boolean isUploader(User currentUser) {
        String role = currentUser.getRole().getName();
        return role.equals("UPLOADER");
    }
}
