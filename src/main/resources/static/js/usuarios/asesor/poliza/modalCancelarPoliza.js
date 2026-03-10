// Precarga datos en el modal de cancelación de póliza
document.addEventListener("DOMContentLoaded", () => {

    const modal = document.getElementById("modalCancelarPoliza");
    if (!modal) return;

    // Cuando el modal se abre, carga los datos de la póliza
    modal.addEventListener("show.bs.modal", (event) => {

        const button = event.relatedTarget;
        if (!button) return;

        const idPoliza = button.dataset.idpoliza;
        const codigoPoliza = button.dataset.codigoPoliza;

        document.getElementById("polizaIdInput").value = idPoliza;
        document.getElementById("polizaIdVisible").value = codigoPoliza;
        document.getElementById("codigoPolizaInput").value = codigoPoliza;

    });

    if (window.abrirModalCancelar) {
        bootstrap.Modal.getOrCreateInstance(modal).show();
    }

});