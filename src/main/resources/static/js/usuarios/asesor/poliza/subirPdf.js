/* =============================================
   CARGA PDF PÓLIZA IA — JS
   ============================================= */

const input    = document.getElementById("archivo");
const dropZone = document.getElementById("dropZone");
const fileInfo = document.getElementById("fileInfo");
const fileName = document.getElementById("fileName");
const fileSize = document.getElementById("fileSize");
const btnEnviar = document.getElementById("btnEnviar");

/* -------------------------------------------------------
   INPUT normal (clic)
------------------------------------------------------- */
input.addEventListener("change", () => {
    if (input.files.length > 0) {
        mostrarArchivo(input.files[0]);
    }
});

/* -------------------------------------------------------
   DRAG & DROP
------------------------------------------------------- */
dropZone.addEventListener("dragover", (e) => {
    e.preventDefault();
    dropZone.classList.add("dragover");
});

dropZone.addEventListener("dragleave", (e) => {
    if (!dropZone.contains(e.relatedTarget)) {
        dropZone.classList.remove("dragover");
    }
});

dropZone.addEventListener("drop", (e) => {
    e.preventDefault();
    dropZone.classList.remove("dragover");

    const file = e.dataTransfer.files[0];

    if (!file) return;

    if (file.type !== "application/pdf") {
        mostrarError("Solo se permiten archivos PDF.");
        return;
    }

    // Asignar al input real
    const dt = new DataTransfer();
    dt.items.add(file);
    input.files = dt.files;

    mostrarArchivo(file);
});

/* -------------------------------------------------------
   MOSTRAR INFO ARCHIVO
------------------------------------------------------- */
function mostrarArchivo(file) {

    if (file.type !== "application/pdf") {
        mostrarError("Solo se permiten archivos PDF.");
        limpiarArchivo();
        return;
    }

    const maxMb = 10;
    if (file.size > maxMb * 1024 * 1024) {
        mostrarError(`El archivo supera los ${maxMb} MB permitidos.`);
        limpiarArchivo();
        return;
    }

    fileName.textContent = file.name;
    fileSize.textContent = formatearTamano(file.size);

    fileInfo.classList.add("visible");
    dropZone.style.display = "none";
    btnEnviar.disabled = false;
}

/* -------------------------------------------------------
   LIMPIAR
------------------------------------------------------- */
function limpiarArchivo() {
    input.value          = "";
    fileInfo.classList.remove("visible");
    dropZone.style.display = "";
    btnEnviar.disabled   = true;
}

/* -------------------------------------------------------
   UTILIDADES
------------------------------------------------------- */
function formatearTamano(bytes) {
    if (bytes < 1024)       return `${bytes} B`;
    if (bytes < 1048576)    return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / 1048576).toFixed(2)} MB`;
}

function mostrarError(mensaje) {
    // Reutiliza el sistema de alertas existente si existe, si no usa un alert simple
    console.warn("[cargaPdf]", mensaje);
    alert(mensaje);
}