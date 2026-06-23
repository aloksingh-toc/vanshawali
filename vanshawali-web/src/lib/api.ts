import type { TreeNode } from "../types/tree";

export const API_BASE_URL =
  (import.meta.env.VITE_API_BASE_URL as string | undefined) ?? "http://localhost:8080";

export function apiUrl(path: string) {
  return `${API_BASE_URL}${path}`;
}

async function getJson<T>(path: string, errorMessage: string): Promise<T> {
  const res = await fetch(apiUrl(path));
  if (!res.ok) throw new Error(`${errorMessage} (${res.status})`);
  return res.json();
}

async function postJson<T>(
  path: string,
  input: unknown,
  fallbackErrorMessage: string
): Promise<T> {
  const res = await fetch(apiUrl(path), {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input),
  });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message ?? `${fallbackErrorMessage} (${res.status})`);
  }
  return res.json();
}

interface PersonNodeDto {
  id: number;
  name: string;
  aliasNote: string | null;
  directLine: boolean;
  issueless: boolean;
  unconfirmed: boolean;
  pending: boolean;
  photoUrl: string | null;
  children: PersonNodeDto[];
}

function toTreeNode(dto: PersonNodeDto): TreeNode {
  const node: TreeNode = { id: dto.id, n: dto.name };
  if (dto.directLine) node.hl = 1;
  if (dto.issueless) node.x = 1;
  if (dto.unconfirmed) node.q = 1;
  if (dto.pending) node.u = 1;
  if (dto.aliasNote) node.note = dto.aliasNote;
  if (dto.photoUrl) node.photoUrl = dto.photoUrl;
  if (dto.children.length > 0) node.k = dto.children.map(toTreeNode);
  return node;
}

export async function fetchTree(): Promise<TreeNode> {
  const dto = await getJson<PersonNodeDto>("/api/tree", "वंश-वृक्ष लोड नहीं हो सका");
  return toTreeNode(dto);
}

export interface PersonDetail {
  id: number;
  name: string;
  aliasNote: string | null;
  directLine: boolean;
  issueless: boolean;
  unconfirmed: boolean;
  pending: boolean;
  birthDate: string | null;
  deathDate: string | null;
  dateIsApproximate: boolean;
  photoUrl: string | null;
  generation: number;
  parentId: number | null;
  parentName: string | null;
}

export interface PersonWriteInput {
  parentId: number;
  name: string;
  aliasNote?: string | null;
  directLine: boolean;
  issueless: boolean;
  unconfirmed: boolean;
  pending: boolean;
  birthDate?: string | null;
  deathDate?: string | null;
  dateIsApproximate: boolean;
  photoUrl?: string | null;
}

export async function fetchPerson(id: number): Promise<PersonDetail> {
  return getJson(`/api/persons/${id}`, "व्यक्ति लोड नहीं हो सका");
}

export type RequestType =
  | "RENAME"
  | "ADD_CHILD"
  | "MARK_ISSUELESS"
  | "CONFIRM_NAME"
  | "ADD_NOTE"
  | "DELETE"
  | "OTHER";

export interface ChangeRequestSubmitInput {
  targetPersonId?: number | null;
  requestType: RequestType;
  proposedData?: Record<string, unknown>;
  requesterName: string;
  requesterContact?: string | null;
}

export interface ChangeRequestDetail {
  id: number;
  targetPersonId: number | null;
  targetPersonName: string | null;
  requestType: RequestType;
  proposedData: Record<string, unknown> | null;
  requesterName: string;
  requesterContact: string | null;
  status: "PENDING" | "APPROVED" | "REJECTED";
  adminNotes: string | null;
  reviewedAt: string | null;
  createdAt: string;
}

export async function submitChangeRequest(
  input: ChangeRequestSubmitInput
): Promise<ChangeRequestDetail> {
  return postJson("/api/requests", input, "निवेदन नहीं भेजा जा सका");
}

export interface GalleryPostDetail {
  id: number;
  photoUrl: string;
  caption: string | null;
  uploaderName: string;
  uploaderContact: string | null;
  status: "PENDING" | "APPROVED" | "REJECTED";
  adminNotes: string | null;
  reviewedAt: string | null;
  createdAt: string;
  approvedCommentCount: number;
}

export interface GalleryCommentDetail {
  id: number;
  postId: number;
  commenterName: string;
  body: string;
  status: "PENDING" | "APPROVED" | "REJECTED";
  adminNotes: string | null;
  createdAt: string;
}

export interface GalleryPostSubmitInput {
  photoUrl: string;
  caption?: string | null;
  uploaderName: string;
  uploaderContact?: string | null;
}

export interface GalleryCommentSubmitInput {
  commenterName: string;
  body: string;
}

export async function fetchApprovedGallery(): Promise<GalleryPostDetail[]> {
  return getJson("/api/gallery", "गैलरी लोड नहीं हो सकी");
}

export async function submitGalleryPost(
  input: GalleryPostSubmitInput
): Promise<GalleryPostDetail> {
  return postJson("/api/gallery", input, "फ़ोटो नहीं भेजी जा सकी");
}

export async function fetchApprovedComments(postId: number): Promise<GalleryCommentDetail[]> {
  return getJson(`/api/gallery/${postId}/comments`, "टिप्पणियाँ लोड नहीं हो सकीं");
}

export async function submitGalleryComment(
  postId: number,
  input: GalleryCommentSubmitInput
): Promise<GalleryCommentDetail> {
  return postJson(`/api/gallery/${postId}/comments`, input, "टिप्पणी नहीं भेजी जा सकी");
}

export interface PersonSearchResult {
  id: number;
  name: string;
  aliasNote: string | null;
  directLine: boolean;
  generation: number;
  parentId: number | null;
  parentName: string | null;
}

