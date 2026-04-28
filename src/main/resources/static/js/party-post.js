$(function () {
    const form = $("#apply-form");
    if (form.length === 0) {
        return;
    }

    form.on("submit", function (event) {
        event.preventDefault();

        const csrfToken = $('meta[name="csrf-token"]').attr("content");
        const csrfHeader = $('meta[name="csrf-header"]').attr("content");
        const button = $("#apply-button");
        const postId = button.data("post-id");
        const message = $("#apply-message").val();
        const output = $("#apply-result");

        button.prop("disabled", true);
        output.text("").removeClass("text-success text-danger");

        $.ajax({
            url: "/api/posts/" + postId + "/apply",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({ message: message }),
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        }).done(function (resp) {
            output.text(resp.message).removeClass("text-danger").addClass("text-success");
            $("#apply-message").val("");
        }).fail(function (xhr) {
            const response = xhr.responseJSON || {};
            output.text(response.message || "Failed to send application").removeClass("text-success").addClass("text-danger");
        }).always(function () {
            button.prop("disabled", false);
        });
    });
});
