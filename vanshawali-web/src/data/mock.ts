/**
 * Placeholder data shaped like the future API responses.
 * Replaced by live calls to the Spring Boot backend in a later phase.
 */

export type TodayKind = "BIRTHDAY" | "BARSI";
export interface TodayEntry {
  id: number;
  name: string;
  relation: string;
  kind: TodayKind;
  initial: string;
}

export interface UpcomingEvent {
  id: number;
  title: string;
  meta: string;
  date: string; // ISO
  type: "WEDDING" | "FESTIVAL" | "PUJA" | "REUNION" | "VILLAGE_PROJECT";
}

export interface FundCause {
  id: number;
  title: string;
  sub: string;
  raised: number;
  goal: number;
  accent: "indigo" | "sindoor";
}

export interface FeedItem {
  id: number;
  text: string;
  meta: string;
  tone: "green" | "blue" | "amber" | "red";
  initial: string;
  fresh?: boolean;
}

export interface GalleryShot {
  id: number;
  title: string;
  likes: number;
  tone: string;
}

export const today: TodayEntry[] = [
  { id: 1, name: "रोहन", relation: "विजय जी के पुत्र · पीढ़ी ९", kind: "BIRTHDAY", initial: "रो" },
  { id: 2, name: "मोहन शर्मा", relation: "गोपाल शर्मा के पुत्र · पीढ़ी ७", kind: "BARSI", initial: "मो" },
  { id: 3, name: "अनिल", relation: "केशव शाखा · पीढ़ी ८", kind: "BIRTHDAY", initial: "अ" },
];

export const events: UpcomingEvent[] = [
  { id: 1, title: "विश्वजीत की शादी", meta: "बारात — ग्राम इब्राहिमाबाद", date: "2026-06-22", type: "WEDDING" },
  { id: 2, title: "हरिद्वार परिवार मिलन", meta: "सभी शाखाएँ आमंत्रित", date: "2026-07-05", type: "REUNION" },
  { id: 3, title: "गाँव की सड़क मरम्मत", meta: "चंदा जारी है", date: "2026-08-15", type: "VILLAGE_PROJECT" },
  { id: 4, title: "गणेश पूजा — मंदिर", meta: "ग्राम इब्राहिमाबाद", date: "2026-09-03", type: "PUJA" },
];

export const funds: FundCause[] = [
  {
    id: 1,
    title: "गाँव की सड़क मरम्मत",
    sub: "मुख्य सड़क · चौराहे तक · कोषाध्यक्ष: प्रकाश जी",
    raised: 32000,
    goal: 50000,
    accent: "indigo",
  },
  {
    id: 2,
    title: "मंदिर जीर्णोद्धार",
    sub: "ग्राम मंदिर · पुनर्निर्माण · कोषाध्यक्ष: पंडित जी",
    raised: 18500,
    goal: 50000,
    accent: "sindoor",
  },
];

export const feed: FeedItem[] = [
  {
    id: 1,
    text: "यश को Bangalore में Software Engineer की नौकरी मिली — बधाई!",
    meta: "2 घंटे पहले · दिनेश जी के पुत्र",
    tone: "green",
    initial: "य",
    fresh: true,
  },
  {
    id: 2,
    text: "राजेश ने सड़क मरम्मत चंदे में ₹5,000 का योगदान दिया",
    meta: "5 घंटे पहले",
    tone: "blue",
    initial: "रा",
  },
  {
    id: 3,
    text: "हर्ष ने NIT प्रयागराज से B.Tech पास किया — स्वर्ण पदक",
    meta: "कल · विनोद जी के पुत्र",
    tone: "amber",
    initial: "ह",
  },
  {
    id: 4,
    text: "तनय के घर नवजात का आगमन — नामकरण शीघ्र",
    meta: "3 दिन पहले · चंद्रशेखर शाखा",
    tone: "red",
    initial: "त",
  },
];

export const gallery: GalleryShot[] = [
  { id: 1, title: "होली 2024", likes: 12, tone: "#E8E0CC" },
  { id: 2, title: "शादी समारोह", likes: 8, tone: "#D8D4C4" },
  { id: 3, title: "मंदिर परिसर", likes: 5, tone: "#E0DCC8" },
  { id: 4, title: "दिवाली 2024", likes: 15, tone: "#D4D0BC" },
  { id: 5, title: "परिवार मिलन", likes: 21, tone: "#DED7C2" },
];

export const tickerItems: { text: string; dot: string }[] = [
  { text: "यश को सॉफ्टवेयर इंजीनियर की नौकरी — बधाई!", dot: "#3B6D2E" },
  { text: "चंदा: राजेश ने ₹5,000 दिए — सड़क मरम्मत", dot: "#185FA5" },
  { text: "नई फ़ोटो: होली 2024 गैलरी में जोड़ी गई", dot: "#B3402A" },
  { text: "विश्वजीत की शादी — 22 जून · बारात इब्राहिमाबाद से", dot: "#A4731B" },
  { text: "हर्ष ने B.Tech पास किया — NIT प्रयागराज", dot: "#3B6D2E" },
  { text: "वंशवृक्ष अपडेट: 3 नए नाम जोड़े गए", dot: "#185FA5" },
];

export const stats = { members: 299, generations: 9, branches: 4 };
