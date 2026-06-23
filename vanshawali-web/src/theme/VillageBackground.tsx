/**
 * A fixed, full-viewport illustration of an ancient Indian village.
 * Self-contained SVG (no external image) so it scales crisply, themes with
 * the manuscript palette, and never fails to load. It sits behind all content.
 */
export default function VillageBackground() {
  return (
    <div
      aria-hidden="true"
      className="pointer-events-none fixed inset-0 -z-10 overflow-hidden"
    >
      <svg
        className="h-full w-full"
        viewBox="0 0 1440 900"
        preserveAspectRatio="xMidYMax slice"
        xmlns="http://www.w3.org/2000/svg"
      >
        <defs>
          <linearGradient id="sky" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="#F3E7C9" />
            <stop offset="42%" stopColor="#F1E6CB" />
            <stop offset="78%" stopColor="#EAD9B6" />
            <stop offset="100%" stopColor="#E3CFA6" />
          </linearGradient>

          <radialGradient id="sun" cx="50%" cy="50%" r="50%">
            <stop offset="0%" stopColor="#E9C268" stopOpacity="0.85" />
            <stop offset="45%" stopColor="#DFB456" stopOpacity="0.45" />
            <stop offset="100%" stopColor="#DFB456" stopOpacity="0" />
          </radialGradient>

          <linearGradient id="hillFar" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="#9FA9B0" />
            <stop offset="100%" stopColor="#B9BBAE" />
          </linearGradient>

          <linearGradient id="groundGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="#C9B488" />
            <stop offset="100%" stopColor="#BBA274" />
          </linearGradient>

          <radialGradient id="vignette" cx="50%" cy="42%" r="75%">
            <stop offset="60%" stopColor="#000000" stopOpacity="0" />
            <stop offset="100%" stopColor="#3A2E16" stopOpacity="0.22" />
          </radialGradient>

          <filter id="grain">
            <feTurbulence
              type="fractalNoise"
              baseFrequency="0.9"
              numOctaves="2"
              stitchTiles="stitch"
            />
            <feColorMatrix type="saturate" values="0" />
            <feComponentTransfer>
              <feFuncA type="linear" slope="0.05" />
            </feComponentTransfer>
          </filter>
        </defs>

        {/* sky */}
        <rect width="1440" height="900" fill="url(#sky)" />

        {/* sun glow */}
        <circle cx="1060" cy="250" r="360" fill="url(#sun)" />
        <circle cx="1060" cy="250" r="92" fill="#E4BB5E" opacity="0.55" />

        {/* drifting birds */}
        <g
          stroke="#5A5040"
          strokeWidth="3"
          fill="none"
          strokeLinecap="round"
          opacity="0.5"
        >
          <path d="M250 150 q14 -12 28 0 q14 -12 28 0" />
          <path d="M330 188 q11 -9 22 0 q11 -9 22 0" />
          <path d="M196 210 q11 -9 22 0 q11 -9 22 0" />
        </g>

        {/* far hills */}
        <path
          d="M0 470 Q220 380 470 452 Q700 520 980 440 Q1200 380 1440 458 L1440 900 L0 900 Z"
          fill="url(#hillFar)"
          opacity="0.55"
        />
        <path
          d="M0 540 Q260 470 540 528 Q820 586 1080 512 Q1280 456 1440 528 L1440 900 L0 900 Z"
          fill="#8F9487"
          opacity="0.5"
        />

        {/* ground */}
        <path
          d="M0 612 Q360 566 720 600 Q1080 634 1440 588 L1440 900 L0 900 Z"
          fill="url(#groundGrad)"
        />

        {/* village silhouette */}
        <g fill="#2A3253" opacity="0.92">
          {/* peepal tree (left) */}
          <g>
            <rect x="150" y="556" width="13" height="86" fill="#3A3526" />
            <path
              d="M156 470 q-78 6 -88 64 q-52 8 -44 62 q-46 26 6 56 q24 40 84 26 q40 34 92 6 q56 18 78 -28 q50 -16 24 -64 q28 -48 -30 -66 q-18 -56 -86 -54 q-30 -22 -60 -2 Z"
              fill="#33502E"
              opacity="0.9"
            />
          </g>

          {/* temple with shikhara (center) */}
          <g>
            <rect x="636" y="486" width="150" height="120" rx="3" />
            {/* spire */}
            <path d="M662 486 Q711 360 760 486 Z" />
            <path d="M676 486 Q711 396 746 486 Z" fill="#1F2747" />
            {/* kalash finial */}
            <circle cx="711" cy="356" r="9" fill="#B3402A" />
            <rect x="708" y="332" width="6" height="20" fill="#B3402A" />
            {/* doorway */}
            <path d="M695 606 L695 548 Q711 524 727 548 L727 606 Z" fill="#F1E6CB" opacity="0.5" />
            {/* flag */}
            <rect x="711" y="318" width="2.5" height="20" fill="#8A2E1C" />
            <path d="M713 320 l26 7 l-26 9 Z" fill="#B3402A" />
          </g>

          {/* huts (right cluster) */}
          <g>
            <rect x="864" y="556" width="96" height="58" rx="2" />
            <path d="M852 556 L912 510 L972 556 Z" fill="#4A3D24" />
            <rect x="900" y="586" width="24" height="28" fill="#F1E6CB" opacity="0.45" />
          </g>
          <g>
            <rect x="980" y="572" width="76" height="42" rx="2" />
            <path d="M970 572 L1018 540 L1066 572 Z" fill="#4A3D24" />
          </g>

          {/* huts (left of temple) */}
          <g>
            <rect x="470" y="566" width="84" height="48" rx="2" />
            <path d="M458 566 L512 528 L566 566 Z" fill="#4A3D24" />
            <rect x="500" y="588" width="22" height="26" fill="#F1E6CB" opacity="0.45" />
          </g>

          {/* the village well */}
          <g>
            <ellipse cx="330" cy="640" rx="34" ry="11" fill="#1F2747" />
            <rect x="298" y="600" width="64" height="40" rx="3" />
            <rect x="300" y="566" width="6" height="40" fill="#3A3526" />
            <rect x="354" y="566" width="6" height="40" fill="#3A3526" />
            <rect x="296" y="560" width="68" height="9" rx="3" fill="#4A3D24" />
          </g>

          {/* small bushes */}
          <ellipse cx="1140" cy="606" rx="46" ry="22" fill="#33502E" opacity="0.85" />
          <ellipse cx="600" cy="618" rx="34" ry="16" fill="#33502E" opacity="0.8" />

          {/* neem tree (right — mirrors peepal weight) */}
          <g>
            <rect x="1308" y="558" width="12" height="84" fill="#3A3526" />
            <ellipse cx="1314" cy="502" rx="68" ry="60" fill="#2D4828" opacity="0.80" />
            <ellipse cx="1284" cy="528" rx="48" ry="40" fill="#33502E" opacity="0.74" />
            <ellipse cx="1346" cy="522" rx="44" ry="36" fill="#2D4828" opacity="0.72" />
            <ellipse cx="1314" cy="474" rx="40" ry="34" fill="#3A5634" opacity="0.78" />
          </g>
        </g>

        {/* foreground field furrows */}
        <g stroke="#9C865C" strokeWidth="2.5" opacity="0.4" fill="none">
          <path d="M-20 720 Q720 690 1460 726" />
          <path d="M-20 770 Q720 740 1460 778" />
          <path d="M-20 824 Q720 792 1460 832" />
        </g>

        {/* paper grain + vignette wash */}
        <rect width="1440" height="900" filter="url(#grain)" opacity="0.5" />
        <rect width="1440" height="900" fill="url(#vignette)" />
        {/* parchment warm wash so content panels read clearly on top */}
        <rect width="1440" height="900" fill="#F4EAD2" opacity="0.34" />
      </svg>
    </div>
  );
}
