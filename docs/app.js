/**
 * CountDown Landing Page — Interactive Simulation & Dynamic Features
 * Fully responsive logic, calendar-accurate breakdown, custom builder, and mobile UI controls.
 */

const PRESETS = {
  trip: {
    title: "Summer Vacation to Tokyo",
    category: "TRAVEL",
    badgeColor: "rgba(56, 189, 248, 0.15)",
    badgeBorder: "rgba(56, 189, 248, 0.35)",
    badgeTextColor: "#38BDF8",
    targetOffsetDays: 132,
    startOffsetDays: -90
  },
  newyear: {
    title: "New Year's Eve 2027",
    category: "CELEBRATION",
    badgeColor: "rgba(251, 191, 36, 0.15)",
    badgeBorder: "rgba(251, 191, 36, 0.35)",
    badgeTextColor: "#FBBF24",
    targetDate: new Date(2027, 0, 1, 0, 0, 0),
    startDate: new Date(2026, 0, 1, 0, 0, 0)
  },
  launch: {
    title: "Dream Project Launch",
    category: "MILESTONE",
    badgeColor: "rgba(167, 139, 250, 0.15)",
    badgeBorder: "rgba(167, 139, 250, 0.35)",
    badgeTextColor: "#A78BFA",
    targetOffsetDays: 45,
    startOffsetDays: -60
  },
  birthday: {
    title: "25th Birthday Milestone",
    category: "GOAL",
    badgeColor: "rgba(52, 211, 153, 0.15)",
    badgeBorder: "rgba(52, 211, 153, 0.35)",
    badgeTextColor: "#34D399",
    targetOffsetDays: 215,
    startOffsetDays: -150
  }
};

const CATEGORY_COLORS = {
  TRAVEL: { bg: "rgba(56, 189, 248, 0.15)", border: "rgba(56, 189, 248, 0.35)", text: "#38BDF8" },
  MILESTONE: { bg: "rgba(167, 139, 250, 0.15)", border: "rgba(167, 139, 250, 0.35)", text: "#A78BFA" },
  CELEBRATION: { bg: "rgba(251, 191, 36, 0.15)", border: "rgba(251, 191, 36, 0.35)", text: "#FBBF24" },
  CAREER: { bg: "rgba(99, 102, 241, 0.15)", border: "rgba(99, 102, 241, 0.35)", text: "#818CF8" },
  GOAL: { bg: "rgba(52, 211, 153, 0.15)", border: "rgba(52, 211, 153, 0.35)", text: "#34D399" },
  CREATIVE: { bg: "rgba(244, 114, 182, 0.15)", border: "rgba(244, 114, 182, 0.35)", text: "#F472B6" },
  ASTRONOMY: { bg: "rgba(192, 132, 252, 0.15)", border: "rgba(192, 132, 252, 0.35)", text: "#C084FC" }
};

let currentPresetKey = 'trip';
let customEventState = null;

