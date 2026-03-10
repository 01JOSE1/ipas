package com.proyecto.ipas.infraestructura.configuracion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
/**
 * Configuración centralizada de seguridad para la aplicación Spring Boot.
 * 
 * Define:
 * - Cadena de filtros de seguridad con autorización por roles
 * - Codificación de contraseñas con BCrypt
 * - Autenticación con DAO personalizado
 * - Manejo de login/logout
 * - Manejo de excepciones de seguridad (401, 403)
 * - Soporte para "Remember Me"
 * 
 * Configuración de rutas:
 * - Públicas: /, /usuarios/login, /usuarios/registro, recursos estáticos
 * - ADMINISTRADOR: /administrador/**
 * - ASESOR: /asesor/**
 * - Ambos roles: /usuarios/**
 */
public class SeguridadConfiguracion {

    @Value("${security.rememberme.key}")
    private String rememberMeKey;


    private UserDetailsService userDetailsService;
    private ManejadorPuntoEntradaAutenticacion manejadorPuntoEntradaAutenticacion;
    private ManejadorAccesoDenegado manejadorAccesoDenegado;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;
    private LogoutSuccessHandler logoutSuccessHandler;


    /**
     * Constructor que inyecta todos los componentes necesarios para la seguridad.
     * 
     * @param userDetailsService el servicio para cargar detalles del usuario
     * @param manejadorAccesoDenegado el manejador para errores 403
     * @param manejadorPuntoEntradaAutenticacion el manejador para errores 401
     * @param authenticationSuccessHandler el manejador de logins exitosos
     * @param authenticationFailureHandler el manejador de logins fallidos
     * @param logoutSuccessHandler el manejador de logouts exitosos
     */
    public SeguridadConfiguracion(UserDetailsService userDetailsService, ManejadorAccesoDenegado manejadorAccesoDenegado, ManejadorPuntoEntradaAutenticacion manejadorPuntoEntradaAutenticacion, AuthenticationSuccessHandler authenticationSuccessHandler, AuthenticationFailureHandler authenticationFailureHandler, LogoutSuccessHandler logoutSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.manejadorPuntoEntradaAutenticacion = manejadorPuntoEntradaAutenticacion;
        this.manejadorAccesoDenegado = manejadorAccesoDenegado;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;

    }

    /**
     * Bean que define el codificador de contraseñas usando BCrypt.
     * 
     * BCrypt es un algoritmo de hash adaptativo que es resistente a ataques de fuerza bruta.
     * 
     * @return un PasswordEncoder configurado con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean que configura el proveedor de autenticación DAO.
     * 
     * Utiliza UserDetailsService para cargar usuarios y PasswordEncoder para validar contraseñas.
     * 
     * @return un DaoAuthenticationProvider configurado
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean que obtiene el AuthenticationManager de la configuración de Spring.
     * 
     * @param authConfig la configuración de autenticación
     * @return el AuthenticationManager para procesar autenticaciones
     * @throws Exception si hay error en la configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Bean que define la cadena de filtros de seguridad HTTP.
     * 
     * Configura:
     * - Autorización por roles para diferentes rutas
     * - Form login personalizado
     * - Logout con invalidación de sesión
     * - "Remember Me" con tokenización segura
     * - Manejo de excepciones de autenticación y autorización
     * - Permisos para iframes de mismo origen
     * 
     * @param http el constructor de seguridad HTTP
     * @return la cadena de filtros de seguridad configurada
     * @throws Exception si hay error en la configuración
     */
    @Bean
    public SecurityFilterChain cadenaFiltrosSeguridad (HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz


                        // RUTAS PUBLICAS
                        .requestMatchers("/", "/usuarios/login", "/usuarios/registro", "/usuarios/error-403", "/css/**", "/js/**", "/webjars/**", "/images/**", "/ai/test/**").permitAll()

                        // RUTAS PARA UN ROL ESPECIFICO
                        .requestMatchers("/administrador/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/asesor/**").hasRole("ASESOR")
                        .requestMatchers("/usuarios/**").hasAnyRole("ASESOR", "ADMINISTRADOR")

                        // DENEGAR TODAS LAS OTRAS RUTAS NO ESTABLECIDAS
                        .anyRequest().denyAll()
                )

                .formLogin(form -> form
                        // URL DE LA PAGINA HTML PERSONALIZADA
                        .loginPage("/usuarios/loginRegistroFormulario")

                        // URL DONDE SPRING PROCESA EL LOGIN
                        .loginProcessingUrl("/usuarios/login")

                        // SI EL LOGIN FUE EXITOSO REDIRIGE A ESA RUTA
                        .successHandler(authenticationSuccessHandler)

                        // SI EL LOGIN FALLA REDIRIGE A ESA RUTA
                        .failureHandler(authenticationFailureHandler)

                        // PERMITIR ACCESO A LA PAGINA SIN AUTENTICACION
                        .permitAll()
                )

                .logout(logout -> logout
                        // URL PARA CERRAR SESION DEL USUARIO
                        .logoutUrl("/logout")

                        // REDIRIGIR DESPUES DE CERRAR SESION
                        .logoutSuccessHandler(logoutSuccessHandler)

                        // INVALIDAR LA SESION ELIMINANDO CUALQUIER ATRIBUTO DE LA SESION
                        .invalidateHttpSession(true)

                        .clearAuthentication(true)

                        // ELIMINAR LAS COOKIES
                        .deleteCookies("JSESSIONID")

                        .permitAll()
                )

                // PERMITE MANTENER LA SESION AUNQUE EL NAVEGADOR SE CIERRE
                .rememberMe(recuerdame -> recuerdame

                        // CLAVE SECRETA PARA FIRMAR LA COOKIE Y EVITAR QUE ALGUIEN MODIFIQUE O SE AUTENTIQUE
                        .key(rememberMeKey)

                        // TIEMPO PARA MANTENER LA SESION
                        .tokenValiditySeconds(86400)

                        // SERVICIO QUE SPRING USA PARA VOLVER A CARGAR EL USUARIO DESDE LA COOKIE
                        .userDetailsService(userDetailsService)
                )


                // CUANDO EL USUARIO NO TIENE PERMISO LO REDIRIGE A LA URL
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(manejadorPuntoEntradaAutenticacion)
                        .accessDeniedHandler(manejadorAccesoDenegado)  // Página cuando no tiene permisos
                )

                // Para permitir iframes
                .headers(headers -> headers
                    .frameOptions(frameOptions -> frameOptions.sameOrigin()) // ESTA ES LA CLAVE
                );

        return http.build();
    }





}
