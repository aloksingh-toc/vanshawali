export interface TreeNode {
  id: number;
  n: string;
  hl?: 1; // मुख्य वंश-रेखा (direct line)
  x?: 1; // लावल्द (issueless)
  q?: 1; // नाम अपुष्ट (unconfirmed)
  u?: 1; // पुष्टि बाकी (pending)
  note?: string;
  photoUrl?: string;
  k?: TreeNode[];
}
