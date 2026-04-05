$(function () {
    function check(field, endpoint, outputId) {
        const value = $(field).val();
        const out = $(outputId);

        if (!value) {
            out.text("").removeClass("text-success text-danger");
            return;
        }

        $.ajax({
            url: endpoint,
            method: "GET",
            data: { value: value }
        }).done(function (resp) {
            if (resp.available) {
                out.text("Available").removeClass("text-danger").addClass("text-success");
            } else {
                out.text("Already in use").removeClass("text-success").addClass("text-danger");
            }
        }).fail(function () {
            out.text("Check failed").removeClass("text-success").addClass("text-danger");
        });
    }

    $("#username").on("blur", function () {
        check("#username", "/api/auth/check-username", "#username-check");
    });

    $("#email").on("blur", function () {
        check("#email", "/api/auth/check-email", "#email-check");
    });
});
