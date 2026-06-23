import { Routes, Route, useLocation } from "react-router-dom";
import { useEffect, useRef } from "react";
import VillageBackground from "./theme/VillageBackground";
import Header from "./components/Header";
import BottomNav from "./components/BottomNav";
import Home from "./pages/Home";
import More from "./pages/More";
import TreePage from "./pages/TreePage";
import AdminLogin from "./pages/AdminLogin";
import AdminRequestInbox from "./pages/AdminRequestInbox";
import GalleryView from "./pages/GalleryView";
import AdminGalleryInbox from "./pages/AdminGalleryInbox";
import RelationFinder from "./pages/RelationFinder";
import AdminHistoricalNotes from "./pages/AdminHistoricalNotes";
import EventsCalendar from "./pages/EventsCalendar";
import AdminEvents from "./pages/AdminEvents";
import AnnouncementsBoard from "./pages/AnnouncementsBoard";
import AdminAnnouncements from "./pages/AdminAnnouncements";
import FundLedger from "./pages/FundLedger";
import AdminFund from "./pages/AdminFund";
import AdminUsers from "./pages/AdminUsers";
import RequireAdmin from "./components/RequireAdmin";
import { useLang } from "./i18n";

export default function App() {
  const { t } = useLang();
  const location = useLocation();
  const scrollRef = useRef<HTMLElement>(null);

  const isCanvas = location.pathname === "/tree";

  // reset scroll to top on route change (native-app feel)
  useEffect(() => {
    scrollRef.current?.scrollTo({ top: 0 });
  }, [location.pathname]);

  return (
    <>
      <VillageBackground />
      <div className="relative mx-auto flex h-svh w-full max-w-[440px] flex-col overflow-hidden bg-paper/30 shadow-[0_0_40px_-8px_rgba(20,27,48,0.45)] ring-1 ring-ink/10 sm:my-0">
        <Header />
        <main
          ref={scrollRef}
          className="no-scrollbar flex flex-1 flex-col overflow-x-hidden overflow-y-auto"
        >
          <div className="flex min-h-0 flex-1 flex-col">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/tree" element={<TreePage />} />
            <Route path="/gallery" element={<GalleryView />} />
            <Route
              path="/admin/gallery"
              element={
                <RequireAdmin>
                  <AdminGalleryInbox />
                </RequireAdmin>
              }
            />
            <Route path="/events" element={<EventsCalendar />} />
            <Route
              path="/admin/events"
              element={
                <RequireAdmin>
                  <AdminEvents />
                </RequireAdmin>
              }
            />
            <Route path="/announcements" element={<AnnouncementsBoard />} />
            <Route
              path="/admin/announcements"
              element={
                <RequireAdmin>
                  <AdminAnnouncements />
                </RequireAdmin>
              }
            />
            <Route path="/fund" element={<FundLedger />} />
            <Route
              path="/admin/fund"
              element={
                <RequireAdmin message="यह पृष्ठ केवल Admin/कोषाध्यक्ष के लिए है।">
                  <AdminFund />
                </RequireAdmin>
              }
            />
            <Route
              path="/admin/users"
              element={
                <RequireAdmin>
                  <AdminUsers />
                </RequireAdmin>
              }
            />
            <Route path="/relation" element={<RelationFinder />} />
            <Route path="/more" element={<More />} />
            <Route path="/admin/login" element={<AdminLogin />} />
            <Route
              path="/admin/requests"
              element={
                <RequireAdmin>
                  <AdminRequestInbox />
                </RequireAdmin>
              }
            />
            <Route
              path="/admin/historical-notes"
              element={
                <RequireAdmin>
                  <AdminHistoricalNotes />
                </RequireAdmin>
              }
            />
            <Route path="*" element={<Home />} />
          </Routes>
          </div>
          {!isCanvas && (
            <footer className="px-4 pb-6 pt-2 text-center">
              <div className="gilt-divider mx-auto mb-2" />
              <p className="text-[11px] text-ink-soft">{t("footer")}</p>
            </footer>
          )}
        </main>
        <BottomNav />
      </div>
    </>
  );
}
