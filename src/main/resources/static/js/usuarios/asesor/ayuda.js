function toggleCard(header) {
            const body = header.nextElementSibling;
            const chevron = header.querySelector('.module-card-chevron');
            const isOpen = body.classList.contains('open');

            body.classList.toggle('open', !isOpen);
            chevron.classList.toggle('open', !isOpen);
        }

        // Smooth scroll for quick nav
        document.querySelectorAll('.qnav-card').forEach(card => {
            card.addEventListener('click', function(e) {
                const href = this.getAttribute('href');
                if (href && href.startsWith('#')) {
                    e.preventDefault();
                    const target = document.querySelector(href);
                    if (target) {
                        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
                    }
                }
            });
        });

        // Auto-open first card in each section
//        document.querySelectorAll('.module-card').forEach((card, i) => {
//            if (i === 0 || i === 4 || i === 10) {
//                const header = card.querySelector('.module-card-header');
//                const body = card.querySelector('.module-card-body');
//                const chevron = card.querySelector('.module-card-chevron');
//                if (body && chevron) {
//                    body.classList.add('open');
//                    chevron.classList.add('open');
//                }
//            }
//        });