import SwiftUI

struct HomeContainerView: View {
    @EnvironmentObject private var app: ATUAppModel
    @State private var selectedTab: HomeTab = .home
    @State private var search = ""
    @State private var selectedFilter = "Hamısı"
    @State private var selectedNews: AtuNews?
    @State private var darkMode = false
    @State private var notificationsEnabled = true
    @State private var news: [AtuNews] = []
    @State private var loadingNews = true
    @State private var messages: [ChatMessage] = [
        .init(text: "Salam. Mən ATU Campus AI köməkçisiyəm. Universitet, xəbərlər və tələbə xidmətləri ilə bağlı sualını yaz.", fromUser: false)
    ]
    @State private var input = ""
    @State private var sending = false
    @State private var passReady = false
    @State private var passApproved = false

    private var filteredNews: [AtuNews] {
        news.filter { item in
            let textMatch = search.isEmpty || item.title.localizedCaseInsensitiveContains(search) || item.summary.localizedCaseInsensitiveContains(search)
            let filterMatch = selectedFilter == "Hamısı" || item.title.localizedCaseInsensitiveContains(selectedFilter) || item.summary.localizedCaseInsensitiveContains(selectedFilter)
            return textMatch && filterMatch
        }
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            Group {
                if let selectedNews {
                    NewsDetailIOSView(item: selectedNews, dark: darkMode) { self.selectedNews = nil }
                } else {
                    switch selectedTab {
                    case .home:
                        HomeDashboardView(
                            student: app.profile,
                            dark: darkMode,
                            news: filteredNews,
                            loadingNews: loadingNews,
                            search: $search,
                            onNewsTap: { selectedNews = $0 }
                        )
                    case .news:
                        NewsListIOSView(
                            dark: darkMode,
                            news: filteredNews,
                            loading: loadingNews,
                            search: $search,
                            selectedFilter: $selectedFilter,
                            onNewsTap: { selectedNews = $0 }
                        )
                    case .assistant:
                        AssistantIOSView(
                            dark: darkMode,
                            messages: $messages,
                            input: $input,
                            sending: $sending,
                            sendAction: sendMessage
                        )
                    case .pass:
                        PassIOSView(student: app.profile, dark: darkMode, ready: $passReady, approved: $passApproved)
                    case .profile:
                        ProfileIOSView(
                            student: app.profile,
                            dark: $darkMode,
                            notificationsEnabled: $notificationsEnabled,
                            onReset: { app.resetProfile() }
                        )
                    }
                }
            }

            if selectedNews == nil {
                HStack {
                    ForEach(HomeTab.allCases, id: \.self) { tab in
                        Button {
                            withAnimation(AtuMotion.normal) { selectedTab = tab }
                        } label: {
                            VStack(spacing: 4) {
                                Image(systemName: icon(for: tab))
                                    .font(.system(size: 20, weight: .semibold))
                                if selectedTab == tab {
                                    Text(tab.rawValue)
                                        .font(.system(size: 11, weight: .bold))
                                }
                            }
                            .foregroundStyle(selectedTab == tab ? AtuTheme.burgundy : (darkMode ? AtuTheme.darkMuted : AtuTheme.textSecondary))
                            .frame(maxWidth: .infinity, minHeight: 56)
                            .background(
                                Group {
                                    if selectedTab == tab {
                                        RoundedRectangle(cornerRadius: 22, style: .continuous)
                                            .fill((darkMode ? AtuTheme.magenta : AtuTheme.pink).opacity(darkMode ? 0.22 : 1))
                                    }
                                }
                            )
                        }
                        .buttonStyle(AtuScaleButtonStyle(scale: 0.97))
                    }
                }
                .padding(8)
                .background(darkMode ? AtuTheme.darkSurface : .white, in: RoundedRectangle(cornerRadius: AtuRadius.nav, style: .continuous))
                .overlay(RoundedRectangle(cornerRadius: AtuRadius.nav, style: .continuous).stroke(darkMode ? Color.white.opacity(0.06) : Color.black.opacity(0.05), lineWidth: 1))
                .shadow(color: Color.black.opacity(darkMode ? 0 : 0.08), radius: 16, y: 10)
                .padding(.horizontal, 18)
                .padding(.bottom, 12)
            }
        }
        .background(darkMode ? AtuTheme.darkBackground : AtuTheme.backgroundLight)
        .task {
            loadingNews = true
            news = await app.newsService.fetchNews()
            loadingNews = false
        }
    }

    private func icon(for tab: HomeTab) -> String {
        switch tab {
        case .home: return "house"
        case .news: return "newspaper"
        case .assistant: return "sparkles"
        case .pass: return "creditcard"
        case .profile: return "person"
        }
    }

    private func sendMessage(_ text: String) {
        let question = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !question.isEmpty, !sending else { return }
        input = ""
        messages.append(.init(text: question, fromUser: true))
        sending = true
        Task {
            let answer = await app.aiService.sendMessage(question)
            await MainActor.run {
                messages.append(.init(text: answer, fromUser: false))
                sending = false
            }
        }
    }
}

