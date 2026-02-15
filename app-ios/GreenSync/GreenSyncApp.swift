import SwiftUI

@main
struct GreenSyncApp: App {
    @StateObject private var viewModel = AdvisorViewModel()

    var body: some Scene {
        WindowGroup {
            ContentView(viewModel: viewModel)
        }
    }
}
