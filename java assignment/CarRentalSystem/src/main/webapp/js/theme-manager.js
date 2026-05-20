/**
 * CarRental — Theme Manager v3.0
 * Luxury Dark/Light Theme Engine
 * 
 * Persistence: localStorage('theme' + 'carrental_theme_pref')
 * Fallback: system preference via matchMedia
 * Sync: optional server POST via window.CONTEXT_PATH
 */
(function() {
    'use strict';

    var STORAGE_KEYS = ['theme', 'carrental_theme_pref'];

    // ── Resolve theme ──
    function getTheme() {
        try {
            for (var i = 0; i < STORAGE_KEYS.length; i++) {
                var stored = localStorage.getItem(STORAGE_KEYS[i]);
                if (stored === 'dark' || stored === 'light') return stored;
            }
        } catch (e) {}
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) return 'dark';
        return 'light';
    }

    // ── Apply theme to DOM ──
    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        if (document.body) {
            document.body.setAttribute('data-theme', theme);
        }
        try {
            for (var i = 0; i < STORAGE_KEYS.length; i++) {
                localStorage.setItem(STORAGE_KEYS[i], theme);
            }
        } catch (e) {}
        updateToggleIcon(theme);
        updateMetaThemeColor(theme);
    }

    // ── Update toggle button icon ──
    function updateToggleIcon(theme) {
        var btn = document.getElementById('themeToggle');
        if (!btn) return;
        var icon = btn.querySelector('i');
        if (!icon) return;
        if (theme === 'dark') {
            icon.className = 'fas fa-sun';
            btn.title = 'Switch to Light Mode';
        } else {
            icon.className = 'fas fa-moon';
            btn.title = 'Switch to Dark Mode';
        }
    }

    // ── Update meta theme-color for mobile browsers ──
    function updateMetaThemeColor(theme) {
        var meta = document.querySelector('meta[name="theme-color"]');
        if (meta) {
            meta.setAttribute('content', theme === 'dark' ? '#0b1120' : '#f8fafc');
        }
    }

    // ── Apply immediately (before DOMContentLoaded) ──
    var currentTheme = getTheme();
    applyTheme(currentTheme);

    // Expose a small API for settings screens
    window.themeManager = {
        applyTheme: applyTheme,
        getTheme: getTheme
    };

    // ── After DOM ready: bind toggle button ──
    function onReady() {
        if (document.body) {
            document.body.setAttribute('data-theme', getTheme());
        }
        updateToggleIcon(getTheme());

        var btn = document.getElementById('themeToggle');
        if (btn) {
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                var next = document.documentElement.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
                applyTheme(next);

                // Smooth body transition
                document.body.style.transition = 'background-color .35s ease, color .35s ease';

                // Optional: sync to server session
                if (window.CONTEXT_PATH) {
                    try {
                        var xhr = new XMLHttpRequest();
                        xhr.open('POST', window.CONTEXT_PATH + '/settings', true);
                        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                        xhr.send('action=updateTheme&theme=' + encodeURIComponent(next));
                    } catch(err) { /* silent fail */ }
                }
            });
        }

        // Listen for system theme changes
        if (window.matchMedia) {
            window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', function(e) {
                var hasStored = false;
                for (var i = 0; i < STORAGE_KEYS.length; i++) {
                    if (localStorage.getItem(STORAGE_KEYS[i])) {
                        hasStored = true;
                        break;
                    }
                }
                if (!hasStored) {
                    applyTheme(e.matches ? 'dark' : 'light');
                }
            });
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', onReady);
    } else {
        onReady();
    }
})();