function initPresets() {
  const now = new Date();
  for (const [key, preset] of Object.entries(PRESETS)) {
    if (!preset.targetDate) {
      preset.targetDate = new Date(now.getTime() + preset.targetOffsetDays * 86400000);
      preset.startDate = new Date(now.getTime() + preset.startOffsetDays * 86400000);
    }
  }

  // Set default custom date picker value (1 month ahead)
  const customDateInput = document.getElementById('custom-date-input');
  if (customDateInput) {
    const nextMonth = new Date(now.getTime() + 30 * 86400000);
    // Format YYYY-MM-DDTHH:MM
    const isoStr = new Date(nextMonth.getTime() - nextMonth.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
    customDateInput.value = isoStr;
  }
}

/**
 * Calculates calendar-accurate breakdown matching CountdownItem.kt
 */
function calculateCalendarBreakdown(targetDate, startDate, currentDate = new Date()) {
  const currentMillis = currentDate.getTime();
  const targetMillis = targetDate.getTime();
  const diffMillis = targetMillis - currentMillis;

  if (diffMillis <= 0) {
    return {
      isFinished: true,
      years: 0,
      months: 0,
      days: 0,
      hours: 0,
      minutes: 0,
      seconds: 0,
      readable: "Event Arrived!",
      progressPct: 100
    };
  }

  // 1. Calendar iteration for years & months
  let tempCal = new Date(currentDate);
  let calYears = 0;
  let calMonths = 0;

  // Count whole years
  while (true) {
    let nextYear = new Date(tempCal);
    nextYear.setFullYear(nextYear.getFullYear() + 1);
    if (nextYear.getTime() <= targetMillis) {
      calYears++;
      tempCal = nextYear;
    } else {
      break;
    }
  }

  // Count whole months
  while (true) {
    let nextMonth = new Date(tempCal);
    nextMonth.setMonth(nextMonth.getMonth() + 1);
    if (nextMonth.getTime() <= targetMillis) {
      calMonths++;
      tempCal = nextMonth;
    } else {
      break;
    }
  }

  // Remaining days, hours, mins, secs
  const leftoverMillis = targetMillis - tempCal.getTime();
  const calRemDays = Math.floor(leftoverMillis / 86400000);
  const remAfterDays = leftoverMillis % 86400000;
  const hours = Math.floor(remAfterDays / 3600000);
  const minutes = Math.floor((remAfterDays % 3600000) / 60000);
  const seconds = Math.floor((remAfterDays % 60000) / 1000);

  // Build human readable phrase
  let readable = "";
  if (calYears > 0) {
    const yStr = `${calYears} year${calYears > 1 ? 's' : ''}`;
    if (calMonths > 0) {
      readable = `${yStr} ${calMonths} month${calMonths > 1 ? 's' : ''} remaining`;
    } else {
      readable = `${yStr} remaining`;
    }
  } else if (calMonths > 0) {
    const mStr = `${calMonths} month${calMonths > 1 ? 's' : ''}`;
    if (calRemDays > 0) {
      readable = `${mStr} ${calRemDays} day${calRemDays > 1 ? 's' : ''} remaining`;
    } else {
      readable = `${mStr} remaining`;
    }
  } else if (calRemDays > 0) {
    readable = `${calRemDays} day${calRemDays > 1 ? 's' : ''} remaining`;
  } else if (hours > 0) {
    readable = `${hours} hour${hours > 1 ? 's' : ''} remaining`;
  } else {
    readable = `${minutes} min${minutes > 1 ? 's' : ''} remaining`;
  }

  // Progress percentage
  let progressPct = 0;
  if (startDate) {
    const totalSpan = targetMillis - startDate.getTime();
    if (totalSpan > 0) {
      const elapsed = currentMillis - startDate.getTime();
      progressPct = Math.min(100, Math.max(0, Math.round((elapsed / totalSpan) * 100)));
    }
  }

  return {
    isFinished: false,
    years: calYears,
    months: calMonths,
    days: calRemDays,
    hours,
    minutes,
    seconds,
    readable,
    progressPct
  };
}

function getActiveEvent() {
  if (currentPresetKey === 'custom' && customEventState) {
    return customEventState;
  }
  return PRESETS[currentPresetKey] || PRESETS.trip;
}

function updateUI() {
  const event = getActiveEvent();
  if (!event || !event.targetDate) return;

  const result = calculateCalendarBreakdown(event.targetDate, event.startDate);

  // Update elements
  const titleEl = document.getElementById('demo-title');
  const dateEl = document.getElementById('demo-date');
  const badgeEl = document.getElementById('demo-category-badge');
  const readableEl = document.getElementById('demo-readable-value');
  const yearsEl = document.getElementById('demo-years');
  const monthsEl = document.getElementById('demo-months');
  const daysEl = document.getElementById('demo-days');
  const hoursEl = document.getElementById('demo-hours');
  const minutesEl = document.getElementById('demo-minutes');
  const secondsEl = document.getElementById('demo-seconds');
  const progressPctEl = document.getElementById('demo-progress-pct');
  const progressBarEl = document.getElementById('demo-progress-bar');

  if (titleEl) titleEl.textContent = event.title;
  if (dateEl) {
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    dateEl.textContent = `Target: ${event.targetDate.toLocaleDateString(undefined, options)}`;
  }

  if (badgeEl) {
    badgeEl.textContent = event.category || 'MILESTONE';
    badgeEl.style.backgroundColor = event.badgeColor || 'rgba(167, 139, 250, 0.15)';
    badgeEl.style.borderColor = event.badgeBorder || 'rgba(167, 139, 250, 0.35)';
    badgeEl.style.color = event.badgeTextColor || '#A78BFA';
  }

  if (readableEl) readableEl.textContent = result.readable;
  if (yearsEl) yearsEl.textContent = result.years;
  if (monthsEl) monthsEl.textContent = result.months;
  if (daysEl) daysEl.textContent = result.days;
  if (hoursEl) hoursEl.textContent = String(result.hours).padStart(2, '0');
  if (minutesEl) minutesEl.textContent = String(result.minutes).padStart(2, '0');
  if (secondsEl) secondsEl.textContent = String(result.seconds).padStart(2, '0');

  if (progressPctEl) progressPctEl.textContent = `${result.progressPct}% completed`;
  if (progressBarEl) progressBarEl.style.width = `${result.progressPct}%`;
}

// Preset button handlers
function setupPresets() {
  const buttons = document.querySelectorAll('.preset-chip[data-event]');
  buttons.forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.preset-chip').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      currentPresetKey = btn.dataset.event;

      // Hide custom panel if switching to standard preset
      const customPanel = document.getElementById('custom-event-panel');
      if (customPanel) customPanel.style.display = 'none';

      updateUI();
    });
  });

  // Custom Event Toggle
  const customToggleBtn = document.getElementById('custom-event-toggle');
  const customPanel = document.getElementById('custom-event-panel');
  const closeCustomBtn = document.getElementById('btn-close-custom');

  if (customToggleBtn && customPanel) {
    customToggleBtn.addEventListener('click', () => {
      document.querySelectorAll('.preset-chip').forEach(b => b.classList.remove('active'));
      customToggleBtn.classList.add('active');
      customPanel.style.display = customPanel.style.display === 'none' ? 'block' : 'none';
      currentPresetKey = 'custom';
      updateCustomFromInputs();
      updateUI();
    });
  }

  if (closeCustomBtn && customPanel) {
    closeCustomBtn.addEventListener('click', () => {
      customPanel.style.display = 'none';
      // Revert to trip
      const tripBtn = document.getElementById('preset-trip');
      if (tripBtn) {
        document.querySelectorAll('.preset-chip').forEach(b => b.classList.remove('active'));
        tripBtn.classList.add('active');
        currentPresetKey = 'trip';
        updateUI();
      }
    });
  }

  // Inputs live binding
  const titleInput = document.getElementById('custom-title-input');
  const dateInput = document.getElementById('custom-date-input');
  const catSelect = document.getElementById('custom-category-select');

  [titleInput, dateInput, catSelect].forEach(input => {
    if (input) {
      input.addEventListener('input', () => {
        if (currentPresetKey === 'custom') {
          updateCustomFromInputs();
          updateUI();
        }
      });
    }
  });
}

