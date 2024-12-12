package com.rush.logistic.client.company_product.domain.company.service;

import com.rush.logistic.client.company_product.domain.company.dto.CompanyDto;
import com.rush.logistic.client.company_product.domain.company.dto.request.CompanyCreateRequest;
import com.rush.logistic.client.company_product.domain.company.dto.request.CompanyUpdateRequest;
import com.rush.logistic.client.company_product.domain.company.dto.response.CompanySearchResponse;
import com.rush.logistic.client.company_product.domain.company.entity.Company;
import com.rush.logistic.client.company_product.domain.company.repository.CompanyRepository;
import com.rush.logistic.client.company_product.global.exception.ApplicationException;
import com.rush.logistic.client.company_product.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    private final EntityManager entityManager;

    //업체 추가
    @Transactional
    public CompanyDto createCompany(CompanyCreateRequest request) {
        // TODO: MASTER, HUB-MANAGER 확인 로직 추가
        CompanyDto dto = CompanyCreateRequest.toDto(request);

        Optional<Company> company = companyRepository.findByName(dto.name());

        if(company.isEmpty()){
            Company companyEntity = companyRepository.save(dto.toEntity(dto));
            System.out.println("Saved Company ID: " + companyEntity.getId());
            return CompanyDto.from(companyEntity);
        }else{
            throw new ApplicationException(ErrorCode.DUPLICATED_COMPANYNAME);
        }
    }

    //업체 전체 조회
    @Transactional
    public Page<CompanyDto> getAllCompany(Pageable pageable) {
        // 페이지 사이즈 제한
        int[] allowedPageSizes = {10, 30, 50};
        int pageSize = pageable.getPageSize();

        // 허용되지 않은 페이지 사이즈일 경우 기본값 10으로 설정
        if (!Arrays.stream(allowedPageSizes).anyMatch(size -> size == pageSize)) {
            pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        }

        Page<Company> companies = companyRepository.findAll(pageable);
        return companies.map(CompanyDto::from);
    }

    //업체 단건 조회
    @Transactional
    public CompanySearchResponse getCompany(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMPANY_NOT_FOUND));
        return CompanySearchResponse.from(company);
    }

    //업체 수정
    @Transactional
    public CompanyDto updateCompany(UUID id, CompanyUpdateRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMPANY_NOT_FOUND));

        company.setHubId(request.hubId());
        company.setName(request.name());
        company.setAddress(request.address());
        company.setType(request.type());

        entityManager.flush();

        entityManager.clear();

        Company companyForReturn = companyRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COMPANY_NOT_FOUND));

        return CompanyDto.from(companyForReturn);
    }

    //업체 삭제
    @Transactional
    public void deleteCompany(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_REQUEST));
        if (company.getIsDelete()){
            throw new ApplicationException(ErrorCode.INVALID_REQUEST);
        }
        company.setIsDelete(true);
        company.setDeletedAt(LocalDateTime.now());
    }
}