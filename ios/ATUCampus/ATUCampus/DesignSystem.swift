import SwiftUI

enum AtuTheme {
    static let backgroundLight = Color(red: 246/255, green: 247/255, blue: 251/255)
    static let surfaceLight = Color.white
    static let surfaceSoft = Color(red: 241/255, green: 243/255, blue: 248/255)
    static let textPrimary = Color(red: 17/255, green: 24/255, blue: 39/255)
    static let textSecondary = Color(red: 107/255, green: 114/255, blue: 128/255)
    static let burgundy = Color(red: 138/255, green: 13/255, blue: 59/255)
    static let magenta = Color(red: 181/255, green: 22/255, blue: 78/255)
    static let pink = Color(red: 252/255, green: 231/255, blue: 241/255)
    static let lavender = Color(red: 238/255, green: 240/255, blue: 255/255)
    static let blue = Color(red: 234/255, green: 241/255, blue: 255/255)
    static let success = Color(red: 22/255, green: 163/255, blue: 74/255)
    static let error = Color(red: 220/255, green: 38/255, blue: 38/255)
    static let darkBackground = Color(red: 9/255, green: 10/255, blue: 15/255)
    static let darkSurface = Color(red: 19/255, green: 17/255, blue: 26/255)
    static let darkElevated = Color(red: 26/255, green: 22/255, blue: 34/255)
    static let darkMuted = Color(red: 176/255, green: 180/255, blue: 192/255)
    static let darkText = Color(red: 245/255, green: 247/255, blue: 251/255)
    static let navy = Color(red: 14/255, green: 19/255, blue: 32/255)
}

enum AtuRadius {
    static let card: CGFloat = 24
    static let hero: CGFloat = 28
    static let input: CGFloat = 20
    static let button: CGFloat = 20
    static let chip: CGFloat = 999
    static let nav: CGFloat = 30
}

enum AtuSpacing {
    static let xs: CGFloat = 8
    static let sm: CGFloat = 12
    static let md: CGFloat = 16
    static let lg: CGFloat = 20
    static let xl: CGFloat = 24
    static let xxl: CGFloat = 32
}

enum AtuMotion {
    static let fast = Animation.easeInOut(duration: 0.15)
    static let normal = Animation.easeInOut(duration: 0.25)
    static let slow = Animation.easeInOut(duration: 0.45)
    static let spring = Animation.spring(response: 0.38, dampingFraction: 0.78)
}

struct AtuCardModifier: ViewModifier {
    var dark: Bool

    func body(content: Content) -> some View {
        content
            .padding(AtuSpacing.md)
            .background(dark ? AtuTheme.darkSurface : AtuTheme.surfaceLight)
            .clipShape(RoundedRectangle(cornerRadius: AtuRadius.card, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: AtuRadius.card, style: .continuous)
                    .stroke(dark ? Color.white.opacity(0.06) : Color.black.opacity(0.05), lineWidth: 1)
            )
            .shadow(color: Color.black.opacity(dark ? 0.0 : 0.06), radius: dark ? 0 : 10, y: dark ? 0 : 6)
    }
}

extension View {
    func atuCard(dark: Bool = false) -> some View {
        modifier(AtuCardModifier(dark: dark))
    }

    func atuPressable(scale: CGFloat = 0.985) -> some View {
        buttonStyle(AtuScaleButtonStyle(scale: scale))
    }
}

struct AtuScaleButtonStyle: ButtonStyle {
    var scale: CGFloat = 0.985

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? scale : 1)
            .animation(AtuMotion.fast, value: configuration.isPressed)
    }
}

struct AtuPrimaryButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.system(size: 16, weight: .bold))
            .foregroundStyle(Color.white)
            .frame(maxWidth: .infinity, minHeight: 56)
            .background(AtuTheme.burgundy)
            .clipShape(RoundedRectangle(cornerRadius: AtuRadius.button, style: .continuous))
            .scaleEffect(configuration.isPressed ? 0.985 : 1)
            .animation(AtuMotion.fast, value: configuration.isPressed)
    }
}

struct AtuSecondaryButtonStyle: ButtonStyle {
    var dark: Bool = false

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.system(size: 16, weight: .bold))
            .foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textPrimary)
            .frame(maxWidth: .infinity, minHeight: 54)
            .background(dark ? AtuTheme.darkSurface : AtuTheme.surfaceLight)
            .clipShape(RoundedRectangle(cornerRadius: AtuRadius.button, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: AtuRadius.button, style: .continuous)
                    .stroke(dark ? Color.white.opacity(0.08) : Color.black.opacity(0.06), lineWidth: 1)
            )
            .scaleEffect(configuration.isPressed ? 0.985 : 1)
            .animation(AtuMotion.fast, value: configuration.isPressed)
    }
}

struct AtuScreen<Content: View>: View {
    var dark: Bool = false
    var contentPadding = EdgeInsets(top: 20, leading: 20, bottom: 120, trailing: 20)
    @ViewBuilder var content: () -> Content

    var body: some View {
        ScrollView(showsIndicators: false) {
            VStack(alignment: .leading, spacing: AtuSpacing.lg) {
                content()
            }
            .padding(contentPadding)
        }
        .background(dark ? AtuTheme.darkBackground : AtuTheme.backgroundLight)
    }
}

struct AtuSectionHeader: View {
    let title: String
    var subtitle: String? = nil
    var action: String? = nil
    var dark: Bool = false

    var body: some View {
        HStack(alignment: .bottom) {
            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.system(size: 22, weight: .bold))
                    .foregroundStyle(dark ? AtuTheme.surfaceLight : AtuTheme.textPrimary)
                if let subtitle {
                    Text(subtitle)
                        .font(.system(size: 15, weight: .medium))
                        .foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary)
                }
            }
            Spacer()
            if let action {
                Text(action)
                    .font(.system(size: 12, weight: .bold))
                    .foregroundStyle(AtuTheme.burgundy)
            }
        }
    }
}