function updateCustomFromInputs() {
  const titleInput = document.getElementById('custom-title-input');
  const dateInput = document.getElementById('custom-date-input');
  const catSelect = document.getElementById('custom-category-select');

  const title = (titleInput && titleInput.value.trim()) || "My Custom Event";
  let targetDate = new Date();
  if (dateInput && dateInput.value) {
    targetDate = new Date(dateInput.value);
  } else {
    targetDate = new Date(Date.now() + 30 * 86400000);
  }

  const category = (catSelect && catSelect.value) || "MILESTONE";
  const colors = CATEGORY_COLORS[category] || CATEGORY_COLORS.MILESTONE;

  customEventState = {
    title,
    category,
    badgeColor: colors.bg,
    badgeBorder: colors.border,
    badgeTextColor: colors.text,
    targetDate,
    startDate: new Date(Date.now() - 15 * 86400000)
  };
}

// Mobile Hamburger Drawer
function setupMobileMenu() {
  const toggleBtn = document.getElementById('nav-toggle');
  const drawer = document.getElementById('mobile-drawer');
  const backdrop = document.getElementById('drawer-backdrop');
  const drawerLinks = document.querySelectorAll('.drawer-link');

  function toggleMenu(open) {
    const isOpen = open !== undefined ? open : !drawer.classList.contains('open');
    if (isOpen) {
      drawer.classList.add('open');
      toggleBtn.classList.add('open');
      toggleBtn.setAttribute('aria-expanded', 'true');
      backdrop.classList.add('active');
      document.body.style.overflow = 'hidden';
    } else {
      drawer.classList.remove('open');
      toggleBtn.classList.remove('open');
      toggleBtn.setAttribute('aria-expanded', 'false');
      backdrop.classList.remove('active');
      document.body.style.overflow = '';
    }
  }

  if (toggleBtn && drawer && backdrop) {
    toggleBtn.addEventListener('click', () => toggleMenu());
    backdrop.addEventListener('click', () => toggleMenu(false));

    drawerLinks.forEach(link => {
      link.addEventListener('click', () => toggleMenu(false));
    });

    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && drawer.classList.contains('open')) {
        toggleMenu(false);
      }
    });
  }
}

