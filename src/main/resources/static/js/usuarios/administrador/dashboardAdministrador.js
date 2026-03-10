/* Dashboard del administrador - fecha dinámica en el banner */

(function setFechaHoy() {
    const el = document.getElementById('fechaHoy');
    if (!el) return;

    const ahora = new Date();
    const opciones = {
        weekday: 'long',
        year:    'numeric',
        month:   'long',
        day:     'numeric'
    };

    // Capitaliza la primera letra de la fecha
    const texto = ahora.toLocaleDateString('es-CO', opciones);
    el.textContent = texto.charAt(0).toUpperCase() + texto.slice(1);
})();