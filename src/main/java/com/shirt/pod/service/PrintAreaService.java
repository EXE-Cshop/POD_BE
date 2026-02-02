package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.PrintAreaCreateRequest;
import com.shirt.pod.model.dto.request.PrintAreaFilterRequest;
import com.shirt.pod.model.dto.request.PrintAreaUpdateRequest;
import com.shirt.pod.model.dto.response.PrintAreaDTO;
import org.springframework.data.domain.Page;

public interface PrintAreaService {

    Page<PrintAreaDTO> getAll(PrintAreaFilterRequest filterRequest);

    PrintAreaDTO getById(Long id);

    PrintAreaDTO create(PrintAreaCreateRequest request);

    PrintAreaDTO update(Long id, PrintAreaUpdateRequest request);

    void delete(Long id);
}