struct HomeDashboardView: View {
    let student: StudentProfile?
    let dark: Bool
    let news: [AtuNews]
    let loadingNews: Bool
    @Binding var search: String
    var onNewsTap: (AtuNews) -> Void

    var body: some View {
        AtuScreen(dark: dark) {
            AtuTopHeader(greeting: "Salam", title: student?.name.isEmpty == false ? student!.name : "Tələbə", darkMode: dark) {
                Image("ATULogo").resizable().scaledToFit().frame(width: 40, height: 40).clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
                Circle().fill(dark ? AtuTheme.darkSurface : .white).frame(width: 40, height: 40).overlay(Image(systemName: "bell").foregroundStyle(dark ? AtuTheme.darkText : AtuTheme.textPrimary))
            }

            AtuSearchBar(value: search, onValueChange: { search = $0 }, darkMode: dark, placeholder: "ATU xəbərlərində axtar")

            if let student {
                AtuHeroCard(title: "Profil aktivdir", subtitle: "ID \(student.id) • Qrup \(student.group)", darkMode: dark) {
                    AtuInlineBadge(title: "Təsdiqlənib", icon: "checkmark.seal")
                        .padding(.top, 16)
                        .padding(.trailing, 16)
                        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topTrailing)
                }
                AtuStudentCardView(student: student, dark: dark)
            }

            AtuSectionHeader(title: "Campus modulları", action: "Aktiv xidmətlər", dark: dark)
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 12) {
                    ModuleTile(title: "Dərs cədvəli", subtitle: "Yaxında aktiv", icon: "calendar")
                    ModuleTile(title: "Kampus xəritəsi", subtitle: "A blok və xidmətlər", icon: "map")
                    ModuleTile(title: "ATU Pass", subtitle: "Giriş tarixçəsi", icon: "creditcard")
                    ModuleTile(title: "AI Köməkçi", subtitle: "Sualını yaz", icon: "sparkles")
                }
            }

            AtuSectionHeader(title: "Son ATU xəbərləri", action: loadingNews ? "Yüklənir" : "\(news.count) xəbər", dark: dark)
            if loadingNews {
                SkeletonNewsStack(dark: dark)
            } else if news.isEmpty {
                EmptyStateView(title: "Xəbər tapılmadı", subtitle: "Axtarış sorğusunu dəyişərək yenidən yoxlayın.", icon: "newspaper", dark: dark)
            } else {
                VStack(spacing: 12) {
                    ForEach(news.prefix(3), id: \.id) { item in
                        NewsCardView(item: item, dark: dark, onTap: { onNewsTap(item) })
                    }
                }
            }
        }
    }
}

struct NewsListIOSView: View {
    let dark: Bool
    let news: [AtuNews]
    let loading: Bool
    @Binding var search: String
    @Binding var selectedFilter: String
    var onNewsTap: (AtuNews) -> Void

