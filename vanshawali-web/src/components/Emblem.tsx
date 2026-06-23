/** A small round village emblem — temple + peepal motif in indigo & gold. */
export default function Emblem({ size = 40 }: { size?: number }) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 64 64"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
      className="shrink-0"
    >
      <circle cx="32" cy="32" r="31" fill="#1A2340" stroke="#C99A3A" strokeWidth="2" />
      <circle cx="32" cy="32" r="26" fill="none" stroke="#C99A3A" strokeWidth="0.75" opacity="0.5" />
      {/* peepal tree (left) */}
      <path
        d="M19 40 q-7 0 -8 -6 q-5 -1 -3 -6 q-3 -4 2 -6 q1 -6 8 -5 q3 -4 7 0 q6 -1 6 6 q4 2 0 6 q1 5 -5 6 q-6 2 -8 -1 Z"
        fill="#3C6B36"
        opacity="0.9"
      />
      <rect x="22" y="38" width="2.5" height="12" fill="#5A4A2A" />
      {/* temple (right) */}
      <path d="M38 50 L38 34 Q46 20 54 34 L54 50 Z" fill="#E9C268" />
      <path d="M41 50 L41 36 Q46 26 51 36 L51 50 Z" fill="#C99A3A" />
      <circle cx="46" cy="20" r="2.6" fill="#B3402A" />
      <rect x="44.7" y="13" width="2.6" height="7" rx="1" fill="#B3402A" />
      {/* ground */}
      <rect x="12" y="49" width="40" height="5" rx="2" fill="#C99A3A" opacity="0.85" />
    </svg>
  );
}
