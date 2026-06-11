import SwiftUI

@main
struct ATUCampusApp: App {
    @StateObject private var appModel = ATUAppModel()

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(appModel)
        }
    }
}
