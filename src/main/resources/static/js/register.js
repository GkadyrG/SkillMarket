$(function () {
    const password = $("#password");
    const confirm = $("#confirmPassword");
    const output = $("#password-check");

    function validatePasswords() {
        if (!password.val() && !confirm.val()) {
            output.text("").removeClass("text-success text-danger");
            return;
        }

        if (password.val() === confirm.val()) {
            output.text("Passwords match").removeClass("text-danger").addClass("text-success");
        } else {
            output.text("Passwords do not match").removeClass("text-success").addClass("text-danger");
        }
    }

    password.on("input", validatePasswords);
    confirm.on("input", validatePasswords);
    $("#email").on("input", function () {
        $("#email-check").text("").removeClass("text-success text-danger");
    });
});
