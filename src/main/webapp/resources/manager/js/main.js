(function ($) {
  "use strict";
  // Spinner
  var spinner = function () {
    setTimeout(function () {
      if ($("#spinner").length > 0) {
        $("#spinner").removeClass("show");
      }
    }, 1);
  };
  spinner();

  // Back to top button
  $(window).scroll(function () {
    if ($(this).scrollTop() > 300) {
      $(".back-to-top").fadeIn("slow");
    } else {
      $(".back-to-top").fadeOut("slow");
    }
  });
  $(".back-to-top").click(function () {
    $("html, body").animate({ scrollTop: 0 }, 1500, "easeInOutExpo");
    return false;
  });

  // Sidebar Toggler
  $(".sidebar-toggler").click(function () {
    $(".sidebar, .content").toggleClass("open");
    return false;
  });

  // Logo preview
  $("#imageFile").change(function (e) {
    const imgURL = URL.createObjectURL(e.target.files[0]);
    $("#imagePreview").attr("src", imgURL);
  });
  $("#imageFile1").change(function (e) {
    const imgURL = URL.createObjectURL(e.target.files[0]);
    $("#imagePreview1").attr("src", imgURL);
  });

  $("#imageFile2").change(function (e) {
    const imgURL = URL.createObjectURL(e.target.files[0]);
    $("#imagePreview2").attr("src", imgURL);
  });

  function onSubmit(token) {
    document.getElementById("demo-form").submit();
  }

  // add active navbar
  // Get the current URL path
  var currentPath = window.location.pathname;

  // Iterate through each nav-link
  $(".nav-item.nav-link").each(function () {
    var href = $(this).attr("href");

    // Check if the current URL path contains the href
    if (currentPath.includes(href)) {
      $(this).addClass("active");
    }
  });

  if (document.getElementById("skills")) {
    // For select 2 library
    $("#skills").select2({
      placeholder: "Chọn kỹ năng",
      allowClear: true,
      width: "100%", // Ensures the Select2 dropdown fits within Bootstrap's grid
    });
  }
  if (document.getElementById("levels")) {
    // For select 2 library
    $("#levels").select2({
      placeholder: "Chọn các vị trí cần tuyển dụng",
      allowClear: true,
      width: "100%", // Ensures the Select2 dropdown fits within Bootstrap's grid
    });
  }

  // Modal for delete button
  $(document).ready(function () {
    let deleteLink = null;

    // Handle the click event of the delete link
    $(".delete-link").on("click", function (event) {
      event.preventDefault(); // Prevent default action (navigation)

      deleteLink = $(this).attr("href"); // Store the href of the clicked link

      // Show the modal
      $("#confirmDeleteModal").modal("show");
    });

    // Handle the confirmation button click in the modal
    $("#confirmDeleteBtn").on("click", function () {
      if (deleteLink) {
        window.location.href = deleteLink; // Redirect to the stored link
      }
    });
  });

  // For TinyMCE editor
  if (document.getElementById("description")) {
    tinymce.init({
      selector: "textarea#description",
      height: 500,
      plugins: [
        "advlist",
        "autolink",
        "lists",
        "link",
        "image",
        "charmap",
        "preview",
        "anchor",
        "searchreplace",
        "visualblocks",
        "code",
        "fullscreen",
        "insertdatetime",
        "media",
        "table",
        "help",
        "wordcount",
      ],
      toolbar:
        "undo redo | blocks | " +
        "bold italic backcolor | alignleft aligncenter " +
        "alignright alignjustify | bullist numlist outdent indent | " +
        "removeformat | help",
      content_style:
        "body { font-family:Helvetica,Arial,sans-serif; font-size:16px }",
    });
  }

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

  // Display an info toast with no title
  let url = window.location.href;
  let queryString = url.split("?")[1];
  switch (queryString) {
    case "register-success":
      toastr.success("Đăng ký tài khoản thành công", "Thành công");
      break;
    case "login-error":
      toastr.error("Đăng nhập với OAuth2 không thành công", "Lỗi");
      break;
    case "invalid":
      toastr.error(
        "Sai tên đăng nhập hoặc mật khẩu, đăng nhập không thành công",
        "Lỗi"
      );
      break;
    case "oauth2-error":
    case "oauth2-error#_=_":
      toastr.error("Đăng nhập OAuth2 không thành công", "Lỗi");
      break;
    case "disabled":
      toastr.error("Tài khoản đang tạm khóa, vui lòng liên hệ Admin", "Lỗi");
      break;
    case "logout":
      toastr.success("Đăng xuất thành công", "Thành công");
      break;
    case "old-pwd-error":
      toastr.error("Mật khẩu cũ không đúng", "Lỗi");
      break;
    case "pwd-not-match":
      toastr.error("Mật khẩu mới không trùng khớp", "Lỗi");
      break;
    case "pwd-change-success":
      toastr.success("Đổi mật khẩu thành công", "Thành công");
      break;
    case "update-success":
      toastr.success("Cập nhật thông tin thành công", "Thành công");
      break;
    case "update-avatar-success":
      toastr.success("Cập nhật ảnh đại diện thành công", "Thành công");
      break;
    case "deactivate-success":
      toastr.success("Tạm khóa tài khoản thành công", "Thành công");
      break;
    case "activate-success":
      toastr.success("Kích hoạt tài khoản thành công", "Thành công");
      break;
    case "add-success":
      toastr.success("Thêm mới thành công", "Thành công");
      break;
    case "delete-user-success":
      toastr.success("Xoá tài khoản thành công", "Thành công");
      break;
    case "invalid-user":
      toastr.error("Tài khoản hoặc Gmail không tồn tại", "Lỗi");
      break;
    case "skillexist":
      toastr.error("Kĩ năng đã tồn tại", "Lỗi");
      break;
    case "skill-add-success":
      toastr.success("Thêm kĩ năng thành công", "Thành công");
      break;
    case "skill-edit-success":
      toastr.success("Chỉnh sửa kĩ năng thành công", "Thành công");
      break;
    case "skill-delete-success":
      toastr.success("Xoá kĩ năng thành công", "Thành công");
      break;
    case "verification-success":
      toastr.success("Xác thực tài khoản thành công", "Thành công");
      break;
    case "company-deleted":
      toastr.success("Xóa công ty thành công", "Thành công");
      break;
    case "update-company-success":
      toastr.success("Sửa thông tin công ty thành công", "Thành công");
      break;
    case "update-job-success":
      toastr.success("Cập nhật tin tuyển dụng thành công", "Thành công");
      break;
    case "add-job-success":
      toastr.success("Đăng tin tuyển dụng thành công", "Thành công");
      break;
    case "add-branch-success":
      toastr.success("Thêm chi nhánh thành công", "Thành công");
      break;
    case "update-branch-success":
      toastr.success("Sửa thông tin chi nhánh thành công", "Thành công");
      break;
    case "delete-branch-success":
      toastr.success("Xóa chi nhánh thành công", "Thành công");
      break;
    case "change-role-success":
      toastr.success("Thay đổi vai trò thành công", "Thành công");
      break;
    case "feedback-support-success":
      toastr.success("Gửi yêu cầu hỗ trợ thành công", "Thành công");
      break;
    case "feedback-support-fail":
      toastr.success("Gửi yêu cầu hỗ trợ thất bại", "Lỗi");
      break;
    case "comment-support-success":
      toastr.success("Gửi góp ý thành công", "Thành công");
      break;
    case "delete-comment-support-success":
      toastr.success("Xóa yêu cầu hỗ trợ thành công", "Thành công");
      break;
    case "mark-read-success":
      toastr.success("Đã đọc phản hồi!", "Thành công");
      break;
    case "send-email-admin":
      toastr.success("Gửi Email tới người dùng thành công!", "Thành công");
      break;
    case "pwd-length-error":
      toastr.error("Mật khẩu phải ít nhất 6 ký tự", "Lỗi");
      break;
    case "delete-review-success":
      toastr.success("Xóa đánh giá thành công", "Thành công");
      break;
    case "add-company-success":
      toastr.success("Thêm công ty thành công", "Thành công");
      break;
    case "add-image-success":
      toastr.success("Thêm hình ảnh thành công", "Thành công");
      break;
    case "update-image-success":
      toastr.success("Sửa hình ảnh thành công", "Thành công");
      break;
    case "delete-job-failed":
      toastr.error("Công việc này đang có người ứng tuyển", "Lỗi");
      break;
    case "delete-job-success":
      toastr.success("Xóa tin tuyển dụng thành công", "Thành công");
      break;
    case "delete-branch-failed":
      toastr.error("Chi nhánh này đã có công việc", "Lỗi");
      break;
    case "skill-delete-failed":
      toastr.error("Không thể xoá do kĩ năng đang được sử dụng", "Lỗi");
      break;
  }
})(jQuery);
