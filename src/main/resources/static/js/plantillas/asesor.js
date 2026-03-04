/* =============================================
   PLANTILLA ASESOR — JS
   ============================================= */

// ─────────────────────────────────────────────
// CONTEXT PATH — inyectado desde Thymeleaf
// ─────────────────────────────────────────────
const CTX = (window.IPAS_CTX || '').replace(/\/$/, '');

// ─────────────────────────────────────────────
// MAPA DE SECCIONES
// La clave es el prefijo único de la sección.
// Cualquier ruta que empiece con ese prefijo
// activará esa pestaña automáticamente.
//
// Ejemplos que se activan solos sin tocar nada:
//   /asesor/poliza/ver          → Pólizas
//   /asesor/poliza/detalle/123  → Pólizas
//   /asesor/poliza/editar/456   → Pólizas
//   /asesor/cliente/ver         → Clientes
//   /asesor/cliente/crear       → Clientes
// ─────────────────────────────────────────────
const SECCIONES = {
    '/asesor/cliente' : { titulo: 'Clientes'   },
    '/asesor/poliza'  : { titulo: 'Pólizas'    },
    '/asesor/perfil': { titulo: 'Mi Perfil'  },
    '/asesor/ayuda'   : { titulo: 'Ayuda'      },
    '/asesor/'        : { titulo: 'Dashboard', exacto: true },
};

// ─────────────────────────────────────────────
// Quita el context path de la URL actual
// ─────────────────────────────────────────────
function pathSinCtx() {
    const path = window.location.pathname;
    return CTX ? path.replace(CTX, '') || '/' : path;
}

// ─────────────────────────────────────────────
// Encuentra qué sección está activa
// ─────────────────────────────────────────────
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

// ─────────────────────────────────────────────
// NAV ACTIVO
// ─────────────────────────────────────────────
function marcarNavActivo() {
    const activa = seccionActiva();

    document.querySelectorAll('.nav-item[data-path]').forEach(item => {
        item.classList.toggle('active', item.getAttribute('data-path') === activa);
    });
}

// ─────────────────────────────────────────────
// TÍTULO DEL TOPBAR
// ─────────────────────────────────────────────
function actualizarTitulo() {
    const activa  = seccionActiva();
    const titulo  = activa ? (SECCIONES[activa]?.titulo ?? 'IPAS') : 'IPAS';

    const el = document.getElementById('topbar-page-title');
    if (el) el.textContent = titulo;
    document.title = 'IPAS - ' + titulo;
}

// ─────────────────────────────────────────────
// SIDEBAR TOGGLE
// ─────────────────────────────────────────────
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

function restaurarSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (!sidebar || window.innerWidth <= 700) return;

    if (localStorage.getItem('sidebarCollapsed') === 'true') {
        sidebar.classList.add('collapsed');
    }
}

// ─────────────────────────────────────────────
// DROPDOWN USUARIO
// ─────────────────────────────────────────────
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

// ─────────────────────────────────────────────
// CERRAR SIDEBAR MOBILE al clic fuera
// ─────────────────────────────────────────────
document.addEventListener('click', function (e) {
    if (window.innerWidth > 700) return;

    const sidebar = document.getElementById('sidebar');
    const toggle  = document.querySelector('.sidebar-toggle');
    if (!sidebar) return;

    if (!sidebar.contains(e.target) && !toggle?.contains(e.target)) {
        sidebar.classList.remove('mobile-open');
    }
});

// ─────────────────────────────────────────────
// INIT
// ─────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', function () {
    restaurarSidebar();
    marcarNavActivo();
    actualizarTitulo();
});