    var body: some View {
        AtuScreen(dark: dark) {
            AtuSectionHeader(title: "Xəbərlər", subtitle: "ATU rəsmi saytından canlı xəbər axını", dark: dark)
            AtuSearchBar(value: search, onValueChange: { search = $0 }, darkMode: dark, placeholder: "Xəbər başlığı axtar")
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 10) {
                    ForEach(["Hamısı", "Tədbir", "Təhsil", "Tələbə", "Elan"], id: \.self) { filter in
                        AtuFilterChipView(label: filter, selected: selectedFilter == filter, dark: dark) {
                            selectedFilter = filter
                        }
                    }
                }
            }
            if loading {
                SkeletonNewsStack(dark: dark)
            } else if news.isEmpty {
                EmptyStateView(title: "Nəticə tapılmadı", subtitle: "Bu axtarış üçün xəbər yoxdur.", icon: "newspaper", dark: dark)
            } else {
                VStack(spacing: 12) {
                    NewsCardView(item: news[0], dark: dark, featured: true, onTap: { onNewsTap(news[0]) })
                    ForEach(news.dropFirst(), id: \.id) { item in
                        NewsCardView(item: item, dark: dark, onTap: { onNewsTap(item) })
                    }
                }
            }
        }
    }
}

struct NewsDetailIOSView: View {
    let item: AtuNews
    let dark: Bool
    let onBack: () -> Void

    var body: some View {
        ScrollView(showsIndicators: false) {
            ZStack(alignment: .topLeading) {
                AsyncImage(url: URL(string: item.imageUrl)) { phase in
                    switch phase {
                    case .success(let image):
                        image.resizable().scaledToFill()
                    default:
                        Color.gray.opacity(0.15)
                    }
                }
                .frame(height: 320)
                .clipped()

                LinearGradient(colors: [Color.black.opacity(0.12), .clear, Color.black.opacity(0.66)], startPoint: .top, endPoint: .bottom)
                    .frame(height: 320)

                Button(action: onBack) {
                    Circle().fill(Color.white.opacity(0.94)).frame(width: 46, height: 46).overlay(Image(systemName: "arrow.left").foregroundStyle(AtuTheme.textPrimary))
                }
                .buttonStyle(AtuScaleButtonStyle())
                .padding(18)

                VStack(alignment: .leading, spacing: 10) {
                    Spacer()
                    AtuInlineBadge(title: item.date, icon: "calendar")
                    Text(item.title)
                        .font(.system(size: 28, weight: .bold))
                        .foregroundStyle(.white)
                }
                .padding(22)
                .frame(maxWidth: .infinity, minHeight: 320, alignment: .bottomLeading)
            }

            AtuScreen(dark: dark) {
                VStack(alignment: .leading, spacing: 14) {
                    Text("ATU rəsmi xəbəri")
                        .font(.system(size: 13, weight: .bold))
                        .foregroundStyle(AtuTheme.burgundy)
                    Text(item.summary.isEmpty ? "Xəbər mətni ATU saytından çəkilib." : item.summary)
                        .font(.system(size: 16, weight: .regular))
                        .foregroundStyle(dark ? AtuTheme.surfaceLight : AtuTheme.textPrimary)
                    Text("Mənbə: atu.edu.az. Xəbər tətbiq daxilində göstərilir.")
                        .font(.system(size: 14, weight: .medium))
                        .foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary)
                }
                .atuCard(dark: dark)
            }
        }
        .background(dark ? AtuTheme.darkBackground : AtuTheme.backgroundLight)
        .ignoresSafeArea(edges: .top)
    }
}

struct AssistantIOSView: View {
    let dark: Bool
    @Binding var messages: [ChatMessage]
    @Binding var input: String
    @Binding var sending: Bool
    let sendAction: (String) -> Void

