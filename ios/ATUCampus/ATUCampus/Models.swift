import Foundation

struct StudentProfile: Codable, Equatable {
    var surname: String
    var name: String
    var fatherName: String
    var id: String
    var faculty: String
    var department: String
    var specialty: String
    var group: String

    var fullName: String {
        [name, surname].joined(separator: " ").trimmingCharacters(in: .whitespaces)
    }
}

struct AtuNews: Identifiable, Codable, Equatable {
    let id = UUID()
    var title: String
    var date: String
    var summary: String
    var url: String
    var imageUrl: String
}

struct ChatMessage: Identifiable, Equatable {
    let id = UUID()
    let text: String
    let fromUser: Bool
}

enum RootScreen {
    case splash
    case permission
    case scan
    case processing
    case confirm
    case home
}

enum ScanSide: String {
    case front = "Ön üz"
    case back = "Arxa üz"
}

enum HomeTab: String, CaseIterable {
    case home = "Ana"
    case news = "Xəbərlər"
    case assistant = "AI"
    case pass = "Pass"
    case profile = "Profil"
}
