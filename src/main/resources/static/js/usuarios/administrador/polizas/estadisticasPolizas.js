/* =============================================
   ESTADÍSTICAS PÓLIZAS — JS (Chart.js 4)
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

const COLORES_ESTADO = [
    PALETA.green,
    PALETA.amber,
    PALETA.red,
    PALETA.gray,
    PALETA.purple,
    PALETA.cyan,
];

const COLORES_RAMO = [
    PALETA.navy,
    PALETA.blue,
    PALETA.cyan,
    PALETA.teal,
    PALETA.amber,
    PALETA.purple,
    PALETA.gray,
];

/* ── Configuración global Chart.js ── */
Chart.defaults.font.family = "'DM Sans', sans-serif";
Chart.defaults.font.size   = 12;
Chart.defaults.color       = '#6C757D';
Chart.defaults.plugins.legend.display = false; // usamos leyenda custom

/* ── Leer datos del DOM ── */
function parsearLista(str) {
    if (!str || str.trim() === '' || str === '[]') return [];
    // Thymeleaf serializa las listas como "[a, b, c]"
    return str.replace(/^\[|\]$/g, '').split(',').map(s => s.trim());
}

function parsearNumericos(str) {
    return parsearLista(str).map(Number);
}

const datos = document.getElementById('chartData');
const estadoLabels      = parsearLista(datos.dataset.estadoLabels);
const estadoValores     = parsearNumericos(datos.dataset.estadoValores);
const ramoLabels        = parsearLista(datos.dataset.ramoLabels);
const ramoValores       = parsearNumericos(datos.dataset.ramoValores);
const aseguradoraLabels = parsearLista(datos.dataset.aseguradoraLabels);
const aseguradoraValores= parsearNumericos(datos.dataset.aseguradoraValores);

/* ── Utilidad: construir leyenda custom ── */
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

/* ─────────────────────────────────────────────
   GRÁFICA 1 — Donut: distribución por estado
   ───────────────────────────────────────────── */
if (estadoLabels.length > 0) {
    new Chart(document.getElementById('chartEstado'), {
        type: 'doughnut',
        data: {
            labels: estadoLabels,
            datasets: [{
                data: estadoValores,
                backgroundColor: COLORES_ESTADO,
                borderColor: '#fff',
                borderWidth: 3,
                hoverOffset: 8,
            }]
        },
        options: {
            cutout: '68%',
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function (ctx) {
                            const total = ctx.dataset.data.reduce((a, b) => a + b, 0);
                            const pct   = total > 0 ? ((ctx.parsed / total) * 100).toFixed(1) : 0;
                            return ` ${ctx.label}: ${ctx.parsed} (${pct}%)`;
                        }
                    }
                }
            },
            animation: { animateRotate: true, duration: 700 },
        }
    });
    construirLeyenda('legendEstado', estadoLabels, COLORES_ESTADO);
}

/* ─────────────────────────────────────────────
   GRÁFICA 2 — Bar vertical: pólizas por ramo
   ───────────────────────────────────────────── */
if (ramoLabels.length > 0) {
    new Chart(document.getElementById('chartRamo'), {
        type: 'bar',
        data: {
            labels: ramoLabels,
            datasets: [{
                label: 'Pólizas',
                data: ramoValores,
                backgroundColor: COLORES_RAMO,
                borderRadius: 8,
                borderSkipped: false,
                maxBarThickness: 52,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                tooltip: {
                    callbacks: {
                        label: ctx => ` ${ctx.parsed.y} pólizas`
                    }
                }
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

/* ─────────────────────────────────────────────
   GRÁFICA 3 — Bar horizontal: por aseguradora
   ───────────────────────────────────────────── */
if (aseguradoraLabels.length > 0) {
    new Chart(document.getElementById('chartAseguradora'), {
        type: 'bar',
        data: {
            labels: aseguradoraLabels,
            datasets: [{
                label: 'Pólizas',
                data: aseguradoraValores,
                backgroundColor: PALETA.blue,
                borderRadius: 6,
                borderSkipped: false,
                maxBarThickness: 36,
            }]
        },
        options: {
            indexAxis: 'y',           // ← horizontal
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                tooltip: {
                    callbacks: {
                        label: ctx => ` ${ctx.parsed.x} pólizas`
                    }
                }
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