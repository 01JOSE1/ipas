/* =============================================
   LAYOUT ADMIN — JS
   Sidebar toggle, user dropdown, nav activo
   ============================================= */

/**
 * Toggle sidebar colapsado / expandido
 */
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('collapsed');

    // En móvil usamos mobile-open en lugar de collapsed
    if (window.innerWidth <= 700) {
        sidebar.classList.remove('collapsed');
        sidebar.classList.toggle('mobile-open');
    }
}

/**
 * Toggle dropdown de usuario
 */
function toggleUserMenu() {
    const dropdown = document.getElementById('userDropdown');
    const chevron  = document.getElementById('userChevron');
    dropdown.classList.toggle('open');
    chevron.classList.toggle('open');
}

/**
 * Cierra el dropdown si se hace click fuera
 */
document.addEventListener('click', function (e) {
    const trigger  = document.getElementById('userDropdownTrigger');
    const dropdown = document.getElementById('userDropdown');
    const chevron  = document.getElementById('userChevron');

    if (trigger && dropdown && !trigger.contains(e.target) && !dropdown.contains(e.target)) {
        dropdown.classList.remove('open');
        chevron && chevron.classList.remove('open');
    }
});

/**
 * Marca el nav-item activo según la URL actual
 * y setea el título del topbar
 */
(function setActiveNav() {
    const ctx      = (window.IPAS_CTX || '/').replace(/\/$/, '');
    const path     = window.location.pathname;
    const navItems = document.querySelectorAll('.nav-item');

    // Mapa de rutas → títulos
    const titulos = {
        '/admin/'                       : 'Dashboard',
        '/admin/usuarios'               : 'Gestión de Usuarios',
        '/admin/poliza'                 : 'Estadísticas de Pólizas',
        '/admin/cliente'                : 'Estadísticas de Clientes',
        '/usuarios/perfil'              : 'Mi Perfil',
    };

    let tituloActual = 'IPAS';

    navItems.forEach(function (item) {
        const datPath = item.getAttribute('data-path') || '';
        const fullPath = ctx + datPath;

        if (datPath && path.startsWith(fullPath) && datPath !== '/admin/') {
            item.classList.add('active');
            tituloActual = titulos[datPath] || 'IPAS';
        } else if (datPath === '/admin/' && (path === ctx + '/admin/' || path === ctx + '/admin')) {
            item.classList.add('active');
            tituloActual = 'Dashboard';
        } else {
            item.classList.remove('active');
        }
    });

    const titleEl = document.getElementById('topbar-page-title');
    if (titleEl) titleEl.textContent = tituloActual;
})();