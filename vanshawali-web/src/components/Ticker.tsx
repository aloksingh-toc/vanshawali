import { tickerItems } from "../data/mock";

export default function Ticker() {
  const loop = [...tickerItems, ...tickerItems];
  return (
    // position:relative + overflow:hidden keeps the absolute child contained
    // and removes the w-max element from document flow so it cannot widen the page
    <div className="relative h-[30px] overflow-hidden border-t border-cream/10 bg-indigo-deep/95">
      <div className="absolute inset-y-0 left-0 flex w-max animate-ticker items-center hover:[animation-play-state:paused]">
        {loop.map((item, i) => (
          <span
            key={i}
            className="inline-flex items-center gap-2 whitespace-nowrap px-6 text-[12px] text-cream/70"
          >
            <span
              className="h-1.5 w-1.5 shrink-0 rounded-full"
              style={{ background: item.dot }}
            />
            {item.text}
          </span>
        ))}
      </div>
    </div>
  );
}