    var body: some View {
        VStack(spacing: 0) {
            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: 14) {
                    AtuScreen(dark: dark, contentPadding: EdgeInsets(top: 20, leading: 20, bottom: 0, trailing: 20)) {
                        AtuHeroCard(title: "ATU AI Köməkçi", subtitle: "Campus, xəbərlər və tələbə xidmətləri üçün sürətli cavablar.", darkMode: dark) {
                            Image(systemName: "sparkles").font(.system(size: 48)).foregroundStyle(AtuTheme.burgundy.opacity(0.24)).frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topTrailing).padding(18)
                        }
                    }

                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 10) {
                            ForEach(["Bugünkü ATU xəbərləri", "Tələbə xidmətləri", "Dərs cədvəli", "Kampus yönləndirməsi"], id: \.self) { prompt in
                                AtuFilterChipView(label: prompt, selected: false, dark: dark) { sendAction(prompt) }
                            }
                        }
                        .padding(.horizontal, 20)
                    }

                    LazyVStack(spacing: 12) {
                        ForEach(messages) { message in
                            ChatBubbleView(message: message, dark: dark)
                        }
                        if sending { TypingDotsView(dark: dark) }
                    }
                    .padding(.horizontal, 20)
                    .padding(.bottom, 16)
                }
            }

            HStack(spacing: 10) {
                TextField("AI köməkçidən soruş", text: $input, axis: .vertical)
                    .textFieldStyle(.plain)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 14)
                    .background(dark ? AtuTheme.darkElevated : AtuTheme.surfaceSoft, in: RoundedRectangle(cornerRadius: AtuRadius.input, style: .continuous))

                Button {
                    sendAction(input)
                } label: {
                    Circle().fill(input.isEmpty || sending ? Color.gray.opacity(0.3) : AtuTheme.burgundy).frame(width: 52, height: 52).overlay(Image(systemName: "paperplane.fill").foregroundStyle(.white))
                }
                .buttonStyle(AtuScaleButtonStyle())
                .disabled(input.isEmpty || sending)
            }
            .padding(14)
            .background(dark ? AtuTheme.darkSurface : .white)
            .overlay(Rectangle().fill((dark ? Color.white : Color.black).opacity(0.05)).frame(height: 1), alignment: .top)
            .padding(.bottom, 70)
        }
        .background(dark ? AtuTheme.darkBackground : AtuTheme.backgroundLight)
    }
}

struct PassIOSView: View {
    let student: StudentProfile?
    let dark: Bool
    @Binding var ready: Bool
    @Binding var approved: Bool

    private var logs: [LogRow] {
        let base = [
            LogRow(title: "Əsas turniket", subtitle: "A korpusuna giriş", time: "08:42", type: "Giriş", success: true),
            LogRow(title: "Kitabxana keçidi", subtitle: "Oxu zalından çıxış", time: "12:18", type: "Çıxış", success: true),
            LogRow(title: "Laboratoriya bloku", subtitle: "B korpusuna giriş", time: "14:05", type: "Giriş", success: true)
        ]
        if approved {
            return [LogRow(title: "Əsas turniket", subtitle: "A korpusuna giriş təsdiqləndi", time: "İndi", type: "Giriş", success: true)] + base
        }
        return base
    }

    var body: some View {
        AtuScreen(dark: dark) {
            AtuSectionHeader(title: "ATU Pass", subtitle: "Rəqəmsal giriş kartı və turniket tarixçəsi", dark: dark)
            if let student {
                AtuPassCardView(student: student, dark: dark, approved: approved)
            }
            VStack(alignment: .leading, spacing: 12) {
                HStack {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Bugünkü status")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary)
                        Text(approved ? "Giriş təsdiqləndi" : ready ? "Turniket yoxlaması hazırdır" : "Yoxlama gözləyir")
                            .font(.system(size: 20, weight: .bold))
                            .foregroundStyle(dark ? AtuTheme.surfaceLight : AtuTheme.textPrimary)
                    }
                    Spacer()
                    AtuInlineBadge(title: approved ? "Təsdiq" : "Demo", icon: approved ? "checkmark.seal.fill" : "shield")
                }
                Button(!ready ? "Turniket yoxlamasını hazırla" : !approved ? "Girişi təsdiqlə" : "İcazə verildi") {
                    if !ready { ready = true } else { approved = true }
                }
                .buttonStyle(AtuPrimaryButtonStyle())
            }
            .atuCard(dark: dark)

            AtuSectionHeader(title: "Giriş-çıxış tarixçəsi", action: "Bu gün", dark: dark)
            ForEach(logs) { log in
                HStack(spacing: 14) {
                    Circle().fill(log.type == "Giriş" ? AtuTheme.blue : AtuTheme.pink).frame(width: 48, height: 48).overlay(Image(systemName: log.type == "Giriş" ? "shield" : "creditcard").foregroundStyle(log.type == "Giriş" ? Color.blue : AtuTheme.burgundy))
                    VStack(alignment: .leading, spacing: 4) {
                        HStack {
                            Text(log.title).font(.system(size: 16, weight: .bold)).foregroundStyle(dark ? AtuTheme.surfaceLight : AtuTheme.textPrimary)
                            Spacer()
                            Text(log.time).font(.system(size: 12, weight: .bold)).foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary)
                        }
                        Text(log.subtitle).font(.system(size: 14, weight: .medium)).foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary)
                    }
                }
                .atuCard(dark: dark)
            }
        }
    }
}

