var contenedorlogin = document.getElementById("form-login");
var div = document.getElementById("miDiv");
var textosesion = document.getElementById("text-sesion");
var contenedorregister = document.getElementById("form-register");

var limiteDerecha = div.parentElement.offsetWidth - div.offsetWidth;
var limiteIzquierda = 0;

var movimientoDerecha = true;


/* -------------------------------------------------------
   Al cargar la página, si hay errores en el form de registro
   se mueve el bloque animado a la izquierda para dejarlo visible
------------------------------------------------------- */
document.addEventListener("DOMContentLoaded", function () {
    var hayErroresRegistro = document.querySelectorAll("#form-register .error").length > 0;

    if (hayErroresRegistro) {
        // Posicionar directamente sin animación como si ya se hubiera hecho click en "Crear Cuenta"
        contenedorlogin.style.left = "50%";
        textosesion.style.left = "-100%";
        contenedorregister.style.left = limiteDerecha + "px";
        contenedorregister.style.zIndex = "10";
        contenedorlogin.style.zIndex = "5";

        div.style.left = limiteIzquierda + "px";
        div.style.borderRadius = "50px 30% 20% 50px";

        // Actualizar el estado para que el siguiente click funcione correctamente
        movimientoDerecha = false;
    }
});


function moverYCambiar() {

    var nuevaPosicion = movimientoDerecha ? limiteIzquierda : limiteDerecha;

    if (movimientoDerecha) {
        contenedorlogin.style.left = "50%";
        textosesion.style.left = "-100%";
        contenedorregister.style.left = limiteDerecha + "px";
        contenedorregister.style.zIndex = "10";
        contenedorlogin.style.zIndex = "5";
    } else {
        contenedorlogin.style.left = limiteIzquierda + "px";
        textosesion.style.left = "0";
        contenedorregister.style.left = "0%";
        contenedorlogin.style.zIndex = "10";
        contenedorregister.style.zIndex = "5";
    }

    var nuevoBorde = movimientoDerecha ? "50px 30% 20% 50px" : "30% 50px 50px 20%";

    div.style.left = nuevaPosicion + "px";
    div.style.borderRadius = nuevoBorde;

    movimientoDerecha = !movimientoDerecha;
}


/* -------------------------------------------------------
   toggleContraseñaVisibility
   Acepta el ID del input y el ID del botón (string)
   como los pasa el HTML: onclick="toggleContraseñaVisibility('id-input','id-boton')"
------------------------------------------------------- */
function toggleContraseñaVisibility(inputId, buttonId) {
    var input = document.getElementById(inputId);
    var boton = document.getElementById(buttonId);

    if (!input) {
        console.error("Input no encontrado:", inputId);
        return;
    }

    var icono = boton ? boton.querySelector("i") : null;

    if (input.type === "password") {
        input.type = "text";
        if (icono) {
            icono.classList.remove("fa-eye");
            icono.classList.add("fa-eye-slash");
        }
    } else {
        input.type = "password";
        if (icono) {
            icono.classList.remove("fa-eye-slash");
            icono.classList.add("fa-eye");
        }
    }
}