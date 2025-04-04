package swp391.jobseeker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import swp391.jobseeker.domain.CompanyImages;
import swp391.jobseeker.repository.CompanyImagesRepository;

@Service
public class CompanyImagesService {

    private CompanyImagesRepository companyImagesRepository;

    public CompanyImagesService(CompanyImagesRepository companyImagesRepository) {
        this.companyImagesRepository = companyImagesRepository;
    }

    public List<CompanyImages> getAllImagesByCompanyId() {
        return this.companyImagesRepository.findAll();
    }

    public void save(CompanyImages image) {
        companyImagesRepository.save(image);
    }

    public CompanyImages findById(Long id) {
        return companyImagesRepository.findById(id).orElse(null);
    }

    public void delete(CompanyImages image) {
        companyImagesRepository.delete(image);
    }

    public Page<CompanyImages> getAllImagesByCompanyIdPageable(long companyId, Pageable pageable) {
        return companyImagesRepository.findByCompanyId(companyId, pageable);
    }

}
