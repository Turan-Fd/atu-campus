import SwiftUI

struct SplashView: View {
    @EnvironmentObject private var app: ATUAppModel
    @State private var animate = false

    var body: some View {
        ZStack {
            LinearGradient(colors: [Color(red: 93/255, green: 8/255, blue: 42/255), AtuTheme.burgundy, AtuTheme.magenta], startPoint: .topLeading, endPoint: .bottomTrailing)
                .ignoresSafeArea()

            Circle().fill(Color.white.opacity(0.08)).frame(width: 280, height: 280).offset(x: -120, y: -260)
            Circle().fill(Color.white.opacity(0.05)).frame(width: 220, height: 220).offset(x: 160, y: 280)

            VStack(spacing: 24) {
                Image("ATULogo")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 94, height: 94)
                    .padding(10)
                    .background(Color.white, in: RoundedRectangle(cornerRadius: 28, style: .continuous))
                    .scaleEffect(animate ? 1 : 0.92)
                    .opacity(animate ? 1 : 0)
                    .offset(y: animate ? 0 : 16)

                VStack(spacing: 8) {
                    Text("ATU Campus")
                        .font(.system(size: 34, weight: .bold))
                        .foregroundStyle(.white)
                    Text("Rəqəmsal tələbə platforması")
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundStyle(Color.white.opacity(0.82))
                }

                HStack(spacing: 8) {
                    Image(systemName: "lock")
                    Text("Lokal skan • Backend yoxlama • ATU xəbərləri")
                }
                .font(.system(size: 13, weight: .semibold))
                .foregroundStyle(Color.white.opacity(0.78))

                ProgressView().tint(.white)
            }
        }
        .task {
            withAnimation(AtuMotion.slow) { animate = true }
            try? await Task.sleep(for: .seconds(1.8))
            app.finishSplash()
        }
    }
}

struct PermissionView: View {
    @EnvironmentObject private var app: ATUAppModel
    @State private var denied = false

    var body: some View {
        AtuScreen {
            AtuSectionHeader(title: "Təhlükəsiz skan", subtitle: "Tələbə vəsiqəsi yalnız cihazınızda emal olunur.")

            VStack(spacing: AtuSpacing.lg) {
                AtuInlineBadge(title: "Məlumatlar qorunur", icon: "checkmark.shield")

                VStack(spacing: AtuSpacing.md) {
                    RoundedRectangle(cornerRadius: 24, style: .continuous)
                        .fill(LinearGradient(colors: [AtuTheme.pink, AtuTheme.lavender], startPoint: .topLeading, endPoint: .bottomTrailing))
                        .frame(width: 72, height: 72)
                        .overlay(Image(systemName: "camera.fill").font(.system(size: 28)).foregroundStyle(AtuTheme.burgundy))

                    Text("Kamera icazəsi")
                        .font(.system(size: 28, weight: .bold))
                        .foregroundStyle(AtuTheme.textPrimary)

                    Text("Tələbə vəsiqənizin ön və arxa üzünü skan etmək üçün kamera icazəsi lazımdır.")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundStyle(AtuTheme.textSecondary)
                        .multilineTextAlignment(.center)

                    AtuInlineNote(text: "Kart şəkilləri serverə göndərilmir və bu mərhələdə daimi saxlanmır.")

                    if denied {
                        Text("Kamera icazəsi verilmədi. Davam etmək üçün icazəni yenidən təsdiqləyin.")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundStyle(AtuTheme.error)
                            .multilineTextAlignment(.center)
                    }

                    Button("Kameraya icazə ver") {
                        Task {
                            let granted = await app.camera.requestAndConfigure()
                            if granted {
                                app.screen = .scan
                            } else {
                                denied = true
                            }
                        }
                    }
                    .buttonStyle(AtuPrimaryButtonStyle())
                }
            }
            .atuCard()
        }
    }
}

struct ScanCardView: View {
    @EnvironmentObject private var app: ATUAppModel

    var progressText: String {
        app.camera.currentSide == .front ? "1/2 Ön üz" : "2/2 Arxa üz"
    }

