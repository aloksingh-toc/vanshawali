# Heirloom Android App — Setup Status

**Package:** `com.meragaw.vanshawali`
**Project path:** `D:\Projects\MeraGaw\heirloom\`
**Design source:** `Family tree application design3.zip`

---

## ✅ Completed (by Claude)

### Project Scaffold
- [x] `settings.gradle` — project name = Heirloom, includes `:app`
- [x] `build.gradle` (root) — AGP 8.2.2
- [x] `gradle.properties` — AndroidX enabled, Jetifier enabled
- [x] `gradle/wrapper/gradle-wrapper.properties` — Gradle 8.4
- [x] `.gitignore`
- [x] `app/proguard-rules.pro`

### App Module
- [x] `app/build.gradle` — compileSdk 34, minSdk 24, targetSdk 34, viewBinding enabled, all dependencies included
- [x] `app/src/main/AndroidManifest.xml` — package set, `Theme.Heirloom` applied, `MainActivity` registered as launcher

### Resource Values
- [x] `res/values/colors.xml` — full Heirloom color palette (30+ tokens: primary, backgrounds, borders, avatars, semantic, tab bar)
- [x] `res/values/strings.xml` — all UI copy (home, tree, profile, photos, search, notifications, tabs, accessibility labels)
- [x] `res/values/dimens.xml` — spacing scale, corner radii, avatar sizes, typography sizes
- [x] `res/values/themes.xml` — `Theme.Heirloom` + all widget styles (buttons, cards, FAB, chips, search field, bottom nav)
- [x] `res/values/selectors.xml` — color state selector templates

### Color Selectors
- [x] `res/color/tab_selector.xml`
- [x] `res/color/chip_background_selector.xml`
- [x] `res/color/chip_text_selector.xml`

### Layouts (11 files)
- [x] `res/layout/activity_main.xml`
- [x] `res/layout/fragment_home.xml`
- [x] `res/layout/fragment_family_tree.xml`
- [x] `res/layout/fragment_member_profile.xml`
- [x] `res/layout/fragment_photos.xml`
- [x] `res/layout/fragment_search.xml`
- [x] `res/layout/fragment_notifications.xml`
- [x] `res/layout/item_member_card.xml`
- [x] `res/layout/item_member_row.xml`
- [x] `res/layout/item_notification.xml`
- [x] `res/layout/item_photo_grid.xml`

### Navigation & Menu
- [x] `res/menu/bottom_nav_menu.xml`
- [x] `res/navigation/nav_graph.xml`

### Java Source Files (12 files — all repackaged to `com.meragaw.vanshawali`)
- [x] `ui/MainActivity.java`
- [x] `ui/home/HomeFragment.java`
- [x] `ui/tree/FamilyTreeFragment.java`
- [x] `ui/profile/MemberProfileFragment.java`
- [x] `ui/photos/PhotosFragment.java`
- [x] `ui/photos/PhotoGridAdapter.java`
- [x] `ui/search/SearchFragment.java`
- [x] `ui/notifications/NotificationsFragment.java`
- [x] `ui/adapters/MemberRowAdapter.java`
- [x] `ui/adapters/NotificationAdapter.java`
- [x] `model/FamilyMember.java`
- [x] `model/FamilyNotification.java`

### Empty directories ready for manual assets
- [x] `res/font/` ← awaiting font files
- [x] `res/drawable/` ← awaiting icon files
- [x] `res/mipmap-hdpi/`, `mdpi/`, `xhdpi/`, `xxhdpi/`, `xxxhdpi/` ← awaiting launcher icons

---

## 🔧 To Do (Manual — do in Android Studio)

### 1. Fonts — download from [fonts.google.com](https://fonts.google.com)
Place all files in `app/src/main/res/font/`

| File name | Font | Weight |
|---|---|---|
| `lora_semibold.ttf` | Lora | SemiBold 600 |
| `lora_medium_italic.ttf` | Lora | Medium Italic 500 |
| `mulish_regular.ttf` | Mulish | Regular 400 |
| `mulish_semibold.ttf` | Mulish | SemiBold 600 |
| `mulish_bold.ttf` | Mulish | Bold 700 |
| `mulish_extrabold.ttf` | Mulish | ExtraBold 800 |

### 2. Icons — download from [fonts.google.com/icons](https://fonts.google.com/icons)
In Android Studio: `File > New > Vector Asset` → select each icon → save as listed below in `res/drawable/`

| File name | Material Icon name | Used in |
|---|---|---|
| `ic_bell.xml` | Notifications | Home top bar |
| `ic_gift.xml` | Card Giftcard | Birthday card |
| `ic_heart.xml` | Favorite | Spouse connector in tree |
| `ic_search.xml` | Search | Search field, tab |
| `ic_add.xml` | Add | FAB, recently added card |
| `ic_remove.xml` | Remove | Zoom out control |
| `ic_arrow_back.xml` | Arrow Back | Profile / Notifications back button |
| `ic_chevron_right.xml` | Chevron Right | Search result rows, profile list |
| `ic_filter.xml` | Filter List | Search filter |
| `ic_memory.xml` | Auto Awesome | Add memory button |
| `ic_tab_home.xml` | Home | Bottom nav tab |
| `ic_tab_tree.xml` | Account Tree | Bottom nav tab |
| `ic_tab_photos.xml` | Photo Library | Bottom nav tab |
| `ic_tab_search.xml` | Search | Bottom nav tab |
| `ic_tab_profile.xml` | Person | Bottom nav tab |

### 3. App Launcher Icon
- Create adaptive icon: terracotta background (`#B65F3F`) + white "H" letterform
- Place in all mipmap directories: `mipmap-mdpi/`, `hdpi/`, `xhdpi/`, `xxhdpi/`, `xxxhdpi/`
- File names: `ic_launcher.png` and `ic_launcher_round.png`

### 4. Gradle Sync
- Open `D:\Projects\MeraGaw\heirloom\` in Android Studio
- Let it sync — it will download all dependencies automatically
- Fix any import errors (usually auto-resolvable with Alt+Enter)

### 5. Remaining Screens to Build (not in handoff — code yourself)
The handoff covers the 6 main screens. These flows still need to be built:

| Screen | Trigger |
|---|---|
| `AddMemberBottomSheet` | FAB on Family Tree |
| `AddMemoryBottomSheet` | "Add memory" on Profile |
| `ComposeMessageActivity` | "Message" / "Send wishes" buttons |
| `ProfileFragment` (current user) | Profile tab in bottom nav |

### 6. Data Layer (not in handoff)
The Java models are stubbed. You still need:
- [ ] A `Room` database (or API integration) for `FamilyMember` and `FamilyNotification`
- [ ] `ViewModel` + `Repository` classes for each Fragment
- [ ] `LiveData` / data binding wired into each Fragment

---

## Dependencies Already in build.gradle

```groovy
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'androidx.core:core:1.12.0'
implementation 'com.google.android.material:material:1.11.0'
implementation 'com.github.bumptech.glide:glide:4.16.0'
annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0'
implementation 'androidx.navigation:navigation-fragment:2.7.7'
implementation 'androidx.navigation:navigation-ui:2.7.7'
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.gridlayout:gridlayout:1.0.0'
```

**You'll likely want to add later:**
```groovy
// Room (local database)
implementation 'androidx.room:room-runtime:2.6.1'
annotationProcessor 'androidx.room:room-compiler:2.6.1'

// ViewModel + LiveData
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'
```

---

## Design Reference
Open `Family tree application design3.zip` → `Heirloom - Family Tree App.dc.html` in any browser to see all 6 screens side-by-side with pixel-level detail.
