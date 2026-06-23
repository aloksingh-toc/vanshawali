import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";

type Lang = "hi" | "en";

const dict = {
  hi: {
    placeOfVillage: "ग्राम इब्राहिमाबाद · जिला बलिया",
    invocation: "॥ श्री गणेशाय नमः ॥",
    searchPlaceholder: "अपना नाम खोजें… (जैसे: रोहन, विकास)",
    searchShort: "नाम खोजें…",
    search: "खोजें",
    nav_home: "मुख्य",
    nav_more: "अधिक",
    more_title: "अधिक विकल्प",
    nav_tree: "वंशवृक्ष",
    nav_gallery: "गाँव की तस्वीरें",
    nav_events: "कार्यक्रम",
    nav_announce: "उपलब्धियाँ",
    nav_fund: "चंदा",
    nav_relation: "रिश्ता खोजें",
    today: "आज का दिन",
    birthday: "जन्मदिन",
    barsi: "बरसी",
    upcoming: "आगामी कार्यक्रम",
    seeAll: "सभी देखें",
    daysLeft: "दिन बाद",
    todayLabel: "आज",
    publicFund: "सार्वजनिक चंदा",
    fullLedger: "पूरा खाता",
    goal: "लक्ष्य",
    contribute: "+ योगदान करें",
    recentPhotos: "ताज़ी तस्वीरें",
    gallery: "गैलरी",
    upload: "अपलोड करें",
    recentActivity: "हाल की गतिविधि",
    all: "सभी",
    new: "नया",
    members: "परिजन",
    generations: "पीढ़ियाँ",
    branches: "शाखाएँ",
    q_tree: "वंशवृक्ष",
    q_tree_sub: "299 सदस्य",
    q_relation: "रिश्ता खोजें",
    q_relation_sub: "कोई दो नाम",
    q_change: "बदलाव भेजें",
    q_change_sub: "Admin समीक्षा करेंगे",
    footer: "हमारी वंश-परम्परा · ९ पीढ़ियाँ · २९९ परिजन",
    tree_expand: "सब खोलें",
    tree_collapse: "समेटें",
    tree_fit: "फिट",
    tree_print: "प्रिंट / PDF",
    legend_line: "मुख्य वंश-रेखा",
    legend_issueless: "लावल्द",
    legend_unconfirmed: "नाम अपुष्ट",
    legend_hidden: "छिपे वंशज",
    panel_requestChange: "बदलाव अनुरोध",
    panel_findRelation: "रिश्ता खोजें",
    gen: "पीढ़ी",
    flag_line: "वंश-रेखा",
    flag_issueless: "लावल्द (निःसंतान)",
    flag_unconfirmed: "नाम अपुष्ट",
    flag_pending: "पुष्टि बाकी",
    searchResults: "परिणाम",
    noResults: "कोई परिणाम नहीं",
    loading: "वंश-वृक्ष लोड हो रहा है…",
    retry: "फिर कोशिश करें",
  },
  en: {
    placeOfVillage: "Village Ibrahimabad · District Ballia",
    invocation: "॥ Shri Ganeshaya Namah ॥",
    searchPlaceholder: "Search your name… (e.g. Rohan, Vikas)",
    searchShort: "Search name…",
    search: "Search",
    nav_home: "Home",
    nav_more: "More",
    more_title: "More options",
    nav_tree: "Family Tree",
    nav_gallery: "Village Photos",
    nav_events: "Events",
    nav_announce: "Achievements",
    nav_fund: "Fund",
    nav_relation: "Find Relation",
    today: "Today",
    birthday: "Birthday",
    barsi: "Barsi",
    upcoming: "Upcoming Events",
    seeAll: "See all",
    daysLeft: "days left",
    todayLabel: "Today",
    publicFund: "Public Fund",
    fullLedger: "Full ledger",
    goal: "Goal",
    contribute: "+ Contribute",
    recentPhotos: "Recent Photos",
    gallery: "Gallery",
    upload: "Upload",
    recentActivity: "Recent Activity",
    all: "All",
    new: "New",
    members: "Members",
    generations: "Generations",
    branches: "Branches",
    q_tree: "Family Tree",
    q_tree_sub: "299 members",
    q_relation: "Find Relation",
    q_relation_sub: "Any two names",
    q_change: "Suggest Change",
    q_change_sub: "Admin reviews",
    footer: "Our family lineage · 9 generations · 299 kin",
    tree_expand: "Expand all",
    tree_collapse: "Collapse",
    tree_fit: "Fit",
    tree_print: "Print / PDF",
    legend_line: "Main bloodline",
    legend_issueless: "Issueless",
    legend_unconfirmed: "Unconfirmed",
    legend_hidden: "Hidden kin",
    panel_requestChange: "Suggest change",
    panel_findRelation: "Find relation",
    gen: "Generation",
    flag_line: "Direct line",
    flag_issueless: "Issueless",
    flag_unconfirmed: "Unconfirmed name",
    flag_pending: "Pending confirmation",
    searchResults: "results",
    noResults: "No results",
    loading: "Loading family tree…",
    retry: "Retry",
  },
} as const;

type Key = keyof (typeof dict)["hi"];

interface Ctx {
  lang: Lang;
  toggle: () => void;
  t: (k: Key) => string;
}

const LanguageContext = createContext<Ctx | null>(null);

export function LanguageProvider({ children }: { children: ReactNode }) {
  const [lang, setLang] = useState<Lang>(
    () => (localStorage.getItem("vanshawali-lang") as Lang) || "hi"
  );

  useEffect(() => {
    localStorage.setItem("vanshawali-lang", lang);
  }, [lang]);

  const value: Ctx = {
    lang,
    toggle: () => setLang((l) => (l === "hi" ? "en" : "hi")),
    t: (k) => dict[lang][k],
  };

  return (
    <LanguageContext.Provider value={value}>{children}</LanguageContext.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components
export function useLang() {
  const ctx = useContext(LanguageContext);
  if (!ctx) throw new Error("useLang must be used within LanguageProvider");
  return ctx;
}