    var instruction: String {
        app.camera.currentSide == .front
            ? "Tələbə vəsiqəsinin ön üzünü çərçivəyə yerləşdirin"
            : "Tələbə vəsiqəsinin arxa üzünü çərçivəyə yerləşdirin"
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            CameraPreviewView(model: app.camera)
                .ignoresSafeArea()

            AtuScanFrame(progressText: progressText, instruction: instruction)

            VStack(spacing: 16) {
                HStack {
                    ScanThumb(title: "Ön üz", image: app.camera.frontImage, active: app.camera.currentSide == .front)
                    Spacer()
                    Button {
                        app.camera.capture { finished in
                            if finished { app.handleCaptured() }
                        }
                    } label: {
                        Circle()
                            .fill(.white)
                            .frame(width: 82, height: 82)
                            .overlay(Image(systemName: "camera.fill").font(.system(size: 30)).foregroundStyle(AtuTheme.burgundy))
                    }
                    .buttonStyle(AtuScaleButtonStyle())
                    Spacer()
                    ScanThumb(title: "Arxa üz", image: app.camera.backImage, active: app.camera.currentSide == .back)
                }

                Button("Yenidən çək") {
                    app.camera.reset()
                }
                .buttonStyle(AtuSecondaryButtonStyle())
                .frame(maxWidth: 280)

                if let error = app.camera.cameraError {
                    Text(error)
                        .font(.system(size: 14, weight: .bold))
                        .foregroundStyle(AtuTheme.error)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 10)
                        .background(AtuTheme.pink, in: RoundedRectangle(cornerRadius: 14, style: .continuous))
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 24)
        }
        .task {
            let granted = await app.camera.requestAndConfigure()
            if !granted { app.screen = .permission }
        }
    }
}

struct ProcessingView: View {
    @EnvironmentObject private var app: ATUAppModel
    @State private var currentStep = 0
    private let steps = ["Kart oxunur", "Məlumatlar çıxarılır", "Tələbə yoxlanılır"]

    var body: some View {
        AtuScreen {
            AtuSectionHeader(title: steps[currentStep], subtitle: "Tələbə kartı məlumatları təhlükəsiz şəkildə analiz olunur.")

            RoundedRectangle(cornerRadius: AtuRadius.hero, style: .continuous)
                .fill(LinearGradient(colors: [AtuTheme.burgundy, AtuTheme.magenta], startPoint: .topLeading, endPoint: .bottomTrailing))
                .frame(height: 220)
                .overlay {
                    Image(systemName: "doc.text.viewfinder")
                        .font(.system(size: 52))
                        .foregroundStyle(Color.white.opacity(0.9))
                }

            VStack(alignment: .leading, spacing: 14) {
                ForEach(Array(steps.enumerated()), id: \.offset) { index, label in
                    HStack(spacing: 12) {
                        Circle()
                            .fill(index < currentStep ? AtuTheme.success : index == currentStep ? AtuTheme.burgundy : AtuTheme.textSecondary.opacity(0.25))
                            .frame(width: index == currentStep ? 12 : 8, height: index == currentStep ? 12 : 8)
                        Text(label)
                            .font(.system(size: 16, weight: .bold))
                            .foregroundStyle(index <= currentStep ? AtuTheme.textPrimary : AtuTheme.textSecondary)
                    }
                }
            }
            .atuCard()
        }
        .task {
            currentStep = 0
            try? await Task.sleep(for: .milliseconds(450))
            currentStep = 1
            try? await Task.sleep(for: .milliseconds(520))
            currentStep = 2
            await app.processScan()
        }
    }
}

struct ConfirmStudentView: View {
    @EnvironmentObject private var app: ATUAppModel
    @State private var name = ""
    @State private var id = ""
    @State private var group = ""

    var body: some View {
        AtuScreen {
            AtuSectionHeader(title: "Tələbə identifikasiyası", subtitle: "Davam etməzdən əvvəl ID və qrup məlumatını təsdiqləyin.")

            VStack(alignment: .leading, spacing: 16) {
                LabeledField(title: "Ad", text: $name, placeholder: "Məsələn: Şaban")
                LabeledField(title: "ID / username", text: $id, placeholder: "Məsələn: 4085604")
                LabeledField(title: "Qrup", text: $group, placeholder: "Məsələn: 1324a3")
            }
            .atuCard()

            AtuInlineNote(text: "OCR yalnız köməkçi mərhələdir. Yekun təsdiq sizin daxil etdiyiniz ID və qrup əsasında aparılır.")

            Button("Təsdiqlə və davam et") {
                Task {
                    guard var profile = app.scannedProfile else { return }
                    profile.name = name.isEmpty ? "Oxunmadı" : name
                    profile.id = id
                    profile.group = group
                    await app.confirm(profile)
                }
            }
            .buttonStyle(AtuPrimaryButtonStyle())

            Button("Yenidən skan et") {
                app.camera.reset()
                app.screen = .scan
            }
            .buttonStyle(AtuSecondaryButtonStyle())
        }
        .onAppear {
            guard let profile = app.scannedProfile else { return }
            name = profile.name == "Oxunmadı" ? "" : profile.name
            id = profile.id == "Oxunmadı" ? "" : profile.id
            group = profile.group == "Oxunmadı" ? "" : profile.group
        }
    }
}