// Mobile Screenshot Tabs
function setupScreenshotTabs() {
  const tabs = document.querySelectorAll('.shot-tab');
  const cards = [
    document.getElementById('shot-card-0'),
    document.getElementById('shot-card-1'),
    document.getElementById('shot-card-2')
  ];

  function setActiveShot(index) {
    tabs.forEach((tab, i) => {
      if (i === index) {
        tab.classList.add('active');
      } else {
        tab.classList.remove('active');
      }
    });

    cards.forEach((card, i) => {
      if (card) {
        if (i === index) {
          card.classList.add('active-shot');
        } else {
          card.classList.remove('active-shot');
        }
      }
    });
  }

  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      const idx = parseInt(tab.dataset.shot, 10);
      setActiveShot(idx);
    });
  });

  // Default to screenshot 1 (All styles) on mobile
  setActiveShot(1);
}

// Floating Mobile Bar & Scroll Effects
function setupScrollEffects() {
  const navbar = document.getElementById('navbar');
  const floatingBar = document.getElementById('floating-mobile-bar');
  const heroSection = document.getElementById('hero');

  window.addEventListener('scroll', () => {
    const scrollY = window.scrollY;

    // Navbar blur
    if (navbar) {
      if (scrollY > 40) {
        navbar.style.background = 'rgba(9, 10, 15, 0.92)';
        navbar.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.5)';
      } else {
        navbar.style.background = 'rgba(9, 10, 15, 0.75)';
        navbar.style.boxShadow = 'none';
      }
    }

    // Floating download bar (shows after scrolling past hero, hides near bottom)
    if (floatingBar && heroSection) {
      const heroBottom = heroSection.offsetTop + heroSection.offsetHeight;
      const isPastHero = scrollY > heroBottom - 200;
      const isNearBottom = (window.innerHeight + scrollY) >= document.body.offsetHeight - 250;

      if (isPastHero && !isNearBottom) {
        floatingBar.classList.add('visible');
      } else {
        floatingBar.classList.remove('visible');
      }
    }
  });
}

// Initialize on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  initPresets();
  setupPresets();
  setupMobileMenu();
  setupScreenshotTabs();
  setupScrollEffects();
  updateUI();

  // 1-second live ticker
  setInterval(updateUI, 1000);
});
