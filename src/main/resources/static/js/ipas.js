/* Landing page - efectos de navegación, animaciones y contadores */

document.addEventListener('DOMContentLoaded', function () {

    // Cambia el estilo del navbar cuando se hace scroll
    const navbar = document.querySelector('.lp-navbar');
    if (navbar) {
        window.addEventListener('scroll', () => {
            navbar.classList.toggle('scrolled', window.scrollY > 40);
        });
    }

    // Revela elementos con animación cuando aparecen en pantalla
    const reveals = document.querySelectorAll('.lp-reveal');
    if (reveals.length) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.12 });

        reveals.forEach(el => observer.observe(el));
    }

    // Anima números en las estadísticas
    function animateCounter(el, target, duration = 1800) {
        let start = 0;
        const isDecimal = String(target).includes('.');
        const increment = target / (duration / 16);

        const step = () => {
            start += increment;
            if (start >= target) {
                el.textContent = isDecimal
                    ? target.toFixed(1)
                    : Math.floor(target).toLocaleString();
                return;
            }
            el.textContent = isDecimal
                ? start.toFixed(1)
                : Math.floor(start).toLocaleString();
            requestAnimationFrame(step);
        };
        requestAnimationFrame(step);
    }

    const statNums = document.querySelectorAll('[data-count]');
    if (statNums.length) {
        const statsObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const el = entry.target;
                    const target = parseFloat(el.dataset.count);
                    animateCounter(el, target);
                    statsObserver.unobserve(el);
                }
            });
        }, { threshold: 0.5 });

        statNums.forEach(el => statsObserver.observe(el));
    }

    // Scroll suave para los enlaces internos de la página
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                e.preventDefault();
                target.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        });
    });

});