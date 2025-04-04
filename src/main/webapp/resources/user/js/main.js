// Skill tag click handling
function addSkillSearch() {
  $(".skill-tag").click(function () {
    let skillId = $(this).data("skill-id");
    window.location.href = "/jobs?" + "requiredSkills=" + skillId;
  });
}

(function ($) {
  "use strict";
  // Spinner
  let spinner = function () {
    setTimeout(function () {
      if ($("#spinner").length > 0) {
        $("#spinner").removeClass("show");
      }
    }, 1);
  };
  spinner();

  $("#imageFile1").change(function (e) {
    const imgURL = URL.createObjectURL(e.target.files[0]);
    $("#imagePreview1").attr("src", imgURL);
  });

  $("#imageFile2").change(function (e) {
    const imgURL = URL.createObjectURL(e.target.files[0]);
    $("#imagePreview2").attr("src", imgURL);
  });
  // Sticky Navbar
  $(window).scroll(function () {
    if ($(this).scrollTop() > 300) {
      $(".sticky-top").css("top", "0px");
    } else {
      $(".sticky-top").css("top", "-100px");
    }
  });

  // Header carousel
  if ($(".header-carousel").length > 0) {
    $(".header-carousel").owlCarousel({
      autoplay: true,
      smartSpeed: 1500,
      items: 1,
      dots: true,
      loop: true,
      nav: true,
      navText: [
        '<i class="bi bi-chevron-left"></i>',
        '<i class="bi bi-chevron-right"></i>',
      ],
    });
  }

  // For skill tags
  $(document).ready(function () {
    addSkillSearch();
    if (document.getElementById("skills")) {
      // Initialize select2 for skills
      $("#skills").select2({
        placeholder: "Chọn kỹ năng",
        allowClear: true,
        width: "100%",
      });
    }

    if (document.getElementById("levels")) {
      $("#levels").select2({
        placeholder: "Cấp bậc",
        allowClear: true,
        width: "100%",
      });
    }

    if (document.getElementById("working-model")) {
      $("#working-model").select2({
        placeholder: "Mô hình làm việc",
        allowClear: true,
        width: "100%",
      });
    }
  });

  // add active navbar
  // Get the current URL path
  let currentPath = window.location.pathname;

  // Iterate through each nav-link
  $(".nav-item.nav-link").each(function () {
    let href = $(this).attr("href");
    // Check if the current URL path contains the href
    if (currentPath === "/") {
      console.log("currentPath", currentPath);
      $(".nav-item.nav-link[href='/']").addClass("active");
      return;
    }
    if (currentPath.includes(href) && href !== "/") {
      $(this).addClass("active");
    }
  });

  toastr.options = {
    closeButton: true,
    debug: false,
    newestOnTop: false,
    progressBar: true,
    positionClass: "toast-top-right",
    preventDuplicates: false,
    onclick: null,
    showDuration: "300",
    hideDuration: "1000",
    timeOut: "5000",
    extendedTimeOut: "1000",
    showEasing: "swing",
    hideEasing: "linear",
    showMethod: "fadeIn",
    hideMethod: "fadeOut",
  };

  let url = window.location.href;
  let queryString = url.split("?")[1];
  switch (queryString) {
    case "old-pwd-error":
      toastr.error("Mật khẩu cũ không đúng", "Lỗi");
      break;
    case "pwd-not-match":
      toastr.error("Mật khẩu mới không trùng khớp", "Lỗi");
      break;
    case "pwd-change-success":
      toastr.success("Đổi mật khẩu thành công", "Thành công");
      break;
    case "edit-success":
      toastr.success("Chỉnh sửa thông tin thành công", "Thành công");
      break;
    case "update-avatar-success":
      toastr.success("Cập nhật ảnh đại diện thành công", "Thành công");
      break;
    case "update-avatar-fail":
      toastr.error("Cập nhật ảnh đại diện thất bại", "Lỗi");
      break;
    case "feedback-support-success":
      toastr.success("Gửi yêu cầu hỗ trợ thành công", "Thành công");
      break;
    case "comment-support-success":
      toastr.success("Gửi góp ý thành công", "Thành công");
      break;
    case "delete-review-success":
      toastr.success("Xóa đánh giá thành công", "Thành công");
      break;
    case "write-review-success":
      toastr.success("Đánh giá thành công", "Thành công");
      break;
    case "pwd-length-error":
      toastr.error("Mật khẩu phải ít nhất 6 ký tự", "Lỗi");
      break;
    case "report-review-success":
      toastr.success("Báo cáo đánh giá thành công", "Thành công");
      break;
    case "edit-review-success":
      toastr.success("Chỉnh sửa đánh giá thành công", "Thành công");
      break;
    case "up-cv-success":
      toastr.success("Tải lên CV thành công", "Thành công");
      break;
    case "cv-file-error":
      toastr.error("File CV bị lỗi", "Lỗi");
      break;
    case "phone-number-invalid":
      toastr.error("Lỗi số điện thoại", "Lỗi");
      break;
    case "job-closed":
      toastr.warning(
        "Việc làm này đang không tuyển dụng! Vui lòng thử lại sau",
        "Thông báo"
      );
      break;
  }
})(jQuery);
