package swp391.jobseeker.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import swp391.jobseeker.domain.District;
import swp391.jobseeker.domain.Province;
import swp391.jobseeker.domain.Ward;
import swp391.jobseeker.repository.DistrictRepository;
import swp391.jobseeker.repository.ProvinceRepository;
import swp391.jobseeker.repository.WardRepository;

@Service
public class LocationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    public LocationService(ProvinceRepository provinceRepository, DistrictRepository districtRepository,
            WardRepository wardRepository) {
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
    }

    // Lấy danh sách tỉnh (depth=1)
    public List<Province> getProvinces() {
        return provinceRepository.findAll();
    }

    // Lấy danh sách huyện theo mã tỉnh (depth=2 để có danh sách huyện)
    public List<District> getDistricts(Long provinceCode) {
        return districtRepository.findByProvinceCode(provinceCode);
    }

    // Lấy danh sách xã theo mã huyện (depth=2 để có danh sách xã)
    public List<Ward> getWards(Long districtCode) {
        return wardRepository.findByDistrict_Code(districtCode);
    }

    public void fetchAndSaveProvinces() {

        if(provinceRepository.existsAny()){
            System.out.println("No need to fetch data");
            return;
        }

        String apiUrl = "https://provinces.open-api.vn/api/?depth=3";
        List<Map<String, Object>> response = restTemplate.getForObject(apiUrl, List.class);

        if (response == null || response.isEmpty()) {
            System.out.println("⚠ Không nhận được dữ liệu từ API!");
            return;
        }

        for (Map<String, Object> provinceData : response) {
            Long provinceCode = ((Number) provinceData.get("code")).longValue();
            String provinceName = (String) provinceData.get("name");

            // Kiểm tra xem Province đã tồn tại trong DB chưa
            Province province = provinceRepository.findByCode(provinceCode)
                    .orElse(new Province());
            province.setCode(provinceCode);
            province.setName(provinceName);
            province = provinceRepository.save(province);

            List<Map<String, Object>> districts = (List<Map<String, Object>>) provinceData.get("districts");
            if (districts != null) {
                for (Map<String, Object> districtData : districts) {
                    Long districtCode = ((Number) districtData.get("code")).longValue();
                    String districtName = (String) districtData.get("name");

                    // Kiểm tra xem District đã tồn tại chưa
                    District district = districtRepository.findByCode(districtCode)
                            .orElse(new District());
                    district.setCode(districtCode);
                    district.setName(districtName);
                    district.setProvince(province);
                    district = districtRepository.save(district);

                    List<Map<String, Object>> wards = (List<Map<String, Object>>) districtData.get("wards");
                    if (wards != null) {
                        for (Map<String, Object> wardData : wards) {
                            Long wardCode = ((Number) wardData.get("code")).longValue();
                            String wardName = (String) wardData.get("name");

                            // Kiểm tra xem Ward đã tồn tại chưa
                            Ward ward = wardRepository.findByCode(wardCode)
                                    .orElse(new Ward());
                            ward.setCode(wardCode);
                            ward.setName(wardName);
                            ward.setDistrict(district);
                            wardRepository.save(ward);
                        }
                    }
                }
            }
        }
        System.out.println("✅ Dữ liệu tỉnh/huyện/xã đã được cập nhật thành công!");
        System.out.println("✅ Data fetching completed!");
    }
}
