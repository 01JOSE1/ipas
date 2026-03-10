/* =============================================
   DASHBOARD ADMINISTRADOR — JS
   Fecha dinámica en el banner de bienvenida
   ============================================= */

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

    // Capitaliza la primera letra
    const texto = ahora.toLocaleDateString('es-CO', opciones);
    el.textContent = texto.charAt(0).toUpperCase() + texto.slice(1);
})();