struct AtuTopHeader<Trailing: View>: View {
    let greeting: String
    let title: String
    var darkMode: Bool = false
    @ViewBuilder var trailing: () -> Trailing

    var body: some View {
        HStack(alignment: .top, spacing: AtuSpacing.md) {
            VStack(alignment: .leading, spacing: 6) {
                Text(greeting)
                    .font(.system(size: 14, weight: .medium))
                    .foregroundStyle(darkMode ? AtuTheme.darkMuted : AtuTheme.textSecondary)
                Text(title)
                    .font(.system(size: 30, weight: .bold))
                    .foregroundStyle(darkMode ? AtuTheme.darkText : AtuTheme.textPrimary)
                    .lineLimit(2)
            }
            Spacer(minLength: AtuSpacing.md)
            HStack(spacing: 10) {
                trailing()
            }
        }
    }
}

struct AtuSearchBar: View {
    let value: String
    let onValueChange: (String) -> Void
    var darkMode: Bool = false
    var placeholder: String = "Axtar"
    @FocusState private var focused: Bool

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: "magnifyingglass")
                .foregroundStyle(focused ? AtuTheme.burgundy : (darkMode ? AtuTheme.darkMuted : AtuTheme.textSecondary))
            TextField(placeholder, text: Binding(get: { value }, set: onValueChange))
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .focused($focused)
            if !value.isEmpty {
                Button {
                    onValueChange("")
                } label: {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundStyle(darkMode ? AtuTheme.darkMuted : AtuTheme.textSecondary)
                }
                .buttonStyle(AtuScaleButtonStyle())
            }
        }
        .padding(.horizontal, 16)
        .frame(height: 56)
        .background(darkMode ? AtuTheme.darkSurface : AtuTheme.surfaceLight)
        .clipShape(RoundedRectangle(cornerRadius: AtuRadius.input, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: AtuRadius.input, style: .continuous)
                .stroke(focused ? AtuTheme.burgundy.opacity(0.28) : (darkMode ? Color.white.opacity(0.06) : Color.black.opacity(0.05)), lineWidth: 1)
        )
        .shadow(color: Color.black.opacity(darkMode ? 0 : 0.04), radius: focused ? 14 : 8, y: focused ? 8 : 4)
        .animation(AtuMotion.fast, value: focused)
    }
}

struct AtuHeroCard<Overlay: View>: View {
    let title: String
    let subtitle: String
    var darkMode: Bool = false
    @ViewBuilder var overlay: () -> Overlay

    var body: some View {
        ZStack {
            LinearGradient(
                colors: darkMode
                    ? [AtuTheme.navy, AtuTheme.burgundy, AtuTheme.magenta.opacity(0.9)]
                    : [AtuTheme.burgundy, AtuTheme.magenta, Color(red: 208/255, green: 132/255, blue: 179/255)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )

            Circle()
                .fill(Color.white.opacity(0.10))
                .frame(width: 180, height: 180)
                .offset(x: 110, y: -70)

            Circle()
                .fill(Color.white.opacity(0.08))
                .frame(width: 120, height: 120)
                .offset(x: -120, y: 90)

            VStack(alignment: .leading, spacing: 8) {
                Text(title)
                    .font(.system(size: 24, weight: .bold))
                    .foregroundStyle(.white)
                Text(subtitle)
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(Color.white.opacity(0.82))
                    .lineLimit(2)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .bottomLeading)
            .padding(22)

            overlay()
        }
        .frame(maxWidth: .infinity)
        .frame(height: 176)
        .clipShape(RoundedRectangle(cornerRadius: AtuRadius.hero, style: .continuous))
        .shadow(color: Color.black.opacity(darkMode ? 0.0 : 0.10), radius: 18, y: 12)
    }
}

struct AtuFilterChipView: View {
    let label: String
    let selected: Bool
    let dark: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(label)
                .font(.system(size: 13, weight: .bold))
                .foregroundStyle(selected ? (dark ? AtuTheme.darkText : AtuTheme.burgundy) : (dark ? AtuTheme.darkMuted : AtuTheme.textSecondary))
                .padding(.horizontal, 14)
                .padding(.vertical, 10)
                .background(
                    Capsule()
                        .fill(selected ? (dark ? AtuTheme.darkElevated : AtuTheme.pink) : (dark ? AtuTheme.darkSurface : AtuTheme.surfaceLight))
                )
                .overlay(
                    Capsule()
                        .stroke(selected ? AtuTheme.burgundy.opacity(0.22) : (dark ? Color.white.opacity(0.06) : Color.black.opacity(0.05)), lineWidth: 1)
                )
        }
        .buttonStyle(AtuScaleButtonStyle(scale: 0.98))
    }
}

struct AtuInlineNote: View {
    let text: String
    var dark: Bool = false

    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            Image(systemName: "lock.shield")
                .font(.system(size: 14, weight: .bold))
                .foregroundStyle(AtuTheme.burgundy)
                .padding(.top, 2)
            Text(text)
                .font(.system(size: 14, weight: .medium))
                .foregroundStyle(dark ? AtuTheme.darkMuted : AtuTheme.textSecondary)
                .multilineTextAlignment(.leading)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, 14)
        .padding(.vertical, 13)
        .background(dark ? AtuTheme.darkSurface : AtuTheme.surfaceSoft, in: RoundedRectangle(cornerRadius: 18, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .stroke(dark ? Color.white.opacity(0.06) : Color.black.opacity(0.05), lineWidth: 1)
        )
    }
}
