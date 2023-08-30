package ru.practicum.ewm.compilation.service;

import org.springframework.lang.Nullable;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto addNewCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto patchCompilationById(Long compId, UpdateCompilationRequest updateCompilationRequest);

    List<CompilationDto> getAllCompilations(@Nullable Boolean pinned, int from, int size);

    CompilationDto getCompilation(Long compId);
}
