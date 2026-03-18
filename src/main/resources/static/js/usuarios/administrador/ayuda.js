function toggleCard(header) {
    const body = header.nextElementSibling;
    const chevron = header.querySelector('.module-card-chevron');
    const isOpen = body.classList.contains('open');
    body.classList.toggle('open', !isOpen);
    chevron.classList.toggle('open', !isOpen);
}

// Smooth scroll para quick nav
document.querySelectorAll('.qnav-card').forEach(card => {
    card.addEventListener('click', function(e) {
        const href = this.getAttribute('href');
        if (href && href.startsWith('#')) {
            e.preventDefault();
            const target = document.querySelector(href);
            if (target) target.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    });
});