struct ProfileIOSView: View {
    let student: StudentProfile?
    @Binding var dark: Bool
    @Binding var notificationsEnabled: Bool
    let onReset: () -> Void

    var body: some View {
        AtuScreen(dark: dark) {
            AtuSectionHeader(title: "Profil", subtitle: "Skan edilmiş tələbə məlumatları", dark: dark)
            if let student {
                AtuStudentCardView(student: student, dark: dark)
                ProfileRow(title: "Fakültə", value: student.faculty.isEmpty ? "Əlavə olunmayıb" : student.faculty, icon: "person.text.rectangle", dark: dark)
                ProfileRow(title: "İxtisas", value: student.specialty.isEmpty ? "Təyin edilməyib" : student.specialty, icon: "megaphone", dark: dark)
            } else {
                EmptyStateView(title: "Məlumat tapılmadı", subtitle: "Tələbə profili hələ yaradılmayıb.", icon: "person.crop.circle.badge.exclamationmark", dark: dark)
            }
            AtuSectionHeader(title: "Ayarlar", action: "Tətbiq", dark: dark)
            SettingsRow(title: "Dark mode", subtitle: dark ? "Qaranlıq görünüş aktivdir" : "İşıqlı görünüş aktivdir", icon: dark ? "moon.fill" : "sun.max.fill", isOn: $dark, dark: dark)
            SettingsRow(title: "Bildirişlər", subtitle: "ATU xəbərləri və campus yenilikləri", icon: "bell", isOn: $notificationsEnabled, dark: dark)
            Button("Profili sil və yenidən skan et", action: onReset)
                .buttonStyle(AtuSecondaryButtonStyle(dark: dark))
                .foregroundStyle(AtuTheme.error)
                .overlay(
                    RoundedRectangle(cornerRadius: AtuRadius.button, style: .continuous)
                        .stroke(AtuTheme.error.opacity(dark ? 0.35 : 0.22), lineWidth: 1)
                )
        }
    }
}

private struct AtuStudentCardView: View {
    let student: StudentProfile
    let dark: Bool

    var body: some View {
        HStack(spacing: 14) {
            RoundedRectangle(cornerRadius: 20, style: .continuous)
                .fill(LinearGradient(colors: [AtuTheme.pink, AtuTheme.lavender], startPoint: .topLeading, endPoint: .bottomTrailing))
                .frame(width: 58, height: 58)
                .overlay(Text(student.name.first.map(String.init)?.uppercased() ?? "A").font(.system(size: 22, weight: .bold)).foregroundStyle(AtuTheme.burgundy))
            VStack(alignment: .leading, spacing: 4) {
                Text(student.fullName.isEmpty ? "Tələbə" : student.fullName).font(.system(size: 20, weight: .bold)).foregroundStyle(dark ? AtuTheme.surfaceLight : AtuTheme.textPrimary)
                Text("ID \(student.id.isEmpty ? "Təyin edilməyib" : student.id) • Qrup \(student.group.isEmpty ? "Təyin edilməyib" : student.group)")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary)
                Text(student.specialty.isEmpty ? "İxtisas təyin edilməyib" : student.specialty)
                    .font(.system(size: 14, weight: .medium))
                    .foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary)
            }
            Spacer()
            AtuInlineBadge(title: "Aktiv", icon: "checkmark.seal.fill")
        }
        .atuCard(dark: dark)
    }
}

private struct ModuleTile: View {
    let title: String
    let subtitle: String
    let icon: String

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .fill(LinearGradient(colors: [AtuTheme.pink, AtuTheme.blue], startPoint: .topLeading, endPoint: .bottomTrailing))
                .frame(width: 42, height: 42)
                .overlay(Image(systemName: icon).foregroundStyle(AtuTheme.burgundy))
            Spacer()
            Text(title).font(.system(size: 16, weight: .bold)).foregroundStyle(AtuTheme.textPrimary)
            Text(subtitle).font(.system(size: 14, weight: .medium)).foregroundStyle(AtuTheme.textSecondary)
        }
        .frame(width: 156, height: 126, alignment: .topLeading)
        .atuCard()
    }
}

