    document.addEventListener("DOMContentLoaded", function () {
        var alerta = document.querySelector(".toast-alert");
        if (alerta) {
            setTimeout(function () {
                alerta.style.animation = "toastSalida 0.35s ease forwards";
                setTimeout(function () { alerta.remove(); }, 350);
            }, 5000);
        }
    });