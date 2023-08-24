package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    CompilationDto addNewCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Получен запрос на эндпоинт {}", "POST /admin/compilations");
        return compilationService.addNewCompilation(newCompilationDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCompilationById(@PathVariable Long compId) {
        log.info("Получен запрос на эндпоинт {}", "DELETE /admin/compilations/" + compId);
        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/admin/compilations/{compId}")
    CompilationDto patchCompilationById(@PathVariable Long compId,
                                        @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Получен запрос на эндпоинт {}", "PATCH /admin/compilations/" + compId);
        return compilationService.patchCompilationById(compId, updateCompilationRequest);
    }

}
