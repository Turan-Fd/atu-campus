import SwiftUI

@MainActor
final class ATUAppModel: ObservableObject {
    @Published var screen: RootScreen = .splash
    @Published var profile: StudentProfile?
    @Published var scannedProfile: StudentProfile?
    @Published var frontImage: UIImage?
    @Published var backImage: UIImage?

    let store = LocalProfileStore()
    let backend = BackendStudentService()
    let newsService = AtuNewsService()
    let aiService = AiChatService()
    let ocrService = OCRService()
    let camera = CameraViewModel()

    init() {
        profile = store.load()
    }

    func finishSplash() {
        profile = store.load()
        screen = profile == nil ? .permission : .home
    }

    func handleCaptured() {
        frontImage = camera.frontImage
        backImage = camera.backImage
        screen = .processing
    }

    func processScan() async {
        let raw = await ocrService.readStudentCard(front: frontImage, back: backImage)
        let verified = await backend.verifyScannedProfile(raw)
        scannedProfile = verified
        screen = .confirm
    }

    func confirm(_ profile: StudentProfile) async {
        let verified = await backend.verifyScannedProfile(profile)
        store.save(verified)
        self.profile = verified
        self.scannedProfile = verified
        screen = .home
    }

    func resetProfile() {
        store.clear()
        profile = nil
        scannedProfile = nil
        frontImage = nil
        backImage = nil
        camera.reset()
        screen = .permission
    }
}

struct RootView: View {
    @EnvironmentObject private var app: ATUAppModel

    var body: some View {
        ZStack {
            switch app.screen {
            case .splash:
                SplashView()
            case .permission:
                PermissionView()
            case .scan:
                ScanCardView()
            case .processing:
                ProcessingView()
            case .confirm:
                ConfirmStudentView()
            case .home:
                HomeContainerView()
            }
        }
        .animation(AtuMotion.normal, value: app.screen)
    }
}