private struct LabeledField: View {
    let title: String
    @Binding var text: String
    let placeholder: String

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title)
                .font(.system(size: 13, weight: .bold))
                .foregroundStyle(AtuTheme.textSecondary)
            TextField(placeholder, text: $text)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .padding(.horizontal, 16)
                .frame(height: 54)
                .background(AtuTheme.surfaceSoft)
                .clipShape(RoundedRectangle(cornerRadius: AtuRadius.input, style: .continuous))
        }
    }
}

private struct ScanThumb: View {
    let title: String
    let image: UIImage?
    let active: Bool

    var body: some View {
        VStack(spacing: 8) {
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .fill(Color.white.opacity(active ? 0.22 : 0.12))
                .frame(width: 74, height: 54)
                .overlay {
                    if let image {
                        Image(uiImage: image).resizable().scaledToFill().clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                    } else {
                        Image(systemName: "arrow.triangle.2.circlepath")
                            .foregroundStyle(.white.opacity(0.85))
                    }
                }
                .overlay(
                    RoundedRectangle(cornerRadius: 16, style: .continuous)
                        .stroke(Color.white.opacity(active ? 0.72 : 0.28), lineWidth: 1)
                )
            Text(title)
                .font(.system(size: 12, weight: .bold))
                .foregroundStyle(.white)
        }
    }
}

struct AtuInlineBadge: View {
    let title: String
    let icon: String

    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: icon)
            Text(title)
        }
        .font(.system(size: 12, weight: .bold))
        .foregroundStyle(AtuTheme.burgundy)
        .padding(.horizontal, 13)
        .padding(.vertical, 9)
        .background(AtuTheme.pink, in: Capsule())
        .overlay(Capsule().stroke(AtuTheme.burgundy.opacity(0.14), lineWidth: 1))
    }
}

private struct AtuScanFrame: View {
    let progressText: String
    let instruction: String
    @State private var linePosition: CGFloat = -0.2

    var body: some View {
        GeometryReader { geo in
            let frameWidth = geo.size.width * 0.84
            let frameHeight = frameWidth * 0.62

            ZStack {
                Color.black.opacity(0.42).ignoresSafeArea()

                VStack {
                    HStack {
                        Text("Kart skanı")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundStyle(.white)
                        Spacer()
                        AtuInlineBadge(title: progressText, icon: "checkmark.shield")
                    }
                    .padding(16)
                    .background(Color.black.opacity(0.36), in: RoundedRectangle(cornerRadius: 26, style: .continuous))
                    .padding(.horizontal, 20)
                    .padding(.top, 24)

                    Spacer()
                }

                RoundedRectangle(cornerRadius: 28, style: .continuous)
                    .stroke(Color.white.opacity(0.7), lineWidth: 2)
                    .frame(width: frameWidth, height: frameHeight)
                    .overlay(alignment: .top) {
                        Capsule()
                            .fill(.white.opacity(0.95))
                            .frame(width: frameWidth - 40, height: 3)
                            .offset(y: frameHeight * linePosition)
                            .blur(radius: 0.4)
                    }

                VStack {
                    Spacer()
                    Text(instruction)
                        .font(.system(size: 18, weight: .bold))
                        .foregroundStyle(.white)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 18)
                        .padding(.vertical, 13)
                        .background(Color.black.opacity(0.42), in: RoundedRectangle(cornerRadius: 22, style: .continuous))
                        .overlay(RoundedRectangle(cornerRadius: 22, style: .continuous).stroke(Color.white.opacity(0.13), lineWidth: 1))
                        .padding(.horizontal, 20)
                        .padding(.bottom, 190)
                }
            }
            .mask {
                ZStack {
                    Rectangle()
                    RoundedRectangle(cornerRadius: 28, style: .continuous)
                        .frame(width: frameWidth, height: frameHeight)
                        .blendMode(.destinationOut)
                }
            }
            .compositingGroup()
        }
        .task {
            withAnimation(.linear(duration: 1.45).repeatForever(autoreverses: false)) {
                linePosition = 1.05
            }
        }
    }
}