private struct NewsCardView: View {
    let item: AtuNews
    let dark: Bool
    var featured: Bool = false
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                AsyncImage(url: URL(string: item.imageUrl)) { phase in
                    switch phase {
                    case .success(let image):
                        image.resizable().scaledToFill()
                    default:
                        (dark ? AtuTheme.darkElevated : AtuTheme.surfaceSoft)
                    }
                }
                .frame(width: featured ? 104 : 82, height: featured ? 104 : 82)
                .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))

                VStack(alignment: .leading, spacing: 5) {
                    Text(item.date.isEmpty ? "ATU" : item.date).font(.system(size: 12, weight: .bold)).foregroundStyle(AtuTheme.burgundy)
                    Text(item.title).font(.system(size: 16, weight: .bold)).foregroundStyle(dark ? AtuTheme.surfaceLight : AtuTheme.textPrimary).multilineTextAlignment(.leading).lineLimit(featured ? 3 : 2)
                    Text(item.summary).font(.system(size: 14, weight: .medium)).foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary).multilineTextAlignment(.leading).lineLimit(2)
                }
                Spacer()
                Image(systemName: "chevron.right").foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary)
            }
            .atuCard(dark: dark)
        }
        .buttonStyle(AtuScaleButtonStyle())
    }
}

private struct SkeletonNewsStack: View {
    let dark: Bool

    var body: some View {
        VStack(spacing: 12) {
            ForEach(0..<3, id: \.self) { _ in
                RoundedRectangle(cornerRadius: AtuRadius.card, style: .continuous)
                    .fill(dark ? AtuTheme.darkElevated : AtuTheme.surfaceSoft)
                    .frame(height: 92)
            }
        }
        .redacted(reason: .placeholder)
    }
}

private struct EmptyStateView: View {
    let title: String
    let subtitle: String
    let icon: String
    let dark: Bool

    var body: some View {
        VStack(spacing: 12) {
            Circle().fill(AtuTheme.pink).frame(width: 56, height: 56).overlay(Image(systemName: icon).foregroundStyle(AtuTheme.burgundy))
            Text(title).font(.system(size: 17, weight: .bold)).foregroundStyle(dark ? AtuTheme.surfaceLight : AtuTheme.textPrimary)
            Text(subtitle).font(.system(size: 14, weight: .medium)).foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary).multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity)
        .atuCard(dark: dark)
    }
}

private struct ChatBubbleView: View {
    let message: ChatMessage
    let dark: Bool

    var body: some View {
        HStack {
            if message.fromUser { Spacer() }
            Text(message.text)
                .font(.system(size: 16, weight: .regular))
                .foregroundStyle(message.fromUser ? Color.white : (dark ? AtuTheme.surfaceLight : AtuTheme.textPrimary))
                .padding(16)
                .background(message.fromUser ? AtuTheme.burgundy : (dark ? AtuTheme.darkSurface : AtuTheme.surfaceLight))
                .clipShape(RoundedRectangle(cornerRadius: 22, style: .continuous))
                .overlay(
                    RoundedRectangle(cornerRadius: 22, style: .continuous)
                        .stroke(message.fromUser ? Color.clear : (dark ? Color.white.opacity(0.06) : Color.black.opacity(0.05)), lineWidth: 1)
                )
                .frame(maxWidth: UIScreen.main.bounds.width * 0.78, alignment: message.fromUser ? .trailing : .leading)
            if !message.fromUser { Spacer() }
        }
    }
}

private struct TypingDotsView: View {
    let dark: Bool
    @State private var phase: CGFloat = 0.35

