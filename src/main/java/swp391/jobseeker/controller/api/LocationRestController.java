package swp391.jobseeker.controller.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import swp391.jobseeker.domain.District;
import swp391.jobseeker.domain.Province;
import swp391.jobseeker.domain.Ward;
import swp391.jobseeker.service.LocationService;

@RestController
public class LocationRestController {

    private final LocationService locationService;

    public LocationRestController(LocationService locationService) {
        this.locationService = locationService;
    }

    // Endpoint lấy danh sách tỉnh
    @GetMapping("/api/provinces")
    public List<Province> getProvinces() {
        return locationService.getProvinces();
    }

    // Endpoint lấy danh sách huyện theo mã tỉnh
    @GetMapping("/api/districts/{provinceCode}")
    public List<District> getDistricts(@PathVariable Long provinceCode) {
        return locationService.getDistricts(provinceCode);
    }

    // Endpoint lấy danh sách xã theo mã huyện
    @GetMapping("/api/wards/{districtCode}")
    public List<Ward> getWards(@PathVariable Long districtCode) {
        return locationService.getWards(districtCode);
    }
}
