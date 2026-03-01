package udpm.hn.server.core.manage.stage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.manage.stage.model.request.StageRequest;
import udpm.hn.server.core.manage.stage.model.request.StageVoteRequest;
import udpm.hn.server.core.manage.stage.service.MAStageVoteService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MappingConstants.API_MANAGE_STAGE)
public class MAStageRestController {

    private final MAStageVoteService maStageVoteService;

    @GetMapping
    public ResponseEntity<?> stageStart(StageVoteRequest request){
        log.info("ID PRoject",request.getIdProject() );
        return Helper.createResponseEntity(maStageVoteService.getAllStage(request));
    }

    @GetMapping("/detail/{idStage}")
    public ResponseEntity<?> detailStage(@PathVariable String idStage ){
        return Helper.createResponseEntity(maStageVoteService.detailStageVote(idStage));
    }

    @DeleteMapping("/{idStageVote}")
    public ResponseEntity<?> deleteStage(@PathVariable String idStageVote){
        return Helper.createResponseEntity(maStageVoteService.deleteStage(idStageVote ));
    }

    @GetMapping("/{idStageVote}")
    public ResponseEntity<?> stageStart(@PathVariable String idStageVote){
        return Helper.createResponseEntity(maStageVoteService.findALlTodoVoteStage(idStageVote ));
    }

    @GetMapping("/take-place/{idProject}")
    public ResponseEntity<?> stageTakePlace(@PathVariable String idProject){
        return Helper.createResponseEntity(maStageVoteService.stageTakePlace(idProject));
    }

    @PostMapping
    public ResponseEntity<?> createUpdate(@RequestBody StageRequest request){
        log.info("lấy dữ liệu stage:{}",request);
        return Helper.createResponseEntity(maStageVoteService.createUpdate(request));
    }

}
