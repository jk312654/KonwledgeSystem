package com.feishu.controller;


import com.feishu.dto.FolderCreateDTO;
import com.feishu.dto.FolderMoveDTO;
import com.feishu.dto.FolderRenameDTO;
import com.feishu.result.Result;
import com.feishu.service.FolderService;
import com.feishu.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalogue")
public class FolderController {

    @Autowired
    private FolderService folderService;


    @PostMapping()
    public Result<FolderCreateVO> createFolder(@RequestBody FolderCreateDTO request) {

        return Result.success(folderService.createFolder(request.getParentId(),request.getTitle()));

    }

    /**
     * 根据当前目录的id来获取其下的所有目录或文档内容
     * @param id
     * @return
     */
    @GetMapping("/self/{id}")
    public Result<FolderVO> getFolderById(@PathVariable("id") String id) {
        return Result.success(folderService.getFolderWithChildren(id));
    }

    /**
     * 根据父级目录查询所有子目录和子目录中的文档
     */
    @GetMapping("/of/{parentId}")
    public Result<List<FolderVO>> getFolderChildren(@PathVariable String parentId) {
        return Result.success(folderService.getFoldersWithDocs(parentId));
    }

    /**
     *  根据目录的id，修改目录的名字
     * @param id
     * @param renameDTO
     * @return
     */
    @PutMapping("/rename/{id}")
    public Result<FolderRenameVO> renameFolder(
            @PathVariable String id,
            @RequestBody FolderRenameDTO renameDTO) {
        FolderRenameVO vo = folderService.renameFolder(id, renameDTO.getNewTitle());
        return Result.success(vo);
    }

    /**
     * 移动目录
     * @param moveDTO
     * @return
     */
    @PutMapping("/move")
    public Result<FolderMoveVO> moveFolder(@RequestBody FolderMoveDTO moveDTO){
        return Result.success(folderService.moveFolder(moveDTO));
    }

    /**
     * 删除当前目录
     * @param id
     * @return
     */
    @DeleteMapping("/remove/{folderId}")
    public Result deleteFolder(@PathVariable("folderId") String id) {
        folderService.deleteFolder(id);
        return Result.success();
    }

}
