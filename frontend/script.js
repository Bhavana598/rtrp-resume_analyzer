function upload() {
    const fileInput = document.getElementById("resume");
    const file = fileInput.files[0];

    if (!file) {
        alert("Please upload a resume PDF");
        return;
    }

    const formData = new FormData();

    
    formData.append("file", file);

    fetch("http://localhost:8080/analyze", {
        method: "POST",
        body: formData
    })
    .then(response => response.text())
    .then(data => {
        document.getElementById("result").innerText = data;
    })
    .catch(error => {
        document.getElementById("result").innerText = "Server error";
    });
}