export async function searchPersons(q: string): Promise<PersonSearchResult[]> {
  if (!q.trim()) return [];
  return getJson(`/api/search?q=${encodeURIComponent(q.trim())}`, "खोज विफल");
}

export interface RelationResult {
  fromId: number;
  fromName: string;
  toId: number;
  toName: string;
  commonAncestorId: number;
  commonAncestorName: string;
  generationGap: number;
  depthFrom: number;
  depthTo: number;
  relationLabel: string;
  pathFrom: string[];
  pathTo: string[];
}

export async function fetchRelation(fromId: number, toId: number): Promise<RelationResult> {
  const res = await fetch(apiUrl(`/api/relation?from=${fromId}&to=${toId}`));
  if (!res.ok) {
    if (res.status === 404) throw new Error("व्यक्ति नहीं मिला");
    throw new Error(`रिश्ता नहीं निकाला जा सका (${res.status})`);
  }
  return res.json();
}

export type OnThisDayType = "BIRTHDAY" | "DEATH_ANNIVERSARY" | "HISTORICAL_NOTE";

export interface OnThisDayItem {
  type: OnThisDayType;
  title: string;
  description: string | null;
  personId: number | null;
  personName: string | null;
  photoUrl: string | null;
  yearsAgo: number | null;
}

export async function fetchOnThisDay(): Promise<OnThisDayItem[]> {
  return getJson("/api/on-this-day", "आज का इतिहास लोड नहीं हो सका");
}

export interface AnniversaryEntry {
  personId: number;
  personName: string;
  type: "BIRTHDAY" | "DEATH_ANNIVERSARY";
  originalDate: string;
  dateIsApproximate: boolean;
  daysUntil: number;
  yearsAtOccurrence: number | null;
}

export async function fetchUpcomingAnniversaries(days = 30): Promise<AnniversaryEntry[]> {
  return getJson(`/api/anniversaries/upcoming?days=${days}`, "आगामी तिथियाँ लोड नहीं हो सकीं");
}

export interface HistoricalNoteDetail {
  id: number;
  noteMonth: number;
  noteDay: number;
  title: string;
  description: string | null;
  relatedPersonId: number | null;
  relatedPersonName: string | null;
  photoUrl: string | null;
  createdAt: string;
}

export interface HistoricalNoteWriteInput {
  noteMonth: number;
  noteDay: number;
  title: string;
  description?: string | null;
  relatedPersonId?: number | null;
  photoUrl?: string | null;
}

export type EventType =
  | "WEDDING"
  | "FESTIVAL"
  | "PUJA"
  | "REUNION"
  | "VILLAGE_PROJECT"
  | "OTHER";

export interface CommunityEventDetail {
  id: number;
  title: string;
  description: string | null;
  eventDate: string;
  eventType: EventType;
  location: string | null;
  createdAt: string;
}

export interface CommunityEventWriteInput {
  title: string;
  description?: string | null;
  eventDate: string;
  eventType: EventType;
  location?: string | null;
}

export async function fetchEvents(): Promise<CommunityEventDetail[]> {
  return getJson("/api/events", "कार्यक्रम लोड नहीं हो सके");
}

export type AnnouncementType = "JOB" | "DEGREE" | "MARRIAGE" | "BIRTH" | "OTHER";

export interface AnnouncementDetail {
  id: number;
  personId: number | null;
  personName: string | null;
  title: string;
  description: string | null;
  announcementType: AnnouncementType;
  submitterName: string;
  submitterContact: string | null;
  status: "PENDING" | "APPROVED" | "REJECTED";
  adminNotes: string | null;
  reviewedAt: string | null;
  createdAt: string;
}

export interface AnnouncementSubmitInput {
  personId?: number | null;
  title: string;
  description?: string | null;
  announcementType: AnnouncementType;
  submitterName: string;
  submitterContact?: string | null;
}

export async function fetchApprovedAnnouncements(): Promise<AnnouncementDetail[]> {
  return getJson("/api/announcements", "उपलब्धियाँ लोड नहीं हो सकीं");
}

export async function submitAnnouncement(
  input: AnnouncementSubmitInput
): Promise<AnnouncementDetail> {
  return postJson("/api/announcements", input, "भेजा नहीं जा सका");
}

export type FundMode = "CASH" | "UPI" | "BANK_TRANSFER" | "CHEQUE" | "OTHER";
export type FundEntryType = "CONTRIBUTION" | "EXPENSE";

export interface FundEntryDetail {
  id: number;
  name: string;
  amount: number;
  entryDate: string;
  mode: FundMode;
  note: string | null;
  entryType: FundEntryType;
  relatedEventId: number | null;
  relatedEventTitle: string | null;
  receiptUrl: string | null;
  createdAt: string;
}

export interface FundEntryWriteInput {
  name: string;
  amount: number;
  entryDate: string;
  mode: FundMode;
  note?: string | null;
  entryType: FundEntryType;
  relatedEventId?: number | null;
  receiptUrl?: string | null;
}

export interface FundEventBreakdown {
  eventId: number;
  eventTitle: string;
  contributions: number;
  expenses: number;
  balance: number;
}

export interface FundSummary {
  totalContributions: number;
  totalExpenses: number;
  balance: number;
  byEvent: FundEventBreakdown[];
}

export async function fetchFundSummary(): Promise<FundSummary> {
  return getJson("/api/fund/summary", "चंदा सार लोड नहीं हो सका");
}

export type AppUserRole = "ADMIN" | "FUND_MANAGER";

export interface AppUserDetail {
  id: number;
  username: string;
  displayName: string | null;
  role: AppUserRole;
  createdAt: string;
}

export interface AppUserCreateInput {
  username: string;
  password: string;
  displayName?: string | null;
  role: AppUserRole;
}
