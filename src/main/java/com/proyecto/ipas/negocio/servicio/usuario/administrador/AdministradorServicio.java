package com.proyecto.ipas.negocio.servicio.usuario.administrador;

import com.proyecto.ipas.datos.repositorio.ClienteRepositorio;
import com.proyecto.ipas.datos.repositorio.PolizaRepositorio;
import com.proyecto.ipas.datos.repositorio.UsuarioRepositorio;
import com.proyecto.ipas.negocio.dominio.enums.EstadoUsuario;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.asesor.ActividadRecienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.asesor.AsesorRankingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdministradorServicio {

    PolizaRepositorio  polizaRepositorio;
    UsuarioRepositorio usuarioRepositorio;
    ClienteRepositorio clienteRepositorio;


    private final static Logger registro = LoggerFactory.getLogger(AdministradorServicio.class);

    public AdministradorServicio(PolizaRepositorio polizaRepositorio, UsuarioRepositorio usuarioRepositorio, ClienteRepositorio clienteRepositorio) {
        this.polizaRepositorio = polizaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.clienteRepositorio = clienteRepositorio;
    }

    /**
     * Obtiene los datos estadísticos para el dashboard del administrador.
     * Incluye información sobre usuarios activos, pólizas, clientes y actividad reciente.
     *
     * @return un mapa con las claves:
     *         - "usuariosActivos": cantidad de usuarios con estado activo
     *         - "totalPolizasActivas": cantidad total de pólizas activas
     *         - "totalClientes": cantidad total de clientes registrados
     *         - "actualizacionesHoy": cantidad de actualizaciones en el día actual
     *         - "actualizacionesMes": cantidad de actualizaciones en el mes actual
     *         - "asesoresConActividad": cantidad de asesores con actividad en los últimos 7 días
     */
    public Map<String, Long> obtenerDatosDashboard() {

        registro.info("Obteniendo datos estadisticos para el dashboard del administrador");

        Map<String, Long> datosDashboard = new LinkedHashMap<>();

        datosDashboard.put("usuariosActivos", usuarioRepositorio.countByEstado(EstadoUsuario.ACTIVO));
        datosDashboard.put("totalPolizasActivas", polizaRepositorio.contarPolizasActivas());
        datosDashboard.put("totalClientes", clienteRepositorio.count());
        datosDashboard.put("actualizacionesHoy", usuarioRepositorio.contarActualizacionesHoy());
        datosDashboard.put("actualizacionesMes", usuarioRepositorio.contarActualizacionesMes());

        LocalDate limite = LocalDate.now().minusDays(7);
        datosDashboard.put("asesoresConActividad", usuarioRepositorio.contarAsesoresActividadUltimosDias(limite));

        return datosDashboard;
    }

    /**
     * Obtiene el ranking de los 5 mejores asesores ordenados por cantidad de pólizas.
     * Calcula el porcentaje de cada asesor en relación al que tiene más pólizas.
     *
     * @return lista de AsesorRankingDTO con la información del asesor y su porcentaje
     *         ordenada de mayor a menor cantidad de pólizas. La lista estará vacía
     *         si no hay asesores registrados.
     */
    public List<AsesorRankingDTO> obtenerRankingAsesores() {

        List<AsesorRankingDTO> lista = polizaRepositorio.encontrarTopAsesores(PageRequest.of(0, 5));

        if (lista.isEmpty()) return lista;

        // Calculamos el porcentaje basado en el que más tiene (el primero de la lista)
        Long maximo = lista.get(0).getTotalPolizas();

        lista.forEach(item -> {
            double calculo = (item.getTotalPolizas().doubleValue() / maximo) * 100;
            item.setPorcentaje(Math.round(calculo * 10.0) / 10.0); // Redondeo a 1 decimal
        });

        return lista;
    }

    /**
     * Obtiene la lista de actividades recientes de los usuarios del sistema.
     *
     * @return lista de ActividadRecienteDTO con la información más reciente de las acciones realizadas por los usuarios
     */
    public List<ActividadRecienteDTO> obtenerActividadReciente() {
        return usuarioRepositorio.encontrarUltimaActividad();
    }
}