    var body: some View {
        HStack {
            HStack(spacing: 6) {
                ForEach(0..<3, id: \.self) { index in
                    Circle()
                        .fill(AtuTheme.burgundy.opacity(max(0.25, Double(phase) - Double(index) * 0.16)))
                        .frame(width: 7, height: 7)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(dark ? AtuTheme.darkSurface : AtuTheme.surfaceLight, in: RoundedRectangle(cornerRadius: 22, style: .continuous))
            .overlay(RoundedRectangle(cornerRadius: 22, style: .continuous).stroke(dark ? Color.white.opacity(0.06) : Color.black.opacity(0.05), lineWidth: 1))
            Spacer()
        }
        .task {
            withAnimation(.easeInOut(duration: 0.42).repeatForever(autoreverses: true)) {
                phase = 1
            }
        }
    }
}

private struct SettingsRow: View {
    let title: String
    let subtitle: String
    let icon: String
    @Binding var isOn: Bool
    let dark: Bool

    var body: some View {
        HStack(spacing: 14) {
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .fill((dark ? AtuTheme.darkElevated : AtuTheme.pink))
                .frame(width: 46, height: 46)
                .overlay(Image(systemName: icon).foregroundStyle(AtuTheme.burgundy))
            VStack(alignment: .leading, spacing: 3) {
                Text(title).font(.system(size: 16, weight: .bold)).foregroundStyle(dark ? AtuTheme.surfaceLight : AtuTheme.textPrimary)
                Text(subtitle).font(.system(size: 14, weight: .medium)).foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary)
            }
            Spacer()
            Toggle("", isOn: $isOn)
                .labelsHidden()
                .tint(AtuTheme.burgundy)
        }
        .atuCard(dark: dark)
    }
}

private struct ProfileRow: View {
    let title: String
    let value: String
    let icon: String
    let dark: Bool

    var body: some View {
        HStack(spacing: 14) {
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .fill(dark ? AtuTheme.darkElevated : AtuTheme.pink)
                .frame(width: 46, height: 46)
                .overlay(Image(systemName: icon).foregroundStyle(AtuTheme.burgundy))
            VStack(alignment: .leading, spacing: 4) {
                Text(title).font(.system(size: 14, weight: .medium)).foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary)
                Text(value).font(.system(size: 16, weight: .bold)).foregroundStyle(dark ? AtuTheme.surfaceLight : AtuTheme.textPrimary)
            }
            Spacer()
        }
        .atuCard(dark: dark)
    }
}

private struct AtuPassCardView: View {
    let student: StudentProfile
    let dark: Bool
    let approved: Bool
    @State private var shine: CGFloat = -0.4

    var body: some View {
        ZStack {
            LinearGradient(colors: [AtuTheme.navy, AtuTheme.burgundy, AtuTheme.magenta], startPoint: .topLeading, endPoint: .bottomTrailing)
                .clipShape(RoundedRectangle(cornerRadius: AtuRadius.hero, style: .continuous))

            Rectangle()
                .fill(Color.white.opacity(0.15))
                .frame(width: 80)
                .rotationEffect(.degrees(18))
                .offset(x: 280 * shine)
                .blur(radius: 2)

            VStack(alignment: .leading, spacing: 0) {
                HStack(alignment: .top) {
                    VStack(alignment: .leading, spacing: 5) {
                        Text("ATU Campus").font(.system(size: 12, weight: .bold)).foregroundStyle(Color.white.opacity(0.78))
                        Text("Digital Pass").font(.system(size: 27, weight: .bold)).foregroundStyle(.white)
                    }
                    Spacer()
                    AtuInlineBadge(title: approved ? "Giriş açıqdır" : "Verified", icon: "checkmark.seal.fill")
                }
                Spacer()
                VStack(alignment: .leading, spacing: 8) {
                    Text(student.fullName).font(.system(size: 20, weight: .bold)).foregroundStyle(.white)
                    Text("ID \(student.id) • Qrup \(student.group)").font(.system(size: 16, weight: .bold)).foregroundStyle(Color.white.opacity(0.82))
                }
            }
            .padding(22)
        }
        .frame(height: 220)
        .task {
            withAnimation(.linear(duration: 3.6).repeatForever(autoreverses: false)) {
                shine = 1.25
            }
        }
    }
}

private struct LogRow: Identifiable {
    let id = UUID()
    let title: String
    let subtitle: String
    let time: String
    let type: String
    let success: Bool
}
