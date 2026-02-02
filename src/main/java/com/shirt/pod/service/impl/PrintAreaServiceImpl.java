package com.shirt.pod.service.impl;

import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.exception.ValidationException;
import com.shirt.pod.mapper.PrintAreaMapper;
import com.shirt.pod.model.dto.request.PrintAreaCreateRequest;
import com.shirt.pod.model.dto.request.PrintAreaFilterRequest;
import com.shirt.pod.model.dto.request.PrintAreaUpdateRequest;
import com.shirt.pod.model.dto.response.PrintAreaDTO;
import com.shirt.pod.model.entity.BaseProduct;
import com.shirt.pod.model.entity.PrintArea;
import com.shirt.pod.repository.BaseProductRepository;
import com.shirt.pod.repository.PrintAreaRepository;
import com.shirt.pod.service.PrintAreaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrintAreaServiceImpl implements PrintAreaService {

        private final PrintAreaRepository printAreaRepository;
        private final BaseProductRepository baseProductRepository;
        private final PrintAreaMapper printAreaMapper;

        @Override
        @Transactional(readOnly = true)
        public Page<PrintAreaDTO> getAll(PrintAreaFilterRequest filterRequest) {
                log.info("Fetching print areas with filters - baseProductId: {}, name: {}",
                                filterRequest.getBaseProductId(), filterRequest.getName());

                // Convert enum to string for native query
                String nameFilter = (filterRequest.getName() != null) ? filterRequest.getName().name() : null;

                // Build pageable
                Sort.Direction direction = Sort.Direction.fromString(filterRequest.getOrder().toUpperCase());
                Pageable pageable = PageRequest.of(
                                filterRequest.getPage() > 0 ? filterRequest.getPage() - 1 : 0,
                                filterRequest.getPageSize(),
                                Sort.by(direction, filterRequest.getSortBy()));

                // Use native query with filters
                Page<PrintArea> printAreaPage = printAreaRepository.searchWithFilters(
                                filterRequest.getBaseProductId(), nameFilter, pageable);

                log.info("Found {} print areas (page {}/{}, size {})",
                                printAreaPage.getTotalElements(),
                                printAreaPage.getNumber() + 1,
                                printAreaPage.getTotalPages(),
                                printAreaPage.getSize());

                return printAreaPage.map(printAreaMapper::toDTO);
        }

        @Override
        @Transactional(readOnly = true)
        public PrintAreaDTO getById(Long id) {
                log.info("Fetching print area with id: {}", id);
                PrintArea printArea = printAreaRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Print area not found with id: " + id));
                return printAreaMapper.toDTO(printArea);
        }

        @Override
        @Transactional
        public PrintAreaDTO create(PrintAreaCreateRequest request) {
                log.info("Creating new print area with name: {} for base product: {}",
                                request.getName(), request.getBaseProductId());

                // Validate base product exists
                BaseProduct baseProduct = baseProductRepository.findById(request.getBaseProductId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Base product not found with id: " + request.getBaseProductId()));

                // Validate unique print area name for this base product
                if (printAreaRepository.existsByBaseProductIdAndName(request.getBaseProductId(), request.getName())) {
                        throw new ValidationException("Print area with name '" + request.getName().getDisplayName() +
                                        "' already exists for this base product");
                }

                PrintArea printArea = printAreaMapper.toEntity(request);
                printArea.setBaseProduct(baseProduct);
                PrintArea savedPrintArea = printAreaRepository.save(printArea);

                log.info("Created print area with id: {}", savedPrintArea.getId());
                return printAreaMapper.toDTO(savedPrintArea);
        }

        @Override
        @Transactional
        public PrintAreaDTO update(Long id, PrintAreaUpdateRequest request) {
                log.info("Updating print area with id: {}", id);

                PrintArea printArea = printAreaRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Print area not found with id: " + id));

                // Validate unique print area name if name is being updated
                if (request.getName() != null && !request.getName().equals(printArea.getName())) {
                        if (printAreaRepository.existsByBaseProductIdAndNameAndIdNot(
                                        printArea.getBaseProduct().getId(), request.getName(), id)) {
                                throw new ValidationException(
                                                "Print area with name '" + request.getName().getDisplayName() +
                                                                "' already exists for this base product");
                        }
                }

                // Update only non-null fields
                printAreaMapper.updateEntity(request, printArea);
                PrintArea updatedPrintArea = printAreaRepository.save(printArea);

                log.info("Updated print area with id: {}", id);
                return printAreaMapper.toDTO(updatedPrintArea);
        }

        @Override
        @Transactional
        public void delete(Long id) {
                log.info("Deleting print area with id: {}", id);

                PrintArea printArea = printAreaRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Print area not found with id: " + id));

                // Hard delete for print areas
                printAreaRepository.delete(printArea);

                log.info("Deleted print area with id: {}", id);
        }
}
