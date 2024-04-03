$(document).ready(function () {
    $("#copyButton").click(function () {
        var r = document.createRange();
        r.selectNode(document.getElementById("processedOutput"));
        window.getSelection().removeAllRanges();
        window.getSelection().addRange(r);
        document.execCommand("copy");
        window.getSelection().removeAllRanges();
        alert("Text copied to clipboard!");
    });

    const dragArea = document.getElementById("drag-area");

    dragArea.addEventListener("dragover", (event) => {
        event.preventDefault();
        dragArea.classList.add("border-indigo-500");
        dragArea.classList.add("bg-gray-200");
    });

    dragArea.addEventListener("dragleave", () => {
        dragArea.classList.remove("bg-gray-200");
        dragArea.classList.remove("border-indigo-500");
    });

    dragArea.addEventListener("drop", (event) => {
        event.preventDefault();
        dragArea.classList.remove("bg-gray-200");
        dragArea.classList.remove("border-indigo-500");
        const file = event.dataTransfer.files[0];
        if (file) {
            const fileInput = document.getElementById("file");
            fileInput.files = event.dataTransfer.files;
            generateImage(fileInput);
        }
    });
});

var isUploadedImageAreaShown = false;

function showUploadedImageArea() {
    var uploadedImageArea = $("#uploaded-image-area");
    uploadedImageArea.css("display", "flex");
    uploadedImageArea.addClass("justify-center");
}

function hideUploadedImageArea() {
    var uploadedImageArea = $("#uploaded-image-area");
    uploadedImageArea.hide();
    uploadedImageArea.removeClass("justify-center");
}

function generateImage(inp) {
    if (inp.files && inp.files[0]) {
        showUploadedImageArea();
        var reader = new FileReader();
        reader.onload = function (e) {
            $("#uploaded-image").attr("src", e.target.result).width(200).height(200);
        };

        reader.readAsDataURL(inp.files[0]);
        enableSelectionButton();
        enableProcesseBotton();
    }
}

// Remove the uploaded image
function removeImage() {
    var uploadedImage = $("#uploaded-image");
    hideUploadedImageArea();
    uploadedImage.attr("src", "");
    uploadedImage.val("");
    $("#processedOutput").html("Transformed Text will be shown here...");
    disableSelectionBotton();
    disableProcessBotton();
}

function enableProcesseBotton() {
    var processButton = $("#process-button");
    processButton.prop("disabled", false);
    processButton.addClass("bg-indigo-500");
}

function disableProcessBotton() {
    var processButton = $("#process-button");
    processButton.prop("disabled", true);
    processButton.removeClass("bg-indigo-500");
}

function enableSelectionButton() {
    var selectButton = $("#language_selector");
    selectButton.prop("disabled", false);
}

function disableSelectionBotton() {
    var selectButton = $("#language_selector");
    selectButton.prop("disabled", true);
    selectButton.val("");
}

// Display response from api in text box
function displayProcessedResponse(response) {
    // $("#processedOutput").html(JSON.stringify(response, null, 2));
    $("#processedOutput").html(response);
}

function processImage() {
    // Get value from language option
    var language_option_value = $("#language_selector").val();
    console.log(language_option_value);
    // <select id="language_selector">
    //   <option value="mm">Myanmar</option>
    //   <option value="en">English</option>
    //   <option value="gm">German</option>
    // </select>
    // If we select Myanmar from option, then the output value will be "mm"

    // Create a new XMLHttpRequest object
    var xhr = new XMLHttpRequest();

    // Configure the request
    // The API for transforming the image to text
    //xhr.open("GET", "https://dummy.restapiexample.com/api/v1/employees", true);
    xhr.open("POST", "http://localhost:8080/ocr", true);

    // Set up a function to handle the response
    xhr.onload = function () {
        if (xhr.status >= 200 && xhr.status < 300) {
            // Request was successful
            // var response = JSON.parse(xhr.responseText);
            var response = xhr.responseText;
            // Do something with the response data
            console.log(response);
            try {
                // Attempt to display the processed response
                displayProcessedResponse(response);
            } catch (error) {
                // Handle any errors that occur during displayProcessedResponse
                console.error("Error while displaying processed response:", error);
            }
        } else {
            // Request failed
            console.error("Request failed with status " + xhr.status);
            alert(`Request Fail with error(s) ${xhr.status}`);
        }
    };

    // Set up a function to handle errors
    xhr.onerror = function () {
        console.error("Request failed");
        alert(`Request Fail with error(s) ${xhr.status}`);
    };
    // Get the image file input element
    var imageInput = document.getElementById("file");

    // Create a new FormData object
    var formData = new FormData();

    // Append the image file to the FormData object
    formData.append("image", imageInput.files[0]);
    formData.append("language",language_option_value)

    // Set the request header to send the image data in the request body
    // xhr.setRequestHeader("Content-Type", "multipart/form-data");

    // Send the request
    xhr.send(formData);
}





