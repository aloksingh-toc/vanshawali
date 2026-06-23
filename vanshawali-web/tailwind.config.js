/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        paper: "#F5EEDD",
        "paper-deep": "#EDE3CC",
        box: "#FCF8EE",
        cream: "#F2E9D4",
        ink: "#26314F",
        "ink-soft": "rgba(38,49,79,0.62)",
        "ink-faint": "rgba(38,49,79,0.40)",
        indigo: {
          DEFAULT: "#232E52",
          deep: "#1A2340",
          dark: "#141B30",
        },
        sindoor: {
          DEFAULT: "#B3402A",
          soft: "#C85A42",
        },
        haldi: {
          DEFAULT: "#A4731B",
          bg: "#F8EFD8",
        },
        leaf: "#3B6D2E",
      },
      fontFamily: {
        sans: ["'Noto Sans Devanagari'", "'Nirmala UI'", "sans-serif"],
        serif: ["'Spectral'", "'Tiro Devanagari Hindi'", "serif"],
        display: ["'Tiro Devanagari Hindi'", "'Noto Sans Devanagari'", "serif"],
      },
      boxShadow: {
        manuscript: "0 1px 0 rgba(38,49,79,0.04), 0 8px 24px -12px rgba(38,49,79,0.22)",
        panel: "0 -6px 28px -10px rgba(38,49,79,0.35)",
      },
      borderRadius: {
        card: "14px",
      },
      keyframes: {
        ticker: {
          "0%": { transform: "translateX(0)" },
          "100%": { transform: "translateX(-50%)" },
        },
        pulseDot: {
          "0%,100%": { opacity: "1", transform: "scale(1)" },
          "50%": { opacity: "0.45", transform: "scale(0.8)" },
        },
        floatSlow: {
          "0%,100%": { transform: "translateY(0)" },
          "50%": { transform: "translateY(-6px)" },
        },
        riseIn: {
          "0%": { opacity: "0", transform: "translateY(10px)" },
          "100%": { opacity: "1", transform: "translateY(0)" },
        },
      },
      animation: {
        ticker: "ticker 32s linear infinite",
        pulseDot: "pulseDot 2s ease-in-out infinite",
        floatSlow: "floatSlow 7s ease-in-out infinite",
        riseIn: "riseIn 0.5s ease-out both",
      },
    },
  },
  plugins: [],
};
