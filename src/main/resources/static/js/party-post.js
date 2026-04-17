$(function () {
    const button = $("#apply-button");
    if (button.length === 0) {
        return;
    }

    button.on("click", function () {
        const csrfToken = $('meta[name="csrf-token"]').attr("content");
        const csrfHeader = $('meta[name="csrf-header"]').attr("content");
        const postId = button.data("post-id");
        const message = $("#apply-message").val();
        const output = $("#apply-result");

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
        });
    });
});
