package com.feishu.service;

import com.feishu.dto.FolderMoveDTO;
import com.feishu.vo.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface FolderService {
    FolderCreateVO createFolder(String parentId, String title);
    FolderVO getFolderWithChildren(String id);
    List<FolderVO> getFoldersWithDocs(String parentId);

    FolderRenameVO renameFolder(String folderId, String newTitle);

    FolderMoveVO moveFolder(FolderMoveDTO moveDTO);

    void deleteFolder(String id);
}
