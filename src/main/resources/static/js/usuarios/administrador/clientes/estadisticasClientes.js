/* =============================================
   ESTADÍSTICAS CLIENTES — JS (Chart.js 4)
   ============================================= */

/* ── Paleta corporativa ── */
const PALETA = {
    navy:   '#081E42',
    blue:   '#0F4C9C',
    cyan:   '#00B4D8',
    teal:   '#0096B7',
    amber:  '#FFC107',
    green:  '#28A745',
    red:    '#DC3545',
    purple: '#6F42C1',
    gray:   '#6C757D',
    light:  '#ADB5BD',
};

const COLORES_ACTIVOS   = [PALETA.green, PALETA.amber];
const COLORES_EST_CIVIL = [PALETA.navy, PALETA.blue, PALETA.cyan, PALETA.amber, PALETA.purple, PALETA.gray];
const COLORES_CIUDAD    = [PALETA.navy, PALETA.blue, PALETA.cyan, PALETA.teal, PALETA.amber, PALETA.purple, PALETA.gray, PALETA.light];
const COLORES_TIPO_DOC  = [PALETA.navy, PALETA.blue, PALETA.cyan, PALETA.amber, PALETA.purple];

/* ── Configuración global Chart.js ── */
Chart.defaults.font.family = "'DM Sans', sans-serif";
Chart.defaults.font.size   = 12;
Chart.defaults.color       = '#6C757D';
Chart.defaults.plugins.legend.display = false;

/* ── Leer datos del DOM ── */
function parsearLista(str) {
    if (!str || str.trim() === '' || str === '[]') return [];
    return str.replace(/^\[|\]$/g, '').split(',').map(s => s.trim());
}

function parsearNumericos(str) {
    return parsearLista(str).map(Number);
}

const datos = document.getElementById('chartData');
const activosLabels     = parsearLista(datos.dataset.activosLabels);
const activosValores    = parsearNumericos(datos.dataset.activosValores);
const ciudadLabels      = parsearLista(datos.dataset.ciudadLabels);
const ciudadValores     = parsearNumericos(datos.dataset.ciudadValores);
const estCivilLabels    = parsearLista(datos.dataset.estadoCivilLabels);
const estCivilValores   = parsearNumericos(datos.dataset.estadoCivilValores);
const tipoDocLabels     = parsearLista(datos.dataset.tipoDocLabels);
const tipoDocValores    = parsearNumericos(datos.dataset.tipoDocValores);

/* ── Utilidad: leyenda custom ── */
function construirLeyenda(idContenedor, labels, colores) {
    const contenedor = document.getElementById(idContenedor);
    if (!contenedor) return;
    contenedor.innerHTML = '';
    labels.forEach(function (label, i) {
        const item = document.createElement('div');
        item.className = 'legend-item';
        item.innerHTML = `
            <span class="legend-dot" style="background:${colores[i % colores.length]}"></span>
            <span>${label}</span>
        `;
        contenedor.appendChild(item);
    });
}

/* ── Config tooltip reutilizable ── */
function tooltipPorcentaje(ctx) {
    const total = ctx.dataset.data.reduce((a, b) => a + b, 0);
    const pct   = total > 0 ? ((ctx.parsed / total) * 100).toFixed(1) : 0;
    return ` ${ctx.label}: ${ctx.parsed} (${pct}%)`;
}

/* ─────────────────────────────────────────────
   GRÁFICA 1 — Donut: activos vs inactivos
   ───────────────────────────────────────────── */
if (activosLabels.length > 0) {
    new Chart(document.getElementById('chartActivos'), {
        type: 'doughnut',
        data: {
            labels: activosLabels,
            datasets: [{
                data: activosValores,
                backgroundColor: COLORES_ACTIVOS,
                borderColor: '#fff',
                borderWidth: 3,
                hoverOffset: 8,
            }]
        },
        options: {
            cutout: '68%',
            plugins: { tooltip: { callbacks: { label: tooltipPorcentaje } } },
            animation: { animateRotate: true, duration: 700 },
        }
    });
    construirLeyenda('legendActivos', activosLabels, COLORES_ACTIVOS);
}

/* ─────────────────────────────────────────────
   GRÁFICA 2 — Donut: estado civil
   ───────────────────────────────────────────── */
if (estCivilLabels.length > 0) {
    new Chart(document.getElementById('chartEstadoCivil'), {
        type: 'doughnut',
        data: {
            labels: estCivilLabels,
            datasets: [{
                data: estCivilValores,
                backgroundColor: COLORES_EST_CIVIL,
                borderColor: '#fff',
                borderWidth: 3,
                hoverOffset: 8,
            }]
        },
        options: {
            cutout: '68%',
            plugins: { tooltip: { callbacks: { label: tooltipPorcentaje } } },
            animation: { animateRotate: true, duration: 700 },
        }
    });
    construirLeyenda('legendEstadoCivil', estCivilLabels, COLORES_EST_CIVIL);
}

/* ─────────────────────────────────────────────
   GRÁFICA 3 — Bar horizontal: clientes por ciudad
   ───────────────────────────────────────────── */
if (ciudadLabels.length > 0) {
    new Chart(document.getElementById('chartCiudad'), {
        type: 'bar',
        data: {
            labels: ciudadLabels,
            datasets: [{
                label: 'Clientes',
                data: ciudadValores,
                backgroundColor: ciudadValores.map((_, i) => COLORES_CIUDAD[i % COLORES_CIUDAD.length]),
                borderRadius: 6,
                borderSkipped: false,
                maxBarThickness: 36,
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                tooltip: { callbacks: { label: ctx => ` ${ctx.parsed.x} clientes` } }
            },
            scales: {
                x: {
                    beginAtZero: true,
                    grid: { color: 'rgba(0,0,0,0.05)' },
                    ticks: { precision: 0 }
                },
                y: {
                    grid: { display: false },
                    ticks: { font: { weight: '600' } }
                }
            },
            animation: { duration: 600 }
        }
    });
}

/* ─────────────────────────────────────────────
   GRÁFICA 4 — Bar vertical: tipo de documento
   ───────────────────────────────────────────── */
if (tipoDocLabels.length > 0) {
    new Chart(document.getElementById('chartTipoDoc'), {
        type: 'bar',
        data: {
            labels: tipoDocLabels,
            datasets: [{
                label: 'Clientes',
                data: tipoDocValores,
                backgroundColor: COLORES_TIPO_DOC,
                borderRadius: 8,
                borderSkipped: false,
                maxBarThickness: 52,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                tooltip: { callbacks: { label: ctx => ` ${ctx.parsed.y} clientes` } }
            },
            scales: {
                x: {
                    grid: { display: false },
                    ticks: { font: { weight: '600' } }
                },
                y: {
                    beginAtZero: true,
                    grid: { color: 'rgba(0,0,0,0.05)' },
                    ticks: { precision: 0 }
                }
            },
            animation: { duration: 600 }
        }
    });
}