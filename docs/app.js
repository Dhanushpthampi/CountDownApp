/**
 * CountDown Landing Page — Interactive Simulation & Logic
 * Replicates the exact calendar-accurate calculation from the Android app!
 */

const PRESETS = {
  trip: {
    title: "Summer Vacation to Tokyo",
    category: "TRAVEL",
    badgeColor: "rgba(56, 189, 248, 0.15)",
    badgeBorder: "rgba(56, 189, 248, 0.35)",
    badgeTextColor: "#38BDF8",
    targetOffsetDays: 132, // approx 4 months 12 days ahead
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

let currentPresetKey = 'trip';
let activeEvent = null;

function initPresets() {
  const now = new Date();
  for (const [key, preset] of Object.entries(PRESETS)) {
    if (!preset.targetDate) {
      preset.targetDate = new Date(now.getTime() + preset.targetOffsetDays * 86400000);
      preset.startDate = new Date(now.getTime() + preset.startOffsetDays * 86400000);
    }
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

function updateUI() {
  const event = PRESETS[currentPresetKey];
  if (!event) return;

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
    const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    dateEl.textContent = `Target: ${event.targetDate.toLocaleDateString(undefined, options)}`;
  }

  if (badgeEl) {
    badgeEl.textContent = event.category;
    badgeEl.style.backgroundColor = event.badgeColor;
    badgeEl.style.borderColor = event.badgeBorder;
    badgeEl.style.color = event.badgeTextColor;
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
  const buttons = document.querySelectorAll('.preset-chip');
  buttons.forEach(btn => {
    btn.addEventListener('click', () => {
      buttons.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      currentPresetKey = btn.dataset.event;
      updateUI();
    });
  });
}

// Navbar scroll blur effect
function setupScrollEffects() {
  const navbar = document.getElementById('navbar');
  window.addEventListener('scroll', () => {
    if (window.scrollY > 40) {
      navbar.style.background = 'rgba(9, 10, 15, 0.9)';
      navbar.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.5)';
    } else {
      navbar.style.background = 'rgba(9, 10, 15, 0.7)';
      navbar.style.boxShadow = 'none';
    }
  });
}

// Initialize on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  initPresets();
  setupPresets();
  setupScrollEffects();
  updateUI();

  // 1-second live ticker
  setInterval(updateUI, 1000);
});
