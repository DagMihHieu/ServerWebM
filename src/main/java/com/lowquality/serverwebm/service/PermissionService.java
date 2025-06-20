package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.repository.UserRepository;
import com.lowquality.serverwebm.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import com.lowquality.serverwebm.models.entity.User;
import org.springframework.security.access.AccessDeniedException;

import java.util.Objects;

@Service
public class PermissionService {
    @Autowired
    private UserRepository userRepository;
    public void checkCommentPermission( int ownerId, String action) {
        User currentUser = getCurrentUser();
        boolean isOwner = currentUser.getId().equals(ownerId);
        if (!isOwner && !isAdminOrMod(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền " + action + " comment này");
        }
    }
    public User getCurrentUser() {

        return userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
    }
    public boolean isAdminOrMod(User user) {
        return "ADMIN".equals(getRoleName(user)) || "MOD".equals(getRoleName(user));
    }
    public void checkUserPermission( int ownerId, String action ) {
        User currentUser = getCurrentUser();
        boolean isOwner = currentUser.getId().equals(ownerId);
        if (!isOwner && !isAdminOrMod(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền " + action );
        }
    }
    public void onlyModAndAdmin(String action) {
        User currentUser = getCurrentUser();
        if (!isAdminOrMod(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền " + action );
        }
    }
    public void noPermission(String action) {
            throw new AccessDeniedException("Bạn không có quyền " + action );
    }
    public void checkMangaPermission( String action) {
        User currentUser = getCurrentUser();
        if ( !isUploader(currentUser) && !isAdminOrMod(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền"+action );
        }
    }
    public boolean isAdmin(User currentUser) {

        return "ADMIN".equals(getRoleName(currentUser));
    }
    public boolean isUploader(User currentUser) {

        return "UPLOADER".equals(getRoleName(currentUser));
    }
    public void onlyAdmin(String action) {
        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền " + action);
        }
    }

    public void checkChangeRolePermission(User targetUser, String targetRoleName) {
        User currentUser = getCurrentUser();
        if(Objects.equals(targetUser.getId(), currentUser.getId())) {
            noPermission("Không thể chỉnh role bản thân");
        }
        if (isAdmin(currentUser)) {
            return; // Admin có thể làm mọi thứ
        }

        if (isMod(currentUser)) {
            // Mod chỉ có thể thay đổi role thành UPLOADER hoặc USER
            if (!"UPLOADER".equals(targetRoleName) && !"USER".equals(targetRoleName)) {
                throw new AccessDeniedException("Bạn chỉ có thể thay đổi role thành UPLOADER hoặc USER");
            }
            // Mod không thể thay đổi role của admin/mod khác
            if (isAdmin(targetUser) || isMod(targetUser)) {
                throw new AccessDeniedException("Bạn không thể thay đổi role của admin/mod khác");
            }
            return;
        }

        throw new AccessDeniedException("Bạn không có quyền thay đổi role");
    }

    boolean isMod(User currentUser) {
        return "MOD".equals(getRoleName(currentUser));
    }
    private String getRoleName(User user) {
        if (user == null) {
            System.out.println("user is null");
            throw new AccessDeniedException("Thông tin người dùng không hợp lệ");
        }
        if (user.getRole() == null) {
            System.out.println("user.role is null: id = " + user.getId() + ", email = " + user.getEmail());
            throw new AccessDeniedException("Thông tin vai trò người dùng không hợp lệ");
        }
        if (user.getRole().getName() == null) {
            System.out.println("user.role.name is null: id = " + user.getId());
            throw new AccessDeniedException("Vai trò người dùng không hợp lệ");
        }

        return user.getRole().getName();
    }

}
