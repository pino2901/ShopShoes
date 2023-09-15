
    window.onload = function() {
    var titleInput = document.getElementById('title');
    var titleError = document.getElementById('title-error');

    function validateTitle() {
    if (titleInput.validity.valid) {
    // Nếu thông tin nhập vào hợp lệ, ẩn thông báo lỗi
    titleError.style.display = 'none';
} else if (titleInput.value.length > 10) {
    // Nếu số kí tự vượt quá giới hạn, hiển thị thông báo lỗi
    titleError.textContent = 'Please enter a valid title (alphanumeric characters and spaces only, up to 10 characters)';
    titleError.style.display = 'block';
}
}

    titleInput.addEventListener('input', function() {
    validateTitle();
});

    titleInput.addEventListener('invalid', function(event) {
    event.preventDefault();
    // Hiển thị lại thông báo lỗi nếu người dùng nhập thông tin không hợp lệ
    titleError.style.display = 'block';
});

    titleInput.addEventListener('focus', function() {
    if (!titleInput.validity.valid) {
    // Nếu trường đang nhập không hợp lệ, hiển thị thông báo lỗi
    titleError.style.display = 'block';
}
});

    titleInput.addEventListener('blur', function() {
    validateTitle();
});
}
