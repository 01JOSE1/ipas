/* Buscador autocomplete de clientes para formulario de póliza */

const inputBusqueda   = document.getElementById("busquedaCliente");
const inputIdCliente  = document.getElementById("idCliente");
const dropdown        = document.getElementById("resultadosCliente");
const btnClear        = document.getElementById("searchClear");

let timeout = null;
let clienteSeleccionado = false;

// Controla el evento de búsqueda con debounce
inputBusqueda.addEventListener("input", function () {

    const termino = this.value.trim();

    // Mostrar/ocultar botón limpiar
    btnClear.classList.toggle("visible", termino.length > 0);

    // Si borra lo que había seleccionado, limpiar el id oculto
    if (clienteSeleccionado && termino !== inputBusqueda.dataset.valorSeleccionado) {
        inputIdCliente.value = "";
        clienteSeleccionado = false;
    }

    clearTimeout(timeout);

    if (termino.length < 3) {
        cerrarDropdown();
        return;
    }

    timeout = setTimeout(() => buscarClientes(termino), 300);
});

function buscarClientes(termino) {
    fetch(`/ipas/asesor/cliente/buscar-cliente?termino=${encodeURIComponent(termino)}`)
        .then(r => r.json())
        .then(data => renderResultados(data))
        .catch(() => cerrarDropdown());
}

function renderResultados(data) {
    dropdown.innerHTML = "";

    if (data.length === 0) {
        dropdown.innerHTML = `<div class="search-no-results">
            <i class="fa fa-search"></i> Sin resultados para esa búsqueda
        </div>`;
        abrirDropdown();
        return;
    }

    data.forEach(cliente => {
        const inicial = cliente.nombreCompleto.charAt(0).toUpperCase();

        const item = document.createElement("div");
        item.className = "search-result-item";
        item.innerHTML = `
            <div class="result-avatar">${inicial}</div>
            <div class="result-info">
                <span class="result-nombre">${cliente.nombreCompleto}</span>
                <span class="result-doc">${cliente.numeroDocumento}</span>
            </div>
        `;

        item.addEventListener("mousedown", (e) => {
            // mousedown en lugar de click para que no dispare blur antes
            e.preventDefault();
            seleccionarCliente(cliente);
        });

        dropdown.appendChild(item);
    });

    abrirDropdown();
}

function seleccionarCliente(cliente) {
    inputBusqueda.value        = cliente.nombreCompleto;
    inputIdCliente.value       = cliente.idCliente;
    clienteSeleccionado        = true;
    inputBusqueda.dataset.valorSeleccionado = cliente.nombreCompleto;
    cerrarDropdown();
}

function abrirDropdown()  { dropdown.classList.add("visible"); }
function cerrarDropdown() { dropdown.classList.remove("visible"); dropdown.innerHTML = ""; }

// Cerrar al hacer clic fuera
document.addEventListener("click", function (e) {
    if (!inputBusqueda.contains(e.target) && !dropdown.contains(e.target)) {
        cerrarDropdown();
    }
});

// Cerrar con Escape
inputBusqueda.addEventListener("keydown", function (e) {
    if (e.key === "Escape") cerrarDropdown();
});

/* -------------------------------------------------------
   LIMPIAR BÚSQUEDA
------------------------------------------------------- */
function limpiarBusqueda() {
    inputBusqueda.value  = "";
    inputIdCliente.value = "";
    clienteSeleccionado  = false;
    btnClear.classList.remove("visible");
    cerrarDropdown();
    inputBusqueda.focus();
}

/* -------------------------------------------------------
   NOMBRE DEL ARCHIVO SELECCIONADO
------------------------------------------------------- */
function mostrarNombreArchivo(input) {
    const label = document.getElementById("fileUploadText");
    if (input.files && input.files.length > 0) {
        const nombre = input.files[0].name;
        label.textContent = nombre;
        label.style.color = "var(--texto-principal)";
        label.style.fontWeight = "600";
    } else {
        label.textContent = "Seleccionar archivo PDF...";
        label.style.color = "";
        label.style.fontWeight = "";
    }
}