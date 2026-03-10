/* Dashboard del asesor - fecha y hora en el banner de bienvenida */

// Muestra la fecha y hora actual en el dashboard
document.addEventListener('DOMContentLoaded', function () {
    var fechaEl = document.getElementById('fechaHoy');
    if (fechaEl) {
        var ahora    = new Date();
        var opciones = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        var fechaStr = ahora.toLocaleDateString('es-CO', opciones);
        fechaStr     = fechaStr.charAt(0).toUpperCase() + fechaStr.slice(1);
        var horaStr  = ahora.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' });
        fechaEl.innerHTML = fechaStr + '<br>' + horaStr;
    }
});