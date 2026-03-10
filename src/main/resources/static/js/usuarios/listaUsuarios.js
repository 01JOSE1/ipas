/* =============================================
   LISTA USUARIOS — ADMINISTRADOR JS
   Modales: cambiar estado / cambiar rol
   ============================================= */

/* ─────────────────────────────────────────────
   ESTADOS disponibles con sus iconos y etiquetas
   ───────────────────────────────────────────── */
const ESTADOS_DISPONIBLES = [
    { value: 'ACTIVO',     label: 'Activo',     icono: 'fa-check-circle'  },
    { value: 'INACTIVO',   label: 'Inactivo',   icono: 'fa-minus-circle'  },
    { value: 'SUSPENDIDO', label: 'Suspendido', icono: 'fa-ban'           },
];

/* ─────────────────────────────────────────────
   MODAL ESTADO
   ───────────────────────────────────────────── */
function abrirModalEstado(btn) {
    const idUsuario    = btn.dataset.id;
    const nombre       = btn.dataset.nombre;
    const estadoActual = btn.dataset.estadoActual;

    // Setear datos en el modal
    document.getElementById('modalEstadoNombre').textContent   = nombre;
    document.getElementById('inputEstadoIdUsuario').value      = idUsuario;
    document.getElementById('btnConfirmarEstado').disabled     = true;

    // Construir opciones de estado (excluye el estado actual)
    const contenedor = document.getElementById('estadoOpciones');
    contenedor.innerHTML = '';

    ESTADOS_DISPONIBLES.forEach(function (est) {
        const esCurrent = est.value === estadoActual;

        const label = document.createElement('label');
        label.className     = 'opcion-item opcion-estado';
        label.dataset.estado = est.value;

        label.innerHTML = `
            <input type="radio" name="estadoCambio" value="${est.value}"
                   ${esCurrent ? 'disabled' : ''}
                   onchange="onEstadoSeleccionado()">
            <span class="opcion-icono"><i class="fa ${est.icono}"></i></span>
            <span class="opcion-label">${est.label}</span>
            ${esCurrent ? '<span style="font-size:11px;color:var(--texto-desactivado);margin-left:auto;">actual</span>' : ''}
            <span class="opcion-check"><i class="fa fa-check"></i></span>
        `;

        contenedor.appendChild(label);
    });

    document.getElementById('modalEstado').classList.add('open');
    document.body.style.overflow = 'hidden';
}

function onEstadoSeleccionado() {
    document.getElementById('btnConfirmarEstado').disabled = false;
}

function cerrarModalEstadoBtn() {
    _cerrarModal('modalEstado');
}

function cerrarModalEstado(event) {
    // Cierra solo si se hace click en el overlay (fuera del box)
    if (event.target === document.getElementById('modalEstado')) {
        _cerrarModal('modalEstado');
    }
}

/* ─────────────────────────────────────────────
   MODAL ROL
   ───────────────────────────────────────────── */
function abrirModalRol(btn) {
    const idUsuario  = btn.dataset.id;
    const nombre     = btn.dataset.nombre;
    const rolActual  = btn.dataset.rolActual;   // idRol como string

    // Setear datos en el modal
    document.getElementById('modalRolNombre').textContent  = nombre;
    document.getElementById('inputRolIdUsuario').value     = idUsuario;
    document.getElementById('btnConfirmarRol').disabled    = true;

    // Marcar como "actual" el rol vigente y limpiar selección previa
    document.querySelectorAll('#rolOpciones .opcion-rol').forEach(function (label) {
        const input    = label.querySelector('input[type="radio"]');
        const idRol    = label.dataset.id;
        const esCurrent = String(idRol) === String(rolActual);

        input.checked  = false;
        input.disabled = esCurrent;

        // Indicador visual "actual"
        let badge = label.querySelector('.badge-actual');
        if (esCurrent && !badge) {
            badge = document.createElement('span');
            badge.className = 'badge-actual';
            badge.style.cssText = 'font-size:11px;color:var(--texto-desactivado);margin-left:auto;';
            badge.textContent = 'actual';
            label.appendChild(badge);
        } else if (!esCurrent && badge) {
            badge.remove();
        }
    });

    document.getElementById('modalRol').classList.add('open');
    document.body.style.overflow = 'hidden';
}

function onRolSeleccionado() {
    document.getElementById('btnConfirmarRol').disabled = false;
}

function cerrarModalRolBtn() {
    _cerrarModal('modalRol');
}

function cerrarModalRol(event) {
    if (event.target === document.getElementById('modalRol')) {
        _cerrarModal('modalRol');
    }
}

/* ─────────────────────────────────────────────
   UTILIDAD
   ───────────────────────────────────────────── */
function _cerrarModal(idModal) {
    document.getElementById(idModal).classList.remove('open');
    document.body.style.overflow = '';
}

// Cierre con tecla Escape
document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') {
        _cerrarModal('modalEstado');
        _cerrarModal('modalRol');
    }
});