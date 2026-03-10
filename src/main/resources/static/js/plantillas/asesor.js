/* Plantilla para asesor - navegación automática, sidebar y dropdown */

// Context path inyectado desde Thymeleaf
const CTX = (window.IPAS_CTX || '').replace(/\/$/, '');

// Mapa de secciones con rutas que se activan automáticamente
// Ejemplo: /asesor/poliza/ver y sus sub-rutas activan la sección de Pólizas
const SECCIONES = {
    '/asesor/cliente' : { titulo: 'Clientes'   },
    '/asesor/poliza'  : { titulo: 'Pólizas'    },
    '/asesor/perfil': { titulo: 'Mi Perfil'  },
    '/asesor/ayuda'   : { titulo: 'Ayuda'      },
    '/asesor/'        : { titulo: 'Dashboard', exacto: true },
};

// Quita el context path de la URL actual
function pathSinCtx() {
    const path = window.location.pathname;
    return CTX ? path.replace(CTX, '') || '/' : path;
}

// Encuentra qué sección está activa
function seccionActiva() {
    const path = pathSinCtx();

    for (const [prefijo, seccion] of Object.entries(SECCIONES)) {
        if (seccion.exacto) {
            if (path === '/asesor/' || path === '/asesor') return prefijo;
            continue;
        }
        if (path.startsWith(prefijo)) return prefijo;
    }

    return null;
}

// Marca la sección activa en la navegación
function marcarNavActivo() {
    const activa = seccionActiva();

    document.querySelectorAll('.nav-item[data-path]').forEach(item => {
        item.classList.toggle('active', item.getAttribute('data-path') === activa);
    });
}

// Actualiza el título de la página según la sección activa
function actualizarTitulo() {
    const activa  = seccionActiva();
    const titulo  = activa ? (SECCIONES[activa]?.titulo ?? 'IPAS') : 'IPAS';

    const el = document.getElementById('topbar-page-title');
    if (el) el.textContent = titulo;
    document.title = 'IPAS - ' + titulo;
}

// Abre o cierra el sidebar
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (!sidebar) return;

    if (window.innerWidth <= 700) {
        sidebar.classList.toggle('mobile-open');
        return;
    }

    sidebar.classList.toggle('collapsed');
    localStorage.setItem('sidebarCollapsed', sidebar.classList.contains('collapsed'));
}

// Restaura el estado del sidebar desde el almacenamiento local
function restaurarSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (!sidebar || window.innerWidth <= 700) return;

    if (localStorage.getItem('sidebarCollapsed') === 'true') {
        sidebar.classList.add('collapsed');
    }
}

// Abre o cierra el menú del usuario
function toggleUserMenu() {
    const dropdown = document.getElementById('userDropdown');
    const chevron  = document.getElementById('userChevron');
    if (!dropdown) return;

    const abierto = dropdown.classList.toggle('open');
    if (chevron) chevron.classList.toggle('open', abierto);
}

document.addEventListener('click', function (e) {
    const trigger  = document.getElementById('userDropdownTrigger');
    const dropdown = document.getElementById('userDropdown');
    const chevron  = document.getElementById('userChevron');

    if (!dropdown || !trigger) return;
    if (!trigger.contains(e.target) && !dropdown.contains(e.target)) {
        dropdown.classList.remove('open');
        if (chevron) chevron.classList.remove('open');
    }
});

// Cierra el sidebar móvil cuando se hace clic fuera
document.addEventListener('click', function (e) {
    if (window.innerWidth > 700) return;

    const sidebar = document.getElementById('sidebar');
    const toggle  = document.querySelector('.sidebar-toggle');
    if (!sidebar) return;

    if (!sidebar.contains(e.target) && !toggle?.contains(e.target)) {
        sidebar.classList.remove('mobile-open');
    }
});

// Inicialización cuando la página carga
document.addEventListener('DOMContentLoaded', function () {
    restaurarSidebar();
    marcarNavActivo();
    actualizarTitulo();
});